package com.example.simpleautofarm.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.example.simpleautofarm.network.HarvesterAnimationPayload;
import com.example.simpleautofarm.network.PlanterSoundPayload;
import com.example.simpleautofarm.registry.ModBlockEntities;
import com.example.simpleautofarm.registry.ModMenuTypes;
import com.example.simpleautofarm.client.render.HarvesterBlockEntityRenderer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;

public class PlanterHarvesterSimpleAutoFarmClient implements ClientModInitializer {
    /** A complete extend-and-retract cycle lasts four client ticks. */
    public static final int ANIMATION_DURATION_TICKS = 8;
    private static final Map<BlockPos, Integer> HARVESTER_ANIMATIONS = new HashMap<>();

    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.

		MenuScreens.register(
            ModMenuTypes.PLANTER,
            PlanterScreen::new
        );

        MenuScreens.register(
            ModMenuTypes.HARVESTER,
            HarvesterScreen::new
        );

        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(
            ModBlockEntities.HARVESTER,
            HarvesterBlockEntityRenderer::new
        );

        ClientPlayNetworking.registerGlobalReceiver(
            HarvesterAnimationPayload.TYPE,
            (payload, context) -> context.client().execute(() -> {
                BlockPos pos = payload.pos().immutable();
                HARVESTER_ANIMATIONS.put(pos, 0);

                // The packet is only sent after a successful server-side harvest,
                // so failed attempts never animate or play sounds.
                if (context.client().level != null) {
                    context.client().level.playLocalSound(
                        pos,
                        SoundEvents.PISTON_EXTEND,
                        SoundSource.BLOCKS,
                        0.5F,
                        0.7F,
                        false
                    );
                }
            })
        );

        ClientPlayNetworking.registerGlobalReceiver(
            PlanterSoundPayload.TYPE,
            (payload, context) -> context.client().execute(() -> {
                if (context.client().level != null) {
                    context.client().level.playLocalSound(
                        payload.pos(),
                        SoundEvents.DISPENSER_DISPENSE,
                        SoundSource.BLOCKS,
                        1.0F,
                        1.0F,
                        false
                    );
                }
            })
        );

        ClientTickEvents.END_CLIENT_TICK.register(
            PlanterHarvesterSimpleAutoFarmClient::tickHarvesterAnimations
        );
    }

    /**
     * Returns extension distance in blocks: 0 -> 1 -> 0.
     * partialTick keeps the movement smooth between client ticks.
     */
    public static float getAnimationProgress(BlockPos pos, float partialTick) {
        Integer elapsed = HARVESTER_ANIMATIONS.get(pos);
        if (elapsed == null) {
            return 0.0F;
        }

        float t = Math.min(1.0F,
            (elapsed + partialTick) / (float) ANIMATION_DURATION_TICKS);

        // Vanilla-like two-stage piston motion: extend, then retract.
        return t < 0.5F ? t * 2.0F : (1.0F - t) * 2.0F;
    }

    public static boolean isRetracting(BlockPos pos, float partialTick) {
        Integer elapsed = HARVESTER_ANIMATIONS.get(pos);
        return elapsed != null
            && (elapsed + partialTick) / (float) ANIMATION_DURATION_TICKS >= 0.5F;
    }

    private static void tickHarvesterAnimations(Minecraft client) {
        Iterator<Map.Entry<BlockPos, Integer>> iterator =
            HARVESTER_ANIMATIONS.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<BlockPos, Integer> entry = iterator.next();
            int elapsed = entry.getValue() + 1;
            entry.setValue(elapsed);

            if (elapsed == ANIMATION_DURATION_TICKS / 2
                    && client.level != null) {
                client.level.playLocalSound(
                    entry.getKey(),
                    SoundEvents.PISTON_CONTRACT,
                    SoundSource.BLOCKS,
                    0.5F,
                    0.7F,
                    false
                );
            }

            if (elapsed >= ANIMATION_DURATION_TICKS) {
                iterator.remove();
            }
        }
    }
}
