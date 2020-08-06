package org.jianzhao.sugar;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.util.TypeUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.jianzhao.sugar.Sugar.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.util.StreamUtils.copyToByteArray;

class SugarTest {

    @Test
    public void testPrintln() {
        // 👋 System.out.println();
        println("hello world");
    }

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
        with(null, it -> fail());

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
                Foobar.of("a", "A"),
                Foobar.of("a", "AA"),
                Foobar.of("b", "B"),
                Foobar.of("b", "B")
        );
        assertEquals(3, distinct(list).size());
        assertEquals(2, distinct(list, Foobar::getFoo).size());
    }

    @Test
    public void testToMap() {
        val list = listOf(
                Foobar.of("a", "A"),
                Foobar.of("b", "B")
        );
        val map = toMap(list, Foobar::getFoo, Foobar::getBar);
        assertEquals(mapOf("a", "A", "b", "B"), map);
    }

    @Test
    public void testGroupToMap() {
        val list = listOf(
                Foobar.of("a", "A"),
                Foobar.of("a", "AA"),
                Foobar.of("b", "B"),
                Foobar.of("b", "BB")
        );
        val groupMap = groupToMap(list, Foobar::getFoo, Foobar::getBar);
        assertEquals(2, groupMap.size());
        assertIterableEquals(listOf("A", "AA"), groupMap.get("a"));
        assertIterableEquals(listOf("B", "BB"), groupMap.get("b"));
    }

    @Test
    public void testIncludes() {
        val list = listOf(3, 1, 4, 1, 5, 9, 2, 6);
        assertTrue(includes(list, 3));
        assertFalse(includes(list, 7));
    }

    @Test
    public void testCollectionShortcut() {
        List<Map<String, Set<Foobar>>> c = listOf(
                mapOf("1", setOf(Foobar.of("a", "A"), Foobar.of("b", "B"))),
                mapOf("2", setOf(Foobar.of("c", "C")), "3", setOf(Foobar.of("d", "D"))),
                mapOf("4", setOf()),
                mapOf()
        );
        assertTrue(c.get(1).get("2").contains(Foobar.of("c", "C")));
    }
}
