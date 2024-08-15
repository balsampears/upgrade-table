package com.balsam.upgradetable;

import com.balsam.upgradetable.config.Constants;
import com.balsam.upgradetable.registry.BlockRegistry;
import com.balsam.upgradetable.registry.ContainerTypeRegistry;
import com.balsam.upgradetable.registry.ItemRegistry;
import com.balsam.upgradetable.registry.TileEntityTypeRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class Main {

    public Main(){
        ItemRegistry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BlockRegistry.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TileEntityTypeRegistry.TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ContainerTypeRegistry.CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
