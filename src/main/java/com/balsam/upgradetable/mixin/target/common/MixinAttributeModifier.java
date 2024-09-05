package com.balsam.upgradetable.mixin.target.common;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(AttributeModifier.class)
public class MixinAttributeModifier {

    @Shadow
    public UUID id;

    /**
     * 修复问题：本mod数据会经过nbt标签的保存和解析，会生成一样的id，但不一样的对象
     */
    @Inject(method = "getId()Ljava/util/UUID;", at=@At("HEAD"))
    public void getId(CallbackInfoReturnable<UUID> callbackInfoReturnable){
        this.checkAndSetId(Item.BASE_ATTACK_DAMAGE_UUID);
        this.checkAndSetId(Item.BASE_ATTACK_SPEED_UUID);
    }
    
    private void checkAndSetId(UUID uuid){
        if (id != uuid && id.toString().equals(uuid.toString())){
            id = uuid;
        }
    }
}
