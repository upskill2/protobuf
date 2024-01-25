package com.grpc.server.metadata;

import io.grpc.CallCredentials;
import io.grpc.Metadata;

import java.util.concurrent.Executor;
import java.util.logging.Logger;

public class UserSessionToken extends CallCredentials {

    Logger logger = Logger.getLogger (UserSessionToken.class.getName ());

    private final String jwt;

    public UserSessionToken (String jwt) {
        this.jwt = jwt;
    }

    @Override
    public void applyRequestMetadata (final RequestInfo requestInfo, final Executor executor,
                                      final MetadataApplier metadataApplier) {
        executor.execute (() -> {
            try {
                Metadata headers = new Metadata ();
                headers.put (Metadata.Key.of ("user-token", Metadata.ASCII_STRING_MARSHALLER), jwt);
                metadataApplier.apply (headers);
            } catch (Throwable e) {
               // metadataApplier.fail (Status.FAILED_PRECONDITION.withCause (e));
                logger.info ("Error: " + e.getMessage ());

            }
        });
    }
}
