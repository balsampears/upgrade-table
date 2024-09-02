package com.balsam.upgradetable.mixin;

import com.balsam.upgradetable.cache.BowDamageCache;
import com.balsam.upgradetable.cache.CacheFactory;
import com.balsam.upgradetable.cache.ItemCache;
import com.balsam.upgradetable.cache.ThrowDamageCache;
import com.balsam.upgradetable.capability.itemAbility.BaseItemAbility;
import com.balsam.upgradetable.config.AttributeEnum;
import com.balsam.upgradetable.mod.ModCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(ProjectileEntity.class)
public class MixinProjectileEntity {

    @Inject(at=@At("RETURN"), method = "setOwner(Lnet/minecraft/entity/Entity;)V")
    public void setOwner(Entity entity, CallbackInfo callbackInfo){
        if (!(entity instanceof LivingEntity)) return;
        ProjectileEntity thisObj = (ProjectileEntity) (Object) this;
        LivingEntity livingEntity = (LivingEntity) entity;
        ItemStack useItem = livingEntity.getUseItem();
        useItem.getCapability(ModCapability.itemAbility).ifPresent(o->{
            BaseItemAbility baseItemAbility = (BaseItemAbility) o;
            //弓箭伤害
            baseItemAbility.findAttribute(AttributeEnum.BOW_DAMAGE).ifPresent(attr->{
                BowDamageCache itemCache = (BowDamageCache)CacheFactory.Map.get(AttributeEnum.BOW_DAMAGE);
                itemCache.setValue(thisObj, attr.getValue());
            });
            //投射伤害
            baseItemAbility.findAttribute(AttributeEnum.THROW_DAMAGE).ifPresent(attr->{
                ThrowDamageCache cache = (ThrowDamageCache) CacheFactory.Map.get(AttributeEnum.THROW_DAMAGE);
                cache.setValue(thisObj, attr.getValue());
            });
        });
    }

}
