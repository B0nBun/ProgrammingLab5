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

// ВАРИАНТ: 968317

// TODO: На сервере команда save должна по умолчанию принимать в аргумент путь указанный с клиентской стороны

/**
    1. Организовать хранение коллекции в реляционной СУБД (PostgresQL). Убрать хранение коллекции в файле.
    2. Для генерации поля id использовать средства базы данных (sequence).
    3. Обновлять состояние коллекции в памяти только при успешном добавлении объекта в БД
    4. Все команды получения данных должны работать с коллекцией в памяти, а не в БД
    5. Организовать возможность регистрации и авторизации пользователей. У пользователя есть возможность указать пароль.
    6. Пароли при хранении хэшировать алгоритмом SHA-224
    7. Запретить выполнение команд не авторизованным пользователям.
    8. При хранении объектов сохранять информацию о пользователе, который создал этот объект.
    9. Пользователи должны иметь возможность просмотра всех объектов коллекции, но модифицировать могут только принадлежащие им.
    10. Для идентификации пользователя отправлять логин и пароль с каждым запросом.

    Необходимо реализовать многопоточную обработку запросов.

    1. Для многопоточного чтения запросов использовать создание нового потока (java.lang.Thread)
    2. Для многопотчной обработки полученного запроса использовать Fixed thread pool
    3. Для многопоточной отправки ответа использовать создание нового потока (java.lang.Thread)
    4. Для синхронизации доступа к коллекции использовать java.util.Collections.synchronizedXXX
 */

/** Entry point of a program, which starts the repl loop and loads the xml file. */
public class App {

    /** Static global logger, which is used across all of the classes. */
    public static final Logger logger = LoggerFactory.getLogger(
        "ru.ifmo.app.local.logger"
    );

    /**
     * Gets vehicles from the xml file. If JDOMException occures during parsing or
     * FileNotFoundException during file stream, it is suppressed, appropriate message logged and
     * {@code null} is returned
     */
    private static Vehicles getVehiclesFromXmlFile(File vehiclesXmlFile)
        throws IOException {
        Vehicles vehicles = null;
        try (
            var vehiclesXmlFileStream = vehiclesXmlFile != null
                ? new FileInputStream(vehiclesXmlFile)
                : null;
        ) {
            if (vehiclesXmlFileStream != null) {
                vehicles = Vehicles.loadFromXml(vehiclesXmlFileStream);
                App.logger.info(
                    Messages.get("LoadedElementsFromFile", vehicles.stream().count())
                );
            } else {
                App.logger.warn(Messages.get("Warn.NoXmlFileInArguments"));
            }
        } catch (JDOMException err) {
            App.logger.error(
                Messages.get("Error.XmlFileParsing", vehiclesXmlFile, err.getMessage())
            );
        } catch (FileNotFoundException err) {
            App.logger.error(
                Messages.get("Error.FileNotFound", vehiclesXmlFile, err.getMessage())
            );
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

            var executor = new DeprecatedCommandExecutor(
                scanner,
                vehicles,
                vehiclesXmlFile,
                0
            );

            try {
                while (true) {
                    System.out.print("> ");
                    var commandString = scanner.nextLine();
                    try {
                        executor.executeCommandString(commandString);
                    } catch (MaximumScriptExecutionDepthException err) {
                        App.logger.warn(
                            Messages.get(
                                "Warn.ScriptExecutionDepthExceededMaximum",
                                err.maximumDepth
                            )
                        );
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
