package com.balsam.upgradetable.block;

import com.balsam.upgradetable.capability.itemAbility.BaseItemAbility;
import com.balsam.upgradetable.capability.itemAbility.IItemAbility;
import com.balsam.upgradetable.capability.pojo.ItemAttributePO;
import com.balsam.upgradetable.config.AttributeEnum;
import com.balsam.upgradetable.config.Constants;
import com.balsam.upgradetable.mixin.interfaces.IItemStack;
import com.balsam.upgradetable.mod.ModCapability;
import com.balsam.upgradetable.network.Networking;
import com.balsam.upgradetable.network.pack.UpgradeButtonPack;
import com.balsam.upgradetable.registry.TileEntityTypeRegistry;
import com.balsam.upgradetable.util.ItemStackUtil;
import com.balsam.upgradetable.util.Logger;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

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
            //判断能否升级
            ItemAttributePO targetAttribute = checkUpgrade(upgradeItemIndex, itemStack, baseItemAbility);
            if (targetAttribute == null) return;

            //消耗材料
            inventory.setItem(1, ItemStack.EMPTY);

            //总等级升级、能力值升级
            baseItemAbility.getTotal().upgrade();
            targetAttribute.upgrade();
            Logger.info(String.format("当前总等级为：%d/%d", baseItemAbility.getTotal().getLevel(), baseItemAbility.getTotal().getMaxLevel()));
            //每次升级，都会刷新全部属性
//            //刷新原版的属性
//            for (EquipmentSlotType slotType : EquipmentSlotType.values()) {
//                Multimap<Attribute, AttributeModifier> attributeModifierMap = itemStack.getItem().getAttributeModifiers(slotType, itemStack);
//                for (Map.Entry<Attribute, AttributeModifier> entry : attributeModifierMap.entries()) {
//                    ItemStackUtil.addOrUpdateAttributeModifier(itemStack, entry.getKey(), entry.getValue(), slotType);
//                }
//            }
//            //刷新能力值属性
//            for (ItemAttributePO attribute : baseItemAbility.getDisplayAttributes()) {
//                switch (attribute.getAttributeEnum()) {
//                    case ATTACK_DAMAGE:
//                        itemStack.getAttributeModifiers()
//                        ItemStackUtil.addOrUpdateAttributeModifier(itemStack, AttributeRegistry.AttackDamage.get(), new AttributeModifier(
//                                        AttributeEnum.ATTACK_DAMAGE.getUuid(), "Weapon Attribute", attribute.getValue(), AttributeModifier.Operation.ADDITION),
//                                EquipmentSlotType.MAINHAND);
//                        break;
//                    case ATTACK_SPEED:
//                        ItemStackUtil.addOrUpdateAttributeModifier(itemStack, AttributeRegistry.AttackSpeed.get(), new AttributeModifier(
//                                        AttributeEnum.ATTACK_SPEED.getUuid(), "Weapon Attribute", attribute.getValue(), AttributeModifier.Operation.ADDITION),
//                                EquipmentSlotType.MAINHAND);
//                        break;
//                }
//            }
            //特别处理耐久度
            int maxDuration = settingMaxDuration(baseItemAbility);
            ((IItemStack)(Object)itemStack).setAdditionalMaxDamage(maxDuration);
            //通知客户端更新
            Networking.INSTANCE.send(PacketDistributor.ALL.noArg(), new UpgradeButtonPack(o.serializeNBT(), upgradeItemIndex));
        });
    }

    private ItemAttributePO checkUpgrade(int upgradeItemIndex, ItemStack itemStack, BaseItemAbility baseItemAbility) {
        //选定能力校验
        if (upgradeItemIndex +1 >= baseItemAbility.getDisplayAttributes().size()) return null;
        //总等级校验
        if (!baseItemAbility.getTotal().canUpgrade()) return null;
        //能力等级校验
        ItemAttributePO targetAttribute = baseItemAbility.getDisplayAttributes().get(upgradeItemIndex + 1);
        if (targetAttribute == null || !targetAttribute.canUpgrade()) return null;
        //消耗耐久校验
        if (targetAttribute.getAttributeEnum()!=AttributeEnum.MAX_DURATION &&
                itemStack.getMaxDamage() - targetAttribute.getPerLevelReduceDuration() <=0) {
            Logger.info("升级失败，物品已接近损坏"); //可以改为发送信息栏通知
            return null;
        }
        //消耗材料校验
        if (!Minecraft.getInstance().player.isCreative()
                && inventory.getItem(1).getItem() != inventory.getItem(0).getItem()) return null;
        return targetAttribute;
    }

    @OnlyIn(Dist.CLIENT)
    public void syncData(CompoundNBT compoundNBT, int upgradeItemIndex) {
        ItemStack itemStack = inventory.getItem(0);
        if (itemStack.isEmpty()) return;
        itemStack.getCapability(ModCapability.itemAbility).ifPresent(o -> {
            o.deserializeNBT(compoundNBT);
            int maxDuration = settingMaxDuration((BaseItemAbility)o );
            ((IItemStack)(Object)itemStack).setAdditionalMaxDamage(maxDuration);
//            Logger.info(String.format("同步数据：%s", new Gson().toJson(compoundNBT)));
        });
    }

    /**
     * 设置额外最大耐久度
     * @param baseItemAbility
     */
    public static int settingMaxDuration(BaseItemAbility baseItemAbility) {
        int maxDuration = 0;
        for (ItemAttributePO attribute : baseItemAbility.getDisplayAttributes()) {
            maxDuration -= attribute.getReduceDuration();
            if (attribute.getAttributeEnum() == AttributeEnum.MAX_DURATION)
                maxDuration += (int)attribute.getValue();
        }
        return maxDuration;
    }

}
