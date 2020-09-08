package me.cereal.utility.module.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.cereal.utility.command.Command;
import me.cereal.utility.util.Friends;
import me.cereal.utility.setting.Setting;
import me.cereal.utility.setting.Settings;
import me.cereal.utility.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.util.text.TextComponentString;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

@Module.Info(name = "Anti32k", category = Module.Category.COMBAT)
public class Anti32k extends Module {

    public Setting LogOut;

    public void setup(){
        this.LogOut = this.register(Settings.b("Log", true));
    }


    private Set<EntityPlayer> sword = Collections.newSetFromMap(new WeakHashMap<>());

    private boolean is32k(EntityPlayer player, ItemStack stack) {
        if (stack.getItem() instanceof net.minecraft.item.ItemSword) {
            NBTTagList enchants = stack.getEnchantmentTagList();
            if (enchants != null)
                for (int i = 0; i < enchants.tagCount(); i++) {
                    if (enchants.getCompoundTagAt(i).getShort("lvl") >= Short.MAX_VALUE)
                        return true;
                }
        }
        return false ;
    }

    public void onUpdate() {
        for (EntityPlayer player : mc.world.playerEntities) {
            int once = 0;
            int Distanc = (int)mc.player.getDistance(player);
            if (player.equals(mc.player))
                continue;
            if (is32k(player, player.itemStackMainHand) && !this.sword.contains(player)) {
                Command.sendChatMessage(ChatFormatting.RED + player.getDisplayNameString() + " is holding a 32k");
                this.sword.add(player);
            }
            if (is32k(player, player.itemStackMainHand)) {
                if(once>0){return;}
                once++;
                if(LogOut.getValBoolean()){
                    if(!Friends.isFriend(player.getName())){
                        if(Distanc < 15){
                            Minecraft.getMinecraft().getConnection().handleDisconnect(new SPacketDisconnect(new TextComponentString(ChatFormatting.RED + "[32k Detect] Detected 32k near you")));
                        }
                    }
                }
            }
            if (!this.sword.contains(player))
                continue;
            if (is32k(player, player.itemStackMainHand))
                continue;
            Command.sendChatMessage(ChatFormatting.GREEN + player.getDisplayNameString() + " is no longer holding a 32k");
            this.sword.remove(player);
        }
    }

    public static final Minecraft mc = Minecraft.getMinecraft();
}
