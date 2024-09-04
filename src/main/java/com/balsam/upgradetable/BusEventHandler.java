package com.balsam.upgradetable;

import com.balsam.upgradetable.capability.ItemAbilityProvider;
import com.balsam.upgradetable.capability.itemAbility.*;
import com.balsam.upgradetable.config.AttributeEnum;
import com.balsam.upgradetable.config.Constants;
import com.balsam.upgradetable.mixin.interfaces.IExtraDamage;
import com.balsam.upgradetable.mod.ModCapability;
import com.balsam.upgradetable.mod.ModConfig;
import com.balsam.upgradetable.util.ItemStackUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Map;

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
        } else if (event.getSource() instanceof DamageSource){
            //额外攻击伤害
            DamageSource source = event.getSource();
            if (source.getDirectEntity() instanceof LivingEntity){
                ItemStack useItem = ItemStackUtil.getUseItem((LivingEntity) source.getDirectEntity());
                if (useItem == ItemStack.EMPTY) return;
                useItem.getCapability(ModCapability.itemAbility).ifPresent(o->{
                    ((BaseItemAbility)o).findAttribute(AttributeEnum.ATTACK_DAMAGE).ifPresent(attr->{
                        event.setAmount(event.getAmount() + attr.getValue());
                    });
                });
            }
        }
    }

}
