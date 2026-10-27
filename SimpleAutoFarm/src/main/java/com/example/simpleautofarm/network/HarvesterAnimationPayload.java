package com.example.simpleautofarm.network;

import com.example.simpleautofarm.PlanterHarvesterSimpleAutoFarm;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record HarvesterAnimationPayload(BlockPos pos) implements CustomPacketPayload {
    public static final Type<HarvesterAnimationPayload> TYPE =
        new Type<>(
            PlanterHarvesterSimpleAutoFarm.id("harvester_animation")
        );

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            HarvesterAnimationPayload
        > CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            HarvesterAnimationPayload::pos,
            HarvesterAnimationPayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
