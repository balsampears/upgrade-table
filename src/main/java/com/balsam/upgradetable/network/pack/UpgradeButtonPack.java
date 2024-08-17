package com.balsam.upgradetable.network.pack;

import com.balsam.upgradetable.block.UpgradeTableContainer;
import com.balsam.upgradetable.block.UpgradeTableScreen;
import com.balsam.upgradetable.registry.ContainerTypeRegistry;
import com.balsam.upgradetable.util.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class UpgradeButtonPack {
    private CompoundNBT compoundNBT;

    public UpgradeButtonPack(CompoundNBT compoundNBT) {
        this.compoundNBT = compoundNBT;
    }

    public UpgradeButtonPack(PacketBuffer buffer) {
        this.compoundNBT = buffer.readNbt();
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeNbt(compoundNBT);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
                Container containerMenu = context.getSender().containerMenu;
                if (containerMenu instanceof UpgradeTableContainer) {
                    UpgradeTableContainer container = (UpgradeTableContainer) containerMenu;
                    container.blockEntity.upgrade();
                }
            } else if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT){
                Screen screen = Minecraft.getInstance().screen;
                if (screen instanceof UpgradeTableScreen) {
                    UpgradeTableContainer container = ((UpgradeTableScreen) screen).getMenu();
                    container.blockEntity.syncData(compoundNBT);
                }
            }
        });
        context.setPacketHandled(true);
    }

}
