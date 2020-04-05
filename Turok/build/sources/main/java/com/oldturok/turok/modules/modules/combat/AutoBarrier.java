package com.oldturok.turok.module.modules.combat;

import com.oldturok.turok.util.BlockInteractionHelper;
import com.oldturok.turok.module.ModuleManager;
import com.oldturok.turok.setting.Settings;
import com.oldturok.turok.setting.Setting;
import com.oldturok.turok.module.Module;
import com.oldturok.turok.TurokMessage;
import com.oldturok.turok.TurokChat;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.block.BlockObsidian;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.BlockAir;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.Entity;
import net.minecraft.block.Block;

import static com.oldturok.turok.util.BlockInteractionHelper.faceVectorPacketInstant;
import static com.oldturok.turok.util.BlockInteractionHelper.canBeClicked;

// Update by Rina 09/03/20.
@Module.Info(name = "AutoBarrier", description = "Auto barrier using obsidians.", category = Module.Category.TUROK_COMBAT)
public class AutoBarrier extends Module {
    private Setting<Mode> mode             = register(Settings.e("Mode", Mode.FULL));
    private Setting<Boolean> triggerable   = register(Settings.b("Triggerable", true));
    private Setting<Integer> timeoutTicks  = register(Settings.integerBuilder("TimeoutTicks").withMinimum(1).withValue(13).withMaximum(100).withVisibility(b -> triggerable.getValue()).build());
    private Setting<Integer> blocksPerTick = register(Settings.integerBuilder("BlocksPerTick").withMinimum(1).withValue(4).withMaximum(9).build());
    private Setting<Integer> tickDelay     = register(Settings.integerBuilder("TickDelay").withMinimum(0).withValue(0).withMaximum(10).build());
    private Setting<Boolean> rotate        = register(Settings.b("Rotate", true));
    private Setting<Boolean> infoMessage   = register(Settings.b("InfoMessage", false));

    private int offsetStep = 0;
    private int delayStep = 0;

    private int playerHotbarSlot = -1;
    private int lastHotbarSlot = -1;
    private boolean isSneaking = false;

    private int totalTicksRunning = 0;
    private boolean firstRun;
    private boolean missingObiDisable = false;

    private static EnumFacing getPlaceableSide(BlockPos pos) {
        for (EnumFacing side : EnumFacing.values()) {

            BlockPos neighbour = pos.offset(side);

            if (!mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false)) {
                continue;
            }

            IBlockState blockState = mc.world.getBlockState(neighbour);
            if (!blockState.getMaterial().isReplaceable()) {
                return side;
            }

        }
        return null;
    }

    @Override
    protected void onEnable() {
        if (mc.player == null) {
            this.disable();
            return;
        }

        firstRun = true;

        playerHotbarSlot = mc.player.inventory.currentItem;
        lastHotbarSlot = -1;

    }

    @Override
    protected void onDisable() {
        if (mc.player == null) {
            return;
        }

        if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
            mc.player.inventory.currentItem = playerHotbarSlot;
        }

        if (isSneaking) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }

        playerHotbarSlot = -1;
        lastHotbarSlot = -1;

        missingObiDisable = false;

    }

    @Override
    public void onUpdate() {
        if (mc.player == null || ModuleManager.isModuleEnabled("Freecam")) {
            return;
        }

        if (triggerable.getValue() && totalTicksRunning >= timeoutTicks.getValue()) {
            totalTicksRunning = 0;
            this.disable();
            return;
        }

        if (!firstRun) {
            if (delayStep < tickDelay.getValue()) {
                delayStep++;
                return;
            } else {
                delayStep = 0;
            }
        }

        if (firstRun) {
            firstRun = false;
            if (findObiInHotbar() == -1) {
                missingObiDisable = true;
            }
        }

        Vec3d[] offsetPattern = new Vec3d[0];
        int maxSteps = 0;

        if (mode.getValue().equals(Mode.FULL)) {
            offsetPattern = Offsets.FULL;
            maxSteps = Offsets.FULL.length;
        }

        if (mode.getValue().equals(Mode.SURROUND)) {
            offsetPattern = Offsets.SURROUND;
            maxSteps = Offsets.SURROUND.length;
        }

        int blocksPlaced = 0;

        while (blocksPlaced < blocksPerTick.getValue()) {

            if (offsetStep >= maxSteps) {
                offsetStep = 0;
                break;
            }

            BlockPos offsetPos = new BlockPos(offsetPattern[offsetStep]);
            BlockPos targetPos = new BlockPos(mc.player.getPositionVector()).add(offsetPos.x, offsetPos.y, offsetPos.z);

            if (placeBlock(targetPos)) {
                blocksPlaced++;
            }

            offsetStep++;

        }

        if (blocksPlaced > 0) {

            if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
                mc.player.inventory.currentItem = playerHotbarSlot;
                lastHotbarSlot = playerHotbarSlot;
            }

            if (isSneaking) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                isSneaking = false;
            }

        }

        totalTicksRunning++;

        if (missingObiDisable) {
            missingObiDisable = false;
            if (infoMessage.getValue()) {
                TurokMessage.send_msg("AutoBarrier <- " + ChatFormatting.RED + "OFF");
            }

            this.disable();
        }

    }

    private boolean placeBlock(BlockPos pos) {
        Block block = mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        }

        for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
            if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                return false;
            }
        }

        EnumFacing side = getPlaceableSide(pos);

        if (side == null) {
            return false;
        }

        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();

        if (!canBeClicked(neighbour)) {
            return false;
        }

        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

        int obiSlot = findObiInHotbar();

        if (obiSlot == -1) {
            missingObiDisable = true;
            return false;
        }

        if (lastHotbarSlot != obiSlot) {
            mc.player.inventory.currentItem = obiSlot;
            lastHotbarSlot = obiSlot;
        }

        if (!isSneaking && BlockInteractionHelper.blackList.contains(neighbourBlock) || BlockInteractionHelper.shulkerList.contains(neighbourBlock)) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            isSneaking = true;
        }

        if (rotate.getValue()) {
            faceVectorPacketInstant(hitVec);
        }

        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;

        return true;

    }

    private int findObiInHotbar() {
        int slot = -1;
        for (int i = 0; i < 9; i++) {

            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack != ItemStack.EMPTY) {
                if (stack.getItem() instanceof ItemBlock) {
                    Block block = ((ItemBlock)stack.getItem()).getBlock();

                    if (block instanceof BlockObsidian) {
                        slot = i;

                        break;
                    }
                }
            }
        }

        return slot;

    }

    private enum Mode {
        SURROUND, FULL
    }

    private static class Offsets {

        private static final Vec3d[] SURROUND = {
                new Vec3d(1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 0, -1),
                new Vec3d(1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(-1, -1, 0),
                new Vec3d(0, -1, -1)
        };

        private static final Vec3d[] FULL = {
                new Vec3d(1, 0, 0),
                new Vec3d(0, 0, 1),
                new Vec3d(-1, 0, 0),
                new Vec3d(0, 0, -1),
                new Vec3d(1, -1, 0),
                new Vec3d(0, -1, 1),
                new Vec3d(-1, -1, 0),
                new Vec3d(0, -1, -1),
                new Vec3d(0, -1, 0)
        };
    }
}