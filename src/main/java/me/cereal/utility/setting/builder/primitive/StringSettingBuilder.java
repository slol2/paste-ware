package me.cereal.utility.setting.builder.primitive;

import me.cereal.utility.setting.builder.SettingBuilder;
import me.cereal.utility.setting.impl.StringSetting;

/**
 * Created by 086 on 13/10/2018.
 */
public class StringSettingBuilder extends SettingBuilder<String> {
    @Override
    public StringSetting build() {
        return new StringSetting(initialValue, predicate(), consumer(), name, visibilityPredicate());
    }
}
