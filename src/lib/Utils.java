package lib;

import java.io.IOException;
import java.io.Writer;

public class Utils {
    public static void print(Writer writer, String string) throws IOException {
        writer.write(string);
        writer.flush();
    }
}
