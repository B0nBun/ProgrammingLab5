package ru.ifmo.app;

import java.io.IOException;
import java.net.URL;

import javax.xml.XMLConstants;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/*
 * Alternative entry point for manual testing and debugging
*/

class Foo {
    String bar;
    int baz;

    public Foo(String bar, int baz) {
        this.bar = bar;
        this.baz = baz;
    }

    public String toString() {
        return "Foo[bar=" + this.bar + ", baz=" + this.baz + "]";
    }
}

public class Test {    
    public static void main(String[] args) throws IOException, JDOMException {
        URL testingFile = Test.class.getClassLoader().getResource("testing.xml");
        System.out.println(testingFile);
        var sax = new SAXBuilder();
        
        sax.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        sax.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        Document doc = sax.build(testingFile);
        Element rootNode = doc.getRootElement();
        var list = rootNode.getChildren();
        for (var e: list) {
            System.out.println(e);
        }
    }
}
