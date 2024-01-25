package com.grpc.server.metadata;

import com.google.common.util.concurrent.Uninterruptibles;
import com.grpc.models.*;
import com.grpc.server.client.CashStreamingRequest;
import com.grpc.server.repository.AccountDatabase;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.grpc.server.metadata.AuthInterceptor.UserRole.PRIME;
import static com.grpc.server.repository.AccountDatabase.getClientBalance;

public class TokenBankService extends BankServiceGrpc.BankServiceImplBase {

    private final Logger LOGGER = Logger.getLogger (TokenBankService.class.getName ());

    @Override
    public StreamObserver<DepositRequest> deposit (final StreamObserver<Balance> responseObserver) {
        return new CashStreamingRequest (responseObserver);
    }

    @Override
    public void getBalance (final BalanceCheckRequest request, final StreamObserver<Balance> responseObserver) {
        final int accountNumber = request.getAccountNumber ();

        AuthInterceptor.UserRole userRole = ServerConstants.USER_ROLE.get ();
        final Balance balance = Balance.newBuilder ()
                .setAmount (userRole == PRIME ? getClientBalance (accountNumber) : getClientBalance (accountNumber) - 15)
                .build ();

        Uninterruptibles.sleepUninterruptibly (100, java.util.concurrent.TimeUnit.MILLISECONDS);
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
                Uninterruptibles.sleepUninterruptibly (3000, java.util.concurrent.TimeUnit.MILLISECONDS);

                if (Context.current ().isCancelled ()) {
                    break;
                }

                responseObserver.onNext (withdrawn);
                LOGGER.info ("Current balance: " + getClientBalance (accountNumber));
                try {
                    Thread.sleep (500);
                } catch (InterruptedException e) {
                    throw new RuntimeException (e);
                }
            }
            LOGGER.info ("Completed");
            responseObserver.onCompleted ();
        }

    }
}
