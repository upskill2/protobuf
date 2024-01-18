package com.game.repository;

import com.grpc.models.MoveType;
import com.grpc.models.NewMove;

import java.util.Map;

public class SnakeAndLaddersRepository {

    private final Integer size;
    private final Map<Integer, Integer> snakes;
    private final Map<Integer, Integer> ladders;

    public SnakeAndLaddersRepository (Integer size) {
        this.size = size;
        ladders = Map.of (
                (int) (size * 0.1), (int) (size * 0.2),
                (int) (size * 0.25), (int) (size * 0.3),
                (int) (size * 0.45), (int) (size * 0.75),
                (int) (size * 0.5), (int) (size * 0.9)
        );
        snakes = Map.of (
                (int) (size * 0.2), (int) (size * 0.15),
                (int) (size * 0.4), (0),
                (int) (size * 0.6), (int) (size * 0.4),
                (int) (size * 0.8), (int) (size * 0.15)
        );
    }

    public NewMove getMoveType (int position) {
        if (snakes.containsKey (position)) {
            return NewMove.newBuilder ()
                    .setMoveType (MoveType.SNAKE)
                    .setNewPosition (snakes.get (position))
                    .build ();
        } else if (ladders.containsKey (position)) {
            return NewMove.newBuilder ()
                    .setMoveType (MoveType.LADDER)
                    .setNewPosition (ladders.get (position))
                    .build ();

        }
        return NewMove.newBuilder ()
                .setMoveType (MoveType.NORMAL)
                .setNewPosition (position)
                .build ();
    }
}
