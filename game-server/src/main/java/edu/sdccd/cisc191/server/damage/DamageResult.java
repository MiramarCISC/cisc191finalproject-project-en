package edu.sdccd.cisc191.server.damage;

import java.util.Random;

public class DamageResult {

    private final int damage;
    private final boolean criticalHit;
    private Random random;

    public DamageResult(int damage, boolean criticalHit) {
        this.damage = damage;
        this.criticalHit = criticalHit;
    }

    public int getDamage() {
        return damage;
    }

    public boolean isCriticalHit() {
        return criticalHit;
    }
}