package reactor.reactiveoperation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * 스트림에서 onError 시그널이 발생했을 때
 * 🔹 에러를 다른 값으로 대체하거나
 * 🔹 다른 Publisher로 전환하거나
 * 🔹 재시도하거나
 * 🔹 에러를 변환하는
 * 에러 대응용 오퍼레이터, onErrorXX 시리즈임
 */
public class ErrorHandlingOperatorTest {

    @Test
    @DisplayName("에러 발생 시 고정값으로 대체하고 종료")
    void should_return_fallback_value_when_error_occurs() {

        Mono<String> mono = Mono.<String>error(new RuntimeException("boom"))
                .onErrorReturn("fallback");

        StepVerifier.create(mono)
                .expectNext("fallback")
                .verifyComplete();   // 에러 대신 정상 종료
    }

    @Test
    @DisplayName("에러 발생 시 동적으로 다른 Mono/Flux 실행")
    void should_switch_to_other_publisher_when_error_occurs() {

        Mono<String> mono =
                Mono.<String>error(new RuntimeException("boom"))
                        .onErrorResume(e -> Mono.just("recovered"));

        StepVerifier.create(mono)
                .expectNext("recovered")
                .verifyComplete();
    }

    @Test
    @DisplayName("에러가 발생한 요소만 스킵하고 계속 진행")
    void should_skip_failing_element_and_continue() {

        Flux<Integer> flux = Flux.just(1, 2, 0, 3)
                .map(i -> 10 / i)   // 0에서 ArithmeticException
                .onErrorContinue((e, value) -> {
                    System.out.println("error on value: " + value);
                });

        StepVerifier.create(flux)
                .expectNext(10)  // 10/1
                .expectNext(5)   // 10/2
                // 0은 스킵
                .expectNext(3)   // 10/3
                .verifyComplete();
    }
}
