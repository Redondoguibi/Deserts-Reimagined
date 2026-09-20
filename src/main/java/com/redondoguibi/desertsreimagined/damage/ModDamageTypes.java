package com.redondoguibi.desertsreimagined.damage;

import com.redondoguibi.desertsreimagined.DesertsReimagined;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

public class ModDamageTypes {

    public static final ResourceKey<DamageType> CTHIRIS_CURSE = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(DesertsReimagined.MODID, "cthiris_curse"));

    /** Dano da maldicao: mitigavel por armadura (nao possui a tag bypasses_armor). */
    public static DamageSource curse(Level level) {
        return new DamageSource(level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(CTHIRIS_CURSE));
    }
}
