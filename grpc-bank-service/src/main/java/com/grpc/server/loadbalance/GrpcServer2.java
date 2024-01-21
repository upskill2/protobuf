package com.grpc.server.loadbalance;

import io.grpc.ServerBuilder;

public class GrpcServer2 {

    public static void main (String[] args) {
        try {
            ServerBuilder.forPort (7575)
                    .addService (new BankService ())
                    .build ()
                    .start ()
                    .awaitTermination ();

        } catch (Exception e) {
            throw new RuntimeException (e);
        }
    }
}
