package ru.ifmo.app;

import java.util.stream.Stream;

import ru.ifmo.app.lib.Utils;

/*
 * Alternative entry point for manual testing and debugging
*/

public class Test {
    public static void main(String[] args) {
        var iterator = Stream.iterate(1, i -> i + 1).limit(0).iterator();
        var peekable = new Utils.Peekable<>(iterator);
        System.out.println(peekable.peek());
        peekable.forEachRemaining(System.out::println);
    }
}
