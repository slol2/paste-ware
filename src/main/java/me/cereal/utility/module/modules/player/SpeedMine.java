package me.cereal.utility.module.modules.player;

import me.cereal.utility.module.Module;

/**
 * @author 086
 */
@Module.Info(name = "SpeedMine", category = Module.Category.PLAYER)
public class SpeedMine extends Module {

    @Override
    public void onUpdate() {
        if(mc.playerController.curBlockDamageMP >= 0)
            mc.playerController.curBlockDamageMP = 1;
    }
}
