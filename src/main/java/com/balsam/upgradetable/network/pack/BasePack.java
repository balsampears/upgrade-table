package com.balsam.upgradetable.network.pack;

import net.minecraft.network.PacketBuffer;

public abstract class BasePack {

    public abstract void encode(PacketBuffer buffer);

    public abstract void decode(PacketBuffer buffer);
}
