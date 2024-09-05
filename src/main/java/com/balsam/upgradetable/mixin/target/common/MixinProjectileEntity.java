package com.balsam.upgradetable.mixin.target.common;

import com.balsam.upgradetable.capability.itemAbility.BaseItemAbility;
import com.balsam.upgradetable.config.AttributeEnum;
import com.balsam.upgradetable.mixin.interfaces.IExtraDamage;
import com.balsam.upgradetable.mod.ModCapability;
import com.balsam.upgradetable.util.ItemStackUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileEntity.class)
public class MixinProjectileEntity implements IExtraDamage {

    private Float extraDamage;

    /**
     * 实现功能：额外弓箭伤害、额外投射伤害
     */
    @Inject(at=@At("RETURN"), method = "setOwner(Lnet/minecraft/entity/Entity;)V")
    public void setOwner(Entity entity, CallbackInfo callbackInfo){
        if (!(entity instanceof LivingEntity)) return;
        ProjectileEntity thisObj = (ProjectileEntity) (Object) this;
        LivingEntity livingEntity = (LivingEntity) entity;
        ItemStack useItem = ItemStackUtil.getUseItem(livingEntity);
        useItem.getCapability(ModCapability.itemAbility).ifPresent(o->{
            BaseItemAbility baseItemAbility = (BaseItemAbility) o;
            //弓箭伤害
            baseItemAbility.findAttribute(AttributeEnum.BOW_DAMAGE).ifPresent(attr->{
                extraDamage = attr.getValue();
            });
            //投射伤害
            baseItemAbility.findAttribute(AttributeEnum.THROW_DAMAGE).ifPresent(attr->{
                extraDamage = attr.getValue();
            });
        });
    }

    @Override
    public Float getExtraDamage() {
        return this.extraDamage;
    }
}
