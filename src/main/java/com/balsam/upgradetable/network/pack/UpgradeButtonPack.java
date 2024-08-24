package com.balsam.upgradetable.network.pack;

import com.balsam.upgradetable.block.UpgradeTableContainer;
import com.balsam.upgradetable.block.gui.UpgradeTableScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class UpgradeButtonPack {
    private CompoundNBT compoundNBT;
    private int upgradeItemIndex;

    public UpgradeButtonPack(CompoundNBT compoundNBT, int upgradeItemIndex) {
        this.compoundNBT = compoundNBT;
        this.upgradeItemIndex = upgradeItemIndex;
    }

    public UpgradeButtonPack(PacketBuffer buffer) {
        this.compoundNBT = buffer.readNbt();
        this.upgradeItemIndex = buffer.readInt();
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeNbt(compoundNBT);
        buffer.writeInt(upgradeItemIndex);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
                Container containerMenu = context.getSender().containerMenu;
                if (containerMenu instanceof UpgradeTableContainer) {
                    UpgradeTableContainer container = (UpgradeTableContainer) containerMenu;
                    container.blockEntity.upgrade(upgradeItemIndex);
                }
            } else if (context.getDirection() == NetworkDirection.PLAY_TO_CLIENT){
                Screen screen = Minecraft.getInstance().screen;
                if (screen instanceof UpgradeTableScreen) {
                    UpgradeTableContainer container = ((UpgradeTableScreen) screen).getMenu();
                    container.blockEntity.syncData(compoundNBT, upgradeItemIndex);
                }
            }
        });
        context.setPacketHandled(true);
    }

}
