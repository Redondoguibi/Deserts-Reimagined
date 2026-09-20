package com.redondoguibi.desertsreimagined.client.gui;

import com.redondoguibi.desertsreimagined.DesertsReimagined;
import com.redondoguibi.desertsreimagined.entity.boss.CthirisGodEntity;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.List;

/**
 * Bossbar customizada do C'Thiris.
 * A bossbar vanilla NAO e usada (a entidade nao possui ServerBossEvent).
 *
 * =============== GUIA DE CALIBRACAO ===============
 * SCALE   -> tamanho da barra na tela. 1 = tamanho real (128x32 px). 0.75 = 25% menor.
 * TOP_Y   -> distancia do topo da tela em pixels.
 * FILL_X  -> coluna (em px da textura) onde a area de vida COMECA.
 * FILL_W  -> largura (em px da textura) da area de vida.
 *
 * Para calibrar FILL_X/FILL_W: abra cthiris_god_bossbar_full.png num editor,
 * localize a primeira e a ultima coluna que contem #c6b283 ou #decba1.
 *   FILL_X = primeira coluna
 *   FILL_W = (ultima coluna - primeira coluna) + 1
 * ==================================================
 */
public class CthirisBossBarLayer implements LayeredDraw.Layer {

    private static final ResourceLocation EMPTY = ResourceLocation.fromNamespaceAndPath(
            DesertsReimagined.MODID, "textures/bossbar/cthiris_god_bossbar.png");
    private static final ResourceLocation FULL = ResourceLocation.fromNamespaceAndPath(
            DesertsReimagined.MODID, "textures/bossbar/cthiris_god_bossbar_full.png");

    // ---- Dimensoes da textura (nao mexer, sao os 128x32 reais) ----
    private static final int TEX_W = 128;
    private static final int TEX_H = 32;

    // ---- AJUSTES VISUAIS ----
    /** Escala da barra. 1.0 = 128x32 na tela. Diminua para uma barra menor. */
    private static final float SCALE = 0.85F;
    /** Distancia do topo da tela, em pixels. 2 = praticamente colada no topo. */
    private static final int TOP_Y = 2;

    // ---- CALIBRACAO DA AREA DE VIDA (px da textura) ----
    private static final int FILL_X = 14;
    private static final int FILL_Y = 0;
    private static final int FILL_W = 100;
    private static final int FILL_H = 32;

    private static final double VIEW_RANGE = 48.0D;

    private float smoothed = -1.0F;

    @Override
    public void render(GuiGraphics gui, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.options.hideGui) return;

        List<CthirisGodEntity> list = mc.level.getEntitiesOfClass(
                CthirisGodEntity.class,
                mc.player.getBoundingBox().inflate(VIEW_RANGE),
                e -> e.getHealth() > 0.0F && !e.isRemoved());

        if (list.isEmpty()) {
            smoothed = -1.0F;
            return;
        }

        CthirisGodEntity boss = list.get(0);
        float pct = Mth.clamp(boss.getHealth() / boss.getMaxHealth(), 0.0F, 1.0F);
        smoothed = (smoothed < 0.0F) ? pct : Mth.lerp(0.15F, smoothed, pct);

        int fillPx = Mth.ceil(FILL_W * smoothed);

        // Centraliza considerando a escala
        int x = Math.round((gui.guiWidth() - TEX_W * SCALE) / 2.0F / SCALE);
        int y = Math.round(TOP_Y / SCALE);

        var pose = gui.pose();
        pose.pushPose();
        pose.scale(SCALE, SCALE, 1.0F);

        // 1) barra vazia (base completa)
        gui.blit(EMPTY, x, y, 0, 0, TEX_W, TEX_H, TEX_W, TEX_H);

        // 2) preenchimento recortado da textura cheia
        if (fillPx > 0) {
            gui.blit(FULL,
                    x + FILL_X, y + FILL_Y,   // destino na tela
                    fillPx, FILL_H,           // tamanho no destino
                    FILL_X, FILL_Y,           // origem na textura (u, v)
                    fillPx, FILL_H,           // tamanho da fatia na textura
                    TEX_W, TEX_H);            // dimensoes totais da textura
        }

        pose.popPose();

        // Nome do boss removido conforme solicitado.
    }
}