package com.balsam.upgradetable.mixin;

import com.balsam.upgradetable.capability.itemAbility.BaseItemAbility;
import com.balsam.upgradetable.capability.itemAbility.IItemAbility;
import com.balsam.upgradetable.capability.pojo.ItemAttributePO;
import com.balsam.upgradetable.config.AttributeEnum;
import com.balsam.upgradetable.mod.ModCapability;
import com.balsam.upgradetable.util.ItemStackUtil;
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
public abstract class MixinCooldownTracker {
    @Shadow
    private Map<Item, CooldownTracker.Cooldown> cooldowns;
    @Shadow
    private int tickCount;

    /**
     * 实现功能：冷却缩减
     */
    @Inject(at = @At("HEAD"), method = "addCooldown(Lnet/minecraft/item/Item;I)V", cancellable = true)
    public void addCooldown(Item item, int times, CallbackInfo callbackInfo) {
        if (!((Object) this instanceof ServerCooldownTracker)) return;
        ServerCooldownTracker thisObj = (ServerCooldownTracker) (Object) this;

        ItemStack useItem = ItemStackUtil.getUseItem(thisObj.player);
        if (useItem == ItemStack.EMPTY) return;
        useItem.getCapability(ModCapability.itemAbility).ifPresent(o->{
            ((BaseItemAbility)o).findAttribute(AttributeEnum.USE_COOLDOWN).ifPresent(attr->{
                if (attr.getValue() > 0) {
                    //根据加法公式计算，例如：攻速 5 + 1 -> 6
//                  float newTimesFloat = 20 * times / (20 + times * attr.getValue());
                    //根据乘法公式计算，例如：攻速 5 * (1+0.5) -> 7.5
                    float newTimesFloat = times / attr.getValue();
                    //小数位通过随机法判定是否缩减
                    int newTimes = (int) newTimesFloat + (newTimesFloat % 1 < RandomUtils.nextFloat(0, 1) ? 1 : 0);
//                  Logger.info(String.format("冷却缩减：%f, 延迟：%d -> %d", attr.getValue(), times, newTimes));
                    //原版方法
                    callbackInfo.cancel();
                    this.cooldowns.put(item, thisObj.new Cooldown(this.tickCount, this.tickCount + newTimes));
                    thisObj.onCooldownStarted(item, newTimes);
                }
            });
        });
    }
}
