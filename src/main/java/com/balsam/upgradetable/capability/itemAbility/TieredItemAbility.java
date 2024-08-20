package com.balsam.upgradetable.capability.itemAbility;

import com.balsam.upgradetable.capability.pojo.ItemLevelPO;
import com.balsam.upgradetable.config.Constants;
import net.minecraft.nbt.CompoundNBT;

public class TieredItemAbility extends BaseItemAbility{

    private ItemLevelPO attack;
    private ItemLevelPO attackSpeed;

    public TieredItemAbility() {
        super(Constants.MAX_LEVEL_TOTAL, Constants.MAX_LEVEL_PER_ABILITY);
        this.attack = new ItemLevelPO(Constants.MAX_LEVEL_PER_ABILITY, 1f);
        this.attackSpeed = new ItemLevelPO(Constants.MAX_LEVEL_PER_ABILITY, 0.1f);
    }

    public ItemLevelPO getAttack() {
        return attack;
    }

    public ItemLevelPO getAttackSpeed() {
        return attackSpeed;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compoundNBT = super.serializeNBT();
        compoundNBT.put("attack", attack.serializeNBT());
        compoundNBT.put("attackSpeed", attackSpeed.serializeNBT());
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT compoundNBT) {
        super.deserializeNBT(compoundNBT);
        this.attack.deserializeNBT(compoundNBT.getCompound("attack"));
        this.attackSpeed.deserializeNBT(compoundNBT.getCompound("attackSpeed"));
    }

}
