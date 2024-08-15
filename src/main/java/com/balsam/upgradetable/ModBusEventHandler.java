package com.balsam.upgradetable;

import com.balsam.upgradetable.block.UpgradeTableScreen;
import com.balsam.upgradetable.mixin.MixSwordItem;
import com.balsam.upgradetable.network.Networking;
import com.balsam.upgradetable.registry.ContainerTypeRegistry;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBusEventHandler {
    @SubscribeEvent
    public static void onClientSetupEvent(FMLClientSetupEvent event) {
        event.enqueueWork(()->{
            ScreenManager.register(ContainerTypeRegistry.UpgradeTableContainer.get(), UpgradeTableScreen::new);
        });
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onFMLLoadCompleteEvent(FMLLoadCompleteEvent event) {
    }

    @SubscribeEvent
    public static void onFMLCommonSetupEvent(FMLCommonSetupEvent event) {
        event.enqueueWork(Networking::registryMessage);
    }
}
