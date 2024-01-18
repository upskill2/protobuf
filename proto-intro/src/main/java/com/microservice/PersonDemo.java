package com.microservice;

import com.microservices.models.Address;
import com.microservices.models.Person;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;



public class PersonDemo {

    public static void main (String[] args) throws IOException {

        Person person = Person.newBuilder ()
                .setName ("John")
                .setAge (10)
                .setAddress (Address.newBuilder ()
                        .setCity ("New York")
                        .setState ("NY")
                        .build ())
                .build ();

        Path path = Paths.get ("person.ser");
     //   Files.write (path, person.toByteArray ());

        final byte[] bytes = Files.readAllBytes (path);
        Person person1 = Person.parseFrom (bytes);
        System.out.println (person1);

    }

}
