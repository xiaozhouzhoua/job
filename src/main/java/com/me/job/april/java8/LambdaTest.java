package com.me.job.april.java8;

import org.junit.Test;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;

public class LambdaTest {
    @Test
    public void lambdaVsAnonymousClass() {
        new Thread(() -> System.out.println("hello1")).start();
    }

    @Test
    public void functionalInterfaces() {
        // Supplier的例子
        Supplier<String> supplier = String::new;
        Supplier<String> stringSupplier = () -> "OJBK";
        Supplier<Integer> random = () -> ThreadLocalRandom.current().nextInt();
        System.out.println(supplier.get());
        System.out.println(stringSupplier.get());
        System.out.println(random.get());

        // Predicate的例子
        Predicate<Integer> positiveNumber = i -> i > 0;
        Predicate<Integer> evenNumber = i -> i % 2 == 0;
        assertTrue(positiveNumber.and(evenNumber).test(2));

        // Consumer的例子
        Consumer<String> println = System.out::println;
        println.andThen(println).accept("jia");

        // Function的例子
        Function<String, String> upperCase = String::toUpperCase;
        Function<String, String> duplicate = s -> s.concat(s);
        assertThat(upperCase.andThen(duplicate).apply("test"), is("TESTTEST"));

        // BinaryOperator
        BinaryOperator<Integer> add = Integer::sum;
        BinaryOperator<Integer> subtraction = (a, b) -> a - b;
        assertThat(subtraction.apply(add.apply(1, 2), 3), is(0));
    }
}
