package me.cereal.utility.module.modules.misc;

import com.mojang.authlib.GameProfile;
import me.cereal.utility.module.Module;
import me.cereal.utility.setting.Setting;
import me.cereal.utility.setting.Settings;
import net.minecraft.client.entity.EntityOtherPlayerMP;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created 10 August 2019 by hub
 * Updated 8 December 2019 by hub
 */
@Module.Info(name = "FakePlayer", category = Module.Category.MISC)
public class FakePlayer extends Module {

    private Setting<SpawnMode> spawnMode = register(Settings.e("Spawn Mode", SpawnMode.SINGLE));

    private List<Integer> fakePlayerIdList = null;

    private enum SpawnMode {
        SINGLE, MULTI
    }

    private static final String[][] fakePlayerInfo =
            {
                    {"66666666-6666-6666-6666-666666666600", "dnger", "-3", "0"},
                    {"66666666-6666-6666-6666-666666666601", "slol", "0", "-3"},
                    {"66666666-6666-6666-6666-666666666602", "XXFROSTUXX", "3", "0"},
                    {"66666666-6666-6666-6666-666666666603", "zopac", "0", "3"},
                    {"66666666-6666-6666-6666-666666666604", "freemanatee", "-6", "0"},
                    {"66666666-6666-6666-6666-666666666605", "CerealKiller202", "0", "-6"},
                    {"66666666-6666-6666-6666-666666666606", "LosPalmeras_Z", "6", "0"},
                    {"66666666-6666-6666-6666-666666666607", "popbob", "0", "6"},
                    {"66666666-6666-6666-6666-666666666608", "jared2013", "-9", "0"},
                    {"66666666-6666-6666-6666-666666666609", "Fit", "0", "-9"},
                    {"66666666-6666-6666-6666-666666666610", "Hausemaster", "9", "0"},
                    {"66666666-6666-6666-6666-666666666611", "iTristan", "0", "9"},
                    {"66666666-6666-6666-6666-666666666612", "TotemShulker", "3", "3"},
            };

    @Override
    protected void onEnable() {

        if (mc.player == null || mc.world == null) {
            this.disable();
            return;
        }

        fakePlayerIdList = new ArrayList<>();

        int entityId = -101;

        for (String[] data : fakePlayerInfo) {

            if (spawnMode.getValue().equals(SpawnMode.SINGLE)) {
                addFakePlayer(data[0], data[1], entityId, 0, 0);
                break;
            } else {
                addFakePlayer(data[0], data[1], entityId, Integer.parseInt(data[2]), Integer.parseInt(data[3]));
            }

            entityId--;

        }

    }

    private void addFakePlayer(String uuid, String name, int entityId, int offsetX, int offsetZ) {

        EntityOtherPlayerMP fakePlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString(uuid), name));
        fakePlayer.copyLocationAndAnglesFrom(mc.player);
        fakePlayer.posX = fakePlayer.posX + offsetX;
        fakePlayer.posZ = fakePlayer.posZ + offsetZ;
        mc.world.addEntityToWorld(entityId, fakePlayer);
        fakePlayerIdList.add(entityId);

    }

    @Override
    public void onUpdate() {

        if (fakePlayerIdList == null || fakePlayerIdList.isEmpty() ) {
            this.disable();
        }

    }

    @Override
    protected void onDisable() {

        if (mc.player == null || mc.world == null) {
            return;
        }

        if (fakePlayerIdList != null) {
            for (int id : fakePlayerIdList) {
                mc.world.removeEntityFromWorld(id);
            }
        }

    }

}
