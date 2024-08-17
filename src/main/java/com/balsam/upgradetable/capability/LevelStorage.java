//package com.balsam.upgradetable.capability;
//
//import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.nbt.INBT;
//import net.minecraft.util.Direction;
//import net.minecraftforge.common.capabilities.Capability;
//
//import javax.annotation.Nullable;
// todo
///**
// * 等级能力使用nbt存储
// */
//public class LevelStorage implements Capability.IStorage<IItemLevel>{
//    @Nullable
//    @Override
//    public INBT writeNBT(Capability<IItemLevel> capability, IItemLevel instance, Direction side) {
//        CompoundNBT compoundNBT = new CompoundNBT();
//        compoundNBT.putInt("level", instance.getLevel());
//        compoundNBT.putInt("maxLevel", instance.getMaxLevel());
//        return compoundNBT;
//    }
//
//    @Override
//    public void readNBT(Capability<IItemLevel> capability, IItemLevel instance, Direction side, INBT nbt) {
//        CompoundNBT compoundNBT = (CompoundNBT) nbt;
//        if (compoundNBT.contains("level")){
//            instance.setLevel(compoundNBT.getInt("level"));
//            instance.setMaxLevel(compoundNBT.getInt("maxLevel"));
//        }
//    }
//}
