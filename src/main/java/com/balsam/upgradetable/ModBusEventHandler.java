package com.balsam.upgradetable;

import com.balsam.upgradetable.block.UpgradeTableScreen;
import com.balsam.upgradetable.capability.itemAbility.IItemAbility;
import com.balsam.upgradetable.network.Networking;
import com.balsam.upgradetable.registry.ContainerTypeRegistry;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBusEventHandler {
    @SubscribeEvent
    public static void onClientSetupEvent(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ScreenManager.register(ContainerTypeRegistry.UpgradeTableContainer.get(), UpgradeTableScreen::new);
        });
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onFMLLoadCompleteEvent(FMLLoadCompleteEvent event) {
    }

    @SubscribeEvent
    public static void onFMLCommonSetupEvent(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Networking.registryMessage();
            CapabilityManager.INSTANCE.register(IItemAbility.class, new Capability.IStorage<IItemAbility>() {
                @Nullable
                @Override
                public INBT writeNBT(Capability<IItemAbility> capability, IItemAbility instance, Direction side) {
                    return null;
                }

                @Override
                public void readNBT(Capability<IItemAbility> capability, IItemAbility instance, Direction side, INBT nbt) {

                }
            }, () -> null);
        });
    }

}
