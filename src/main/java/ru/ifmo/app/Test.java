package ru.ifmo.app;

import java.io.IOException;
import java.time.LocalDate;

import org.jdom2.JDOMException;

import ru.ifmo.app.lib.exceptions.ParsingException;

/*
 * Alternative entry point for manual testing and debugging
*/


public class Test {    
    public static void main(String[] args) throws IOException, JDOMException, ParsingException {
        LocalDate a = LocalDate.parse(null);
        System.out.println(a);
    }
}
