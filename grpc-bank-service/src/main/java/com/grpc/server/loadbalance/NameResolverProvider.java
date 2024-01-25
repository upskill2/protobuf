package com.grpc.server.loadbalance;

import io.grpc.NameResolver;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.logging.Logger;


public class NameResolverProvider extends io.grpc.NameResolverProvider {

    Logger logger = Logger.getLogger (NameResolverProvider.class.getName ());
    @Override
    protected boolean isAvailable () {
        return true;
    }

    @Override
    protected int priority () {
        return 5;
    }

    @Override
    public NameResolver newNameResolver (final URI uri, final NameResolver.Args args) {
        logger.info ("Looking for URI: " + uri.toString () + " and args: " + args.toString ());
        return new TempNameResolver (uri.getAuthority ());
    }

    @Override
    public String getDefaultScheme () {
        return "http";
    }
}
