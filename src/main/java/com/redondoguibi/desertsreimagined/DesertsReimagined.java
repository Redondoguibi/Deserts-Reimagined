package com.redondoguibi.desertsreimagined;

import com.mojang.logging.LogUtils;
import com.redondoguibi.desertsreimagined.entity.boss.CthirisGodEntity;
import com.redondoguibi.desertsreimagined.registry.*;
import com.redondoguibi.redondoguibilib.BossRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import com.redondoguibi.desertsreimagined.dimension.CthirisStructureLoader;
import net.neoforged.neoforge.event.level.LevelEvent;

@Mod(DesertsReimagined.MODID)
public class DesertsReimagined {
    public static final String MODID = "desertsreimagined";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DesertsReimagined(IEventBus modEventBus) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModEffects.MOB_EFFECTS.register(modEventBus);
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModDimensions.DIMENSION_TYPES.register(modEventBus);
        ModLevelStems.LEVEL_STEMS.register(modEventBus);


        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onLevelLoad);

        LOGGER.info("DesertsReimagined inicializated.");
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            BossRegistry.register(CthirisGodEntity.BOSS_ID, () ->
                    new CthirisGodEntity(ModEntities.CTHIRIS_GOD.get(), null).getBossHandle());
            LOGGER.info("C'Thiris registered in RedondoguibiLib.");
        });
    }

    private void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            CthirisStructureLoader.loadStructure(serverLevel);
        }
        LOGGER.info("DesertsReimagined level loaded.");
    }
}