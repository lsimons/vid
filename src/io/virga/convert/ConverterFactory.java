/* Copyright 2014 The VID Authors. Licensed under CC by-nc-nd 4.0. */
package io.virga.convert;

import io.virga.exception.SerializerNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConverterFactory {
    private static List<Serializer> serializers;

    static {
        List<Serializer> serializerList = new ArrayList<>();

        // could add default serializers here

        serializers = new CopyOnWriteArrayList<>(serializerList);
    }

    public static void register(Serializer serializer) {
        serializers.add(serializer);
    }

    @SuppressWarnings("unchecked")
    public static <T> Serializer<T> getSerializer(T object) throws SerializerNotFoundException {
        for (Serializer serializer : serializers) {
            if (serializer.supports(object)) {
                return serializer;
            }
        }

        throw new SerializerNotFoundException(object);
    }
}
