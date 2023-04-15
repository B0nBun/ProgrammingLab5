package ru.ifmo.app.shared;

import java.io.Serializable;

public class SerializableDummy implements Serializable {

    public static SerializableDummy singletone = new SerializableDummy();
}
