package io.virga.servlet;

import io.virga.exception.HttpException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ExceptionServlet implements Servlet {
    static {
        ErrorMessageSerializer.load();
    }

    private final ExceptionHandler exceptionHandler = new ExceptionHandler();
    private ServletConfig config;

    @Override
    public void init(ServletConfig config) throws ServletException {
        String showStackTraces = config.getInitParameter("showStackTraces");
        if (showStackTraces != null) {
            exceptionHandler.setShowStackTraces(Boolean.parseBoolean(showStackTraces));
        }
        this.config = config;
    }

    @Override
    public ServletConfig getServletConfig() {
        return config;
    }

    @Override
    public void service(ServletRequest request, ServletResponse response)
            throws ServletException, IOException {
        Throwable exception = (Throwable) request
                        .getAttribute("javax.servlet.error.exception");
        if (exception == null) {
            exception = new RuntimeException("Unknown exception");
        }
        if (!(exception instanceof HttpException)) {
            Throwable cause = exception.getCause();
            if (cause instanceof HttpException) {
                exception = cause;
            } else {
                Integer statusCode = (Integer) request
                                .getAttribute("javax.servlet.error.status_code");
                if (statusCode != null) {
                    exception = HttpException.fromCode(statusCode, exception);
                }
            }
        }
        exceptionHandler.handleException((HttpServletRequest)request, (HttpServletResponse)response, exception);
    }

    @Override
    public String getServletInfo() {
        return "generic error handler";
    }

    @Override
    public void destroy() {
    }
}
