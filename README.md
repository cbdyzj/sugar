# sugar

[![Release](https://jitpack.io/v/cbdyzj/sugar.svg)](https://jitpack.io/#cbdyzj/sugar)

勉强增加一点Java☕的甜度

```java
byte[] origin = {104, 101, 108, 108, 111, 32, 65, 100, 97};
var in = new ByteArrayInputStream(origin);
var s = use(in, it -> new String(it.readAllBytes(), UTF_8));
assertEquals("hello Ada", s);
```
