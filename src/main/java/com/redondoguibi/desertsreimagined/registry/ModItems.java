package com.redondoguibi.desertsreimagined.registry;

import com.redondoguibi.desertsreimagined.DesertsReimagined;
import com.redondoguibi.desertsreimagined.item.EssenceOfCurseItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(DesertsReimagined.MODID);

    // ---- Materiais ----
    public static final DeferredItem<Item> FIERY_RUBY =
            ITEMS.registerSimpleItem("fiery_ruby", new Item.Properties().fireResistant());

    // Item do bloco runic_sandstone
    public static final DeferredItem<BlockItem> RUNIC_SANDSTONE =
            ITEMS.registerSimpleBlockItem("runic_sandstone", ModBlocks.RUNIC_SANDSTONE);

    // Item animado via GeckoLib
    public static final DeferredItem<EssenceOfCurseItem> ESSENCE_OF_CURSE =
            ITEMS.register("essence_of_curse",
                    () -> new EssenceOfCurseItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<BlockItem> ALGATHOR_STATUE =
            ITEMS.registerSimpleBlockItem("algathor_statue", ModBlocks.ALGATHOR_STATUE);

    // ---- Spawn Eggs ----
    public static final DeferredItem<DeferredSpawnEggItem> MUMMY_SPAWN_EGG =
            ITEMS.register("mummy_spawn_egg", () -> new DeferredSpawnEggItem(
                    ModEntities.MUMMY, 0x8C744E, 0xD5C7A2, new Item.Properties()));

    public static final DeferredItem<DeferredSpawnEggItem> CTHIRIS_GOD_SPAWN_EGG =
            ITEMS.register("cthiris_god_spawn_egg", () -> new DeferredSpawnEggItem(
                    ModEntities.CTHIRIS_GOD, 0x7A5A2E, 0xAD9C78, new Item.Properties()));
}