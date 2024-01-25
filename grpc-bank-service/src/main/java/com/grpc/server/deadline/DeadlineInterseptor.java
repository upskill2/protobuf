package com.grpc.server.deadline;

import io.grpc.*;

import java.util.concurrent.TimeUnit;

public class DeadlineInterseptor implements ClientInterceptor {
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall (final MethodDescriptor<ReqT,
            RespT> methodDescriptor, CallOptions callOptions, final Channel channel) {

        final Deadline deadline = callOptions.getDeadline ();
        if (deadline == null) {
            callOptions = callOptions.withDeadline (Deadline.after (2, TimeUnit.SECONDS));
        }

        return channel.newCall (methodDescriptor,
                callOptions.withDeadline (Deadline.after (5, TimeUnit.SECONDS)));
    }
}
