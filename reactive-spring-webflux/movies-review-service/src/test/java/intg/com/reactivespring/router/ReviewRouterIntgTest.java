package com.reactivespring.router;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
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
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.5.5")
public class ReviewRouterIntgTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReviewReactiveRepository reviewReactiveRepository;

    static String REVIEWS_URL = "/v1/reviews";

    @BeforeEach
    void setUp() {

        var reviewsList = List.of(
            new Review(null, 1L, "Awesome Movie", 9.0),
            new Review(null, 1L, "Awesome Movie1", 9.0),
            new Review(null, 2L, "Excellent Movie", 8.0));

        reviewReactiveRepository.saveAll(reviewsList)
            .blockLast();
    }

    @AfterEach
    void tearDown() {
        reviewReactiveRepository.deleteAll().block();
    }

    @Test
    void addReview() {

        var review = new Review(null, 1L, "Awesome Movie", 9.0);

        webTestClient
            .post()
            .uri(REVIEWS_URL)
            .bodyValue(review)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Review.class)
            .consumeWith(entityExchangeResult -> {

                var savedReview = entityExchangeResult.getResponseBody();
                assert savedReview != null;
                assert savedReview.getReviewId() != null;
            });
    }

    @Test
    void getReviews() {

        webTestClient
            .get()
            .uri(REVIEWS_URL)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(Review.class)
            .hasSize(3);
    }

    @Test
    void getReviewsByMovieInfoId() {

        var movieInfoId = 1;

        webTestClient
            .get()
            .uri(REVIEWS_URL + "?movieInfoId=" + movieInfoId)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(Review.class)
            .hasSize(2);
    }

    @Test
    void updateReview() {

        var review = new Review(null, 1L, "Awesome Movie", 9.0);
        Review savedReview = reviewReactiveRepository.save(review).block();

        var updatedReviewRating = 8.0;
        var reviewUpdate = new Review(null, 1L, "Awesome Movie", updatedReviewRating);

        webTestClient
            .put()
            .uri(REVIEWS_URL + "/{id}", savedReview.getReviewId())
            .bodyValue(reviewUpdate)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody(Review.class)
            .consumeWith(entityExchangeResult -> {

                var updatedReview = entityExchangeResult.getResponseBody();
                assert updatedReview != null;
                assert updatedReview.getRating() == updatedReviewRating;
            });
    }


    @Test
    void deleteReview() {

        var reviewId = "abc";
        var review = new Review(reviewId, 1L, "Awesome Movie", 9.0);
        reviewReactiveRepository.save(review).block();

        webTestClient
            .delete()
            .uri(REVIEWS_URL + "/{id}", reviewId)
            .exchange()
            .expectStatus()
            .isNoContent();
    }
}