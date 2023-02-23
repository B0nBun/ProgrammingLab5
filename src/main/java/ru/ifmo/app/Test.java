package ru.ifmo.app;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.security.AnyTypePermission;

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
    public static void main(String[] args) {
        XStream xstream = new XStream();
        xstream.addPermission(AnyTypePermission.ANY);
        xstream.alias("foo", Foo.class);
        var foo = new Foo("bar", 123);
        String xml = xstream.toXML(foo);
        System.out.println(xml);
        Foo foo2 = (Foo) xstream.fromXML(xml);
        System.out.println(foo2);
        try {
            Foo foo3 = (Foo) xstream.fromXML("<foo><bar>bar</bar><baz>ad3</baz></foo>");
            System.out.println(foo3);
        } catch (ConversionException err) {
            System.out.println("Couldn't convert");
        }
    }
}
