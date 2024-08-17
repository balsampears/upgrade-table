package com.balsam.upgradetable.block;

import com.balsam.upgradetable.capability.IItemLevel;
import com.balsam.upgradetable.config.Constants;
import com.balsam.upgradetable.mod.ModCapability;
import com.balsam.upgradetable.network.Networking;
import com.balsam.upgradetable.network.pack.UpgradeButtonPack;
import com.balsam.upgradetable.registry.TileEntityTypeRegistry;
import com.balsam.upgradetable.util.Logger;
import com.google.gson.Gson;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nullable;

public class UpgradeTableTileEntity extends TileEntity implements INamedContainerProvider {

    private Inventory inventory = new Inventory(2);

    public UpgradeTableTileEntity() {
        super(TileEntityTypeRegistry.UpgradeTableTileEntity.get());
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(String.format("gui.%s.upgradetable", Constants.MOD_ID));
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new UpgradeTableContainer(id, this.level, this.worldPosition, playerInventory);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void upgrade() {
        ItemStack itemStack = inventory.getItem(1);
        if (itemStack.isEmpty()) return;
//        CompoundNBT tag = itemStack.getOrCreateTag();
//        int currentLevel = tag.getInt("currentLevel");
//        tag.putInt("currentLevel", ++currentLevel);
//        itemStack.save(tag);

        LazyOptional<IItemLevel> levelOption = itemStack.getCapability(ModCapability.Level);
        levelOption.ifPresent(iItemLevel -> {
            //升级
            int level = iItemLevel.getLevel();
            int maxLevel = iItemLevel.getMaxLevel();
            level = Math.min(level + 1, maxLevel);
            iItemLevel.setLevel(level);
            Logger.info(String.format("%s当前等级为：%d/%d", iItemLevel.hashCode(), level, maxLevel));
            //更新服务端stack
//            ItemStack copy = itemStack.copy();
//            inventory.setItem(1, copy);
            iItemLevel.deserializeNBT(iItemLevel.serializeNBT());
            //通知客户端更新
            Networking.INSTANCE.send(PacketDistributor.ALL.noArg(), new UpgradeButtonPack(iItemLevel.serializeNBT()));
        });
    }

    @OnlyIn(Dist.CLIENT)
    public void syncData(CompoundNBT compoundNBT) {
        ItemStack itemStack = inventory.getItem(1);
        if (itemStack.isEmpty()) return;
        itemStack.getCapability(ModCapability.Level).ifPresent(o->{
            o.deserializeNBT(compoundNBT);
            Logger.info(String.format("%s同步数据：%s", o.hashCode(),new Gson().toJson(compoundNBT)));
        });
    }
}
