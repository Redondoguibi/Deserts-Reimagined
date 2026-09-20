package com.redondoguibi.desertsreimagined.event;

import com.redondoguibi.desertsreimagined.DesertsReimagined;
import com.redondoguibi.desertsreimagined.entity.MummyEntity;
import com.redondoguibi.desertsreimagined.entity.boss.CthirisGodEntity;
import com.redondoguibi.desertsreimagined.registry.ModAttachments;
import com.redondoguibi.desertsreimagined.registry.ModEntities;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public class ModEventHandlers {

    /** Bus MOD: registros do ciclo de vida. */
    @EventBusSubscriber(modid = DesertsReimagined.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class ModBus {
        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            event.put(ModEntities.MUMMY.get(), MummyEntity.createAttributes().build());
            event.put(ModEntities.CTHIRIS_GOD.get(), CthirisGodEntity.createAttributes().build());
        }
    }

    /** Bus GAME: eventos de jogo. */
    @EventBusSubscriber(modid = DesertsReimagined.MODID, bus = EventBusSubscriber.Bus.GAME)
    public static class GameBus {


         /** Imunidade ao leite: o leite chama removeAllEffects(). */

        @SubscribeEvent
        public static void onMilkFinish(net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent.Finish event) {
            if (event.getEntity().level().isClientSide) return;
            if (!event.getItem().is(net.minecraft.world.item.Items.MILK_BUCKET)) return;

            LivingEntity entity = event.getEntity();
            net.minecraft.world.effect.MobEffectInstance curse =
                    entity.getEffect(com.redondoguibi.desertsreimagined.registry.ModEffects.curse());
            if (curse == null) return;

            final int amp = curse.getAmplifier();
            // Reaplica no tick seguinte, apos o removeAllEffects() do leite
            if (entity.level() instanceof net.minecraft.server.level.ServerLevel sl) {
                sl.getServer().execute(() -> {
                    if (entity.isAlive()) {
                        entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                com.redondoguibi.desertsreimagined.registry.ModEffects.curse(),
                                net.minecraft.world.effect.MobEffectInstance.INFINITE_DURATION,
                                amp, false, true, true));
                    }
                });
            }
        }
        /** Bloqueia regeneracao enquanto a maldicao estiver detonada. */
        @SubscribeEvent
        public static void onHeal(LivingHealEvent event) {
            LivingEntity entity = event.getEntity();
            if (entity.level().isClientSide) return;
            if (entity.getData(ModAttachments.NO_REGEN_TICKS.get()) > 0) {
                event.setCanceled(true);
            }
        }

        /** Decrementa o contador de sem-regeneracao. */
        @SubscribeEvent
        public static void onEntityTick(EntityTickEvent.Post event) {
            if (!(event.getEntity() instanceof LivingEntity entity)) return;
            if (entity.level().isClientSide) return;

            int ticks = entity.getData(ModAttachments.NO_REGEN_TICKS.get());
            if (ticks > 0) {
                entity.setData(ModAttachments.NO_REGEN_TICKS.get(), ticks - 1);
            }
        }
    }
}
