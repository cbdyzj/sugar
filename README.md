# sugar

[![Release](https://jitpack.io/v/cbdyzj/sugar.svg)](https://jitpack.io/#cbdyzj/sugar)

勉强增加一点Java☕的甜度

```java
byte[] origin = {104, 101, 108, 108, 111, 32, 65, 100, 97};
val in = new ByteArrayInputStream(origin);
val s = use(in, it -> new String(copyToByteArray(it), UTF_8));
assertEquals("hello Ada", s);
```
- JDK 8 required
- 使用参考：[SugarTest](https://github.com/cbdyzj/sugar/blob/master/src/test/java/org/jianzhao/sugar/SugarTest.java)
