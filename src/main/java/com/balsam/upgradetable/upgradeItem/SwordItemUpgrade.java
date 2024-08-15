package com.balsam.upgradetable.upgradeItem;

import com.balsam.upgradetable.upgradeItem.attribute.AttackItemAttribute;
import com.balsam.upgradetable.upgradeItem.attribute.AttackSpeedItemAttribute;
import com.balsam.upgradetable.upgradeItem.attribute.DurabilityItemAttribute;
import net.minecraft.item.Item;

public class SwordItemUpgrade {

    /**
     * 总计最大等级
     */
    public static final int TOTAL_MAX_LEVEL = 16;
    /**
     * 当前总计等级
     */
    private int totalLevel;

    /****属性及属性的等级***/
    private AttackItemAttribute attackAttri;
    private AttackSpeedItemAttribute attackSpeedAttri;
    private DurabilityItemAttribute durabilityAttri;

    public SwordItemUpgrade() {
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
