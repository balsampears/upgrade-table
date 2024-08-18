package com.balsam.upgradetable;

import com.balsam.upgradetable.capability.ItemAbilityProvider;
import com.balsam.upgradetable.capability.SwordItemAbility;
import com.balsam.upgradetable.config.Constants;
import com.balsam.upgradetable.util.Logger;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class BusEventHandler {
    @SubscribeEvent
    public static void onAttachCapabilitiesEvent(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack object = event.getObject();
        if (object.getItem() instanceof SwordItem) {
            event.addCapability(new ResourceLocation(String.format("cap.%s.upgrade", Constants.MOD_ID)),
                    new ItemAbilityProvider(new SwordItemAbility()));
            Logger.info(String.format("物品%s使用'升级'的能力",object.getItem().getRegistryName().toString()));
        }
    }
}
