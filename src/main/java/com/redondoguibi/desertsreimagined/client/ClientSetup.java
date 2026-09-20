package com.redondoguibi.desertsreimagined.client;

import com.redondoguibi.desertsreimagined.DesertsReimagined;
import com.redondoguibi.desertsreimagined.client.renderer.EssenceOfCurseRenderer;
import com.redondoguibi.desertsreimagined.registry.ModItems;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = DesertsReimagined.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private EssenceOfCurseRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new EssenceOfCurseRenderer();
                }
                return renderer;
            }
        }, ModItems.ESSENCE_OF_CURSE.get());
    }
}
