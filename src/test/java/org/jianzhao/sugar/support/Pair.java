package org.jianzhao.sugar.support;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Pair for test
 *
 * @author cbdyzj
 * @since 2020.8.6
 */
public class Pair<K, V> extends AbstractMap.SimpleImmutableEntry<K, V> {

    private Pair(K key, V value) {
        super(key, value);
    }

    private Pair(Map.Entry<? extends K, ? extends V> entry) {
        super(entry);
    }

    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }

    public static <K, V> Pair<K, V> of(Map.Entry<? extends K, ? extends V> entry) {
        return new Pair<>(entry);
    }
}
