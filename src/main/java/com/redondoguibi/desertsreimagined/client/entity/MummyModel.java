package com.redondoguibi.desertsreimagined.client.entity;

import com.redondoguibi.desertsreimagined.DesertsReimagined;
import com.redondoguibi.desertsreimagined.entity.MummyEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MummyModel extends GeoModel<MummyEntity> {
    @Override
    public ResourceLocation getModelResource(MummyEntity e) {
        return ResourceLocation.fromNamespaceAndPath(DesertsReimagined.MODID, "geo/entity/mummy.geo.json");
    }
    @Override
    public ResourceLocation getTextureResource(MummyEntity e) {
        return ResourceLocation.fromNamespaceAndPath(DesertsReimagined.MODID, "textures/entity/mummy.png");
    }
    @Override
    public ResourceLocation getAnimationResource(MummyEntity e) {
        return ResourceLocation.fromNamespaceAndPath(DesertsReimagined.MODID, "animations/entity/mummy.animation.json");
    }
}
