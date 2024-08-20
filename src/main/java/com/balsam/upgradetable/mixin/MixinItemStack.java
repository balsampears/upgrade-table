package com.balsam.upgradetable.mixin;

import com.balsam.upgradetable.registry.AttributeRegistry;
import com.google.common.collect.*;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.extensions.IForgeItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Shadow
    public abstract ITextComponent getHoverName();
    @Shadow
    public abstract boolean hasCustomHoverName();
    @Shadow
    public abstract int getHideFlags();
    @Shadow
    public abstract ListNBT getEnchantmentTags();
    @Shadow
    public abstract Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType p_111283_1_);
    @Shadow
    public abstract boolean isDamaged();
    @Shadow
    public abstract Rarity getRarity();
    @Shadow
    public abstract int getDamageValue();
    @Shadow
    public abstract int getMaxDamage();

    @Inject(at=@At("RETURN"), method = "getMaxDamage()I", cancellable = true)
    private void getMaxDamage(CallbackInfoReturnable<Integer> callback){
        ItemStack thisObj = (ItemStack)(Object)this;
        Collection<AttributeModifier> attributeModifiers = thisObj.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(AttributeRegistry.MaxDuration.get());
        if (attributeModifiers!=null && attributeModifiers.size()>0){
            AttributeModifier attributeModifier = attributeModifiers.iterator().next();
            Integer returnValue = callback.getReturnValue();
            callback.setReturnValue(returnValue + (int)attributeModifier.getAmount());
//            Logger.info(String.format("总计耐久：%d,额外耐久：%d",callback.getReturnValue(),(int)attributeModifier.getAmount()));
        }
    }

    /**
     * 原版方法修复：存在Attributes时，没有按照塞入顺序展示tooltip
     */
    @Inject(at = @At("HEAD"), method = "getAttributeModifiers(Lnet/minecraft/inventory/EquipmentSlotType;)Lcom/google/common/collect/Multimap;", cancellable = true)
    private void getAttributeModifiers(EquipmentSlotType p_111283_1_, CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> callback) {
        ItemStack thisObj = (ItemStack)(Object)this;
        Multimap<Attribute, AttributeModifier> multimap;
        if (this.hasTag() && this.tag.contains("AttributeModifiers", 9)) {
            multimap = LinkedListMultimap.create();
            ListNBT listnbt = this.tag.getList("AttributeModifiers", 10);

            for(int i = 0; i < listnbt.size(); ++i) {
                CompoundNBT compoundnbt = listnbt.getCompound(i);
                if (!compoundnbt.contains("Slot", 8) || compoundnbt.getString("Slot").equals(p_111283_1_.getName())) {
                    Optional<Attribute> optional = Registry.ATTRIBUTE.getOptional(ResourceLocation.tryParse(compoundnbt.getString("AttributeName")));
                    if (optional.isPresent()) {
                        AttributeModifier attributemodifier = AttributeModifier.load(compoundnbt);
                        if (attributemodifier != null && attributemodifier.getId().getLeastSignificantBits() != 0L && attributemodifier.getId().getMostSignificantBits() != 0L) {
                            multimap.put(optional.get(), attributemodifier);
                        }
                    }
                }
            }
        } else {
            multimap = this.getItem().getAttributeModifiers(p_111283_1_, thisObj);
        }

        multimap = net.minecraftforge.common.ForgeHooks.getAttributeModifiers(thisObj, p_111283_1_, multimap);
        callback.setReturnValue(multimap);
        callback.cancel();
    }

    /**
     * 原版方法修复：存在Attributes(damage、damageSpeed)时，无法正常附魔等
     */
    @Inject(at = @At("HEAD"), method = "getTooltipLines(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/client/util/ITooltipFlag;)Ljava/util/List;", cancellable = true)
    private void getTooltipLines(PlayerEntity p_82840_1_, ITooltipFlag p_82840_2_, CallbackInfoReturnable<List<ITextComponent>> callback){
        ItemStack thisObj = (ItemStack)(Object)this;
        List<ITextComponent> list = Lists.newArrayList();
        IFormattableTextComponent iformattabletextcomponent = (new StringTextComponent("")).append(this.getHoverName()).withStyle(this.getRarity().color);
        if (this.hasCustomHoverName()) {
            iformattabletextcomponent.withStyle(TextFormatting.ITALIC);
        }

        list.add(iformattabletextcomponent);
        if (!p_82840_2_.isAdvanced() && !this.hasCustomHoverName() && this.getItem() == Items.FILLED_MAP) {
            list.add((new StringTextComponent("#" + FilledMapItem.getMapId(thisObj))).withStyle(TextFormatting.GRAY));
        }

        int i = this.getHideFlags();
        if (shouldShowInTooltip2(i, ItemStack.TooltipDisplayFlags.ADDITIONAL)) {
            this.getItem().appendHoverText(thisObj, p_82840_1_ == null ? null : p_82840_1_.level, list, p_82840_2_);
        }

        if (this.hasTag()) {
            if (shouldShowInTooltip2(i, ItemStack.TooltipDisplayFlags.ENCHANTMENTS)) {
                appendEnchantmentNames2(list, this.getEnchantmentTags());
            }

            if (this.tag.contains("display", 10)) {
                CompoundNBT compoundnbt = this.tag.getCompound("display");
                if (shouldShowInTooltip2(i, ItemStack.TooltipDisplayFlags.DYE) && compoundnbt.contains("color", 99)) {
                    if (p_82840_2_.isAdvanced()) {
                        list.add((new TranslationTextComponent("item.color", String.format("#%06X", compoundnbt.getInt("color")))).withStyle(TextFormatting.GRAY));
                    } else {
                        list.add((new TranslationTextComponent("item.dyed")).withStyle(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC}));
                    }
                }

                if (compoundnbt.getTagType("Lore") == 9) {
                    ListNBT listnbt = compoundnbt.getList("Lore", 8);

                    for(int j = 0; j < listnbt.size(); ++j) {
                        String s = listnbt.getString(j);

                        try {
                            IFormattableTextComponent iformattabletextcomponent1 = ITextComponent.Serializer.fromJson(s);
                            if (iformattabletextcomponent1 != null) {
                                list.add(TextComponentUtils.mergeStyles(iformattabletextcomponent1, ItemStack.LORE_STYLE));
                            }
                        } catch (JsonParseException jsonparseexception) {
                            compoundnbt.remove("Lore");
                        }
                    }
                }
            }
        }

        if (shouldShowInTooltip2(i, ItemStack.TooltipDisplayFlags.MODIFIERS)) {
            for(EquipmentSlotType equipmentslottype : EquipmentSlotType.values()) {
                Multimap<Attribute, AttributeModifier> multimap = this.getAttributeModifiers(equipmentslottype);
                if (!multimap.isEmpty()) {
                    list.add(StringTextComponent.EMPTY);
                    list.add((new TranslationTextComponent("item.modifiers." + equipmentslottype.getName())).withStyle(TextFormatting.GRAY));

                    for(Map.Entry<Attribute, AttributeModifier> entry : multimap.entries()) {
                        AttributeModifier attributemodifier = entry.getValue();
                        double d0 = attributemodifier.getAmount();
                        boolean flag = false;
                        if (p_82840_1_ != null) {
                            if (Item.BASE_ATTACK_DAMAGE_UUID.toString().equals(attributemodifier.getId().toString())) { //update
                                d0 = d0 + p_82840_1_.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
                                d0 = d0 + (double) EnchantmentHelper.getDamageBonus(thisObj, CreatureAttribute.UNDEFINED);
                                flag = true;
                            } else if (Item.BASE_ATTACK_SPEED_UUID.toString().equals(attributemodifier.getId().toString())) { //update
                                d0 += p_82840_1_.getAttributeBaseValue(Attributes.ATTACK_SPEED);
                                flag = true;
                            }
                        }

                        double d1;
                        if (attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                            if (entry.getKey().equals(Attributes.KNOCKBACK_RESISTANCE)) {
                                d1 = d0 * 10.0D;
                            } else {
                                d1 = d0;
                            }
                        } else {
                            d1 = d0 * 100.0D;
                        }

                        if (flag) {
                            list.add((new StringTextComponent(" ")).append(new TranslationTextComponent("attribute.modifier.equals." + attributemodifier.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new TranslationTextComponent(entry.getKey().getDescriptionId()))).withStyle(TextFormatting.DARK_GREEN));
                        } else if (d0 > 0.0D) {
                            list.add((new TranslationTextComponent("attribute.modifier.plus." + attributemodifier.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new TranslationTextComponent(entry.getKey().getDescriptionId()))).withStyle(TextFormatting.BLUE));
                        } else if (d0 < 0.0D) {
                            d1 = d1 * -1.0D;
                            list.add((new TranslationTextComponent("attribute.modifier.take." + attributemodifier.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), new TranslationTextComponent(entry.getKey().getDescriptionId()))).withStyle(TextFormatting.RED));
                        }
                    }
                }
            }
        }

        if (this.hasTag()) {
            if (shouldShowInTooltip2(i, ItemStack.TooltipDisplayFlags.UNBREAKABLE) && this.tag.getBoolean("Unbreakable")) {
                list.add((new TranslationTextComponent("item.unbreakable")).withStyle(TextFormatting.BLUE));
            }

            if (shouldShowInTooltip2(i, ItemStack.TooltipDisplayFlags.CAN_DESTROY) && this.tag.contains("CanDestroy", 9)) {
                ListNBT listnbt1 = this.tag.getList("CanDestroy", 8);
                if (!listnbt1.isEmpty()) {
                    list.add(StringTextComponent.EMPTY);
                    list.add((new TranslationTextComponent("item.canBreak")).withStyle(TextFormatting.GRAY));

                    for(int k = 0; k < listnbt1.size(); ++k) {
                        list.addAll(expandBlockState2(listnbt1.getString(k)));
                    }
                }
            }

            if (shouldShowInTooltip2(i, ItemStack.TooltipDisplayFlags.CAN_PLACE) && this.tag.contains("CanPlaceOn", 9)) {
                ListNBT listnbt2 = this.tag.getList("CanPlaceOn", 8);
                if (!listnbt2.isEmpty()) {
                    list.add(StringTextComponent.EMPTY);
                    list.add((new TranslationTextComponent("item.canPlace")).withStyle(TextFormatting.GRAY));

                    for(int l = 0; l < listnbt2.size(); ++l) {
                        list.addAll(expandBlockState2(listnbt2.getString(l)));
                    }
                }
            }
        }

        if (p_82840_2_.isAdvanced()) {
            if (this.isDamaged()) {
                list.add(new TranslationTextComponent("item.durability", this.getMaxDamage() - this.getDamageValue(), this.getMaxDamage()));
            }

            list.add((new StringTextComponent(Registry.ITEM.getKey(this.getItem()).toString())).withStyle(TextFormatting.DARK_GRAY));
            if (this.hasTag()) {
                list.add((new TranslationTextComponent("item.nbt_tags", this.tag.getAllKeys().size())).withStyle(TextFormatting.DARK_GRAY));
            }
        }

        net.minecraftforge.event.ForgeEventFactory.onItemTooltip(thisObj, p_82840_1_, list, p_82840_2_);
        callback.setReturnValue(list);
        callback.cancel();
    }

    @OnlyIn(Dist.CLIENT)
    private static boolean shouldShowInTooltip2(int p_242394_0_, ItemStack.TooltipDisplayFlags p_242394_1_) {
        return (p_242394_0_ & p_242394_1_.getMask()) == 0;
    }
    @OnlyIn(Dist.CLIENT)
    private static Collection<ITextComponent> expandBlockState2(String p_206845_0_) {
        try {
            BlockStateParser blockstateparser = (new BlockStateParser(new StringReader(p_206845_0_), true)).parse(true);
            BlockState blockstate = blockstateparser.getState();
            ResourceLocation resourcelocation = blockstateparser.getTag();
            boolean flag = blockstate != null;
            boolean flag1 = resourcelocation != null;
            if (flag || flag1) {
                if (flag) {
                    return Lists.newArrayList(blockstate.getBlock().getName().withStyle(TextFormatting.DARK_GRAY));
                }

                ITag<Block> itag = BlockTags.getAllTags().getTag(resourcelocation);
                if (itag != null) {
                    Collection<Block> collection = itag.getValues();
                    if (!collection.isEmpty()) {
                        return collection.stream().map(Block::getName).map((p_222119_0_) -> {
                            return p_222119_0_.withStyle(TextFormatting.DARK_GRAY);
                        }).collect(Collectors.toList());
                    }
                }
            }
        } catch (CommandSyntaxException commandsyntaxexception) {
        }

        return Lists.newArrayList((new StringTextComponent("missingno")).withStyle(TextFormatting.DARK_GRAY));
    }
    @OnlyIn(Dist.CLIENT)
    private static void appendEnchantmentNames2(List<ITextComponent> p_222120_0_, ListNBT p_222120_1_) {
        for(int i = 0; i < p_222120_1_.size(); ++i) {
            CompoundNBT compoundnbt = p_222120_1_.getCompound(i);
            Registry.ENCHANTMENT.getOptional(ResourceLocation.tryParse(compoundnbt.getString("id"))).ifPresent((p_222123_2_) -> {
                p_222120_0_.add(p_222123_2_.getFullname(compoundnbt.getInt("lvl")));
            });
        }

    }


}
