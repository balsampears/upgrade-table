package com.balsam.upgradetable;

import com.balsam.upgradetable.config.Constants;
import com.balsam.upgradetable.mod.ModConfig;
import com.balsam.upgradetable.registry.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class Main {

    public Main(){
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ItemRegistry.ITEMS.register(bus);
        BlockRegistry.BLOCKS.register(bus);
        TileEntityTypeRegistry.TILE_ENTITIES.register(bus);
        ContainerTypeRegistry.CONTAINERS.register(bus);

        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, ModConfig.COMMON_CONFIG);

    }
}
