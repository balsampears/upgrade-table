package com.balsam.upgradetable.util;

import com.balsam.upgradetable.mixin.MixinItemStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItemStackCache {

    private static Map<ItemStack, PlayerEntity> cache = new ConcurrentHashMap<>();

    public static void setPlayerCache(PlayerEntity playerEntity){
        for (ItemStack item : playerEntity.inventory.items) {
            cache.put(item, playerEntity);
        }
        for (ItemStack item : playerEntity.inventory.armor) {
            cache.put(item, playerEntity);
        }
        for (ItemStack item : playerEntity.inventory.offhand) {
            cache.put(item, playerEntity);
        }
    }

    public static PlayerEntity getPlayer(ItemStack itemStack){
        if (itemStack == ItemStack.EMPTY) return null;
        return cache.get(itemStack);
    }

    public static void clearPlayerCache(PlayerEntity playerEntity){
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
