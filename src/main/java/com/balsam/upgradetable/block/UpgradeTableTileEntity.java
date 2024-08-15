package com.balsam.upgradetable.block;

import com.balsam.upgradetable.config.Constants;
import com.balsam.upgradetable.registry.TileEntityTypeRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class UpgradeTableTileEntity extends TileEntity implements INamedContainerProvider {

    private Inventory inventory = new Inventory(2);

    public UpgradeTableTileEntity() {
        super(TileEntityTypeRegistry.UpgradeTableTileEntity.get());
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(String.format("gui.%s.upgradetable", Constants.MOD_ID));
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new UpgradeTableContainer(id, this.level, this.worldPosition, playerInventory);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void upgrade(){
        ItemStack itemStack = inventory.getItem(1);
        if (itemStack.isEmpty()) return;
        CompoundNBT tag = itemStack.getOrCreateTag();
        int currentLevel = tag.getInt("currentLevel");
        tag.putInt("currentLevel", ++currentLevel);
        itemStack.save(tag);
    }
}
