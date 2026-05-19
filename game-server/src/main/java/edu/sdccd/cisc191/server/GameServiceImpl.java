package edu.sdccd.cisc191.server;

import edu.sdccd.cisc191.grpc.GameServiceGrpc;
import edu.sdccd.cisc191.grpc.JoinMatchRequest;
import edu.sdccd.cisc191.grpc.JoinMatchResponse;
import edu.sdccd.cisc191.grpc.MatchHistoryRequest;
import edu.sdccd.cisc191.grpc.MatchHistoryResponse;
import edu.sdccd.cisc191.grpc.MatchResultResponse;
import edu.sdccd.cisc191.grpc.PlayMatchRequest;
import io.grpc.stub.StreamObserver;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameServiceImpl extends GameServiceGrpc.GameServiceImplBase {

    private final Map<String, ServerMatch> matches = new ConcurrentHashMap<>();
    private final MatchStatistics statistics = new MatchStatistics();
    private final Random random = new Random();

    @Override
    public void joinMatch(
            JoinMatchRequest request,
            StreamObserver<JoinMatchResponse> responseObserver
    ) {
        String playerName = request.getPlayerName().isBlank()
                ? "Player"
                : request.getPlayerName();

        String difficulty = request.getDifficulty().isBlank()
                ? "Normal"
                : request.getDifficulty();

        boolean ranked = request.getRanked();
        String matchId = UUID.randomUUID().toString();

        int startingHp = request.getStartingHp();
        //int startingHp = 100;
        ServerMatch match = new ServerMatch(
                matchId,
                playerName,
                "Bot (" + difficulty + ")",
                difficulty,
                ranked,
                startingHp,
                startingHp
        );

        matches.put(matchId, match);
        statistics.recordJoin();

        JoinMatchResponse response = JoinMatchResponse.newBuilder()
                .setMatchId(matchId)
                .setPlayerName(match.playerName())
                .setOpponentName(match.opponentName())
                .setMessage("Joined " + match.matchType() + " match " + matchId
                        + " on " + difficulty + " difficulty. Click Play Match to let the server choose a winner.")
                .setSummary(buildJoinSummary(
                        match.matchId, match.playerName,
                        match.opponentName, match.difficulty,
                        match.ranked))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * TODO 6: Complete this server-side summary helper, then use it in JoinMatchResponse
     * after adding the new summary field to the .proto file.
     *
     * Expected format:
     * Match match-001: Ada vs Bot (Hard, ranked)
     *
     * Requirements:
     * - Use "No match" when matchId is null or blank.
     * - Use "Player" when playerName is null or blank.
     * - Use "Bot" when opponentName is null or blank.
     * - Use "Normal" when difficulty is null or blank.
     * - Use "ranked" when ranked is true, otherwise "casual".
     */
    public static String buildJoinSummary(
            String matchId,
            String playerName,
            String opponentName,
            String difficulty,
            boolean ranked
    ) {
        if(matchId == null || matchId.isBlank()){
            return "No match";
        }

        if(playerName == null || playerName.isBlank()){
            playerName = "Player";
        }

        if(opponentName == null || opponentName.isBlank()){
            opponentName = "Bot";
        }

        if(difficulty == null || difficulty.isBlank()){
            difficulty = "Normal";
        }

        String isRanked = "casual";
        if(ranked) isRanked = "ranked";

        return String.format("Match %s: %s vs %s (%s, %s)",
                matchId, playerName.trim(), opponentName.trim(), difficulty, isRanked);
    }

    @Override
    public void playMatch(
            PlayMatchRequest request,
            StreamObserver<MatchResultResponse> responseObserver
    ) {
        ServerMatch match = matches.get(request.getMatchId());

        if (match == null) {
            responseObserver.onNext(MatchResultResponse.newBuilder()
                    .setMatchId(request.getMatchId())
                    .setWinnerName("No winner")
                    .setLoserName("No loser")
                    .setMessage("Match not found. Join a match first.")
                    .setPlayerWon(false)
                    .build());
            responseObserver.onCompleted();
            return;
        }


        int playerDamage = random.nextInt(25) + 1;
        int botDamage = random.nextInt(25) + 1;

        match.setPlayerHp(match.playerHp() - botDamage);
        match.setOpponentHp(match.opponentHp() - playerDamage);

        if (match.playerHp() < 0) {
            match.setPlayerHp(0);
        }

        if (match.opponentHp() < 0) {
            match.setOpponentHp(0);
        }

        statistics.recordCompletion();

        boolean playerWon = false;

        String winner = "";
        String loser = "";

        if (match.playerHp() <= 0) {
            winner = match.opponentName();
            loser = match.playerName();
        }
        else if (match.opponentHp() <= 0) {
            winner = match.playerName();
            loser = match.opponentName();
            playerWon = true;
        }

        String message;

        if (winner.isBlank()) {
            message = match.playerName() + " dealt " + playerDamage
                    + " damage. "
                    + match.opponentName() + " dealt " + botDamage
                    + " damage.\n"
                    + match.playerName() + " HP: " + match.playerHp()
                    + " | "
                    + match.opponentName() + " HP: " + match.opponentHp();
        } else {
            message = "Server result: " + winner
                    + " defeated " + loser
                    + " in a "
                    + match.matchType() + " "
                    + match.difficulty() + " match.";
        }

        MatchResultResponse response = MatchResultResponse.newBuilder()
                .setMatchId(match.matchId())
                .setWinnerName(winner)
                .setLoserName(loser)
                .setPlayerWon(playerWon)
                .setPlayerHp(match.playerHp())
                .setOpponentHp(match.opponentHp())
                .setMessage(message)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void loadMatchHistory(
            MatchHistoryRequest request,
            StreamObserver<MatchHistoryResponse> responseObserver
    ) {
        String playerName = request.getPlayerName().isBlank()
                ? "Player"
                : request.getPlayerName();

        MatchHistoryResponse response = MatchHistoryResponse.newBuilder()
                .addMatches(playerName + " vs Bot: Win")
                .addMatches(playerName + " vs Bot: Loss")
                .addMatches(playerName + " vs Bot: Win")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public MatchStatistics getStatisticsForTesting() {
        return statistics;
    }

//    private record ServerMatch(
//            String matchId,
//            String playerName,
//            String opponentName,
//            String difficulty,
//            boolean ranked,
//            int playerHp,
//            int opponentHp
//
//    ) {
//        private String matchType() {
//            return ranked ? "ranked" : "casual";
//        }
//    }

    private static class ServerMatch {

        private final String matchId;
        private final String playerName;
        private final String opponentName;
        private final String difficulty;
        private final boolean ranked;

        private int playerHp;
        private int opponentHp;

        public ServerMatch(
                String matchId,
                String playerName,
                String opponentName,
                String difficulty,
                boolean ranked,
                int playerHp,
                int opponentHp
        ) {
            this.matchId = matchId;
            this.playerName = playerName;
            this.opponentName = opponentName;
            this.difficulty = difficulty;
            this.ranked = ranked;
            this.playerHp = playerHp;
            this.opponentHp = opponentHp;
        }

        public String matchId() { return matchId; }
        public String playerName() { return playerName; }
        public String opponentName() { return opponentName; }
        public String difficulty() { return difficulty; }
        public boolean ranked() { return ranked; }

        public int playerHp() { return playerHp; }
        public int opponentHp() { return opponentHp; }

        public void setPlayerHp(int hp) { playerHp = hp; }
        public void setOpponentHp(int hp) { opponentHp = hp; }

        public String matchType() {
            return ranked ? "ranked" : "casual";
        }
    }
}
