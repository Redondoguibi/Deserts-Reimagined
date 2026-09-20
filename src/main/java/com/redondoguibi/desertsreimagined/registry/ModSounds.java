package com.redondoguibi.desertsreimagined.registry;

import com.redondoguibi.desertsreimagined.DesertsReimagined;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, DesertsReimagined.MODID);

    private static DeferredHolder<SoundEvent, SoundEvent> reg(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(
                ResourceLocation.fromNamespaceAndPath(DesertsReimagined.MODID, name)));
    }

    // ---- Sons existentes (ja no sounds.json) ----
    public static final DeferredHolder<SoundEvent, SoundEvent> CTHIRIS_HURT      = reg("cthiris_god.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> CTHIRIS_DEATH     = reg("cthiris_god.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> CTHIRIS_BOSSFIGHT = reg("cthiris_god.bossfight");

    // ---- Sons futuros: descomente quando os .ogg + sounds.json estiverem prontos ----
    // public static final DeferredHolder<SoundEvent, SoundEvent> CTHIRIS_ROAR             = reg("cthiris_god.roar");
    // public static final DeferredHolder<SoundEvent, SoundEvent> CTHIRIS_COMBO            = reg("cthiris_god.combo");
    // public static final DeferredHolder<SoundEvent, SoundEvent> CTHIRIS_CHARGED          = reg("cthiris_god.charged");
    // public static final DeferredHolder<SoundEvent, SoundEvent> CTHIRIS_PHASE_TRANSITION = reg("cthiris_god.phase_transition");
    // public static final DeferredHolder<SoundEvent, SoundEvent> CTHIRIS_STEP             = reg("cthiris_god.step");
}
