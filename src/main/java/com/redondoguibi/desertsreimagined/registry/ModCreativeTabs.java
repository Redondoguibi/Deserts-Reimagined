package com.redondoguibi.desertsreimagined.registry;

import com.redondoguibi.desertsreimagined.DesertsReimagined;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DesertsReimagined.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DESERTS_TAB =
            TABS.register("deserts_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.desertsreimagined.deserts_tab"))
                    // Icone = runic_sandstone
                    .icon(() -> new ItemStack(ModItems.RUNIC_SANDSTONE.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.RUNIC_SANDSTONE.get());
                        output.accept(ModItems.ESSENCE_OF_CURSE.get());
                        output.accept(ModItems.ALGATHOR_STATUE.get());
                        output.accept(ModItems.MUMMY_SPAWN_EGG.get());
                        output.accept(ModItems.CTHIRIS_GOD_SPAWN_EGG.get());
                    })
                    .build()
            );
}
