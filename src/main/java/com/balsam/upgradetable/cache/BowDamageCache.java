package com.balsam.upgradetable.cache;

import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BowDamageCache extends ItemCache<ProjectileEntity, Float> {

    public BowDamageCache(){
        super(true, 60 * 1000);
    }
}
