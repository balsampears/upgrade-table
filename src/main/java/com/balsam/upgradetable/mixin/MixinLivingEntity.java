package com.balsam.upgradetable.mixin;

import com.balsam.upgradetable.registry.AttributeRegistry;
import com.balsam.upgradetable.util.Logger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
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
}
