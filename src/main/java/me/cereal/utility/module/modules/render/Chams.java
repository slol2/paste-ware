package me.cereal.utility.module.modules.render;

import me.cereal.utility.module.Module;
import me.cereal.utility.setting.Setting;
import me.cereal.utility.setting.Settings;
import me.cereal.utility.util.EntityUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by 086 on 12/12/2017.
 */
@Module.Info(name = "Chams", category = Module.Category.RENDER, description = "See entities through walls")
public class Chams extends Module {

    private static final Setting<Boolean> players = Settings.b("Players", true);
    private static final Setting<Boolean> animals = Settings.b("Animals", false);
    private static final Setting<Boolean> mobs = Settings.b("Mobs", false);

    public Chams() {
        registerAll(players, animals, mobs);
    }

    public static boolean renderChams(Entity entity) {
        return (entity instanceof EntityPlayer ? players.getValue() : (EntityUtil.isPassive(entity) ? animals.getValue() : mobs.getValue()));
    }

}
