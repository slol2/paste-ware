package me.cereal.utility.module.modules.chat;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.cereal.utility.event.events.PacketEvent;
import me.cereal.utility.module.Module;
import me.cereal.utility.setting.Setting;
import me.cereal.utility.setting.Settings;
import me.cereal.utility.util.ChatTextUtils;
import net.minecraft.network.play.client.CPacketChatMessage;

import java.util.Random;

/**
 * @author 086
 * @since 2018-8-4
 */
@Module.Info(name = "ChatSuffix", category = Module.Category.CHAT)
public class ChatSuffix extends Module {

    private Setting<Boolean> ignoreCommands = register(Settings.b("IgnoreCommands", true));
    private Setting<Boolean> ignoreSpecial = register(Settings.b("IgnoreSpecial", true));
    private Setting<SuffixMode> suffixMode = register(Settings.e("SuffixMode", SuffixMode.DEFAULT));

    @EventHandler
    public Listener<PacketEvent.Send> listener = new Listener<>(event -> {

        if (!(event.getPacket() instanceof CPacketChatMessage)) {
            return;
        }

        String message = ((CPacketChatMessage) event.getPacket()).getMessage();

        if (ignoreCommands.getValue() && message.startsWith("/")) {
            return;
        }

        if (ignoreSpecial.getValue() && (!Character.isLetter(message.charAt(0)) && !Character.isDigit(message.charAt(0)))) {
            if (!(message.charAt(0) == '>' || message.charAt(0) == '^')) {
                return;
            }
        }

        if (suffixMode.getValue().equals(SuffixMode.DEFAULT)) {
            message = ChatTextUtils.appendChatSuffix(message);
        }

        ((CPacketChatMessage) event.getPacket()).message = message.replaceAll(ChatTextUtils.SECTIONSIGN, "");

    });

    private enum SuffixMode {
        DEFAULT
    }

    private String getRandomStringFromArray(String[] array) {
        return array[new Random().nextInt(array.length)];
    }

    private static class NameParts {

        private static final String[] pre =
                {
                        "/u1D04/u1D07/u0280/u1D07/u1D00/u029F/20/u1D1C/u1D1B/u026A/u029F/u026A/u1D1B/u028F",
                };
    }

}
