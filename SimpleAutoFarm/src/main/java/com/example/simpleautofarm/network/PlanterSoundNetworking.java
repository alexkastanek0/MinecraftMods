package com.example.simpleautofarm.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public final class PlanterSoundNetworking {

    private PlanterSoundNetworking() {
    }

    public static void sendSound(ServerLevel level, BlockPos pos) {
        PlanterSoundPayload payload =
            new PlanterSoundPayload(pos);

        List<ServerPlayer> players = level.players();

        for (ServerPlayer player : players) {
            if (player.level() == level
                    && player.blockPosition().distSqr(pos) <= 64 * 64) {
                ServerPlayNetworking.send(player, payload);
            }
        }
    }
}
