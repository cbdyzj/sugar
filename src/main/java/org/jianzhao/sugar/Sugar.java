package org.jianzhao.sugar;

import lombok.SneakyThrows;

import java.io.Closeable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 勉强增加一点Java☕的甜度
 *
 * @author cbdyzj
 * @since 2020.8.4
 */
public final class Sugar {

    private Sugar() {
    }

    public static void println(Object o) {
        System.out.println(o);
    }

    public static void printf(String format, Object... args) {
        System.out.printf(format, args);
    }

    @SneakyThrows
    public static <T> void with(T target, CTE<T> block) {
        if (target == null) {
            return;
        }
        Objects.requireNonNull(block);
        block.invoke(target);
    }

    @SneakyThrows
    public static <T extends Closeable> void use(T t, ATE block) {
        Objects.requireNonNull(block);
        try (T _t = t) {
            block.invoke();
        }
    }

    @SneakyThrows
    public static <T1 extends Closeable, T2 extends Closeable> void use(T1 t1, T2 t2, ATE block) {
        Objects.requireNonNull(block);
        try (T1 _t1 = t1; T2 _t2 = t2) {
            block.invoke();
        }
    }

    @SneakyThrows
    public static <L extends Lock> void use(L l, ATE block) {
        Objects.requireNonNull(block);
        l.lock();
        try {
            block.invoke();
        } finally {
            l.unlock();
        }
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> List<T> toList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

    public static <T> List<T> toList(T[] array) {
        if (array == null || array.length == 0) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(array));
    }

    public static <T, R> List<R> map(List<T> list, Function<? super T, ? extends R> mapper) {
        if (isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(mapper).collect(Collectors.toList());
    }

    public static <T> void forEach(List<T> list, Consumer<? super T> action) {
        if (isEmpty(list)) {
            return;
        }
        list.forEach(action);
    }

    public static <T> T findFirst(List<T> list, Predicate<? super T> predicate) {
        if (isEmpty(list)) {
            return null;
        }
        Optional<T> ot = list.stream().filter(predicate).findFirst();
        return ot.orElse(null);
    }

    public static <T> boolean every(List<T> list, Predicate<? super T> predicate) {
        if (isEmpty(list)) {
            return false;
        }
        return list.stream().allMatch(predicate);
    }

    public static <T> List<T> distinct(List<T> list, Function<? super T, ?> keyExtractor) {
        Objects.requireNonNull(keyExtractor);
        Set<Object> seen = new HashSet<>();
        return filter(list, t -> {
            Object key = keyExtractor.apply(t);
            if (seen.contains(key)) {
                return false;
            }
            seen.add(key);
            return true;
        });
    }

    public static <T> List<T> distinct(List<T> list) {
        if (isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().distinct().collect(Collectors.toList());
    }

    public static <T> List<T> filter(List<T> list, Predicate<? super T> predicate) {
        if (isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().filter(predicate).collect(Collectors.toList());
    }

    public static <T, R> R reduce(List<T> list, R identity, BiFunction<R, ? super T, R> accumulator) {
        if (isEmpty(list)) {
            return identity;
        }
        return list.stream().reduce(identity, accumulator, (a, c) -> null);
    }

    public static <T> List<List<T>> partition(List<T> list, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be positive integer");
        }
        if (isEmpty(list)) {
            return new ArrayList<>();
        }
        List<List<T>> result = new ArrayList<>(list.size() / size + 1);
        List<T> currentList = null;
        for (int i = 0; i < list.size(); i++) {
            if (i % size == 0) {
                currentList = new ArrayList<>(size);
                result.add(currentList);
            }
            currentList.add(list.get(i));
        }
        return result;
    }

    public static <T, K> Map<K, T> toMap(List<T> list, Function<? super T, ? extends K> keyExtractor) {
        Objects.requireNonNull(keyExtractor);
        return toMap(list, keyExtractor, Function.identity());
    }

    public static <T, K, V> Map<K, V> toMap(List<T> list,
                                            Function<? super T, ? extends K> keyExtractor,
                                            Function<? super T, ? extends V> valueExtractor) {
        if (isEmpty(list)) {
            return new HashMap<>();
        }
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(valueExtractor);
        return list.stream().collect(Collectors.toMap(keyExtractor, valueExtractor));
    }

    public static <T, K> Map<K, List<T>> groupToMap(List<T> list, Function<? super T, ? extends K> keyExtractor) {
        if (isEmpty(list)) {
            return new HashMap<>();
        }
        Objects.requireNonNull(keyExtractor);
        return list.stream().collect(Collectors.groupingBy(keyExtractor));
    }

    public static <T, K, V> Map<K, List<V>> groupToMap(List<T> list,
                                                       Function<? super T, ? extends K> keyExtractor,
                                                       Function<? super T, ? extends V> valueExtractor) {
        if (isEmpty(list)) {
            return new HashMap<>();
        }
        Objects.requireNonNull(keyExtractor);
        Objects.requireNonNull(valueExtractor);
        return list.stream().collect(
                Collectors.groupingBy(keyExtractor, Collectors.mapping(valueExtractor, Collectors.toList())));
    }

    public static <T> boolean includes(List<T> list, Predicate<? super T> predicate) {
        if (isEmpty(list)) {
            return false;
        }
        return list.stream().anyMatch(predicate);
    }

    @SafeVarargs
    public static <T> List<T> listOf(T... ts) {
        return new ArrayList<>(Arrays.asList(ts));
    }

    @SafeVarargs
    public static <T> Set<T> setOf(T... ts) {
        return new HashSet<>(Arrays.asList(ts));
    }

    public static <K, V> Map<K, V> mapOf() {
        return new HashMap<>();
    }

    public static <K, V> Map<K, V> mapOf(K k, V v) {
        return buildMap(k, v);
    }

    public static <K, V> Map<K, V> mapOf(K k1, V v1,
                                         K k2, V v2) {
        return buildMap(k1, v1, k2, v2);
    }

    public static <K, V> Map<K, V> mapOf(K k1, V v1,
                                         K k2, V v2,
                                         K k3, V v3) {
        return buildMap(k1, v1, k2, v2, k3, v3);
    }


    public static <K, V> Map<K, V> mapOf(K k1, V v1,
                                         K k2, V v2,
                                         K k3, V v3,
                                         K k4, V v4) {
        return buildMap(k1, v1, k2, v2, k3, v3, k4, v4);
    }

    public static <K, V> Map<K, V> mapOf(K k1, V v1,
                                         K k2, V v2,
                                         K k3, V v3,
                                         K k4, V v4,
                                         K k5, V v5) {
        return buildMap(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
    }

    public static <K, V> Map<K, V> mapOf(K k1, V v1,
                                         K k2, V v2,
                                         K k3, V v3,
                                         K k4, V v4,
                                         K k5, V v5,
                                         K k6, V v6) {
        return buildMap(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6);
    }

    public static <K, V> Map<K, V> mapOf(K k1, V v1,
                                         K k2, V v2,
                                         K k3, V v3,
                                         K k4, V v4,
                                         K k5, V v5,
                                         K k6, V v6,
                                         K k7, V v7) {
        return buildMap(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7);
    }

    public static <K, V> Map<K, V> mapOf(K k1, V v1,
                                         K k2, V v2,
                                         K k3, V v3,
                                         K k4, V v4,
                                         K k5, V v5,
                                         K k6, V v6,
                                         K k7, V v7,
                                         K k8, V v8) {
        return buildMap(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8);
    }

    public static <K, V> Map<K, V> mapOf(K k1, V v1,
                                         K k2, V v2,
                                         K k3, V v3,
                                         K k4, V v4,
                                         K k5, V v5,
                                         K k6, V v6,
                                         K k7, V v7,
                                         K k8, V v8,
                                         K k9, V v9) {
        return buildMap(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9);
    }

    public static <K, V> Map<K, V> mapOf(K k1, V v1,
                                         K k2, V v2,
                                         K k3, V v3,
                                         K k4, V v4,
                                         K k5, V v5,
                                         K k6, V v6,
                                         K k7, V v7,
                                         K k8, V v8,
                                         K k9, V v9,
                                         K k10, V v10) {
        return buildMap(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9, k10, v10);
    }

    private static <K, V> Map<K, V> buildMap(Object... input) {
        if ((input.length & 1) != 0) {
            throw new IllegalArgumentException("length is odd");
        }
        Map<Object, Object> map = new HashMap<>();
        for (int i = 0; i < input.length; i += 2) {
            map.put(input[i], input[i + 1]);
        }
        return cast(map);
    }

    public static <T> T[] ref(T t) {
        Objects.requireNonNull(t);
        T[] ref = cast(Array.newInstance(t.getClass(), 1));
        ref[0] = t;
        return ref;
    }

    public static Object[] ref() {
        return new Object[]{null};
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object o) {
        return (T) o;
    }

    /**
     * ActionThrowsException
     */
    public interface ATE {

        void invoke() throws Exception;
    }

    /**
     * ConsumerThrowsException
     */
    public interface CTE<T> {

        void invoke(T target) throws Exception;
    }

    @SneakyThrows
    public static void repeat(int times, ATE block) {
        if (times <= 0) {
            throw new IllegalArgumentException("times requires positive integer");
        }
        Objects.requireNonNull(block);
        for (int i = 0; i < times; i++) {
            block.invoke();
        }
    }
}
