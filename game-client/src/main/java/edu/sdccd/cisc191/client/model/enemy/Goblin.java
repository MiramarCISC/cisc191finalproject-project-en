package edu.sdccd.cisc191.client.model.enemy;

public class Goblin extends Enemy {

    public Goblin(int playerHp) {
        super(playerHp);
        this.name = "Goblin";
    }

    @Override
    protected int calculateHp(int playerHp) {
        // Easy difficulty enemy has 75% of the player's HP
        return (int) (playerHp * 0.75);
    }
}