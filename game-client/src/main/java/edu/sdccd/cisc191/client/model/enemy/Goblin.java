package edu.sdccd.cisc191.client.model.enemy;

public class Goblin extends Enemy {

    public Goblin(int playerHp) {
        super(playerHp);
        this.name = "Goblin";
    }

    // explain why goblins use 0.75 multiplier so others who look at it can understand
    @Override
    protected int calculateHp(int playerHp) {
        return (int)(playerHp * 0.75);
    }
}