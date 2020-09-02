package me.cereal.utility.module.modules.player;

import me.cereal.utility.module.Module;

/**
 * @author 086
 */
@Module.Info(name = "Fastplace", category = Module.Category.PLAYER)
public class Fastplace extends Module {

    @Override
    public void onUpdate() {
        mc.rightClickDelayTimer = 0;
    }
}
