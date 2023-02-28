package ru.ifmo.app.lib.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import ru.ifmo.app.App;
import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.utils.Messages;

public class SaveCommand implements Command {
    private File askForFilepath(Scanner scanner) {
        App.logger.info(Messages.get("ProvideFileForSaving"));
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
            savingFile = askForFilepath(context.scanner());
        }
        while (true) {
            try (var printWriter = new PrintWriter(savingFile)) {
                printWriter.write(vehiclesSerialized);
                break;
            } catch (FileNotFoundException err) {
                App.logger.error(Messages.get("Error.FileNotFound", savingFile, err.getMessage()));
                savingFile = askForFilepath(context.scanner());
                if (savingFile == null) {
                    break;
                }
            }
        }
        if (savingFile == null) {
            App.logger.info(Messages.get("SaveCancel"));
            return;
        }
        App.logger.info(Messages.get("CollectionWasSaved"), savingFile.getAbsolutePath());
    }
    
    @Override
    public String helpMessage() {
        return Messages.get("Help.Command.Save");
    }
}
