package com.redondoguibi.desertsreimagined.dimension;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelStem;
import net.minecraft.world.level.dimension.LevelStem;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

public class ModLevelStems {
    public static final DeferredRegister<LevelStem> LEVEL_STEMS = DeferredRegister.create(Registries.LEVEL_STEM, "desertsreimagined");

    public static final DeferredHolder<LevelStem> CTHIRIS_LEVEL_STEM = LEVEL_STEMS.register("cthiris",
            () -> new LevelStem(
                    ModDimensions.CTHIRIS_DIMENSION_TYPE.getHolder().orElseThrow(),
                    new net.minecraft.world.level.dimension.LevelStem() {
                        @Override
                        public long getSeed() {
                            return 0L;
                        }
                    }
            )
    );
}
