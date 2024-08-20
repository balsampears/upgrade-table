package com.balsam.upgradetable.capability.itemAbility;

import com.balsam.upgradetable.capability.pojo.ItemAttributePO;
import com.balsam.upgradetable.config.AttributeEnum;
import net.minecraft.nbt.CompoundNBT;

import java.util.ArrayList;
import java.util.List;

/**
 * 等级能力实现
 */
public abstract class BaseItemAbility implements IItemAbility {

    /**
     * 所有属性对象集合
     * 所有字段属性必须手动设置添加进这个集合，否则不会保存nbt，也不会在升级台面板展示，也不能被升级
     */
    protected final List<ItemAttributePO> displayAttributes;

    private final ItemAttributePO total;
    private final ItemAttributePO duration;

    public ItemAttributePO getTotal() {
        return total;
    }

    public ItemAttributePO getDuration() {
        return duration;
    }

    public List<ItemAttributePO> getDisplayAttributes() {
        return displayAttributes;
    }

    public BaseItemAbility(int totalMaxLevel, int durationMaxLevel) {
        this.total = new ItemAttributePO(AttributeEnum.Total, totalMaxLevel, null);
        this.duration = new ItemAttributePO(AttributeEnum.MAX_DURATION, durationMaxLevel, new float[]{100,200,300,400,500,600});
        this.displayAttributes = new ArrayList<>();
        this.displayAttributes.add(total);
        this.displayAttributes.add(duration);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compoundNBT = new CompoundNBT();
        for (ItemAttributePO po : displayAttributes) {
            compoundNBT.put(po.getAttributeEnum().getSimpleName(), po.serializeNBT());
        }
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT compoundNBT) {
        for (ItemAttributePO po : displayAttributes) {
            po.deserializeNBT(compoundNBT.getCompound(po.getAttributeEnum().getSimpleName()));
        }
    }
}
