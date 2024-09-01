package com.balsam.upgradetable.capability.itemAbility;

import com.balsam.upgradetable.capability.pojo.ItemAttributePO;
import com.balsam.upgradetable.config.AttributeEnum;
import com.balsam.upgradetable.config.Constants;
import com.balsam.upgradetable.mod.Config;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.ArrayUtils;

public class ConfigItemAbility extends BaseItemAbility {

    public ConfigItemAbility(Config.ConfigItem configItem) {
        super(Constants.MAX_LEVEL_TOTAL);
        this.handleAttribute(configItem.name0, configItem.maxLevel0, configItem.values0, configItem.reduceDuration0);
        this.handleAttribute(configItem.name1, configItem.maxLevel1, configItem.values1, configItem.reduceDuration1);
        this.handleAttribute(configItem.name2, configItem.maxLevel2, configItem.values2, configItem.reduceDuration2);
        this.handleAttribute(configItem.name3, configItem.maxLevel3, configItem.values3, configItem.reduceDuration3);
    }

    private void handleAttribute(ForgeConfigSpec.ConfigValue<AttributeEnum> name, ForgeConfigSpec.IntValue maxLevel,
                                 ForgeConfigSpec.ConfigValue<String> values, ForgeConfigSpec.IntValue reduceDuration){
        if (name.get() == AttributeEnum.NULL) return;
        float[] valuesArr = ArrayUtils.toPrimitive(Config.stringToArray(values.get()));
        ItemAttributePO itemAttributePO = new ItemAttributePO(name.get(), maxLevel.get(), valuesArr, reduceDuration.get());
        this.displayAttributes.add(itemAttributePO);
    }


}
