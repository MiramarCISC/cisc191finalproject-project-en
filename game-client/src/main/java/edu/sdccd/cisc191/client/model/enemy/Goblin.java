package edu.sdccd.cisc191.client.model.enemy;

public class Goblin extends Enemy {

    public Goblin(int playerHp) {
        super(playerHp);
        this.name = "Goblin";
    }

    @Override
    protected int calculateHp(int playerHp) {
        return (int)(playerHp * 0.75);
    }
}