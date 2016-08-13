package com.shihui.commons;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 框架日志覆盖实现
 * @author zhouqisheng
 *
 */
public class ApiLogger {
	  /**
     * 空格分隔符
     */
    public static final Logger DEBUG = LoggerFactory.getLogger("debug");
    public static final Logger INFO = LoggerFactory.getLogger("info");
    public static final Logger WARN = LoggerFactory.getLogger("warn");
    public static final Logger ERROR = LoggerFactory.getLogger("error");
    public static final Logger REQUEST = LoggerFactory.getLogger("request");
    public static final Logger DB_INFO = LoggerFactory.getLogger("db_info");
    public static final Logger REVIEW_INFO = LoggerFactory.getLogger("review_info");

    public static void debug(String message) {
        DEBUG.debug(message);
    }

    public static void warn(String message) {
        WARN.warn(message);
    }

    public static void info(String message) {
        INFO.info(message);
    }

    public static void error(String message) {
        ERROR.error(message);
    }

    public static void error(String message, Throwable throwable) {
        ERROR.error(message, throwable);
    }

    public static void dbInfo(String message) {
        DB_INFO.info(message);
    }

    public static void requset(String message) {
        REQUEST.info(message);
    }

    public static void reviewInfo(String message) {REVIEW_INFO.info(message);}

}
