package com.grpc.server.client;

import com.grpc.models.WithdrawResponse;

import java.util.concurrent.CountDownLatch;

public class MoneyStreamingResponse implements io.grpc.stub.StreamObserver<com.grpc.models.WithdrawResponse> {

    private final CountDownLatch latch;

    public MoneyStreamingResponse (CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext (final WithdrawResponse withdrawResponse) {
        System.out.println ("Received async: " + withdrawResponse.getWithdrawnAmount ());

    }

    @Override
    public void onError (final Throwable throwable) {
        System.out.println (throwable.getMessage ());
        latch.countDown ();

    }

    @Override
    public void onCompleted () {
        System.out.println ("Server is done sending data");
        latch.countDown ();
    }
}
