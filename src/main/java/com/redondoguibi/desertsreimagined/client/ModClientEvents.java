package com.redondoguibi.desertsreimagined.client;

import com.redondoguibi.desertsreimagined.DesertsReimagined;
import com.redondoguibi.desertsreimagined.client.block.AlgathorStatueRenderer;
import com.redondoguibi.desertsreimagined.client.entity.CthirisGodRenderer;
import com.redondoguibi.desertsreimagined.client.entity.MummyRenderer;
import com.redondoguibi.desertsreimagined.client.gui.CthirisBossBarLayer;
import com.redondoguibi.desertsreimagined.client.model.AlgathorStatueModel;
import com.redondoguibi.desertsreimagined.registry.ModBlockEntities;
import com.redondoguibi.desertsreimagined.registry.ModEntities;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;


@EventBusSubscriber(modid = DesertsReimagined.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.MUMMY.get(), MummyRenderer::new);
        event.registerEntityRenderer(ModEntities.CTHIRIS_GOD.get(), CthirisGodRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.ALGATHOR_STATUE.get(), AlgathorStatueRenderer::new);

    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(AlgathorStatueModel.LAYER_LOCATION, AlgathorStatueModel::createBodyLayer);
    }
    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(
                ResourceLocation.fromNamespaceAndPath(DesertsReimagined.MODID, "cthiris_bossbar"),
                new CthirisBossBarLayer());
    }
}
