package com.game.client;

import com.grpc.models.GameServiceGrpc;
import com.grpc.models.RollRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;

@TestInstance (TestInstance.Lifecycle.PER_CLASS)
class GameStreamingClientTest {

    private GameServiceGrpc.GameServiceStub stub;

    @BeforeAll
    void setup () {
        final ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress ("localhost", 6565)
                .usePlaintext ()
                .build ();
        stub = GameServiceGrpc.newStub (managedChannel);
    }

    @Test
    void gameTest () throws InterruptedException {
        CountDownLatch latch = new CountDownLatch (1);
        GameStreamingClient client = new GameStreamingClient (latch);
        StreamObserver<RollRequest> startGame = stub.roll (client);

        for (int i = 0; i < 30; i++) {

            final RollRequest request = RollRequest.newBuilder ()
                    .setRandomDice (true)
                    .setClientDice (0)
                    .build ();

            startGame.onNext (request);

        }

        latch.await ();
        startGame.onCompleted ();


    }

}