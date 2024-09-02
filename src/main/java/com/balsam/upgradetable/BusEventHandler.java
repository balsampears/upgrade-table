package com.balsam.upgradetable;

import com.balsam.upgradetable.cache.CacheFactory;
import com.balsam.upgradetable.cache.ThrowDamageCache;
import com.balsam.upgradetable.capability.ItemAbilityProvider;
import com.balsam.upgradetable.capability.itemAbility.*;
import com.balsam.upgradetable.config.AttributeEnum;
import com.balsam.upgradetable.config.Constants;
import com.balsam.upgradetable.mod.ModConfig;
import com.balsam.upgradetable.cache.BowDamageCache;
import com.balsam.upgradetable.util.Logger;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

//        if (item instanceof TieredItem) {
//            itemAbility = new TieredItemAbility();
//        }
//        else if (item instanceof BowItem) {
//            itemAbility = new BowItemAbility();
//        }
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
     * 实现功能：额外弓箭伤害
     */
    @SubscribeEvent
    public static void onLivingHurtEvent(LivingHurtEvent event){
        if (event.getSource() instanceof IndirectEntityDamageSource){
            IndirectEntityDamageSource eventSource = (IndirectEntityDamageSource) event.getSource();
            //额外弓箭伤害
            if (eventSource.msgId.equals("arrow") && eventSource.getDirectEntity() instanceof AbstractArrowEntity){
                AbstractArrowEntity arrowEntity = (AbstractArrowEntity) eventSource.getDirectEntity();
                BowDamageCache itemCache = (BowDamageCache) CacheFactory.Map.get(AttributeEnum.BOW_DAMAGE);
                Float value = itemCache.getValue(arrowEntity);
                if (value!=null) {
                    //如果没满弦，则伤害降低1/3
                    value = value / (arrowEntity.isCritArrow() ? 1 : 3);
                    event.setAmount(event.getAmount() + value);
                    itemCache.removeValue(arrowEntity);
//                    Logger.info("额外增加伤害："+value);
                }
            }
            //额外投射伤害
            else if (eventSource.getDirectEntity() instanceof ThrowableEntity){
                ThrowableEntity throwableEntity = (ThrowableEntity) eventSource.getDirectEntity();
                ThrowDamageCache itemCache = (ThrowDamageCache) CacheFactory.Map.get(AttributeEnum.THROW_DAMAGE);
                Float value = itemCache.getValue(throwableEntity);
                if (value!=null){
                    event.setAmount(event.getAmount() + value);
                    itemCache.removeValue(throwableEntity);
//                    Logger.info("额外增加伤害："+value);
                }
            }
        }
    }

}
