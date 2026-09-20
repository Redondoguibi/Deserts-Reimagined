package com.redondoguibi.desertsreimagined.client.sound;

import com.redondoguibi.desertsreimagined.DesertsReimagined;
import com.redondoguibi.desertsreimagined.entity.boss.CthirisGodEntity;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.sound.SoundEngineLoadEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;

@EventBusSubscriber(modid = DesertsReimagined.MODID,
        bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class CthirisMusicHandler {

    private static CthirisMusicSound current;
    private static int checkCooldown;

    @SubscribeEvent
    public static void onClientTick(PlayerTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (!event.getEntity().level().isClientSide) return;
        if (mc.player == null || mc.level == null) return;
        if (event.getEntity() != mc.player) return;

        if (current != null && !mc.getSoundManager().isActive(current)) {
            current = null;
        }

        if (--checkCooldown > 0) return;
        checkCooldown = 20;

        List<CthirisGodEntity> list = mc.level.getEntitiesOfClass(
                CthirisGodEntity.class,
                mc.player.getBoundingBox().inflate(48.0D),
                e -> e.getHealth() > 0.0F && !e.isRemoved());

        if (!list.isEmpty()) {
            if (current == null) {
                mc.getMusicManager().stopPlaying();
                current = new CthirisMusicSound(list.get(0));
                mc.getSoundManager().play(current);
            }
        } else if (current != null) {
            mc.getSoundManager().stop(current);
            current = null;
        }
    }

    @SubscribeEvent
    public static void onSoundEngineReload(SoundEngineLoadEvent event) {
        current = null;
    }
}
