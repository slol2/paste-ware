package me.zeroeightsix.kami.module.modules.combat;

import java.math.RoundingMode;
import java.text.DecimalFormat;


import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.BlockInteractionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
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
import net.minecraft.item.Item;
import net.minecraft.item.*;

@Module.Info(name="AutoBedBomb", category=Module.Category.COMBAT)
public class AutoBedBomb extends Module {
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
        if (Auto32k.mc.player == null || ModuleManager.isModuleEnabled("Freecam")) {
            this.disable();
            return;
        }
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
                Command.sendChatMessage("Bed(s) missing, disabling.");
            }
            this.disable();
            return;
        }
        if (Auto32k.mc.objectMouseOver == null || Auto32k.mc.objectMouseOver.getBlockPos() == null || Auto32k.mc.objectMouseOver.getBlockPos().up() == null) {
            if (this.debugMessages.getValue().booleanValue()) {
                Command.sendChatMessage("Not a valid place target, disabling.");
            }
            this.disable();
            return;
        }
        this.placeTarget = Auto32k.mc.objectMouseOver.getBlockPos().up();

        if (this.debugMessages.getValue() == false) return;

    }

    @Override
    public void onUpdate() {
        if (Auto32k.mc.player == null) return;
        if (ModuleManager.isModuleEnabled("Freecam")) {
            return;
        }
        if (this.stage == 0) {
            Auto32k.mc.player.inventory.currentItem = this.bedSlot;
            this.placeBlock(new BlockPos((Vec3i) this.placeTarget), EnumFacing.DOWN);

            Auto32k.mc.player.connection.sendPacket((Packet) new CPacketEntityAction((Entity) Auto32k.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
            Auto32k.mc.player.connection.sendPacket((Packet) new CPacketPlayerTryUseItemOnBlock(this.placeTarget.add(0, 0, 0), EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
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
            Auto32k.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity) Auto32k.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            this.isSneaking = true;
        }
        Vec3d hitVec = new Vec3d((Vec3i)neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        if (this.rotate.getValue().booleanValue()) {
            BlockInteractionHelper.faceVectorPacketInstant(hitVec);
        }
        Auto32k.mc.playerController.processRightClickBlock(Auto32k.mc.player, Auto32k.mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        Auto32k.mc.player.swingArm(EnumHand.MAIN_HAND);
    }
}