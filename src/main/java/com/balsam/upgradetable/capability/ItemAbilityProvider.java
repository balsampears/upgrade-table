package com.balsam.upgradetable.capability;

import com.balsam.upgradetable.capability.itemAbility.IItemAbility;
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
public class ItemAbilityProvider implements ICapabilitySerializable<CompoundNBT>, ICapabilityProvider {

    private IItemAbility itemAbility;
//    private Capability.IStorage<IItemLevel> levelIStorage = CapabilityRegistry.Level.getStorage();

    public ItemAbilityProvider(IItemAbility iItemAbility) {
        this.itemAbility = iItemAbility;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (ModCapability.itemAbility.equals(cap)){
            return LazyOptional.of(()-> itemAbility).cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.put("upgrade", itemAbility.serializeNBT());
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        CompoundNBT compoundNBT = (CompoundNBT) nbt.get("upgrade");
        itemAbility.deserializeNBT(compoundNBT);
    }

}
