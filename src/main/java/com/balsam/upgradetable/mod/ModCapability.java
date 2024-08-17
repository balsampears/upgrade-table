package com.balsam.upgradetable.mod;

import com.balsam.upgradetable.capability.IItemLevel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.items.IItemHandler;

public class ModCapability {
    @CapabilityInject(IItemLevel.class)
    public static Capability<IItemLevel> Level;
}
