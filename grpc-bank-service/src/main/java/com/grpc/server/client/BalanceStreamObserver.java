package com.grpc.server.client;

import com.grpc.models.Balance;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BalanceStreamObserver implements StreamObserver<Balance> {

    Logger logger = Logger.getLogger (BalanceStreamObserver.class.getName ());

    private final CountDownLatch latch;

    public BalanceStreamObserver (CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext (final Balance balance) {
        System.out.println ("Final balance: " + balance.getAmount ());
        logger.log (Level.INFO, "Final balance: " + balance.getAmount ());
    }

    @Override
    public void onError (final Throwable throwable) {
        latch.countDown ();
    }

    @Override
    public void onCompleted () {
        logger.log (Level.INFO, "Server is done sending data");
        latch.countDown ();
    }
}
