package com.balsam.upgradetable.cache;

import net.minecraft.entity.projectile.AbstractArrowEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BowDamageCache extends ItemCache<AbstractArrowEntity, Float> {

    public BowDamageCache(){
        super(true, 60 * 1000);
    }
}
