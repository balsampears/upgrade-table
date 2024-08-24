package com.balsam.upgradetable.block;

import com.balsam.upgradetable.capability.itemAbility.BaseItemAbility;
import com.balsam.upgradetable.capability.itemAbility.IItemAbility;
import com.balsam.upgradetable.capability.itemAbility.TieredItemAbility;
import com.balsam.upgradetable.capability.pojo.ItemAttributePO;
import com.balsam.upgradetable.config.AttributeEnum;
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

    public void upgrade(int upgradeItemIndex) {
        ItemStack itemStack = inventory.getItem(0);
        if (itemStack.isEmpty()) return;

        LazyOptional<IItemAbility> levelOption = itemStack.getCapability(ModCapability.itemAbility);
        levelOption.ifPresent(o -> {
            BaseItemAbility baseItemAbility = (BaseItemAbility) o;
            //每次升级，都会刷新全部属性
            //刷新原版的属性
            for (EquipmentSlotType slotType : EquipmentSlotType.values()) {
                Multimap<Attribute, AttributeModifier> attributeModifierMap = itemStack.getItem().getAttributeModifiers(slotType, itemStack);
                for (Map.Entry<Attribute, AttributeModifier> entry : attributeModifierMap.entries()) {
                    ItemStackUtil.addOrUpdateAttributeModifier(itemStack, entry.getKey(), entry.getValue(), slotType);
                }
            }
            if (!baseItemAbility.getTotal().canUpgrade()) return;
            if (upgradeItemIndex+1 >= baseItemAbility.getDisplayAttributes().size()) return;
            ItemAttributePO targetAttribute = baseItemAbility.getDisplayAttributes().get(upgradeItemIndex + 1);
            if (targetAttribute == null || !targetAttribute.canUpgrade()) return;

            //总等级升级、能力值升级
            baseItemAbility.getTotal().upgrade();
            targetAttribute.upgrade();
            Logger.info(String.format("当前总等级为：%d/%d", baseItemAbility.getTotal().getLevel(), baseItemAbility.getTotal().getMaxLevel()));
            //刷新能力值属性
            for (ItemAttributePO attribute : baseItemAbility.getDisplayAttributes()) {
                switch (attribute.getAttributeEnum()) {
                    case ATTACK_DAMAGE:
                        ItemStackUtil.addOrUpdateAttributeModifier(itemStack, AttributeRegistry.AttackDamage.get(), new AttributeModifier(
                                        AttributeEnum.ATTACK_DAMAGE.getUuid(), "Weapon Attribute", attribute.getValue(), AttributeModifier.Operation.ADDITION),
                                EquipmentSlotType.MAINHAND);
                        break;
                    case ATTACK_SPEED:
                        ItemStackUtil.addOrUpdateAttributeModifier(itemStack, AttributeRegistry.AttackSpeed.get(), new AttributeModifier(
                                        AttributeEnum.ATTACK_SPEED.getUuid(), "Weapon Attribute", attribute.getValue(), AttributeModifier.Operation.ADDITION),
                                EquipmentSlotType.MAINHAND);
                        break;
                    case MAX_DURATION:
                        ItemStackUtil.addOrUpdateAttributeModifier(itemStack, AttributeRegistry.MaxDuration.get(), new AttributeModifier(
                                        AttributeEnum.MAX_DURATION.getUuid(), "Normal Attribute", attribute.getValue(), AttributeModifier.Operation.ADDITION),
                                EquipmentSlotType.MAINHAND);
                        break;
                }
            }
            //通知客户端更新
            Networking.INSTANCE.send(PacketDistributor.ALL.noArg(), new UpgradeButtonPack(o.serializeNBT(), upgradeItemIndex));
        });
    }

    @OnlyIn(Dist.CLIENT)
    public void syncData(CompoundNBT compoundNBT, int upgradeItemIndex) {
        ItemStack itemStack = inventory.getItem(1);
        if (itemStack.isEmpty()) return;
        itemStack.getCapability(ModCapability.itemAbility).ifPresent(o -> {
            o.deserializeNBT(compoundNBT);
//            Logger.info(String.format("同步数据：%s", new Gson().toJson(compoundNBT)));
        });
    }


}
