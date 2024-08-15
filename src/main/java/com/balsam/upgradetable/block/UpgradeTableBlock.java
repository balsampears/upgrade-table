package com.balsam.upgradetable.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class UpgradeTableBlock extends Block {
    public UpgradeTableBlock() {
        super(Properties.of(Material.STONE)
                .requiresCorrectToolForDrops()
                .strength(3f, 6f)
                .harvestTool(ToolType.PICKAXE));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new UpgradeTableTileEntity();
    }

    @Override
    public ActionResultType use(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult blockRayTraceResult) {
        if (!world.isClientSide && hand == Hand.MAIN_HAND){
            UpgradeTableTileEntity blockEntity = (UpgradeTableTileEntity) world.getBlockEntity(blockPos);
            NetworkHooks.openGui((ServerPlayerEntity) playerEntity, blockEntity, packetBuffer ->
                    packetBuffer.writeBlockPos(blockPos));
        }
        return ActionResultType.SUCCESS;
    }
}
