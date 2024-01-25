package com.grpc.server.loadbalance;

import com.grpc.models.*;
import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance (TestInstance.Lifecycle.PER_CLASS)
class LoadbalanceBankServiceTest {

    Logger logger = Logger.getLogger (LoadbalanceBankServiceTest.class.getName ());

    private BankServiceGrpc.BankServiceBlockingStub bankServiceBlockingStub;

    @BeforeAll
    public void setup () {

        final ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress ("localhost", 6565)
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
                .withDeadline (Deadline.after (2, TimeUnit.SECONDS))
                .getBalance (balanceCheckRequest);

        logger.info ("Received: " + balance.getAmount ());
        assertTrue (balance.getAmount () >= 70);
    }

}