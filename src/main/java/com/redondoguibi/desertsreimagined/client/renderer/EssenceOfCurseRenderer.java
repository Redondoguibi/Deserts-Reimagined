package com.redondoguibi.desertsreimagined.client.renderer;

import com.redondoguibi.desertsreimagined.item.EssenceOfCurseItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class EssenceOfCurseRenderer extends GeoItemRenderer<EssenceOfCurseItem> {
    public EssenceOfCurseRenderer() {
        super(new EssenceOfCurseModel());
    }
}
