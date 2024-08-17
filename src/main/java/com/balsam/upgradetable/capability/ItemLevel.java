package com.balsam.upgradetable.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;

/**
 * 等级能力实现
 */
public class ItemLevel implements IItemLevel {

    private int level;
    private int maxLevel;

    public ItemLevel(int maxLevel) {
        this.level = 0;
        this.maxLevel = maxLevel;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putInt("level", this.getLevel());
        compoundNBT.putInt("maxLevel", this.getMaxLevel());
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT compoundNBT) {
        this.setLevel(compoundNBT.getInt("level"));
        this.setMaxLevel(compoundNBT.getInt("maxLevel"));
    }
}
