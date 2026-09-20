package com.redondoguibi.desertsreimagined.client.entity;

import com.redondoguibi.desertsreimagined.entity.MummyEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MummyRenderer extends GeoEntityRenderer<MummyEntity> {
    public MummyRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new MummyModel());
        this.shadowRadius = 0.5f;
    }
    @Override
    public RenderType getRenderType(MummyEntity animatable, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
