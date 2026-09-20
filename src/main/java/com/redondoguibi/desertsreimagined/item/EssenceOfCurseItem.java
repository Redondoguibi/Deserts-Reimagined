package com.redondoguibi.desertsreimagined.item;

import com.redondoguibi.desertsreimagined.dimension.DimensionPositionMemory;
import com.redondoguibi.desertsreimagined.dimension.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
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

        if (level.isClientSide()) {
            return InteractionResultHolder.pass(stack);
        }

        ServerLevel serverLevel = (ServerLevel) level;
        boolean inCthiris = serverLevel.dimension().equals(ModDimensions.CTHIRIS_LEVEL);

        // Dentro de C'Thiris, o item sempre pode devolver o jogador ao ponto salvo.
        if (inCthiris) {
            ServerLevel overworld = serverLevel.getServer().getLevel(Level.OVERWORLD);
            if (overworld == null) {
                return InteractionResultHolder.fail(stack);
            }

            BlockPos returnPos = DimensionPositionMemory.getDesertPosition(player);
            Vec3 destination = new Vec3(
                    returnPos.getX() + 0.5D,
                    returnPos.getY(),
                    returnPos.getZ() + 0.5D
            );

            player.changeDimension(new DimensionTransition(
                    overworld,
                    destination,
                    Vec3.ZERO,
                    player.getYRot(),
                    player.getXRot(),
                    DimensionTransition.DO_NOTHING
            ));

            stack.shrink(1);
            return InteractionResultHolder.success(stack);
        }

        // Entrada em C'Thiris só é permitida a partir do deserto vanilla.
        boolean isDesert = level.getBiome(player.blockPosition()).is(Biomes.DESERT);
        if (!isDesert) {
            return InteractionResultHolder.fail(stack);
        }

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        DimensionPositionMemory.saveDesertPosition(player, player.blockPosition());

        ServerLevel cthiris = serverLevel.getServer().getLevel(ModDimensions.CTHIRIS_LEVEL);
        if (cthiris == null) {
            return InteractionResultHolder.fail(stack);
        }

        Vec3 destination = new Vec3(18.5D, 12.0D, 3.5D);
        player.changeDimension(new DimensionTransition(
                cthiris,
                destination,
                Vec3.ZERO,
                player.getYRot(),
                player.getXRot(),
                DimensionTransition.DO_NOTHING
        ));

        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        return InteractionResultHolder.success(stack);
    }
}
