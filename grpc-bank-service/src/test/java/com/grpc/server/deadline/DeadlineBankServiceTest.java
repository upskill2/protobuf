package com.grpc.server.deadline;

import com.grpc.models.Balance;
import com.grpc.models.BalanceCheckRequest;
import com.grpc.models.BankServiceGrpc;
import com.grpc.models.WithdrawRequest;
import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance (TestInstance.Lifecycle.PER_CLASS)
class DeadlineBankServiceTest {

    Logger logger = Logger.getLogger (DeadlineBankServiceTest.class.getName ());
    private BankServiceGrpc.BankServiceBlockingStub bankServiceBlockingStub;

    @BeforeAll
    public void setup () {

        final ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress ("localhost", 6565)
                .intercept (new DeadlineInterseptor ())
                .usePlaintext ()
                .build ();

        bankServiceBlockingStub = BankServiceGrpc.newBlockingStub (managedChannel);
    }


    @Test
    void balanceTest () {
        BalanceCheckRequest balanceCheckRequest = BalanceCheckRequest.newBuilder ()
                .setAccountNumber (7)
                .build ();
        final Balance balance = bankServiceBlockingStub
      //          .withDeadline (Deadline.after (1, TimeUnit.SECONDS))
                .getBalance (balanceCheckRequest);

        logger.info ("Received: " + balance.getAmount ());
        assertTrue (balance.getAmount () >= 70);
    }

    @Test
    void testWithdraw () {

        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder ()
                .setAccountNumber (9)
                .setAmount (50)
                .build ();

        bankServiceBlockingStub
                .withDeadline (Deadline.after (10, TimeUnit.SECONDS))
                .withdraw (withdrawRequest)
                .forEachRemaining (withdrawResponse -> {
                    final int withdrawnAmount = withdrawResponse.getWithdrawnAmount ();
                    logger.info ("Received: " + withdrawnAmount);
                    assertEquals (10, withdrawnAmount);
                });
    }


}