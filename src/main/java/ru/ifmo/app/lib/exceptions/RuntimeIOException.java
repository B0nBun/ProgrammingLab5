package ru.ifmo.app.lib.exceptions;

import java.io.IOException;

// Функции-аргументы для многих методов в стандартной библиотеке
// не имеют дженериков для ошибок, поэтому приходится выкручиваться
// с помощью таких классов
public class RuntimeIOException extends RuntimeException {
    public IOException iocause;
    public RuntimeIOException(IOException iocause) {
        this.iocause = iocause;
    }
}
