package com.learnreactiveprogramming.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
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
}