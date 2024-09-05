package com.balsam.upgradetable.config;

import java.util.UUID;

/**
 * 属性枚举
 */
public enum AttributeEnum {

    //空
    NULL("", "","FA233E1C-4180-4865-B01B-BCCE9785ACA1", Operation.NULL),
    //总等级
    Total("attribute.name.upgrade.total","total","FA233E1C-4180-4865-B01B-BCCE9785ACA0", Operation.NULL),
    //最大耐久
    MAX_DURATION("attribute.name.upgrade.max_duration","duration","FA233E1C-4180-4865-B01B-BCCE9785ACB0", Operation.ADDITION),
    //额外攻击伤害
    ATTACK_DAMAGE("attribute.name.upgrade.attack_damage","attack","FA233E1C-4180-4865-B01B-BCCE9785ACB1", Operation.ADDITION),
    //额外攻击速度
    ATTACK_SPEED("attribute.name.upgrade.attack_speed","attackSpeed","FA233E1C-4180-4865-B01B-BCCE9785ACB2", Operation.ADDITION),
    //额外弓箭伤害
    BOW_DAMAGE("attribute.name.upgrade.bow_damage","bowDamage","FA233E1C-4180-4865-B01B-BCCE9785ACB2", Operation.ADDITION),
    //拉弓时间降低
    BOW_ARC_TIME("attribute.name.upgrade.bow_arc_time","bowArcTime","FA233E1C-4180-4865-B01B-BCCE9785ACB2", Operation.MULTIPLY),
    //弹药消耗降低
    AMMO_COST("attribute.name.upgrade.ammo_cost","ammoCost","FA233E1C-4180-4865-B01B-BCCE9785ACB2", Operation.MULTIPLY),
    //额外投射伤害
    THROW_DAMAGE("attribute.name.upgrade.throw_damage","throwDamage","FA233E1C-4180-4865-B01B-BCCE9785ACB3", Operation.ADDITION),
    //冷却缩减
    USE_COOLDOWN("attribute.name.upgrade.use_cooldown","useCooldown","FA233E1C-4180-4865-B01B-BCCE9785ACB4", Operation.MULTIPLY),
    ;

    public enum Operation{
        ADDITION,    //加法计算
        MULTIPLY,    //乘法计算
        NULL
    }

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
    /**
     * 操作方式
     */
    private Operation operation;

    AttributeEnum(String name,String simpleName, String uuidString, Operation operation){
        this.name = name;
        this.simpleName = simpleName;
        this.uuid = UUID.fromString(uuidString);
        this.operation = operation;
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

    public Operation getOperation() {
        return operation;
    }
}
