package com.balsam.upgradetable.capability.itemAbility;

import com.balsam.upgradetable.capability.pojo.ItemAttributePO;
import com.balsam.upgradetable.config.AttributeEnum;
import com.balsam.upgradetable.config.Constants;

public class BowItemAbility extends BaseItemAbility {

    private final ItemAttributePO bowDamage;
    private final ItemAttributePO bowArcTime;
    private final ItemAttributePO ammoCost;

    public BowItemAbility() {
        super(Constants.MAX_LEVEL_TOTAL);
        this.bowDamage = new ItemAttributePO(AttributeEnum.BOW_DAMAGE, Constants.MAX_LEVEL_PER_ABILITY, new float[]{0.5f, 1.5f, 3f, 5f, 7f, 10f}, 50);
        this.bowArcTime = new ItemAttributePO(AttributeEnum.BOW_ARC_TIME, Constants.MAX_LEVEL_PER_ABILITY, new float[]{0.05f, 0.14f, 0.25f, 0.4f, 0.58f, 0.8f}, 50);
        this.ammoCost = new ItemAttributePO(AttributeEnum.AMMO_COST, Constants.MAX_LEVEL_PER_ABILITY, new float[]{0.02f, 0.05f, 0.1f, 0.15f, 0.5f, 0.8f}, 50);
        this.duration = new ItemAttributePO(AttributeEnum.MAX_DURATION, Constants.MAX_LEVEL_PER_ABILITY, new float[]{100,200,300,400,500,600});
        this.displayAttributes.add(this.bowDamage);
        this.displayAttributes.add(this.bowArcTime);
        this.displayAttributes.add(this.ammoCost);
        this.displayAttributes.add(this.duration);
    }

    public ItemAttributePO getBowDamage() {
        return bowDamage;
    }

    public ItemAttributePO getBowArcTime() {
        return bowArcTime;
    }

}
