package ru.ifmo.app;

import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.ifmo.app.lib.exceptions.ParsingException;

/*
 * Alternative entry point for manual testing and debugging
*/


public class Test {    
    public static void main(String[] args) throws JDOMException, ParsingException {
        Logger logger = LoggerFactory.getLogger("ru.ifmo.app.logger");
        logger.trace("trace message");
        logger.debug("debug message");
        logger.info("info message");
        logger.warn("warn message");
        logger.error("error message");
    }
}
