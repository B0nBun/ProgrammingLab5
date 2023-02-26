package ru.ifmo.app.lib.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Scanner;

import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.Utils;

public class SaveCommand implements Command {
    private File askForFilepath(Writer writer, Scanner scanner) throws IOException {
        Utils.print(writer, "Please, provide file for saving a collection (empty to cancel): ");
        String filepath = scanner.nextLine();
        if (filepath == null || filepath.length() == 0) {
            return null;
        }
        return new File(filepath);
    }
    
    @Override
    public void execute(CommandContext context) throws IOException {
        File savingFile = context.vehiclesFile();

        var xmlOutputter = new XMLOutputter();
        xmlOutputter.setFormat(Format.getPrettyFormat());
        Element vehiclesRootElement = context.vehicles().toXmlElement();
        String vehiclesSerialized = xmlOutputter.outputString(vehiclesRootElement);

        if (savingFile == null) {
            savingFile = askForFilepath(context.writer(), context.scanner());
        }
        while (true) {
            try (var printWriter = new PrintWriter(savingFile)) {
                printWriter.write(vehiclesSerialized);
                break;
            } catch (FileNotFoundException err) {
                Utils.print(context.writer(), "Provided file not found: " + err.getMessage() + "\n");
                savingFile = askForFilepath(context.writer(), context.scanner());
                if (savingFile == null) {
                    break;
                }
            }
        }
        if (savingFile == null) {
            Utils.print(context.writer(), "Saving canceled\n");
            return;
        }
        Utils.print(context.writer(), "Collection was saved to file: " + savingFile.getAbsolutePath() + "\n");
    }
    
    @Override
    public String helpMessage() {
        return "Saves a collection to specified file";
    }
}
