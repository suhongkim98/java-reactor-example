package reactor.reactiveoperation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * 스트림의 데이터나 흐름을 변경하지 않고, 중간에 관찰(hook)하거나 부수 동작을 실행하는 오퍼레이터
 * 단순히 흐르는 데이터를 엿보고(side-effect) 무언가 실행할 뿐, upstream/downstream 데이터에는 영향 없음
 *
 * 오퍼레이터	실행 시점
 * doOnNext	데이터 방출
 * doOnSuccess	Mono 성공 완료
 * doOnError	에러 발생
 * doOnComplete	정상 완료
 * doOnTerminate	complete/error 직전
 * doAfterTerminate	complete/error 이후
 * doFinally	모든 종료 신호
 * doOnSubscribe	구독 시
 * doOnRequest	request 발생
 * doOnCancel	취소
 * doOnEach	모든 Signal
 */
public class SideEffectOperatorTest {

    /**
     * doOnNext : 데이터가 성공적으로 '방출'되었을 때 실행됨 (즉 데이터가 empty이면 안되고 존재해야함)
     * doOnSuccess : 앞의 publisher가 성공적으로 '완료'되었을 때 실행됨 (즉 데이터가 empty여도 됨)
     */
    @Test
    @DisplayName("doOnNext는 데이터가 성공적으로 '방출'되었을 때 실행")
    void testDoOnNext() {
        Flux<Object> flux = Flux.empty()
                .doOnNext(i -> System.out.println("보임??"));

        // "보임??" 문구 보이지 않음
        StepVerifier.create(flux)
                .verifyComplete();
    }

    @Test
    @DisplayName("doOnSuccess(Mono 전용)는 앞의 publisher가 성공적으로 '완료'되었을 때 실행")
    void testDoOnSuccess() {
        Mono<Object> flux = Mono.empty()
                .doOnSuccess(i -> System.out.println("보임??"));

        // "보임??" 문구 보인다.
        StepVerifier.create(flux)
                .verifyComplete();
    }

    @Test
    @DisplayName("doOnNext와 doOnSuccess는 에러 상황에서는 실행되지 않는다")
    void testDoOnError() {
        Mono<Object> mono = Mono.just("hello")
                .map(a -> {
                    throw new RuntimeException("Something wrong");
                })
                .doOnNext(i -> System.out.println("On next: " + i))
                .doOnSuccess(i -> System.out.println("On success: " + i))
                .doOnError(i -> System.out.println("On error: " + i));

        // On error 문구만 보임
        StepVerifier.create(mono)
                .expectError(RuntimeException.class)
                .verify();
    }
}
