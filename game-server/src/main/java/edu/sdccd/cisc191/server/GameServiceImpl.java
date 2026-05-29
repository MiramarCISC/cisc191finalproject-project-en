package edu.sdccd.cisc191.server;

import edu.sdccd.cisc191.client.model.enemy.Enemy;
import edu.sdccd.cisc191.client.model.enemy.Ghoul;
import edu.sdccd.cisc191.client.model.enemy.Goblin;
import edu.sdccd.cisc191.client.model.enemy.Ork;
import edu.sdccd.cisc191.grpc.GameServiceGrpc;
import edu.sdccd.cisc191.grpc.JoinMatchRequest;
import edu.sdccd.cisc191.grpc.JoinMatchResponse;
import edu.sdccd.cisc191.grpc.MatchHistoryRequest;
import edu.sdccd.cisc191.grpc.MatchHistoryResponse;
import edu.sdccd.cisc191.grpc.MatchResultResponse;
import edu.sdccd.cisc191.grpc.PlayMatchRequest;
import edu.sdccd.cisc191.server.damage.*;
import edu.sdccd.cisc191.server.repository.MatchRepository;
import edu.sdccd.cisc191.server.repository.PlayerRepository;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameServiceImpl extends GameServiceGrpc.GameServiceImplBase {

    // move repeated enemy creation log to EnemyFactory so it's easier to reuse & maintain
    private final Map<String, ServerMatch> matches = new ConcurrentHashMap<>();
    private final MatchStatistics statistics = new MatchStatistics();
    private final Random random = new Random();
    private  final MatchRepository matchRep = new MatchRepository();
    private  final PlayerRepository playerRep = new PlayerRepository();
    private DamageCalculator calculator;

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

        Enemy enemy;

        switch (difficulty) {
            case "Easy":
                enemy = new Goblin(startingHp);
                break;

            case "Hard":
                enemy = new Ork(startingHp);
                break;

            default:
                enemy = new Ghoul(startingHp);
                break;
        }


        ServerMatch match = new ServerMatch(
                matchId,
                playerName,
                enemy.getName(),
                difficulty,
                ranked,
                startingHp,
                enemy.getHp()
        );

        matches.put(matchId, match);
        statistics.recordJoin();

        JoinMatchResponse response = JoinMatchResponse.newBuilder()
                .setMatchId(matchId)
                .setPlayerName(match.playerName())
                .setOpponentName(match.opponentName())
                .setPlayerHp(match.playerHp())
                .setOpponentHp(match.opponentHp())
                .setMessage("Joined " + match.matchType() + " match " + matchId
                        + " on " + difficulty + " difficulty. Click Play Round to start the match.")
                .setSummary(buildJoinSummary(
                        match.matchId, match.playerName,
                        match.opponentName, match.difficulty,
                        match.ranked))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

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


        switch (match.difficulty()){
            case "Easy":
                calculator = new EasyDamageCalculator();
                break;

            case "Normal":
                calculator = new NormalDamageCalculator();
                break;

            case "Hard":
                calculator = new HardDamageCalculator();
                break;
        }

        int botDamage = calculator.calculateDamage();

        DamageResult playerAttack =
                calcCritDamage(calculator.calculateDamage());

        int playerDamage = playerAttack.getDamage();

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

        if (match.playerHp() <= 0 && match.opponentHp<=0) {
            winner = "Draw";
            loser = match.playerName();
        } else if (match.playerHp() <= 0) {
            winner = match.opponentName();
            loser = match.playerName();
        } else if (match.opponentHp() <= 0) {
            winner = match.playerName();
            loser = match.opponentName();
            playerWon = true;
        }

        String message;

        if (winner.isBlank()) {

            String playerCritText = "";

            if (playerAttack.isCriticalHit()) {
                playerCritText = " CRITICAL HIT!";
            }

            message = match.playerName() + " dealt " + playerDamage
                    + " damage." + playerCritText + "\n"
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

        playerRep.savePlayer(match.playerName());

        if (!winner.isBlank()) {

            matchRep.saveMatch(
                    match.matchId(),
                    match.playerName(),
                    match.opponentName(),
                    winner,
                    match.difficulty(),
                    match.ranked(),
                    match.playerHp(),
                    match.opponentHp()
            );

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

        MatchHistoryResponse.Builder builder =
                MatchHistoryResponse.newBuilder();

        List<String> history = matchRep.getMatchHistory(playerName);

        long wins = history
                .stream()
                .filter(match -> match.contains("Win"))
                .count();


        appendHistory(history, 0, builder);


        builder.addMatches("Total Matches: " + history.size());
        builder.addMatches("Matches Won: " + wins);
        builder.addMatches("Matches Lost " + (history.size() - wins));

        MatchHistoryResponse response = builder.build();


        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public MatchStatistics getStatisticsForTesting() {
        return statistics;
    }


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

    private void appendHistory(
            List<String> matches,
            int index,
            MatchHistoryResponse.Builder builder
    ) {
        if (index >= matches.size()) {
            return;
        }

        builder.addMatches("- " + matches.get(index));

        appendHistory(matches, index + 1, builder);
    }

    private DamageResult calcCritDamage(int baseDamage) {

        int critChance = random.nextInt(20) + 1;

        if (critChance == 20) {

            return new DamageResult(
                    baseDamage * 2,
                    true
            );
        }

        return new DamageResult(
                baseDamage,
                false
        );
    }
}
