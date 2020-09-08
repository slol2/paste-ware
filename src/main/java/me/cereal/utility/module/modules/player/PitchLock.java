package me.cereal.utility.module.modules.player;

import me.cereal.utility.module.Module;
import me.cereal.utility.setting.Setting;
import me.cereal.utility.setting.Settings;
import net.minecraft.util.math.MathHelper;

/**
 * Created by 086 on 16/12/2017.
 */
@Module.Info(name = "Pitch Lock", category = Module.Category.PLAYER)
public class PitchLock extends Module {
    private final Setting<Boolean> auto = register(Settings.b("Auto", true));
    private final Setting<Float> pitch = register(Settings.f("Pitch", 180));
    private final Setting<Integer> slice = register(Settings.i("Slice", 8));

    @Override
    public void onUpdate() {
        if (slice.getValue() == 0) return;
        if (auto.getValue()) {
            int angle = 360 / slice.getValue();
            float yaw = mc.player.rotationPitch;
            yaw = Math.round(yaw / angle) * angle;
            mc.player.rotationPitch = yaw;
            if (mc.player.isRiding()) mc.player.getRidingEntity().rotationPitch = yaw;
        } else {
            mc.player.rotationPitch = MathHelper.clamp(pitch.getValue() - 180, -180, 180);
        }
    }
}
