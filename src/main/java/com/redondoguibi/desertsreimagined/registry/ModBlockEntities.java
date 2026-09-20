package com.redondoguibi.desertsreimagined.registry;

import com.redondoguibi.desertsreimagined.DesertsReimagined;
import com.redondoguibi.desertsreimagined.block.entity.AlgathorStatuePartBlockEntity;
import com.redondoguibi.desertsreimagined.block.entity.AlgathorStatueBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, DesertsReimagined.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlgathorStatueBlockEntity>>
            ALGATHOR_STATUE = BLOCK_ENTITIES.register("algathor_statue",
            () -> BlockEntityType.Builder.of(
                    AlgathorStatueBlockEntity::new,
                    ModBlocks.ALGATHOR_STATUE.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlgathorStatuePartBlockEntity>>
            ALGATHOR_STATUE_PART = BLOCK_ENTITIES.register("algathor_statue_part",
            () -> BlockEntityType.Builder.of(
                    AlgathorStatuePartBlockEntity::new,
                    ModBlocks.ALGATHOR_STATUE_PART.get()
            ).build(null));
}