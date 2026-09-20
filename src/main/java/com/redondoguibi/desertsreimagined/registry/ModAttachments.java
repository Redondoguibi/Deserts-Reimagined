package com.redondoguibi.desertsreimagined.registry;

import com.mojang.serialization.Codec;
import com.redondoguibi.desertsreimagined.DesertsReimagined;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, DesertsReimagined.MODID);

    /** Ticks restantes em que a entidade NAO pode regenerar vida (maldicao de C'Thiris). */
    public static final Supplier<AttachmentType<Integer>> NO_REGEN_TICKS =
            ATTACHMENT_TYPES.register("no_regen_ticks",
                    () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());
}
