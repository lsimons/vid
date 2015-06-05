/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.json;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.Buffer;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static io.virga.json.JsonParserState.BEGIN;
import static io.virga.json.JsonParserState.END;
import static io.virga.json.JsonParserState.IN_ARRAY;
import static io.virga.json.JsonParserState.IN_OBJECT;
import static io.virga.json.JsonParserState.NAME;
import static io.virga.json.JsonParserState.NAME_END;
import static io.virga.json.JsonParserState.NAME_ESCAPE;
import static io.virga.json.JsonParserState.NAME_UNICODE_ESCAPE;
import static io.virga.json.JsonParserState.NEXT_VALUE;
import static io.virga.json.JsonParserState.VALUE_FALSE_A;
import static io.virga.json.JsonParserState.VALUE_FALSE_F;
import static io.virga.json.JsonParserState.VALUE_FALSE_L;
import static io.virga.json.JsonParserState.VALUE_FALSE_S;
import static io.virga.json.JsonParserState.VALUE_NULL_L;
import static io.virga.json.JsonParserState.VALUE_NULL_N;
import static io.virga.json.JsonParserState.VALUE_NULL_U;
import static io.virga.json.JsonParserState.VALUE_NUMBER;
import static io.virga.json.JsonParserState.VALUE_START;
import static io.virga.json.JsonParserState.VALUE_STRING;
import static io.virga.json.JsonParserState.VALUE_STRING_ESCAPE;
import static io.virga.json.JsonParserState.VALUE_STRING_ESCAPE_UNICODE;
import static io.virga.json.JsonParserState.VALUE_TRUE_R;
import static io.virga.json.JsonParserState.VALUE_TRUE_T;
import static io.virga.json.JsonParserState.VALUE_TRUE_U;
import static io.virga.json.JsonValueType.TYPE_FALSE;
import static io.virga.json.JsonValueType.TYPE_NULL;
import static io.virga.json.JsonValueType.TYPE_NUMBER;
import static io.virga.json.JsonValueType.TYPE_STRING;
import static io.virga.json.JsonValueType.TYPE_TRUE;

public class JsonParser {
    private final static char[] HEX = "0123456789abcdefABCDEF".toCharArray();

    static {
        Arrays.sort(HEX);
    }

    private final static String DETECTION_UNKNOWN = "unknown";
    private final static String DETECTION_UTF8 = "UTF-8";
    private final static String DETECTION_UTF16LE = "UTF-16LE";
    private final static String DETECTION_UTF32LE = "UTF-32LE";
    private final static String DETECTION_UTF16BE = "UTF-16BE";
    private final static String DETECTION_UTF32BE = "UTF-32BE";
    private final static byte NULL = (byte) 0;
    private final static char START_OBJECT = '{';
    private final static char END_OBJECT = '}';
    private final static char START_ARRAY = '[';
    private final static char END_ARRAY = ']';
    private final static char COMMA = ',';
    private final static char COLON = ':';
    private final static char QUOTE = '"';
    private final static char CR = '\r';
    private final static char LF = '\n';
    private final static char SP = ' ';
    private final static char TAB = '\t';
    private final static char ESCAPE = '\\';
    private final static char SLASH = '/';
    private final static char B = '\b';
    private final static char F = '\f';
    private final static char MINUS = '-';
    
    // IO management
    private InputStream input;
    private Reader reader;
    private CharBuffer buf = CharBuffer.allocate(128);
    {
        buf.limit(0);
    }

    private CharBuffer unicodeBuf = CharBuffer.allocate(4);
    // value accumulation
    private StringBuilder value = new StringBuilder();
    private JsonValueType valueType = TYPE_STRING;
    // parser state
    private JsonParserState state = BEGIN;
    private char c;
    private int col = 0;
    private int line = 1;
    private List<JsonParserState> stack = new LinkedList<>();

    public JsonParser(InputStream input) {
        if (input == null) {
            throw new NullPointerException("input cannot be null");
        }
        this.input = input;
    }

    public JsonParser(Reader reader) {
        if (reader == null) {
            throw new NullPointerException("reader cannot be null");
        }
        this.reader = reader;
    }

    public JsonParser(String string) {
        this(new StringReader(string));
    }

    @SuppressWarnings("ConstantConditions")
    public JsonToken next() throws IOException {
        //System.out.println("state = " + state);
        if (state == END || (state != BEGIN && stack.isEmpty())) {
            buf.clear();
            buf.limit(0);
            return JsonToken.END;
        }

        fillBuffer();

        if (!buf.hasRemaining()) {
            return JsonToken.CONTINUE;
        }

        while (buf.hasRemaining()) {
            switch (state) {
                case BEGIN:
                    consume();
                    if (c == START_OBJECT) {
                        state = IN_OBJECT;
                        stack.add(state);
                        return JsonToken.START_OBJECT;
                    } else if (c == START_ARRAY) {
                        state = IN_ARRAY;
                        stack.add(state);
                        return JsonToken.START_ARRAY;
                    } else {
                        throw expected("{ or [");
                    }
                case IN_OBJECT:
                    consume();
                    if (isWhitespace(c)) {
                        break;
                    } else if (c == QUOTE) {
                        state = NAME;
                        value = new StringBuilder();
                        tokenStart();
                        break;
                    } else if (c == END_OBJECT) {
                        popFrame(IN_OBJECT);
                        return JsonToken.END_OBJECT;
                    } else {
                        throw expected("ignorable whitespace or \" or }");
                    }
                case IN_ARRAY:
                    consume();
                    if (isWhitespace(c)) {
                        break;
                    } else if (c == END_ARRAY) {
                        popFrame(IN_ARRAY);
                        return JsonToken.END_ARRAY;
                    } else {
                        back();
                        value = new StringBuilder();
                        tokenStart();
                        state = VALUE_START;
                        break;
                    }
                case NAME:
                    consume();
                    if (c == ESCAPE) {
                        state = NAME_ESCAPE;
                        back(); // back up, to consume up to the escape
                        consumeTokens();
                        buf.get(); // skip ahead again
                        break;
                    } else if (c == QUOTE) {
                        state = NAME_END;
                        consumeTokens();
                        return JsonToken.NAME;
                    } else if (tokenChar(c)) {
                        break;
                    } else {
                        throw expected("token character or \\ or \"");
                    }
                case NAME_ESCAPE:
                    escape(NAME, NAME_UNICODE_ESCAPE);
                    break;
                case NAME_UNICODE_ESCAPE:
                    unicodeEscape(NAME);
                    break;
                case NAME_END:
                    consume();
                    if (isWhitespace(c)) {
                        break;
                    } else if (c == COLON) {
                        state = VALUE_START;
                        break;
                    } else {
                        throw expected("ignorable whitespace or :");
                    }
                case VALUE_START:
                    consume();
                    tokenStart();
                    value = new StringBuilder();
                    if (isWhitespace(c)) {
                        break;
                    } else if (c == QUOTE) {
                        state = VALUE_STRING;
                        valueType = TYPE_STRING;
                    } else if (isDigit(c) || c == MINUS) {
                        state = VALUE_NUMBER;
                        valueType = TYPE_NUMBER;
                    } else if (c == START_OBJECT) {
                        state = IN_OBJECT;
                        stack.add(state);
                        return JsonToken.START_OBJECT;
                    } else if (c == START_ARRAY) {
                        state = IN_ARRAY;
                        stack.add(state);
                        return JsonToken.START_ARRAY;
                    } else if (c == 't') {
                        state = VALUE_TRUE_T;
                        valueType = TYPE_TRUE;
                    } else if (c == 'f') {
                        state = VALUE_FALSE_F;
                        valueType = TYPE_FALSE;
                    } else if (c == 'n') {
                        state = VALUE_NULL_N;
                        valueType = TYPE_NULL;
                    } else {
                        throw expected("ignorable whitespace or value");
                    }
                    break;
                case VALUE_STRING:
                    consume();
                    if (c == ESCAPE) {
                        state = VALUE_STRING_ESCAPE;
                        back();
                        consumeTokens();
                        consume();
                        break;
                    } else if (c == QUOTE) {
                        state = NEXT_VALUE;
                        consumeTokens();
                        return JsonToken.VALUE;
                    } else if (tokenChar(c)) {
                        break;
                    } else {
                        throw expected("token character or \\ or \"");
                    }
                case VALUE_STRING_ESCAPE:
                    escape(VALUE_STRING, VALUE_STRING_ESCAPE_UNICODE);
                    break;
                case VALUE_STRING_ESCAPE_UNICODE:
                    unicodeEscape(VALUE_STRING);
                    break;
                case VALUE_NUMBER:
                    consume();
                    if (isDigit(c) || c == MINUS || c == 'e' || c == 'E' || c == '.' || c == '+' || c == '-') {
                        // rather than parse this out exactly, we'll assume this will work out ok
                        break;
                    } else {
                        back();
                        consumeTokens();
                        state = NEXT_VALUE;
                        return JsonToken.VALUE;
                    }
                case VALUE_TRUE_T:
                    consume();
                    if (c == 'r') {
                        state = VALUE_TRUE_R;
                    } else {
                        throw expected("true");
                    }
                    break;
                case VALUE_TRUE_R:
                    consume();
                    if (c == 'u') {
                        state = VALUE_TRUE_U;
                    } else {
                        throw expected("true");
                    }
                    break;
                case VALUE_TRUE_U:
                    consume();
                    if (c == 'e') {
                        consumeTokens();
                        state = NEXT_VALUE;
                        return JsonToken.VALUE;
                    } else {
                        throw expected("true");
                    }
                case VALUE_FALSE_F:
                    consume();
                    if (c == 'a') {
                        state = VALUE_FALSE_A;
                    } else {
                        throw expected("false");
                    }
                    break;
                case VALUE_FALSE_A:
                    consume();
                    if (c == 'l') {
                        state = VALUE_FALSE_L;
                    } else {
                        throw expected("false");
                    }
                    break;
                case VALUE_FALSE_L:
                    consume();
                    if (c == 's') {
                        state = VALUE_FALSE_S;
                    } else {
                        throw expected("false");
                    }
                    break;
                case VALUE_FALSE_S:
                    consume();
                    if (c == 'e') {
                        consumeTokens();
                        state = NEXT_VALUE;
                        return JsonToken.VALUE;
                    } else {
                        throw expected("false");
                    }
                case VALUE_NULL_N:
                    consume();
                    if (c == 'u') {
                        state = VALUE_NULL_U;
                    } else {
                        throw expected("null");
                    }
                    break;
                case VALUE_NULL_U:
                    consume();
                    if (c == 'l') {
                        state = VALUE_NULL_L;
                    } else {
                        throw expected("null");
                    }
                    break;
                case VALUE_NULL_L:
                    consume();
                    if (c == 'l') {
                        consumeTokens();
                        state = NEXT_VALUE;
                        return JsonToken.VALUE;
                    } else {
                        throw expected("null");
                    }
                case NEXT_VALUE:
                    consume();
                    if (isWhitespace(c)) {
                        break;
                    } else if (c == COMMA) {
                        // since we do not check what comes after the , it may be a } or ], meaning this comma is
                        // really a trailing comma, which we decide to ignore...
                        peekFrame();
                        break;
                    } else if (c == END_OBJECT) {
                        popFrame(IN_OBJECT);
                        return JsonToken.END_OBJECT;
                    } else if (c == END_ARRAY) {
                        popFrame(IN_ARRAY);
                        return JsonToken.END_ARRAY;
                    } else {
                        throw expected("ignorable whitespace or , or ] or }");
                    }
                case END:
                    // won't occur since fillBuffer() will return empty buffer
                    return JsonToken.END;
                default:
                    throw new IllegalStateException("Illegal parser state " + state);
            }
        }

        return JsonToken.CONTINUE;
    }

    private Buffer back() {
        return buf.position(buf.position() - 1);
    }

    private void popFrame(JsonParserState expectedState) throws JsonParseException {
        if (stack.isEmpty()) {
            state = END;
            switch (expectedState) {
                case IN_OBJECT: // encountered a } that does not match up with anything
                    throw expected("matched {");
                case IN_ARRAY: // encountered a ] that does not match up with anything
                    throw expected("matched [");
                default:
                    throw new IllegalStateException("Illegal parser state " + expectedState);
            }
        } else {
            state = NEXT_VALUE;
            JsonParserState popped = stack.remove(stack.size() - 1);
            if (popped != expectedState) {
                switch (popped) {
                    case IN_OBJECT: // encountered a ] but needed a }
                        throw expected("}");
                    case IN_ARRAY: // encountered a } but needed a ]
                        throw expected("]");
                    default:
                        throw new IllegalStateException("Illegal parser state " + expectedState);
                }
            }
        }
    }

    private void peekFrame() {
        JsonParserState containingState = stack.get(stack.size() - 1);
        state = containingState;
        value = new StringBuilder();
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void escape(JsonParserState targetState, JsonParserState unicodeTargetState) throws JsonParseException {
        consume();
        /*  %x22 /          ; "    quotation mark  U+0022
            %x5C /          ; \    reverse solidus U+005C
            %x2F /          ; /    solidus         U+002F
            %x62 /          ; b    backspace       U+0008
            %x66 /          ; f    form feed       U+000C
            %x6E /          ; n    line feed       U+000A
            %x72 /          ; r    carriage return U+000D
            %x74 /          ; t    tab             U+0009
            %x75 4HEXDIG )  ; uXXXX                U+XXXX */
        if (c == QUOTE || c == ESCAPE || c == SLASH) {
            value.append(c);
            state = targetState;
        } else if (c == 'b') {
            value.append(B);
            state = targetState;
        } else if (c == 'f') {
            value.append(F);
            state = targetState;
        } else if (c == 'n') {
            value.append(LF);
            state = targetState;
        } else if (c == 'r') {
            value.append(CR);
            state = targetState;
        } else if (c == 't') {
            value.append(TAB);
            state = targetState;
        } else if (c == 'u') {
            state = unicodeTargetState;
        } else if (tokenChar(c)) {
            // this is invalid json, technically...but seems recoverable:
            // assume \ was not meant to escape
            value.append(ESCAPE);
            value.append(c);
            state = targetState;
        } else {
            throw expected("valid escape sequence");
        }
        tokenStart();
    }

    private void unicodeEscape(JsonParserState targetState) throws JsonParseException {
        if (unicodeBuf.hasRemaining()) {
            consume();
            if (isHex(c)) {
                unicodeBuf.append(c);
            } else {
                // this is invalid json, technically...but seems recoverable:
                // assume \ was not meant to escape
                value.append(ESCAPE);
                unicodeBuf.flip();
                value.append(unicodeBuf);
                unicodeBuf.clear();
                state = targetState;
                tokenStart();
            }
        } else {
            unicodeBuf.flip();
            int characterCode;
            try {
                characterCode = Integer.parseInt(unicodeBuf.toString(), 16);
            } catch (NumberFormatException e) {
                throw expected("valid unicode escape character code");
            }
            value.append((char) characterCode);
            unicodeBuf.clear();
            state = targetState;
            buf.mark();
        }
    }

    private boolean isHex(char c) {
        return Arrays.binarySearch(HEX, c) >= 0;
    }

    private boolean tokenChar(char c) {
        return !Character.isISOControl(c);
    }

    private void consumeTokens() {
        int initialPosition = buf.position();
        int position = initialPosition;
        if (position <= 0) {
            // assert buf.position() == initialPosition;
            buf.mark();
            return;
        }
        if (buf.get(position - 1) == QUOTE) {
            position--;
        }

        buf.reset();
        int mark = buf.position();
        if (buf.get(mark) == QUOTE) {
            mark++;
        }

        int length = position - mark;
        if (length <= 0) {
            buf.position(initialPosition);
            buf.mark();
            return;
        }

        value.append(buf.array(), mark, length);
        buf.position(initialPosition);
        buf.mark();
    }

    private void tokenStart() {
        buf.position(buf.position() - 1);
        buf.mark();
        buf.get();
    }

    private boolean isWhitespace(char c) {
        return c == SP || c == LF || c == CR || c == TAB;
    }

    private JsonParseException expected(String expected) {
        state = END;
        return new JsonParseException("Expected " + expected + " at position " + line + ":" + col);
    }

    private void consume() {
        c = buf.get();
        if (c == LF) {
            col = 0;
            line++;
        } else {
            col++;
        }
    }

    private void fillBuffer() throws IOException {
        if (reader == null) {
            initReader();
        }
        if (buf.hasRemaining()) {
            return;
        }

        buf.clear();
        int read = reader.read(buf);
        if (read == -1) {
            buf.limit(0);
        } else {
            buf.flip();
            buf.mark();
        }
    }

    private void initReader() throws IOException {
        if (input == null) {
            return;
        }

        InputStream is = input;
        if (!is.markSupported()) {
            is = new BufferedInputStream(is, 16);
        }

        String determinedEncoding = DETECTION_UNKNOWN;

        is.mark(16);
        try {
            byte[] buf = new byte[8];
            int count = 0;
            while (count < 4) {
                int read = is.read(buf, count, buf.length - count);
                if (read == -1) {
                    count = 0;
                    break;
                }
                if (read > 0) {
                    count += read;
                }
            }

            if (count >= 4) {
                determinedEncoding = determineEncoding(buf);
            }
        } finally {
            try {
                is.reset();
            } catch (IOException e) {
                // ignore
            }
        }

        initReaderWithEncoding(determinedEncoding);
    }

    private void initReaderWithEncoding(String determinedEncoding) throws UnsupportedEncodingException {
        if (DETECTION_UNKNOWN.equals(determinedEncoding)) {
            determinedEncoding = "UTF-8";
        }
        reader = new InputStreamReader(input, determinedEncoding);
    }

    private String determineEncoding(byte[] buf) {
        /* 00 00 00 xx  UTF-32BE
           00 xx 00 xx  UTF-16BE
           xx 00 00 00  UTF-32LE
           xx 00 xx 00  UTF-16LE
           xx xx xx xx  UTF-8 */
        String determinedEncoding = DETECTION_UNKNOWN;
        byte b1 = buf[0];
        byte b2 = buf[1];
        byte b3 = buf[2];
        byte b4 = buf[3];
        if (b1 == NULL) { // UTF-32BE or UTF-16BE
            if (b2 == NULL) { // UTF-32BE?
                if (b3 == NULL) { // UTF-32BE
                    determinedEncoding = DETECTION_UTF32BE;
                } else { // invalid, guess UTF-8
                    determinedEncoding = DETECTION_UTF8;
                }
            } else if (b3 == NULL) { // UTF-16BE
                determinedEncoding = DETECTION_UTF16BE;
            }
        } else if (b2 == NULL) { // UTF-32LE or UTF-16LE
            if (b3 == NULL) { // UTF-32LE
                if (b4 == NULL) { // UTF-32LE
                    determinedEncoding = DETECTION_UTF32LE;
                } else { // invalid, guess UTF-8
                    determinedEncoding = DETECTION_UTF8;
                }
            } else { // UTF-16LE
                if (b4 == NULL) { // UTF-16LE
                    determinedEncoding = DETECTION_UTF16LE;
                } else { // invalid, guess UTF-8
                    determinedEncoding = DETECTION_UTF8;
                }
            }
        } else {
            // either valid UTF-8 or invalid and guess UTF-8
            determinedEncoding = DETECTION_UTF8;
        }

        return determinedEncoding;
    }

    public String getString() {
        switch (valueType) {
            case TYPE_FALSE:
                return "false";
            case TYPE_TRUE:
                return "true";
            case TYPE_NULL:
                return "null";
            case TYPE_STRING:
            case TYPE_NUMBER:
            default:
                return value.toString();
        }
    }

    public int getInteger() {
        switch (valueType) {
            case TYPE_FALSE:
                return 0;
            case TYPE_TRUE:
                return 1;
            case TYPE_NULL:
                return -1;
            case TYPE_STRING:
            case TYPE_NUMBER:
            default:
                return Integer.parseInt(value.toString());
        }
    }

    public long getLong() {
        switch (valueType) {
            case TYPE_FALSE:
                return 0L;
            case TYPE_TRUE:
                return 1L;
            case TYPE_NULL:
                return -1L;
            case TYPE_STRING:
            case TYPE_NUMBER:
            default:
                return Long.parseLong(value.toString());
        }
    }

    public float getFloat() {
        switch (valueType) {
            case TYPE_FALSE:
                return 0f;
            case TYPE_TRUE:
                return 1f;
            case TYPE_NULL:
                return -1f;
            case TYPE_STRING:
            case TYPE_NUMBER:
            default:
                return Float.parseFloat(value.toString());
        }
    }

    public double getDouble() {
        switch (valueType) {
            case TYPE_FALSE:
                return 0d;
            case TYPE_TRUE:
                return 1d;
            case TYPE_NULL:
                return -1d;
            case TYPE_STRING:
            case TYPE_NUMBER:
            default:
                return Double.parseDouble(value.toString());
        }
    }

    public boolean getBoolean() {
        switch (valueType) {
            case TYPE_FALSE:
                return false;
            case TYPE_TRUE:
                return true;
            case TYPE_NULL:
                return false;
            case TYPE_STRING:
            case TYPE_NUMBER:
            default:
                String str = value.toString().trim();
                return "1".equals(str) || Boolean.parseBoolean(str);
        }
    }
}
