/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.convert;

import io.virga.exception.BadRequest;
import io.virga.exception.InternalServerError;
import io.virga.exception.NotAcceptable;
import io.virga.log.Logger;
import io.virga.log.LoggerFactory;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("SameReturnValue")
public class MimeTypes {
    public final static MimeType applicationJson;
    public final static MimeType applicationXml;
    public final static MimeType defaultResponseType;
    public final static List<MimeType> supportedResponseTypes;
    public final static Pattern missingSubtypePattern = Pattern.compile("^\\*([^;]*?(?:;.*)?)");
    private final static Logger log = LoggerFactory.getLogger(MimeTypes.class);

    static {
        try {
            textHtml = new MimeType("text/html");
            applicationXml = new MimeType("application/xml");
            applicationJson = new MimeType("application/json");
            textPlain = new MimeType("text/plain");
            defaultResponseType = MimeTypes.textHtml;
            supportedResponseTypes = new CopyOnWriteArrayList<>(
                    new MimeType[]{MimeTypes.textHtml, MimeTypes.applicationXml, MimeTypes.applicationJson,
                            MimeTypes.textPlain});
        } catch (MimeTypeParseException e) {
            throw new IllegalStateException(e);
        }
    }

    public final static MimeType textHtml;
    public final static MimeType textPlain;

    public static MimeType chooseResponseType(HttpServletRequest req) throws BadRequest {
        Enumeration<String> accepts = req.getHeaders("Accept");
        if (accepts == null || !accepts.hasMoreElements()) {
            return defaultResponseType;
        }

        List<MimeType> acceptedTypes = new ArrayList<>();
        while (accepts.hasMoreElements()) {
            String accept = accepts.nextElement();
            try {
                addMimeTypes(accept, acceptedTypes);
            } catch (MimeTypeParseException e) {
                throw new BadRequest("Cannot parse Accept header", e);
            }
        }
        sortByQuality(acceptedTypes);
        return chooseResponseType(acceptedTypes);
    }

    public static void addMimeTypes(String mimeTypesString, Collection<MimeType> mimeTypes)
            throws MimeTypeParseException {
        while (mimeTypesString.length() > 0) {
            int comma = mimeTypesString.indexOf(',');
            if (comma == -1) {
                mimeTypesString = mimeTypesString.trim();
                if (!mimeTypesString.isEmpty()) {
                    MimeType mimeType = new MimeType(mimeTypesString);
                    mimeTypes.add(mimeType);
                }
                break;
            } else {
                String typeString = mimeTypesString.substring(0, comma);
                mimeTypesString = mimeTypesString.substring(comma + 1);
                String modifiedTypeString = typeString.trim();
                if (modifiedTypeString.isEmpty()) {
                    continue;
                }
                if (modifiedTypeString.equals("*")) {
                    modifiedTypeString = "*/*";
                } else if (!modifiedTypeString.contains("/")) {
                    Matcher m = missingSubtypePattern.matcher(modifiedTypeString);
                    if (m.matches()) {
                        modifiedTypeString = "*/*" + m.group(1);
                    }
                }
                try {
                    MimeType acceptedType = new MimeType(modifiedTypeString);
                    mimeTypes.add(acceptedType);
                } catch (MimeTypeParseException e) {
                    log.info("Unparseable mime type " + typeString);
                }
            }
        }
    }

    public static void sortByQuality(List<MimeType> mimeTypes) {
        if (mimeTypes.size() == 0) {
            return;
        }
        Collections.sort(mimeTypes, new Comparator<MimeType>() {
            @Override
            public int compare(MimeType o1, MimeType o2) {
                String q1String = o1.getParameter("q");
                String q2String = o2.getParameter("q");
                try {
                    float q1 = q1String == null ? 1.0f : Float.parseFloat(q1String);
                    float q2 = q2String == null ? 1.0f : Float.parseFloat(q2String);
                    return Float.compare(q2, q1); // descending order
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        });
    }

    public static MimeType chooseResponseType(List<MimeType> acceptedTypes) throws NotAcceptable {
        if (acceptedTypes.isEmpty()) {
            return defaultResponseType;
        }
        for (MimeType acceptedType : acceptedTypes) {
            for (MimeType supportedType : supportedResponseTypes) {
                if (match(supportedType, acceptedType)) {
                    return supportedType;
                }
            }
        }
        throw new NotAcceptable();
    }

    private static boolean match(MimeType supportedType, MimeType acceptedType) {
        if (supportedType.match(acceptedType)) {
            return true;
        }
        if ("*".equals(acceptedType.getPrimaryType())) {
            String acceptedSubType = acceptedType.getSubType();
            if ("*".equals(acceptedSubType)) {
                return true;
            }
            String supportedSubType = supportedType.getSubType();
            return acceptedSubType.equals(supportedSubType);
        }
        return false;
    }

    public static MimeType findResponseType(HttpServletResponse res) throws InternalServerError, NotAcceptable {
        String contentType = res.getContentType();
        if (contentType == null) {
            return defaultResponseType;
        }
        contentType = contentType.trim();
        if (contentType.isEmpty()) {
            return defaultResponseType;
        }
        MimeType mimeType;
        try {
            mimeType = new MimeType(contentType);
        } catch (MimeTypeParseException e) {
            throw new InternalServerError(e);
        }
        List<MimeType> responseTypes = Arrays.asList(mimeType);
        return chooseResponseType(responseTypes);
    }

    public static List<MimeType> getSupportedResponseTypes() {
        return supportedResponseTypes;
    }

    public static boolean isHtml(MimeType type) {
        return textHtml.match(type);
    }

    public static boolean isJson(MimeType type) {
        return applicationJson.match(type);
    }

    public static boolean isText(MimeType type) {
        return textPlain.match(type);
    }

    public static boolean isXml(MimeType type) {
        return applicationXml.match(type);
    }
}
