package edu.sdccd.cisc191.client.model.enemy;

public class Ghoul extends Enemy {
    public Ghoul(int playerHp) {
        super(playerHp);
        this.name = "Ghoul";
    }

    @Override
    protected int calculateHp(int playerHp) {
        return playerHp;
    }
}
