package com.grpc.server.client;

import com.grpc.models.TransferResponse;
import io.grpc.stub.StreamObserver;
import lombok.extern.java.Log;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class TransferStreamingResponse implements StreamObserver<TransferResponse> {
    Logger logger = Logger.getLogger (TransferStreamingResponse.class.getName ());

    private final CountDownLatch latch;

    public TransferStreamingResponse (CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext (final TransferResponse transferResponse) {
        logger.info ("Transfer status: " + transferResponse.getStatus ());
        transferResponse.getAccountBalancesList ()
                .stream ()
                .map (accountBalance -> accountBalance.getAccountNumber () + ": " + accountBalance.getAccountBalance ())
                .forEach (logger::info);
        logger.info ("------------------");


    }

    @Override
    public void onError (final Throwable throwable) {

    }

    @Override
    public void onCompleted () {
       logger.info ("All transfers completed");
        latch.countDown ();


    }
}
