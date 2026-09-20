package com.redondoguibi.desertsreimagined.entity.boss;

import com.redondoguibi.desertsreimagined.DesertsReimagined;
import com.redondoguibi.desertsreimagined.effect.CthirisCurseEffect;
import com.redondoguibi.desertsreimagined.entity.MummyEntity;
import com.redondoguibi.desertsreimagined.entity.boss.goal.CthirisChargedAttackGoal;
import com.redondoguibi.desertsreimagined.entity.boss.goal.CthirisComboAttackGoal;
import com.redondoguibi.desertsreimagined.entity.boss.goal.CthirisMoveToTargetGoal;
import com.redondoguibi.desertsreimagined.entity.boss.goal.CthirisSummonGoal;
import com.redondoguibi.desertsreimagined.entity.boss.phase.CthirisPhaseOne;
import com.redondoguibi.desertsreimagined.entity.boss.phase.CthirisPhaseTwo;
import com.redondoguibi.desertsreimagined.registry.ModEffects;
import com.redondoguibi.desertsreimagined.registry.ModEntities;
import com.redondoguibi.desertsreimagined.registry.ModSounds;
import com.redondoguibi.redondoguibilib.api.IBoss;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * C'Thiris, o Deus da Tumba.
 *
 * NOTA DE ARQUITETURA: esta classe NAO implementa IBoss diretamente, porque
 * IBoss#getId() retorna String e Entity#getId() retorna int -> conflito de assinatura
 * impossivel de resolver na mesma classe. O contrato da RedondoguibiLib e exposto
 * pelo adapter interno {@link BossHandle}, acessivel via {@link #getBossHandle()}.
 *
 * NOTA DE IA: as flags dos Goals sao criticas.
 *  - CthirisMoveToTargetGoal  -> MOVE apenas
 *  - CthirisComboAttackGoal   -> nenhuma flag
 *  - CthirisChargedAttackGoal -> MOVE
 *  - CthirisSummonGoal        -> MOVE
 * Nenhum goal usa LOOK. A rotacao e feita via getLookControl(), que dispensa flag.
 * Se algum goal de ataque pedir LOOK, ele bloqueia o goal de movimento e o boss trava.
 */
public class CthirisGodEntity extends Monster implements GeoEntity {

    public static final String BOSS_ID = "desertsreimagined:cthiris_god";

    // ====================== Balanceamento ======================
    private static final float PHASE2_THRESHOLD = 0.25F;
    private static final float PHASE2_DAMAGE_MULT = 1.15F;
    private static final double PHASE2_TOUGHNESS_BONUS = 10.0D;

    // Combo: 35t, dano nos ticks 7 e 16, alcance 3.5, cd 60t
    private static final int COMBO_HIT_1 = 7, COMBO_HIT_2 = 16;
    private static final float COMBO_DMG_1 = 30.0F, COMBO_DMG_2 = 20.0F;
    public  static final double COMBO_RANGE = 3.5D;
    private static final int COMBO_CD = 60;

    // Carregado: 69t, dano no tick 46, cone 145 graus, raio 5.5, cd 120t
    private static final int CHARGED_HIT = 46;
    private static final float CHARGED_DMG = 70.0F;
    public  static final double CHARGED_RANGE = 5.5D;
    private static final double CHARGED_ANGLE = 145.0D;
    private static final int CHARGED_CD = 120;

    // Rugido: 44t invulneravel, trigger 8% em 100t, raio 8, cd 100t
    private static final float ROAR_HP_TRIGGER = 0.08F;
    private static final int ROAR_WINDOW = 100;
    private static final double ROAR_RADIUS = 8.0D;
    private static final int ROAR_CD = 100;

    // Summon: 56t, spawn no tick 28, cd 350t
    private static final int SUMMON_SPAWN_TICK = 28;
    private static final int SUMMON_CD = 350;
    private static final int SUMMON_COUNT_P1 = 4, SUMMON_COUNT_P2 = 6;

    private static final Vector3f FLASH_COLOR =
            new Vector3f(0xC6 / 255F, 0xB2 / 255F, 0x83 / 255F);

    private static final ResourceLocation PHASE2_TOUGHNESS_ID =
            ResourceLocation.fromNamespaceAndPath(DesertsReimagined.MODID, "cthiris_phase2_toughness");

    // ====================== Dados sincronizados ======================
    private static final EntityDataAccessor<Integer> DATA_ACTION =
            SynchedEntityData.defineId(CthirisGodEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ACTION_TICK =
            SynchedEntityData.defineId(CthirisGodEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_PHASE =
            SynchedEntityData.defineId(CthirisGodEntity.class, EntityDataSerializers.INT);

    // ====================== Animacoes ======================
    private static final RawAnimation ANIM_IDLE    = RawAnimation.begin().thenLoop("animation.cthiris.idle");
    private static final RawAnimation ANIM_WALK    = RawAnimation.begin().thenLoop("animation.cthiris.walk");
    private static final RawAnimation ANIM_COMBO   = RawAnimation.begin().thenPlay("animation.cthiris.attack_combo");
    private static final RawAnimation ANIM_CHARGED = RawAnimation.begin().thenPlay("animation.cthiris.charged_attack");
    private static final RawAnimation ANIM_ROAR    = RawAnimation.begin().thenPlay("animation.cthiris.roar");
    private static final RawAnimation ANIM_SUMMON  = RawAnimation.begin().thenPlay("animation.cthiris.summon_scarabs");
    private static final RawAnimation ANIM_PHASE   = RawAnimation.begin().thenPlay("animation.cthiris.phase_transition");
    private static final RawAnimation ANIM_HURT    = RawAnimation.begin().thenPlay("animation.cthiris.hurt");
    private static final RawAnimation ANIM_DEATH   = RawAnimation.begin().thenPlay("animation.cthiris.death");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // ====================== Estado interno ======================
    private final BossHandle bossHandle = new BossHandle();
    private final CthirisPhaseController phases;
    private final List<UUID> minions = new ArrayList<>();

    private int comboCd, chargedCd, summonCd, roarCd;
    private float damageInWindow;
    private int damageWindowTicks;
    private boolean comboHit1Done, comboHit2Done, chargedHitDone, summonDone;
    private boolean phase2Applied;
    private int hurtAnimTick = -1;

    public CthirisGodEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
        this.xpReward = 500;
        this.phases = new CthirisPhaseController(this.bossHandle);
        this.phases.addPhase(0, new CthirisPhaseOne());
        this.phases.addPhase(1, new CthirisPhaseTwo());
        this.phases.setPhase(0);
    }

    /** Handle IBoss para integracao com a RedondoguibiLib. */
    public BossHandle getBossHandle() {
        return bossHandle;
    }

    // ====================== Atributos ======================
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 400.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ATTACK_DAMAGE, 30.0D)
                // 0.055 base; o goal multiplica ate 2.4x conforme a distancia
                .add(Attributes.MOVEMENT_SPEED, 0.055D)
                .add(Attributes.FOLLOW_RANGE, 50.0D)
                .add(Attributes.STEP_HEIGHT, 1.2D);
    }

    /** Navegacao ajustada para hitbox larga (1.9 blocos). */
    @Override
    protected PathNavigation createNavigation(Level level) {
        GroundPathNavigation nav = new GroundPathNavigation(this, level);
        nav.setCanFloat(true);
        nav.setCanOpenDoors(false);
        nav.setCanPassDoors(true);
        return nav;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ACTION, CthirisAction.NONE.ordinal());
        builder.define(DATA_ACTION_TICK, 0);
        builder.define(DATA_PHASE, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new CthirisChargedAttackGoal(this));
        this.goalSelector.addGoal(2, new CthirisSummonGoal(this));
        // Movimento ANTES do combo: garante perseguicao continua
        this.goalSelector.addGoal(3, new CthirisMoveToTargetGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new CthirisComboAttackGoal(this));
        // LookAtPlayerGoal foi removido de proposito: roubava a flag LOOK sem necessidade.

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    // ====================== Acessores ======================
    public CthirisAction getAction()   { return CthirisAction.byId(this.entityData.get(DATA_ACTION)); }
    public int getActionTick()         { return this.entityData.get(DATA_ACTION_TICK); }
    public int getPhase()              { return this.entityData.get(DATA_PHASE); }
    public boolean isPhaseTwo()        { return getPhase() >= 1; }
    public boolean isBusy()            { return getAction().isBusy(); }
    public boolean isMovementLocked()  { return getAction().locksMovement; }
    public float getDamageMultiplier() { return isPhaseTwo() ? PHASE2_DAMAGE_MULT : 1.0F; }
    public List<UUID> getMinions()     { return minions; }

    public boolean canStart(CthirisAction action) {
        if (isBusy() || this.isDeadOrDying()) return false;
        return switch (action) {
            case COMBO   -> comboCd   <= 0;
            case CHARGED -> chargedCd <= 0;
            case SUMMON  -> summonCd  <= 0;
            case ROAR    -> roarCd    <= 0;
            default      -> true;
        };
    }

    public void startAction(CthirisAction action) {
        if (this.level().isClientSide) return;
        this.entityData.set(DATA_ACTION, action.ordinal());
        this.entityData.set(DATA_ACTION_TICK, 0);
        this.comboHit1Done = this.comboHit2Done = this.chargedHitDone = this.summonDone = false;

        if (action.locksMovement) {
            this.getNavigation().stop();
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.2D, 1.0D, 0.2D));
        }

        // ---- HOOKS DE SOM: descomente quando os .ogg + sounds.json estiverem prontos ----
        // switch (action) {
        //     case ROAR    -> playSound(ModSounds.CTHIRIS_ROAR.get(), 3.0F, 1.0F);
        //     case COMBO   -> playSound(ModSounds.CTHIRIS_COMBO.get(), 1.5F, 1.0F);
        //     case CHARGED -> playSound(ModSounds.CTHIRIS_CHARGED.get(), 2.0F, 1.0F);
        //     case PHASE   -> playSound(ModSounds.CTHIRIS_PHASE_TRANSITION.get(), 3.0F, 1.0F);
        //     default      -> { }
        // }
    }

    // ====================== Tick ======================
    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            if (hurtAnimTick >= 0 && ++hurtAnimTick > 9) hurtAnimTick = -1;
            return;
        }

        if (comboCd   > 0) comboCd--;
        if (chargedCd > 0) chargedCd--;
        if (summonCd  > 0) summonCd--;
        if (roarCd    > 0) roarCd--;

        if (damageWindowTicks > 0) {
            damageWindowTicks--;
            if (damageWindowTicks == 0) damageInWindow = 0.0F;
        }

        if (!phase2Applied && !isBusy() && !isDeadOrDying()
                && this.getHealth() / this.getMaxHealth() <= PHASE2_THRESHOLD) {
            phase2Applied = true;
            startAction(CthirisAction.PHASE);
        }

        CthirisAction action = getAction();
        if (action.isBusy()) {
            int t = getActionTick() + 1;
            this.entityData.set(DATA_ACTION_TICK, t);

            if (action.locksMovement) {
                this.getNavigation().stop();
                this.setDeltaMovement(0.0D, this.getDeltaMovement().y, 0.0D);
            }
            handleActionTick(action, t);

            if (t >= action.duration && action != CthirisAction.DEATH) {
                finishAction(action);
            }
        }

        phases.tick();
        syncMinionBuffs();
    }

    private void handleActionTick(CthirisAction action, int t) {
        switch (action) {
            case COMBO -> {
                if (!comboHit1Done && t >= COMBO_HIT_1) { comboHit1Done = true; meleeHit(COMBO_DMG_1); }
                if (!comboHit2Done && t >= COMBO_HIT_2) { comboHit2Done = true; meleeHit(COMBO_DMG_2); }
            }
            case CHARGED -> {
                if (!chargedHitDone && t >= CHARGED_HIT) { chargedHitDone = true; chargedBlast(); }
            }
            case ROAR -> {
                if (t == 20) roarPush();
            }
            case SUMMON -> {
                if (!summonDone && t >= SUMMON_SPAWN_TICK) { summonDone = true; summonMummies(); }
            }
            case PHASE -> {
                if (t == 60) applyPhaseTwo();
            }
            default -> { }
        }
    }

    private void finishAction(CthirisAction action) {
        switch (action) {
            case COMBO   -> comboCd   = COMBO_CD;
            case CHARGED -> chargedCd = CHARGED_CD;
            case SUMMON  -> summonCd  = SUMMON_CD;
            case ROAR    -> roarCd    = ROAR_CD;
            default      -> { }
        }
        this.entityData.set(DATA_ACTION, CthirisAction.NONE.ordinal());
        this.entityData.set(DATA_ACTION_TICK, 0);
    }

    // ====================== Ataques ======================
    private void meleeHit(float baseDamage) {
        LivingEntity target = this.getTarget();
        if (target == null) return;
        if (this.distanceTo(target) > COMBO_RANGE + target.getBbWidth() / 2.0F) return;
        target.hurt(this.damageSources().mobAttack(this), baseDamage * getDamageMultiplier());
        applyCurse(target, 1);
    }

    /** Cone de 145 graus na direcao do olhar, raio 5.5. */
    private void chargedBlast() {
        Vec3 facing = Vec3.directionFromRotation(0.0F, this.getYRot()).normalize();
        double halfAngleCos = Math.cos(Math.toRadians(CHARGED_ANGLE / 2.0D));

        for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(CHARGED_RANGE), this::isValidVictim)) {

            Vec3 to = e.position().subtract(this.position()).with(Direction.Axis.Y, 0.0D);
            if (to.lengthSqr() < 1.0E-4D) { hitCharged(e); continue; }
            if (to.length() > CHARGED_RANGE + e.getBbWidth() / 2.0F) continue;
            if (facing.dot(to.normalize()) >= halfAngleCos) hitCharged(e);
        }

        if (this.level() instanceof ServerLevel sl) {
            sl.sendParticles(new DustParticleOptions(FLASH_COLOR, 1.6F),
                    this.getX(), this.getY() + 0.2D, this.getZ(), 90, 3.0D, 0.3D, 3.0D, 0.05D);
        }
    }

    private void hitCharged(LivingEntity e) {
        e.hurt(this.damageSources().mobAttack(this), CHARGED_DMG * getDamageMultiplier());
        applyCurse(e, isPhaseTwo() ? 2 : 1);
    }

    private void roarPush() {
        for (LivingEntity e : this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(ROAR_RADIUS), this::isValidVictim)) {
            Vec3 dir = e.position().subtract(this.position()).normalize();
            e.push(dir.x * 1.6D, 0.55D, dir.z * 1.6D);
            e.hurtMarked = true;
        }
        if (this.level() instanceof ServerLevel sl) {
            sl.sendParticles(new DustParticleOptions(FLASH_COLOR, 2.0F),
                    this.getX(), this.getY() + 0.2D, this.getZ(), 140, 4.0D, 0.4D, 4.0D, 0.08D);
        }
    }

    private void summonMummies() {
        if (!(this.level() instanceof ServerLevel sl)) return;
        int count = isPhaseTwo() ? SUMMON_COUNT_P2 : SUMMON_COUNT_P1;

        for (int i = 0; i < count; i++) {
            double angle = (Math.PI * 2.0D / count) * i;
            double x = this.getX() + Math.cos(angle) * 4.0D;
            double z = this.getZ() + Math.sin(angle) * 4.0D;
            BlockPos pos = BlockPos.containing(x, this.getY(), z);

            MummyEntity mummy = ModEntities.MUMMY.get().create(sl);
            if (mummy == null) continue;

            mummy.moveTo(x, pos.getY(), z, this.random.nextFloat() * 360.0F, 0.0F);
            mummy.setBossOwner(this.getUUID());
            mummy.setPersistenceRequired();
            mummy.setTarget(this.getTarget());
            if (isPhaseTwo()) mummy.setBossBuffed(true);
            mummy.finalizeSpawn(sl, sl.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null);
            sl.addFreshEntity(mummy);
            minions.add(mummy.getUUID());

            // Clarao + poeira dourada (#C6B283)
            sl.sendParticles(ParticleTypes.FLASH, x, mummy.getY() + 1.0D, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            sl.sendParticles(new DustParticleOptions(FLASH_COLOR, 1.4F),
                    x, mummy.getY() + 1.0D, z, 40, 0.5D, 0.9D, 0.5D, 0.05D);
        }
    }

    private boolean isValidVictim(LivingEntity e) {
        return e != this
                && !(e instanceof MummyEntity m && this.getUUID().equals(m.getBossOwner()))
                && e.isAlive()
                && !e.isSpectator();
    }

    // ====================== Maldicao ======================
    /**
     * Aplica/incrementa a maldicao. Duracao infinita.
     * A imunidade ao leite e tratada em ModEventHandlers (reaplicacao pos-consumo),
     * pois MobEffectInstance#setCurativeItems nao existe mais no NeoForge 1.21.1.
     */
    public void applyCurse(LivingEntity target, int stacks) {
        if (this.level().isClientSide || stacks <= 0) return;

        MobEffectInstance currentEffect = target.getEffect(ModEffects.curse());
        int newAmp = (currentEffect == null ? 0 : currentEffect.getAmplifier() + stacks);

        if (newAmp >= CthirisCurseEffect.TRIGGER_AMPLIFIER) {
            CthirisCurseEffect.detonate(target);
            return;
        }
        target.addEffect(new MobEffectInstance(
                ModEffects.curse(), MobEffectInstance.INFINITE_DURATION, newAmp,
                false, true, true), this);
    }

    // ====================== Fase 2 ======================
    private void applyPhaseTwo() {
        this.entityData.set(DATA_PHASE, 1);
        AttributeInstance tough = this.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (tough != null && tough.getModifier(PHASE2_TOUGHNESS_ID) == null) {
            tough.addPermanentModifier(new AttributeModifier(
                    PHASE2_TOUGHNESS_ID, PHASE2_TOUGHNESS_BONUS,
                    AttributeModifier.Operation.ADD_VALUE));
        }
        phases.setPhase(1);
        syncMinionBuffs();
    }

    private void syncMinionBuffs() {
        if (!isPhaseTwo() || !(this.level() instanceof ServerLevel sl)) return;
        for (UUID id : minions) {
            if (sl.getEntity(id) instanceof MummyEntity m && m.isAlive()) m.setBossBuffed(true);
        }
    }

    // ====================== Dano / imunidades ======================
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide) return false;

        // Reflete projeteis
        if (source.getDirectEntity() instanceof Projectile projectile) {
            reflect(projectile);
            return false;
        }
        if (isInvulnerableTo(source)) return false;

        float before = this.getHealth();
        boolean result = super.hurt(source, amount);

        if (result) {
            float dealt = before - this.getHealth();
            if (dealt > 0.0F) {
                damageInWindow += dealt;
                damageWindowTicks = ROAR_WINDOW;

                if (!isBusy() && roarCd <= 0
                        && damageInWindow >= this.getMaxHealth() * ROAR_HP_TRIGGER
                        && hasNearbyPlayer(ROAR_RADIUS)) {
                    damageInWindow = 0.0F;
                    startAction(CthirisAction.ROAR);
                }
            }
        }
        return result;
    }

    private void reflect(Projectile projectile) {
        projectile.setOwner(this);
        projectile.setDeltaMovement(projectile.getDeltaMovement().scale(-1.1D));
        projectile.setYRot(projectile.getYRot() + 180.0F);
        projectile.hurtMarked = true;
        if (projectile instanceof AbstractArrow arrow) {
            // setPierceLevel e private na 1.21.1 -> apenas removemos o critico
            arrow.setCritArrow(false);
        }
        this.level().playSound(null, this.blockPosition(), SoundEvents.SHIELD_BLOCK,
                this.getSoundSource(), 1.0F, 1.2F);
    }

    private boolean hasNearbyPlayer(double radius) {
        return this.level().getNearestPlayer(this, radius) != null;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (super.isInvulnerableTo(source)) return true;
        if (getAction().invulnerable) return true;

        return source.is(DamageTypeTags.IS_FIRE)
                || source.is(DamageTypeTags.IS_FALL)
                || source.is(DamageTypeTags.IS_DROWNING)
                || source.is(DamageTypeTags.IS_FREEZING)
                || source.is(DamageTypeTags.IS_EXPLOSION)
                || source.is(DamageTypes.WITHER)
                || source.is(DamageTypes.WITHER_SKULL)
                || source.is(DamageTypes.IN_WALL)
                || source.is(DamageTypes.CRAMMING)
                || (source.is(DamageTypes.MAGIC) && source.getEntity() == null);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        if (effect.getEffect() == MobEffects.POISON || effect.getEffect() == MobEffects.WITHER) {
            return false;
        }
        return super.canBeAffected(effect);
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean ignoreExplosion(Explosion explosion) {
        return true;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        // Somente o boss atravessa as mumias
        if (entity instanceof MummyEntity) return false;
        return super.canCollideWith(entity);
    }

    @Override
    protected void pushEntities() {
        // nao empurra nem e empurrado
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public boolean canChangeDimensions(Level from, Level to) {
        return false;
    }

    // ====================== Morte ======================
    @Override
    protected void tickDeath() {
        if (getAction() != CthirisAction.DEATH) {
            startAction(CthirisAction.DEATH);
            killMinions();
        }
        this.deathTime++;
        if (this.deathTime >= CthirisAction.DEATH.duration && !this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    private void killMinions() {
        if (!(this.level() instanceof ServerLevel sl)) return;
        for (UUID id : minions) {
            if (sl.getEntity(id) instanceof MummyEntity m && m.isAlive()) {
                m.hurt(this.damageSources().magic(), Float.MAX_VALUE);
            }
        }
        minions.clear();
    }

    // ====================== Sons ======================
    @Nullable @Override protected SoundEvent getAmbientSound() { return null; }
    @Nullable @Override protected SoundEvent getHurtSound(DamageSource s) { return ModSounds.CTHIRIS_HURT.get(); }
    @Nullable @Override protected SoundEvent getDeathSound() { return ModSounds.CTHIRIS_DEATH.get(); }
    @Override protected float getSoundVolume() { return 2.0F; }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        // HOOK: descomente quando existir o som de passo
        // this.playSound(ModSounds.CTHIRIS_STEP.get(), 0.9F, 1.0F);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 2) hurtAnimTick = 0; // dano -> animacao de hurt no cliente
        super.handleEntityEvent(id);
    }

    // ====================== NBT ======================
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Phase", getPhase());
        tag.putBoolean("Phase2Applied", phase2Applied);
        tag.putInt("ComboCd", comboCd);
        tag.putInt("ChargedCd", chargedCd);
        tag.putInt("SummonCd", summonCd);
        tag.putInt("RoarCd", roarCd);
        ListTag list = new ListTag();
        for (UUID id : minions) list.add(NbtUtils.createUUID(id));
        tag.put("Minions", list);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(DATA_PHASE, tag.getInt("Phase"));
        phase2Applied = tag.getBoolean("Phase2Applied");
        comboCd   = tag.getInt("ComboCd");
        chargedCd = tag.getInt("ChargedCd");
        summonCd  = tag.getInt("SummonCd");
        roarCd    = tag.getInt("RoarCd");
        minions.clear();
        for (var t : tag.getList("Minions", 11)) minions.add(NbtUtils.loadUUID(t));
        phases.setPhase(getPhase());
    }

    // ====================== GeckoLib ======================
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main_ctrl", 4, state -> {
            CthirisAction action = getAction();

            switch (action) {
                case DEATH   -> { state.setAnimation(ANIM_DEATH);   return PlayState.CONTINUE; }
                case PHASE   -> { state.setAnimation(ANIM_PHASE);   return PlayState.CONTINUE; }
                case CHARGED -> { state.setAnimation(ANIM_CHARGED); return PlayState.CONTINUE; }
                case SUMMON  -> { state.setAnimation(ANIM_SUMMON);  return PlayState.CONTINUE; }
                case ROAR    -> { state.setAnimation(ANIM_ROAR);    return PlayState.CONTINUE; }
                case COMBO   -> { state.setAnimation(ANIM_COMBO);   return PlayState.CONTINUE; }
                default      -> { }
            }
            if (hurtAnimTick >= 0) { state.setAnimation(ANIM_HURT); return PlayState.CONTINUE; }
            state.setAnimation(state.isMoving() ? ANIM_WALK : ANIM_IDLE);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    // ====================================================================
    //  Adapter IBoss (RedondoguibiLib)
    //  Necessario porque Entity#getId() -> int conflita com IBoss#getId() -> String
    // ====================================================================
    public class BossHandle implements IBoss {

        public CthirisGodEntity getBoss() {
            return CthirisGodEntity.this;
        }

        @Override
        public String getId() {
            return BOSS_ID;
        }

        @Override
        public void spawn(Level world, BlockPos pos) {
            CthirisGodEntity boss = getBoss();
            boss.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
            world.addFreshEntity(boss);
        }

        @Override
        public void onDamage(float amount) {
            CthirisGodEntity boss = getBoss();
            boss.hurt(boss.damageSources().generic(), amount);
        }

        @Override
        public CompoundTag serializeNBT() {
            CthirisGodEntity boss = getBoss();
            CompoundTag tag = new CompoundTag();
            tag.putFloat("health", boss.getHealth());
            tag.putInt("phase", boss.getPhase());
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            CthirisGodEntity boss = getBoss();
            if (tag.contains("health")) boss.setHealth(tag.getFloat("health"));
            if (tag.contains("phase")) boss.entityData.set(DATA_PHASE, tag.getInt("phase"));
        }

        @Override
        public LivingEntity asLivingEntity() {
            return getBoss();
        }
    }
}