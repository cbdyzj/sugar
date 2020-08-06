package org.jianzhao.sugar;

import lombok.Data;
import lombok.val;

/**
 * 测试用
 */
@Data
public class Foobar {

    private String foo;
    private String bar;

    public static Foobar of(String foo, String bar) {
        val foobar = new Foobar();
        foobar.setFoo(foo);
        foobar.setBar(bar);
        return foobar;
    }
}
