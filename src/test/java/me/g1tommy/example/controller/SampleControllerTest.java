package me.g1tommy.example.controller;

import lombok.RequiredArgsConstructor;
import me.g1tommy.example.domain.User;
import me.g1tommy.example.repository.UserRepository;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor
class SampleControllerTest {

    private final UserRepository userRepository;

    // 4. StepVerifier
    void expectFooBarComplete() {
        Flux<String> flux = Flux.just("foo", "bar");
        StepVerifier.create(flux)
                .expectNext("foo")
                .expectNext("bar")
                .verifyComplete();
    }

    void expectFooBarError() {
        Flux<String> flux = Flux.just("foo", "bar");
        StepVerifier.create(flux)
                .expectNext("foo")
                .expectNext("bar")
                .expectError(RuntimeException.class);
    }

    void expectAlessandraMyriamComplete() {
        Flux<User> userFlux = Flux.fromIterable(userRepository.findAll());
        StepVerifier.create(userFlux)
                .assertNext(user -> {
                    assertThat(user.getUsername()).isEqualTo("janelle.hoeger");
                })
                .assertNext(user -> {
                    assertThat(user.getUsername()).isEqualTo("rippin.norbert");
                })
                .verifyComplete();
    }

    void expect100Elements() {
        Flux<User> userFlux = Flux.fromIterable(userRepository.findAll());
        StepVerifier.create(userFlux)
                .expectNextCount(100)
                .verifyComplete();
    }

    void expect100Elements2() {
        StepVerifier.withVirtualTime(() -> Flux.interval(Duration.ofSeconds(1)).take(1))
                .thenAwait(Duration.ofSeconds(100))
                .expectNextCount(100)
                .verifyComplete();
    }

    // 7. Request
    StepVerifier requestAllExpectHundred() {
        Flux<User> userFlux = Flux.fromIterable(userRepository.findAll());
        return StepVerifier.create(userFlux)
                .expectNextCount(100)
                .expectComplete();
    }

    StepVerifier requestOneExpectAlessandraThenRequestOneExpectMyriam() {
        Flux<User> userFlux = Flux.fromIterable(userRepository.findAll());
        return StepVerifier.create(userFlux, 1)
                .expectNext(new User(1L, "janelle.hoeger", "Alessandra", "Heidenreich"))
                .thenRequest(1)
                .expectNext(new User(2L, "rippin.norbert", "Myriam", "Rowe"))
                .thenCancel();
    }

    Flux<User> fluxWithLog() {
        return Flux.fromIterable(userRepository.findAll())
                .log();
    }

    Flux<User> fluxWithDoOnPrintln() {
        return Flux.fromIterable(userRepository.findAll())
                .doOnSubscribe(subscription -> System.out.println("Starring:"))
                .doOnNext(user -> System.out.println(user.getFirstname() + " " + user.getLastname()))
                .doOnComplete(() -> System.out.println("The end!"));
    }
}