package com.redondoguibi.desertsreimagined.dimension;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.OptionalLong;

public class ModDimensions {
    public static final DeferredRegister<DimensionType> DIMENSION_TYPES = DeferredRegister.create(Registries.DIMENSION_TYPE, "desertsreimagined");

    public static final DeferredHolder<DimensionType> CTHIRIS_DIMENSION_TYPE = DIMENSION_TYPES.register("cthiris",
            () -> new DimensionType(
                    OptionalLong.of(18000L), // fixed time
                    false, // no skylight
                    true,  // ceiling
                    false, // ultrawarm
                    1.0,   // coordinate scale
                    true,  // bed works
                    false, // respawn anchor works
                    0,     // min y
                    384,   // height
                    384,   // logical height
                    ModDimensionTags.CTHIRIS_BLOCK_TAG,
                    ResourceKey.create(Registries.DIMENSION, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("desertsreimagined", "cthiris")),
                    0.0f  // ambient light
            )
    );
}
