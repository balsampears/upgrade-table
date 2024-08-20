package com.balsam.upgradetable.mod;

import com.balsam.upgradetable.capability.itemAbility.IItemAbility;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class ModCapability {
    @CapabilityInject(IItemAbility.class)
    public static Capability<IItemAbility> itemAbility;
}
