/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.exception;

import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

@Test(groups = {"checkin"})
public class SerializerNotFoundExceptionTest {
    public void testSerializerNotFoundException() {
        SerializerNotFoundException e = new SerializerNotFoundException(this);
        assertTrue(e.getMessage().contains(this.getClass().getSimpleName()));
    }
}
