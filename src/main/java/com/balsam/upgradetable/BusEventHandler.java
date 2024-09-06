package com.balsam.upgradetable;

import com.balsam.upgradetable.capability.ItemAbilityProvider;
import com.balsam.upgradetable.capability.itemAbility.*;
import com.balsam.upgradetable.capability.pojo.ItemAttributePO;
import com.balsam.upgradetable.config.AttributeEnum;
import com.balsam.upgradetable.config.Constants;
import com.balsam.upgradetable.mixin.interfaces.IExtraDamage;
import com.balsam.upgradetable.mod.ModCapability;
import com.balsam.upgradetable.mod.ModConfig;
import com.balsam.upgradetable.util.ItemStackUtil;
import com.balsam.upgradetable.util.Logger;
import com.google.common.collect.Multimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;

@Mod.EventBusSubscriber
public class BusEventHandler {
    @SubscribeEvent
    public static void onAttachCapabilitiesEvent(AttachCapabilitiesEvent<ItemStack> event) throws ClassNotFoundException {
        Item item = event.getObject().getItem();
        IItemAbility itemAbility = null;

        //读取配置文件，倒序匹配class
        Map<Class<?>, ModConfig.ConfigItem> configMap = ModConfig.getConfigMap();
        ListIterator<Map.Entry<Class<?>, ModConfig.ConfigItem>> listIterator = new ArrayList<>(configMap.entrySet()).listIterator(configMap.size());
        while (listIterator.hasPrevious()){
            Map.Entry<Class<?>, ModConfig.ConfigItem> entry = listIterator.previous();
            if (entry.getKey()!=null && entry.getKey().isAssignableFrom(item.getClass())){
                itemAbility = new ConfigItemAbility(entry.getValue());
                break;
            }
        }

        if (itemAbility!=null){
            event.addCapability(new ResourceLocation(String.format("cap.%s.upgrade", Constants.MOD_ID)),
                    new ItemAbilityProvider(itemAbility));
        }
    }

    /**
     * 实现功能：额外弓箭伤害、额外投射伤害、额外攻击伤害
     */
    @SubscribeEvent
    public static void onLivingHurtEvent(LivingHurtEvent event){
        if (event.getSource() instanceof IndirectEntityDamageSource){
            IndirectEntityDamageSource eventSource = (IndirectEntityDamageSource) event.getSource();
            //额外弓箭伤害
            if (eventSource.getDirectEntity() instanceof AbstractArrowEntity){
                AbstractArrowEntity arrowEntity = (AbstractArrowEntity) eventSource.getDirectEntity();
                Float value = ((IExtraDamage)arrowEntity).getExtraDamage();
                if (value!=null) {
                    //如果没满弦，则伤害降低到1/3
                    value = value / (arrowEntity.isCritArrow() ? 1 : 3);
                    event.setAmount(event.getAmount() + value);
//                    Logger.info("额外增加伤害："+value);
                }
            }
            //额外投射伤害
            else if (eventSource.getDirectEntity() instanceof ThrowableEntity){
                ThrowableEntity throwableEntity = (ThrowableEntity) eventSource.getDirectEntity();
                Float value = ((IExtraDamage)throwableEntity).getExtraDamage();
                if (value!=null){
                    event.setAmount(event.getAmount() + value);
//                    Logger.info("额外增加伤害："+value);
                }
            }
//        } else if (event.getSource() instanceof DamageSource){
//            //额外攻击伤害
//            DamageSource source = event.getSource();
//            if (source.getDirectEntity() instanceof LivingEntity){
//                ItemStack useItem = ItemStackUtil.getUseItem((LivingEntity) source.getDirectEntity());
//                if (useItem == ItemStack.EMPTY) return;
//                useItem.getCapability(ModCapability.itemAbility).ifPresent(o->{
//                    ((BaseItemAbility)o).findAttribute(AttributeEnum.ATTACK_DAMAGE).ifPresent(attr->{
//                        event.setAmount(event.getAmount() + attr.getValue());
//                    });
//                });
//            }
        }
    }

    /**
     * 攻击伤害、攻击速度叠加
     * 显示时每tick调用一次，切换装备时调用一次
     */
    @SubscribeEvent
    public static void onItemAttributeModifierEvent(ItemAttributeModifierEvent event){
        if (event.getSlotType() != EquipmentSlotType.MAINHAND) return;
        ItemStack itemStack = event.getItemStack();
        itemStack.getCapability(ModCapability.itemAbility).ifPresent(o->{
            BaseItemAbility baseItemAbility = (BaseItemAbility) o;
            //从item中获取基础属性
            Multimap<Attribute, AttributeModifier> itemModifierMap = itemStack.getItem().getAttributeModifiers(EquipmentSlotType.MAINHAND, itemStack);
            for (int i = 0;i<baseItemAbility.getDisplayAttributes().size();i++) {
                ItemAttributePO attr = baseItemAbility.getDisplayAttributes().get(i);
                //攻击伤害
                if (attr.getAttributeEnum() == AttributeEnum.ATTACK_DAMAGE){
                    Optional<AttributeModifier> optional = itemModifierMap.get(Attributes.ATTACK_DAMAGE).stream().findFirst();
                    if (optional.isPresent()) {
                        AttributeModifier itemModifier = optional.get();
                        //攻击伤害=基础攻击伤害+额外攻击伤害
                        AttributeModifier attributeModifier = new AttributeModifier(Item.BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", itemModifier.getAmount() + attr.getValue(), AttributeModifier.Operation.ADDITION);
                        //需要移除原有属性，否则无法正常生效
                        event.removeAttribute(Attributes.ATTACK_DAMAGE);
                        event.addModifier(Attributes.ATTACK_DAMAGE, attributeModifier);
//                        Logger.info(String.format("攻击伤害增强：%f -> %f", itemModifier.getAmount(), attributeModifier.getAmount()));
                    }
                }
                //攻击速度
                else if (attr.getAttributeEnum() == AttributeEnum.ATTACK_SPEED){
                    Optional<AttributeModifier> optional = itemModifierMap.get(Attributes.ATTACK_SPEED).stream().findFirst();
                    if (optional.isPresent()) {
                        AttributeModifier itemModifier = optional.get();
                        //攻击速度=基础攻击速度+额外攻击速度
                        AttributeModifier attributeModifier = new AttributeModifier(Item.BASE_ATTACK_SPEED_UUID, "Weapon modifier", itemModifier.getAmount() + attr.getValue(), AttributeModifier.Operation.ADDITION);
                        //需要移除原有属性，否则无法正常生效
                        event.removeAttribute(Attributes.ATTACK_SPEED);
                        event.addModifier(Attributes.ATTACK_SPEED, attributeModifier);
//                        Logger.info(String.format("攻击速度增强：%f -> %f", itemModifier.getAmount(), attributeModifier.getAmount()));
                    }
                }
            }
        });
    }

}
