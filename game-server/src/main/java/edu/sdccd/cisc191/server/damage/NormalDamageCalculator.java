package edu.sdccd.cisc191.server.damage;

import java.util.Random;

public  class NormalDamageCalculator implements DamageCalculator {
    private final Random random = new Random();

    @Override
    public int calculateDamage() {
        return random.nextInt(20) + 1;
    }
}
