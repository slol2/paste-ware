package me.cereal.utility.module.modules.movement;

import me.cereal.utility.module.Module;
import me.cereal.utility.module.ModuleManager;
import me.cereal.utility.module.modules.render.Pathfind;
import me.cereal.utility.setting.Setting;
import me.cereal.utility.setting.Settings;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.pathfinding.PathPoint;
import net.minecraftforge.client.event.InputUpdateEvent;

import static me.cereal.utility.util.EntityUtil.calculateLookAt;

/**
 * Created by 086 on 16/12/2017.
 */
@Module.Info(name = "AutoWalk", category = Module.Category.MOVEMENT)
public class AutoWalk extends Module {

    private final Setting<AutoWalkMode> mode = register(Settings.e("Mode", AutoWalkMode.FORWARD));

    @EventHandler
    private final Listener<InputUpdateEvent> inputUpdateEventListener = new Listener<>(event -> {
        switch (mode.getValue()) {
            case FORWARD:
                event.getMovementInput().moveForward = 1;
                break;
            case BACKWARDS:
                event.getMovementInput().moveForward = -1;
                break;
            case PATH:
                if (Pathfind.points.isEmpty()) return;
                event.getMovementInput().moveForward = 1;
                if (mc.player.isInWater() || mc.player.isInLava()) mc.player.movementInput.jump = true;
                else if (mc.player.collidedHorizontally && mc.player.onGround) mc.player.jump();
                if (!ModuleManager.isModuleEnabled("Pathfind") || Pathfind.points.isEmpty()) return;
                PathPoint next = Pathfind.points.get(0);
                lookAt(next);
                break;
        }
    });

    private void lookAt(PathPoint pathPoint) {
        double[] v = calculateLookAt(pathPoint.x + .5f, pathPoint.y, pathPoint.z + .5f, mc.player);
        mc.player.rotationYaw = (float) v[0];
        mc.player.rotationPitch = (float) v[1];
    }

    private enum AutoWalkMode {
        FORWARD, BACKWARDS, PATH
    }
}
