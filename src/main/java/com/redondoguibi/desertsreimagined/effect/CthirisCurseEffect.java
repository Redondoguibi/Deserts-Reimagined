package com.redondoguibi.desertsreimagined.effect;

import com.redondoguibi.desertsreimagined.damage.ModDamageTypes;
import com.redondoguibi.desertsreimagined.registry.ModAttachments;
import com.redondoguibi.desertsreimagined.registry.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * Maldicao de C'Thiris.
 * Duracao infinita, acumula niveis. Ao atingir NIVEL 6 (amplifier 5):
 *  - aplica 60 de dano mitigavel por armadura
 *  - remove o efeito
 *  - impede regeneracao de vida por 15s (300 ticks)
 * Nao pode ser curada com leite (curative items vazio).
 */
public class CthirisCurseEffect extends MobEffect {

    public static final int TRIGGER_AMPLIFIER = 5; // nivel 6
    public static final float TRIGGER_DAMAGE = 60.0F;
    public static final int NO_REGEN_TICKS = 300;

    public CthirisCurseEffect() {
        super(MobEffectCategory.HARMFUL, 0xC6B283);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide && amplifier >= TRIGGER_AMPLIFIER) {
            detonate(entity);
            return false;
        }
        return true;
    }

    public static void detonate(LivingEntity entity) {
        if (entity.level().isClientSide) return;
        entity.removeEffect(ModEffects.curse());
        entity.setData(ModAttachments.NO_REGEN_TICKS.get(), NO_REGEN_TICKS);
        entity.invulnerableTime = 0;
        entity.hurt(ModDamageTypes.curse(entity.level()), TRIGGER_DAMAGE);
    }
}
