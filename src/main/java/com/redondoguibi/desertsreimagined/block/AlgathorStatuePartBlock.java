package com.redondoguibi.desertsreimagined.block;

import com.mojang.serialization.MapCodec;
import com.redondoguibi.desertsreimagined.block.entity.AlgathorStatuePartBlockEntity;
import com.redondoguibi.desertsreimagined.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class AlgathorStatuePartBlock extends HorizontalDirectionalBlock implements EntityBlock {

    public static final MapCodec<AlgathorStatuePartBlock> CODEC = simpleCodec(AlgathorStatuePartBlock::new);

    public AlgathorStatuePartBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AlgathorStatuePartBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        Direction facing = state.getValue(FACING);
        BlockPos core = getCore(level, pos);
        if (core == null) return Shapes.empty();
        Vec3i cell = StatueShapes.localCell(core, facing, pos);
        return StatueShapes.shapeFor(facing, cell);
    }

    private static @Nullable BlockPos getCore(BlockGetter level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof AlgathorStatuePartBlockEntity part
                ? part.getCorePos() : null;
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            BlockPos core = getCore(level, pos);
            if (core != null) {
                AlgathorStatueBlock.destroyStructure(level, core, state.getValue(FACING), !player.isCreative());
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(ModItems.ALGATHOR_STATUE.get());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}