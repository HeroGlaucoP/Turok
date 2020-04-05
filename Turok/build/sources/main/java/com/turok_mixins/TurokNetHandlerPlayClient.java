package com.turok_mixins;

import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.world.chunk.Chunk;

import com.oldturok.turok.event.events.ChunkEvent;
import com.oldturok.turok.TurokMod;

import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(NetHandlerPlayClient.class)
public class TurokNetHandlerPlayClient {

    @Inject(method = "handleChunkData",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;read(Lnet/minecraft/network/PacketBuffer;IZ)V"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void read(SPacketChunkData data, CallbackInfo info, Chunk chunk) {
        TurokMod.EVENT_BUS.post(new ChunkEvent(chunk, data));
    }

}
