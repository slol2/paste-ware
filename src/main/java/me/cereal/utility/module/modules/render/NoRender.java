package me.cereal.utility.module.modules.render;

import me.cereal.utility.event.events.PacketEvent;
import me.cereal.utility.module.Module;
import me.cereal.utility.setting.Setting;
import me.cereal.utility.setting.Settings;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;

/**
 * Created by 086 on 4/02/2018.
 */
@Module.Info(name = "NoRender", category = Module.Category.RENDER, description = "Ignore entity spawn packets")
public class NoRender extends Module {

    private final Setting<Boolean> mob = register(Settings.b("Mob", false));
    private final Setting<Boolean> gentity = register(Settings.b("GEntity", false));
    private final Setting<Boolean> object = register(Settings.b("Object", false));
    private final Setting<Boolean> xp = register(Settings.b("XP", false));
    private final Setting<Boolean> paint = register(Settings.b("Paintings", false));
    @EventHandler
    public Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
        Packet packet = event.getPacket();
        if ((packet instanceof SPacketSpawnMob && mob.getValue()) ||
                (packet instanceof SPacketSpawnGlobalEntity && gentity.getValue()) ||
                (packet instanceof SPacketSpawnObject && object.getValue()) ||
                (packet instanceof SPacketSpawnExperienceOrb && xp.getValue()) ||
                (packet instanceof SPacketSpawnPainting && paint.getValue()))
            event.cancel();
    });
    private final Setting<Boolean> fire = register(Settings.b("Fire"));
    @EventHandler
    public Listener<RenderBlockOverlayEvent> blockOverlayEventListener = new Listener<>(event -> {
        if (fire.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE)
            event.setCanceled(true);
    });

}
