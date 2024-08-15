package com.balsam.upgradetable.network.pack;

import com.balsam.upgradetable.block.UpgradeTableContainer;
import com.balsam.upgradetable.block.UpgradeTableScreen;
import com.balsam.upgradetable.util.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class UpgradeButtonPack {
    private String message;

    public UpgradeButtonPack(String message) {
        this.message = message;
    }

    public UpgradeButtonPack(PacketBuffer buffer) {
        this.message = buffer.readUtf(Short.MAX_VALUE);
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeUtf(message);
    }

    public void handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            Screen screen = Minecraft.getInstance().screen;
            if (screen instanceof UpgradeTableScreen){
                UpgradeTableContainer container = ((UpgradeTableScreen) screen).getMenu();
                container.blockEntity.upgrade();
            }
            Logger.info(this.message);
        });
        context.setPacketHandled(true);
    }

}
