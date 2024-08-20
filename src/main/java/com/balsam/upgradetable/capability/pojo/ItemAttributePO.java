package com.balsam.upgradetable.capability.pojo;

import com.balsam.upgradetable.config.AttributeEnum;
import net.minecraft.nbt.CompoundNBT;

public class ItemAttributePO {

    /**
     * 属性枚举
     */
    private AttributeEnum attributeEnum;
    /**
     * 当前等级
     */
    private int level;
    /**
     * 最大等级
     */
    private int maxLevel;
    /**
     * 不同等级的数据值
     */
    private float[] perLevelValues;

    public ItemAttributePO(AttributeEnum attributeEnum, int maxLevel, float[] perLevelValues) {
        this.attributeEnum = attributeEnum;
        this.level = 0;
        this.maxLevel = maxLevel;
        if (perLevelValues!=null) {
            if (perLevelValues.length != maxLevel)
                throw new IllegalArgumentException("values array length must be equals max Level");
            this.perLevelValues = perLevelValues;
        }
    }

    public AttributeEnum getAttributeEnum() {
        return attributeEnum;
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

    public boolean upgrade(int upgradeLevel){
        if (this.level >= this.maxLevel) return false;
        this.level = Math.min(level+upgradeLevel, maxLevel);
        return true;
    }

    public boolean upgrade(){
        return upgrade(1);
    }

    public float getValue(){
        return this.perLevelValues[this.level-1];
    }

    public float getValue(int level){
        return this.perLevelValues[level-1];
    }
}
