package com.balsam.upgradetable.mixin.target.client;

import com.balsam.upgradetable.capability.itemAbility.BaseItemAbility;
import com.balsam.upgradetable.capability.itemAbility.IItemAbility;
import com.balsam.upgradetable.capability.pojo.ItemAttributePO;
import com.balsam.upgradetable.config.AttributeEnum;
import com.balsam.upgradetable.mixin.interfaces.IItemStack;
import com.balsam.upgradetable.mod.ModCapability;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.*;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.extensions.IForgeItemStack;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class MixinItemStack extends CapabilityProvider<ItemStack> implements IForgeItemStack {

    protected MixinItemStack(Class<ItemStack> baseClass) {
        super(baseClass);
    }

    protected MixinItemStack(Class<ItemStack> baseClass, boolean isLazy) {
        super(baseClass, isLazy);
    }

    @Shadow
    private CompoundNBT tag;

    @Shadow
    public abstract boolean hasTag();

    @Shadow
    public abstract Item getItem();

    /**
     * 添加总等级提示
     */
    @ModifyArg(method = "getTooltipLines(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/util/ITooltipFlag;)Ljava/util/List;",
            at = @At(value = "INVOKE", ordinal = 0,
                    target = "net/minecraft/util/text/StringTextComponent.append(Lnet/minecraft/util/text/ITextComponent;)Lnet/minecraft/util/text/IFormattableTextComponent;"))
    private ITextComponent stringTextComponentAppend(ITextComponent textComponent) {
        ItemStack thisObj = (ItemStack) (Object) this;
        if (ModCapability.itemAbility == null) return textComponent;
        LazyOptional<IItemAbility> capability = thisObj.getCapability(ModCapability.itemAbility);
        if (!capability.isPresent()) return textComponent;
        BaseItemAbility baseItemAbility = (BaseItemAbility) capability.orElse(null);
        if (baseItemAbility.getTotal().getLevel() <= 0) return textComponent;
        String value = " Lv." + (baseItemAbility.getTotal().canUpgrade() ? baseItemAbility.getTotal().getLevel() + "" : "max");
        return ((TextComponent) textComponent).append(value);
    }

    /**
     * 修复原版bug，改为LinkedListMultimap，可以按照塞入顺序展示tooltip
     */
    @ModifyVariable(method = "getAttributeModifiers(Lnet/minecraft/inventory/EquipmentSlotType;)Lcom/google/common/collect/Multimap;",
            at = @At(value = "INVOKE", remap = false, ordinal = 0, shift = At.Shift.BY, by = 2,
                    target = "com/google/common/collect/HashMultimap.create()Lcom/google/common/collect/HashMultimap;"),
            ordinal = 0)
    private Multimap<Attribute, AttributeModifier> getAttributeModifiersSetMap(Multimap<Attribute, AttributeModifier> map) {
        return LinkedListMultimap.create();
    }

    /**
     * 注入属性栏提示
     */
    @Inject(method = "getTooltipLines(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/util/ITooltipFlag;)Ljava/util/List;",
            at = @At(value = "INVOKE", shift = At.Shift.BEFORE, ordinal = 1,
                    target = "Lnet/minecraft/item/ItemStack;hasTag()Z"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void getTooltipLines(PlayerEntity playerEntity, ITooltipFlag p_82840_2_, CallbackInfoReturnable<List<ITextComponent>> cir, List<ITextComponent> list) {
        ItemStack thisObj = (ItemStack) (Object) this;
        if (ModCapability.itemAbility == null) return;
        thisObj.getCapability(ModCapability.itemAbility).ifPresent(o -> {
            BaseItemAbility baseItemAbility = (BaseItemAbility) o;
            for (ItemAttributePO attr : baseItemAbility.getDisplayAttributes()) {
                AttributeEnum attributeEnum = attr.getAttributeEnum();
                if (attributeEnum == AttributeEnum.Total || attributeEnum.getOperation() == AttributeEnum.Operation.NULL)
                    continue;
                double value = attributeEnum == AttributeEnum.MAX_DURATION ? ((IItemStack) this).getAdditionalMaxDamage() : attr.getValue();
                //处理百分比
                if (attributeEnum.getOperation() == AttributeEnum.Operation.MULTIPLY) {
                    value = value * 100.0D;
                }
                IFormattableTextComponent textComponent = null;
                //添加条目
                if (value > 0.0D) {
                    textComponent = (new TranslationTextComponent("attribute.modifier.plus." + attributeEnum.getOperation().ordinal(),
                            ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(value), new TranslationTextComponent(attributeEnum.getName()))).withStyle(TextFormatting.BLUE);
                } else if (value < 0.0D) {
                    value = value * -1.0D;
                    textComponent = (new TranslationTextComponent("attribute.modifier.take." + attributeEnum.getOperation().ordinal(),
                            ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(value), new TranslationTextComponent(attributeEnum.getName()))).withStyle(TextFormatting.RED);
                }
                if (textComponent != null) {
                    String lvStr = String.format(" (Lv.%s)", attr.canUpgrade() ? attr.getLevel() + "/" + attr.getMaxLevel() : "max");
                    textComponent.append(new StringTextComponent(lvStr));
                    list.add(textComponent);
                }
            }
        });
    }
}
