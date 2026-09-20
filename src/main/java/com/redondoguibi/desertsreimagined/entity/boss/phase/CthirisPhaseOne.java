package com.redondoguibi.desertsreimagined.entity.boss.phase;

import com.redondoguibi.redondoguibilib.api.IBoss;
import com.redondoguibi.redondoguibilib.api.IBossPhase;
import net.minecraft.nbt.CompoundTag;

public class CthirisPhaseOne implements IBossPhase {

    @Override public String getName() { return "cthiris_phase_one"; }
    @Override public void onEnter(IBoss boss) { }
    @Override public void onTick(IBoss boss) { }
    @Override public void onExit(IBoss boss) { }

    @Override public CompoundTag serializeNBT() { return new CompoundTag(); }
    @Override public void deserializeNBT(CompoundTag tag) { }
}
