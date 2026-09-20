package com.redondoguibi.desertsreimagined.registry;

import com.redondoguibi.desertsreimagined.DesertsReimagined;
import com.redondoguibi.desertsreimagined.block.AlgathorStatueBlock;
import com.redondoguibi.desertsreimagined.block.AlgathorStatuePartBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(DesertsReimagined.MODID);

    public static final DeferredBlock<Block> RUNIC_SANDSTONE = BLOCKS.registerSimpleBlock(
            "runic_sandstone",
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(0.8F)
                    .sound(SoundType.STONE)
    );

    public static final DeferredBlock<AlgathorStatueBlock> ALGATHOR_STATUE = BLOCKS.register("algathor_statue",
            () -> new AlgathorStatueBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SAND)
                    .strength(3.0F)
                    .sound(SoundType.STONE)
                    .noLootTable()
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .pushReaction(PushReaction.BLOCK)));

    public static final DeferredBlock<AlgathorStatuePartBlock> ALGATHOR_STATUE_PART =
            BLOCKS.register("algathor_statue_part",
                    () -> new AlgathorStatuePartBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.SAND)
                            .strength(3.0F)
                            .sound(SoundType.STONE)
                            .noLootTable()
                            .requiresCorrectToolForDrops()
                            .noOcclusion()
                            .pushReaction(PushReaction.BLOCK)));
}