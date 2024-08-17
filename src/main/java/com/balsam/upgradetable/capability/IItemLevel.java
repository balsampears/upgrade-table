package com.balsam.upgradetable.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * 等级能力
 */
public interface IItemLevel extends INBTSerializable<CompoundNBT> {
    int getLevel();
    void setLevel(int level);
    int getMaxLevel();
    void setMaxLevel(int maxLevel);
}
