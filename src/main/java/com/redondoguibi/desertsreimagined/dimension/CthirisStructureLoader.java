package com.redondoguibi.desertsreimagined.dimension;

import com.redondoguibi.desertsreimagined.DesertsReimagined;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.nio.file.Files;
import java.nio.file.Path;

public final class CthirisStructureLoader {

    private static final Path STRUCTURE_PATH = Path.of("cthiris_bossfight.nbt");

    private CthirisStructureLoader() {
    }

    public static void loadStructure(ServerLevel level) {
        if (!level.dimension().equals(ModDimensions.CTHIRIS_LEVEL)) {
            return;
        }

        if (!Files.exists(STRUCTURE_PATH)) {
            DesertsReimagined.LOGGER.warn(
                    "C'Thiris arena structure was not found at {}",
                    STRUCTURE_PATH.toAbsolutePath()
            );
            return;
        }

        BlockPos spawn = BlockPos.ZERO;

        try {
            CompoundTag nbt = NbtIo.readCompressed(
                    STRUCTURE_PATH,
                    NbtAccounter.unlimitedHeap()
            );

            StructureTemplate template = new StructureTemplate();
            template.load(
                    level.registryAccess().lookupOrThrow(Registries.BLOCK),
                    nbt
            );

            template.placeInWorld(
                    level,
                    spawn,
                    spawn,
                    new StructurePlaceSettings(),
                    level.getRandom(),
                    Block.UPDATE_ALL
            );
        } catch (Exception exception) {
            DesertsReimagined.LOGGER.error("Failed to load C'Thiris arena structure", exception);
        }
    }
}
