package com.balsam.upgradetable.mixin;

import com.balsam.upgradetable.upgradeItem.attribute.AttackItemAttribute;
import com.balsam.upgradetable.upgradeItem.attribute.AttackSpeedItemAttribute;
import com.balsam.upgradetable.upgradeItem.attribute.DurabilityItemAttribute;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SwordItem.class)
public class MixSwordItem extends Item {

    @Shadow
    public float attackDamage;
    @Shadow
    public Multimap<Attribute, AttributeModifier> defaultModifiers;

    /**
     * 总计最大等级
     */
    private static final int TOTAL_MAX_LEVEL = 16;
    /**
     * 当前总计等级
     */
    private int totalLevel;

    /****属性及属性的等级***/
    private AttackItemAttribute attackAttri;
    private AttackSpeedItemAttribute attackSpeedAttri;
    private DurabilityItemAttribute durabilityAttri;

    public MixSwordItem(Properties properties) {
        super(properties);
        totalLevel = 1;
        attackAttri = new AttackItemAttribute();
        attackSpeedAttri = new AttackSpeedItemAttribute();
        durabilityAttri = new DurabilityItemAttribute();
    }

    public boolean upgrade(int type){
        switch (type){
            case 0:
                if (!attackAttri.upgrade())
                    return false;
            case 1:
                if (!attackSpeedAttri.upgrade())
                    return false;
            case 2:
                if (!durabilityAttri.upgrade())
                    return false;
        }
        rewriteTooltip();
        return true;
    }

    public void rewriteTooltip(){

    }


}
