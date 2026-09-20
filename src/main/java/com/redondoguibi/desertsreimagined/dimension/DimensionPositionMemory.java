package com.redondoguibi.desertsreimagined.dimension;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DimensionPositionMemory {
    private static final Map<UUID, BlockPos> desertPositions = new HashMap<>();

    public static void saveDesertPosition(Player player, BlockPos pos) {
        desertPositions.put(player.getUUID(), pos);
    }

    public static BlockPos getDesertPosition(Player player) {
        return desertPositions.getOrDefault(player.getUUID(), new BlockPos(0, 64, 0));
    }

    public static void clear(Player player) {
        desertPositions.remove(player.getUUID());
    }
}
