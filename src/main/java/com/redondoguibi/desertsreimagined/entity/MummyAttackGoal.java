package com.redondoguibi.desertsreimagined.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class MummyAttackGoal extends MeleeAttackGoal {

    private final MummyEntity mummy;

    public MummyAttackGoal(MummyEntity mob, double speed, boolean followWithoutLos) {
        super(mob, speed, followWithoutLos);
        this.mummy = mob;
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target) {
        if (this.isTimeToAttack()
                && this.mob.getSensing().hasLineOfSight(target)
                && this.mob.isWithinMeleeAttackRange(target)) {
            this.resetAttackCooldown();
            this.mummy.startAttack();
        }
    }
}
