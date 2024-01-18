package com.grpc.server.client;

import com.grpc.models.Balance;
import com.grpc.models.DepositRequest;
import io.grpc.stub.StreamObserver;

import static com.grpc.server.repository.AccountDatabase.addBalance;

public class CashStreamingRequest implements StreamObserver<DepositRequest> {

    private final StreamObserver<Balance> balanceStreamObserver;
    private int accountBalance;

    public CashStreamingRequest (StreamObserver<Balance> balanceStreamObserver) {
        this.balanceStreamObserver = balanceStreamObserver;
    }

    @Override
    public void onNext (final DepositRequest depositRequest) {
        int accountNumber = depositRequest.getAccountNumber ();
        int depositAmount = depositRequest.getAmount ();

        accountBalance = addBalance (accountNumber, depositAmount);

    }

    @Override
    public void onError (final Throwable throwable) {
        System.out.println (throwable.getMessage ());
    }

    @Override
    public void onCompleted () {
        Balance balance = Balance.newBuilder ()
                .setAmount (accountBalance)
                .build ();
        balanceStreamObserver.onNext (balance);
        balanceStreamObserver.onCompleted ();
    }
}
