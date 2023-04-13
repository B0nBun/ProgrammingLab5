package ru.ifmo.app.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.app.local.lib.DeprecatedCommandExecutor;
import ru.ifmo.app.local.lib.exceptions.ExitProgramException;
import ru.ifmo.app.local.lib.exceptions.MaximumScriptExecutionDepthException;
import ru.ifmo.app.shared.Vehicles;
import ru.ifmo.app.shared.utils.Messages;

// ВАРИАНТ: 1251262

/**
 * Требования: - Объекты между клиентом и сервером должны передаваться в сериализованном виде. -
 * Клиент должен корректно обрабатывать временную недоступность сервера. - Обмен данными между
 * клиентом и сервером должен осуществляться по протоколу *TCP* - Для обмена данными на сервере
 * необходимо использовать потоки ввода-вывода - Для обмена данными на клиенте необходимо
 * использовать *сетевой канал* (channel) - Сетевые каналы должны использоваться в *неблокирующем
 * режиме*.
 * 
 * Сервер: - Работа с файлом, хранящим коллекцию. - Управление коллекцией объектов. - Назначение
 * автоматически генерируемых полей объектов в коллекции. - Ожидание подключений и запросов от
 * клиента. - Обработка полученных запросов (команд). - Сохранение коллекции в файл при завершении
 * работы приложения. - Сохранение коллекции в файл при исполнении специальной команды, доступной
 * только серверу (клиент такую команду отправить не может). - Модули: Модуль приёма подключений.
 * Модуль чтения запроса. Модуль обработки полученных команд. Модуль отправки ответов клиенту.
 * 
 * Клиент: - Чтение команд из консоли. - Валидация вводимых данных. - Сериализация введённой команды
 * и её аргументов. - Отправка полученной команды и её аргументов на сервер. - Обработка ответа от
 * сервера (вывод результата исполнения команды в консоль). - Команду save из клиентского приложения
 * необходимо убрать. - Команда exit завершает работу клиентского приложения.
 * 
 * ! Команды и их аргументы должны представлять из себя объекты классов. Недопустим обмен "простыми"
 * строками. Так, для команды add или её аналога необходимо сформировать объект, содержащий тип
 * команды и объект, который должен храниться в вашей коллекции. ? Реализовать логирование различных
 * этапов работы сервера (начало работы, получение нового подключения, получение нового запроса,
 * отправка ответа и т.п.)
 */

/** Entry point of a program, which starts the repl loop and loads the xml file. */
public class App {

  /** Static global logger, which is used across all of the classes. */
  public static final Logger logger = LoggerFactory.getLogger("ru.ifmo.app.local.logger");

  /**
   * Gets vehicles from the xml file. If JDOMException occures during parsing or
   * FileNotFoundException during file stream, it is suppressed, appropriate message logged and
   * {@code null} is returned
   */
  private static Vehicles getVehiclesFromXmlFile(File vehiclesXmlFile) throws IOException {
    Vehicles vehicles = null;
    try (var vehiclesXmlFileStream =
        vehiclesXmlFile != null ? new FileInputStream(vehiclesXmlFile) : null;) {
      if (vehiclesXmlFileStream != null) {
        vehicles = Vehicles.loadFromXml(vehiclesXmlFileStream);
        App.logger.info(Messages.get("LoadedElementsFromFile", vehicles.stream().count()));
      } else {
        App.logger.warn(Messages.get("Warn.NoXmlFileInArguments"));
      }
    } catch (JDOMException err) {
      App.logger.error(Messages.get("Error.XmlFileParsing", vehiclesXmlFile, err.getMessage()));
    } catch (FileNotFoundException err) {
      App.logger.error(Messages.get("Error.FileNotFound", vehiclesXmlFile, err.getMessage()));
    }

    return vehicles;
  }

  /**
   * Entry point of a program, which starts the repl loop and loads the xml file.
   *
   * @param args Command line arguments, the first one of which can be an XML file.
   */
  public static void main(String[] args) {
    File vehiclesXmlFile = null;
    if (args.length > 0) {
      vehiclesXmlFile = new File(args[0]);
    }

    try (var scanner = new Scanner(System.in);) {

      Vehicles vehicles = getVehiclesFromXmlFile(vehiclesXmlFile);

      if (vehicles == null) {
        App.logger.warn(Messages.get("Warn.EmptyCollectionStart"));
        vehicles = new Vehicles();
      }

      var executor = new DeprecatedCommandExecutor(scanner, vehicles, vehiclesXmlFile, 0);

      try {
        while (true) {
          System.out.print("> ");
          var commandString = scanner.nextLine();
          try {
            executor.executeCommandString(commandString);
          } catch (MaximumScriptExecutionDepthException err) {
            App.logger
                .warn(Messages.get("Warn.ScriptExecutionDepthExceededMaximum", err.maximumDepth));
          } catch (ExitProgramException err) {
            System.exit(0);
          }
        }
      } catch (NoSuchElementException err) {
        App.logger.error(Messages.get("Error.NoSuchElement.CtrlD"));
      } catch (IllegalStateException err) {
        App.logger.error(Messages.get("Error.IllegalState", err.getMessage()));
      }

    } catch (IOException err) {
      App.logger.error(Messages.get("Error.IO", err.getMessage()));
    }
  }
}
