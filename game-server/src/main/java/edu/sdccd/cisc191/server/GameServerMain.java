package edu.sdccd.cisc191.server;

import edu.sdccd.cisc191.server.util.DatabaseInitializer;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GameServerMain {

    private static final int PORT = 50051;

    public static void main(String[] args) throws IOException, InterruptedException {

        DatabaseInitializer.initialize();

        Server server = ServerBuilder
                .forPort(PORT)
                .addService(new GameServiceImpl())
                .build();

        server.start();

        System.out.println("1v1 gRPC Game Server started on port " + PORT);
        System.out.println("Press Ctrl+C to stop.");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Stopping gRPC Game Server...");
            server.shutdown();
        }));

        server.awaitTermination();
    }
}
//peer reviewed