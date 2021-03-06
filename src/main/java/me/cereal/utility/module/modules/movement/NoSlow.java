package me.cereal.utility.module.modules.movement;

import me.cereal.utility.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraftforge.client.event.InputUpdateEvent;

/**
 * Created by 086 on 15/12/2017.
 */
@Module.Info(name = "NoSlow", category = Module.Category.MOVEMENT)
public class NoSlow extends Module {

    @EventHandler
    private final Listener<InputUpdateEvent> eventListener = new Listener<>(event -> {
        //
        // InputUpdateEvent is called just before the player is slowed down @see EntityPlayerSP.onLivingUpdate)
        // We'll abuse this fact, and multiply moveStrafe and moveForward by 5 to nullify the *0.2f hardcoded by mojang.
        //

        // Check if the player should be slowed down or not
        if (mc.player.isHandActive() && !mc.player.isRiding()) {
            event.getMovementInput().moveStrafe *= 5;
            event.getMovementInput().moveForward *= 5;
        }
    });

    // Check MixinBlockSoulSand for soulsand slowdown nullification

}
