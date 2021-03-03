package com.glqdlt.ex.reactive;

import org.junit.Assert;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

/**
 * 스프링 리액터의 특징은 배압(backPresure)를 지원한다. 벡프레셔는 흐름의 강약을 조절한다,
 * 즉 원소 N개의 작업을 해야한다면 특정 구간에서는 N 의 절반에 해당하는 갯수만 처리 가능하다던지의 속도를 조절할수 있다.
 * 백프레셔가 필요한 것은 과도한 리소스의 사용을 조절하고 싶을 때 사용한다.
 * 자바 스트림 API는 PULL 방식이다. 반면 스프링 리액터는 PUSH 방식의 메카니즘을 가지고 있다.
 * PULL 방식으로 접근하면 내가 1000개의 원소만 작업처리하고 싶다면.. 1000 개이상이 넘어왔을 때 에도 계속 데이터는 흘러들어올건데, 이에 대한 버퍼링 관리가 복잡해지거나 불가능할수있다.
 * 반면 PUSH 방식으로 하면 데이터를 보내주는 주체에서 관리가 가능하기 때문에 다른 관점으로 접근이 가능해서 쉬워진다.
 * 이에 대한 설명은 아래에서 찾을수 있다.
 * <a href='https://www.baeldung.com/reactor-core'>https://www.baeldung.com/reactor-core</a>
 *
 * @author jhun
 */
public class SpringReactorTest {

    @Test
    public void example1() {
        List<Integer> target = Flux.fromStream(TestDummyStub.SOME_DATA.stream()
                .map(Mono::justOrEmpty))
                .map(x -> x.defaultIfEmpty("0"))
                .flatMap(Mono::flux)
                .map(x -> x.replace("NUMBER_", ""))
                .map(Integer::parseInt)
                .map(x -> x + 5)
                .filter(x -> x % 2 == 0)
                .filter(x -> x % 7 == 0)
                .collectList().block();
        Assert.assertEquals(TestDummyStub.EXAMPLE_1_STUB, target);
    }

    @Test
    public void example1Another() {
        Mono<List<Integer>> zxc = Flux.fromStream(
                TestDummyStub.SOME_DATA.stream()
                        .map(Optional::ofNullable)
        ).map(x -> x.orElse("0"))
                .map(x -> x.replace("NUMBER_", ""))
                .map(Integer::parseInt)
                .map(x -> x + 5)
                .filter(x -> x % 7 == 0)
                .filter(x -> x % 2 == 0)
                .collectList();


        Assert.assertEquals(TestDummyStub.EXAMPLE_1_STUB, zxc.block());
    }

}