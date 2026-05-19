package edu.sdccd.cisc191.client.model;


public class Player {
    private String name;
    private int hp;

    public Player(String name) {
        setName(name);
        setHp(50);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            this.name = "Player";
        } else {
            this.name = name;
        }
    }

    public void setHp(int hp) {
        this.hp = Math.max(hp, 0);
    }

    public int getHp () {
        return hp;
    }
}