package com.balsam.upgradetable.block;

import com.balsam.upgradetable.capability.itemAbility.BaseItemAbility;
import com.balsam.upgradetable.capability.itemAbility.IItemAbility;
import com.balsam.upgradetable.capability.itemAbility.TieredItemAbility;
import com.balsam.upgradetable.config.Constants;
import com.balsam.upgradetable.mod.ModCapability;
import com.balsam.upgradetable.network.Networking;
import com.balsam.upgradetable.network.pack.UpgradeButtonPack;
import com.balsam.upgradetable.registry.AttributeRegistry;
import com.balsam.upgradetable.registry.TileEntityTypeRegistry;
import com.balsam.upgradetable.util.ItemStackUtil;
import com.balsam.upgradetable.util.Logger;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Map;

public class UpgradeTableTileEntity extends TileEntity implements INamedContainerProvider {

    private Inventory inventory = new Inventory(2);

    public UpgradeTableTileEntity() {
        super(TileEntityTypeRegistry.UpgradeTableTileEntity.get());
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(String.format("gui.%s.upgradetable", Constants.MOD_ID));
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new UpgradeTableContainer(id, this.level, this.worldPosition, playerInventory);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void upgrade() {
        ItemStack itemStack = inventory.getItem(1);
        if (itemStack.isEmpty()) return;

        LazyOptional<IItemAbility> levelOption = itemStack.getCapability(ModCapability.itemAbility);
        levelOption.ifPresent(o -> {
            BaseItemAbility baseItemAbility = (BaseItemAbility) o;
            //补充原版的属性
            for (EquipmentSlotType slotType : EquipmentSlotType.values()) {
                Multimap<Attribute, AttributeModifier> attributeModifierMap = itemStack.getItem().getAttributeModifiers(slotType, itemStack);
                for (Map.Entry<Attribute, AttributeModifier> entry : attributeModifierMap.entries()) {
                    ItemStackUtil.addOrUpdateAttributeModifier(itemStack, entry.getKey(), entry.getValue(), slotType);
                }
            }
            //总等级升级
            baseItemAbility.getTotal().upgrade();
            Logger.info(String.format("当前总等级为：%d/%d", baseItemAbility.getTotal().getLevel(), baseItemAbility.getTotal().getMaxLevel()));

            if (o instanceof TieredItemAbility) {
                //攻击
                TieredItemAbility tieredItemAbility = (TieredItemAbility) o;
                tieredItemAbility.getAttack().upgrade();
                ItemStackUtil.addOrUpdateAttributeModifier(itemStack, AttributeRegistry.AttackDamage.get(),new AttributeModifier(
                        Constants.Uuid.ATTACK_DAMAGE, "Weapon Attribute", tieredItemAbility.getAttack().getValue(), AttributeModifier.Operation.ADDITION),
                        EquipmentSlotType.MAINHAND);
                //攻速
                tieredItemAbility.getAttackSpeed().upgrade();
                ItemStackUtil.addOrUpdateAttributeModifier(itemStack, AttributeRegistry.AttackSpeed.get(),new AttributeModifier(
                                Constants.Uuid.ATTACK_SPEED, "Weapon Attribute", tieredItemAbility.getAttackSpeed().getValue(), AttributeModifier.Operation.ADDITION),
                        EquipmentSlotType.MAINHAND);
            }
            //耐久
            baseItemAbility.getDuration().upgrade();
            ItemStackUtil.addOrUpdateAttributeModifier(itemStack, AttributeRegistry.MaxDuration.get(),new AttributeModifier(
                            Constants.Uuid.MAX_DURATION, "Normal Attribute", baseItemAbility.getDuration().getValue(), AttributeModifier.Operation.ADDITION),
                    EquipmentSlotType.MAINHAND);

            //通知客户端更新
            Networking.INSTANCE.send(PacketDistributor.ALL.noArg(), new UpgradeButtonPack(o.serializeNBT()));
        });
    }

    @OnlyIn(Dist.CLIENT)
    public void syncData(CompoundNBT compoundNBT) {
        ItemStack itemStack = inventory.getItem(1);
        if (itemStack.isEmpty()) return;
        itemStack.getCapability(ModCapability.itemAbility).ifPresent(o -> {
            o.deserializeNBT(compoundNBT);
//            Logger.info(String.format("同步数据：%s", new Gson().toJson(compoundNBT)));
        });
    }


}
