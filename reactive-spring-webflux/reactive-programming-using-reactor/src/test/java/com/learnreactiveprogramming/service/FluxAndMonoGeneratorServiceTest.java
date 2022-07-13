package com.learnreactiveprogramming.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {

        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux();

        StepVerifier.create(namesFlux)
//            .expectNext("alex", "ben", "chloe")
//            .expectNextCount(3)
            .expectNext("alex")
            .expectNextCount(2)
            .verifyComplete();

    }

    @Test
    void namesFlux_map() {

        int stringLength = 3;

        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_map(stringLength);

        StepVerifier.create(namesFlux)
            .expectNext("4-ALEX", "5-CHLOE")
            .verifyComplete();

    }

    @Test
    void namesFlux_immutability() {

        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_immutability();

        StepVerifier.create(namesFlux)
            .expectNext("alex", "ben", "chloe")
            .verifyComplete();

    }

    @Test
    void namesFlux_flatmap() {

        int stringLength = 3;
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap(stringLength);

        StepVerifier.create(namesFlux)
            .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
            .verifyComplete();

    }

    @Test
    void namesFlux_flatmap_async() {
        int stringLength = 3;

        var namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap_async(stringLength);

        StepVerifier.create(namesFlux)
//            .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
            .expectNextCount(9)
            .verifyComplete();
    }

    @Test
    void namesFlux_concatMap() {

        int stringLength = 3;

        var namesFlux = fluxAndMonoGeneratorService.namesFlux_concatMap(stringLength);

        StepVerifier.create(namesFlux)
            .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
//            .expectNextCount(9)
            .verifyComplete();
    }

    @Test
    void namesMono_map_flatmap() {

        int stringLength = 3;

        Mono<List<String>> value = fluxAndMonoGeneratorService.namesMono_map_flatmap(stringLength);

        StepVerifier.create(value)
            .expectNext(List.of("A", "L", "E", "X"))
            .verifyComplete();
    }

    @Test
    void namesMono_map_flatMapMany() {

        int stringLength = 3;

        Flux<String> value = fluxAndMonoGeneratorService.namesMono_map_flatMapMany(stringLength);

        StepVerifier.create(value)
            .expectNext("A", "L", "E", "X")
            .verifyComplete();
    }

    @Test
    void namesFlux_transform() {
        int stringLength = 3;
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_transform(stringLength);

        StepVerifier.create(namesFlux)
            .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
            .verifyComplete();
    }

    @Test
    void namesFlux_transform_1() {
        int stringLength = 6;
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_transform(stringLength);

        StepVerifier.create(namesFlux)
//            .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
            .expectNext("default")
            .verifyComplete();
    }

    @Test
    void namesFlux_transform_switchIfEmpty() {
        int stringLength = 6;
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_transform_switchIfEmpty(stringLength);

        StepVerifier.create(namesFlux)
//            .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
            .expectNext("D", "E", "F", "A", "U", "L", "T")
            .verifyComplete();
    }

    @Test
    void explore_concat() {

        var concatFlux = fluxAndMonoGeneratorService.explore_concat();

        StepVerifier.create(concatFlux)
            .expectNext("A", "B", "C", "D", "E", "F")
            .verifyComplete();
    }

    @Test
    void explore_concatWith() {
        var concatFlux = fluxAndMonoGeneratorService.explore_concatWith();

        StepVerifier.create(concatFlux)
            .expectNext("A", "B", "C", "D", "E", "F")
            .verifyComplete();
    }

    @Test
    void explore_concatWith_mono() {
        var concatFlux = fluxAndMonoGeneratorService.explore_concatWith_mono();

        StepVerifier.create(concatFlux)
            .expectNext("A", "B")
            .verifyComplete();
    }
}