package com.redondoguibi.desertsreimagined.client.sound;

import com.redondoguibi.desertsreimagined.entity.boss.CthirisGodEntity;
import com.redondoguibi.desertsreimagined.registry.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class CthirisMusicSound extends AbstractTickableSoundInstance {

    private final CthirisGodEntity boss;

    public CthirisMusicSound(CthirisGodEntity boss) {
        super(ModSounds.CTHIRIS_BOSSFIGHT.get(), SoundSource.MUSIC, RandomSource.create());
        this.boss = boss;
        this.looping = true;
        this.delay = 0;
        this.volume = 1.0F;
        this.relative = true;
        this.x = 0.0D;
        this.y = 0.0D;
        this.z = 0.0D;
    }

    public CthirisGodEntity getBoss() {
        return boss;
    }

    @Override
    public void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || boss.isRemoved() || boss.getHealth() <= 0.0F
                || boss.distanceToSqr(mc.player) > 64.0D * 64.0D) {
            this.stop();
        }
    }
}
