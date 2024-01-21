package com.grpc.server.service;

import com.grpc.models.TransferRequest;
import com.grpc.models.TransferServiceGrpc;
import com.grpc.server.client.TransferStreamingResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

@TestInstance (TestInstance.Lifecycle.PER_CLASS)
class TransferServiceTest {

    private TransferServiceGrpc.TransferServiceStub transferServiceStub;

    @BeforeAll
    void setup () {
        final ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress ("localhost", 8585)
                .usePlaintext ()
                .build ();
        transferServiceStub = TransferServiceGrpc.newStub (managedChannel);
    }

    @Test
    void transferTest () throws InterruptedException {
        CountDownLatch latch = new CountDownLatch (1);
        TransferStreamingResponse response = new TransferStreamingResponse (latch);
        final StreamObserver<TransferRequest> transfer = transferServiceStub.transfer (response);
        for (int i = 0; i < 100; i++) {
            final TransferRequest request = TransferRequest.newBuilder ()
                    .setFromAccount (ThreadLocalRandom.current ().nextInt (1, 11))
                    .setToAccount (ThreadLocalRandom.current ().nextInt (1, 11))
                    .setTransferAmount (ThreadLocalRandom.current ().nextInt (1, 100))
                    .build ();
            transfer.onNext (request);
        }

        transfer.onCompleted ();
        latch.await ();
    }

}



