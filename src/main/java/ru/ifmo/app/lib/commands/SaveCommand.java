package ru.ifmo.app.lib.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Scanner;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import ru.ifmo.app.App;
import ru.ifmo.app.lib.Command;
import ru.ifmo.app.lib.CommandContext;
import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.utils.Messages;

/**
 * Command used to save currently stored collection in the selected file. If the file is not
 * available for some reason, then the user is asked to input a new filepath with a cancellable
 * prompt.
 */
public class SaveCommand implements Command {
  // https://stackoverflow.com/questions/7163364/how-to-handle-in-file-paths
  // Replaced `ls` with `echo` command, because it didn't work with non-existant files
  private String expandPath(String path) {
    try {
      String command = "echo " + path;
      Process shellExec = Runtime.getRuntime().exec(new String[] {"bash", "-c", command});

      BufferedReader reader = new BufferedReader(new InputStreamReader(shellExec.getInputStream()));
      String expandedPath = reader.readLine();

      // Only return a new value if expansion worked.
      // We're reading from stdin. If there was a problem, it was written
      // to stderr and our result will be null.
      if (expandedPath != null) {
        path = expandedPath;
      }
    } catch (java.io.IOException ex) {
      // Just consider it unexpandable and return original path.
    }

    return path;
  }


  private File askForFilepath(Scanner scanner) {
    App.logger.info(Messages.get("ProvideFileForSaving"));
    String filepath = scanner.nextLine();
    if (filepath == null || filepath.trim().length() == 0) {
      return null;
    }
    String expanded = expandPath(filepath);
    return new File(expanded.trim());
  }

  @Override
  public void execute(CommandContext context) {

    var xmlOutputter = new XMLOutputter();
    xmlOutputter.setFormat(Format.getPrettyFormat());
    Element vehiclesRootElement = context.vehicles().toXmlElement();
    String vehiclesSerialized = xmlOutputter.outputString(vehiclesRootElement);

    while (true) {
      File savingFile = context.vehiclesFile() == null ? askForFilepath(context.scanner())
          : context.vehiclesFile();

      if (savingFile == null) {
        App.logger.info(Messages.get("SaveCancel"));
        break;
      }

      var validationError = Utils.validateFilename(savingFile.getName());
      if (validationError.isPresent() && !savingFile.exists()) {
        App.logger.error(validationError.get());
        continue;
      }

      try (var printWriter = new PrintWriter(savingFile)) {
        printWriter.write(vehiclesSerialized);
        App.logger.info(Messages.get("CollectionWasSaved", savingFile.getAbsolutePath()));
        break;
      } catch (FileNotFoundException err) {
        App.logger.error(Messages.get("Error.FileNotFound", savingFile, err.getMessage()));
        continue;
      }
    }
  }

  @Override
  public String helpMessage() {
    return Messages.get("Help.Command.Save");
  }
}
