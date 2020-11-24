package org.jianzhao.sugar;

import org.jianzhao.sugar.support.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
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
    public void testUseLock() throws InterruptedException {
        int nTask = 8;
        CountDownLatch cdl = new CountDownLatch(nTask);
        Integer[] ref = ref(0);
        Runnable task = (Runnable) () -> {
            repeat(10000, () -> ref[0]++);
            cdl.countDown();
        };
        ReentrantLock lock = new ReentrantLock();
        Runnable taskWithLock = (Runnable) () -> use(lock, task::run);
        ExecutorService pool = Executors.newFixedThreadPool(4);
        repeat(nTask, () -> pool.submit(taskWithLock));
        cdl.await();
        pool.shutdown();
        assertEquals(10000 * nTask, ref[0]);
    }

    @Test
    public void testReduce() {
        List<Integer> list = listOf(1, 2, 3, 4, 5);
        Integer sum = reduce(list, 0, Integer::sum);
        assertEquals(15, sum);
        // Another test case
        StringBuilder sb = reduce(list, new StringBuilder(), StringBuilder::append);
        assertEquals("12345", sb.toString());
    }

    @Test
    public void testMap() {
        List<Integer> list = listOf(3, 1, 4, 1, 5);
        assertIterableEquals(listOf("3", "1", "4", "1", "5"), map(list, String::valueOf));
    }

    @Test
    public void testPartition() {
        List<Object> list = Stream.generate(Object::new).limit(10).collect(Collectors.toList());
        List<List<Object>> partitionList = partition(list, 3);
        assertEquals(4, partitionList.size());
        assertThrows(IllegalArgumentException.class, () -> partition(list, 0));
    }

    @Test
    public void testDistinct() {
        List<Pair<String, String>> list = listOf(
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
        List<Pair<String, String>> list = listOf(
                Pair.of("a", "A"),
                Pair.of("b", "B")
        );
        Map<String, String> map = toMap(list, Pair::getKey, Pair::getValue);
        assertEquals(mapOf("a", "A", "b", "B"), map);
    }

    @Test
    public void testGroupToMap() {
        List<Pair<String, String>> list = listOf(
                Pair.of("a", "A"),
                Pair.of("a", "AA"),
                Pair.of("b", "B"),
                Pair.of("b", "BB")
        );
        Map<String, List<String>> groupMap = groupToMap(list, Pair::getKey, Pair::getValue);
        assertEquals(2, groupMap.size());
        assertIterableEquals(listOf("A", "AA"), groupMap.get("a"));
        assertIterableEquals(listOf("B", "BB"), groupMap.get("b"));
    }

    @Test
    public void testIncludes() {
        List<Integer> list = listOf(3, 1, 4, 1, 5, 9, 2, 6);
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
        List<Integer> list = listOf(3, 1, 4, 1, 5, 9);
        assertTrue(every(list, it -> it > 0));
    }

    @Test
    public void testFindFirst() {
        List<Integer> list = listOf(3, 1, 4, 1, 5, 9);
        Integer first = findFirst(list, it -> it > 3);
        assertEquals(4, first);
    }

    @Test
    public void testToList() {
        Integer[] ref = ref(1);
        Iterable<Integer> iterable = () -> Stream.generate(() -> ref[0]++).limit(5).iterator();
        List<Integer> list = toList(iterable);
        assertIterableEquals(listOf(1, 2, 3, 4, 5), list);
    }
}
