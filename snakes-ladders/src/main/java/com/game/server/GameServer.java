package com.game.server;


import com.game.service.GameService;
import io.grpc.ServerBuilder;

public class GameServer {

    public static void main (String[] args) {
        try {
            ServerBuilder.forPort (6565)
                    .addService (new GameService ())
                    .build ()
                    .start ()
                    .awaitTermination ();

        } catch (Exception e) {
            throw new RuntimeException (e);
        }
    }
}
