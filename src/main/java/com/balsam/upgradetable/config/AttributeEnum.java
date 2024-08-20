package com.balsam.upgradetable.config;

import java.util.UUID;

/**
 * 属性枚举
 */
public enum AttributeEnum {
    Total("attribute.name.upgrade.total","total","FA233E1C-4180-4865-B01B-BCCE9785ACA0"),
    MAX_DURATION("attribute.name.upgrade.max_duration","duration","FA233E1C-4180-4865-B01B-BCCE9785ACB0"),
    ATTACK_DAMAGE("attribute.name.upgrade.attack_damage","attack","FA233E1C-4180-4865-B01B-BCCE9785ACB1"),
    ATTACK_SPEED("attribute.name.upgrade.attack_speed","attackSpeed","FA233E1C-4180-4865-B01B-BCCE9785ACB2"),
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
