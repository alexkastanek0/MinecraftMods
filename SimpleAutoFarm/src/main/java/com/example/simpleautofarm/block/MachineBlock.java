package com.example.simpleautofarm.block;

import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.Level;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.redstone.Orientation;

public class MachineBlock extends Block {

    public MachineBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(
            stateDefinition.any()
                .setValue(
                    BlockStateProperties.HORIZONTAL_FACING,
                    Direction.NORTH
                )
                .setValue(BlockStateProperties.POWERED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(
        StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(
            BlockStateProperties.HORIZONTAL_FACING,
            BlockStateProperties.POWERED
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
            .setValue(
                BlockStateProperties.HORIZONTAL_FACING,
                context.getHorizontalDirection().getOpposite()
            )
            .setValue(
                BlockStateProperties.POWERED,
                context.getLevel().hasNeighborSignal(context.getClickedPos()
            )
        );
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(
            BlockStateProperties.HORIZONTAL_FACING,
            rotation.rotate(
                state.getValue(BlockStateProperties.HORIZONTAL_FACING)
            )
        );
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(
            state.getValue(BlockStateProperties.HORIZONTAL_FACING)
        ));
    }

    /**
     * Detects changes in redstone power.
     * Subclasses implement the actual machine operation.
     */
    @Override
    protected void neighborChanged(
        BlockState state,
        Level level,
        BlockPos pos,
        Block neighborBlock,
        Orientation orientation,
        boolean movedByPiston
    ) {
        super.neighborChanged(
            state, level, pos, neighborBlock, orientation, movedByPiston
        );

        if (level.isClientSide()) {
            return;
        }

        boolean poweredNow = level.hasNeighborSignal(pos);
        boolean poweredBefore = state.getValue(
            BlockStateProperties.POWERED
        );

        if (poweredNow == poweredBefore) {
            return;
        }

        BlockState updatedState = state.setValue(
            BlockStateProperties.POWERED,
            poweredNow
        );

        level.setBlock(pos, updatedState, Block.UPDATE_ALL);

        // Only the off-to-on transition triggers an operation.
        if (poweredNow && !poweredBefore) {
            onRedstonePulse(level, pos);
        }
    }

    /**
     * Called once when this machine receives a rising redstone edge.
     */
    protected void onRedstonePulse(Level level, BlockPos pos) {
        // Intentionally empty; subclasses supply their own behavior.
    }

    @Override
    protected InteractionResult useWithoutItem(
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        BlockHitResult hit
    ) {
        if (!(level.getBlockEntity(pos) instanceof MenuProvider menuProvider)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            player.openMenu(menuProvider);
        }

        return InteractionResult.SUCCESS;
    }
}
