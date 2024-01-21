package com.grpc.server;

import com.grpc.server.service.BankService;
import com.grpc.server.service.TransferService;
import io.grpc.ServerBuilder;

public class GrpcServer {

    public static void main (String[] args) {
        try {
            ServerBuilder.forPort (6565)
                    .addService (new BankService ())
                    .addService (new TransferService ())
                    .build ()
                    .start ()
                    .awaitTermination ();

        } catch (Exception e) {
            throw new RuntimeException (e);
        }
    }
}
