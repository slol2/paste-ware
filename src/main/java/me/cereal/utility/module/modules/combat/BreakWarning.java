package me.cereal.utility.module.modules.combat;

import me.cereal.utility.command.Command;
import me.cereal.utility.event.events.PacketEvent;
import me.cereal.utility.module.Module;
import me.cereal.utility.setting.Setting;
import me.cereal.utility.setting.Settings;
import me.cereal.utility.util.Friends;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Module.Info(name = "BreakWarning", category = Module.Category.COMBAT)
public class BreakWarning extends Module {

    private final Setting<Integer> distanceToDetect = this.register(Settings.integerBuilder("Max Break Distance").withMinimum(1).withValue(2).withMaximum(5).build());
    private final Setting<Boolean> announce = this.register(Settings.b("Announce in chat", false));
    private final Setting<Integer> chatDelay = this.register(Settings.integerBuilder("Chat Delay").withMinimum(14).withValue(18).withMaximum(25).withVisibility(o -> announce.getValue()).build());

    private int delay;
    @EventHandler
    public Listener<PacketEvent.Receive> packetReceiveListener = new Listener<PacketEvent.Receive>(event -> {
        EntityPlayerSP player = mc.player;
        WorldClient world = mc.world;
        if (Objects.isNull(player) || Objects.isNull(world)) {
            return;
        }
        if (event.getPacket() instanceof SPacketBlockBreakAnim) {
            SPacketBlockBreakAnim packet = (SPacketBlockBreakAnim) event.getPacket();
            BlockPos pos = packet.getPosition();
            if (this.pastDistance(player, pos, this.distanceToDetect.getValue())) {
                sendChat();
            }
        }
    });

    private boolean pastDistance(EntityPlayer player, BlockPos pos, double dist) {
        return player.getDistanceSqToCenter(pos) <= Math.pow(dist, 2.0);
    }

    public void sendChat() {
        if (this.delay > this.chatDelay.getValue() && this.announce.getValue()) {
            this.delay = 0;
            mc.player.connection.sendPacket(new CPacketChatMessage("Hello there, " + getPlayer() + " please stop breaking that :)"));
        }
        Command.sendChatMessage("\u00A74Your obsidian is being broken yikes");
        this.delay++;
    }

    public String getPlayer() {
        List<EntityPlayer> entities = new ArrayList<EntityPlayer>();
        entities.addAll(mc.world.playerEntities.stream().filter(entityPlayer -> !Friends.isFriend(entityPlayer.getName())).collect(Collectors.toList()));
        for (EntityPlayer e : entities) {
            if (e.isDead || e.getHealth() <= 0.0f) continue;
            if (e.getName() == mc.player.getName()) continue;
            if (e.getHeldItemMainhand().getItem() instanceof ItemTool) {
                return e.getName();
            }
        }
        return "";
    }
}