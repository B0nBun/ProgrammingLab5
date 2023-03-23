package ru.ifmo.app;

import ru.ifmo.app.lib.exceptions.ValidationException;
import ru.ifmo.app.lib.utils.fieldschema.FieldSchema;

/** Alternative entry point of the program made for manual testing */
public class Test {
  public static void main(String[] args) throws ValidationException {
    var fs = FieldSchema.str().notcontains("notallowed");

    // fs.validate("notallowed");
    fs.validate("allowed");
    fs.validate(null);
  }
}
