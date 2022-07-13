package com.learnreactiveprogramming.service;

import java.time.Duration;
import java.util.Random;
import java.util.function.Function;
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

    public Mono<String> namesMono_map_filter(int stringLength) {
        return Mono.just("alex")
            .map(String::toUpperCase)
            .filter(s -> s.length() > stringLength);
    }

    public Mono<List<String>> namesMono_map_flatmap(int stringLength) {
        return Mono.just("alex")
            .map(String::toUpperCase)
            .filter(s -> s.length() > stringLength)
            .flatMap(this::splitStringMono)
            .log();
    }

    public Flux<String> namesMono_map_flatMapMany(int stringLength) {
        return Mono.just("alex")
            .map(String::toUpperCase)
            .filter(s -> s.length() > stringLength)
            .flatMapMany(this::splitString)
            .log();
    }

    private Mono<List<String>> splitStringMono(String s) {

        var charArray = s.split("");
        List<String> charList = List.of(charArray);
        return Mono.just(charList);
    }

    public Flux<String> namesFlux_flatmap(int stringLength) {

        // filter the string whose length is greater than 3
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
            .map(String::toUpperCase)
//            .map(s -> s.toUpperCase())
            .filter(s -> s.length() > stringLength)
            .flatMap(s -> splitString(s))
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

    public Flux<String> splitString(String name) {
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


    public Flux<String> namesFlux_transform(int stringLength) {

        Function<Flux<String>, Flux<String>> filtermap = name -> name.map(String::toUpperCase)
            .filter(s -> s.length() > stringLength);

        // Flux.empty()
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
            .transform(filtermap)
            .flatMap(s -> splitString(s))
            .defaultIfEmpty("default")
            .log(); // db or remote service call
    }

    public Flux<String> namesFlux_transform_switchIfEmpty(int stringLength) {

        Function<Flux<String>, Flux<String>> filtermap = name -> name.map(String::toUpperCase)
            .filter(s -> s.length() > stringLength)
            .flatMap(s -> splitString(s));

        Flux<String> defaultFlux = Flux.just("default")
            .transform(filtermap);

        // Flux.empty()
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
            .transform(filtermap)
            .switchIfEmpty(defaultFlux)
            .log(); // db or remote service call
    }

    public Flux<String> explore_concat() {

        Flux<String> abcFlux = Flux.just("A", "B", "C");
        Flux<String> defFlux = Flux.just("D", "E", "F");

        return Flux.concat(abcFlux, defFlux).log();
    }

    public Flux<String> explore_concatWith() {

        Flux<String> abcFlux = Flux.just("A", "B", "C");
        Flux<String> defFlux = Flux.just("D", "E", "F");

        return abcFlux.concatWith(defFlux).log();
    }

    public Flux<String> explore_concatWith_mono() {

        Mono<String> aMono = Mono.just("A");
        Mono<String> bMono = Mono.just("B");

        return aMono.concatWith(bMono).log();
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
