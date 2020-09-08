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
                    {"81fa29cd-6a02-4c93-be81-1559c89230d4", "dnger", "-3", "0"},
                    {"fa17e02f-dc69-43ca-b527-c1af36f3f68a", "slol", "0", "-3"},
                    {"0b4ed733-da68-4311-8696-8806d9f6623b", "XXFROSTUXX", "3", "0"},
                    {"4538d5ab-ff77-407c-9a1e-1b713ef99a0d", "zopac", "0", "3"},
                    {"78bd6a79-6582-4309-b0bf-3e19c7a781be", "freemanatee", "-6", "0"},
                    {"f83eff2a-4dfe-4a26-9753-abbb78bc5926", "CerealKiller202", "0", "-6"},
                    {"332220aa-f9a5-46f0-95fd-151f9d0f8d9c", "LosPalmeras_Z", "6", "0"},
                    {"0f75a81d-70e5-43c5-b892-f33c524284f2", "popbob", "0", "6"},
                    {"70ee432d-0a96-4137-a2c0-37cc9df67f03", "jared2013", "-9", "0"},
                    {"fdee323e-7f0c-4c15-8d1c-0f277442342a", "Fit", "0", "-9"},
                    {"e195d3d7-e6dc-456e-8393-e2f90816a1af", "Hausemaster", "9", "0"},
                    {"f6af9556-449a-4080-9c68-f0db29df1035", "TotemShulker", "0", "9"},
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
