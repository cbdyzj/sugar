package org.jianzhao.sugar;

import lombok.val;
import org.jianzhao.sugar.support.Pair;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public void testWith() {
        assertThrows(IOException.class, () -> with((ATE) () -> {
            throw new IOException("Dummy IOException");
        }));
    }

    @Test
    public void testWithResource() {
        // "hello Ada" bytes
        byte[] origin = {104, 101, 108, 108, 111, 32, 65, 100, 97};
        val closed = ref(false);
        val in = new ByteArrayInputStream(origin) {
            @Override
            public void close() throws IOException {
                super.close();
                closed[0] = true;
            }
        };
        val s = with(in, () -> {
            val bytes = copyToByteArray(in);
            val utf8 = "utf8";
            return new String(bytes, Charset.forName(utf8));
        });
        assertTrue(closed[0]);
        assertEquals("hello Ada", s);
    }

    @Test
    public void testWithLock() {
        val nTask = 8;
        val cdl = new CountDownLatch(nTask);
        val ref = ref(0);
        val task = (Runnable) () -> {
            repeat(10000, () -> ref[0]++);
            cdl.countDown();
        };
        val lock = new ReentrantLock();
        val taskWithLock = (Runnable) () -> with(lock, task::run);
        val pool = Executors.newFixedThreadPool(4);
        repeat(nTask, () -> pool.submit(taskWithLock));
        with((ATE) cdl::await);
        pool.shutdown();
        assertEquals(10000 * nTask, ref[0]);
    }

    @Test
    public void testReduce() {
        val list = listOf(1, 2, 3, 4, 5);
        val sum = reduce(list, 0, Integer::sum);
        assertEquals(15, sum);
        // Another test case
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
                Pair.of("a", "A"),
                Pair.of("a", "AA"),
                Pair.of("b", "B"),
                Pair.of("b", "B")
        );
        assertEquals(3, distinct(list).size());
        assertEquals(2, distinct(list, Pair::getKey).size());
    }

    @Test
    public void testToMap() {
        val list = listOf(
                Pair.of("a", "A"),
                Pair.of("b", "B")
        );
        val map = toMap(list, Pair::getKey, Pair::getValue);
        assertEquals(mapOf("a", "A", "b", "B"), map);
    }

    @Test
    public void testGroupToMap() {
        val list = listOf(
                Pair.of("a", "A"),
                Pair.of("a", "AA"),
                Pair.of("b", "B"),
                Pair.of("b", "BB")
        );
        val groupMap = groupToMap(list, Pair::getKey, Pair::getValue);
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
        List<Map<String, Set<Pair<String, String>>>> c = listOf(
                mapOf("1", setOf(Pair.of("a", "A"), Pair.of("b", "B"))),
                mapOf("2", setOf(Pair.of("c", "C")), "3", setOf(Pair.of("d", "D"))),
                mapOf("4", setOf()),
                mapOf()
        );
        assertTrue(c.get(1).get("2").contains(Pair.of("c", "C")));
    }
}
