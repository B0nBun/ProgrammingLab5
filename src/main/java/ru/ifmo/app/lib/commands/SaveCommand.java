package ru.ifmo.app.lib.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Scanner;

import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import ru.ifmo.app.App;
import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.utils.Messages;

public class SaveCommand implements Command {
    private File askForFilepath(Writer writer, Scanner scanner) {
        App.logger.info("Please, provide file for saving a collection (empty to cancel): ");
        String filepath = scanner.nextLine();
        if (filepath == null || filepath.length() == 0) {
            return null;
        }
        return new File(filepath);
    }
    
    @Override
    public void execute(CommandContext context) {
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
                App.logger.error("Provided file not found: {}", err.getMessage());
                savingFile = askForFilepath(context.writer(), context.scanner());
                if (savingFile == null) {
                    break;
                }
            }
        }
        if (savingFile == null) {
            App.logger.info("Saving canceled...");
            return;
        }
        App.logger.info("Collection was saved to a file: {}", savingFile.getAbsolutePath());
    }
    
    @Override
    public String helpMessage() {
        return Messages.get("Help.Command.Save");
    }
}
