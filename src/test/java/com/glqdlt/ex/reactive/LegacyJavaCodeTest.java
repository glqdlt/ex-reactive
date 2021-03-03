package com.glqdlt.ex.reactive;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author jhun
 */
public class LegacyJavaCodeTest {

    private List<String> _dummy = new ArrayList<>(TestDummyStub.SOME_DATA);

    /**
     * 2N 으로 동작한다. N 으로도 동작하게 할수 있겠지만 너무 코드가 지저분해서 안함.
     */
    @Test
    public void example1() {

        final List<Integer> result = new LinkedList<>();

        for (String s : _dummy) {
            if (s != null) {
                result.add(Integer.parseInt(s.replace("NUMBER_", "")));
            } else {
                result.add(0);
            }
        }

        final List<Integer> result2 = new LinkedList<>();

        for (Integer i : result) {
            Integer current = i + 5;
            if (current % 7 == 0 && current % 2 == 0) {
                result2.add(current);
            }
        }

        Assert.assertEquals(TestDummyStub.EXAMPLE_1_STUB, result2);

    }
}