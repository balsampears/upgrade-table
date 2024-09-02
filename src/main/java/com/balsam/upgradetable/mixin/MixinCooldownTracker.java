package com.balsam.upgradetable.mixin;

import com.balsam.upgradetable.capability.itemAbility.BaseItemAbility;
import com.balsam.upgradetable.capability.itemAbility.IItemAbility;
import com.balsam.upgradetable.capability.pojo.ItemAttributePO;
import com.balsam.upgradetable.config.AttributeEnum;
import com.balsam.upgradetable.mod.ModCapability;
import com.balsam.upgradetable.util.Logger;
import com.google.common.collect.Maps;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SCooldownPacket;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.ServerCooldownTracker;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.RandomUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;

@Mixin(CooldownTracker.class)
public abstract class MixinCooldownTracker{
    @Shadow
    private Map<Item, CooldownTracker.Cooldown> cooldowns;
    @Shadow
    private int tickCount;
    /**
     * 实现功能：冷却缩减
     */
    @Inject(at= @At("HEAD"), method = "addCooldown(Lnet/minecraft/item/Item;I)V", cancellable = true)
    public void addCooldown(Item item, int times, CallbackInfo callbackInfo) {
        if (!((Object)this instanceof ServerCooldownTracker)) return;
        ServerCooldownTracker thisObj = (ServerCooldownTracker) (Object) this;
        callbackInfo.cancel();

        ItemStack useItem = thisObj.player.getUseItem();
        int newTimes = times;
        if (useItem != ItemStack.EMPTY) {
            LazyOptional<IItemAbility> capability = useItem.getCapability(ModCapability.itemAbility);
            if (capability.isPresent()) {
                BaseItemAbility baseItemAbility = (BaseItemAbility) capability.orElse(null);
                Optional<ItemAttributePO> attributeOptional = baseItemAbility.findAttribute(AttributeEnum.USE_COOLDOWN);
                if (attributeOptional.isPresent()) {
                    ItemAttributePO attr = attributeOptional.orElse(null);
                    if (attr.getValue() > 0) {
                        //根据公式计算
                        float newTimesFloat = 20 * times / (20 + times * attr.getValue());
                        //小数位通过随机法判定是否缩减
                        newTimes = (int) newTimesFloat + (newTimesFloat % 1 < RandomUtils.nextFloat(0, 1) ? 1 : 0);
                        Logger.info(String.format("冷却缩减：%f, 延迟：%d -> %d", attr.getValue(), times, newTimes));
                    }
                }
            }
        }
        //原版方法
        this.cooldowns.put(item, thisObj.new Cooldown(this.tickCount, this.tickCount + newTimes));
        thisObj.onCooldownStarted(item, newTimes);
    }
}
