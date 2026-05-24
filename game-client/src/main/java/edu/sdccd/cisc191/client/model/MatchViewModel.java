package edu.sdccd.cisc191.client.model;

import java.util.concurrent.atomic.AtomicInteger;

public class MatchViewModel {
    private String matchId;
    private final Player player = new Player("Player");
    private final Player opponent = new Player("Opponent");
    private boolean matchOver;
    private String winnerName = "";

    private AtomicInteger completedMatchCount = new AtomicInteger(0);

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getOpponent() {
        return opponent;
    }

    public boolean isMatchOver() {
        return matchOver;
    }

    public void setMatchOver(boolean matchOver) {
        this.matchOver = matchOver;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName == null ? "" : winnerName;
    }

    public int getCompletedMatchCount() {
        return completedMatchCount.get();
    }

    public void recordCompletedMatchThreadSafely(String winnerName) {
        completedMatchCount.addAndGet(1);
        setWinnerName(winnerName);
        matchOver = true;
    }

    public boolean hasJoinedMatch() {
        return matchId != null && !matchId.isBlank();
    }

    public boolean canPlayMatch() {
        return hasJoinedMatch() && !matchOver;
    }

    public String buildMatchSummary(String difficulty, boolean ranked) {
        if(matchId == null || matchId.isBlank()) {
            return "No match";
        }

        String isRanked = "casual";
        if (ranked){
            isRanked = "ranked";
        }

        if (difficulty == null || difficulty.isBlank()){
            difficulty = "Normal";
        }

        return String.format("Match %s: %s vs %s (%s, %s)",
                matchId, player.getName(), opponent.getName(), difficulty, isRanked);

    }


    public void resetLocalState() {
        matchId = null;
        player.setName("Player");
        opponent.setName("Opponent");
        matchOver = false;
        winnerName = "";
        completedMatchCount = new AtomicInteger(0);
    }
}
