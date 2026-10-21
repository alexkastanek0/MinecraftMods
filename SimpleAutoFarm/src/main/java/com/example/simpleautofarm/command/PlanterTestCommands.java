package com.example.simpleautofarm.command;

import com.example.simpleautofarm.PlanterHarvesterSimpleAutoFarm;
import com.example.simpleautofarm.blockentity.HarvesterBlockEntity;
import com.example.simpleautofarm.blockentity.PlanterBlockEntity;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public final class PlanterTestCommands {
    private PlanterTestCommands() {

    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> {
                dispatcher.register(
                    Commands.literal("simpleautofarm")
                        .requires(source ->
                            source.permissions().hasPermission(
                                Permissions.COMMANDS_MODERATOR
                            )
                        )
                        .then(
                            Commands.literal("plant")
                                .executes(context -> {
                                    var source = context.getSource();
                                    var player =
                                        source.getPlayerOrException();

                                    // Find the block the player is looking at,
                                    // up to five blocks away.
                                    HitResult hit = player.pick(5.0, 0.0F, false);

                                    if (!(hit instanceof BlockHitResult blockHit)
                                            || hit.getType()
                                                == HitResult.Type.MISS) {
                                        source.sendFailure(
                                            Component.literal(
                                                "Look at a planter within 5 blocks."
                                            )
                                        );
                                        return 0;
                                    }

                                    // Get the block entity at the targeted position.
                                    BlockEntity blockEntity =
                                        source.getLevel().getBlockEntity(
                                            blockHit.getBlockPos()
                                        );

                                    if (!(blockEntity
                                            instanceof PlanterBlockEntity planter)) {
                                        source.sendFailure(
                                            Component.literal(
                                                "The targeted block is not a planter."
                                            )
                                        );
                                        return 0;
                                    }

                                    // Attempt exactly one planting operation.
                                    PlanterHarvesterSimpleAutoFarm.LOGGER.info(
                                        "Plant test: target position={}, state={}",
                                        planter.getTargetCropPos(),
                                        source.getLevel().getBlockState(
                                            planter.getTargetCropPos()
                                        )
                                    );
                                    boolean planted = planter.tryPlant();

                                    if (planted) {
                                        source.sendSuccess(
                                            () -> Component.literal(
                                                "Planter test: crop planted successfully."
                                            ),
                                            false
                                        );
                                        return 1;
                                    }

                                    source.sendFailure(
                                        Component.literal(
                                            "Planter test: planting failed. "
                                                + "Check the target, farmland, "
                                                + "and inventory."
                                        )
                                    );
                                    return 0;
                                })
                        )
                        .then(
                            Commands.literal("harvest")
                                .executes(context -> {
                                    var source = context.getSource();
                                    var player = source.getPlayerOrException();

                                    // Find the block the player is looking at,
                                    // up to five blocks away.
                                    HitResult hit = player.pick(5.0, 0.0F, false);

                                    if (!(hit instanceof BlockHitResult blockHit)
                                            || hit.getType() == HitResult.Type.MISS) {
                                        source.sendFailure(
                                            Component.literal(
                                                "Look at a harvester within 5 blocks."
                                            )
                                        );
                                        return 0;
                                    }

                                    // Get the block entity at the targeted position.
                                    BlockEntity blockEntity =
                                        source.getLevel().getBlockEntity(
                                            blockHit.getBlockPos()
                                        );

                                    if (!(blockEntity instanceof HarvesterBlockEntity harvester)) {
                                        source.sendFailure(
                                            Component.literal(
                                                "The targeted block is not a harvester."
                                            )
                                        );
                                        return 0;
                                    }

                                    // Attempt exactly one harvesting operation.
                                    PlanterHarvesterSimpleAutoFarm.LOGGER.info(
                                        "Harvest test: target position={}, state={}",
                                        harvester.getTargetCropPos(),
                                        source.getLevel().getBlockState(
                                            harvester.getTargetCropPos()
                                        )
                                    );
                                    boolean harvested = harvester.tryHarvest();

                                    if (harvested) {
                                        source.sendSuccess(
                                            () -> Component.literal(
                                                "Harvester test: crop harvested successfully."
                                            ),
                                            false
                                        );
                                        return 1;
                                    }

                                    source.sendFailure(
                                        Component.literal(
                                            "Harvester test failed. Check crop maturity, "
                                                + "facing direction, and inventory capacity."
                                        )
                                    );
                                    return 0;
                                })
                        )
                );
            }
        );
    }
}
