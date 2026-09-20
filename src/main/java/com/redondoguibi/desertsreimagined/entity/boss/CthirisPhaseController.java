package com.redondoguibi.desertsreimagined.entity.boss;

import com.redondoguibi.redondoguibilib.api.IBoss;
import com.redondoguibi.redondoguibilib.api.IBossPhase;

import java.util.HashMap;
import java.util.Map;

/**
 * Gerenciador minimo de IBossPhase.
 * Substituivel pelo PhaseManager da RedondoguibiLib quando o pacote exato for confirmado.
 */
public class CthirisPhaseController {

    private final IBoss boss;
    private final Map<Integer, IBossPhase> phases = new HashMap<>();
    private IBossPhase current;
    private int currentIndex = -1;

    public CthirisPhaseController(IBoss boss) {
        this.boss = boss;
    }

    public void addPhase(int index, IBossPhase phase) {
        phases.put(index, phase);
    }

    public void setPhase(int index) {
        if (index == currentIndex) return;
        IBossPhase next = phases.get(index);
        if (next == null) return;
        if (current != null) current.onExit(boss);
        current = next;
        currentIndex = index;
        current.onEnter(boss);
    }

    public void tick() {
        if (current != null) current.onTick(boss);
    }

    public IBossPhase getCurrent()    { return current; }
    public int getCurrentPhaseIndex() { return currentIndex; }
}
