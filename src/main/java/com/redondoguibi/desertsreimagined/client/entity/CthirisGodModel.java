package com.redondoguibi.desertsreimagined.client.entity;

import com.redondoguibi.desertsreimagined.DesertsReimagined;
import com.redondoguibi.desertsreimagined.entity.boss.CthirisGodEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CthirisGodModel extends GeoModel<CthirisGodEntity> {

    @Override
    public ResourceLocation getModelResource(CthirisGodEntity e) {
        return ResourceLocation.fromNamespaceAndPath(
                DesertsReimagined.MODID, "geo/entity/cthiris_god.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CthirisGodEntity e) {
        return ResourceLocation.fromNamespaceAndPath(
                DesertsReimagined.MODID, "textures/entity/cthiris_god.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CthirisGodEntity e) {
        return ResourceLocation.fromNamespaceAndPath(
                DesertsReimagined.MODID, "animations/entity/cthiris_god.animation.json");
    }
}
