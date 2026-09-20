package com.redondoguibi.desertsreimagined.entity.boss.phase;

import com.redondoguibi.desertsreimagined.entity.boss.CthirisGodEntity;
import com.redondoguibi.redondoguibilib.api.IBoss;
import com.redondoguibi.redondoguibilib.api.IBossPhase;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

public class CthirisPhaseTwo implements IBossPhase {

    private int tickCounter;

    @Override public String getName() { return "cthiris_phase_two"; }

    @Override
    public void onEnter(IBoss boss) {
        tickCounter = 0;
    }

    @Override
    public void onTick(IBoss boss) {
        tickCounter++;
        // O boss agora chega como BossHandle -> resolvemos via asLivingEntity()
        if (!(boss.asLivingEntity() instanceof CthirisGodEntity c)) return;
        if (tickCounter % 20 != 0) return;
        if (c.level() instanceof ServerLevel sl) {
            sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    c.getX(), c.getY() + 2.4D, c.getZ(), 8, 0.2D, 0.9D, 0.2D, 0.03D);
        }
    }

    @Override public void onExit(IBoss boss) { }

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