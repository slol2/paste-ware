package me.zeroeightsix.kami.module.modules.misc;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.command.Command;

/**
 * Coded by dnger
 */

@Module.Info(name = "AutoLeafDupe", category = Module.Category.MISC)
public class AutoLeafDupe extends Module {

    private Setting<Boolean> info = register(Settings.b("2b2t New Bypass", true));

    @Override
    public void onEnable() {
        if (info.getValue()) {
            mc.player.sendChatMessage("/kill");
        }
    }
}

