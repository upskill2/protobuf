package com.grpc.server.loadbalance;

import com.grpc.models.*;
import com.grpc.server.client.CashStreamingRequest;
import com.grpc.server.repository.AccountDatabase;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.grpc.server.repository.AccountDatabase.getClientBalance;

public class BankService extends BankServiceGrpc.BankServiceImplBase {

    private final Logger LOGGER = Logger.getLogger (BankService.class.getName ());

    @Override
    public StreamObserver<DepositRequest> deposit (final StreamObserver<Balance> responseObserver) {
        return new CashStreamingRequest (responseObserver);
    }

    @Override
    public void getBalance (final BalanceCheckRequest request, final StreamObserver<Balance> responseObserver) {
        final int accountNumber = request.getAccountNumber ();
        LOGGER.info ("Received request for " + accountNumber);
        final Balance balance = Balance.newBuilder ()
                .setAmount (getClientBalance (accountNumber))
                .build ();

        responseObserver.onNext (balance);
        responseObserver.onCompleted ();
    }

    @Override
    public void withdraw (final WithdrawRequest request, final StreamObserver<WithdrawResponse> responseObserver) {
        int accountNumber = request.getAccountNumber ();
        int amount = request.getAmount ();
        int balance = getClientBalance (accountNumber);


        if (balance < amount) {

            LOGGER.log (Level.ALL, balance + "to withdraw: " + amount);
            responseObserver.onError (Status.FAILED_PRECONDITION.
                    withDescription ("Insufficient balance: " + balance + " to withdraw: " + amount)
                    .asRuntimeException ());
        } else {
            for (int i = 0; i < amount / 10; i++) {
                WithdrawResponse withdrawn = WithdrawResponse.newBuilder ().setWithdrawnAmount (10).build ();
                AccountDatabase.deductBalance (accountNumber, 10);
                responseObserver.onNext (withdrawn);
                try {
                    Thread.sleep (500);
                } catch (InterruptedException e) {
                    throw new RuntimeException (e);
                }
            }
            responseObserver.onCompleted ();
        }

    }
}
