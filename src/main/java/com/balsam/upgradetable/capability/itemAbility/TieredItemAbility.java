package com.balsam.upgradetable.capability.itemAbility;

import com.balsam.upgradetable.capability.pojo.ItemAttributePO;
import com.balsam.upgradetable.config.AttributeEnum;
import com.balsam.upgradetable.config.Constants;

public class TieredItemAbility extends BaseItemAbility {

    public TieredItemAbility() {
        super(Constants.MAX_LEVEL_TOTAL);
        ItemAttributePO attack = new ItemAttributePO(AttributeEnum.ATTACK_DAMAGE, Constants.MAX_LEVEL_PER_ABILITY, new float[]{0.5f, 1.4f, 2.5f, 4.0f, 5.8f, 8f}, 50);
        ItemAttributePO attackSpeed = new ItemAttributePO(AttributeEnum.ATTACK_SPEED, Constants.MAX_LEVEL_PER_ABILITY, new float[]{0.07f, 0.17f, 0.32f, 0.51f, 0.73f, 1f}, 50);
        ItemAttributePO duration = new ItemAttributePO(AttributeEnum.MAX_DURATION, Constants.MAX_LEVEL_PER_ABILITY, new float[]{100,200,300,400,500,600});
        this.displayAttributes.add(attack);
        this.displayAttributes.add(attackSpeed);
        this.displayAttributes.add(duration);
    }


}
