package org.jianzhao.sugar;

import org.jianzhao.sugar.support.Pair;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.jianzhao.sugar.Sugar.*;
import static org.junit.jupiter.api.Assertions.*;

class SugarTest {

    @Test
    public void testPrintln() {
        // 👋 System.out.println();
        println("hello world");
    }

    @Test
    public void testWith() {
        with(null, it -> fail());
    }

    @Test
    public void testUseResource() {
        // "hello Ada" bytes
        byte[] origin = {104, 101, 108, 108, 111, 32, 65, 100, 97};
        var closed = ref(false);
        var in = new ByteArrayInputStream(origin) {
            @Override
            public void close() throws IOException {
                super.close();
                closed[0] = true;
            }
        };
        var string = ref();
        use(in, () -> {
            var bytes = in.readAllBytes();
            var utf8 = "utf8";
            string[0] = new String(bytes, Charset.forName(utf8));
        });
        assertTrue(closed[0]);
        assertEquals("hello Ada", string[0]);
    }

    @Test
    public void testUseLock() throws InterruptedException {
        var nTask = 8;
        var cdl = new CountDownLatch(nTask);
        var ref = ref(0);
        var task = (Runnable) () -> {
            repeat(10000, () -> ref[0]++);
            cdl.countDown();
        };
        var lock = new ReentrantLock();
        var taskWithLock = (Runnable) () -> use(lock, task::run);
        var pool = Executors.newFixedThreadPool(4);
        repeat(nTask, () -> pool.submit(taskWithLock));
        cdl.await();
        pool.shutdown();
        assertEquals(10000 * nTask, ref[0]);
    }

    @Test
    public void testReduce() {
        var list = listOf(1, 2, 3, 4, 5);
        var sum = reduce(list, 0, Integer::sum);
        assertEquals(15, sum);
        // Another test case
        var sb = reduce(list, new StringBuilder(), StringBuilder::append);
        assertEquals("12345", sb.toString());
    }

    @Test
    public void testMap() {
        var list = listOf(3, 1, 4, 1, 5);
        assertIterableEquals(listOf("3", "1", "4", "1", "5"), map(list, String::valueOf));
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
        var list = listOf(
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
        var list = listOf(
                Pair.of("a", "A"),
                Pair.of("b", "B")
        );
        var map = toMap(list, Pair::getKey, Pair::getValue);
        assertEquals(mapOf("a", "A", "b", "B"), map);
    }

    @Test
    public void testGroupToMap() {
        var list = listOf(
                Pair.of("a", "A"),
                Pair.of("a", "AA"),
                Pair.of("b", "B"),
                Pair.of("b", "BB")
        );
        var groupMap = groupToMap(list, Pair::getKey, Pair::getValue);
        assertEquals(2, groupMap.size());
        assertIterableEquals(listOf("A", "AA"), groupMap.get("a"));
        assertIterableEquals(listOf("B", "BB"), groupMap.get("b"));
    }

    @Test
    public void testIncludes() {
        var list = listOf(3, 1, 4, 1, 5, 9, 2, 6);
        assertTrue(includes(list, Predicate.isEqual(5)));
        assertFalse(includes(list, it -> it < 0));
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

    @Test
    public void testEvery() {
        var list = listOf(3, 1, 4, 1, 5, 9);
        assertTrue(every(list, it -> it > 0));
    }

    @Test
    public void testFindFirst() {
        var list = listOf(3, 1, 4, 1, 5, 9);
        var first = findFirst(list, it -> it > 3);
        assertEquals(4, first);
    }

    @Test
    public void testToList() {
        var ref = ref(1);
        var iterable = (Iterable<Integer>) () -> Stream.generate(() -> ref[0]++).limit(5).iterator();
        var list = toList(iterable);
        assertIterableEquals(listOf(1, 2, 3, 4, 5), list);
    }
}
