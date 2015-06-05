/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.exception;

import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test(groups = {"functional"})
public class HttpExceptionTest {
    private List<Class<? extends HttpException>> exceptions = Arrays.asList(HttpException.class,

            BadRequest.class, Conflict.class, Forbidden.class, InternalServerError.class, MethodNotAllowed.class,
            NotAcceptable.class, NotFound.class, NotImplemented.class, RequestEntityTooLarge.class,
            RequestURITooLong.class,
            //SerializerNotFoundException.class,
            Unauthorized.class);

    public void testHttpExceptions() throws Exception {
        Constructor<? extends HttpException> constructor;
        HttpException e;
        String message = "CUSTOM MESSAGE";
        String detail = "foo";
        Exception cause = new Exception();
        int code = 999;
        int otherCode = 888;

        for (Class<? extends HttpException> clazz : exceptions) {
            if (!"HttpException".equals(clazz.getSimpleName())) {
                e = clazz.newInstance();
                int defaultCode = e.getStatusCode();

                HttpException other = HttpException.fromCode(defaultCode);
                assertTrue(clazz.isInstance(other));
                other = HttpException.fromCode(defaultCode, message);
                assertEquals(other.getMessage(), message);
            }

            constructor = clazz.getConstructor(Throwable.class);
            e = constructor.newInstance(cause);
            assertEquals(e.getCause(), cause);

            constructor = clazz.getConstructor(String.class);
            e = constructor.newInstance(message);
            assertEquals(e.getMessage(), message);
            assertTrue(e.toString().contains(message));

            constructor = clazz.getConstructor(String.class, Throwable.class);
            e = constructor.newInstance(message, cause);
            assertEquals(e.getMessage(), message);
            assertEquals(e.getCause(), cause);
            assertTrue(e.toString().contains(message));

            constructor = clazz.getConstructor(int.class);
            e = constructor.newInstance(code);
            assertEquals(e.getStatusCode(), code);
            assertTrue(e.toString().contains(Integer.toString(code)));

            constructor = clazz.getConstructor(String.class, int.class);
            e = constructor.newInstance(message, code);
            assertEquals(e.getMessage(), message);
            assertEquals(e.getStatusCode(), code);
            assertTrue(e.toString().contains(message));
            assertTrue(e.toString().contains(Integer.toString(code)));

            constructor = clazz.getConstructor(String.class, Throwable.class, int.class);
            e = constructor.newInstance(message, cause, code);
            assertEquals(e.getMessage(), message);
            assertEquals(e.getStatusCode(), code);
            assertEquals(e.getCause(), cause);
            assertTrue(e.toString().contains(message));
            assertTrue(e.toString().contains(Integer.toString(code)));

            constructor = clazz.getConstructor(Throwable.class, int.class);
            e = constructor.newInstance(cause, code);
            assertEquals(e.getStatusCode(), code);
            assertEquals(e.getCause(), cause);
            assertTrue(e.toString().contains(Integer.toString(code)));

            e.setDetail(detail);
            assertEquals(e.getDetail(), detail);
            e.setStatusCode(otherCode);
            assertEquals(e.getStatusCode(), otherCode);
        }

        e = new HttpException(-1);
        assertEquals(e.getStatusCode(), 500);

        assertTrue(HttpException.fromCode(499) instanceof BadRequest);
        assertTrue(HttpException.fromCode(599) instanceof InternalServerError);
    }

    @Test(expectedExceptions = {RequestEntityTooLarge.class})
    public void testThrowRequestEntityTooLarge() throws Exception {
        RequestEntityTooLarge.throwForSize(12345);
    }

    @Test(expectedExceptions = {RequestURITooLong.class})
    public void testThrowRequestURITooLong() throws Exception {
        RequestURITooLong.throwForSize(12345);
    }
}
