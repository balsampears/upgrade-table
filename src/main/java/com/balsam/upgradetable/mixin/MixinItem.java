package com.balsam.upgradetable.mixin;

import com.balsam.upgradetable.mod.ModCapability;
import com.balsam.upgradetable.upgradeItem.attribute.AttackItemAttribute;
import com.balsam.upgradetable.upgradeItem.attribute.AttackSpeedItemAttribute;
import com.balsam.upgradetable.upgradeItem.attribute.DurabilityItemAttribute;
import com.balsam.upgradetable.util.Logger;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.CompoundNBT;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

//todo 未生效
@Mixin(Item.class)
public class MixinItem extends Item {
    public MixinItem(Properties properties) {
        super(properties);
    }

    @Inject(at = @At("RETURN"), method = "readShareTag()V")
    public void readShareTag(ItemStack stack, CompoundNBT nbt, CallbackInfo callbackInfo) {
        Logger.info("注入readShareTag:"+new Gson().toJson(nbt));
        stack.getCapability(ModCapability.Level).ifPresent(o->{
            o.deserializeNBT(nbt);
        });
    }

    @Inject(at = @At("RETURN"), method = "getShareTag()Lnet.minecraft.nbt.CompoundNBT", cancellable = true)
    public void getShareTag(ItemStack stack, CallbackInfoReturnable<CompoundNBT> callbackInfo) {
        Logger.info("注入getShareTag");
        stack.getCapability(ModCapability.Level).ifPresent(o->{
            CompoundNBT compoundNBT = o.serializeNBT();
            callbackInfo.setReturnValue(compoundNBT);
            Logger.info("注入getShareTag:"+new Gson().toJson(compoundNBT));
        });
    }

    //    /**
//     * 总计最大等级
//     */
//    private static final int TOTAL_MAX_LEVEL = 16;
//    /**
//     * 当前总计等级
//     */
//    private int totalLevel;
//
//    /****属性及属性的等级***/
//    private AttackItemAttribute attackAttri;
//    private AttackSpeedItemAttribute attackSpeedAttri;
//    private DurabilityItemAttribute durabilityAttri;

//    public MixItem(Properties properties) {
//        super(properties);
//        totalLevel = 1;
//        attackAttri = new AttackItemAttribute();
//        attackSpeedAttri = new AttackSpeedItemAttribute();
//        durabilityAttri = new DurabilityItemAttribute();
//    }
//
//    public boolean upgrade(int type){
//        switch (type){
//            case 0:
//                if (!attackAttri.upgrade())
//                    return false;
//            case 1:
//                if (!attackSpeedAttri.upgrade())
//                    return false;
//            case 2:
//                if (!durabilityAttri.upgrade())
//                    return false;
//        }
//        rewriteTooltip();
//        return true;
//    }
//
//    public void rewriteTooltip(){
//
//    }


}
