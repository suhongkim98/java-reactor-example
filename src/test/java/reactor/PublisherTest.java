package reactor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Date;
import java.util.List;

public class PublisherTest {
    @Test
    @DisplayName("flux")
    void fluxTest() {
        /**
         * Flux는 여러 요소(0 ~ N)를 생성할 수 있는 리액티브 스트림이다.
         * 메모리 부족을 야기하지 않고도 무한대의 리액티브 스트림을 만들 수 있다.
         * publisher 는 구독자가 없으면 가만히 있기 때문
         * subscribe를 통해 구독자를 추가하면 데이터가 전달된다.
         */

        Flux<Integer> flux = Flux.range(0, 5).repeat();

        // 다만, 이것을 수집하려고 시도하면 OOM이 날 것이다.
        // List<Integer> list = flux.collectList().block();
    }

    @Test
    @DisplayName("mono")
    void monoTest() {
        /**
         * Mono는 최대 하나의 요소(0 ~ 1)를 생성할 수 있는 리액티브 스트림이다.
         *
         * 아니 다 Flux로 퉁치면 되지 않나??
         * Mono는 0 아니면 1개의 데이터의 흐름만 관리하기에 Mono의 연산자들은 버퍼 중복, 값비싼 동기화 작업 등이 생략되어 있다.
         */
        Mono<Integer> mono = Mono.just(1);
    }

    @Test
    @DisplayName("Flux와 Mono는 서로 쉽게 변환할 수 있다. flux -> mono")
    void convertableFluxToMonoTest() {
        Flux<String> flux = Flux.just("A");
        Mono<List<String>> mono = flux.collectList();
    }

    @Test
    @DisplayName("Flux와 Mono는 서로 쉽게 변환할 수 있다. mono -> flux")
    void convertableMonoToFluxTest() {
        Mono<String> mono = Mono.just("A");
        Flux<String> flux = mono.flux();
    }

    @Test
    @DisplayName("share() Operator를 이용해 Cold Sequence를 Hot Sequence로 동작하게 할 수 있다")
    void testShareHotSequence() throws InterruptedException {
        String[] singers = {"로이킴", "뉴진스", "트와이스", "테일러 스위프트"};

        // 4명의 가수는 2초 간격으로 무대에 나와 노래를 부를 예정입니다.
        Flux<String> concertFlux = Flux.fromArray(singers)
                .delayElements(Duration.ofSeconds(2)) // 디폴트 스케쥴러는 parallel
                .share();

        // 첫 관객 관람(구독)을 하여 공연이 시작됐습니다.
        concertFlux.subscribe(singer -> System.out.printf("[%s] 관객 A가 %s의 무대를 보았습니다.%n", Thread.currentThread(), singer));

        Thread.sleep(2500); // 공연 시작 2.5초 뒤.. 관객 B가 관람(구독) 시작
        concertFlux.subscribe(singer -> System.out.printf("[%s] 관객 B가 %s의 무대를 보았습니다.%n", Thread.currentThread(), singer));

        Thread.sleep(6000);
    }

    @Test
    @DisplayName("cache() 오퍼레이터를 통해 Cold Sequence로 동작하는 Mono를 Hot Sequence로 변경해주고, emit된 데이터를 캐시한 뒤 구독이 발생할 때마다 캐시된 데이터를 전달할 수 있다")
    void testCacheHotSequence() throws InterruptedException {
        var mono = Mono.fromCallable(() -> {
                    System.out.println("Go!");
                    return 5;
                })
                .map(i -> {
                    System.out.println("Double!");
                    return i * 2;
                });

        var cached = mono.cache();

        System.out.println("Using cached"); // Hot Sequence로 동작한다.
        System.out.println("1. " + cached.block()); // 최초 한 번 연산
        System.out.println("2. " + cached.block()); // 캐싱 반환
        System.out.println("3. " + cached.block()); // 캐싱 반환

        System.out.println("Using NOT cached"); // Cold Sequence로 동작한다.
        System.out.println("1. " + mono.block()); // 처음부터 다시 동작
        System.out.println("2. " + mono.block()); // 처음부터 다시 동작
        System.out.println("3. " + mono.block()); // 처음부터 다시 동작
    }
}
