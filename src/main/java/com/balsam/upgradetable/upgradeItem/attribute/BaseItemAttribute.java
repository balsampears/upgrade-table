package com.balsam.upgradetable.upgradeItem.attribute;

public abstract class BaseItemAttribute {

    protected int maxLevel;
    protected int currentLevel;
    protected float addAttriPerLevel;

    public BaseItemAttribute(int maxLevel, float addAttriPerLevel){
        this.maxLevel = maxLevel;
        this.addAttriPerLevel = addAttriPerLevel;
        currentLevel = 0;
    }

    /**
     * 判断是否能升级
     */
    public boolean canLevel(){
        return currentLevel < maxLevel;
    }

    /**
     * 升级
     */
    public boolean upgrade(){
        if (!canLevel()) return false;
        currentLevel++;
        return true;
    }

    /**
     * 获取增加的能力值
     */
    public float getValue(){
        return this.currentLevel * addAttriPerLevel;
    }
}
