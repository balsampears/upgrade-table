package com.balsam.upgradetable.cache;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class AmmoCostCache extends ItemCache<ItemStack, PlayerEntity>{

    @Override
    public void setValue(ItemStack itemStack, PlayerEntity playerEntity) {
        for (ItemStack item : playerEntity.inventory.items) {
            super.setValue(item, playerEntity);
        }
        for (ItemStack item : playerEntity.inventory.armor) {
            super.setValue(item, playerEntity);
        }
        for (ItemStack item : playerEntity.inventory.offhand) {
            super.setValue(item, playerEntity);
        }
    }

    @Override
    public PlayerEntity getValue(ItemStack itemStack) {
        if (itemStack == ItemStack.EMPTY) return null;
        return super.getValue(itemStack);
    }

    public void removeValueByPlayer(PlayerEntity playerEntity){
        for (ItemStack itemStack : cache.keySet()) {
            for (ItemStack item : playerEntity.inventory.items) {
                if (item.equals(itemStack))
                    cache.remove(itemStack);
            }
            for (ItemStack item : playerEntity.inventory.armor) {
                if (item.equals(itemStack))
                    cache.remove(itemStack);
            }
            for (ItemStack item : playerEntity.inventory.offhand) {
                if (item.equals(itemStack))
                    cache.remove(itemStack);
            }
        }
    }
}
