package com.balsam.upgradetable.util;

import org.apache.logging.log4j.LogManager;

public final class Logger {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger();

    public static void debug(Object s) {
        LOGGER.debug(s);
    }

    public static void info(Object s) {
        LOGGER.info(s);
    }

    public static void warn(Object s) {
        LOGGER.warn(s);
    }

    public static void error(Object s) {
         LOGGER.error(s);
    }

    public static void forceDebug(Object s) {
        LOGGER.debug(s);
    }

    public static void forceInfo(Object s) {
        LOGGER.info(s);
    }

    public static void forceWarn(Object s) {
        LOGGER.warn(s);
    }

    public static void forceError(Object s) {
        LOGGER.error(s);
    }
}
