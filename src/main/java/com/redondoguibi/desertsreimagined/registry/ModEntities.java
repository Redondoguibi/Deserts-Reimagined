package com.redondoguibi.desertsreimagined.registry;

import com.redondoguibi.desertsreimagined.DesertsReimagined;
import com.redondoguibi.desertsreimagined.entity.MummyEntity;
import com.redondoguibi.desertsreimagined.entity.boss.CthirisGodEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, DesertsReimagined.MODID);

    public static final Supplier<EntityType<MummyEntity>> MUMMY =
            ENTITY_TYPES.register("mummy", () -> EntityType.Builder
                    .of(MummyEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f)            // largura x altura (igual zumbi)
                    .clientTrackingRange(10)
                    .build("mummy"));

    public static final Supplier<EntityType<CthirisGodEntity>> CTHIRIS_GOD =
            ENTITY_TYPES.register("cthiris_god", () -> EntityType.Builder
                    .of(CthirisGodEntity::new, MobCategory.MONSTER)
                    .sized(1.9f, 3.9f)             // ~2 x ~4 blocos
                    .fireImmune()
                    .clientTrackingRange(32)
                    .updateInterval(1)
                    .build("cthiris_god"));
}
