package com.redondoguibi.desertsreimagined.block.entity;

import com.redondoguibi.desertsreimagined.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class AlgathorStatuePartBlockEntity extends BlockEntity {

    private BlockPos corePos = BlockPos.ZERO;

    public AlgathorStatuePartBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ALGATHOR_STATUE_PART.get(), pos, state);
    }

    public BlockPos getCorePos() {
        return corePos;
    }

    public void setCorePos(BlockPos corePos) {
        this.corePos = corePos.immutable();
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("CoreX", corePos.getX());
        tag.putInt("CoreY", corePos.getY());
        tag.putInt("CoreZ", corePos.getZ());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.corePos = new BlockPos(tag.getInt("CoreX"), tag.getInt("CoreY"), tag.getInt("CoreZ"));
    }

    // ---- sincronização com o cliente (essencial para a colisão não desincronizar) ----

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}