package me.cereal.utility.module.modules.misc;

import me.cereal.utility.command.Command;
import me.cereal.utility.module.Module;
import me.cereal.utility.module.ModuleManager;
import me.cereal.utility.setting.Setting;
import me.cereal.utility.setting.Settings;
import me.cereal.utility.util.BlockInteractionHelper;
import me.cereal.utility.util.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

@Module.Info(name = "AutoNomadHut", category = Module.Category.MISC)
public class AutoNomadHut extends Module {
    private final Vec3d[] surroundTargets;
    private final Setting<Boolean> toggleable;
    private final Setting<Boolean> spoofHotbar;
    private final Setting<Integer> blockPerTick;
    private BlockPos basePos;
    private int offsetStep;
    private int playerHotbarSlot;
    private int lastHotbarSlot;

    public AutoNomadHut() {
        this.surroundTargets = new Vec3d[]{new Vec3d(0.0, 0.0, 0.0), new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, 0.0, 1.0), new Vec3d(1.0, 0.0, -1.0), new Vec3d(-1.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, -1.0), new Vec3d(2.0, 0.0, 0.0), new Vec3d(2.0, 0.0, 1.0), new Vec3d(2.0, 0.0, -1.0), new Vec3d(-2.0, 0.0, 0.0), new Vec3d(-2.0, 0.0, 1.0), new Vec3d(-2.0, 0.0, -1.0), new Vec3d(0.0, 0.0, 2.0), new Vec3d(1.0, 0.0, 2.0), new Vec3d(-1.0, 0.0, 2.0), new Vec3d(0.0, 0.0, -2.0), new Vec3d(-1.0, 0.0, -2.0), new Vec3d(1.0, 0.0, -2.0), new Vec3d(2.0, 1.0, -1.0), new Vec3d(2.0, 1.0, 1.0), new Vec3d(-2.0, 1.0, 0.0), new Vec3d(-2.0, 1.0, 1.0), new Vec3d(-2.0, 1.0, -1.0), new Vec3d(0.0, 1.0, 2.0), new Vec3d(1.0, 1.0, 2.0), new Vec3d(-1.0, 1.0, 2.0), new Vec3d(0.0, 1.0, -2.0), new Vec3d(1.0, 1.0, -2.0), new Vec3d(-1.0, 1.0, -2.0), new Vec3d(2.0, 2.0, -1.0), new Vec3d(2.0, 2.0, 1.0), new Vec3d(-2.0, 2.0, 1.0), new Vec3d(-2.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 2.0), new Vec3d(-1.0, 2.0, 2.0), new Vec3d(1.0, 2.0, -2.0), new Vec3d(-1.0, 2.0, -2.0), new Vec3d(2.0, 3.0, 0.0), new Vec3d(2.0, 3.0, -1.0), new Vec3d(2.0, 3.0, 1.0), new Vec3d(-2.0, 3.0, 0.0), new Vec3d(-2.0, 3.0, 1.0), new Vec3d(-2.0, 3.0, -1.0), new Vec3d(0.0, 3.0, 2.0), new Vec3d(1.0, 3.0, 2.0), new Vec3d(-1.0, 3.0, 2.0), new Vec3d(0.0, 3.0, -2.0), new Vec3d(1.0, 3.0, -2.0), new Vec3d(-1.0, 3.0, -2.0), new Vec3d(0.0, 4.0, 0.0), new Vec3d(1.0, 4.0, 0.0), new Vec3d(-1.0, 4.0, 0.0), new Vec3d(0.0, 4.0, 1.0), new Vec3d(0.0, 4.0, -1.0), new Vec3d(1.0, 4.0, 1.0), new Vec3d(-1.0, 4.0, 1.0), new Vec3d(-1.0, 4.0, -1.0), new Vec3d(1.0, 4.0, -1.0), new Vec3d(2.0, 4.0, 0.0), new Vec3d(2.0, 4.0, 1.0), new Vec3d(2.0, 4.0, -1.0)};
        this.toggleable = this.register(Settings.b("Toggleable", true));
        this.spoofHotbar = this.register(Settings.b("Spoof Hotbar", false));
        this.blockPerTick = this.register(Settings.i("Blocks per Tick", 1));
        this.offsetStep = 0;
        this.playerHotbarSlot = -1;
        this.lastHotbarSlot = -1;
    }

    private static boolean canBeClicked(final BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }

    private static Block getBlock(final BlockPos pos) {
        return getState(pos).getBlock();
    }

    private static IBlockState getState(final BlockPos pos) {
        return Wrapper.getWorld().getBlockState(pos);
    }

    private static void faceVectorPacketInstant(final Vec3d vec) {
        final float[] rotations = getLegitRotations(vec);
        Wrapper.getPlayer().connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], rotations[1], Wrapper.getPlayer().onGround));
    }

    private static float[] getLegitRotations(final Vec3d vec) {
        final Vec3d eyesPos = getEyesPos();
        final double diffX = vec.x - eyesPos.x;
        final double diffY = vec.y - eyesPos.y;
        final double diffZ = vec.z - eyesPos.z;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{Wrapper.getPlayer().rotationYaw + MathHelper.wrapDegrees(yaw - Wrapper.getPlayer().rotationYaw), Wrapper.getPlayer().rotationPitch + MathHelper.wrapDegrees(pitch - Wrapper.getPlayer().rotationPitch)};
    }

    private static Vec3d getEyesPos() {
        return new Vec3d(Wrapper.getPlayer().posX, Wrapper.getPlayer().posY + Wrapper.getPlayer().getEyeHeight(), Wrapper.getPlayer().posZ);
    }

    @Override
    public void onUpdate() {
        if (this.isDisabled() || AutoNomadHut.mc.player == null || ModuleManager.isModuleEnabled("Freecam")) {
            return;
        }
        if (this.offsetStep == 0) {
            this.basePos = new BlockPos(AutoNomadHut.mc.player.getPositionVector()).down();
            this.playerHotbarSlot = Wrapper.getPlayer().inventory.currentItem;
            if (!this.spoofHotbar.getValue()) {
                this.lastHotbarSlot = AutoNomadHut.mc.player.inventory.currentItem;
            }
        }
        for (int i = 0; i < this.blockPerTick.getValue(); ++i) {
            if (this.offsetStep >= this.surroundTargets.length) {
                this.endLoop();
                return;
            }
            final Vec3d offset = this.surroundTargets[this.offsetStep];
            this.placeBlock(new BlockPos(this.basePos.add(offset.x, offset.y, offset.z)));
            ++this.offsetStep;
        }
    }

    @Override
    protected void onEnable() {
        if (AutoNomadHut.mc.player == null) {
            this.disable();
            return;
        }
        this.playerHotbarSlot = Wrapper.getPlayer().inventory.currentItem;
        this.lastHotbarSlot = -1;
    }

    @Override
    protected void onDisable() {
        if (AutoNomadHut.mc.player == null) {
            return;
        }
        if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
            if (this.spoofHotbar.getValue()) {
                AutoNomadHut.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.playerHotbarSlot));
            } else {
                Wrapper.getPlayer().inventory.currentItem = this.playerHotbarSlot;
            }
        }
        this.playerHotbarSlot = -1;
        this.lastHotbarSlot = -1;
    }

    private void endLoop() {
        this.offsetStep = 0;
        if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
            if (this.spoofHotbar.getValue()) {
                AutoNomadHut.mc.player.connection.sendPacket(new CPacketHeldItemChange(this.playerHotbarSlot));
            } else {
                Wrapper.getPlayer().inventory.currentItem = this.playerHotbarSlot;
            }
            this.lastHotbarSlot = this.playerHotbarSlot;
        }
        if (!this.toggleable.getValue()) {
            this.disable();
        }
    }

    private void placeBlock(final BlockPos blockPos) {
        if (!Wrapper.getWorld().getBlockState(blockPos).getMaterial().isReplaceable()) {
            return;
        }
        if (!BlockInteractionHelper.checkForNeighbours(blockPos)) {
            return;
        }
        this.placeBlockExecute(blockPos, true);
    }

    public int findObiInHotbar() {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = Wrapper.getPlayer().inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlock) {
                final Block block = ((ItemBlock) stack.getItem()).getBlock();
                if (block instanceof BlockObsidian) {
                    slot = i;
                    break;
                }
            }
        }
        return slot;
    }

    public void placeBlockExecute(final BlockPos pos, final boolean spoofRotation) {
        if (!mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
            Command.sendChatMessage("BLOCK WAS UNABLE TO BE PLACED :(");
            return;
        }
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbor = pos.offset(side);
            final EnumFacing side2 = side.getOpposite();
            if (mc.world.getBlockState(neighbor).getBlock().canCollideCheck(mc.world.getBlockState(neighbor), false)) {
                final Vec3d hitVec = new Vec3d(neighbor).add(0.5, 0.5, 0.5).add(new Vec3d(side2.getDirectionVec()).scale(0.5));
                if (spoofRotation) {
                    final Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
                    final double diffX = hitVec.x - eyesPos.x;
                    final double diffY = hitVec.y - eyesPos.y;
                    final double diffZ = hitVec.z - eyesPos.z;
                    final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
                    final float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
                    final float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));
                    mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw), mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch), mc.player.onGround));
                }
                final int obiSlot = this.findObiInHotbar();
                if (obiSlot == -1) {
                    this.disable();
                    return;
                }
                if (this.lastHotbarSlot != obiSlot) {
                    if (this.spoofHotbar.getValue()) {
                        AutoNomadHut.mc.player.connection.sendPacket(new CPacketHeldItemChange(obiSlot));
                    } else {
                        Wrapper.getPlayer().inventory.currentItem = obiSlot;
                    }
                    this.lastHotbarSlot = obiSlot;
                }
                mc.playerController.processRightClickBlock(mc.player, mc.world, neighbor, side2, hitVec, EnumHand.MAIN_HAND);
                mc.player.swingArm(EnumHand.MAIN_HAND);
                mc.rightClickDelayTimer = 4;
                return;
            }
        }
    }
}