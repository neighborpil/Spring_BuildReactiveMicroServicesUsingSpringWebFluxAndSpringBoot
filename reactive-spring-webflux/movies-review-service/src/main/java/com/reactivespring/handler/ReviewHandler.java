package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.exception.ReviewNotFoundException;
import com.reactivespring.repository.ReviewReactiveRepository;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Component
@Slf4j
public class ReviewHandler {

    Sinks.Many<Review> reviewSink = Sinks.many().replay().latest();
    @Autowired
    private Validator validator;
    private ReviewReactiveRepository reviewReactiveRepository;

    public ReviewHandler(ReviewReactiveRepository reviewReactiveRepository) {
        this.reviewReactiveRepository = reviewReactiveRepository;
    }

    public Mono<ServerResponse> addReview(ServerRequest request) {

        return request.bodyToMono(Review.class)
            .doOnNext(this::validate)
                .flatMap(reviewReactiveRepository::save)
                .doOnNext(review -> {
                    reviewSink.tryEmitNext(review);
                })
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }

    private void validate(Review review) {

        Set<ConstraintViolation<Review>> constraintViolations = validator.validate(review);
        log.info("constraintViolations: {}", constraintViolations);
        if (constraintViolations.size() > 0) {
            String errorMessage = constraintViolations
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(","));

            throw new ReviewDataException(errorMessage);
        }


    }

    public Mono<ServerResponse> getReviews(ServerRequest request) {

        var movieInfoId = request.queryParam("movieInfoId");
        if (movieInfoId.isPresent()) {
            Flux<Review> reviewsFlux = reviewReactiveRepository.findReviewsByMovieInfoId(Long.valueOf(movieInfoId.get()));
            return buildReviewsResponse(reviewsFlux);
        } else {
            Flux<Review> reviewsFlux = reviewReactiveRepository.findAll();
            return buildReviewsResponse(reviewsFlux);
        }

    }

    private Mono<ServerResponse> buildReviewsResponse(Flux<Review> reviewsFlux) {
        return ServerResponse.ok().body(reviewsFlux, Review.class);
    }

    public Mono<ServerResponse> updateReview(ServerRequest request) {

        String reviewId = request.pathVariable("id");
        Mono<Review> existingReview = reviewReactiveRepository.findById(reviewId);
//            .switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found for the given Review ID " + reviewId)));


        return existingReview
            .flatMap(review -> request.bodyToMono(Review.class) // convert jsonBody to Review class
                .map(reqReview -> {
                    review.setComment(reqReview.getComment());
                    review.setRating(reqReview.getRating());
                    return review;
                })
                .flatMap(reviewReactiveRepository::save) // convert to Mono<Review>
                .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview)) // convert from Mono<Review> to Mono<ServerResponse>
            )
            .switchIfEmpty(ServerResponse.notFound().build());
    }


//    public Mono<ServerResponse> updateReview(ServerRequest request) {
//
//        String reviewId = request.pathVariable("id");
//        Mono<Review> existingReview = reviewReactiveRepository.findById(reviewId)
//            .switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found for the given Review ID " + reviewId)));
//
//
//        return existingReview
//            .flatMap(review -> request.bodyToMono(Review.class) // convert jsonBody to Review class
//                .map(reqReview -> {
//                    review.setComment(reqReview.getComment());
//                    review.setRating(reqReview.getRating());
//                    return review;
//                })
//                .flatMap(reviewReactiveRepository::save) // convert to Mono<Review>
//                .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview)) // convert from Mono<Review> to Mono<ServerResponse>
//            );
//    }

    public Mono<ServerResponse> deleteReview(ServerRequest request) {

        String reviewId = request.pathVariable("id");
        Mono<Review> existingReview = reviewReactiveRepository.findById(reviewId);

        return existingReview.flatMap(review -> reviewReactiveRepository.deleteById(reviewId))
            .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> getReivewsStream(ServerRequest request) {

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(reviewSink.asFlux(), Review.class)
                .log();
    }
}
