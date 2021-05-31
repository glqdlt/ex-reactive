package com.glqdlt.ex.reactive;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author jhun
 */
public class Java8StreamTest {
    /**
     * 각 원소들은 독립적으로 선언된 함수 로직을 탄다.
     * LegacyJavaCode와 차이점은 레거시코드들은 어떠한 로직이 모든 원소를 다 처리하고, 다른 로직으로 넘기는 반면에
     * 스트림 개념은 원소 들은 다 개별적인 로직의 파이프 연산으로 진행되기 때문에, 먼저 작업이 끝난 원소는 다른 함수 로직으로 넘어가게 된다.
     * 중간에 Optional 로 null 을 감싸는 것은, null 을 원소로 취급하기 위함인데 이를 함수형 프로그래밍에서는 이를 모나드라고 얘기한다.
     * 모나드는 함수와 함수의 브릿지이다. 모나드는 단순하게 함수에 전달해주는 값과 타입의 제네릭 지원으로 생각할수있지만, 실은 함수와 함수의 연결과 함수 흐름 제어를 목적으로 한다.
     * 함수A에서 함수B로 흘러갈 때.. 함수A의 결과가 null 이라면 함수B는 호출이 되면 안된다. 이 경우는 함수B가 방어하면 넘어오는 인자가 null 을 체크하는 것으로 쉽게 생각할수 있지만,
     * 생각을 바꿔어보면 애초에 null일 때에 함수B가 호출이 안되게 되면 안될까? 란 생각을 해볼수 있다. 이러한 제어 흐름을 도와주는 것이 모나드이다.
     * Optional.empty() 정적 메소드를 통해 Empty 유형의 Optional 은 추후 함수들이 호출되지 않도록 다른 함수와의 연결을 생략시켜준다.
     * 예를 들어서 Optional.empty 는 map(), filter() 와 같은 고차함수가 실행이 안되도록 한다.
     * 즉 아래와 같은 흐름으로 진행이 가능하다.
     * 정상케이스: fx() -> fx() -> fx() -> result
     * empty일떄: fx() -> skip() -> skip() -> result
     * 이러한 함수 파이프의 연결 흐름에 문제가 생기거나 복잡도가 높아질수 있다.
     * 함수A의 결과 그 자체가 자기 자신이 함수B를 적용하는게 좋을 지 말지를 결정하게 하면 어떨까? 로 이해하면 모나드의 필요성을 이해할수가 있다.
     */
    @Test
    public void example1() {
        List<Integer> result = TestDummyStub.SOME_DATA.stream()
                .map(Optional::ofNullable)
                .map(x -> x.orElse("0"))
                .map(x -> x.replace("NUMBER_", ""))
                .map(Integer::parseInt)
                .map(x -> x + 5)
                .filter(x -> x % 7 == 0)
                .filter(x -> x % 2 == 0)
                .collect(Collectors.toList());
        Assert.assertEquals(TestDummyStub.EXAMPLE_1_STUB, result);
    }

    private final List<Integer> stub = IntStream.rangeClosed(1, 100)
            .boxed()
            .map(x -> x % 2 == 0 ? x : null)
            .collect(Collectors.toList());

    @Test
    public void example2() {

        stub.stream()
                .map(Optional::ofNullable)
                .map(x -> x.map(y -> y * 2))
                .map(x -> x.orElse(0))
                .forEach(x -> System.out.println(x));

    }

    @Test
    public void example3() {
        List<Integer> zxc = stub.stream()
                .map(x -> x == null ? Arrays.asList(0, 0) : Collections.singletonList(x))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        Assert.assertEquals(150, zxc.size());
    }

    @Test
    public void example4() {

        Integer cc = Optional.empty()
                .map(x -> (Integer) x)
                .map(x -> x * 2)
                .orElse(0);

        Assert.assertTrue(cc.equals(0));

        Optional.empty()
                .map(x -> (Integer) x)
                .flatMap(Optional::ofNullable)
                .map(x -> x *  2)
                .orElse(0);

    }
}
