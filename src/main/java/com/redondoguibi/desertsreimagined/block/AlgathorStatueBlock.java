package com.redondoguibi.desertsreimagined.block;

import com.mojang.serialization.MapCodec;
import com.redondoguibi.desertsreimagined.block.entity.AlgathorStatuePartBlockEntity;
import com.redondoguibi.desertsreimagined.block.entity.AlgathorStatueBlockEntity;
import com.redondoguibi.desertsreimagined.registry.ModBlocks;
import com.redondoguibi.desertsreimagined.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class AlgathorStatueBlock extends HorizontalDirectionalBlock implements EntityBlock {

    public static final MapCodec<AlgathorStatueBlock> CODEC = simpleCodec(AlgathorStatueBlock::new);

    private static final Vec3i CORE_CELL = new Vec3i(0, 0, 0);

    public AlgathorStatueBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AlgathorStatueBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE; // quem desenha é o BlockEntityRenderer
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return StatueShapes.shapeFor(state.getValue(FACING), CORE_CELL);
    }

    // ---------------- colocação ----------------

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        if (!hasRoom(context.getLevel(), context.getClickedPos(), facing)) return null;
        return this.defaultBlockState().setValue(FACING, facing);
    }

    private static boolean hasRoom(LevelReader level, BlockPos core, Direction facing) {
        for (Vec3i cell : StatueShapes.CELLS) {
            if (cell.equals(CORE_CELL)) continue;
            BlockPos p = StatueShapes.worldPos(core, facing, cell);
            if (!level.getBlockState(p).canBeReplaced()) return false;
            if (level.isOutsideBuildHeight(p)) return false;
        }
        return true;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.isClientSide()) return;

        Direction facing = state.getValue(FACING);
        BlockState partState = ModBlocks.ALGATHOR_STATUE_PART.get().defaultBlockState()
                .setValue(AlgathorStatuePartBlock.FACING, facing);

        for (Vec3i cell : StatueShapes.CELLS) {
            if (cell.equals(CORE_CELL)) continue;
            BlockPos p = StatueShapes.worldPos(pos, facing, cell);
            level.setBlock(p, partState, Block.UPDATE_ALL);
            if (level.getBlockEntity(p) instanceof AlgathorStatuePartBlockEntity part) {
                part.setCorePos(pos);
            }
        }
    }

    // ---------------- destruição ----------------

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            destroyStructure(level, pos, state.getValue(FACING), !player.isCreative());
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    /** Remove todas as partes. Chamado também pelas partes. */
    public static void destroyStructure(Level level, BlockPos core, Direction facing, boolean drop) {
        for (Vec3i cell : StatueShapes.CELLS) {
            BlockPos p = StatueShapes.worldPos(core, facing, cell);
            BlockState s = level.getBlockState(p);
            if (s.getBlock() instanceof AlgathorStatuePartBlock
                    || s.getBlock() instanceof AlgathorStatueBlock) {
                level.setBlock(p, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
            }
        }
        if (drop) {
            popResource(level, core, new ItemStack(ModItems.ALGATHOR_STATUE.get()));
        }
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(ModItems.ALGATHOR_STATUE.get());
    }

    // ---------------- rotação ----------------

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