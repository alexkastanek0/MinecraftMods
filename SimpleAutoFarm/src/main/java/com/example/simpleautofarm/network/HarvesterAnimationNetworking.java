
package com.example.simpleautofarm.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

public final class HarvesterAnimationNetworking {

    private HarvesterAnimationNetworking() {
    }

    public static void sendAnimation(ServerLevel level, BlockPos pos) {
        HarvesterAnimationPayload payload =
            new HarvesterAnimationPayload(pos);

        List<ServerPlayer> players =
            level.players();

        for (ServerPlayer player : players) {
            if (player.level() == level
                    && player.blockPosition().distSqr(pos) <= 64 * 64) {
                ServerPlayNetworking.send(player, payload);
            }
        }
    }
}
