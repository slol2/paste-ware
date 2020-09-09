package me.cereal.utility.module.modules.render;

import me.cereal.utility.module.Module;
import me.cereal.utility.setting.Setting;
import me.cereal.utility.setting.Settings;

@Module.Info(name = "Custom FOV", category = Module.Category.RENDER)
public class FOV extends Module {

    private final Setting<Float> FOV = this.register(Settings.floatBuilder("FOV").withMinimum(90.0f).withValue(110.0f).withMaximum(200.0f).build());
    private float fov;

    @Override
    protected void onEnable() {
        this.fov = mc.gameSettings.fovSetting;
    }

    @Override
    protected void onDisable() {
        mc.gameSettings.fovSetting = this.fov;
    }

    @Override
    public void onUpdate() {
        if (this.isDisabled() || mc.world == null) {
            return;
        }
        mc.gameSettings.fovSetting = this.FOV.getValue();
    }

}