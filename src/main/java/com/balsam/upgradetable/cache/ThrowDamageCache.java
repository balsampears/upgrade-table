package com.balsam.upgradetable.cache;

import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;

public class ThrowDamageCache extends ItemCache<ProjectileEntity, Float> {

    public ThrowDamageCache(){
        super(true,  60 * 1000);
    }
}
