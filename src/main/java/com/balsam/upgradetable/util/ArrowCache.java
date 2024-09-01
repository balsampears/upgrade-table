package com.balsam.upgradetable.util;

import net.minecraft.entity.projectile.AbstractArrowEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ArrowCache {

    private static int DELAY_TIMES = 60 * 1000;

    private static Map<AbstractArrowEntity, Float> cache = new ConcurrentHashMap<>();
    private static Map<AbstractArrowEntity, Long> timeCache = new ConcurrentHashMap<>();

    public static void setValue(AbstractArrowEntity arrow, float moreDamage){
        cache.put(arrow, moreDamage);
        timeCache.put(arrow, System.currentTimeMillis());
        tryClear();
    }

    public static Float getValue(AbstractArrowEntity arrow){
        return cache.get(arrow);
    }

    public static void removeValue(AbstractArrowEntity arrow){
        cache.remove(arrow);
        timeCache.remove(arrow);
    }

    private static void tryClear(){
        for (Map.Entry<AbstractArrowEntity, Long> entry : timeCache.entrySet()) {
            if (entry.getValue() > System.currentTimeMillis() + DELAY_TIMES){
                removeValue(entry.getKey());
            }
        }
    }

}
