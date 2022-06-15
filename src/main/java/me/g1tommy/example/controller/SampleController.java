package me.g1tommy.example.controller;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import me.g1tommy.example.domain.User;
import me.g1tommy.example.repository.UserRepository;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
public class SampleController {

    private final UserRepository userRepository;

    // 2. Flux Instances
    public Flux<String> emptyFlux() {
        return Flux.empty();
    }

    public Flux<String> fooBarFluxFromValues() {
        return Flux.just("foo", "bar");
    }

    public Flux<String> fooBarFluxFromList() {
        return Flux.fromIterable(Arrays.asList("foo", "bar"));
    }

    public Flux<String> errorFlux() {
        return Flux.error(new IllegalStateException());
    }

    public Flux<Long> counter() {
        return Flux.interval(Duration.ofMillis(100))
                    .take(10);
    }

    // 3. Mono Instances
    public Mono<String> emptyMono() {
        return Mono.empty();
    }

    public Mono<String> monoWithNoSignal() {
        return Mono.never();
    }

    public Mono<String> fooMono() {
        return Mono.just("foo");
    }

    public Mono<String> errorMono() {
        return Mono.error(new IllegalStateException());
    }

    // 5. Transform
    public Mono<User> capitalizeOne(Mono<User> mono) {
        return mono.map(user -> {
            // 단순 Reactive Stream 코드용으로 Random Long은 replacable함
            return new User(new Random().nextLong(), user.getUsername().toUpperCase(), user.getFirstname().toUpperCase(), user.getLastname().toUpperCase());
        });
    }

    public Flux<User> capitalizeMany() {
        Flux<User> userFlux = Flux.fromIterable(userRepository.findAll());
        return userFlux.map(user -> {
            // 단순 Reactive Stream 코드용으로 Random Long은 replacable함
            return new User(new Random().nextLong(), user.getUsername().toUpperCase(), user.getFirstname().toUpperCase(), user.getLastname().toUpperCase());
        });
    }

    public Flux<User> asyncCapitalizeMany() {
        Flux<User> userFlux = Flux.fromIterable(userRepository.findAll());
        return userFlux.flatMap(this::asyncCapitalizeUser);
    }

    private Mono<User> asyncCapitalizeUser(User user) {
        return Mono.just(new User(user.getId(), user.getUsername(), user.getFirstname(), user.getLastname()));
    }

    // 6. Merge
    public Flux<User> mergeFluxWithNoInterleave() {
        Flux<User> flux1 = Flux.fromIterable(userRepository.findAll());
        Flux<User> flux2 = Flux.fromIterable(userRepository.findAll());
        return Flux.mergeSequential(flux1, flux2);
    }

    public Flux<User> createFluxFromMultipleMono() {
        // 반드시 존재함을 전제로 함
        Mono<User> mono1 = Mono.just(userRepository.findById(1L).get());
        Mono<User> mono2 = Mono.just(userRepository.findById(2L).get());

        return Flux.concat(mono1, mono2);
    }

    // 8. Error
    public Mono<User> betterCallAlessandraForMyriamMono() {
        // 반드시 존재함을 전제로 함
        Mono<User> mono = Mono.just(userRepository.findById(2L).get());
        return mono.onErrorReturn(new User(1L, "janelle.hoeger", "Alessandra", "Heidenreich"))
    }

    public Flux<User> betterCallAlessandraAndMyriamForKurtFlux() {
        // 반드시 존재함을 전제로 함
        Flux<User> userFlux = Flux.fromIterable(userRepository.findAll());
        return userFlux.onErrorResume(e -> Flux.just(
                new User(1L, "janelle.hoeger", "Alessandra", "Heidenreich"),
                new User(2L, "rippin.norbert", "Myriam", "Rowe")
        ));
    }

    public Flux<User> capitalizeMany2() {
        Flux<User> userFlux = Flux.fromIterable(userRepository.findAll());
        return userFlux.map(user -> {
            try {
                return capitalizeUser(user);
            } catch (RuntimeException e) {
                throw Exceptions.propagate(e);
            }
        });
    }

    private User capitalizeUser(User user) {
        if (user.equals(new User(1L, "janelle.hoeger", "Alessandra", "Heidenreich"))) {
            throw new RuntimeException();
        }
        return new User(user.getId(), user.getUsername(), user.getFirstname(), user.getLastname());
    }

    // 9. Adapt
    public Flux<User> fromFlowableToFlux(Flowable<User> flowable) {
        return Flux.from(flowable);
    }

    public Observable<User> fromFluxToObservable(Flux<User> flux) {
        return Observable.fromPublisher(flux);
    }

    public Flux<User> fromObservableToFlux(Observable<User> observable) {
        return Flux.from(observable.toFlowable(BackpressureStrategy.BUFFER));
    }

    public Single<User> fromMonoToSingle(Mono<User> mono) {
        return Single.fromPublisher(mono);
    }

    public Mono<User> fromSingleToMono(Single<User> single) {
        return Mono.from(single.toFlowable());
    }

    public CompletableFuture<User> fromMonoToCompletableFuture(Mono<User> mono) {
        return mono.toFuture();
    }

    public Mono<User> fromCompletableFutureToMono(CompletableFuture<User> completableFuture) {
        return Mono.fromFuture(completableFuture);
    }

    // 10. Other Operations
    public Flux<User> userFluxFromStringFlux(Flux<String> usernameFlux, Flux<String> firstnameFlux, Flux<String> lastnameFlux) {
        return Flux.zip(usernameFlux, firstnameFlux, lastnameFlux)
                .flatMap(user -> Flux.just(new User(new Random().nextLong(), user.getT1(), user.getT2(), user.getT3())));
    }

    public Mono<User> useFastestMono(Mono<User> mono1, Mono<User> mono2) {
        return Mono.firstWithSignal(mono1, mono2);
        // Mono.first is deprecated
    }

    public Flux<User> useFastestFlux(Flux<User> flux1, Flux<User> flux2) {
        return Flux.firstWithSignal(flux1, flux2);
    }

    public Mono<Void> fluxCompletion(Flux<User> flux) {
        return flux.ignoreElements().then();
    }

    public Mono<User> nullAwareUserToMono(User user) {
        return Mono.justOrEmpty(user);
    }

    public Mono<User> emptyToAlessandra(Mono<User> mono) {
        return mono.switchIfEmpty(Mono.just(userRepository.findById(1L).get()));
    }

    public Mono<List<User>> fluxCollection(Flux<User> flux) {
        return flux.collectList();
    }

    // 11. Reactive to Blocking
    public User monoToValue(Mono<User> mono) {
        return mono.block();
    }

    public Iterable<User> fluxToValues(Flux<User> flux) {
        return flux.toIterable();
    }

    // 12. Blocking to Reactive
    public Flux<User> blockingRepositoryToFlux() {
        return Flux.defer(() -> Flux.fromIterable(userRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> fluxToBlockingRepository(Flux<User> flux) {
        return flux.publishOn(Schedulers.boundedElastic())
                .doOnNext(userRepository::save)
                .then();
    }
}
