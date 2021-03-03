package com.glqdlt.ex.reactive;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author jhun
 */
public class TestDummyStub {
    public final static List<String> SOME_DATA;

    public final static List<Integer> EXAMPLE_1_STUB = Arrays.asList(14,
            28,
            42,
            56,
            70,
            84,
            98);

    static {
        SOME_DATA = IntStream.rangeClosed(1, 100)
                .boxed()
                .map(x -> {
                    if (x % 2 == 0 || x % 7 == 0) {
                        return null;
                    }
                    return String.format("NUMBER_%s", x);
                }).collect(Collectors.toList());
    }
}
