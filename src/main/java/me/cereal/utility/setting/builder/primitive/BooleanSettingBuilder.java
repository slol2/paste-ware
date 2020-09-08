package me.cereal.utility.setting.builder.primitive;

import me.cereal.utility.setting.impl.BooleanSetting;
import me.cereal.utility.setting.builder.SettingBuilder;

/**
 * Created by 086 on 13/10/2018.
 */
public class BooleanSettingBuilder extends SettingBuilder<Boolean> {
    @Override
    public BooleanSetting build() {
        return new BooleanSetting(initialValue, predicate(), consumer(), name, visibilityPredicate());
    }

    @Override
    public BooleanSettingBuilder withName(String name) {
        return (BooleanSettingBuilder) super.withName(name);
    }
}
