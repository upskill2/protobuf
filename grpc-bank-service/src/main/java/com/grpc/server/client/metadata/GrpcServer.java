package com.grpc.server.client.metadata;

import io.grpc.ServerBuilder;

public class GrpcServer {

    public static void main (String[] args) {
        try {
            ServerBuilder.forPort (6565)
                    .addService (new TokenBankService ())
                    .intercept (new AuthInterceptor ())
                    .build ()
                    .start ()
                    .awaitTermination ();

        } catch (Exception e) {
            throw new RuntimeException (e);
        }
    }
}
