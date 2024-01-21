package com.grpc.server.loadbalance;

import io.grpc.ServerBuilder;

public class GrpcServer1 {

    public static void main (String[] args) {
        try {
            ServerBuilder.forPort (6565)
                    .addService (new BankService ())
                    .build ()
                    .start ()
                    .awaitTermination ();

        } catch (Exception e) {
            throw new RuntimeException (e);
        }
    }
}
