package com.game.client;

import com.grpc.models.RollRequest;
import com.grpc.models.RollResponse;
import com.grpc.models.Status;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;


public class GameStreamingClient implements StreamObserver<RollResponse> {

    Logger logger = Logger.getLogger (GameStreamingClient.class.getName ());

    private final CountDownLatch latch;
    private StreamObserver<RollRequest> rollRequestStreamObserver;

    public GameStreamingClient (CountDownLatch latch) {
        this.latch = latch;
    }

    public void roll (RollRequest request) {
        this.rollRequestStreamObserver.onNext (request);
    }

    @Override
    public void onNext (final RollResponse rollResponse) {

        Status status = rollResponse.getStatus ();
        int clientPosition = rollResponse.getClientPosition ();
        int serverPosition = rollResponse.getServerPosition ();
        int moveCount = rollResponse.getMoveCount ();

        logger.info (
                "Game Status: " + status
                        + " Winner: " + rollResponse.getWinner ()
                        + " Move count: " + moveCount
                        + " Client position: " + clientPosition
                        + " Server position: " + serverPosition
                        + " Last server move: " + rollResponse.getServerLastMoveType ()
                        + " Last client move: " + rollResponse.getClientLastMoveType ()
        );
        logger.info ("------------------");

        if (status == Status.GAME_OVER) {
            logger.info ("Game over");
           rollRequestStreamObserver.onCompleted ();
        } else {
            this.rollAgain ();
        }

    }

    private void rollAgain () {

        RollRequest rollRequest = RollRequest.newBuilder ()
                .setClientDice (0)
                .setRandomDice (false)
                .build ();
        this.rollRequestStreamObserver.onNext (rollRequest);
    }

    public void setRollRequestStreamObserver (final StreamObserver<RollRequest> rollRequestStreamObserver) {
        this.rollRequestStreamObserver = rollRequestStreamObserver;
    }

    @Override
    public void onError (final Throwable throwable) {

    }

    @Override
    public void onCompleted () {
        logger.info ("All rolls completed");
        latch.countDown ();

    }
}
