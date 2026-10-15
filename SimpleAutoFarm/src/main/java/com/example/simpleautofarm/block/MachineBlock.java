package com.example.simpleautofarm.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class MachineBlock extends Block {

    public MachineBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(
            stateDefinition.any().setValue(
                BlockStateProperties.HORIZONTAL_FACING,
                Direction.NORTH
            )
        );
    }

    @Override
    protected void createBlockStateDefinition(
        StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(
            BlockStateProperties.HORIZONTAL_FACING,
            context.getHorizontalDirection().getOpposite()
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

}
