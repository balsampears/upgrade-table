package com.balsam.upgradetable.block.gui;

import com.balsam.upgradetable.mod.ModCapability;
import com.balsam.upgradetable.network.Networking;
import com.balsam.upgradetable.network.pack.UpgradeButtonPack;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class UpgradeTableButton implements Button.IPressable{
    private final UpgradeTableScreen screen;
    private final int index;
    public UpgradeTableButton(UpgradeTableScreen screen,int index){
        this.screen = screen;
        this.index = index;
    }

    @Override
    public void onPress(Button button) {
        Inventory inventory = this.screen.getMenu().blockEntity.getInventory();
        ItemStack itemStack = inventory.getItem(0);
        if (itemStack.isEmpty()) return;
        itemStack.getCapability(ModCapability.itemAbility).ifPresent(o -> {
            Networking.INSTANCE.sendToServer(new UpgradeButtonPack(o.serializeNBT(), index));
        });

    }
}
