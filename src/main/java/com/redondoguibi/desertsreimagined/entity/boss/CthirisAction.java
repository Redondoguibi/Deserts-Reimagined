package com.redondoguibi.desertsreimagined.entity.boss;

/**
 * Estados de acao do boss. Sincronizado ao cliente via EntityData
 * para escolher a animacao (sem triggerAnim -> robusto a reconexao/render distante).
 */
public enum CthirisAction {
    NONE   (0,  false, false),
    COMBO  (35, false, false),
    CHARGED(69, true,  false),
    ROAR   (44, false, true),
    SUMMON (56, true,  false),
    PHASE  (80, true,  true),
    DEATH  (96, true,  true);

    public final int duration;
    public final boolean locksMovement;
    public final boolean invulnerable;

    CthirisAction(int duration, boolean locksMovement, boolean invulnerable) {
        this.duration = duration;
        this.locksMovement = locksMovement;
        this.invulnerable = invulnerable;
    }

    public boolean isBusy() {
        return this != NONE;
    }

    public static CthirisAction byId(int id) {
        CthirisAction[] v = values();
        return (id >= 0 && id < v.length) ? v[id] : NONE;
    }
}
