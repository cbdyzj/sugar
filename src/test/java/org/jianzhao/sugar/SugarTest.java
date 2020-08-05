package org.jianzhao.sugar;

import lombok.val;
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
import static org.springframework.util.StreamUtils.copyToByteArray;

class SugarTest {

    @Test
    public void testUse() throws IOException {
        byte[] origin = {104, 101, 108, 108, 111, 32, 65, 100, 97};
        val in = new ByteArrayInputStream(origin);
        val ref = ref();
        use(in, it -> {
            val bytes = copyToByteArray(it);
            ref[0] = new String(bytes, UTF_8);
        });
        assertEquals("hello Ada", ref[0]);
    }

    @Test
    public void testWith() {
        with(null, t -> fail());

        val list = new ArrayList<>(listOf(1, 2, 3));
        with(list, Collections::reverse);
        assertIterableEquals(listOf(3, 2, 1), list);
    }

    @Test
    public void testReduce() {
        val list = listOf(1, 2, 3, 4, 5);
        val sum = reduce(list, 0, Integer::sum);
        assertEquals(15, sum);

        val sb = reduce(list, new StringBuilder(), StringBuilder::append);
        assertEquals("12345", sb.toString());
    }

    @Test
    public void testMap() {
        val list = listOf(3, 1, 4, 1, 5);
        assertIterableEquals(listOf("3", "1", "4", "1", "5"), map(list, String::valueOf));
    }

    @Test
    public void testPartition() {
        val list = Stream.generate(Object::new).limit(10).collect(Collectors.toList());
        val partitionList = partition(list, 3);
        assertEquals(4, partitionList.size());
        assertThrows(IllegalArgumentException.class, () -> partition(list, 0));
    }

    @Test
    public void testDistinct() {
        val list = listOf(
                mapOf("key", 1, "value", 314),
                mapOf("key", 1, "value", 159),
                mapOf("key", 2, "value", 265),
                mapOf("key", 2, "value", 265)
        );
        assertEquals(3, distinct(list).size());
        assertEquals(2, distinct(list, t -> t.get("key")).size());
    }

    @Test
    public void testGroupToMap() {
        val list = listOf(
                mapOf("key", 1, "value", 314),
                mapOf("key", 1, "value", 159),
                mapOf("key", 2, "value", 265),
                mapOf("key", 2, "value", 358)
        );
        Map<Integer, List<Integer>> groupMap = groupToMap(list, t -> t.get("key"), t -> t.get("value"));
        assertEquals(2, groupMap.size());
        assertIterableEquals(listOf(314, 159), groupMap.get(1));
        assertIterableEquals(listOf(265, 358), groupMap.get(2));
    }

    @Test
    public void testIncludes() {
        val list = listOf(3, 1, 4, 1, 5, 9, 2, 6);
        assertTrue(includes(list, 3));
        assertFalse(includes(list, 7));
    }
}
