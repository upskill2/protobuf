package com.grpc.server.loadbalance;

import com.grpc.models.Balance;
import com.grpc.models.BalanceCheckRequest;
import com.grpc.models.BankServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance (TestInstance.Lifecycle.PER_CLASS)
class NginxTestClient {

    private BankServiceGrpc.BankServiceBlockingStub bankServiceBlockingStub;

    @BeforeAll
    public void setup () {

        final ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress ("localhost", 8585)
                .usePlaintext ()
                .build ();

        bankServiceBlockingStub = BankServiceGrpc.newBlockingStub (managedChannel);
    }

    @Test
    void balanceTest () {
        for (int i = 0; i < 10; i++) {
            BalanceCheckRequest balanceCheckRequest = BalanceCheckRequest.newBuilder ()
                    .setAccountNumber (i)
                    .build ();
            final Balance balance = bankServiceBlockingStub.getBalance (balanceCheckRequest);
            System.out.println ("Received: " + balance.getAmount ());


        }


    }

}
