package com.example.simpleautofarm.client.render;

import com.example.simpleautofarm.blockentity.HarvesterBlockEntity;
import com.example.simpleautofarm.client.PlanterHarvesterSimpleAutoFarmClient;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.vertex.PoseStack;
import org.jspecify.annotations.Nullable;

/**
 * Renders a vanilla piston head moving out of and back into the harvester.
 * It is visual-only: it never changes world blocks or collision shapes.
 */
public final class HarvesterBlockEntityRenderer
        implements BlockEntityRenderer<HarvesterBlockEntity, HarvesterBlockEntityRenderer.State> {

    public HarvesterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        // No renderer context is needed yet.
    }

    public static final class State extends BlockEntityRenderState {
        public @Nullable MovingBlockRenderState movingHead;
        public float xOffset;
        public float yOffset;
        public float zOffset;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(
            HarvesterBlockEntity blockEntity,
            State state,
            float partialTick,
            Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(
            blockEntity, state, partialTick, cameraPosition, breakProgress
        );

        state.movingHead = null;
        state.xOffset = 0.0F;
        state.yOffset = 0.0F;
        state.zOffset = 0.0F;

        Level level = blockEntity.getLevel();
        if (!(level instanceof ClientLevel clientLevel)) {
            return;
        }

        float progress = PlanterHarvesterSimpleAutoFarmClient.getAnimationProgress(
            blockEntity.getBlockPos(), partialTick
        );
        if (progress <= 0.0F) {
            return;
        }

        BlockPos pos = blockEntity.getBlockPos();
        BlockState baseState = blockEntity.getBlockState();
        Direction facing = baseState.getValue(
            net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
        );

        BlockState headState = Blocks.PISTON_HEAD.defaultBlockState()
            .setValue(PistonHeadBlock.FACING, facing)
            .setValue(PistonHeadBlock.TYPE, PistonType.DEFAULT)
            .setValue(
                PistonHeadBlock.SHORT,
                PlanterHarvesterSimpleAutoFarmClient.isRetracting(
                    pos, partialTick
                ) ? progress >= 0.5F : progress <= 0.5F
            );

        BlockPos headPos = pos.relative(facing);
        Holder<Biome> biome = clientLevel.getBiome(headPos);
        MovingBlockRenderState moving = new MovingBlockRenderState();
        moving.randomSeedPos = headPos;
        moving.blockPos = headPos;
        moving.blockState = headState;
        moving.biome = biome;
        moving.cardinalLighting = clientLevel.cardinalLighting();
        moving.lightEngine = clientLevel.getLightEngine();
        state.movingHead = moving;

        state.xOffset = facing.getStepX() * progress;
        state.yOffset = facing.getStepY() * progress;
        state.zOffset = facing.getStepZ() * progress;
    }

    @Override
    public void submit(
            State state,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            CameraRenderState camera) {
        if (state.movingHead == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(state.xOffset, state.yOffset, state.zOffset);
        collector.submitMovingBlock(poseStack, state.movingHead, 0);
        poseStack.popPose();
    }
}
