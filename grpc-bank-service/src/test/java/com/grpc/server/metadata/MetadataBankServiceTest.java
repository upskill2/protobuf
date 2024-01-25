package com.grpc.server.metadata;

import com.grpc.models.*;
import com.grpc.server.client.BalanceStreamObserver;
import com.grpc.server.client.MoneyStreamingResponse;
import com.grpc.server.repository.AccountDatabase;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.MetadataUtils;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import static com.grpc.server.repository.AccountDatabase.getClientBalance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance (TestInstance.Lifecycle.PER_CLASS)
class MetadataBankServiceTest {

    Logger logger = Logger.getLogger (MetadataBankServiceTest.class.getName ());

    private BankServiceGrpc.BankServiceBlockingStub bankServiceBlockingStub;
    private BankServiceGrpc.BankServiceStub bankServiceStub;

    @BeforeAll
    public void setup () {

        final ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress ("localhost", 6565)
                .intercept (MetadataUtils.newAttachHeadersInterceptor (ClientConstants.getClientToken ()))
                .usePlaintext ()
                .build ();

        bankServiceBlockingStub = BankServiceGrpc.newBlockingStub (managedChannel);
    }


    @Test
    void balanceTest () {

        BalanceCheckRequest balanceCheckRequest = BalanceCheckRequest.newBuilder ()
                .setAccountNumber (7)
                .build ();


        try {

            for (int i = 0; i < 20; i++) {
                int random = ThreadLocalRandom.current ().nextInt (2, 5);
                logger.info ("random: " + random);
                final Balance balance = bankServiceBlockingStub
                        .withCallCredentials (new UserSessionToken ("user-secret-" + random))
                        .getBalance (balanceCheckRequest);
                logger.info ("Received balance: " + balance.getAmount ());
            //    assertTrue (balance.getAmount () >= 70);
            }
        } catch (Exception e) {
            logger.info ("Exception: " + e.getMessage ());
        }

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

        final int clientBalance = AccountDatabase.getClientBalance (7);

        assertTrue (clientBalance - initialBalance == 100);
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