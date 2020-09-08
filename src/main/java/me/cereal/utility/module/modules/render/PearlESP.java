package me.cereal.utility.module.modules.render;

import me.cereal.utility.event.events.RenderEvent;
import me.cereal.utility.module.Module;
import me.cereal.utility.setting.Setting;
import me.cereal.utility.setting.Settings;
import me.cereal.utility.util.KamiTessellator;
import me.cereal.utility.util.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

@Module.Info(name = "PearlESP", category = Module.Category.RENDER)
public class PearlESP
        extends Module {

    private final Setting<Integer> r = this.register(Settings.integerBuilder("Red").withMinimum(0).withMaximum(255).withValue(0).build());
    private final Setting<Integer> g = this.register(Settings.integerBuilder("Green").withMinimum(0).withMaximum(255).withValue(255).build());
    private final Setting<Integer> b = this.register(Settings.integerBuilder("Blue").withMinimum(0).withMaximum(255).withValue(30).build());
    private final Setting<Integer> a = this.register(Settings.integerBuilder("Alpha").withMinimum(0).withMaximum(255).withValue(255).build());

    @Override
    public void onWorldRender(RenderEvent event) {
        for (Entity e : PearlESP.mc.world.loadedEntityList) {
            if (!(e instanceof EntityEnderPearl)) continue;
            Vec3d vec = MathUtil.interpolateEntity(e, event.getPartialTicks());
            double posX = vec.x - (double) PearlESP.mc.player.renderOffsetX;
            double posY = vec.y - (double) PearlESP.mc.player.renderOffsetY;
            double posZ = vec.z - (double) PearlESP.mc.player.renderOffsetZ;
            Color color = new Color(this.r.getValue(), this.g.getValue(), this.b.getValue(), this.a.getValue());
            KamiTessellator.prepare(7);
            KamiTessellator.drawBox((int) posX, (int) posY, (int) posZ, color.getRGB(), 63);
            KamiTessellator.release();
        }
    }
}

