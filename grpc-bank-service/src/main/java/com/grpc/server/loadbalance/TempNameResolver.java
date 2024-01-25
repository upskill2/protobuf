package com.grpc.server.loadbalance;

import io.grpc.NameResolver;

public class TempNameResolver extends NameResolver {

    private final String service;

    public TempNameResolver (String service) {
        this.service = service;
    }

    @Override
    public String getServiceAuthority () {
        return "temp";
    }

    @Override
    public void start (final Listener2 listener) {
        listener.onResult (ResolutionResult.newBuilder ()
                .setAddresses (ServiceRegistry.getInstances (service))
                .build ());
    }

    @Override
    public void shutdown () {

    }
}
