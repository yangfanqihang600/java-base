package com.house.common.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class SystemException extends RuntimeException {
    /**
     * 错误码
     */
    private ErrorCode errorCode;
    /**
     * 动态属性
     */
    private final Map<String, Object> properties = new TreeMap<>();

    /**
     * 包装异常，减少不必要的嵌套
     *
     * @param message   异常信息
     * @param exception 异常
     * @param errorCode 错误码
     * @return 包装异常
     */
    public static SystemException wrap(String message, Throwable exception, ErrorCode errorCode) {
        if (exception instanceof SystemException) {
            SystemException se = (SystemException) exception;
            if (errorCode != null && errorCode != se.getErrorCode()) {
                return new SystemException(message, exception, errorCode);
            }
            return se;
        } else {
            return new SystemException(message, exception, errorCode);
        }
    }

    /**
     * 包装异常
     *
     * @param message   异常信息
     * @param exception 异常
     * @return 包装异常
     */
    public static SystemException wrap(String message, Throwable exception) {
        return wrap(message, exception, null);
    }

    /**
     * 包装异常
     *
     * @param exception 异常
     * @param errorCode 错误码
     * @return 包装异常
     */
    public static SystemException wrap(Throwable exception, ErrorCode errorCode) {
        return wrap(null, exception, errorCode);
    }

    /**
     * 包装异常
     *
     * @param exception 异常
     * @return 包装异常
     */
    public static SystemException wrap(Throwable exception) {
        return wrap(null, exception, null);
    }

    /**
     * 构造方法
     *
     * @param errorCode 错误码
     */
    public SystemException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * 构造方法
     *
     * @param message   异常信息
     * @param errorCode 错误码
     */
    public SystemException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造方法
     *
     * @param cause     异常链
     * @param errorCode 错误码
     */
    public SystemException(Throwable cause, ErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    /**
     * 构造方法
     *
     * @param message   异常信息
     * @param cause     异常链
     * @param errorCode 错误码
     */
    public SystemException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * 动态属性设置
     *
     * @param name  属性名
     * @param value 属性值
     * @return 当前异常
     */
    public SystemException set(String name, Object value) {
        properties.put(name, value);
        return this;
    }

    @Override
    public void printStackTrace(PrintStream s) {
        printStackTrace(new PrintWriter(s));
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        synchronized (s) {
            s.println(this);

            Optional.ofNullable(errorCode).ifPresent(e -> s.println("\t" + e.getClass().getName() + ":" + e + "[" + e.getCode() + "-" + e.getMessage() + "]"));

            properties.keySet().forEach(key -> s.println("\t" + key + "=[" + properties.get(key) + "]"));

            Arrays.stream(getStackTrace()).map(trace -> "\tat " + trace).forEach(s::println);

            Optional.ofNullable(getCause()).ifPresent(ourCause -> {
                s.print("Caused by: ");
                ourCause.printStackTrace(s);
            });
            s.flush();
        }
    }
}
