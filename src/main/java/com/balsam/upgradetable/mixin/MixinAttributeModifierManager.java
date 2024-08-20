package com.balsam.upgradetable.mixin;

import com.balsam.upgradetable.registry.AttributeRegistry;
import com.balsam.upgradetable.util.Logger;
import net.minecraft.entity.ai.attributes.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(AttributeModifierManager.class)
public abstract class MixinAttributeModifierManager {

    @Shadow
    private Map<Attribute, ModifiableAttributeInstance> attributes;
    @Shadow
    private AttributeModifierMap supplier;

    /**
     * 将能力进行实现
     */
    @Inject(at = @At("RETURN"), method = "getValue(Lnet/minecraft/entity/ai/attributes/Attribute;)D", cancellable = true)
    private void getValue(Attribute attribute, CallbackInfoReturnable<Double> callback){
        if (attribute == Attributes.ATTACK_DAMAGE){
            ModifiableAttributeInstance instance = attributes.get(AttributeRegistry.AttackDamage.get());
            double value = instance != null ? instance.getValue() : supplier.getValue(AttributeRegistry.AttackDamage.get());
//            Logger.info(String.format("总计伤害：%f,额外伤害：%f",callback.getReturnValue()+value,value));
            callback.setReturnValue(callback.getReturnValue() + value);
        }
        if (attribute == Attributes.ATTACK_SPEED){
            ModifiableAttributeInstance instance = attributes.get(AttributeRegistry.AttackSpeed.get());
            double value = instance != null ? instance.getValue() : supplier.getValue(AttributeRegistry.AttackSpeed.get());
//            Logger.info(String.format("总计攻速：%f,额外攻速：%f",callback.getReturnValue()+value,value));
            callback.setReturnValue(callback.getReturnValue() + value);
        }
    }

}
