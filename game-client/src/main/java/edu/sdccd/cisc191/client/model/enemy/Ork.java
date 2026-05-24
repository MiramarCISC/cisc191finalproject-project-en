package edu.sdccd.cisc191.client.model.enemy;

public class Ork extends Enemy {
    public Ork(int playerHp) {
        super(playerHp);
        this.name = "Ork";
    }

    @Override
    protected int calculateHp(int playerHp) {
        return (int)(playerHp * 1.25);
    }
}
