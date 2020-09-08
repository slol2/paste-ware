package me.cereal.utility.module.modules.chat;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.cereal.utility.event.events.GuiScreenEvent;
import me.cereal.utility.module.Module;
import me.cereal.utility.setting.Setting;
import me.cereal.utility.setting.Settings;
import net.minecraft.client.gui.GuiGameOver;

@Module.Info(name = "Excuses", category = Module.Category.CHAT)
public class Excuses extends Module {

    private Setting<Boolean> respawn = register(Settings.b("Respawn", true));

    @EventHandler
    public Listener<GuiScreenEvent.Displayed> listener = new Listener<>(event -> {
        if (event.getScreen() instanceof GuiGameOver) {
            if (respawn.getValue()) {
                mc.player.respawnPlayer();
                mc.displayGuiScreen(null);
                mc.player.sendChatMessage("Damn I should be focusing on the game more instead of fucking your mom");
            }
        }
    });

}