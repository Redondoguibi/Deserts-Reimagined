package com.redondoguibi.desertsreimagined.item;

import com.redondoguibi.desertsreimagined.dimension.DimensionPositionMemory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class EssenceOfCurseItem extends Item implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final int COOLDOWN_TICKS = 200; // 10 segundos

    public EssenceOfCurseItem(Properties properties) {
        super(properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "idle_controller", 0, state -> {
            state.setAnimation(IDLE);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            // Verifica se está no bioma de deserto
            boolean isDesert = level.getBiome(player.blockPosition()).is(BiomeTags.IS_DESERT);
            if (!isDesert) {
                return InteractionResultHolder.fail(stack);
            }

            // Verifica cooldown
            if (player.getCooldowns().isOnCooldown(this)) {
                return InteractionResultHolder.fail(stack);
            }

            ServerLevel serverLevel = (ServerLevel) level;
            boolean inCthiris = serverLevel.dimension().equals(com.redondoguibi.desertsreimagined.dimension.ModDimensions.CTHIRIS_DIMENSION_TYPE.getKey());

            if (inCthiris) {
                DimensionPositionMemory.saveDesertPosition(player, player.blockPosition());
                ServerLevel overworld = serverLevel.getServer().getLevel(net.minecraft.world.level.Level.OVERWORLD);
                if (overworld != null) {
                    BlockPos returnPos = DimensionPositionMemory.getDesertPosition(player);
                    player.changeDimension(overworld, (server, entity, portalDir) -> {
                        entity.teleportTo(overworld, returnPos.getX(), returnPos.getY(), returnPos.getZ(), entity.getYRot(), entity.getXRot());
                    });
                    stack.shrink(1);
                    return InteractionResultHolder.success(stack);
                }
            } else {
                DimensionPositionMemory.saveDesertPosition(player, player.blockPosition());
                ServerLevel cthiris = serverLevel.getServer().getLevel(com.redondoguibi.desertsreimagined.dimension.ModDimensions.CTHIRIS_DIMENSION_TYPE.getKey());
                if (cthiris != null) {
                    player.changeDimension(cthiris, (server, entity, portalDir) -> {
                        entity.teleportTo(cthiris, 18, 12, 3, entity.getYRot(), entity.getXRot());
                    });
                    player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
                    return InteractionResultHolder.success(stack);
                }
            }
        }
        return InteractionResultHolder.pass(stack);
    }
}
