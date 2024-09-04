package com.balsam.upgradetable.block;

import com.balsam.upgradetable.registry.ContainerTypeRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UpgradeTableContainer extends Container {

    public UpgradeTableTileEntity blockEntity;

    public UpgradeTableContainer(int id, World world, BlockPos blockPos, PlayerInventory playerInventory) {
        super(ContainerTypeRegistry.UpgradeTableContainer.get(), id);
        //玩家物品栏
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, 9 + i * 9 + j, 15 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 15 + i * 18, 142));
        }
        //方块物品栏
        blockEntity = (UpgradeTableTileEntity) world.getBlockEntity(blockPos);
        this.addSlot(new Slot(blockEntity.getInventory(), 0, 11, 20));
        this.addSlot(new Slot(blockEntity.getInventory(), 1, 11, 50));
    }


    @Override
    public boolean stillValid(PlayerEntity playerEntity) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity playerEntity, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;
        ItemStack oldItemStck = slot.getItem();
        ItemStack newItemStck = oldItemStck.copy();
        if (index < 36){
            if (!this.moveItemStackTo(oldItemStck, 36, this.slots.size(), false))
                return ItemStack.EMPTY;
        } else {
            if (!this.moveItemStackTo(oldItemStck, 0, 36, false))
                return ItemStack.EMPTY;
        }
        if (oldItemStck.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        }else{
            slot.setChanged();
        }
        return newItemStck;
    }

    @Override
    public void removed(PlayerEntity playerEntity) {
        super.removed(playerEntity);
        if (!playerEntity.level.isClientSide){
            this.clearContainer(playerEntity, playerEntity.level, blockEntity.getInventory());
        }
    }
}
