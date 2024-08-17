package com.balsam.upgradetable.capability;

import com.balsam.upgradetable.config.Constants;
import com.balsam.upgradetable.mod.ModCapability;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 将能力与物品关联
 */
public class ItemLevelProvider implements ICapabilitySerializable<CompoundNBT>, ICapabilityProvider {

    private IItemLevel level = new ItemLevel(Constants.MAX_LEVEL);
//    private Capability.IStorage<IItemLevel> levelIStorage = CapabilityRegistry.Level.getStorage();

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (ModCapability.Level.equals(cap)){
            return LazyOptional.of(()->level).cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.put("upgrade", level.serializeNBT());
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        CompoundNBT compoundNBT = (CompoundNBT) nbt.get("upgrade");
        level.deserializeNBT(compoundNBT);
    }

}
