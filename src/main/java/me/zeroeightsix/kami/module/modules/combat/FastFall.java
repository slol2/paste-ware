package me.zeroeightsix.kami.module.modules.combat;

import me.zeroeightsix.kami.module.Module;

@Module.Info(name = "FastFall", category = Module.Category.COMBAT)
public class FastFall extends Module {
    public FastFall() {
    }
    public void onUpdate() {
        if (mc.player.onGround)
            --mc.player.motionY;
    }
}