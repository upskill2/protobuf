package com.grpc.server.metadata;

import io.grpc.*;

import java.util.Objects;

public class AuthInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall (final ServerCall<ReqT, RespT> serverCall,
                                                                  final Metadata metadata,
                                                                  final ServerCallHandler<ReqT, RespT> serverCallHandler) {


        final String clientToken = metadata.get (ServerConstants.USER_TOKEN);
        if (validate (clientToken)) {
            UserRole userRole = getUserRole (clientToken);
            final Context context = Context.current ().withValue (ServerConstants.USER_ROLE, userRole);
            // final Status status = Status.UNAUTHENTICATED.withDescription ("Invalid client token");
            //serverCall.close (status, metadata);
            return Contexts.interceptCall (context, serverCall, metadata, serverCallHandler);
            //   return new ServerCall.Listener<ReqT> ()

        } else {
            final Status status = Status.UNAUTHENTICATED.withDescription ("Invalid client token");
          //  serverCall.close (status, metadata);
            return new ServerCall.Listener<ReqT> () {
            };
        }

    }


    private boolean validate (String clientToken) {
        return Objects.nonNull (clientToken) &&

                (clientToken.equals ("user-secret-3") || clientToken.startsWith ("user-secret-2"));

    }

    private UserRole getUserRole (String clientToken) {
        if (clientToken.startsWith ("user-secret-3")) {
            return UserRole.PRIME;
        } else {
            return UserRole.STANDARD;
        }
    }


    public enum UserRole {
        PRIME,
        STANDARD;
    }

}
