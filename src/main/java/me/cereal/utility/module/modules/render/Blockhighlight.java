package me.cereal.utility.module.modules.render;

import me.cereal.utility.event.events.RenderEvent;
import me.cereal.utility.module.Module;
import me.cereal.utility.setting.Setting;
import me.cereal.utility.setting.Settings;
import me.cereal.utility.util.CerealTessellator;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

@Module.Info(name = "Block Highlight", category = Module.Category.RENDER)

public class Blockhighlight extends Module {

    private final Setting<Float> width = this.register(Settings.floatBuilder("Width").withMinimum(0.0f).withValue(2.5f).build());
    private final Setting<Boolean> rainbow = this.register(Settings.b("RainbowMode", false));
    private final Setting<Integer> red = this.register(Settings.integerBuilder("Red").withRange(0, 255).withValue(255).withVisibility(o -> !rainbow.getValue()).build());
    private final Setting<Integer> green = this.register(Settings.integerBuilder("Green").withRange(0, 255).withValue(255).withVisibility(o -> !rainbow.getValue()).build());
    private final Setting<Integer> blue = this.register(Settings.integerBuilder("Blue").withRange(0, 255).withValue(255).withVisibility(o -> !rainbow.getValue()).build());
    private final Setting<Float> satuation = this.register(Settings.floatBuilder("Saturation").withRange(0.0f, 1.0f).withValue(0.6f).withVisibility(o -> rainbow.getValue()).build());
    private final Setting<Float> brightness = this.register(Settings.floatBuilder("Brightness").withRange(0.0f, 1.0f).withValue(0.6f).withVisibility(o -> rainbow.getValue()).build());
    private final Setting<Integer> speed = this.register(Settings.integerBuilder("Speed").withRange(0, 10).withValue(2).withVisibility(o -> rainbow.getValue()).build());
    private final Setting<Integer> alpha = this.register(Settings.integerBuilder("Transparency").withRange(0, 255).withValue(70).build());
    private final Setting<RenderMode> renderMode = this.register(Settings.e("Render Mode", RenderMode.SOLID));

    private BlockPos renderBlock;
    private float hue;
    private Color rgbc;

    public static Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) time);
    }

    @Override
    public void onWorldRender(RenderEvent event) {
        if (this.renderBlock != null && !(mc.world.getBlockState(this.renderBlock).getBlock() instanceof BlockAir) && !(mc.world.getBlockState(this.renderBlock).getBlock() instanceof BlockLiquid)) {
            if (rainbow.getValue()) {
                this.rgbc = Color.getHSBColor(this.hue, this.satuation.getValue(), this.brightness.getValue());
                this.drawBlock(this.renderBlock, rgbc.getRed(), rgbc.getGreen(), rgbc.getBlue());
                if (this.hue + ((float) speed.getValue() / 200) > 1) {
                    this.hue = 0;
                } else {
                    this.hue += ((float) speed.getValue() / 200);
                }
            } else {
                this.drawBlock(this.renderBlock, this.red.getValue(), this.green.getValue(), this.blue.getValue());
            }
        }
    }

    private void drawBlock(final BlockPos blockPos, final int r, final int g, final int b) {
        final Color color = new Color(r, g, b, this.alpha.getValue());
        CerealTessellator.prepare(7);
        if (this.renderMode.getValue().equals(RenderMode.UP)) {
            CerealTessellator.drawBox(blockPos, color.getRGB(), 2);
        } else if (this.renderMode.getValue().equals(RenderMode.SOLID)) {
            CerealTessellator.drawBox(blockPos, color.getRGB(), 63);
        } else if (this.renderMode.getValue().equals(RenderMode.OUTLINE)) {
            IBlockState iBlockState2 = mc.world.getBlockState(this.renderBlock);
            Vec3d interp2 = interpolateEntity(mc.player, mc.getRenderPartialTicks());
            CerealTessellator.drawBoundingBox(iBlockState2.getSelectedBoundingBox(mc.world, this.renderBlock).offset(-interp2.x, -interp2.y, -interp2.z), width.getValue(), r, g, b, this.alpha.getValue());
        } else {
            IBlockState iBlockState3 = mc.world.getBlockState(this.renderBlock);
            Vec3d interp3 = interpolateEntity(mc.player, mc.getRenderPartialTicks());
            CerealTessellator.drawFullBox(iBlockState3.getSelectedBoundingBox(mc.world, this.renderBlock).offset(-interp3.x, -interp3.y, -interp3.z), this.renderBlock, width.getValue(), r, g, b, this.alpha.getValue());
        }
        CerealTessellator.release();
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || this.isDisabled()) {
            return;
        }
        try {
            this.renderBlock = new BlockPos(mc.objectMouseOver.getBlockPos().getX(), mc.objectMouseOver.getBlockPos().getY(), (double) mc.objectMouseOver.getBlockPos().getZ());
        } catch (Exception e) {
            return;
        }
    }

    @Override
    public void onEnable() {
        this.hue = 0f;
    }

    @Override
    public void onDisable() {
        this.renderBlock = null;
    }

    private enum RenderMode {
        SOLID,
        OUTLINE,
        UP,
        FULL
    }
}