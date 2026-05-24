package edu.sdccd.cisc191.client;

import edu.sdccd.cisc191.client.controller.GameController;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class GameClientApplicationTest {
    // Module 7
    @Test
    void buildJoinLogMessageFormatsRankedMatch() {
        String message = GameController.buildJoinLogMessage("Ada", "Hard", true);

        assertEquals("Joining ranked match as Ada on Hard difficulty...", message);
    }

    @Test
    void buildJoinLogMessageTrimsAndDefaultsInput() {
        String message = GameController.buildJoinLogMessage("   ", "   ", false);

        assertEquals("Joining casual match as Player on Normal difficulty...", message);
    }

    @Test
    void runOnFxThreadUsesPlatformRunLaterForBackgroundThreads() throws IOException {
        String source = Files.readString(Path.of("src/main/java/edu/sdccd/cisc191/controller/GameController.java"));
        String sourceWithoutComments = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL)
                .matcher(source)
                .replaceAll("");

        assertTrue(sourceWithoutComments.contains("Platform.isFxApplicationThread()"),
                "TODO 8: Check Platform.isFxApplicationThread() before updating JavaFX controls.");
        assertTrue(sourceWithoutComments.contains("Platform.runLater(action)"),
                "TODO 8: Use Platform.runLater(action) when code is not already on the JavaFX Application Thread.");
        assertTrue(sourceWithoutComments.contains("runOnFxThread(() ->"),
                "TODO 8: Use runOnFxThread for JavaFX control updates, such as updateView().");
    }

}
