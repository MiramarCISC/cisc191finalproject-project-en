package edu.sdccd.cisc191.client.controller;

import edu.sdccd.cisc191.grpc.JoinMatchResponse;
import edu.sdccd.cisc191.grpc.MatchHistoryResponse;
import edu.sdccd.cisc191.grpc.MatchResultResponse;
import edu.sdccd.cisc191.client.model.MatchViewModel;
import edu.sdccd.cisc191.client.service.GameGrpcClient;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class GameController {

    @FXML
    private TextField playerNameField;

    @FXML
    private Label statusLabel;

    @FXML
    private Label playerLabel;

    @FXML
    private Label opponentLabel;

    @FXML
    private Label winnerLabel;

    @FXML
    private Label matchSummaryLabel;

    @FXML
    private TextArea matchLog;

    @FXML
    private ComboBox<String> difficultyComboBox;

    @FXML
    private CheckBox rankedMatchCheckBox;

    @FXML
    private TextField hpField;

    @FXML
    private Label opponentHpLabel;

    @FXML
    private Label playerHpLabel;


    private final MatchViewModel match = new MatchViewModel();

    private final GameGrpcClient grpcClient = new GameGrpcClient("localhost", 50051);

    @FXML
    private void initialize() {
        difficultyComboBox.getItems().addAll("Easy", "Normal", "Hard");
        difficultyComboBox.setValue("Normal");

        match.resetLocalState();
        updateView();
        matchLog.appendText("Client loaded. Start the gRPC server, then click Join Match.\n");
    }

    @FXML
    private void handleJoinMatch() {
        String playerName = getPlayerName();
        String difficulty = difficultyComboBox.getValue();
        boolean ranked = rankedMatchCheckBox.isSelected();
        int startingHp = getStartingHp();

        int opponentHp;
        if (difficulty.equals("Hard")){
             opponentHp = (int)(startingHp * 1.5);
        } else if (difficulty.equals("Easy")){
             opponentHp = (int)(startingHp * 0.75);
        } else {
            opponentHp = startingHp;
        }

        statusLabel.setText("Status: Joining match...");
        matchLog.appendText(buildJoinLogMessage(playerName, difficulty, ranked) + "\n");

        Task<JoinMatchResponse> task = grpcClient.joinMatchTask(
                playerName,
                difficulty,
                ranked,
                startingHp
        );

        task.setOnSucceeded(event -> {
            JoinMatchResponse response = task.getValue();

            match.setMatchId(response.getMatchId());
            match.getPlayer().setName(response.getPlayerName());
            match.getOpponent().setName(response.getOpponentName());
            match.getPlayer().setHp(startingHp);
            match.getOpponent().setHp(opponentHp);
            match.setMatchOver(false);
            match.setWinnerName("");

            statusLabel.setText("Status: Match ready");
            matchLog.appendText(response.getMessage() + "\n");

            updateView();
        });

        task.setOnFailed(event -> {
            statusLabel.setText("Status: Server unavailable");
            matchLog.appendText("Could not join match. Is the gRPC server running?\n");
            matchLog.appendText("Error: " + task.getException().getMessage() + "\n");
        });

        runInBackground(task);
    }

    @FXML
    private void handlePlayMatch() {
        if (!match.canPlayMatch()) {
            matchLog.appendText("Join a match before playing, or reset after a completed match.\n");
            return;
        }

        statusLabel.setText("Status: Playing match...");
        matchLog.appendText("Playing turn...\n");

        Task<MatchResultResponse> task = grpcClient.playMatchTask(
                match.getMatchId(),
                match.getPlayer().getName()
        );

        task.setOnSucceeded(event -> {
            MatchResultResponse response = task.getValue();

            match.getPlayer().setHp(response.getPlayerHp());
            match.getOpponent().setHp(response.getOpponentHp());

            matchLog.appendText(response.getMessage() + "\n");

            if (!response.getWinnerName().isBlank()) {
                match.recordCompletedMatchThreadSafely(response.getWinnerName());

                statusLabel.setText(response.getPlayerWon()
                        ? "Status: You won!"
                        : "Status: You lost.");
            } else {
                statusLabel.setText("Status: Match in progress...");
            }

            updateView();
        });

        task.setOnFailed(event -> {
            statusLabel.setText("Status: Match failed");
            matchLog.appendText("Could not play match.\n");
            matchLog.appendText("Error: " + task.getException().getMessage() + "\n");
        });

        runInBackground(task);
    }

    @FXML
    private void handleLoadHistory() {
        String playerName = getPlayerName();

        matchLog.appendText("Loading match history from gRPC server...\n");

        Task<MatchHistoryResponse> task = grpcClient.loadMatchHistoryTask(playerName);

        task.setOnSucceeded(event -> {
            MatchHistoryResponse response = task.getValue();

            matchLog.appendText("Match history:\n");
            for (String line : response.getMatchesList()) {
                matchLog.appendText("- " + line + "\n");

            }
        });

        task.setOnFailed(event -> {
            matchLog.appendText("Could not load match history.\n");
            matchLog.appendText("Error: " + task.getException().getMessage() + "\n");
        });

        runInBackground(task);
    }

    @FXML
    private void handleResetLocalView() {
        match.resetLocalState();
        statusLabel.setText("Status: Local view reset");
        matchLog.appendText("Local client view reset. Click Join Match for a new server match.\n");
        updateView();
    }

    private String getPlayerName() {
        String typedName = playerNameField.getText();

        if (typedName == null || typedName.isBlank()) {
            return "Player";
        }

        return typedName.trim();
    }

    private int getStartingHp() {
        try {
            String text = hpField.getText();
            if (text == null || text.isBlank()) {
                return 100;
            }
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return 100;
        }
    }

    private void updateView() {
        runOnFxThread(() -> {
            playerLabel.setText("Player: " + match.getPlayer().getName());
            opponentLabel.setText("Opponent: " + match.getOpponent().getName());

            if (match.getWinnerName().isBlank()) {
                winnerLabel.setText("Winner: TBD");
            } else {
                winnerLabel.setText("Winner: " + match.getWinnerName());
            }

            playerHpLabel.setText("Player: " + match.getPlayer().getHp());
            opponentHpLabel.setText("Opponent: " + match.getOpponent().getHp());

            if (matchSummaryLabel != null) {
                matchSummaryLabel.setText("Summary: "
                        + match.buildMatchSummary(difficultyComboBox.getValue(), rankedMatchCheckBox.isSelected()));
            }
        });
    }

    public static String buildJoinLogMessage(String playerName, String difficulty, boolean ranked) {

        String isRanked = "casual";

        if(ranked) isRanked = "ranked";

        if(playerName == null || playerName.isBlank()){
            playerName = "Player";
        }

        if(difficulty == null || difficulty.isBlank()){
            difficulty = "Normal";
        }

        return String.format("Joining %s match as %s on %s difficulty...",
                isRanked, playerName.trim(), difficulty.trim());
    }

    public static void runOnFxThread(Runnable action) {
        if (action != null) {
            if (Platform.isFxApplicationThread()) {
                action.run();
            } else{
                Platform.runLater(action);
            }
        }
    }

    private void runInBackground(Task<?> task) {
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}
