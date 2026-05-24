package edu.sdccd.cisc191.server.damage;

import java.util.Random;

public class EasyDamageCalculator implements DamageCalculator {
    private final Random random = new Random();

    @Override
    public int calculateDamage() {
        return random.nextInt(10) + 1;
    }

}
