package com.balsam.upgradetable.mixin.target.common;

import com.balsam.upgradetable.block.UpgradeTableTileEntity;
import com.balsam.upgradetable.capability.itemAbility.BaseItemAbility;
import com.balsam.upgradetable.capability.itemAbility.IItemAbility;
import com.balsam.upgradetable.capability.pojo.ItemAttributePO;
import com.balsam.upgradetable.config.AttributeEnum;
import com.balsam.upgradetable.mixin.interfaces.IItemStack;
import com.balsam.upgradetable.mod.ModCapability;
import com.balsam.upgradetable.util.ItemStackUtil;
import com.balsam.upgradetable.util.Logger;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.extensions.IForgeItemStack;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.RandomUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class MixinItemStack extends CapabilityProvider<ItemStack> implements IForgeItemStack, IItemStack {

    protected MixinItemStack(Class<ItemStack> baseClass) {
        super(baseClass);
    }

    protected MixinItemStack(Class<ItemStack> baseClass, boolean isLazy) {
        super(baseClass, isLazy);
    }

    @Shadow
    private CompoundNBT tag;

    @Shadow
    public abstract boolean hasTag();

    @Shadow
    public abstract Item getItem();

    /**
     * 使用中的玩家
     */
    private PlayerEntity usingPlayer;
    /**
     * 额外最大耐久度
     */
    private Integer additionalMaxDamage;

    @Override
    public void setUsingPlayer(PlayerEntity usingPlayer) {
        this.usingPlayer = usingPlayer;
    }

    @Override
    public PlayerEntity getUsingPlayer() {
        return usingPlayer;
    }

    @Override
    public Integer getAdditionalMaxDamage() {
        if (additionalMaxDamage == null && ModCapability.itemAbility!=null){
            ItemStack thisObj = (ItemStack) (Object) this;
            thisObj.getCapability(ModCapability.itemAbility).ifPresent(o->{
                additionalMaxDamage = UpgradeTableTileEntity.settingMaxDuration((BaseItemAbility) o);
            });
        }
        return additionalMaxDamage;
    }

    @Override
    public void setAdditionalMaxDamage(Integer additionalMaxDamage) {
        this.additionalMaxDamage = additionalMaxDamage;
    }

    /**
     * 实现功能：最大耐久
     */
    @Inject(at = @At("RETURN"), method = "getMaxDamage()I", cancellable = true)
    private void getMaxDamage(CallbackInfoReturnable<Integer> callback) {
        Integer additionalMaxDamage = getAdditionalMaxDamage();
        if (additionalMaxDamage !=null) {
            callback.setReturnValue(callback.getReturnValue() +additionalMaxDamage);
//            Logger.info("额外耐久：" + additionalMaxDamage);
        }
    }

    /**
     * 实现能力：消耗降低
     * 思路：当玩家使用物品时会将物品和玩家绑定，并添加在缓存ItemStackCache；
     * 如果在使用物品时，出现玩家库存减少，说明是弹药。检查使用中的物品的能力和消耗降低等级，使用随机数判断是否取消减少弹药的行为
     * 当玩家使用完物品，清空缓存
     */
    @Inject(at = @At("HEAD"), method = "shrink(I)V", cancellable = true)
    public void shrink(int amount, CallbackInfo callback) {
        ItemStack thisObj = (ItemStack) (Object) this;
        PlayerEntity player = this.getUsingPlayer();
        if (player == null) return;

        ItemStack useItem = ItemStackUtil.getUseItem(player);
        if (useItem == ItemStack.EMPTY) return;

        LazyOptional<IItemAbility> capability = useItem.getCapability(ModCapability.itemAbility);
        capability.ifPresent(iItemAbility -> {
            BaseItemAbility baseItemAbility = (BaseItemAbility) iItemAbility;
            for (ItemAttributePO itemAttributePO : baseItemAbility.getDisplayAttributes()) {
                if (itemAttributePO.getAttributeEnum() != AttributeEnum.AMMO_COST) continue;
                //服务端判断是否可以触发消耗降低
                if (player instanceof ServerPlayerEntity) {
                    float nextFloat = RandomUtils.nextFloat(0, 1);
//                    Logger.info(String.format("弹药消耗降低：%f/%f，判定结果：%b", nextFloat, itemAttributePO.getValue(), nextFloat < itemAttributePO.getValue()));
                    if (nextFloat < itemAttributePO.getValue())
                        callback.cancel();
                    //同步客户端
                    ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                    Integer index = getPlayerInventoryIndex(thisObj, serverPlayer);
                    serverPlayer.connection.send(new SSetSlotPacket(-2, index, thisObj));
                }
                //客户端等待更新
                else {
                    callback.cancel();
                }
            }
        });

    }

    private Integer getPlayerInventoryIndex(ItemStack thisObj, ServerPlayerEntity serverPlayer) {
        Integer index = null;
        for (int i = 0; i < serverPlayer.inventory.items.size(); i++) {
            if (thisObj.equals(serverPlayer.inventory.items.get(i)))
                index = i;
        }
        if (index != null) return index;
        for (int i = 0; i < serverPlayer.inventory.offhand.size(); i++) {
            if (thisObj.equals(serverPlayer.inventory.offhand.get(i)))
                index = i;
        }
        return index;
    }


}
