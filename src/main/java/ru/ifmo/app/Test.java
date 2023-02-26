package ru.ifmo.app;

import java.io.IOException;
import java.net.URL;

import org.jdom2.JDOMException;

import ru.ifmo.app.lib.Vehicles;
import ru.ifmo.app.lib.exceptions.ParsingException;

/*
 * Alternative entry point for manual testing and debugging
*/


public class Test {    
    public static void main(String[] args) throws IOException, JDOMException, ParsingException {
        URL testingFile = Test.class.getClassLoader().getResource("testing.xml");
        var vehicles = Vehicles.loadFromXml(testingFile.openStream());
        System.out.println(vehicles.stream().toList());
    }
}
