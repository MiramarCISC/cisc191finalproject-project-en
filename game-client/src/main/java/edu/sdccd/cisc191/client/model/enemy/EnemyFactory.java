package edu.sdccd.cisc191.client.model.enemy;

public class EnemyFactory {
    public static Enemy create(String difficulty, int playerHp) {

        return switch (difficulty) {
            case "Easy" -> new Goblin(playerHp);
            case "Hard" -> new Ork(playerHp);
            default -> new Ghoul(playerHp);
        };
    }
}
