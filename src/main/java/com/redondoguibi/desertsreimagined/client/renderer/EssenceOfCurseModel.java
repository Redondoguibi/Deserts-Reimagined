package com.redondoguibi.desertsreimagined.client.renderer;

import com.redondoguibi.desertsreimagined.DesertsReimagined;
import com.redondoguibi.desertsreimagined.item.EssenceOfCurseItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EssenceOfCurseModel extends GeoModel<EssenceOfCurseItem> {

    @Override
    public ResourceLocation getModelResource(EssenceOfCurseItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(
                DesertsReimagined.MODID, "geo/item/essence_of_curse.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EssenceOfCurseItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(
                DesertsReimagined.MODID, "textures/item/essence_of_curse.png");
    }

    @Override
    public ResourceLocation getAnimationResource(EssenceOfCurseItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(
                DesertsReimagined.MODID, "animations/item/essence_of_curse.animation.json");
    }
}
