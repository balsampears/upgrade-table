package com.balsam.upgradetable.mixin.target.common;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = ItemAttributeModifierEvent.class,remap = false)
public class MixinItemAttributeModifierEvent {
    @Shadow
    private Multimap<Attribute, AttributeModifier> originalModifiers;
    @Shadow
    private Multimap<Attribute, AttributeModifier> modifiableModifiers;

    /**
     * 修复原版没有按顺序设置属性bug
     */
    @Inject(method = "net.minecraftforge.event.ItemAttributeModifierEvent.getModifiableMap",
            at = @At(value = "INVOKE", shift = At.Shift.AFTER,
                    target = "Lcom/google/common/collect/HashMultimap;create(Lcom/google/common/collect/Multimap;)Lcom/google/common/collect/HashMultimap;"))
    private void getModifiableMap(CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> callback){
        this.modifiableModifiers = LinkedHashMultimap.create(this.originalModifiers);
    }

}
