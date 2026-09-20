package com.redondoguibi.desertsreimagined.dimension;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class CthirisStructureLoader {
    public static void loadStructure(ServerLevel level) {
        if (level.dimension().equals(ModDimensions.CTHIRIS_DIMENSION_TYPE.getKey())) {
            BlockPos spawn = new BlockPos(0, 0, 0);
            try {
                // Carrega o arquivo .nbt da estrutura
                Path nbtPath = Path.of("cthiris_bossfight.nbt");
                if (Files.exists(nbtPath)) {
                    CompoundTag nbt = net.minecraft.nbt.NbtIo.readCompressed(Files.newInputStream(nbtPath), HolderLookup.create(Registries.COMPOUND));
                    StructureTemplate template = new StructureTemplate();
                    template.load(level.registryAccess(), nbt);
                    template.placeInWorld(level, spawn, spawn, new StructureTemplate.StructurePlaceSettings(), level.getRandom(), Block.UPDATE_ALL);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
