package com.balsam.upgradetable.capability.itemAbility;

import com.balsam.upgradetable.capability.pojo.ItemAttributePO;
import com.balsam.upgradetable.config.AttributeEnum;
import com.balsam.upgradetable.config.Constants;

public class BowItemAbility extends BaseItemAbility {

    private final ItemAttributePO bowPower;
    private final ItemAttributePO bowArcTime;
    private final ItemAttributePO ammoCost;

    public BowItemAbility() {
        super(Constants.MAX_LEVEL_TOTAL);
        this.bowPower = new ItemAttributePO(AttributeEnum.BOW_POWER, Constants.MAX_LEVEL_PER_ABILITY, new float[]{0.5f, 1.4f, 2.5f, 4.0f, 5.8f, 8f}, 50);
        this.bowArcTime = new ItemAttributePO(AttributeEnum.BOW_ARC_TIME, Constants.MAX_LEVEL_PER_ABILITY, new float[]{0.07f, 0.17f, 0.32f, 0.51f, 0.73f, 1f}, 50);
        this.ammoCost = new ItemAttributePO(AttributeEnum.AMMO_COST, Constants.MAX_LEVEL_PER_ABILITY, new float[]{0.02f, 0.05f, 0.1f, 0.15f, 0.5f, 0.8f});
        this.duration = new ItemAttributePO(AttributeEnum.MAX_DURATION, Constants.MAX_LEVEL_PER_ABILITY, new float[]{100,200,300,400,500,600});
        this.displayAttributes.add(this.bowPower);
        this.displayAttributes.add(this.bowArcTime);
        this.displayAttributes.add(this.ammoCost);
        this.displayAttributes.add(this.duration);
    }

    public ItemAttributePO getBowPower() {
        return bowPower;
    }

    public ItemAttributePO getBowArcTime() {
        return bowArcTime;
    }

}
