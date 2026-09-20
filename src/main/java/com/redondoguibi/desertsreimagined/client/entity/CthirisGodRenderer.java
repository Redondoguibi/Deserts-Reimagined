package com.redondoguibi.desertsreimagined.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.redondoguibi.desertsreimagined.entity.boss.CthirisGodEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Renderer do C'Thiris.
 *
 * ================= POR QUE ESTE RENDERER E CUSTOMIZADO =================
 * O LivingEntityRenderer (herdado pelo GeoEntityRenderer) aplica dois efeitos
 * automaticos que ARRUINAM a animacao exclusiva de morte do boss:
 *
 *  1) FLASH VERMELHO -> vem de getOverlayCoords(), que consulta hurtTime.
 *  2) TOMBAR DE LADO -> vem de setupRotations()/applyRotations(), que aplica
 *     getFlipDegrees() enquanto deathTime > 0.
 *
 * Nao podemos simplesmente impedir deathTime de crescer: CthirisGodEntity#tickDeath()
 * usa deathTime para cronometrar os 96 ticks da animacao de morte.
 *
 * SOLUCAO: mascaramos os campos apenas durante a chamada de rotacao.
 * deathTime e hurtTime sao public em LivingEntity; salvamos, zeramos, chamamos o
 * super e restauramos no mesmo frame. Isso e imune a mudancas de assinatura entre
 * versoes do GeckoLib (nao reimplementamos applyRotations na mao).
 * =======================================================================
 */
public class CthirisGodRenderer extends GeoEntityRenderer<CthirisGodEntity> {

    public CthirisGodRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new CthirisGodModel());
        this.shadowRadius = 1.4f;
    }

    // ================= 1) REMOVE O FLASH VERMELHO =================

    /**
     * NO_OVERLAY desliga permanentemente a camada vermelha de dano.
     * A reacao visual a dano fica 100% a cargo da animacao
     * "animation.cthiris.hurt" (GeckoLib), controlada por hurtAnimTick.
     */
    @Override
    public int getPackedOverlay(CthirisGodEntity animatable, float u, float partialTick) {
        return OverlayTexture.NO_OVERLAY;
    }

    // ================= 2) REMOVE O TOMBO DE MORTE =================

    @Override
    protected void applyRotations(CthirisGodEntity animatable, PoseStack poseStack,
                                  float ageInTicks, float rotationYaw,
                                  float partialTick, float nativeScale) {
        int savedDeathTime = animatable.deathTime;
        int savedHurtTime  = animatable.hurtTime;

        // Mascara os campos que causam o flip e o tremor
        animatable.deathTime = 0;
        animatable.hurtTime  = 0;
        try {
            super.applyRotations(animatable, poseStack, ageInTicks,
                    rotationYaw, partialTick, nativeScale);
        } finally {
            // Restaura SEMPRE, mesmo se o super lancar excecao
            animatable.deathTime = savedDeathTime;
            animatable.hurtTime  = savedHurtTime;
        }
    }

    // ================= Render type =================

    @Override
    public RenderType getRenderType(CthirisGodEntity animatable, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }
}