package com.game.server;

import com.game.repository.MoveTrackerRepository;
import com.game.repository.SnakeAndLaddersRepository;
import com.grpc.models.*;
import io.grpc.stub.StreamObserver;

import java.util.Random;

public class GameStreamingRequest implements StreamObserver<RollRequest> {

    public static final int GAME_LENGTH = 60;
    private final StreamObserver<RollResponse> rollResponseStreamObserver;
    private final MoveTrackerRepository moveTrackerRepository;
    private final SnakeAndLaddersRepository snakeAndLaddersRepository;

    public GameStreamingRequest (StreamObserver<RollResponse> rollResponseStreamObserver) {
        this.rollResponseStreamObserver = rollResponseStreamObserver;
        this.moveTrackerRepository = new MoveTrackerRepository ();
        snakeAndLaddersRepository = new SnakeAndLaddersRepository (GAME_LENGTH);
    }

    @Override
    public void onNext (final RollRequest rollRequest) {
        NewMove clientNewMove = moveClient (rollRequest);
        NewMove serverNewMove = moveServer (rollRequest);

        int clientNewPosition = clientNewMove.getNewPosition ();
        int serverNewPosition = serverNewMove.getNewPosition ();

        RollResponse rollResponse = null;

        int moveCount = moveTrackerRepository.getMoveCount ();
        if (checkGameStatus (clientNewPosition, serverNewPosition) == Status.GAME_OVER) {

            Winner winner = checkTheWinner (clientNewPosition, serverNewPosition);

            rollResponse = RollResponse.newBuilder ()
                    .setClientPosition (clientNewPosition)
                    .setServerPosition (serverNewPosition)
                    .setMoveCount (moveCount)
                    .setStatus (Status.GAME_OVER)
                    .setServerLastMoveType (serverNewMove.getMoveType ())
                    .setClientLastMoveType (clientNewMove.getMoveType ())
                    .setWinner (winner)
                    .build ();
        } else {
            moveCount += 1;
            moveTrackerRepository.setMoveCount (moveCount);

            rollResponse = RollResponse.newBuilder ()
                    .setClientPosition (clientNewPosition)
                    .setServerPosition (serverNewPosition)
                    .setMoveCount (moveCount)
                    .setStatus (Status.PLAYING)
                    .setServerLastMoveType (serverNewMove.getMoveType ())
                    .setClientLastMoveType (clientNewMove.getMoveType ())
                    .setWinner (Winner.NONE)
                    .build ();
        }
        rollResponseStreamObserver.onNext (rollResponse);
    }

    private Winner checkTheWinner (final int clientNewPosition, final int serverNewPosition) {
        if (clientNewPosition == GAME_LENGTH) {
            return Winner.CLIENT;
        }
        return Winner.SERVER;
    }

    private Status checkGameStatus (final int clientNewPosition, final int serverNewPosition) {

        if (clientNewPosition == GAME_LENGTH || serverNewPosition == GAME_LENGTH) {
            return Status.GAME_OVER;

        }
        return Status.PLAYING;
    }

    private NewMove moveServer (final RollRequest rollRequest) {
        int serverOldPosition = moveTrackerRepository.getServerPosition ();
        int clientOldPosition = moveTrackerRepository.getClientPosition ();

        int serverNewPosition = 0;
        if (serverOldPosition == GAME_LENGTH || clientOldPosition == GAME_LENGTH) {
            serverNewPosition = serverOldPosition;
        } else {
            int serverDiceValue = new Random ().nextInt (6) + 1;
            serverNewPosition = calculateNewPosition (serverOldPosition, serverDiceValue);
        }

        NewMove move = checkSnakeAndLadder (serverNewPosition);
        moveTrackerRepository.setServerPosition (move.getNewPosition ());

        return move;
    }

    private NewMove checkSnakeAndLadder (final int calculatedPosition) {
        return snakeAndLaddersRepository.getMoveType (calculatedPosition);

    }

    private NewMove moveClient (final RollRequest rollRequest) {
        int clientDiceValue = 0;
        int clientOldPosition = moveTrackerRepository.getClientPosition ();
        int serverOldPosition = moveTrackerRepository.getServerPosition ();

        if (rollRequest.getRandomDice () || rollRequest.getClientDice () < 1 || rollRequest.getClientDice () > 6) {
            clientDiceValue = new Random ().nextInt (6) + 1;
        } else {
            clientDiceValue = rollRequest.getClientDice ();
        }
        int clientNewPosition = 0;

        if (serverOldPosition == GAME_LENGTH || clientOldPosition == GAME_LENGTH) {
            clientNewPosition = clientOldPosition;
            //   onCompleted ();
        } else {
            clientNewPosition = calculateNewPosition (clientOldPosition, clientDiceValue);
        }


        NewMove move = checkSnakeAndLadder (clientNewPosition);
        moveTrackerRepository.setClientPosition (move.getNewPosition ());
        return move;
    }

    private int calculateNewPosition (final int oldPosition, final int diceValue) {
        int newPosition = oldPosition + diceValue;
        if (newPosition > GAME_LENGTH) {
            return GAME_LENGTH - (newPosition - GAME_LENGTH);
        }
        return newPosition;
    }

    @Override
    public void onError (final Throwable throwable) {
    }

    @Override
    public void onCompleted () {
        rollResponseStreamObserver.onCompleted ();
    }
}
