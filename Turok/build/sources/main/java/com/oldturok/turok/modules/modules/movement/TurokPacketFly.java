package com.oldturok.turok.module.modules.movement;

import com.oldturok.turok.util.BlockInteractionHelper;
import com.oldturok.turok.event.events.PacketEvent;
import com.oldturok.turok.setting.Settings;
import com.oldturok.turok.setting.Setting;
import com.oldturok.turok.util.EntityUtil;
import com.oldturok.turok.module.Module;

import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.Packet;
import net.minecraft.entity.Entity;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

import java.util.function.Predicate;
import java.util.ArrayList;
import java.util.List;

// Rina.
// Coded in 05/03/20.
// About this code, is not a paste, i modify somethings and the seppuku is public.
// Thanks Memmez for source.
// The seppuku is a good fly, i cant make a better, i recoded and make somes modify to be "better".
// Enjoy.
@Module.Info(name = "Turok Seppuku Fly", category = Module.Category.TUROK_MOVEMENT)
public class TurokPacketFly extends Module {
	private Setting<Boolean> fly_no_kick = register(Settings.b("No Kick", true));
	private Setting<Float> fly_speed = register(Settings.floatBuilder("Speed").withMinimum(1.0f).withValue(5.0f).withMaximum(6.0f));

	private int fly_teleport_id;

	private CPacketPlayer[] fly_bounds = new CPacketPlayer[1];

	private double[] fly_y_speed   = new double[1];
	private double[] fly_2_y_speed = new double[1];
	private double[] fly_n         = new double[1];

	private double[][] fly_directional_speed = new double[1][1];

	private int[] fly_i = new int[1];
	private int[] fly_j = new int[1];
	private int[] fly_k = new int[1];

	private List<CPacketPlayer> fly_packets = new ArrayList<CPacketPlayer>();

	@EventHandler
	public Listener<InputUpdateEvent> listener = new Listener<InputUpdateEvent>(event -> {
		if (fly_teleport_id <= 0) {
			fly_bounds[0] = (CPacketPlayer) new CPacketPlayer.Position(mc.player.posX, 0.0, mc.player.posZ, mc.player.onGround);
			
			fly_packets.add(fly_bounds[0]);

			mc.player.connection.sendPacket((Packet) fly_bounds[0]);

			return;
		} else {
			mc.player.setVelocity(0.0, 0.0, 0.0);

			if (mc.world.getCollisionBoxes((Entity) mc.player, mc.player.getEntityBoundingBox().expand(- 0.0625, 0.0, - 0.0625)).isEmpty()) {
				if (mc.gameSettings.keyBindJump.isKeyDown()) {
					if (fly_no_kick.getValue()) {
						fly_2_y_speed[0] = ((mc.player.ticksExisted % 20 == 0) ? - 0.03999999910593033 : 0.06199999898672104);
					} else {
						fly_2_y_speed[0] = 0.06199999898672104;
					}
				} else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
					fly_2_y_speed[0] = - 0.062;
				} else {
					if (mc.world.getCollisionBoxes((Entity) mc.player, mc.player.getEntityBoundingBox().expand(- 0.0625, - 0.0625, - 0.0625)).isEmpty()) {
						if (mc.player.ticksExisted % 4 == 0) {
							fly_n[0] = (fly_no_kick.getValue() ? - 0.04f : 0.0f);
						} else {
							fly_n[0] = 0.0;
						}
					} else {
						fly_n[0] = 0.0;
					}
					fly_2_y_speed[0] = fly_n[0];
				}

				fly_directional_speed[0] = BlockInteractionHelper.directionSpeed(fly_speed.getValue());
				if (mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindSneak.isKeyDown() || mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown()) {
					if (fly_directional_speed[0][0] != 0.0 || fly_2_y_speed[0] != 0.0 || fly_directional_speed[0][1] != 0.0) {
						if (mc.player.movementInput.jump && (mc.player.moveStrafing != 0.0f || mc.player.moveForward != 0.0f)) {
							mc.player.setVelocity(0.0, 0.0, 0.0);
							fly_move(0.0, 0.0, 0.0);
							for (fly_i[0] = 0; fly_i[0] <= 3; ++fly_i[0]);
								mc.player.setVelocity(0.0, fly_2_y_speed[0] * fly_i[0], 0.0);
								fly_move(0.0, fly_2_y_speed[0] * fly_i[0], 0.0);
						}
					} else if (mc.player.movementInput.jump) {
						mc.player.setVelocity(0.0, 0.0, 0.0);
						for (fly_j[0] = 0; fly_j[0] <= 3; ++fly_j[0]) {
							mc.player.setVelocity(0.0, fly_2_y_speed[0] * fly_j[0], 0.0);
							fly_move(0.0, fly_2_y_speed[0] * fly_j[0], 0.0);
						}
					} else {
						for (fly_k[0] = 0; fly_k[0] <= 2; ++fly_k[0]) {
							mc.player.setVelocity(fly_directional_speed[0][0] * fly_k[0], fly_2_y_speed[0] * fly_k[0], fly_directional_speed[0][1] * fly_k[0]);
							fly_move(fly_directional_speed[0][0] * fly_k[0], fly_2_y_speed[0] * fly_k[0], fly_directional_speed[0][1] * fly_k[0]);
						}
					}
				}
			} else if (fly_no_kick.getValue() && mc.world.getCollisionBoxes((Entity) mc.player, mc.player.getEntityBoundingBox().expand(- 0.0625, - 0.0625, - 0.0625)).isEmpty()) {
				mc.player.setVelocity(0.0, (mc.player.ticksExisted % 2 == 0) ? 0.03999999910593033 : - 0.03999999910593033, 0.0);
				fly_move(0.0, (mc.player.ticksExisted % 2 == 0) ? 0.03999999910593033 : - 0.03999999910593033, 0.0);
			}
		}

		return;
	}, (Predicate <InputUpdateEvent>[]) new Predicate[0]);

	CPacketPlayer[] fly_packet = new CPacketPlayer[1];

	@EventHandler
	public Listener<PacketEvent.Send> sendListener = new Listener<PacketEvent.Send> (event -> {
		if (event.getPacket() instanceof CPacketPlayer && !(event.getPacket() instanceof CPacketPlayer.Position)) {
			event.cancel();
		}

		if (event.getPacket() instanceof CPacketPlayer) {
			fly_packet[0] = (CPacketPlayer) event.getPacket();

			if (fly_packets.contains(fly_packet[0])) {
				fly_packets.remove(fly_packet[0]);
			} else {
				event.cancel();
			}
		}

		return;
	}, (Predicate<PacketEvent.Send>[]) new Predicate[0]);

	SPacketPlayerPosLook[] fly_packet_2 = new SPacketPlayerPosLook[1];

	@EventHandler
	public Listener<PacketEvent.Receive> receiveListener = new Listener<PacketEvent.Receive> (event -> {
		if (event.getPacket() instanceof SPacketPlayerPosLook) {
			fly_packet_2[0] = (SPacketPlayerPosLook) event.getPacket();

			if (mc.player.isEntityAlive()) {
				if (mc.world.isBlockLoaded(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)) && !(mc.currentScreen instanceof GuiDownloadTerrain)) {
					if (fly_teleport_id <= 0) {
						fly_teleport_id = fly_packet_2[0].getTeleportId();
					} else {
						event.cancel();
					}
				}
			}
		}
	}, (Predicate<PacketEvent.Receive>[]) new Predicate[0]);

	public void onEnable() {
		if (mc.world != null) {
			fly_teleport_id = 0;
			
			fly_packets.clear();
			
			CPacketPlayer fly_bounds = (CPacketPlayer) new CPacketPlayer.Position(mc.player.posX, 0.0, mc.player.posZ, mc.player.onGround);

			fly_packets.add(fly_bounds);

			mc.player.connection.sendPacket((Packet) fly_bounds);
		}
	}

	public void fly_move(double x, double y, double z) {
		CPacketPlayer fly_pos = ((CPacketPlayer) new CPacketPlayer.Position(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z, mc.player.onGround));

		fly_packets.add(fly_pos);

		mc.player.connection.sendPacket((Packet) fly_pos);

		CPacketPlayer fly_bounds = ((CPacketPlayer) new CPacketPlayer.Position(mc.player.posX + x, 0.0, mc.player.posZ + z, mc.player.onGround));

		fly_packets.add(fly_bounds);

		mc.player.connection.sendPacket((Packet) fly_bounds);

		++fly_teleport_id;

		mc.player.connection.sendPacket((Packet) new CPacketConfirmTeleport(fly_teleport_id - 1));
		mc.player.connection.sendPacket((Packet) new CPacketConfirmTeleport(fly_teleport_id ));
		mc.player.connection.sendPacket((Packet) new CPacketConfirmTeleport(fly_teleport_id + 1));
	}
}