package com.redondoguibi.desertsreimagined.dimension;

import com.redondoguibi.desertsreimagined.DesertsReimagined;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

/**
 * Chaves da dimensão C'Thiris.
 *
 * A dimensão e o DimensionType são data-driven em 1.21.1 e são definidos em:
 * data/desertsreimagined/dimension/cthiris.json
 * data/desertsreimagined/dimension_type/cthiris.json
 */
public final class ModDimensions {

    public static final ResourceKey<Level> CTHIRIS_LEVEL = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(DesertsReimagined.MODID, "cthiris")
    );

    private ModDimensions() {
    }
}
