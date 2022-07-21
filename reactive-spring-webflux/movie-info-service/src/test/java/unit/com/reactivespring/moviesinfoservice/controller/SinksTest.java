package com.reactivespring.moviesinfoservice.controller;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.EmitFailureHandler;
import reactor.core.publisher.Sinks.Many;

public class SinksTest {

    @Test
    void sink() {

        Sinks.Many<Integer> replaySink = Sinks.many().replay().all(); // replay: subcriber가 올 때마다 모든 이벤트를 보낸다

        replaySink.emitNext(1, EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(2, EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux = replaySink.asFlux();
        integerFlux.subscribe(i -> {
            System.out.println("Subscriber 1 : " + i);
        });

        Flux<Integer> integerFlux1 = replaySink.asFlux();
        integerFlux1.subscribe(i -> {
            System.out.println("Subscriber 2 : " + i);
        });

        replaySink.tryEmitNext(3);

        Flux<Integer> integerFlux2 = replaySink.asFlux();
        integerFlux2.subscribe(i -> {
            System.out.println("Subscriber 3 : " + i);
        });

    }


    @Test
    void sink_multicast() {

        Sinks.Many<Integer> multicast = Sinks.many().multicast().onBackpressureBuffer(); // 여러 사용자들이 붙을 수 있다. 접속한 이후로만 확인 가능하다

        multicast.emitNext(1, EmitFailureHandler.FAIL_FAST);
        multicast.emitNext(2, EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux = multicast.asFlux();
        integerFlux.subscribe(i -> {
            System.out.println("Subscriber 1 : " + i);
        });

        Flux<Integer> integerFlux1 = multicast.asFlux();
        integerFlux1.subscribe(i -> {
            System.out.println("Subscriber 2 : " + i);
        });

//        multicast.tryEmitNext(3);
        multicast.emitNext(3, EmitFailureHandler.FAIL_FAST);

    }

    @Test
    void sink_unicast() {

        Sinks.Many<Integer> multicast = Sinks.many().unicast().onBackpressureBuffer(); // 하나의 사용자만 붙을 수 있다.

        multicast.emitNext(1, EmitFailureHandler.FAIL_FAST);
        multicast.emitNext(2, EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux = multicast.asFlux();
        integerFlux.subscribe(i -> {
            System.out.println("Subscriber 1 : " + i);
        });

//        Flux<Integer> integerFlux1 = multicast.asFlux();
//        integerFlux1.subscribe(i -> {
//            System.out.println("Subscriber 2 : " + i);
//        });

//        multicast.tryEmitNext(3);
        multicast.emitNext(3, EmitFailureHandler.FAIL_FAST);

    }

}
