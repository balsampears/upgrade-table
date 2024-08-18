package com.balsam.upgradetable.block;

import com.balsam.upgradetable.capability.BaseItemAbility;
import com.balsam.upgradetable.capability.IItemAbility;
import com.balsam.upgradetable.capability.SwordItemAbility;
import com.balsam.upgradetable.config.Constants;
import com.balsam.upgradetable.mod.ModCapability;
import com.balsam.upgradetable.network.Networking;
import com.balsam.upgradetable.network.pack.UpgradeButtonPack;
import com.balsam.upgradetable.registry.ItemRegistry;
import com.balsam.upgradetable.registry.TileEntityTypeRegistry;
import com.balsam.upgradetable.util.Logger;
import com.google.common.collect.ImmutableMultimap;
import com.google.gson.Gson;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

import static net.minecraft.item.Item.BASE_ATTACK_DAMAGE_UUID;

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

        LazyOptional<IItemAbility> levelOption = itemStack.getCapability(ModCapability.Level);
        levelOption.ifPresent(o -> {
            BaseItemAbility baseItemAbility = (BaseItemAbility) o;
            //升级
            baseItemAbility.getTotal().upgrade();
            Logger.info(String.format("当前总等级为：%d/%d", baseItemAbility.getTotal().getLevel(), baseItemAbility.getTotal().getMaxLevel()));
            if (o instanceof SwordItemAbility){
                SwordItemAbility swordItemAbility = (SwordItemAbility) o;
                swordItemAbility.getAttack().upgrade();
                //todo 后续需要改为itemstack属性来增加伤害
                SwordItem sword = (SwordItem)itemStack.getItem();
                sword.attackDamage = sword.attackDamage + (int)swordItemAbility.getAttack().getValue();
                //重新更新提示信息
                AttributeModifier speedMod = sword.defaultModifiers.get(Attributes.ATTACK_SPEED).stream().findFirst().orElse(null);
                double speed = speedMod.getAmount();
                ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
                builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)sword.attackDamage, AttributeModifier.Operation.ADDITION));
                builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)speed, AttributeModifier.Operation.ADDITION));
                sword.defaultModifiers = builder.build();
            }

            //通知客户端更新
//            Networking.INSTANCE.send(PacketDistributor.ALL.noArg(), new UpgradeButtonPack(o.serializeNBT()));
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
