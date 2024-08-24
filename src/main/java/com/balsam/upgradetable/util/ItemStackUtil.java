package com.balsam.upgradetable.util;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

public class ItemStackUtil {

    /**
     * 新增或更新IItemAbility的属性
     *
     * @param itemStack
     * @param attribute 属性
     * @param modifier  属性修改对象
     * @param slotType  槽位
     */
    public static void addOrUpdateAttributeModifier(ItemStack itemStack, Attribute attribute, AttributeModifier modifier, EquipmentSlotType slotType) {
        ListNBT attributeModifiers = (ListNBT) itemStack.getOrCreateTag().get("AttributeModifiers");
        //删除已存在
        removeAttributeModifier(attributeModifiers, attribute, slotType);
        //添加新属性
        itemStack.addAttributeModifier(attribute, modifier, slotType);
    }

    private static void removeAttributeModifier(ListNBT attributeModifiers, Attribute attribute, EquipmentSlotType slotType) {
        if (attributeModifiers != null) {
            for (int i = attributeModifiers.size() - 1; i >= 0; i--) {
                CompoundNBT attriObj = (CompoundNBT) attributeModifiers.get(i);
                String attributeName = ForgeRegistries.ATTRIBUTES.getKey(attribute).toString();
                if (StringUtils.equals(attriObj.getString("AttributeName"), attributeName) &&
                        StringUtils.equals(attriObj.getString("Slot"), slotType.getName())) {
                    attributeModifiers.remove(i);
                }
            }
        }
    }

    public static void removeAttributeModifier(ItemStack itemStack, Attribute attribute, EquipmentSlotType slotType) {
        ListNBT attributeModifiers = (ListNBT) itemStack.getOrCreateTag().get("AttributeModifiers");
        removeAttributeModifier(attributeModifiers, attribute, slotType);
    }
}
