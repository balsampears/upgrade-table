package com.balsam.upgradetable.mod;

import com.balsam.upgradetable.config.AttributeEnum;
import com.balsam.upgradetable.config.Constants;
import com.balsam.upgradetable.util.Logger;
import com.google.gson.Gson;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModConfig {


    public static ForgeConfigSpec COMMON_CONFIG;

    private static Gson gson;
    private static List<ConfigItem> configItems;
    private static Map<Class<?>, ConfigItem> ConfigMap;

    public static class ConfigItem{
        public ForgeConfigSpec.ConfigValue<String> targetClass;

        public ForgeConfigSpec.EnumValue<AttributeEnum> name0;
        public ForgeConfigSpec.IntValue maxLevel0;
        public ForgeConfigSpec.ConfigValue<String> values0;
        public ForgeConfigSpec.EnumValue<AttributeEnum> name1;
        public ForgeConfigSpec.IntValue reduceDuration0;

        public ForgeConfigSpec.IntValue maxLevel1;
        public ForgeConfigSpec.ConfigValue<String> values1;
        public ForgeConfigSpec.EnumValue<AttributeEnum> name2;
        public ForgeConfigSpec.IntValue reduceDuration1;

        public ForgeConfigSpec.IntValue maxLevel2;
        public ForgeConfigSpec.ConfigValue<String> values2;
        public ForgeConfigSpec.EnumValue<AttributeEnum> name3;
        public ForgeConfigSpec.IntValue reduceDuration2;

        public ForgeConfigSpec.IntValue maxLevel3;
        public ForgeConfigSpec.ConfigValue<String> values3;
        public ForgeConfigSpec.IntValue reduceDuration3;
    }

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        configItems = new ArrayList<>();
        for (int i=0;i<20;i++){
            builder.push(i+"");
            ConfigItem configItem = new ConfigItem();
            if (i==0) {
                configItem.targetClass = builder.define("targetClass", "net.minecraft.item.TieredItem");
                configItem.name0 = builder.defineEnum("name0", AttributeEnum.ATTACK_DAMAGE);
                configItem.maxLevel0 = builder.defineInRange("maxLevel0", Constants.MAX_LEVEL_PER_ABILITY,0,99);
                configItem.values0 = builder.define("values0", arrayToString(new Float[]{0.5f, 1.4f, 2.5f, 4.0f, 5.8f, 8f}));
                configItem.reduceDuration0 = builder.defineInRange("reduceDuration0", 50,0,Integer.MAX_VALUE);

                configItem.name1 = builder.defineEnum("name1", AttributeEnum.ATTACK_SPEED);
                configItem.maxLevel1 = builder.defineInRange("maxLevel1", Constants.MAX_LEVEL_PER_ABILITY,0,99);
                configItem.values1 = builder.define("values1", arrayToString(new Float[]{0.07f, 0.17f, 0.32f, 0.51f, 0.73f, 1f}));
                configItem.reduceDuration1 = builder.defineInRange("reduceDuration1", 50,0,Integer.MAX_VALUE);

                configItem.name2 = builder.defineEnum("name2", AttributeEnum.MAX_DURATION);
                configItem.maxLevel2 = builder.defineInRange("maxLevel2", Constants.MAX_LEVEL_PER_ABILITY,0,99);
                configItem.values2 = builder.define("values2", arrayToString(new Integer[]{100,200,300,400,500,600}));
                configItem.reduceDuration2 = builder.defineInRange("reduceDuration2", 0,0,Integer.MAX_VALUE);

                configItem.name3 = builder.defineEnum("name3", AttributeEnum.NULL);
                configItem.maxLevel3 = builder.defineInRange("maxLevel3", 0,0,99);
                configItem.values3 = builder.define("values3", "");
                configItem.reduceDuration3 = builder.defineInRange("reduceDuration3", 0,0,Integer.MAX_VALUE);
            }
            else if (i==1) {
                configItem.targetClass = builder.define("targetClass", "net.minecraft.item.BowItem");
                configItem.name0 = builder.defineEnum("name0", AttributeEnum.BOW_DAMAGE);
                configItem.maxLevel0 = builder.defineInRange("maxLevel0", Constants.MAX_LEVEL_PER_ABILITY,0,99);
                configItem.values0 = builder.define("values0", arrayToString(new Float[]{0.5f, 1.5f, 3f, 5f, 7f, 10f}));
                configItem.reduceDuration0 = builder.defineInRange("reduceDuration0", 50,0,Integer.MAX_VALUE);

                configItem.name1 = builder.defineEnum("name1", AttributeEnum.BOW_ARC_TIME);
                configItem.maxLevel1 = builder.defineInRange("maxLevel1", Constants.MAX_LEVEL_PER_ABILITY,0,99);
                configItem.values1 = builder.define("values1", arrayToString(new Float[]{0.05f, 0.14f, 0.25f, 0.4f, 0.58f, 0.8f}));
                configItem.reduceDuration1 = builder.defineInRange("reduceDuration1", 50,0,Integer.MAX_VALUE);

                configItem.name2 = builder.defineEnum("name2", AttributeEnum.AMMO_COST);
                configItem.maxLevel2 = builder.defineInRange("maxLevel2", Constants.MAX_LEVEL_PER_ABILITY,0,99);
                configItem.values2 = builder.define("values2", arrayToString(new Float[]{0.02f, 0.05f, 0.1f, 0.15f, 0.5f, 0.8f}));
                configItem.reduceDuration2 = builder.defineInRange("reduceDuration2", 50,0,Integer.MAX_VALUE);

                configItem.name3 = builder.defineEnum("name3", AttributeEnum.MAX_DURATION);
                configItem.maxLevel3 = builder.defineInRange("maxLevel3", Constants.MAX_LEVEL_PER_ABILITY,0,99);
                configItem.values3 = builder.define("values3", arrayToString(new Integer[]{100,200,300,400,500,600}));
                configItem.reduceDuration3 = builder.defineInRange("reduceDuration3", 0,0,Integer.MAX_VALUE);
            }
            else {
                configItem.targetClass = builder.define("targetClass", "");
                configItem.name0 = builder.defineEnum("name0", AttributeEnum.NULL);
                configItem.maxLevel0 = builder.defineInRange("maxLevel0", 0,0,99);
                configItem.values0 = builder.define("values0", "");
                configItem.reduceDuration0 = builder.defineInRange("reduceDuration0", 50,0,Integer.MAX_VALUE);

                configItem.name1 = builder.defineEnum("name1", AttributeEnum.NULL);
                configItem.maxLevel1 = builder.defineInRange("maxLevel1", 0,0,99);
                configItem.values1 = builder.define("values1", "");
                configItem.reduceDuration1 = builder.defineInRange("reduceDuration1", 50,0,Integer.MAX_VALUE);

                configItem.name2 = builder.defineEnum("name2", AttributeEnum.NULL);
                configItem.maxLevel2 = builder.defineInRange("maxLevel2", 0,0,99);
                configItem.values2 = builder.define("values2", "");
                configItem.reduceDuration2 = builder.defineInRange("reduceDuration2", 50,0,Integer.MAX_VALUE);

                configItem.name3 = builder.defineEnum("name3", AttributeEnum.NULL);
                configItem.maxLevel3 = builder.defineInRange("maxLevel3", 0,0,99);
                configItem.values3 = builder.define("values3", "");
                configItem.reduceDuration3 = builder.defineInRange("reduceDuration3", 50,0,Integer.MAX_VALUE);
            }
            configItems.add(configItem);
            builder.pop();
        }
        COMMON_CONFIG = builder.build();
    }

    /**
     * 数组转为字符串
     */
    public static <T> String arrayToString(T[] array){
        if (array == null) return "";
        return Arrays.stream(array).map(Object::toString).collect(Collectors.joining(","));
    }

    /**
     * 字符串转为数组
     */
    public static Float[] stringToArray(String string){
        String[] split = string.split(",");
        return Arrays.stream(split).map(Float::valueOf).toArray(Float[]::new);
    }

    public static Map<Class<?>, ConfigItem> getConfigMap(){
        if (ConfigMap !=null) return ConfigMap;
        ConfigMap = configItems.stream().collect(Collectors.toMap(o -> {
            try {
                return Class.forName(o.targetClass.get());
            } catch (ClassNotFoundException e) {
                Logger.info(e.getMessage());
                return null;
            }
        }, Function.identity(), (o1,o2)-> o1, LinkedHashMap::new));
        return ConfigMap;
    }

}
