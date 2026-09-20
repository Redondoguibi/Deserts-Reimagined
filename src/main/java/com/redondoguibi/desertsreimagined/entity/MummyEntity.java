package com.redondoguibi.desertsreimagined.entity;

import com.redondoguibi.desertsreimagined.DesertsReimagined;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.UUID;

public class MummyEntity extends Monster implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Animacoes
    private static final RawAnimation IDLE   = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK   = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");

    // Controle do ataque
    private int attackAnimTick = -1;
    private boolean pendingHit = false;
    private static final int ATTACK_LENGTH_TICKS = 20; // 1s * 20
    private static final int HIT_TICK = 12;            // 0.6s * 20

    // ---- Vinculo com C'Thiris ----
    private static final ResourceLocation BOSS_BUFF_DAMAGE_ID =
            ResourceLocation.fromNamespaceAndPath(DesertsReimagined.MODID, "cthiris_minion_damage");
    private static final ResourceLocation BOSS_BUFF_TOUGHNESS_ID =
            ResourceLocation.fromNamespaceAndPath(DesertsReimagined.MODID, "cthiris_minion_toughness");

    @Nullable private UUID bossOwner;
    private boolean bossBuffed;
    private int ownerSyncCooldown;

    public MummyEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    // ---- Atributos ----
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 26.0D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 1.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.2D)
                .add(Attributes.ATTACK_DAMAGE, 5.5D)
                .add(Attributes.MOVEMENT_SPEED, 0.18D)
                .add(Attributes.FOLLOW_RANGE, 14.0D);
    }

    // ---- IA ----
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MummyAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    // ================= Vinculo com o boss =================
    @Nullable public UUID getBossOwner() { return bossOwner; }

    public void setBossOwner(@Nullable UUID owner) {
        this.bossOwner = owner;
    }

    public boolean isBossMinion() { return bossOwner != null; }

    public boolean isBossBuffed() { return bossBuffed; }

    /** Aplica/remove +15% de dano e +10 de armor toughness (fase 2 do C'Thiris). */
    public void setBossBuffed(boolean buffed) {
        if (this.bossBuffed == buffed) return;
        this.bossBuffed = buffed;

        AttributeInstance dmg = this.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance tough = this.getAttribute(Attributes.ARMOR_TOUGHNESS);

        if (buffed) {
            if (dmg != null && dmg.getModifier(BOSS_BUFF_DAMAGE_ID) == null) {
                dmg.addPermanentModifier(new AttributeModifier(
                        BOSS_BUFF_DAMAGE_ID, 0.15D,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
            }
            if (tough != null && tough.getModifier(BOSS_BUFF_TOUGHNESS_ID) == null) {
                tough.addPermanentModifier(new AttributeModifier(
                        BOSS_BUFF_TOUGHNESS_ID, 10.0D,
                        AttributeModifier.Operation.ADD_VALUE));
            }
        } else {
            if (dmg != null) dmg.removeModifier(BOSS_BUFF_DAMAGE_ID);
            if (tough != null) tough.removeModifier(BOSS_BUFF_TOUGHNESS_ID);
        }
    }

    /** Herda o alvo do boss a cada 1s. */
    private void inheritBossTarget() {
        if (bossOwner == null || !(this.level() instanceof ServerLevel sl)) return;
        if (--ownerSyncCooldown > 0) return;
        ownerSyncCooldown = 20;

        if (sl.getEntity(bossOwner) instanceof Mob boss && boss.isAlive()) {
            LivingEntity bossTarget = boss.getTarget();
            if (bossTarget != null && bossTarget.isAlive() && this.getTarget() == null) {
                this.setTarget(bossTarget);
            }
        }
    }

    /** Inicia a animacao de ataque e agenda o dano para o frame 0.6s */
    public void startAttack() {
        if (!this.level().isClientSide && this.attackAnimTick < 0) {
            this.attackAnimTick = 0;
            this.pendingHit = true;
            triggerAnim("attack_ctrl", "attack");
        }
    }

    public boolean isAttacking() {
        return this.attackAnimTick >= 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;

        inheritBossTarget();

        if (this.attackAnimTick >= 0) {
            this.attackAnimTick++;

            if (this.pendingHit && this.attackAnimTick >= HIT_TICK) {
                this.pendingHit = false;
                LivingEntity target = this.getTarget();
                if (target != null && this.isWithinMeleeAttackRange(target)) {
                    this.doHurtTarget(target);
                    this.swing(InteractionHand.MAIN_HAND);
                    // Lentidao II por 2s (40 ticks)
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 3, false, false, false));
                }
            }

            if (this.attackAnimTick >= ATTACK_LENGTH_TICKS) {
                this.attackAnimTick = -1;
            }
        }
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return !isBossMinion() && super.removeWhenFarAway(distance);
    }

    // ---- NBT ----
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (bossOwner != null) tag.putUUID("BossOwner", bossOwner);
        tag.putBoolean("BossBuffed", bossBuffed);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("BossOwner")) bossOwner = tag.getUUID("BossOwner");
        if (tag.getBoolean("BossBuffed")) setBossBuffed(true);
    }

    // ---- GeckoLib ----
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "move_ctrl", 5, state -> {
            if (state.isMoving()) {
                state.setAnimation(WALK);
            } else {
                state.setAnimation(IDLE);
            }
            return PlayState.CONTINUE;
        }));

        controllers.add(
                new AnimationController<>(this, "attack_ctrl", 0, state -> PlayState.STOP)
                        .triggerableAnim("attack", ATTACK)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
