package com.balsam.upgradetable.registry;

import com.balsam.upgradetable.block.UpgradeTableContainer;
import com.balsam.upgradetable.config.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ContainerTypeRegistry {

    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, Constants.MOD_ID);

    public static final RegistryObject<ContainerType<UpgradeTableContainer>> UpgradeTableContainer = CONTAINERS.register("obsidian_first_container",
            () -> IForgeContainerType.create((int windowId, PlayerInventory inv, PacketBuffer data) -> new UpgradeTableContainer(windowId,
                    Minecraft.getInstance().level, data.readBlockPos(), inv)));
}
