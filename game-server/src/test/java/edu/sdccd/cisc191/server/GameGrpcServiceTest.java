package edu.sdccd.cisc191.server;

import edu.sdccd.cisc191.client.model.Player;
import edu.sdccd.cisc191.client.model.enemy.Enemy;
import edu.sdccd.cisc191.client.model.enemy.Ghoul;
import edu.sdccd.cisc191.grpc.*;
import edu.sdccd.cisc191.server.damage.DamageCalculator;
import edu.sdccd.cisc191.server.damage.HardDamageCalculator;
import edu.sdccd.cisc191.server.repository.MatchRepository;
import edu.sdccd.cisc191.server.repository.PlayerRepository;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameGrpcServiceTest {

    // Module 1
    @Test
    void MatchHistoryStoresResultsTest() {
        MatchRepository repoMatch = new MatchRepository();
        PlayerRepository repoPlayer = new PlayerRepository();

        repoPlayer.savePlayer("Adele");

        repoMatch.saveMatch(
                "1" ,
                        "Adele" ,
                        "Bot" ,
                        "Adele" ,
                        "Hard",
                        true ,
                        50 ,
                        0
        );

        List<String> history = repoMatch.getMatchHistory("Adele");

        assertFalse(history.isEmpty());
    }

    // Module 2
    @Test
    void DamageCalculatorReturnValidDamageTest() {

        DamageCalculator calc = new HardDamageCalculator();
        int damage = calc.calculateDamage();
        assertTrue(damage >= 1 && damage <= 40);
    }

    // module 3
    @Test
    void PolymorphismTest() {
        Enemy e = new Ghoul(100);

        assertEquals(100, e.getHp());
    }

    @Test
    void equalsTest() {
        Enemy e1 = new Ghoul(100);
        Enemy e2 = new Ghoul(100);

        assertEquals(e1.getHp(), e2.getHp());
    }

    // module 5
    @Test
    void MatchHistoryBuilderTest() {
        GameServiceImpl service = new GameServiceImpl();
        final MatchHistoryResponse[] holder = new MatchHistoryResponse[1];
        MatchHistoryRequest request = MatchHistoryRequest.newBuilder()
                .setPlayerName("Adele")
                .build();

        service.loadMatchHistory(request, new StreamObserver<>() {
            @Override
            public void onNext(MatchHistoryResponse value) {
                holder[0] = value;
            }

            @Override
            public void onError(Throwable t) {
                fail(t);
            }

            @Override
            public void onCompleted() {}
        });

        assertTrue(holder[0] != null);
    }

    // module 6
    @Test
    void Module6Test() {

        GameServiceImpl service = new GameServiceImpl();

        final MatchHistoryResponse[] historyHolder = new MatchHistoryResponse[1];

        JoinMatchRequest joinRequest = JoinMatchRequest.newBuilder()
                .setPlayerName("Adele")
                .setDifficulty("Normal")
                .setStartingHp(100)
                .setOpponentHp(100)
                .build();

        service.joinMatch(joinRequest, new io.grpc.stub.StreamObserver<JoinMatchResponse>() {

            @Override
            public void onNext(JoinMatchResponse value) {}

            @Override
            public void onError(Throwable t) {
                fail();
            }

            @Override
            public void onCompleted() {}
        });

        PlayMatchRequest playRequest = PlayMatchRequest.newBuilder()
                .setMatchId("1") // safe fallback
                .build();

        service.playMatch(playRequest, new io.grpc.stub.StreamObserver<MatchResultResponse>() {

            @Override
            public void onNext(MatchResultResponse value) {}

            @Override
            public void onError(Throwable t) {
                fail();
            }

            @Override
            public void onCompleted() {}
        });

        MatchHistoryRequest historyRequest = MatchHistoryRequest.newBuilder()
                .setPlayerName("Adele")
                .build();

        service.loadMatchHistory(historyRequest, new io.grpc.stub.StreamObserver<MatchHistoryResponse>() {

            @Override
            public void onNext(MatchHistoryResponse value) {
                historyHolder[0] = value;
            }

            @Override
            public void onError(Throwable t) {
                fail();
            }

            @Override
            public void onCompleted() {}
        });

        assertNotNull(historyHolder[0]);
    }


}

