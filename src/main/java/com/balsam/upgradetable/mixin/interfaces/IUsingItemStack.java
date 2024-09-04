package com.balsam.upgradetable.mixin.interfaces;

import net.minecraft.entity.player.PlayerEntity;

/**
 * 使用物品中的itemstack
 */
public interface IUsingItemStack {

    void setUsingPlayer(PlayerEntity usingPlayer);

    PlayerEntity getUsingPlayer();

}
