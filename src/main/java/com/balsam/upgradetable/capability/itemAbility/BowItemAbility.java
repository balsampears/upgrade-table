package com.balsam.upgradetable.capability.itemAbility;

import com.balsam.upgradetable.capability.pojo.ItemAttributePO;
import com.balsam.upgradetable.config.AttributeEnum;
import com.balsam.upgradetable.config.Constants;

public class BowItemAbility extends BaseItemAbility {

    public BowItemAbility() {
        super(Constants.MAX_LEVEL_TOTAL);
        ItemAttributePO bowDamage = new ItemAttributePO(AttributeEnum.BOW_DAMAGE, Constants.MAX_LEVEL_PER_ABILITY, new float[]{0.5f, 1.5f, 3f, 5f, 7f, 10f}, 50);
        ItemAttributePO bowArcTime = new ItemAttributePO(AttributeEnum.BOW_ARC_TIME, Constants.MAX_LEVEL_PER_ABILITY, new float[]{0.05f, 0.14f, 0.25f, 0.4f, 0.58f, 0.8f}, 50);
        ItemAttributePO ammoCost = new ItemAttributePO(AttributeEnum.AMMO_COST, Constants.MAX_LEVEL_PER_ABILITY, new float[]{0.02f, 0.05f, 0.1f, 0.15f, 0.5f, 0.8f}, 50);
        ItemAttributePO duration = new ItemAttributePO(AttributeEnum.MAX_DURATION, Constants.MAX_LEVEL_PER_ABILITY, new float[]{100,200,300,400,500,600});
        this.displayAttributes.add(bowDamage);
        this.displayAttributes.add(bowArcTime);
        this.displayAttributes.add(ammoCost);
        this.displayAttributes.add(duration);
    }


}
