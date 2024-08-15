package com.balsam.upgradetable.network;

import com.balsam.upgradetable.config.Constants;
import com.balsam.upgradetable.network.pack.UpgradeButtonPack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Networking {
    public static SimpleChannel INSTANCE;
    public static final String VERSION = "1.0";
    public static int id = 0;

    public static int nextId(){
        return id++;
    }

    public static void registryMessage(){
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Constants.MOD_ID, "default_channel"),
                ()->VERSION,
                (version)->version.equals(VERSION),
                (version)->version.equals(VERSION));
        INSTANCE.messageBuilder(UpgradeButtonPack.class, nextId())
                .encoder((upgradeButtonPack, buffer) -> upgradeButtonPack.encode(buffer))
                .decoder(buffer -> new UpgradeButtonPack(buffer))
                .consumer((upgradeButtonPack, supplier) -> {
                    upgradeButtonPack.handle(supplier.get());
                }).add();
    }


}
