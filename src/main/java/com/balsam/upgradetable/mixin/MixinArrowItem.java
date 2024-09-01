package com.balsam.upgradetable.mixin;

import com.balsam.upgradetable.cache.CacheFactory;
import com.balsam.upgradetable.cache.ItemCache;
import com.balsam.upgradetable.capability.itemAbility.BaseItemAbility;
import com.balsam.upgradetable.capability.itemAbility.IItemAbility;
import com.balsam.upgradetable.config.AttributeEnum;
import com.balsam.upgradetable.mod.ModCapability;
import com.balsam.upgradetable.cache.BowDamageCache;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArrowItem.class)
public class MixinArrowItem {

    /**
     * 实现能力：额外弓箭伤害
     */
    @Inject(at = @At("RETURN"), method = "createArrow(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/entity/projectile/AbstractArrowEntity;", cancellable = true)
    private void createArrow(World world, ItemStack itemStack, LivingEntity livingEntity, CallbackInfoReturnable<AbstractArrowEntity> callback) {
        ItemStack useItem = livingEntity.getUseItem();
        LazyOptional<IItemAbility> capability = useItem.getCapability(ModCapability.itemAbility);
        capability.ifPresent(o -> {
            BaseItemAbility baseItemAbility = (BaseItemAbility) o;
            baseItemAbility.findAttribute(AttributeEnum.BOW_DAMAGE).ifPresent(attr->{
                BowDamageCache itemCache = (BowDamageCache)CacheFactory.Map.get(AttributeEnum.BOW_DAMAGE);
                itemCache.setValue(callback.getReturnValue(), attr.getValue());
            });
        });
    }

//    /**
//     * 实现能力：弓额外力量
//     */
//    @Inject(at=@At("RETURN"), method = "createArrow(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/entity/projectile/AbstractArrowEntity;", cancellable = true)
//    private void createArrow(World world, ItemStack itemStack, LivingEntity livingEntity, CallbackInfoReturnable<AbstractArrowEntity> callback){
//        ItemStack useItem = livingEntity.getUseItem();
//        LazyOptional<IItemAbility> capability = useItem.getCapability(ModCapability.itemAbility);
//        capability.ifPresent(o->{
//            BaseItemAbility baseItemAbility = (BaseItemAbility) o;
//            for (ItemAttributePO displayAttribute : baseItemAbility.getDisplayAttributes()) {
//                if (displayAttribute.getAttributeEnum() != AttributeEnum.BOW_DAMAGE) continue;
//                AbstractArrowEntity arrowEntity = callback.getReturnValue();
//                //增加伤害
//                arrowEntity.setBaseDamage(arrowEntity.getBaseDamage() + displayAttribute.getValue());
//                callback.setReturnValue(arrowEntity);
//            }
//        });
//    }
}
