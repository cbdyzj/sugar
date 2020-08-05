package org.jianzhao.sugar;

import lombok.SneakyThrows;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
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
    public static <T> void with(T target, ConsumerThrowsException<? super T> block) {
        if (target == null) {
            return;
        }
        Objects.requireNonNull(block);
        block.invoke(target);
    }

    public interface ConsumerThrowsException<T> {

        void invoke(T t) throws Exception;
    }

    public static <T extends Closeable, R> R use(T t, FunctionThrowsIOException<? super T, ? extends R> block) throws IOException {
        Objects.requireNonNull(block);
        try (t) {
            return block.invoke(t);
        }
    }

    public static <T1 extends Closeable, T2 extends Closeable, R> R use(T1 t1, T2 t2, BiFunctionThrowsIOException<? super T1, ? super T2, ? extends R> block) throws IOException {
        Objects.requireNonNull(block);
        try (t1; t2) {
            return block.invoke(t1, t2);
        }
    }

    public interface FunctionThrowsIOException<T, R> {

        R invoke(T t) throws IOException;
    }

    public interface BiFunctionThrowsIOException<T1, T2, R> {

        R invoke(T1 t1, T2 t2) throws IOException;
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T, R> List<R> map(List<T> list, Function<? super T, ? extends R> mapper) {
        if (isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(mapper).collect(Collectors.toList());
    }

    public static <T> List<T> distinct(List<T> list, Function<? super T, ?> keyExtractor) {
        var seen = new HashSet<>();
        return filter(list, t -> {
            var key = keyExtractor.apply(t);
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

    public static <T, K> Map<K, T> toMap(List<T> list, Function<? super T, ? extends K> keyMapper) {
        return toMap(list, keyMapper, Function.identity());
    }

    public static <T, K, V> Map<K, V> toMap(List<T> list, Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends V> valueMapper) {
        if (isEmpty(list)) {
            return new HashMap<>();
        }
        return list.stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }

    public static <T> boolean includes(List<T> list, T item) {
        return includes(list, Predicate.isEqual(item));
    }

    public static <T> boolean includes(List<T> list, Predicate<? super T> predicate) {
        if (isEmpty(list)) {
            return false;
        }
        return list.stream().anyMatch(predicate);
    }

}
