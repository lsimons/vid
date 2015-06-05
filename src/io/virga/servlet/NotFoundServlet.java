package io.virga.servlet;

import io.virga.exception.HttpException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class NotFoundServlet implements Servlet {
    private ServletConfig config;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        this.config = config;
    }

    @Override
    public ServletConfig getServletConfig() {
        return config;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse)
            throws ServletException, IOException {
        String message = "No handler found";
        if (servletRequest instanceof HttpServletRequest) {
            HttpServletRequest req = (HttpServletRequest) servletRequest;
            message += " for " + req.getContextPath() + req.getPathInfo();
        }
        throw HttpException.fromCode(404, message);
    }

    @Override
    public String getServletInfo() {
        return "generic error handler";
    }

    @Override
    public void destroy() {
    }
}
