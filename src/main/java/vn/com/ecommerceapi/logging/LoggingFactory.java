package vn.com.ecommerceapi.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import vn.com.ecommerceapi.utils.MaskingUtils;

public class LoggingFactory implements Logger {

    private final Logger logger;

    private LoggingFactory(Class<?> clazz) {
        logger = LoggerFactory.getLogger(clazz);
    }

    public static Logger getLogger(Class<?> clazz) {
        return new LoggingFactory(clazz);
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(String s) {
        logger.trace(s);
    }

    @Override
    public void trace(String s, Object o) {
        o = MaskingUtils.maskingSensitiveData(o);
        logger.trace(s, o);
    }

    @Override
    public void trace(String s, Object o, Object o1) {
        if (logger.isTraceEnabled()) {
            o = MaskingUtils.maskingSensitiveData(o);
            o1 = MaskingUtils.maskingSensitiveData(o1);
            logger.trace(s, o, o1);
        }
    }

    @Override
    public void trace(String s, Object... objects) {
        objects = MaskingUtils.maskingSensitiveData(objects);
        logger.trace(s, objects);
    }

    @Override
    public void trace(String s, Throwable throwable) {
        logger.trace(s, throwable);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return logger.isTraceEnabled(marker);
    }

    @Override
    public void trace(Marker marker, String s) {
        logger.trace(marker, s);
    }

    @Override
    public void trace(Marker marker, String s, Object o) {
        logger.trace(marker, s, o);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1) {
        logger.trace(marker, s, o, o1);
    }

    @Override
    public void trace(Marker marker, String s, Object... objects) {
        logger.trace(marker, s, objects);
    }

    @Override
    public void trace(Marker marker, String s, Throwable throwable) {
        logger.trace(marker, s, throwable);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(String s) {
        logger.debug(s);
    }

    @Override
    public void debug(String s, Object o) {
        if (logger.isDebugEnabled()) {
            o = MaskingUtils.maskingSensitiveData(o);
            logger.debug(s, o);
        }
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        if (logger.isDebugEnabled()) {
            o = MaskingUtils.maskingSensitiveData(o);
            o1 = MaskingUtils.maskingSensitiveData(o1);
            logger.debug(s, o, o1);
        }
    }

    @Override
    public void debug(String s, Object... objects) {
        if (logger.isDebugEnabled()) {
            objects = MaskingUtils.maskingSensitiveData(objects);
            logger.debug(s, objects);
        }
    }

    @Override
    public void debug(String s, Throwable throwable) {
        if (logger.isDebugEnabled()) {
            logger.debug(s, throwable);
        }
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return logger.isDebugEnabled(marker);
    }

    @Override
    public void debug(Marker marker, String s) {
        logger.debug(marker, s);
    }

    @Override
    public void debug(Marker marker, String s, Object o) {
        logger.debug(marker, s, o);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1) {
        logger.debug(marker, s, o, o1);
    }

    @Override
    public void debug(Marker marker, String s, Object... objects) {
        logger.debug(marker, s, objects);
    }

    @Override
    public void debug(Marker marker, String s, Throwable throwable) {
        logger.debug(marker, s, throwable);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(String s) {
        if (logger.isInfoEnabled()) {
            logger.info(s);
        }
    }

    @Override
    public void info(String s, Object o) {
        if (logger.isInfoEnabled()) {
            o = MaskingUtils.maskingSensitiveData(o);
            logger.info(s, o);
        }
    }

    @Override
    public void info(String s, Object o, Object o1) {
        if (logger.isInfoEnabled()) {
            o = MaskingUtils.maskingSensitiveData(o);
            o1 = MaskingUtils.maskingSensitiveData(o1);
            logger.info(s, o, o1);
        }
    }

    @Override
    public void info(String s, Object... objects) {
        if (logger.isInfoEnabled()) {
            objects = MaskingUtils.maskingSensitiveData(objects);
            logger.info(s, objects);
        }
    }

    @Override
    public void info(String s, Throwable throwable) {
        if (logger.isInfoEnabled()) {
            logger.info(s, throwable);
        }
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return false;
    }

    @Override
    public void info(Marker marker, String s) {
        logger.info(s, s);
    }

    @Override
    public void info(Marker marker, String s, Object o) {
        logger.info(marker, s, o);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1) {
        logger.info(marker, s, o, o1);
    }

    @Override
    public void info(Marker marker, String s, Object... objects) {
        logger.info(marker, s, objects);
    }

    @Override
    public void info(Marker marker, String s, Throwable throwable) {
        logger.info(marker, s, throwable);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(String s) {
        if (logger.isWarnEnabled()) {
            logger.warn(s);
        }
    }

    @Override
    public void warn(String s, Object o) {
        if (logger.isWarnEnabled()) {
            o = MaskingUtils.maskingSensitiveData(o);
            logger.warn(s, o);
        }
    }

    @Override
    public void warn(String s, Object... objects) {
        if (logger.isWarnEnabled()) {
            objects = MaskingUtils.maskingSensitiveData(objects);
            logger.warn(s, objects);
        }
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        if (logger.isWarnEnabled()) {
            o = MaskingUtils.maskingSensitiveData(o);
            o1 = MaskingUtils.maskingSensitiveData(o1);
            logger.warn(s, o, o1);
        }
    }

    @Override
    public void warn(String s, Throwable throwable) {
        if (logger.isWarnEnabled()) {
            logger.warn(s, throwable);
        }
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return logger.isWarnEnabled(marker);
    }

    @Override
    public void warn(Marker marker, String s) {
        logger.warn(marker, s);
    }

    @Override
    public void warn(Marker marker, String s, Object o) {
        logger.warn(marker, s, o);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1) {
        logger.warn(marker, s, o, o1);
    }

    @Override
    public void warn(Marker marker, String s, Object... objects) {
        logger.warn(marker, s, objects);
    }

    @Override
    public void warn(Marker marker, String s, Throwable throwable) {
        logger.warn(marker, s, throwable);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public void error(String s) {
        if (logger.isErrorEnabled()) {
            logger.error(s);
        }
    }

    @Override
    public void error(String s, Object o) {
        if (logger.isErrorEnabled()) {
            o = MaskingUtils.maskingSensitiveData(o);
            logger.error(s, o);
        }
    }

    @Override
    public void error(String s, Object o, Object o1) {
        if (logger.isErrorEnabled()) {
            o = MaskingUtils.maskingSensitiveData(o);
            o1 = MaskingUtils.maskingSensitiveData(o1);
            logger.error(s, o, o1);
        }
    }

    @Override
    public void error(String s, Object... objects) {
        if (logger.isErrorEnabled()) {
            objects = MaskingUtils.maskingSensitiveData(objects);
            logger.error(s, objects);
        }
    }

    @Override
    public void error(String s, Throwable throwable) {
        if (logger.isErrorEnabled()) {
            logger.error(s, throwable);
        }
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return logger.isErrorEnabled(marker);
    }

    @Override
    public void error(Marker marker, String s) {
        logger.error(marker, s);
    }

    @Override
    public void error(Marker marker, String s, Object o) {
        logger.error(marker, s, o);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1) {
        logger.error(marker, s, o, o1);
    }

    @Override
    public void error(Marker marker, String s, Object... objects) {
        logger.error(marker, s, objects);
    }

    @Override
    public void error(Marker marker, String s, Throwable throwable) {
        logger.error(marker, s, throwable);
    }

}