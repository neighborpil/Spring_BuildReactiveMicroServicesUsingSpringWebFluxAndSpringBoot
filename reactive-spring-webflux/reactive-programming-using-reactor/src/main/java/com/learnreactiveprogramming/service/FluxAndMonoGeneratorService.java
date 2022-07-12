package com.learnreactiveprogramming.service;

import java.time.Duration;
import java.util.Random;
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

    public Flux<String> namesFlux_flatmap(int stringLength) {

        // filter the string whose length is greater than 3
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
            .map(String::toUpperCase)
//            .map(s -> s.toUpperCase())
            .filter(s -> s.length() > stringLength)
            .flatMap(s -> splitstring(s))
            .log(); // db or remote service call
    }

    public Flux<String> namesFlux_flatmap_async(int stringLength) {

        // filter the string whose length is greater than 3
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
            .map(String::toUpperCase)
//            .map(s -> s.toUpperCase())
            .filter(s -> s.length() > stringLength)
            .flatMap(s -> splitstring_withDelay(s))
            .log(); // db or remote service call
    }

    public Flux<String> splitstring(String name) {
        String[] charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    public Flux<String> splitstring_withDelay(String name) {
        String[] charArray = name.split("");
        int delay = new Random().nextInt(1000);
        return Flux.fromArray(charArray)
            .delayElements(Duration.ofMillis(delay));
    }

    public Flux<String> namesFlux_concatMap(int stringLength) {

        // filter the string whose length is greater than 3
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
            .map(String::toUpperCase)
//            .map(s -> s.toUpperCase())
            .filter(s -> s.length() > stringLength)
            .concatMap(s -> splitstring_withDelay(s))
            .log(); // db or remote service call
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
