package edu.sdccd.cisc191.server;

import edu.sdccd.cisc191.grpc.MatchHistoryRequest;
import edu.sdccd.cisc191.grpc.MatchHistoryResponse;
import edu.sdccd.cisc191.server.repository.MatchRepository;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class GameGrpcServiceTest {

    // Module 1
    @Test
    void MatchHistoryStoresResultsTest() {
        MatchRepository repo = new MatchRepository();

        repo.saveMatch(
                "1" ,
                        "Adele" ,
                        "Bot" ,
                        "Adele" ,
                        "Hard",
                        true ,
                        50 ,
                        0
        );

        List<String> history = repo.getMatchHistory("Adele");

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
    
}