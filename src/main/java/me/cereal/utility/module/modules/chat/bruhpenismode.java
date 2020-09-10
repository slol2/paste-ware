package me.cereal.utility.module.modules.chat;

import me.cereal.utility.module.Module;

@Module.Info(name = "enabl for 2b2t op", category = Module.Category.CHAT)
public class bruhpenismode extends Module {

    @Override
    protected void onEnable() {
        mc.player.sendChatMessage("i have a small penis");
        this.disable();
    }
}
