package edu.sdccd.cisc191.client.model.enemy;

public class Ork extends Enemy {

    public Ork(int playerHp) {
        super(playerHp);
        this.name = "Ork";
    }

    @Override
    protected int calculateHp(int playerHp) {
        // Hard difficulty enemy has 125% of the player's HP
        return (int) (playerHp * 1.25);
    }
}