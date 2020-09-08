package me.cereal.utility.module.modules.combat;

import me.cereal.utility.module.Module;

@Module.Info(name = "FastFall", category = Module.Category.COMBAT)
public class FastFall extends Module {
    public FastFall() {
    }

    public void onUpdate() {
        if (mc.player.onGround)
            --mc.player.motionY;
    }
}