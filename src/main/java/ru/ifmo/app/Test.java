package ru.ifmo.app;

import java.util.stream.Stream;

import com.fasterxml.uuid.Generators;

import ru.ifmo.app.lib.Utils;

/*
 * Alternative entry point for manual testing and debugging
*/

public class Test {
    public static void main(String[] args) {
        var uuidgenerator = Generators.randomBasedGenerator();
        var uuidstream = Stream.iterate(uuidgenerator.generate(), __ -> uuidgenerator.generate());
        var uuidpeekable = new Utils.Peekable<>(uuidstream.iterator());
        System.out.println(uuidpeekable.peek());
        System.out.println(uuidpeekable.next());
        System.out.println(uuidpeekable.peek());
        System.out.println(uuidpeekable.next());
    }
}
