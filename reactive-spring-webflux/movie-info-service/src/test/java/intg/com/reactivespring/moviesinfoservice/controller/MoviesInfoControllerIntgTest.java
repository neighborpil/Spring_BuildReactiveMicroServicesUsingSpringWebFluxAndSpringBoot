package com.reactivespring.moviesinfoservice.controller;

import com.reactivespring.moviesinfoservice.domain.MovieInfo;
import com.reactivespring.moviesinfoservice.repository.MovieInfoRepository;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.5.5")
@ActiveProfiles("test")
class MoviesInfoControllerIntgTest {


    @Autowired
    MovieInfoRepository movieInfoRepository;

    @Autowired
    WebTestClient webTestClient;

    static String MOVIES_INFO_URL = "/v1/movieinfos";

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
    void addMovieInfo() {

        MovieInfo movieInfo = new MovieInfo(null, "Batman Begins1",
            2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        webTestClient.post()
            .uri(MOVIES_INFO_URL)
            .bodyValue(movieInfo)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(MovieInfo.class)
            .consumeWith(movieInfoEntityExchangeResult -> {

                MovieInfo savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                assert savedMovieInfo.getMovieInfoId() != null;
            });
    }

    @Test
    void getAllMovieInfos() {

        webTestClient
            .get()
            .uri(MOVIES_INFO_URL)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(MovieInfo.class)
            .hasSize(3);
    }

    @Test
    void getMovieInfoByYear() {

        URI uri = UriComponentsBuilder.fromUriString(MOVIES_INFO_URL)
            .queryParam("year", 2005)
            .buildAndExpand().toUri();

        webTestClient
            .get()
            .uri(uri)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(MovieInfo.class)
            .hasSize(1);
    }

    @Test
    void getMovieInfoById() {

        var movieInfoId = "abc";

        webTestClient
            .get()
            .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody(MovieInfo.class)
            .consumeWith(movieInfoEntityExchangeResult -> {
                MovieInfo movieInfo = movieInfoEntityExchangeResult.getResponseBody();
                assertThat(movieInfo.getMovieInfoId()).isNotNull();
            });
    }

    @Test
    void getMovieInfoById_notFound() {

        var movieInfoId = "def";

        webTestClient
            .get()
            .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void getMovieInfoById_1() {

        var movieInfoId = "abc";

        webTestClient
            .get()
            .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody()
            .jsonPath("$.name").isEqualTo("Dark Knight Rises");

    }

    @Test
    void updateMovieInfo() {
        var moveInfoId = "abc";
        MovieInfo movieInfo = new MovieInfo(null, "Dark Knight Rises1",
            2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        webTestClient
            .put()
            .uri(MOVIES_INFO_URL + "/{id}", moveInfoId)
            .bodyValue(movieInfo)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody(MovieInfo.class)
            .consumeWith(movieInfoEntityExchangeResult -> {

                MovieInfo updatedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                assert updatedMovieInfo != null;
                assert updatedMovieInfo.getMovieInfoId() != null;
                assertEquals("Dark Knight Rises1", updatedMovieInfo.getName());
            });
    }

    @Test
    void updateMovieInfo_notfound() {
        var moveInfoId = "def";
        MovieInfo movieInfo = new MovieInfo(null, "Dark Knight Rises1",
            2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        webTestClient
            .put()
            .uri(MOVIES_INFO_URL + "/{id}", moveInfoId)
            .bodyValue(movieInfo)
            .exchange()
            .expectStatus()
            .isNotFound();

    }

    @Test
    void deleteMovieInfo() {
        var moveInfoId = "abc";

        webTestClient
            .delete()
            .uri(MOVIES_INFO_URL + "/{id}", moveInfoId)
            .exchange()
            .expectStatus()
            .isNoContent();
    }
}