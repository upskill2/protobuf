package com.game.service;


import com.game.server.GameStreamingRequest;
import com.grpc.models.GameServiceGrpc;
import com.grpc.models.RollRequest;
import com.grpc.models.RollResponse;
import io.grpc.stub.StreamObserver;

public class GameService extends GameServiceGrpc.GameServiceImplBase {
    @Override
    public StreamObserver<RollRequest> roll (final StreamObserver<RollResponse> responseObserver) {
          return new GameStreamingRequest (responseObserver);
    }
}
