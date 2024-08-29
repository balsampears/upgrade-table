package com.balsam.upgradetable.mixin;

import com.balsam.upgradetable.registry.AttributeRegistry;
import com.balsam.upgradetable.util.ItemStackCache;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = 1001)
public abstract class MixinLivingEntity extends Entity {
    public MixinLivingEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }


    /**
     * 给存活实体添加额外属性，避免报错
     */
    @Inject(at = @At("RETURN"), method = "createLivingAttributes()Lnet/minecraft/entity/ai/attributes/AttributeModifierMap$MutableAttribute;", cancellable = true)
    private static void createLivingAttributes(CallbackInfoReturnable<AttributeModifierMap.MutableAttribute> callback){
        AttributeModifierMap.MutableAttribute mutableAttribute = callback.getReturnValue();
        mutableAttribute.add(AttributeRegistry.AttackDamage.get())
                .add(AttributeRegistry.AttackSpeed.get());
        callback.setReturnValue(mutableAttribute);
    }


    /**
     * 当使用物品时，给玩家物品库存的所有物品设置当前玩家
     * @param hand
     * @param callback
     */
    @Inject(at = @At("RETURN"), method = "startUsingItem(Lnet/minecraft/util/Hand;)V")
    private void startUsingItem(Hand hand, CallbackInfo callback){
        if (!PlayerEntity.class.isAssignableFrom(this.getClass())) return;
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        ItemStackCache.setPlayerCache(playerEntity);
    }

    /**
     * 当结束使用时，设置当前玩家为空
     * @param callback
     */
    @Inject(at = @At("RETURN"), method = "completeUsingItem()V")
    private void completeUsingItem(CallbackInfo callback){
        if (!PlayerEntity.class.isAssignableFrom(this.getClass())) return;
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        ItemStackCache.clearPlayerCache(playerEntity);
    }
//
//    public static void main(String[] args) {
//        System.out.println(PlayerEntity.class.isAssignableFrom(MixinLivingEntity.class));
//    }
}
