package com.balsam.upgradetable;

import com.balsam.upgradetable.capability.ItemAbilityProvider;
import com.balsam.upgradetable.capability.itemAbility.BowItemAbility;
import com.balsam.upgradetable.capability.itemAbility.IItemAbility;
import com.balsam.upgradetable.capability.itemAbility.TieredItemAbility;
import com.balsam.upgradetable.config.Constants;
import com.balsam.upgradetable.util.ArrowCache;
import com.balsam.upgradetable.util.Logger;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class BusEventHandler {
    @SubscribeEvent
    public static void onAttachCapabilitiesEvent(AttachCapabilitiesEvent<ItemStack> event) throws ClassNotFoundException {
        Item item = event.getObject().getItem();
        IItemAbility itemAbility = null;
        if (item instanceof TieredItem) {
            itemAbility = new TieredItemAbility();
        }
        else if (item instanceof BowItem) {
            itemAbility = new BowItemAbility();
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
            if (eventSource.msgId.equals("arrow") && eventSource.getDirectEntity() instanceof AbstractArrowEntity){
                AbstractArrowEntity arrowEntity = (AbstractArrowEntity) eventSource.getDirectEntity();
                Float value = ArrowCache.getValue(arrowEntity);
                if (value!=null) {
                    //如果没满弦，则伤害降低1/3
                    value = value / (arrowEntity.isCritArrow() ? 1 : 3);
                    event.setAmount(event.getAmount() + value);
                    ArrowCache.removeValue(arrowEntity);
                    Logger.info("额外增加伤害："+value);
                }
            }
        }
    }

}
