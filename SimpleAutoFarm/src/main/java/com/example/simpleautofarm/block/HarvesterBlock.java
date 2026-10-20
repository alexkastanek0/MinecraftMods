package com.example.simpleautofarm.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;

import com.example.simpleautofarm.blockentity.HarvesterBlockEntity;

public class HarvesterBlock extends MachineBlock implements EntityBlock {
    public HarvesterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HarvesterBlockEntity(pos, state);
    }
}
