package edu.sdccd.cisc191.client.model.enemy;

public class EnemyFactory {

    private static final String EASY = "Easy";
    private static final String HARD = "Hard";

    public static Enemy create(String difficulty, int playerHp) {

        return switch (difficulty) {
            case EASY -> new Goblin(playerHp);
            case HARD -> new Ork(playerHp);
            default -> new Ghoul(playerHp);
        };
    }
}