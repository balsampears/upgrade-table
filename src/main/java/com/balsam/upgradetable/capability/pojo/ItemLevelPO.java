package com.balsam.upgradetable.capability.pojo;

import net.minecraft.nbt.CompoundNBT;

public class ItemLevelPO {

    private int level;
    private int maxLevel;
    private float valuePerLevel;

    public ItemLevelPO(int maxLevel, float valuePerLevel) {
        this.level = 0;
        this.maxLevel = maxLevel;
        this.valuePerLevel = valuePerLevel;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public float getValuePerLevel() {
        return valuePerLevel;
    }

    public void setValuePerLevel(float valuePerLevel) {
        this.valuePerLevel = valuePerLevel;
    }

    public CompoundNBT serializeNBT() {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putInt("level", this.getLevel());
        compoundNBT.putInt("maxLevel", this.getMaxLevel());
        return compoundNBT;
    }

    public void deserializeNBT(CompoundNBT compoundNBT) {
        this.setLevel(compoundNBT.getInt("level"));
        this.setMaxLevel(compoundNBT.getInt("maxLevel"));
    }

    public void upgrade(int upgradeLevel){
        this.level = Math.min(level+upgradeLevel, maxLevel);
    }

    public void upgrade(){
        upgrade(1);
    }

    public float getValue(){
        return this.level * this.valuePerLevel;
    }
}
