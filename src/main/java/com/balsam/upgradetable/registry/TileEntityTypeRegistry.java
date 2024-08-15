package com.balsam.upgradetable.registry;

import com.balsam.upgradetable.block.UpgradeTableTileEntity;
import com.balsam.upgradetable.config.Constants;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityTypeRegistry{

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Constants.MOD_ID);

    public static final RegistryObject<TileEntityType<UpgradeTableTileEntity>> UpgradeTableTileEntity = TILE_ENTITIES.register("upgrade_table_tile_entity", ()->
            TileEntityType.Builder.of(UpgradeTableTileEntity::new, BlockRegistry.UpgradeTableBlock.get()).build(null));

}
