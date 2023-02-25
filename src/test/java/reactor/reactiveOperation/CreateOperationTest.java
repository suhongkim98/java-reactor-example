package reactor.reactiveOperation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 생성 오퍼레이션
 * 리액티브 스트림 시퀀스 생성하기
 * Flux와 Mono는 많은 펙토리 메서드를 제공한다.
 */
public class CreateOperationTest
{
    @Test
    @DisplayName("create flux")
    void createFlux()
    {
        /**
         * Flux.just("A", "B")
         * Flux.fromArray(new Integer[] { 2, 4, 8 })
         * Flux.fromIterable(Arrays.asList(3, 6, 9))
         * Flux.range(1, 5);
         * Flux.from(publisher) : 다른 Publisher를 Flux로 변환
         * Flux.empty()
         * Flux.never() : onComplete, onError 신호까지 보내지 않음
         * Flux.error(throwable) : 바로 오류를 전파하는 시퀀스를 생성
         * Flux.defer(supplier) : 구독되는 순간에 supplier를 실행하여 시퀀스를 생성
         */
    }

    @Test
    @DisplayName("create mono")
    void createMono()
    {
        /**
         * Mono.just("devljh")
         * Mono.justOrEmpty(null) : nullable, Optional.empty 모두 가능
         * Mono.fromCallable(this::httpRequest)
         * Mono.fromRunnable(runnable)
         * Mono.fromSupplier(supplier)
         * Mono.fromFuture(future)
         * Mono.fromCompletionStage(completionStage)
         * Mono.from(publisher) : 다른 Publisher를 Mono로 변환
         * Mono.empty()
         * Mono.never() : onComplete, onError 신호까지 보내지 않음
         * Mono.error(throwable) : 바로 오류를 전파하는 시퀀스를 생성
         * Mono.defer(supplier) : 구독되는 순간에 supplier를 실행하여 시퀀스를 생성
         */
    }

    @Test
    @DisplayName("just")
    void just()
    {
        // just는 즉시 시퀀스를 만든다.
        Mono<String> mono = Mono.just("A");
        Flux<String> flux = Flux.just("Apple", "Orange", "Grape", "Banana", "Strawberry");
    }

    @Test
    @DisplayName("배열로부터 시퀀스 만들기")
    void createFlux_fromArray()
    {
        String[] fruits = new String[]
            {
                "Apple", "Orange", "Grape", "Banana", "Strawberry"
            };
        Flux<String> fruitFlux = Flux.fromArray(fruits);


        StepVerifier.create(fruitFlux)
            .expectNext("Apple")
            .expectNext("Orange")
            .expectNext("Grape")
            .expectNext("Banana")
            .expectNext("Strawberry")
            .verifyComplete();
    }

    @Test
    @DisplayName("컬렉션으로부터 시퀀스 만들기(List, Set, Iterable)")
    void createFlux_fromIterable()
    {
        List<String> fruitList = new ArrayList<>();
        fruitList.add("Apple");
        fruitList.add("Orange");
        fruitList.add("Grape");
        fruitList.add("Banana");
        fruitList.add("Strawberry");


        Flux<String> fruitFlux = Flux.fromIterable(fruitList);


        StepVerifier.create(fruitFlux)
            .expectNext("Apple")
            .expectNext("Orange")
            .expectNext("Grape")
            .expectNext("Banana")
            .expectNext("Strawberry")
            .verifyComplete();
    }

    @Test
    @DisplayName("스트림으로부터 시퀀스 만들기")
    void createFlux_fromStream()
    {
        Stream<String> fruitStream =
            Stream.of("Apple", "Orange", "Grape", "Banana", "Strawberry");


        Flux<String> fruitFlux = Flux.fromStream(fruitStream);


        StepVerifier.create(fruitFlux)
            .expectNext("Apple")
            .expectNext("Orange")
            .expectNext("Grape")
            .expectNext("Banana")
            .expectNext("Strawberry")
            .verifyComplete();
    }

    @Test
    @DisplayName("range로 시퀀스 만들기")
    void createFlux_range()
    {
        Flux<Integer> intervalFlux = Flux.range(1, 5);


        StepVerifier.create(intervalFlux)
            .expectNext(1)
            .expectNext(2)
            .expectNext(3)
            .expectNext(4)
            .expectNext(5)
            .verifyComplete();
    }

    @Test
    @DisplayName("interval로 생성예제")
    void createFlux_interval()
    {
        Flux<Long> intervalFlux = Flux.interval(Duration.ofSeconds(1)).take(5); // take(5) : 최대 5개 항목으로 결과를 제한

        StepVerifier.create(intervalFlux)
            .expectNext(0L)
            .expectNext(1L)
            .expectNext(2L)
            .expectNext(3L)
            .expectNext(4L)
            .verifyComplete();
    }

    @Test
    @DisplayName("fromSupplier, defer")
    void fromSupplier() throws Exception
    {
        /**
         * fromSupplier과 defer는 구독시점에 시퀀스를 생성한다. (lazy 처리)
         */
        long start = System.currentTimeMillis();

        // just
        Mono<Long> clock1 = Mono.just(System.currentTimeMillis());

        Thread.sleep(5000);
        long result1 = clock1.block() - start;
        System.out.println("흐른 시간 = " + result1); // 5초가 지났으나, 위에서 즉발실행되어서 0 출력


        // fromSupplier
        Mono<Long> clock2 = Mono.fromSupplier(System::currentTimeMillis);

        Thread.sleep(5000);
        long result2 = clock2.block() - start;
        System.out.println("흐른 시간 = " + result2); // 10초 지남


        // defer
        Mono<Long> clock3 = Mono.defer(() -> Mono.just(System.currentTimeMillis()));

        Thread.sleep(5000);
        long result3 = clock3.block() - start;
        System.out.println("흐른 시간 = " + result3); // 15초 지남
    }
}
