package com.balsam.upgradetable.registry;

import com.balsam.upgradetable.config.AttributeEnum;
import com.balsam.upgradetable.config.Constants;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AttributeRegistry {

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Constants.MOD_ID);

    public static final RegistryObject<Attribute> MaxDuration = ATTRIBUTES.register("upgrade.max_duration",
            ()->(new RangedAttribute(AttributeEnum.MAX_DURATION.getName(), 0.0D, 0.0D, 9999)).setSyncable(true));
    public static final RegistryObject<Attribute> AttackDamage = ATTRIBUTES.register("upgrade.attack_damage",
            ()->new RangedAttribute(AttributeEnum.ATTACK_DAMAGE.getName(), 0.0D, 0.0D, 9999));
    public static final RegistryObject<Attribute> AttackSpeed = ATTRIBUTES.register("upgrade.attack_speed",
            ()->(new RangedAttribute(AttributeEnum.ATTACK_SPEED.getName(), 0.0D, 0.0D, 9999)).setSyncable(true));
    public static final RegistryObject<Attribute> BowPower = ATTRIBUTES.register("upgrade.bow_power",
            ()->(new RangedAttribute(AttributeEnum.BOW_POWER.getName(), 0.0D, 0.0D, 9999)).setSyncable(true));
    public static final RegistryObject<Attribute> BowArcTime = ATTRIBUTES.register("upgrade.bow_arc_time",
            ()->(new RangedAttribute(AttributeEnum.BOW_ARC_TIME.getName(), 0.0D, 0.0D, 1)).setSyncable(true));
    public static final RegistryObject<Attribute> AmmoCost = ATTRIBUTES.register("upgrade.ammo_cost",
            ()->(new RangedAttribute(AttributeEnum.AMMO_COST.getName(), 0.0D, 0.0D, 1)).setSyncable(true));

}
