package com.balsam.upgradetable.cache;

import com.balsam.upgradetable.config.AttributeEnum;

import java.util.HashMap;
import java.util.Map;

public class CacheFactory {

    public static Map<AttributeEnum, ItemCache> Map = new HashMap<>();

    static {
        Map.put(AttributeEnum.AMMO_COST, new AmmoCostCache());
        Map.put(AttributeEnum.BOW_DAMAGE, new BowDamageCache());
        Map.put(AttributeEnum.THROW_DAMAGE, new ThrowDamageCache());
    }
}
