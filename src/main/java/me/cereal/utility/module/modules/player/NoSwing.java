package me.cereal.utility.module.modules.player;

import me.cereal.utility.event.events.PacketEvent;
import me.cereal.utility.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.CPacketAnimation;

/**
 * Created 13 August 2019 by hub
 * Updated 14 November 2019 by hub
 */
@Module.Info(name = "NoSwing", category = Module.Category.PLAYER, description = "Prevents arm swing animation server side")
public class NoSwing extends Module {

    @EventHandler
    public Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if (event.getPacket() instanceof CPacketAnimation) {
            event.cancel();
        }
    });

}
