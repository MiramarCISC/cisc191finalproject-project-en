package edu.sdccd.cisc191.client.model.enemy;

public abstract class Enemy {
    protected String name;
    protected int hp;

    public Enemy(int playerHp) {
        this.hp = calculateHp(playerHp);
    }

    protected abstract int calculateHp(int playerHp);

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }
}
