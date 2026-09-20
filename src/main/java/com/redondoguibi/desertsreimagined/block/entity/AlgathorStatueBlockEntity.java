package com.redondoguibi.desertsreimagined.block.entity;

import com.redondoguibi.desertsreimagined.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AlgathorStatueBlockEntity extends BlockEntity {
    public AlgathorStatueBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ALGATHOR_STATUE.get(), pos, state);
    }
}