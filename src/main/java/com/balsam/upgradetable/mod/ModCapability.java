package com.balsam.upgradetable.mod;

import com.balsam.upgradetable.capability.IItemAbility;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class ModCapability {
    @CapabilityInject(IItemAbility.class)
    public static Capability<IItemAbility> Level;
}
