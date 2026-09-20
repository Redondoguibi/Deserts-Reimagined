package com.redondoguibi.desertsreimagined.registry;

import com.redondoguibi.desertsreimagined.DesertsReimagined;
import com.redondoguibi.desertsreimagined.effect.CthirisCurseEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, DesertsReimagined.MODID);

    public static final DeferredHolder<MobEffect, CthirisCurseEffect> CTHIRIS_CURSE =
            MOB_EFFECTS.register("cthiris_curse", CthirisCurseEffect::new);

    public static Holder<MobEffect> curse() {
        return CTHIRIS_CURSE;
    }
}
