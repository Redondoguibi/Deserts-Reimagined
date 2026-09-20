# RedondoguibiLib - Guia de Integração

Uma biblioteca Minecraft **NeoForge 1.21.1** modular para criação de bosses, arenas e projéteis avançados.

**Para referência técnica completa das APIs, veja `TECHNICAL_REFERENCE.md`**

---

## 1. Setup Inicial

### 1.1 Adicionar Dependência

No seu `build.gradle` (agora usando **NeoGradle** ou **ModDevGradle**), adicione:

```gradle
repositories {
    // ... outros repos
    maven {
        url "file://${project.projectDir}/../Redondoguibi Lib/build/libs"
    }
}

dependencies {
    // ... outras dependências
    implementation 'com.redondoguibi:redondoguibilib:1.0.0'
}
```

> ⚠️ **NeoForge:** declare também a dependência no `neoforge.mods.toml` (ver seção 1.3). O `implementation` só resolve em compile-time; o runtime exige a entrada `[[dependencies.<seumod>]]`.

Ou copie o JAR compilado para a pasta `libs/` do seu projeto.

### 1.2 Importar Pacotes Principais

```java
import com.redondoguibi.redondoguibilib.api.IBoss;
import com.redondoguibi.redondoguibilib.api.IBossPhase;
import com.redondoguibi.redondoguibilib.BossRegistry;
import com.redondoguibi.redondoguibilib.arena.BossArenaManager;
import com.redondoguibi.redondoguibilib.projectile.ProjectileFactory;
```

### 1.3 Declarar dependência no `neoforge.mods.toml`

```toml
[[dependencies.mymod]]
    modId = "redondoguibilib"
    type = "required"
    versionRange = "[1.0.0,)"
    ordering = "AFTER"
    side = "BOTH"
```

---

## 2. Criando Seu Primeiro Boss

### 2.1 Implementar a Interface IBoss

`IBoss` é o contrato que seu boss precisa implementar. Aqui está um exemplo mínimo:

```java
public class FireDragonBoss extends Mob implements IBoss {

    public FireDragonBoss(EntityType<? extends Mob> type, Level level) {
        super(type, level);
    }

    @Override
    public String getId() {
        return "mymod:fire_dragon";  // Identificador único
    }

    @Override
    public void spawn(Level world, BlockPos pos) {
        // Chamado quando o boss é spawned
        this.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        world.addFreshEntity(this);
    }

    @Override
    public void onDamage(float amount) {
        // Chamado quando o boss sofre dano
        this.hurt(this.damageSources().generic(), amount);
    }

    @Override
    public CompoundTag serializeNBT() {
        // Salvar estado em NBT (para persistência)
        CompoundTag tag = new CompoundTag();
        tag.putFloat("health", this.getHealth());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        // Carregar estado de NBT
        if (tag.contains("health")) {
            this.setHealth(tag.getFloat("health")); // getHealth/setHealth usam float
        }
    }

    @Override
    public LivingEntity asLivingEntity() {
        return this;  // Seu boss é uma LivingEntity
    }
}
```

> 🔧 **Correção:** o original usava `putDouble`/`getDouble` para a vida, mas `getHealth()`/`setHealth()` trabalham com `float`. Trocado para `putFloat`/`getFloat` para evitar perda/erro de tipo.

### 2.2 Registrar o Boss

Na inicialização do seu mod:

```java
public class MyModCommonSetup {
    public static void setup() {
        // Registrar factory do boss
        BossRegistry.register("mymod:fire_dragon",
            () -> new FireDragonBoss(ModEntities.FIRE_DRAGON.get(), null));
    }
}
```

### 2.3 Spawnar o Boss

```java
// Em command, função, ou evento:
IBoss boss = BossRegistry.create("mymod:fire_dragon");
if (boss != null) {
    boss.spawn(level, new BlockPos(100, 64, 100));
}
```

---

## 3. Sistema de Fases

### 3.1 Criar Fases para o Boss

Cada fase é um comportamento (ataque, ação especial, etc). Implemente `IBossPhase`:

```java
public class PhaseOne implements IBossPhase {
    private int tickCounter = 0;

    @Override
    public String getName() {
        return "phase_one";
    }

    @Override
    public void onEnter(IBoss boss) {
        // Chamado ao entrar nesta fase (animar, soar som, etc.)
        tickCounter = 0;
    }

    @Override
    public void onTick(IBoss boss) {
        tickCounter++;
        if (tickCounter % 60 == 0) { // A cada 3 segundos (60 ticks)
            spawnFireballs(boss);
        }
    }

    @Override
    public void onExit(IBoss boss) {
        // Chamado ao sair desta fase
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("ticks", tickCounter);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains("ticks")) {
            tickCounter = tag.getInt("ticks");
        }
    }

    private void spawnFireballs(IBoss boss) {
        LivingEntity entity = boss.asLivingEntity();
        if (entity == null) return;

        for (int i = 0; i < 3; i++) {
            double angle = (Math.PI * 2 / 3) * i;
            double x = entity.getX() + Math.cos(angle) * 5;
            double z = entity.getZ() + Math.sin(angle) * 5;

            // Em 1.21.x o construtor de LargeFireball/SmallFireball mudou.
            // Use a entidade de projétil do seu mod ou os vanilla atualizados.
            SmallFireball fb = new SmallFireball(entity.level(), entity, new Vec3(0, 0, 0));
            fb.moveTo(x, entity.getY() + 2, z, 0, 0);
            entity.level().addFreshEntity(fb);
        }
    }
}

public class PhaseTwo implements IBossPhase {
    // ... implementar similarmente
}
```

> 🔧 **Correção:** o construtor `new Fireball(level, owner, x, y, z)` do 1.20.1 **não existe mais** em 1.21.x. Os fireballs vanilla agora recebem um `Vec3` de direção/aceleração. Recomendado usar o **seu próprio projétil** (`SimpleFireball` da seção 5) para controle total.

### 3.2 Usar PhaseManager no Boss

```java
public class FireDragonBoss extends Mob implements IBoss {
    private PhaseManager phaseManager;

    public FireDragonBoss(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.phaseManager = new PhaseManager(this);
    }

    @Override
    public void tick() {
        super.tick();
        phaseManager.tick();

        float healthPercent = this.getHealth() / this.getMaxHealth();
        if (healthPercent < 0.5f && phaseManager.getCurrent() != null
            && !phaseManager.getCurrent().getName().equals("phase_two")) {
            phaseManager.setPhase(1);
        }
    }
    // ... resto da implementação
}
```

---

## 4. Sistema de Arenas

### 4.1 O que é uma Arena?

Uma **arena** é uma área cubóide que pode:
- ✅ Confinar o boss (teleporta se tentar sair)
- ✅ Confinar jogadores (não conseguem sair enquanto boss está vivo)
- ✅ Proteger blocos (não podem ser quebrados)
- ✅ Aplicar fog (neblina customizada)
- ✅ Tocar música
- ✅ Mostrar partículas
- ✅ Exibir bossbar customizada

### 4.2 Criar Arena (Programaticamente)

```java
BlockPos min = new BlockPos(0, 60, 0);     // Canto inferior
BlockPos max = new BlockPos(20, 80, 20);   // Canto superior

IBossArena arena = BossArenaManager.createAndRegister("meu_boss", min, max);

BossArenaProperties props = arena.getProperties();

props.setConfineBoss(true);
props.setConfinePlayer(true);
props.setProtectBlocks(true);

props.setUseBossBar(true);
props.setBossBarName("Fire Dragon");

props.setMusicId("minecraft:music.record.cat");

BossArenaProperties.ParticleConfig particleConfig = props.getParticleConfig();
particleConfig.setParticleId("minecraft:flame");
particleConfig.setCount(5);
particleConfig.setFrequency(20);
particleConfig.setSpawnAtCenter(true);
```

### 4.3 Criar Arena com COMANDO In-Game

```
/redondoguibilib create_area boss_arena 0 60 0 20 80 20
/redondoguibilib list_areas
/redondoguibilib set_property boss_arena confine_boss true
/redondoguibilib set_property boss_arena protect_blocks true
/redondoguibilib set_property boss_arena music minecraft:music.record.cat
/redondoguibilib remove_area boss_arena
```

### 4.4 Encontrar Arena no Boss

```java
@Override
public void tick() {
    super.tick();

    IBossArena arena = BossArenaManager.findArenaAt(this.level(), this.blockPosition());
    if (arena != null) {
        BossArenaHandler.handleBossConfinement(arena, this);
    }
}
```

---

## 5. Sistema de Projéteis Avançados

### 5.1 Projétil Simples

```java
public class SimpleFireball extends Fireball {
    private ProjectileFactory.ProjectileProfile profile;

    // Em 1.21.x: Fireball(EntityType, Level)
    public SimpleFireball(EntityType<? extends Fireball> type, Level level) {
        super(type, level);
    }

    public void setProfile(ProjectileFactory.ProjectileProfile profile) {
        this.profile = profile;
    }

    @Override
    public void tick() {
        super.tick();
        if (profile != null) {
            profile.incrementTicks();
            if (profile.shouldDie()) {
                this.discard();
            }
        }
    }

    // Necessário em 1.21.x (Fireball é abstrato para o item de partícula)
    @Override
    protected net.minecraft.world.item.ItemStack getItemRaw() {
        return net.minecraft.world.item.ItemStack.EMPTY;
    }
}
```

> 🔧 **Correção:** o construtor `Fireball(Level, x,y,z, speed)` foi removido. Agora projéteis usam o padrão `(EntityType, Level)`. Por isso seu mod deve **registrar um `EntityType`** para o `SimpleFireball`.

### 5.2 Projétil com Homing (Teleguiado)

```java
Player player = level.getNearestPlayer(boss.getX(), boss.getY(), boss.getZ(), 50, false);

ProjectileFactory.ProjectileProfile profile = new ProjectileFactory.ProjectileProfile()
    .withOwner(boss)
    .withHoming(player, 0.15f)
    .withHomingStrength(0.8f)
    .withMaxTicks(200)
    .withSpeed(0.5);

SimpleFireball fireball = new SimpleFireball(ModEntities.SIMPLE_FIREBALL.get(), boss.level());
fireball.setProfile(profile);
boss.level().addFreshEntity(fireball);
```

### 5.3 Projétil com Penetração

```java
ProjectileFactory.ProjectileProfile profile = new ProjectileFactory.ProjectileProfile()
    .withOwner(boss)
    .withPenetration(30.0, 5)
    .withSpeed(0.6)
    .withMaxTicks(300);

@Override
protected void onHit(HitResult result) {
    if (result instanceof EntityHitResult ehr) {
        Entity target = ehr.getEntity();
        target.hurt(this.damageSources().arrow(this, this.getOwner()), 10.0f);

        if (ProjectileFactory.PenetrationBehavior.canPenetrate(profile)) {
            profile.incrementPenetrations();
            return;
        }
    }
    this.discard();
}
```

> ⚠️ **Atenção (1.21.2+):** se você estiver na 1.21.2 ou superior, `entity.hurt(...)` foi renomeado para `entity.hurtServer(ServerLevel, DamageSource, float)`. Na **1.21.1** continua `hurt(...)`. Ajuste conforme sua versão.

### 5.4 Projétil com Dano Contínuo (Laser)

```java
ProjectileFactory.ProjectileProfile profile = new ProjectileFactory.ProjectileProfile()
    .withOwner(boss)
    .ignoreVanillaGravity(true)
    .withContinuousDamage(5.0f, 20.0, 1.0, 10, true)
    .withMaxTicks(100);
```

---

## 6. Callbacks e Eventos

### 6.1 Eventos de Fase

```java
@EventBusSubscriber(modid = "mymod", bus = EventBusSubscriber.Bus.GAME)
public class BossEventHandler {

    @SubscribeEvent
    public static void onPhaseChange(BossPhaseChangeEvent event) {
        IBoss boss = event.getBoss();
        IBossPhase from = event.getFrom();
        IBossPhase to = event.getTo();

        LOGGER.info("Boss {} mudou de fase: {} -> {}",
            boss.getId(), from.getName(), to.getName());
    }
}
```

Imports necessários:
```java
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
```

> 🔧 **Correções importantes:**
> - `@Mod.EventBusSubscriber` → `@EventBusSubscriber` (classe top-level no NeoForge).
> - `Bus.MOD` → use `Bus.GAME` para **eventos de jogo** (como mudança de fase, tick, morte). `Bus.MOD` é só pro ciclo de vida (setup, registries).
> - Troquei `System.out.println` por `LOGGER` (boa prática — `System.out` polui o console e não respeita níveis de log).

### 6.2 Callbacks de Projéteis

```java
ProjectileFactory.ProjectileProfile profile = new ProjectileFactory.ProjectileProfile()
    .onSpawn((projectile, prof) -> {
        projectile.playSound(SoundEvents.FIREWORK_ROCKET_LAUNCH);
    })
    .onEntityHit((projectile, prof) -> { /* impacto */ })
    .onBlockHit((projectile, prof) -> { projectile.discard(); })
    .onTick((projectile, prof) -> { /* lógica por tick */ });
```

> 🔧 **Correção:** `SoundEvents.FIREWORK_LAUNCH` foi renomeado para `SoundEvents.FIREWORK_ROCKET_LAUNCH`.

---

## 7. Guia Step-by-Step Prático

### 7.1 Criar um Boss Completo em 10 Minutos

**Passo 1**: Criar classe do boss
```java
public class MyBoss extends Mob implements IBoss { ... }
```

**Passo 2**: Registrar em BossRegistry
```java
BossRegistry.register("mymod:myboss", () -> new MyBoss(...));
```

**Passo 3**: Criar 2 fases
```java
public class Phase1 implements IBossPhase { ... }
public class Phase2 implements IBossPhase { ... }
```

**Passo 4**: Usar PhaseManager no boss
```java
phaseManager.addPhase(0, new Phase1());
phaseManager.addPhase(1, new Phase2());
```

**Passo 5**: Spawnar com comando
```
/summon mymod:myboss ~ ~ ~
```

---

## 8. Estrutura de Componentes

- **API (IBoss, IBossPhase)**: Contratos para integração.
- **BossRegistry**: Registro global de bosses.
- **PhaseManager**: Gerencia transições de fases.
- **ProjectileFactory + ProjectileProfile**: Sistema expandido de projéteis.
- **BossPhaseChangeEvent**: Evento de transição de fase.
- **IBossArena + BossArena**: Arenas configuráveis.
- **BossArenaProperties**: Propriedades (confinamento, fog, partículas, música, bossbar).
- **BossArenaManager**: Registry de arenas.
- **BossArenaHandler**: Aplicador de efeitos de arena.

---

## 9. Boas Práticas

### 9.1 Serialização NBT

```java
@Override
public CompoundTag serializeNBT() {
    CompoundTag tag = new CompoundTag();
    tag.putFloat("health", this.getHealth());
    tag.putInt("phase", phaseManager.getCurrentPhaseIndex());
    tag.put("phase_data", phaseManager.getCurrent().serializeNBT());
    return tag;
}

@Override
public void deserializeNBT(CompoundTag tag) {
    if (tag.contains("health")) this.setHealth(tag.getFloat("health"));
    if (tag.contains("phase")) phaseManager.setPhase(tag.getInt("phase"));
}
```

### 9.2 Gerenciar Recursos

```java
@SubscribeEvent
public static void onBossDeath(LivingDeathEvent event) {
    if (event.getEntity() instanceof IBoss boss) {
        BossArenaManager.removeArena(boss.getId());
    }
}
```

Import:
```java
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
```

### 9.3 Usar Cached Arena Reference

```java
public class MyBoss extends Mob implements IBoss {
    private IBossArena cachedArena;

    @Override
    public void onDamage(float amount) {
        if (cachedArena == null) {
            cachedArena = BossArenaManager.findArenaAt(this.level(), this.blockPosition());
        }
        // Usar cachedArena
    }
}
```

### 9.4 Performance com Projéteis

```java
private final ProjectileFactory.ProjectileProfile template = new ProjectileFactory.ProjectileProfile()
    .withSpeed(0.5)
    .withMaxTicks(200);

// Usar:
ProjectileFactory.ProjectileProfile runtime = template.copy();
```

- Reutilize templates de `ProjectileProfile`
- Não crie novos profiles a cada tick
- Limite a quantidade de projéteis ativos simultâneos
- Use `maxTicks` para limpeza automática

---

## 10. Troubleshooting

### ❌ Boss não aparece

Verifique:
- Boss foi registrado em `BossRegistry`?
- Coordenadas corretas (y dentro dos limites do mundo)?
- Chunk carregado?

```java
IBoss boss = BossRegistry.create("mymod:myboss");
if (boss == null) {
    LOGGER.error("Boss não registrado em BossRegistry!");
}
```

> 🔧 **Correção:** o bloco original estava **corrompido** (`}.getHeight() method is called.");`). Removido o lixo e fechado corretamente.

### ❌ Arena não confina

- Use `findArenaAt()` no tick
- Chame `BossArenaHandler.handleBossConfinement()`

```java
@SubscribeEvent
public static void onEntityTick(EntityTickEvent.Post event) {
    Entity entity = event.getEntity();
    IBossArena arena = BossArenaManager.findArenaAt(entity.level(), entity.blockPosition());
    if (arena != null) {
        BossArenaHandler.handleBossConfinement(arena, entity);
    }
}
```

Import:
```java
import net.neoforged.neoforge.event.tick.EntityTickEvent;
```

> 🔧 **Correção:** `LivingEvent.LivingTickEvent` **não existe mais** no NeoForge 1.21.x. Foi substituído por `EntityTickEvent.Pre` / `EntityTickEvent.Post`. Também `event.entity` (campo público) virou `event.getEntity()` (método).

### ❌ Projéteis desaparecem

- Devem estar em mundo carregado
- Ter velocidade/direção válida
- Não estar removidos (`!isRemoved()`)

### ❌ Memory leak com projéteis

Implementado em `ProjectileCleanupEvents.java` — projéteis são limpos automaticamente ao serem removidos.

---

## 11. Performance Tips

| Otimização | Recomendação |
|---|---|
| Arena Lookup | Cache do resultado de `findArenaAt()` |
| Partículas | Frequency >= 20 ticks (1 segundo) |
| Músicas | Toque a cada 200 ticks (10 segundos) |
| Projéteis | Máximo 50 ativos simultâneos |
| Fases | Use callbacks ao invés de loops infinitos |
| NBT | Serialize apenas dados essenciais |

---

## 12. Exemplo Completo: Dragon Boss

```java
// 1. Entidade
public class DragonBoss extends Mob implements IBoss {
    private PhaseManager phaseManager;
    private IBossArena arena;

    public DragonBoss(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.phaseManager = new PhaseManager(this);
        this.phaseManager.addPhase(0, new DragonPhase1());
        this.phaseManager.addPhase(1, new DragonPhase2());
    }

    @Override
    public String getId() { return "mymod:dragon"; }

    @Override
    public void spawn(Level world, BlockPos pos) {
        this.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
        world.addFreshEntity(this);
        this.arena = BossArenaManager.findArenaAt(world, pos);
    }

    @Override
    public void tick() {
        super.tick();
        phaseManager.tick();

        if (arena != null) {
            BossArenaHandler.handleBossConfinement(arena, this);
        }

        float health = this.getHealth() / this.getMaxHealth();
        if (health < 0.5f && phaseManager.getCurrent() != null
            && !phaseManager.getCurrent().getName().equals("phase_2")) {
            phaseManager.setPhase(1);
        }
    }

    @Override
    public void onDamage(float amount) {
        this.hurt(this.damageSources().generic(), amount);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("health", this.getHealth());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains("health")) this.setHealth(tag.getFloat("health"));
    }

    @Override
    public LivingEntity asLivingEntity() { return this; }
}

// 2. Fase 1
public class DragonPhase1 implements IBossPhase {
    private int tickCounter;

    @Override
    public String getName() { return "phase_1"; }

    @Override
    public void onEnter(IBoss boss) { tickCounter = 0; }

    @Override
    public void onTick(IBoss boss) {
        tickCounter++;
        if (tickCounter % 60 == 0) {
            spawnFireballs(boss);
        }
    }

    private void spawnFireballs(IBoss boss) {
        LivingEntity entity = boss.asLivingEntity();
        if (entity == null) return;
        for (int i = 0; i < 3; i++) {
            double angle = (Math.PI * 2 / 3) * i;
            SimpleFireball fb = new SimpleFireball(ModEntities.SIMPLE_FIREBALL.get(), entity.level());
            fb.setOwner(entity);
            fb.moveTo(entity.getX() + Math.cos(angle) * 10,
                      entity.getY() + 2,
                      entity.getZ() + Math.sin(angle) * 10, 0, 0);
            entity.level().addFreshEntity(fb);
        }
    }

    @Override
    public void onExit(IBoss boss) {}

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("ticks", tickCounter);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains("ticks")) tickCounter = tag.getInt("ticks");
    }
}

// 3. Registrar
public class ModSetup {
    public static void setup() {
        BossRegistry.register("mymod:dragon",
            () -> new DragonBoss(ModEntities.DRAGON.get(), null));
    }
}

// 4. Criar Arena In-Game
// /redondoguibilib create_area dragon_arena 0 60 0 30 100 30
// /redondoguibilib set_property dragon_arena confine_boss true
// /redondoguibilib set_property dragon_arena protect_blocks true
```

---

## 13. Referência Rápida de Comandos

```
# Arena Management
/redondoguibilib create_area <name> x1 y1 z1 x2 y2 z2
/redondoguibilib remove_area <name>
/redondoguibilib list_areas
/redondoguibilib set_property <arena> confine_boss <true|false>
/redondoguibilib set_property <arena> confine_player <true|false>
/redondoguibilib set_property <arena> protect_blocks <true|false>
/redondoguibilib set_property <arena> music <sound_id>
/redondoguibilib set_property <arena> particles <particle>
```

---

## 14. Checklist de Integração

- [ ] Importar pacotes da biblioteca
- [ ] Adicionar dependência no `neoforge.mods.toml`
- [ ] Implementar `IBoss` na entidade
- [ ] Criar 2+ classes `IBossPhase`
- [ ] Registrar `EntityType` do seu projétil (`SimpleFireball`)
- [ ] Registrar boss em `BossRegistry`
- [ ] Implementar `serializeNBT()` / `deserializeNBT()` (usar `float` para vida)
- [ ] Usar `PhaseManager` no boss
- [ ] Criar arena com comando ou código
- [ ] Usar `@EventBusSubscriber(bus = Bus.GAME)` para eventos de jogo
- [ ] Testar confinamento via `EntityTickEvent.Post`
- [ ] Testar persistência (save/load mundo)

---

## 15. Requisitos e Compatibilidade

- **Minecraft**: 1.21.1
- **NeoForge**: 21.1.x+
- **Java**: 21+
- **Mixin**: Não necessário (compatível com transformers nativos do NeoForge)

> 🔧 **Correção:** NeoForge 1.21.x exige **Java 21** (não mais Java 17). Atualize seu `gradle.properties` e a toolchain do projeto.

---

## 16. Suporte e Documentação

- `src/main/java/com/redondoguibi/redondoguibilib/` - Código-fonte comentado
- `IBoss.java` - Contrato básico
- `IBossPhase.java` - Contrato de fases
- `ProjectileFactory.java` - Sistema de projéteis
- `BossArenaManager.java` - Gerenciador de arenas


**Explore recursos avançados:**
- Múltiplos bosses na mesma arena
- Fog com transições dinâmicas
- Projéteis com penetração + homing combinados
- Arena com triggers customizados
