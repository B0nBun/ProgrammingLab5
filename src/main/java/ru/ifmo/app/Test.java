package ru.ifmo.app;

import java.io.IOException;
import java.io.StringReader;

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
    private static String xmlString = """
    <root>
        <elem tip=\"someattr1\">
            <optional>content1</optional>
        </elem>
        <elem tip=\"someattr2\">content2</elem>
        <elem tip=\"someattr3\">content3</elem>
    </root>
    """;
    
    public static void main(String[] args) throws IOException, JDOMException {
        var sax = new SAXBuilder();
        
        sax.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        sax.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        Document doc = sax.build(new StringReader(Test.xmlString));
        Element rootNode = doc.getRootElement();
        var list = rootNode.getChildren();
        for (var e: list) {
            System.out.println(e);
            Element child = e.getChild("optional");
            if (child != null) {
                System.out.println("Child: " + child.getText());
            }
        }
    }
}
