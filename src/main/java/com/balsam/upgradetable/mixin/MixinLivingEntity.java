package com.balsam.upgradetable.mixin;

import com.balsam.upgradetable.cache.AmmoCostCache;
import com.balsam.upgradetable.cache.CacheFactory;
import com.balsam.upgradetable.cache.ItemCache;
import com.balsam.upgradetable.capability.itemAbility.BaseItemAbility;
import com.balsam.upgradetable.capability.itemAbility.IItemAbility;
import com.balsam.upgradetable.config.AttributeEnum;
import com.balsam.upgradetable.mod.ModCapability;
import com.balsam.upgradetable.registry.AttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = 1001)
public abstract class MixinLivingEntity extends Entity {
    public MixinLivingEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public int useItemRemaining;

    @Shadow
    public abstract ItemStack getUseItem();

    /**
     * 实现能力：拉弓时间降低
     */
    @Inject(at = @At("HEAD"), method = "getUseItemRemainingTicks()I", cancellable = true)
    public void getUseItemRemainingTicks(CallbackInfoReturnable<Integer> callback) {
        ItemStack useItem = getUseItem();
        if (!(useItem.getItem() instanceof BowItem)) return;
        LazyOptional<IItemAbility> capability = useItem.getCapability(ModCapability.itemAbility);
        capability.ifPresent(o -> {
            BaseItemAbility baseItemAbility = (BaseItemAbility) o;
            baseItemAbility.findAttribute(AttributeEnum.BOW_ARC_TIME).ifPresent(attr -> {
                float multi = 1 + attr.getValue();
                int newTimeLeft = (int) ((1 - multi) * useItem.getUseDuration() + multi * useItemRemaining);
                callback.setReturnValue(newTimeLeft);
//                Logger.info(String.format("拉弓剩余时间：%d -> %d ", useItemRemaining, newTimeLeft));
            });
        });
    }

    /**
     * 给存活实体添加额外属性，避免报错
     */
    @Inject(at = @At("RETURN"), method = "createLivingAttributes()Lnet/minecraft/entity/ai/attributes/AttributeModifierMap$MutableAttribute;", cancellable = true)
    private static void createLivingAttributes(CallbackInfoReturnable<AttributeModifierMap.MutableAttribute> callback) {
        AttributeModifierMap.MutableAttribute mutableAttribute = callback.getReturnValue();
        mutableAttribute.add(AttributeRegistry.AttackDamage.get())
                .add(AttributeRegistry.AttackSpeed.get());
        callback.setReturnValue(mutableAttribute);
    }


    /**
     * 当使用物品时，给玩家物品库存的所有物品设置当前玩家
     */
    @Inject(at = @At("RETURN"), method = "startUsingItem(Lnet/minecraft/util/Hand;)V")
    private void startUsingItem(Hand hand, CallbackInfo callback) {
        if (!PlayerEntity.class.isAssignableFrom(this.getClass())) return;
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        AmmoCostCache cache = (AmmoCostCache) CacheFactory.Map.get(AttributeEnum.AMMO_COST);
        cache.setValue(ItemStack.EMPTY, playerEntity);
    }

    /**
     * 当结束使用时，设置当前玩家为空
     */
    @Inject(at = @At("RETURN"), method = "completeUsingItem()V")
    private void completeUsingItem(CallbackInfo callback) {
        if (!PlayerEntity.class.isAssignableFrom(this.getClass())) return;
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        AmmoCostCache cache = (AmmoCostCache) CacheFactory.Map.get(AttributeEnum.AMMO_COST);
        cache.removeValueByPlayer(playerEntity);
    }
}
