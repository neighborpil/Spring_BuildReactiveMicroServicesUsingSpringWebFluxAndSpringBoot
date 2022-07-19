package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.exceptionHandler.GlobalErrorHandler;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewReactiveRepository;
import com.reactivespring.router.ReviewRouter;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class, GlobalErrorHandler.class})
@AutoConfigureWebTestClient
public class ReviewsUnitTest {

    @MockBean
    private ReviewReactiveRepository reviewReactiveRepository;

    @Autowired
    private WebTestClient webTestClient;

    static String REVIEWS_URL = "/v1/reviews";

    @Test
    void addReview() {
        var review = new Review(null, 1L, "Awesome Movie", 9.0);

        when(reviewReactiveRepository.save(isA(Review.class)))
            .thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

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
    void addReview_validation() {
        var review = new Review(null, null, "Awesome Movie", -9.0);

        when(reviewReactiveRepository.save(isA(Review.class)))
            .thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

        webTestClient
            .post()
            .uri(REVIEWS_URL)
            .bodyValue(review)
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody(String.class)
            .isEqualTo("rating.negative : please pass a non-negative value,rating.movieInfoId : must not be null");
    }

    @Test
    void getReviews() {

        var reviews = List.of(
            new Review(null, 1L, "Awesome Movie", 9.0),
            new Review(null, 1L, "Awesome Movie1", 9.0),
            new Review(null, 2L, "Excellent Movie", 8.0));

        when(reviewReactiveRepository.findAll())
            .thenReturn(Flux.fromIterable(reviews));

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

        var reviews =List.of(
            new Review(null, 1L, "Awesome Movie", 9.0),
            new Review(null, 1L, "Awesome Movie1", 9.0));

        when(reviewReactiveRepository.findReviewsByMovieInfoId(isA(Long.class)))
            .thenReturn(Flux.fromIterable(reviews));


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

        var reviewId = "abc";
        var review = new Review("abc", 1L, "Awesome Movie", 9.0);
        var updatedReviewRating = 8.0;
        var reviewUpdate = new Review(null, 1L, "Awesome Movie", updatedReviewRating);

        when(reviewReactiveRepository.save(isA(Review.class)))
            .thenReturn(Mono.just(reviewUpdate));

        when(reviewReactiveRepository.findById(reviewId))
            .thenReturn(Mono.just(review));

        webTestClient
            .put()
            .uri(REVIEWS_URL + "/{id}", reviewId)
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
        var review = new Review("abc", 1L, "Awesome Movie", 9.0);
        when(reviewReactiveRepository.findById(reviewId))
            .thenReturn(Mono.just(review));

        when(reviewReactiveRepository.deleteById(isA(String.class)))
            .thenReturn(Mono.empty());

        webTestClient
            .delete()
            .uri(REVIEWS_URL + "/{id}", reviewId)
            .exchange()
            .expectStatus()
            .isNoContent();
    }
}
