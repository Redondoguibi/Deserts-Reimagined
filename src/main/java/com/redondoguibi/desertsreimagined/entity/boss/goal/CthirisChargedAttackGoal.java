package com.redondoguibi.desertsreimagined.entity.boss.goal;

import com.redondoguibi.desertsreimagined.entity.boss.CthirisAction;
import com.redondoguibi.desertsreimagined.entity.boss.CthirisGodEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.EnumSet;

public class CthirisChargedAttackGoal extends Goal {

    private final CthirisGodEntity boss;

    public CthirisChargedAttackGoal(CthirisGodEntity boss) {
        this.boss = boss;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity t = boss.getTarget();
        if (t == null || !t.isAlive()) return false;
        if (!boss.canStart(CthirisAction.CHARGED)) return false;
        return boss.distanceTo(t) <= CthirisGodEntity.CHARGED_RANGE;
    }

    @Override
    public void start() {
        boss.startAction(CthirisAction.CHARGED);
        boss.getNavigation().stop();
    }

    @Override
    public boolean canContinueToUse() {
        return boss.getAction() == CthirisAction.CHARGED;
    }

    @Override
    public void tick() {
        boss.getNavigation().stop();
        LivingEntity t = boss.getTarget();
        // Encara o alvo ate o fim do windup (tick 40); depois trava para o cone ser previsivel
        if (t != null && boss.getActionTick() <= 40) {
            boss.getLookControl().setLookAt(t, 30.0F, 30.0F);
            boss.setYRot(boss.getYHeadRot());
            boss.yBodyRot = boss.getYRot();
        }
    }
}