package me.cereal.utility.module.modules.misc;

import me.cereal.utility.event.events.PacketEvent;
import me.cereal.utility.module.Module;
import me.cereal.utility.setting.Setting;
import me.cereal.utility.setting.Settings;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.CPacketChatMessage;

/**
 * Created by 086 on 8/04/2018.
 */
@Module.Info(name = "CustomChat", category = Module.Category.MISC, description = "Modifies your chat messages")
public class CustomChat extends Module {

    private final String KAMI_SUFFIX = " \u23D0 \u1D0B\u1D00\u1D0D\u026A";
    private final Setting<Boolean> commands = register(Settings.b("Commands", false));
    @EventHandler
    public Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketChatMessage) {
            String s = ((CPacketChatMessage) event.getPacket()).getMessage();
            if (s.startsWith("/") && !commands.getValue()) return;
            s += KAMI_SUFFIX;
            if (s.length() >= 256) s = s.substring(0, 256);
            ((CPacketChatMessage) event.getPacket()).message = s;
        }
    });

}
