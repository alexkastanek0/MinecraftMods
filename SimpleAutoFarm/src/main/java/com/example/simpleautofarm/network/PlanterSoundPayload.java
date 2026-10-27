package com.example.simpleautofarm.network;

import com.example.simpleautofarm.PlanterHarvesterSimpleAutoFarm;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record PlanterSoundPayload(BlockPos pos)
        implements CustomPacketPayload {

    public static final Type<PlanterSoundPayload> TYPE =
        new Type<>(
            PlanterHarvesterSimpleAutoFarm.id("planter_sound")
        );

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            PlanterSoundPayload
        > CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            PlanterSoundPayload::pos,
            PlanterSoundPayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
