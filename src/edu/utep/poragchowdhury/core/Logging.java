package edu.utep.poragchowdhury.core;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Logging {
    private static final boolean APPEND = true;

    public static void setupFormat() {
        // %1 = Date, %2 = Source, %3 = Logger, %4 = Level, %5 = Message, &6 = Thrown
        // %1$tF = Date -> Y-m-d
        // %1$tT = Date -> 24 hour format
        // %4$s = Log Type (Info, ...)
        // %2$s = Class and Method Call
        // %5$s%6$s = Message
        System.setProperty("java.util.logging.SimpleFormatter.format", "{%1$tT} %2$s %5$s%6$s" + "\n");
    }

    public static void attachLoggerToFile(Logger l, String logFilename) {
        // Connect to file for saving
        FileHandler fh;
        try {
            fh = new FileHandler(logFilename, APPEND);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            l.addHandler(fh);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }
}
