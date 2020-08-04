package org.jianzhao.sugar;

import lombok.SneakyThrows;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;

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
    public static <T> void with(T target, ConsumerThrowsException<T> block) {
        Objects.requireNonNull(block);
        block.invoke(target);
    }

    public static <T extends Closeable, R> R use(T target, FunctionThrowsIOException<T, R> block) throws IOException {
        Objects.requireNonNull(block);
        try (target) {
            return block.invoke(target);
        }
    }

    public interface FunctionThrowsIOException<T, R> {

        R invoke(T t) throws IOException;
    }

    public interface ConsumerThrowsException<T> {

        void invoke(T t) throws IOException;
    }
}
