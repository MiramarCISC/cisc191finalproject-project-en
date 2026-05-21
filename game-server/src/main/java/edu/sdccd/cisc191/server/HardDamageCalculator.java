package edu.sdccd.cisc191.server;

import java.util.Random;

public  class HardDamageCalculator implements DamageCalculator {
    private Random random = new Random();

    @Override
    public int calculateDamage() {
        return random.nextInt(40) + 1;
    }
}
