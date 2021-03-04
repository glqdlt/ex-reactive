package com.glqdlt.ex.reactive;

import org.junit.Assert;
import org.junit.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
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

    /**
     * 리액터는 핫 시퀀스와 콜드 시퀀스에 따라 개념이 달라진다. 참고로 자바 stream api는 콜드 시퀀스와 개념이 같다.
     * 핫 시퀀스와 콜드 시퀀스의 차이는 Push 방법이냐 pull 방법이냐이다.
     * 핫은 Push 방법이고, 콜드는 Pull 방법이다. 2개의 차이는 아래와 같다.
     * Push 개념은 텔레비전과 같다. TV 전원을 키는 순간 부터 방송되고 있던 부분부터 영상이 출력된다.
     * 반면, Pull 개념은 유튜브 OTT와 같다. 유튜브는 내가 클릭하는 순간 영상을 처음부터 볼수있다.
     * 즉 차이는 Push 개념은 영상 수신자 의 상태와는 상관없이 일방적으로 밀어넣는 즉 PUSH 이고,
     * Pull 은 내가 원할때 받아보는 개념이다.
     * 콜과 핫을 구분하는 가장 기초적인 개념은 connect() 를 지원하냐 차이이다.
     * 아래 예제는 핫 시퀀스에 대한 테스트이다.
     * A 와 B 유저가 있고, A 유저가 먼저 connect()를 호출해서 해당 스트림의 시작을 알린다.
     * 이후 10초 후에 유저 B가 subscribe 하면 10초후의 데이터는 NO-10 이후 부터 수신이 되는 것을 알수가 있다.
     */
    @Test
    public void hotTest() throws InterruptedException {

        ConnectableFlux<String> aaa = Flux.interval(Duration.ofSeconds(1))
                .map(x -> "NO-" + x)
                .publish();
        aaa.subscribe((x) -> System.out.println(String.format("[A] %s", x)));
        aaa.connect();
        Thread.sleep(Duration.ofSeconds(10).toMillis());

        aaa.subscribe((x) -> System.out.println(String.format("[B] %s", x)));

        Thread.sleep(Duration.ofMinutes(1).toMillis());

    }

    /**
     * 반면 콜드 테스트의 경우, 10초후에 B가 구독을 했음에도 NO-0 최초의 데이터부터 가져오는 것을 알수가 있다.
     * Push 방식의 경우 송신자 입장에서 원 데이터는 소비하고 지우면 되나, Pull 방식의 경우에는 송신자 입장에서 데이터를 계속 관리를 해야하는 부담감이 생긴다.
     * 여기서 관리란 원본 데이터를 보관하는 방법도 있지만, 얼마나 많은 인원이 이를 Pull 당겨갈지의 기준처럼.. 무한정으로 보관할지 말지에 대한 관리도 필요하다.
     */
    @Test
    public void coldTest() throws InterruptedException {
        Flux<String> aaa = Flux.interval(Duration.ofSeconds(1))
                .map(x -> "NO-" + x);

        aaa.subscribe((x) -> System.out.println(String.format("[A] %s", x)));
        Thread.sleep(Duration.ofSeconds(10).toMillis());

        aaa.subscribe((x) -> System.out.println(String.format("[B] %s", x)));

        Thread.sleep(Duration.ofMinutes(1).toMillis());

    }
}