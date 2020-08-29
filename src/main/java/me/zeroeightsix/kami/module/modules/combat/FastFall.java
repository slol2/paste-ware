package me.zeroeightsix.kami.module.modules.combat;

import me.zeroeightsix.kami.KamiMod;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;

@Module.Info(name = "FastFall", category = Module.Category.COMBAT, description = "Drops u into holes ;)")
public class FastFall extends Module {
    public FastFall() {
    }
    public void onUpdate() {
        if (mc.player.onGround)
            --mc.player.motionY;
    }
}