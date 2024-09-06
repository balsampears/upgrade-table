package com.balsam.upgradetable.mixin.target.common;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> p_i48577_1_, World p_i48577_2_) {
        super(p_i48577_1_, p_i48577_2_);
    }

//    /**
//     * 实现功能：额外攻击速度
//     */
//    @Inject(at=@At("HEAD"), method = "getCurrentItemAttackStrengthDelay()F", cancellable = true)
//    public void getCurrentItemAttackStrengthDelay(CallbackInfoReturnable<Float> callback) {
//        callback.cancel();
//        double damageSpeed = this.getAttributeValue(Attributes.ATTACK_SPEED) + this.getAttributeValue(AttributeRegistry.AttackSpeed.get());
//        callback.setReturnValue((float)(1.0D / damageSpeed * 20.0D));
//    }
}
