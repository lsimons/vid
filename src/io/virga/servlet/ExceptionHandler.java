package io.virga.servlet;

import io.virga.convert.ConverterFactory;
import io.virga.convert.IO;
import io.virga.convert.MimeTypes;
import io.virga.convert.Serializer;
import io.virga.exception.BadRequest;
import io.virga.exception.HttpException;
import io.virga.exception.InternalServerError;
import io.virga.exception.NotAcceptable;
import io.virga.exception.SerializerNotFoundException;
import io.virga.log.Logger;
import io.virga.log.LoggerFactory;

import javax.activation.MimeType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;

public class ExceptionHandler {
    private final static Logger log = LoggerFactory.getLogger(ExceptionFilter.class);
    private boolean showStackTraces = true;

    public void setShowStackTraces(boolean showStackTraces) {
        this.showStackTraces = showStackTraces;
    }

    public ExceptionHandler() {
    }

    public void handleException(HttpServletRequest req, HttpServletResponse res, Throwable e) throws IOException {
        String msg = String.format("%s at %s%s: %s", e.getClass().getSimpleName(), req.getContextPath(),
                req.getServletPath(), e.getMessage());
        if (showStackTraces) {
            log.debug(msg + ":", e);
        } else {
            log.debug(msg);
        }

        MimeType responseType;

        if (!res.isCommitted()) {
            if (e instanceof HttpException) {
                setStatusLine((HttpException) e, res);
            } else {
                setStatusLineError(res);
            }
            responseType = chooseResponseTypeSafe(req);
            res.setContentType(responseType.toString());
            res.setCharacterEncoding("UTF-8");
        } else {
            responseType = findResponseTypeSafe(res);
        }

        writeErrorMessage(res, e, responseType);
    }

    private void writeErrorMessage(HttpServletResponse res, Throwable e, MimeType responseType) throws IOException {
        ErrorMessage errorMessage = new ErrorMessage(e, showStackTraces);
        PrintWriter pw;
        try {
            pw = res.getWriter();
        } catch (IllegalStateException ise) {
            OutputStream os = res.getOutputStream();
            pw = IO.utf8Writer(os);
        }
        try {
            Serializer<ErrorMessage> serializer = ConverterFactory.getSerializer(errorMessage);
            serializer.serialize(errorMessage, pw, responseType);
            pw.flush();
        } catch (SerializerNotFoundException nfe) {
            pw.println(String.format("Error: %s: %s", e.getClass().getSimpleName(), e.getMessage()));
            pw.println();
            pw.println("Additionally, a format-appropriate serializer was not found trying to report this error.");
            pw.flush();
        }
    }

    @SuppressWarnings("deprecation")
    private static void setStatusLine(HttpException he, HttpServletResponse res) {
        int statusCode = he.getStatusCode();
        String message = he.getMessage();
        if (isValidReasonPhrase(message)) {
            res.setStatus(statusCode, message);
        } else {
            res.setStatus(statusCode);
        }
    }

    private static void setStatusLineError(HttpServletResponse res) {
        res.setStatus(500);
    }

    private static boolean isValidReasonPhrase(String message) {
        return message != null && !message.isEmpty() &&
                message.length() <= 255 && isText(message, '\r', '\n');
    }

    private static boolean isText(String message, char... excludes) {
        boolean checkExcludes = excludes.length > 0;
        Arrays.sort(excludes);
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (checkExcludes) {
                if (Arrays.binarySearch(excludes, c) != -1) {
                    return false;
                }
            }
            switch (c) {
                case 9: // HT
                case 10: // LF
                case 13: // CR
                    continue;
                case 127: // DEL
                    return false;
                default:
                    if (c > 31 && c <= 255) { // ! control char && ascii
                        continue;
                    }
            }
            return false;
        }
        return true;
    }

    private static MimeType chooseResponseTypeSafe(HttpServletRequest req) {
        try {
            return MimeTypes.chooseResponseType(req);
        } catch (BadRequest e) {
            return MimeTypes.defaultResponseType;
        }
    }

    private static MimeType findResponseTypeSafe(HttpServletResponse res) {
        try {
            return MimeTypes.findResponseType(res);
        } catch (InternalServerError | NotAcceptable e) {
            return MimeTypes.defaultResponseType;
        }
    }
}
