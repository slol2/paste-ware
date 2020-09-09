package me.cereal.utility.module.modules.combat;

import me.cereal.utility.command.Command;
import me.cereal.utility.module.Module;
import me.cereal.utility.setting.Setting;
import me.cereal.utility.setting.Settings;
import me.cereal.utility.util.BlockInteractionHelper;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.math.RoundingMode;
import java.text.DecimalFormat;

@Module.Info(name = "BedBomb", category = Module.Category.COMBAT)
public class BedBomb extends Module {

    private static final DecimalFormat df = new DecimalFormat("#.#");
    boolean moving = false;
    private final Setting<Boolean> rotate = this.register(Settings.b("Rotate", false));
    private final Setting<Boolean> debugMessages = this.register(Settings.b("Debug Messages", false));
    private final Setting<Boolean> hotbarRefill = this.register(Settings.b("Refill Hotbar", true));
    private int stage;
    private BlockPos placeTarget;
    private int bedSlot;
    private boolean isSneaking;

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
            this.placeBlock(new BlockPos(this.placeTarget), EnumFacing.DOWN);

            BedBomb.mc.player.connection.sendPacket(new CPacketEntityAction(BedBomb.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
            BedBomb.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(this.placeTarget.add(0, 0, 0), EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
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
            BedBomb.mc.player.connection.sendPacket(new CPacketEntityAction(BedBomb.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            this.isSneaking = true;
        }
        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        if (this.rotate.getValue().booleanValue()) {
            BlockInteractionHelper.faceVectorPacketInstant(hitVec);
        }
        BedBomb.mc.playerController.processRightClickBlock(BedBomb.mc.player, BedBomb.mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        BedBomb.mc.player.swingArm(EnumHand.MAIN_HAND);
    }
}