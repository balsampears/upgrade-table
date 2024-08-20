package com.balsam.upgradetable.registry;

import com.balsam.upgradetable.config.Constants;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AttributeRegistry {

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Constants.MOD_ID);

    public static final RegistryObject<Attribute> AttackDamage = ATTRIBUTES.register("upgrade.attack_damage",
            ()->new RangedAttribute("attribute.name.upgrade.attack_damage", 0.0D, 0.0D, 9999));
    public static final RegistryObject<Attribute> AttackSpeed = ATTRIBUTES.register("upgrade.attack_speed",
            ()->(new RangedAttribute("attribute.name.upgrade.attack_speed", 0.0D, 0.0D, 9999)).setSyncable(true));
    public static final RegistryObject<Attribute> MaxDuration = ATTRIBUTES.register("upgrade.max_duration",
            ()->(new RangedAttribute("attribute.name.upgrade.max_duration", 0.0D, 0.0D, 9999)).setSyncable(true));

}
