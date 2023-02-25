package reactor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class PublisherTest
{
    @Test
    @DisplayName("flux")
    void fluxTest()
    {
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
    void monoTest()
    {
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
    void convertableFluxToMonoTest()
    {
        Flux<String> flux = Flux.just("A");
        Mono<List<String>> mono = flux.collectList();
    }

    @Test
    @DisplayName("Flux와 Mono는 서로 쉽게 변환할 수 있다. mono -> flux")
    void convertableMonoToFluxTest()
    {
        Mono<String> mono = Mono.just("A");
        Flux<String> flux = mono.flux();
    }
}
