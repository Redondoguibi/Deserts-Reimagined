package com.redondoguibi.desertsreimagined.entity.boss.goal;

import com.redondoguibi.desertsreimagined.entity.boss.CthirisGodEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Goal de perseguicao do C'Thiris.
 *
 * IMPORTANTE: usa APENAS a flag MOVE.
 * Se pedisse LOOK, o CthirisComboAttackGoal (prioridade menor, tambem LOOK)
 * bloquearia este goal permanentemente e o boss ficaria parado.
 * A rotacao aqui e feita via getLookControl(), que nao exige flag.
 */
public class CthirisMoveToTargetGoal extends Goal {

    private final CthirisGodEntity boss;
    private final double baseSpeed;

    private int repathCooldown;
    private int stuckTicks;
    private double lastDistSqr = -1.0D;

    /** Distancia em que o boss para de avancar (fica no alcance do combo). */
    private static final double STOP_DISTANCE = 2.6D;

    public CthirisMoveToTargetGoal(CthirisGodEntity boss, double baseSpeed) {
        this.boss = boss;
        this.baseSpeed = baseSpeed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity t = boss.getTarget();
        return t != null && t.isAlive() && !boss.isMovementLocked();
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        repathCooldown = 0;
        stuckTicks = 0;
        lastDistSqr = -1.0D;
    }

    @Override
    public void stop() {
        boss.getNavigation().stop();
        repathCooldown = 0;
        stuckTicks = 0;
        lastDistSqr = -1.0D;
    }

    @Override
    public void tick() {
        LivingEntity t = boss.getTarget();
        if (t == null) return;

        // Rotacao (nao usa a flag LOOK)
        boss.getLookControl().setLookAt(t, 30.0F, 30.0F);

        double distSqr = boss.distanceToSqr(t);

        // Ja esta em alcance de golpe: para de empurrar o pathfinding
        if (distSqr <= STOP_DISTANCE * STOP_DISTANCE) {
            boss.getNavigation().stop();
            boss.setYRot(boss.getYHeadRot());
            boss.yBodyRot = boss.getYRot();
            return;
        }

        // Velocidade dinamica: longe = avanca mais decidido, perto = passo de coloso
        double dist = Math.sqrt(distSqr);
        double speed = baseSpeed;
        if (dist > 20.0D)      speed = baseSpeed * 2.4D;
        else if (dist > 12.0D) speed = baseSpeed * 2.0D;
        else if (dist > 6.0D)  speed = baseSpeed * 1.6D;
        else                   speed = baseSpeed * 1.2D;

        if (--repathCooldown <= 0) {
            repathCooldown = 5;
            boolean ok = boss.getNavigation().moveTo(t, speed);

            // Fallback: pathfinding falhou (hitbox grande costuma travar em vaos)
            if (!ok) {
                nudgeToward(t, speed);
            }
        }

        // Deteccao de travamento: se nao encurtou distancia em 40 ticks, empurra na mao
        if (lastDistSqr >= 0.0D && distSqr >= lastDistSqr - 0.05D) {
            stuckTicks++;
        } else {
            stuckTicks = 0;
        }
        lastDistSqr = distSqr;

        if (stuckTicks > 40) {
            stuckTicks = 0;
            boss.getNavigation().stop();
            nudgeToward(t, speed);
        }
    }

    /** Movimento direto quando o pathfinding nao consegue traçar rota. */
    private void nudgeToward(LivingEntity t, double speed) {
        Vec3 dir = t.position().subtract(boss.position()).normalize();
        double accel = boss.getSpeed() * speed * 0.55D;
        boss.setDeltaMovement(
                boss.getDeltaMovement().add(dir.x * accel, 0.0D, dir.z * accel));

        // Sobe degraus / obstaculos de 1 bloco
        if (boss.horizontalCollision && boss.onGround()) {
            boss.setDeltaMovement(boss.getDeltaMovement().add(0.0D, 0.42D, 0.0D));
        }
    }
}