package com.microservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.microservice.json.JPerson;
import com.microservices.models.Person;


import java.io.IOException;

public class PerformanceTest {

    public static void main (String[] args) {
        JPerson jPerson = new JPerson ();
        jPerson.setAge (10);
        jPerson.setName ("John");

        ObjectMapper mapper = new ObjectMapper ();

        Runnable runnable1 = () -> {
            try {
                final byte[] bytes = mapper.writeValueAsBytes (jPerson);
                mapper.readValue (bytes, JPerson.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException (e);
            } catch (IOException e) {
                throw new RuntimeException (e);
            }
        };

        Person person = Person.newBuilder ()
                .setName ("John")
                .setAge (10)
                .build ();


        Runnable runnable2 = () -> {
            final byte[] byteArray = person.toByteArray ();
            try {
                Person person1 = Person.parseFrom (byteArray);
            } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException (e);
            }

        };

        try {
            final byte[] bytes = mapper.writeValueAsBytes (jPerson);
            mapper.readValue (bytes, JPerson.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException (e);
        } catch (IOException e) {
            throw new RuntimeException (e);
        }


        for (int i = 0; i < 5; i++) {
            runPerformanceTest (runnable1, "JSON");
            runPerformanceTest (runnable2, "PROTOBUF");
        }


    }

    private static void runPerformanceTest (Runnable runnable, String method) {
        long start = System.currentTimeMillis ();
        for (int i = 0; i < 5_000_000; i++) {
            runnable.run ();
        }
        long end = System.currentTimeMillis ();
        System.out.println (method + ":time taken: " + (end - start) + "ms");

    }

}
