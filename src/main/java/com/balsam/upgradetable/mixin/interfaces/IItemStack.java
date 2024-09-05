package com.balsam.upgradetable.mixin.interfaces;

import net.minecraft.entity.player.PlayerEntity;

public interface IItemStack {

    void setUsingPlayer(PlayerEntity usingPlayer);

    PlayerEntity getUsingPlayer();

    Integer getAdditionalMaxDamage();

    void setAdditionalMaxDamage(Integer additionalMaxDamage);

}
