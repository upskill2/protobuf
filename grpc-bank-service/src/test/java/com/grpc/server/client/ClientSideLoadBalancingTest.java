package com.grpc.server.client;

import com.grpc.models.Balance;
import com.grpc.models.BalanceCheckRequest;
import com.grpc.models.BankServiceGrpc;
import com.grpc.server.loadbalance.NameResolverProvider;
import com.grpc.server.loadbalance.ServiceRegistry;
import com.grpc.server.loadbalance.TempNameResolver;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolverRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance (TestInstance.Lifecycle.PER_CLASS)
class ClientSideLoadBalancingTest {


    private BankServiceGrpc.BankServiceBlockingStub bankServiceBlockingStub;
    private BankServiceGrpc.BankServiceStub bankServiceStub;

    @BeforeAll
    public void setup () {

        ServiceRegistry.register ("bank-service",
                List.of ("localhost:6565", "localhost:7575"));

        NameResolverRegistry.getDefaultRegistry ().register (new NameResolverProvider ());


        final ManagedChannel managedChannel = ManagedChannelBuilder
                .forTarget ("http://bank-service")
                .defaultLoadBalancingPolicy ("round_robin")
                .usePlaintext ()
                .build ();

        bankServiceBlockingStub = BankServiceGrpc.newBlockingStub (managedChannel);
        bankServiceStub = BankServiceGrpc.newStub (managedChannel);
    }


    @Test
    void balanceTest () {

        for (int i = 0; i < 100; i++) {
            final int account = ThreadLocalRandom.current ().nextInt (1, 11);
            BalanceCheckRequest balanceCheckRequest = BalanceCheckRequest.newBuilder ()
                    .setAccountNumber (account)
                    .build ();
            final Balance balance = bankServiceBlockingStub.getBalance (balanceCheckRequest);
            assertTrue (balance.getAmount () == account*100);
        }

    }
}
