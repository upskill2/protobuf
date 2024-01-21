package com.grpc.server.loadbalance;

import com.grpc.models.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance (TestInstance.Lifecycle.PER_CLASS)
class BankServiceTest_1 {

    private BankServiceGrpc.BankServiceBlockingStub bankServiceBlockingStub;
    @BeforeAll
    public void setup () {

        final ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress ("localhost", 8585)
                .usePlaintext ()
                .build ();

        bankServiceBlockingStub = BankServiceGrpc.newBlockingStub (managedChannel);
  //      bankServiceStub = BankServiceGrpc.newStub (managedChannel);
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



}