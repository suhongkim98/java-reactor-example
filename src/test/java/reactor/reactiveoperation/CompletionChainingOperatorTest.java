package reactor.reactiveoperation;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * 값(value)이 아니라 “완료 신호(onComplete)”를 기준으로 다음 시퀀스를 이어붙이는 오퍼레이터
 * upstream의 데이터를 쓰지 않거나, 데이터를 버리고, “완료되면 다음 걸 실행” 하는 계열
 * then, thenReturn, thenEmpty, thenMany, ignoreElements
 */
public class CompletionChainingOperatorTest {

    // "hello"는 downstream으로 전달되지 않음
    @Test
    void then_should_discard_value_and_complete() {
        Mono<Void> mono = Mono.just("hello")
                .then();

        StepVerifier.create(mono)
                .verifyComplete();   // 값 없이 완료
    }

    /**
     * ✔ "first"는 버림
     * ✔ "second"만 전달됨
     */
    @Test
    void then_should_switch_to_next_mono_after_completion() {

        Mono<String> result = Mono.just("first")
                .then(Mono.just("second"));

        StepVerifier.create(result)
                .expectNext("second")
                .verifyComplete();
    }

    // 업스트림 에러 발생 시 then 실행 안됨
    @Test
    void then_should_not_run_when_upstream_errors() {

        Mono<String> result =
                Mono.<String>error(new RuntimeException("boom"))
                        .then(Mono.just("next"));

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }
}
