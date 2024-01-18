package com.grpc.server.service;

import com.grpc.models.TransferRequest;
import com.grpc.models.TransferResponse;
import com.grpc.models.TransferServiceGrpc;
import com.grpc.server.client.TransferStreamingRequest;
import io.grpc.stub.StreamObserver;

public class TransferService extends TransferServiceGrpc.TransferServiceImplBase {

    @Override
    public StreamObserver<TransferRequest> transfer (final StreamObserver<TransferResponse> responseObserver) {
        return new TransferStreamingRequest (responseObserver);
    }
}
