package me.cereal.utility.module.modules.movement;

import me.cereal.utility.module.Module;

/**
 * Created by 086 on 23/08/2017.
 */
@Module.Info(name = "Sprint", description = "Automatically makes the player sprint", category = Module.Category.MOVEMENT)
public class Sprint extends Module {

    @Override
    public void onUpdate() {
        try {
            mc.player.setSprinting(!mc.player.collidedHorizontally && mc.player.moveForward > 0);
        } catch (Exception ignored) {
        }
    }

}
