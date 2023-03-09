package reactor.threadScheduling;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class ThreadSchedulingTest
{
    /**
     * 리액터는 비동기 실행을 강제하지 않는다. 모두 main 스레드에서 실행된다.
     *
     * 실행 결과를 보면 map(), flatMap(), subscribe()에 전달한 코드가 모두 main 쓰레드에서 실행된 것을 알 수 있다.
     * 즉 map 연산, flatMap 연산뿐만 아니라 subscribe를 이용한 구독까지 모두 main 쓰레드가 실행한다.
     * 스케줄러를 사용하면 구독이나 신호 처리를 별도 쓰레드로 실행할 수 있다.
     *
     * 스케쥴러 종류는 다음 공식 문서를 참고한다.
     *  * https://projectreactor.io/docs/core/release/api/
     */
    @Test
    void singleThreadTest()
    {
        Flux.range(1, 3)
            .map(i -> {
                System.out.printf("%s, map %d to %d\n", Thread.currentThread(), i, i + 2);
                return i + 2;
            })
            .flatMap(i -> {
                System.out.printf("%s, flatMap %d to Flux.range(%d, %d)", Thread.currentThread(), i, 1, i);
                return Flux.range(1, i);
            })
            .subscribe(i -> System.out.println(Thread.currentThread() + " next " + i));
    }
}
