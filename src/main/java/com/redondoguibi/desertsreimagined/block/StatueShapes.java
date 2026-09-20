package com.redondoguibi.desertsreimagined.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Colisão da estátua de Al'Gathor.
 *
 * Espaço LOCAL, em pixels. Origem = canto do bloco "core" no lado -right / -forward.
 *   X : 0..32   -> da esquerda para a direita da estátua
 *   Y : 0..80   -> altura, 0 = base do pedestal
 *   Z : 0..64   -> profundidade; 0 = atrás (ponta da cauda), 64 = frente (focinho)
 *
 * O bloco core ocupa X[0,16] Z[32,48], por isso as células vão de x=-1..0 e z=-2..1.
 */
public final class StatueShapes {

    private StatueShapes() {}

    /** Deslocamento aplicado para que o eixo local comece em zero. */
    private static final double OX = 16.0; // metade da largura
    private static final double OZ = 32.0; // deslocamento da cauda

    /**
     * Caixas em pixels, coordenadas do Blockbench convertidas:
     *   x igual ao do modelo (+OX), y = 24 - yModelo (invertido), z = -zModelo (+OZ)
     */
    private static final double[][] BOXES = {
            // { x1, y1, z1, x2, y2, z2 }
            { -16,  0, -16,  16, 15,  16 },   // pedestal
            { -11, 15, -10,  11, 34,  10 },   // pernas + kilt
            { -12, 34, -11,  12, 52,  11 },   // torso
            { -16, 34,  -6,  16, 52,   6 },   // braços
            { -11, 52, -11,  11, 79,  10 },   // cabeça + nemes
            {  -6, 53,  10,   6, 62,  22 },   // focinho
            {  -4, 14, -30,   4, 30,  -9 },   // cauda
    };

    private static final int MIN_X = -1, MAX_X = 0;
    private static final int MIN_Z = -2, MAX_Z = 1;
    private static final int MIN_Y =  0, MAX_Y = 4;

    public static final List<Vec3i> CELLS;

    private static final Map<Long, VoxelShape> SHAPE_CACHE = new HashMap<>();

    static {
        List<Vec3i> cells = new ArrayList<>();
        for (int y = MIN_Y; y <= MAX_Y; y++)
            for (int x = MIN_X; x <= MAX_X; x++)
                for (int z = MIN_Z; z <= MAX_Z; z++)
                    if (hasGeometry(x, y, z)) cells.add(new Vec3i(x, y, z));
        CELLS = List.copyOf(cells);
    }

    private static boolean hasGeometry(int cx, int cy, int cz) {
        for (double[] b : BOXES) if (clip(b, cx, cy, cz) != null) return true;
        return false;
    }

    /** Recorta a caixa dentro da célula. Retorna coords 0..16 locais à célula. */
    private static double[] clip(double[] b, int cx, int cy, int cz) {
        double ox = cx * 16.0, oy = cy * 16.0, oz = cz * 16.0;
        double x1 = Math.max(b[0], ox) - ox;
        double y1 = Math.max(b[1], oy) - oy;
        double z1 = Math.max(b[2], oz) - oz;
        double x2 = Math.min(b[3], ox + 16.0) - ox;
        double y2 = Math.min(b[4], oy + 16.0) - oy;
        double z2 = Math.min(b[5], oz + 16.0) - oz;
        if (x2 - x1 <= 0.05 || y2 - y1 <= 0.05 || z2 - z1 <= 0.05) return null;
        return new double[]{ x1, y1, z1, x2, y2, z2 };
    }

    /**
     * Converte a caixa recortada (coords locais 0..16 da célula) para coords do bloco 0..16
     * no espaço do mundo, considerando o FACING.
     * Local +X = right(facing); Local +Z = facing.
     */
    private static VoxelShape orient(Direction facing, double[] c) {
        Direction right = facing.getClockWise();

        double u1 = c[0], w1 = c[2], u2 = c[3], w2 = c[5];

        double ax1 = 8 + (u1 - 8) * right.getStepX() + (w1 - 8) * facing.getStepX();
        double ax2 = 8 + (u2 - 8) * right.getStepX() + (w2 - 8) * facing.getStepX();
        double az1 = 8 + (u1 - 8) * right.getStepZ() + (w1 - 8) * facing.getStepZ();
        double az2 = 8 + (u2 - 8) * right.getStepZ() + (w2 - 8) * facing.getStepZ();

        return Block.box(
                Math.min(ax1, ax2), c[1], Math.min(az1, az2),
                Math.max(ax1, ax2), c[4], Math.max(az1, az2));
    }

    public static VoxelShape shapeFor(Direction facing, Vec3i cell) {
        long key = ((long) facing.get2DDataValue() << 32)
                | ((cell.getX() + 8L) << 20)
                | ((cell.getY() + 8L) << 10)
                | (cell.getZ() + 8L);

        return SHAPE_CACHE.computeIfAbsent(key, k -> {
            VoxelShape shape = Shapes.empty();
            for (double[] b : BOXES) {
                double[] c = clip(b, cell.getX(), cell.getY(), cell.getZ());
                if (c != null) shape = Shapes.joinUnoptimized(shape, orient(facing, c), BooleanOp.OR);
            }
            return shape.optimize();
        });
    }

    public static BlockPos worldPos(BlockPos core, Direction facing, Vec3i cell) {
        Direction right = facing.getClockWise();
        int dx = right.getStepX() * cell.getX() + facing.getStepX() * cell.getZ();
        int dz = right.getStepZ() * cell.getX() + facing.getStepZ() * cell.getZ();
        return core.offset(dx, cell.getY(), dz);
    }

    public static Vec3i localCell(BlockPos core, Direction facing, BlockPos pos) {
        BlockPos d = pos.subtract(core);
        Direction right = facing.getClockWise();
        int x = d.getX() * right.getStepX() + d.getZ() * right.getStepZ();
        int z = d.getX() * facing.getStepX() + d.getZ() * facing.getStepZ();
        return new Vec3i(x, d.getY(), z);
    }
}