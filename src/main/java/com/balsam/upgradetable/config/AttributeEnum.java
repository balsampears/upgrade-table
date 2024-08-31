package com.balsam.upgradetable.config;

import java.util.UUID;

/**
 * 属性枚举
 */
public enum AttributeEnum {
    //总等级
    Total("attribute.name.upgrade.total","total","FA233E1C-4180-4865-B01B-BCCE9785ACA0"),
    //最大耐久
    MAX_DURATION("attribute.name.upgrade.max_duration","duration","FA233E1C-4180-4865-B01B-BCCE9785ACB0"),
    //攻击伤害
    ATTACK_DAMAGE("attribute.name.upgrade.attack_damage","attack","FA233E1C-4180-4865-B01B-BCCE9785ACB1"),
    //攻击速度
    ATTACK_SPEED("attribute.name.upgrade.attack_speed","attackSpeed","FA233E1C-4180-4865-B01B-BCCE9785ACB2"),
    //额外弓箭伤害
    BOW_DAMAGE("attribute.name.upgrade.bow_damage","bowDamage","FA233E1C-4180-4865-B01B-BCCE9785ACB2"),
    //拉弓时间降低
    BOW_ARC_TIME("attribute.name.upgrade.bow_arc_time","bowArcTime","FA233E1C-4180-4865-B01B-BCCE9785ACB2"),
    //弹药消耗降低
    AMMO_COST("attribute.name.upgrade.ammo_cost","ammoCost","FA233E1C-4180-4865-B01B-BCCE9785ACB2"),
    ;

    /**
     * 属性名，用于翻译
     */
    private String name;
    /**
     * 属性简名，用于nbt标签
     */
    private String simpleName;
    /**
     * 唯一标识
     */
    private UUID uuid;

    AttributeEnum(String name,String simpleName, String uuidString){
        this.name = name;
        this.simpleName = simpleName;
        this.uuid = UUID.fromString(uuidString);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getSimpleName() {
        return simpleName;
    }
}
