package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
            .log(); // db or remote service call
    }

    public Mono<String> nameMono() {

        return Mono.just("alex").log();
    }

    public Flux<String> namesFlux_map(int stringLength) {

        // filter the string whose length is greater than 3
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
            .map(String::toUpperCase)
//            .map(s -> s.toUpperCase())
            .filter(s -> s.length() > stringLength)
            .map(s -> s.length() + "-" + s)
            .log(); // db or remote service call
    }

    public Flux<String> namesFlux_immutability() {

        var naemsFlux = Flux.fromIterable(List.of("alex", "ben", "chloe"));
        naemsFlux.map(String::toUpperCase); // flux is immutable
        return naemsFlux;

    }

    public static void main(String[] args) {

        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

        fluxAndMonoGeneratorService.namesFlux()
                .subscribe(name -> {
                    System.out.println("Name is : " + name);
                });

        fluxAndMonoGeneratorService.nameMono()
                .subscribe(name -> {
                    System.out.println("Name is : " + name);
                });

    }
}
