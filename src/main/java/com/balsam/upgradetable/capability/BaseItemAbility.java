package com.balsam.upgradetable.capability;

import com.balsam.upgradetable.capability.pojo.ItemLevelPO;
import net.minecraft.nbt.CompoundNBT;

/**
 * 等级能力实现
 */
public abstract class BaseItemAbility implements IItemAbility {

    private ItemLevelPO total;
    private ItemLevelPO duration;

    public ItemLevelPO getTotal() {
        return total;
    }

    public ItemLevelPO getDuration() {
        return duration;
    }

    public BaseItemAbility(int totalMaxLevel, int durationMaxLevel) {
        this.total = new ItemLevelPO(totalMaxLevel, 0f);
        this.duration = new ItemLevelPO(durationMaxLevel, 100f);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.put("total", total.serializeNBT());
        compoundNBT.put("duration", duration.serializeNBT());
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT compoundNBT) {
        this.total.deserializeNBT(compoundNBT.getCompound("total"));
        this.duration.deserializeNBT(compoundNBT.getCompound("duration"));
    }
}
