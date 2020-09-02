package me.zeroeightsix.kami.module.modules.combat;

import java.math.RoundingMode;
import java.text.DecimalFormat;


import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.BlockInteractionHelper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.init.Items;
import net.minecraft.item.*;

@Module.Info(name="BedBomb", category=Module.Category.COMBAT)
public class BedBomb extends Module {

    private static final DecimalFormat df = new DecimalFormat("#.#");
    private Setting<Boolean> rotate = this.register(Settings.b("Rotate", false));
    private Setting<Boolean> debugMessages = this.register(Settings.b("Debug Messages", false));
    private Setting<Boolean> hotbarRefill = this.register(Settings.b("Refill Hotbar", true));
    private int stage;
    private BlockPos placeTarget;
    private int bedSlot;
    private boolean isSneaking;

    boolean moving = false;

    @Override
    protected void onEnable() {
        df.setRoundingMode(RoundingMode.CEILING);
        this.stage = 0;
        this.placeTarget = null;
        this.bedSlot = -1;
        this.isSneaking = false;
        for (int i = 0; i < 9; i++) {
            if (bedSlot != -1) {
                break;
            }
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() instanceof ItemBed) {
                bedSlot = i;
                break;
            }
        }
        if (this.bedSlot == -1) {
            if (this.debugMessages.getValue().booleanValue()) {
                Command.sendChatMessage("Bed(s) missing");
            }
            this.disable();
            return;
        }
        if (BedBomb.mc.objectMouseOver == null || BedBomb.mc.objectMouseOver.getBlockPos() == null || BedBomb.mc.objectMouseOver.getBlockPos().up() == null) {
            if (this.debugMessages.getValue().booleanValue()) {
                Command.sendChatMessage("Not a valid place target");
            }
            this.disable();
            return;
        }
        this.placeTarget = BedBomb.mc.objectMouseOver.getBlockPos().up();

        if (this.debugMessages.getValue() == false) return;

    }

    @Override
    public void onUpdate() {
        if (BedBomb.mc.player == null) return;
        if (this.stage == 0) {
            BedBomb.mc.player.inventory.currentItem = this.bedSlot;
            this.placeBlock(new BlockPos((Vec3i) this.placeTarget), EnumFacing.DOWN);

            BedBomb.mc.player.connection.sendPacket((Packet) new CPacketEntityAction((Entity) BedBomb.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
            BedBomb.mc.player.connection.sendPacket((Packet) new CPacketPlayerTryUseItemOnBlock(this.placeTarget.add(0, 0, 0), EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
            this.stage = 1;
            return;
        }
        this.disable();

        if (hotbarRefill.getValue()) {
            if (mc.currentScreen == null || !(mc.currentScreen instanceof GuiContainer)) {
                if (mc.player.inventory.getStackInSlot(0).getItem() != Items.BED) {
                    for (int i = 9; i < 35; ++i) {
                        if (mc.player.inventory.getStackInSlot(i).getItem() == Items.BED) {
                            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, 0, ClickType.SWAP, mc.player);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void placeBlock(BlockPos pos, EnumFacing side) {
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        if (!this.isSneaking) {
            BedBomb.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity) BedBomb.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            this.isSneaking = true;
        }
        Vec3d hitVec = new Vec3d((Vec3i)neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        if (this.rotate.getValue().booleanValue()) {
            BlockInteractionHelper.faceVectorPacketInstant(hitVec);
        }
        BedBomb.mc.playerController.processRightClickBlock(BedBomb.mc.player, BedBomb.mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        BedBomb.mc.player.swingArm(EnumHand.MAIN_HAND);
    }
}