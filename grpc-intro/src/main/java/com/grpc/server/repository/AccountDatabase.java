package com.grpc.server.repository;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AccountDatabase {
    private static final Map<Integer, Integer> MAP = IntStream
            .rangeClosed (1, 10)
            .boxed ()
            .collect (
                    Collectors.toMap (Function.identity (), v -> v * 100));

    public static int getClientBalance (int accountId) {
        return MAP.get (accountId);
    }

    public static Integer addBalance (int accountId, int amount) {
        return MAP.computeIfPresent (accountId, (k, v) -> v + amount);
    }

    public static Integer deductBalance (int accountId, int amount) {
        return MAP.computeIfPresent (accountId, (k, v) -> v - amount);

    }

    public static String printAccountDetails () {
        String MAP = IntStream
                .rangeClosed (1, 10)
                .boxed ()
                .map (i -> i + " : " + AccountDatabase.getClientBalance (i))
                .collect (Collectors.joining (", "));
        return MAP;
    }

}
