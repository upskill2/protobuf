package com.grpc.server.client;

import com.grpc.models.AccountBalance;
import com.grpc.models.TransferRequest;
import com.grpc.models.TransferResponse;
import com.grpc.models.TransferStatus;
import com.grpc.server.repository.AccountDatabase;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.logging.Logger;

public class TransferStreamingRequest implements StreamObserver<TransferRequest> {
Logger logger = Logger.getLogger (TransferStreamingRequest.class.getName ());

    private final StreamObserver<TransferResponse> transferResponseStreamObserver;

    public TransferStreamingRequest (StreamObserver<TransferResponse> transferResponseStreamObserver) {
        this.transferResponseStreamObserver = transferResponseStreamObserver;
    }

    @Override
    public void onNext (final TransferRequest transferRequest) {
        int fromAccount = transferRequest.getFromAccount ();
        int toAccount = transferRequest.getToAccount ();
        int transferAmount = transferRequest.getTransferAmount ();

        int fromAccountBalance = AccountDatabase.getClientBalance (fromAccount);

        TransferStatus status = TransferStatus.FAILED;
        if (fromAccountBalance >= transferAmount && fromAccount != toAccount) {
            AccountDatabase.deductBalance (fromAccount, transferAmount);
            AccountDatabase.addBalance (toAccount, transferAmount);
            status = TransferStatus.SUCCESS;
        } else {
           /* transferResponseStreamObserver.onError (Status.FAILED_PRECONDITION
                    .withDescription ("Insufficient balance")
                    .asRuntimeException ());*/
        }

        final AccountBalance.Builder from_acc = AccountBalance.newBuilder ()
                .setAccountNumber (fromAccount)
                .setAccountBalance (AccountDatabase.getClientBalance (fromAccount));
        final AccountBalance.Builder to_acc = AccountBalance.newBuilder ()
                .setAccountNumber (fromAccount)
                .setAccountBalance (AccountDatabase.getClientBalance (toAccount));


        TransferResponse transferResponse = TransferResponse.newBuilder ()
                .setStatus (status)
                .addAccountBalances (from_acc)
                .addAccountBalances (to_acc)
                .build ();
        transferResponseStreamObserver.onNext (transferResponse);

    }

    @Override
    public void onError (final Throwable throwable) {
        logger.info (throwable.getMessage ());
    }

    @Override
    public void onCompleted () {
        logger.info (AccountDatabase.printAccountDetails ());
            transferResponseStreamObserver.onCompleted ();
    }
}
