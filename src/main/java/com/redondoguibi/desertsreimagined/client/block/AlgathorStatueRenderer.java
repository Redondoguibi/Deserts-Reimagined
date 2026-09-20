package com.redondoguibi.desertsreimagined.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.redondoguibi.desertsreimagined.DesertsReimagined;
import com.redondoguibi.desertsreimagined.block.entity.AlgathorStatueBlockEntity;
import com.redondoguibi.desertsreimagined.client.model.AlgathorStatueModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

public class AlgathorStatueRenderer implements BlockEntityRenderer<AlgathorStatueBlockEntity> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            DesertsReimagined.MODID, "textures/block/algathor_statue.png");

    private final AlgathorStatueModel model;

    public AlgathorStatueRenderer(BlockEntityRendererProvider.Context ctx) {
        this.model = new AlgathorStatueModel(ctx.bakeLayer(AlgathorStatueModel.LAYER_LOCATION));
    }

    @Override
    public void render(AlgathorStatueBlockEntity be, float partialTick, PoseStack pose,
                       MultiBufferSource buffers, int light, int overlay) {

        Direction facing = be.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        Direction right = facing.getClockWise();

        // canto do core no lado -right / -forward, igual ao usado na colisão
        double ox = 0.5 - 0.5 * right.getStepX() - 0.5 * facing.getStepX();
        double oz = 0.5 - 0.5 * right.getStepZ() - 0.5 * facing.getStepZ();

        pose.pushPose();
        // 1.5 compensa o PartPose.offset(0, 24, 0) da raiz do modelo
        pose.translate(ox, 1.5D, oz);
        pose.mulPose(Axis.YP.rotationDegrees(180.0F - facing.toYRot()));
        pose.scale(-1.0F, -1.0F, 1.0F); // convenção de modelo de entidade

        model.render(pose, buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)),
                light, OverlayTexture.NO_OVERLAY);
        pose.popPose();
    }

    @Override
    public int getViewDistance() {
        return 128;
    }

    @Override
    public boolean shouldRenderOffScreen(AlgathorStatueBlockEntity be) {
        return true;
    }
}