package com.balsam.upgradetable.registry;

import com.balsam.upgradetable.block.UpgradeTableBlock;
import com.balsam.upgradetable.config.Constants;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Constants.MOD_ID);

    public static final RegistryObject<UpgradeTableBlock> UpgradeTableBlock = BLOCKS.register("upgrade_table_block", UpgradeTableBlock::new);

}
