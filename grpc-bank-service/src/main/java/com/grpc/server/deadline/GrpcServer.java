package com.grpc.server.deadline;

import io.grpc.ServerBuilder;

public class GrpcServer {

    public static void main (String[] args) {
        try {
            ServerBuilder.forPort (6565)
                    .addService (new DeadlineBankService ())
                    .build ()
                    .start ()
                    .awaitTermination ();

        } catch (Exception e) {
            throw new RuntimeException (e);
        }
    }
}
