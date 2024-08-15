package com.balsam.upgradetable.registry;

import com.balsam.upgradetable.block.UpgradeTableBlock;
import com.balsam.upgradetable.config.Constants;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

    public static final RegistryObject<BlockItem> UpgradeTableBlock = ITEMS.register("upgrade_table_block",
            ()-> new BlockItem(BlockRegistry.UpgradeTableBlock.get(), new Item.Properties().tab(ItemGroup.TAB_DECORATIONS)));

}

