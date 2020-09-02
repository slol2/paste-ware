package me.cereal.utility.module.modules.player;

import me.cereal.utility.module.Module;

/**
 * @author 086
 */
@Module.Info(name = "FastPlace", category = Module.Category.PLAYER)
public class FastPlace extends Module {

    @Override
    public void onUpdate() {
        mc.rightClickDelayTimer = 0;
    }
}
