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

@Module.Info(name="Auto32k", category=Module.Category.COMBAT)
public class Auto32k extends Module {
    private static final DecimalFormat df = new DecimalFormat("#.#");
    private Setting<Boolean> rotate = this.register(Settings.b("Rotate", false));
    private Setting<Boolean> grabItem = this.register(Settings.b("Grab Item", false));
    private Setting<Boolean> autoEnableHitAura = this.register(Settings.b("Auto enable Hit Aura", false));
    private Setting<Boolean> autoEnableBypass = this.register(Settings.b("Auto enable Illegals Bypass", false));
    private Setting<Boolean> debugMessages = this.register(Settings.b("Debug Messages", false));
    private int stage;
    private BlockPos placeTarget;
    private int obiSlot;
    private int dispenserSlot;
    private int shulkerSlot;
    private int redstoneSlot;
    private int hopperSlot;
    private boolean isSneaking;

    @Override
    protected void onEnable() {
        if (Auto32k.mc.player == null || ModuleManager.isModuleEnabled("Freecam")) {
            this.disable();
            return;
        }
        df.setRoundingMode(RoundingMode.CEILING);
        this.stage = 0;
        this.placeTarget = null;
        this.obiSlot = -1;
        this.dispenserSlot = -1;
        this.shulkerSlot = -1;
        this.redstoneSlot = -1;
        this.hopperSlot = -1;
        this.isSneaking = false;
        for (int i = 0; i < 9 && (this.obiSlot == -1 || this.dispenserSlot == -1 || this.shulkerSlot == -1 || this.redstoneSlot == -1 || this.hopperSlot == -1); ++i) {
            ItemStack stack = Auto32k.mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) continue;
            Block block = ((ItemBlock)stack.getItem()).getBlock();
            if (block == Blocks.HOPPER) {
                this.hopperSlot = i;
                continue;
            }
            if (BlockInteractionHelper.shulkerList.contains((Object)block)) {
                this.shulkerSlot = i;
                continue;
            }
            if (block == Blocks.OBSIDIAN) {
                this.obiSlot = i;
                continue;
            }
            if (block == Blocks.DISPENSER) {
                this.dispenserSlot = i;
                continue;
            }
            if (block != Blocks.REDSTONE_BLOCK) continue;
            this.redstoneSlot = i;
        }
        if (this.obiSlot == -1 || this.dispenserSlot == -1 || this.shulkerSlot == -1 || this.redstoneSlot == -1 || this.hopperSlot == -1) {
            if (this.debugMessages.getValue().booleanValue()) {
                Command.sendChatMessage("[Auto32k] Items missing, disabling.");
            }
            this.disable();
            return;
        }
        if (Auto32k.mc.objectMouseOver == null || Auto32k.mc.objectMouseOver.getBlockPos() == null || Auto32k.mc.objectMouseOver.getBlockPos().up() == null) {
            if (this.debugMessages.getValue().booleanValue()) {
                Command.sendChatMessage("[Auto32k] Not a valid place target, disabling.");
            }
            this.disable();
            return;
        }
        this.placeTarget = Auto32k.mc.objectMouseOver.getBlockPos().up();
        if (this.autoEnableBypass.getValue().booleanValue()) {
            ModuleManager.getModuleByName("IllegalItemBypass").enable();
        }
        if (this.debugMessages.getValue() == false) return;
        Command.sendChatMessage("[Auto32k] Place Target: " + this.placeTarget.x + " " + this.placeTarget.y + " " + this.placeTarget.z + " Distance: " + df.format(Auto32k.mc.player.getPositionVector().distanceTo(new Vec3d((Vec3i)this.placeTarget))));
    }

    @Override
    public void onUpdate() {
        if (Auto32k.mc.player == null) return;
        if (ModuleManager.isModuleEnabled("Freecam")) {
            return;
        }
        if (this.stage == 0) {
            Auto32k.mc.player.inventory.currentItem = this.obiSlot;
            this.placeBlock(new BlockPos((Vec3i)this.placeTarget), EnumFacing.DOWN);
            Auto32k.mc.player.inventory.currentItem = this.dispenserSlot;
            this.placeBlock(new BlockPos((Vec3i)this.placeTarget.add(0, 1, 0)), EnumFacing.DOWN);
            Auto32k.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity) Auto32k.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
            Auto32k.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(this.placeTarget.add(0, 1, 0), EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
            this.stage = 1;
            return;
        }
        if (this.stage == 1) {
            if (!(Auto32k.mc.currentScreen instanceof GuiContainer)) {
                return;
            }
            Auto32k.mc.playerController.windowClick(Auto32k.mc.player.openContainer.windowId, 1, this.shulkerSlot, ClickType.SWAP, (EntityPlayer) Auto32k.mc.player);
            Auto32k.mc.player.closeScreen();
            Auto32k.mc.player.inventory.currentItem = this.redstoneSlot;
            this.placeBlock(new BlockPos((Vec3i)this.placeTarget.add(0, 2, 0)), EnumFacing.DOWN);
            this.stage = 2;
            return;
        }
        if (this.stage == 2) {
            Block block = Auto32k.mc.world.getBlockState(this.placeTarget.offset(Auto32k.mc.player.getHorizontalFacing().getOpposite()).up()).getBlock();
            if (block instanceof BlockAir) return;
            if (block instanceof BlockLiquid) {
                return;
            }
            Auto32k.mc.player.inventory.currentItem = this.hopperSlot;
            this.placeBlock(new BlockPos((Vec3i)this.placeTarget.offset(Auto32k.mc.player.getHorizontalFacing().getOpposite())), Auto32k.mc.player.getHorizontalFacing());
            Auto32k.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity) Auto32k.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
            Auto32k.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(this.placeTarget.offset(Auto32k.mc.player.getHorizontalFacing().getOpposite()), EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
            Auto32k.mc.player.inventory.currentItem = this.shulkerSlot;
            if (!this.grabItem.getValue().booleanValue()) {
                this.disable();
                return;
            }
            this.stage = 3;
            return;
        }
        if (this.stage != 3) return;
        if (!(Auto32k.mc.currentScreen instanceof GuiContainer)) {
            return;
        }
        if (((GuiContainer) Auto32k.mc.currentScreen).inventorySlots.getSlot((int)0).getStack().isEmpty) {
            return;
        }
        Auto32k.mc.playerController.windowClick(Auto32k.mc.player.openContainer.windowId, 0, Auto32k.mc.player.inventory.currentItem, ClickType.SWAP, (EntityPlayer) Auto32k.mc.player);
        if (this.autoEnableHitAura.getValue().booleanValue()) {
            ModuleManager.getModuleByName("Aura").enable();
        }
        this.disable();
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