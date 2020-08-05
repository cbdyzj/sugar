package org.jianzhao.sugar;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.jianzhao.sugar.Sugar.*;
import static org.junit.jupiter.api.Assertions.*;

class SugarTest {

    @Test
    public void testUse() throws IOException {
        byte[] origin = {104, 101, 108, 108, 111, 32, 65, 100, 97};
        var in = new ByteArrayInputStream(origin);
        var s = use(in, it -> new String(it.readAllBytes(), UTF_8));
        assertEquals("hello Ada", s);
    }

    @Test
    public void testWith() {
        with(null, t -> fail());

        var list = new ArrayList<>(List.of(1, 2, 3));
        with(list, Collections::reverse);
        assertIterableEquals(List.of(3, 2, 1), list);
    }

    @Test
    public void testReduce() {
        var list = List.of(1, 2, 3, 4, 5);
        var sum = reduce(list, 0, Integer::sum);
        assertEquals(15, sum);

        var sb = reduce(list, new StringBuilder(), StringBuilder::append);
        assertEquals("12345", sb.toString());
    }

    @Test
    public void testMap() {
        var list = List.of(3, 1, 4, 1, 5);
        assertIterableEquals(List.of("3", "1", "4", "1", "5"), map(list, String::valueOf));
    }

    @Test
    public void testPartition() {
        var list = Stream.generate(Object::new).limit(10).collect(Collectors.toList());
        var partitionList = partition(list, 3);
        assertEquals(4, partitionList.size());
        assertThrows(IllegalArgumentException.class, () -> partition(list, 0));
    }

    @Test
    public void testDistinct() {
        var list = List.of(
                Map.of("key", 1, "value", 314),
                Map.of("key", 1, "value", 159),
                Map.of("key", 2, "value", 265),
                Map.of("key", 2, "value", 265)
        );
        assertEquals(3, distinct(list).size());
        assertEquals(2, distinct(list, t -> t.get("key")).size());
    }

    @Test
    public void testIncludes() {
        var list = List.of(3, 1, 4, 1, 5, 9, 2, 6);
        assertTrue(includes(list, 3));
        assertFalse(includes(list, 7));
    }
}
