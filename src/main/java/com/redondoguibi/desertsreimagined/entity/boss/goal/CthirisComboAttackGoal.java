package com.redondoguibi.desertsreimagined.entity.boss.goal;

import com.redondoguibi.desertsreimagined.entity.boss.CthirisAction;
import com.redondoguibi.desertsreimagined.entity.boss.CthirisGodEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Ataque combo (35t, dano nos ticks 7 e 16).
 *
 * IMPORTANTE: nao declara NENHUMA flag.
 * Se declarasse LOOK ou MOVE, bloquearia o CthirisMoveToTargetGoal
 * (prioridade maior) e o boss ficaria parado durante a luta.
 * A rotacao e feita via getLookControl(), que dispensa flag.
 */
public class CthirisComboAttackGoal extends Goal {

    private final CthirisGodEntity boss;

    public CthirisComboAttackGoal(CthirisGodEntity boss) {
        this.boss = boss;
        this.setFlags(EnumSet.noneOf(Flag.class));
    }

    @Override
    public boolean canUse() {
        LivingEntity t = boss.getTarget();
        if (t == null || !t.isAlive()) return false;
        if (!boss.canStart(CthirisAction.COMBO)) return false;
        return boss.distanceTo(t) <= CthirisGodEntity.COMBO_RANGE + t.getBbWidth() / 2.0F;
    }

    @Override
    public void start() {
        boss.startAction(CthirisAction.COMBO);
    }

    @Override
    public boolean canContinueToUse() {
        return boss.getAction() == CthirisAction.COMBO;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity t = boss.getTarget();
        // Encara o alvo apenas no windup (primeiros 8 ticks); depois trava a rotacao
        if (t != null && boss.getActionTick() <= 8) {
            boss.getLookControl().setLookAt(t, 40.0F, 40.0F);
            boss.setYRot(boss.getYHeadRot());
            boss.yBodyRot = boss.getYRot();
        }
    }
}