package com.grpc.server.client;

import com.grpc.models.*;
import com.grpc.server.repository.AccountDatabase;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;

import static com.grpc.server.repository.AccountDatabase.getClientBalance;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance (TestInstance.Lifecycle.PER_CLASS)
class BankServiceTest {

    private BankServiceGrpc.BankServiceBlockingStub bankServiceBlockingStub;
    private BankServiceGrpc.BankServiceStub bankServiceStub;

    @BeforeAll
    public void setup () {

        final ManagedChannel managedChannel = ManagedChannelBuilder.forAddress ("localhost", 6565)
                .usePlaintext ()
                .build ();

        bankServiceBlockingStub = BankServiceGrpc.newBlockingStub (managedChannel);
        bankServiceStub = BankServiceGrpc.newStub (managedChannel);
    }


    @Test
    void depositTest () throws InterruptedException {
        CountDownLatch latch = new CountDownLatch (1);
        int initialBalance = getClientBalance (7);

        final StreamObserver<DepositRequest> depositObserver = bankServiceStub.deposit (new BalanceStreamObserver (latch));

        for (int i = 0; i < 10; i++) {
            DepositRequest depositRequest = DepositRequest.newBuilder ()
                    .setAccountNumber (7)
                    .setAmount (10)
                    .build ();

            depositObserver.onNext (depositRequest);
        }
        depositObserver.onCompleted ();
        latch.await ();

        final int clientBalance =AccountDatabase.getClientBalance (7);

              assertTrue (clientBalance-initialBalance==100);
    }

    @Test
    void balanceTest () {

        BalanceCheckRequest balanceCheckRequest = BalanceCheckRequest.newBuilder ()
                .setAccountNumber (7)
                .build ();
        final Balance balance = bankServiceBlockingStub.getBalance (balanceCheckRequest);
        assertTrue ( balance.getAmount ()>=70);
    }

    @Test
    void testWithdraw () {

        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder ()
                .setAccountNumber (8)
                .setAmount (50)
                .build ();

        bankServiceBlockingStub.withdraw (withdrawRequest).forEachRemaining (withdrawResponse -> {
            assertEquals (10, withdrawResponse.getWithdrawnAmount ());
        });
    }


    @Test
    void withdrawAsyncTest () throws InterruptedException {
        CountDownLatch latch = new CountDownLatch (1);

        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder ()
                .setAccountNumber (10)
                .setAmount (30)
                .build ();

        bankServiceStub.withdraw (withdrawRequest, new MoneyStreamingResponse (latch));
        latch.await ();

    }

}