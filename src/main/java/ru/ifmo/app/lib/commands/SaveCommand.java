package ru.ifmo.app.lib.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import ru.ifmo.app.App;
import ru.ifmo.app.lib.DeprecatedCommand;
import ru.ifmo.app.lib.DeprecatedCommandContext;
import ru.ifmo.app.lib.Utils;
import ru.ifmo.app.lib.utils.Messages;

/**
 * Command used to save currently stored collection in the selected file. If the file is not
 * available for some reason, then the user is asked to input a new filepath with a cancellable
 * prompt.
 */
public class SaveCommand implements DeprecatedCommand {
  private File askForFilepath(Scanner scanner) {
    App.logger.info(Messages.get("ProvideFileForSaving"));
    String filepath = scanner.nextLine();
    if (filepath == null || filepath.trim().length() == 0) {
      return null;
    }
    String expanded = Utils.expandPath(filepath);
    return new File(expanded.trim());
  }

  @Override
  public void execute(DeprecatedCommandContext context) {

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
