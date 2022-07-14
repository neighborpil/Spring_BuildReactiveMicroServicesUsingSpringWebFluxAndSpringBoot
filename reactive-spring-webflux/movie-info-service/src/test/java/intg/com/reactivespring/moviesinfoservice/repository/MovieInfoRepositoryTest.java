package com.reactivespring.moviesinfoservice.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@DataMongoTest
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.5.5")
class MovieInfoRepositoryTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @BeforeEach
    void setUp() {
        var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
            new MovieInfo(null, "The Dark Knight",
                2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
            new MovieInfo("abc", "Dark Knight Rises",
                2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        movieInfoRepository.saveAll(movieinfos)
            .blockLast(); // 마지막으로 데이터를 받고 끊어주는 역할. 테스트 케이스에서만 사용해야 한다.
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void findAll() {

        Flux<MovieInfo> moviesInfoFlux = movieInfoRepository.findAll().log();

        StepVerifier.create(moviesInfoFlux)
            .expectNextCount(3)
            .verifyComplete();
    }

    @Test
    void findById() {

        var moviesInfoMono = movieInfoRepository.findById("abc").log();

        StepVerifier.create(moviesInfoMono)
//                .expectNextCount(1)
                .assertNext(movieInfo -> {
                    assertThat(movieInfo.getName()).isEqualTo("Dark Knight Rises");
                })
                .verifyComplete();
    }
}