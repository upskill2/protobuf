package com.grpc.server.metadata;

import io.grpc.Metadata;

public class ClientConstants {

    public static final Metadata metadata = new Metadata ();

    public static Metadata getClientToken(){
        Metadata metadata = new Metadata ();
        metadata.put (Metadata.Key.of ("client-token", Metadata.ASCII_STRING_MARSHALLER),
                "secret-client-secret");
        return metadata;
    }
}
