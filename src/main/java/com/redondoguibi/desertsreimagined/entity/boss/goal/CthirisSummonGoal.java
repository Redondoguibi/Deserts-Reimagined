package com.redondoguibi.desertsreimagined.entity.boss.goal;

import com.redondoguibi.desertsreimagined.entity.MummyEntity;
import com.redondoguibi.desertsreimagined.entity.boss.CthirisAction;
import com.redondoguibi.desertsreimagined.entity.boss.CthirisGodEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class CthirisSummonGoal extends Goal {

    private final CthirisGodEntity boss;

    public CthirisSummonGoal(CthirisGodEntity boss) {
        this.boss = boss;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity t = boss.getTarget();
        if (t == null || !t.isAlive()) return false;
        if (!boss.canStart(CthirisAction.SUMMON)) return false;

        // Só invoca se o player NAO estiver perto
        if (boss.distanceTo(t) <= CthirisGodEntity.CHARGED_RANGE) return false;

        int cap = boss.isPhaseTwo() ? 6 : 4;
        int alive = boss.level().getEntitiesOfClass(MummyEntity.class,
                boss.getBoundingBox().inflate(32.0D),
                m -> m.isAlive() && boss.getUUID().equals(m.getBossOwner())).size();
        return alive < cap;
    }

    @Override
    public void start() {
        boss.startAction(CthirisAction.SUMMON);
        boss.getNavigation().stop();
    }

    @Override
    public boolean canContinueToUse() {
        return boss.getAction() == CthirisAction.SUMMON;
    }

    @Override
    public void tick() {
        boss.getNavigation().stop();
    }
}
