package reactor.threadscheduling;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CountDownLatch;

/**
 * subscribeOn을 이용한 구독 처리 쓰레드 스케줄링, 작업 자체가 어느 Scheduler에서 시작될지 결정한다.
 * subscribeOn()을 사용하면 Subscriber가 시퀀스에 대한 request신호를 별도 스케줄러로 처리한다.(시퀀스를 실행할 스케줄러를 지정한다)
 */
public class SubscribeOnTest {
    /**
     * subscribeOn()으로 지정한 스케줄러는 시퀀스의 request 요청 처리뿐만 아니라 첫 번째 publishOn() 지정 이전까지의 신호 처리를 실행한다.
     * 따라서 위 코드를 실행하면 Flux.range()가 생성한 시퀀스의 신호 발생뿐만 아니라 map() 실행, Subscriber의 next, complete 신호 처리를 "SUB" 스케줄러가 실행한다.
     * 참고로 시퀀스의 request 요청과 관련된 로그를 보기 위해 log() 메서드를 사용했다.
     */
    @Test
    void subscribeOnTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Flux.range(1, 6)
                .log() // 보다 상세한 로그 출력 위함
                .subscribeOn(Schedulers.boundedElastic())
                .map(i -> {
                    System.out.printf("%s, map 1: %d to %d\n", Thread.currentThread(), i, i + 10);
                    return i + 10;
                })
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        System.out.println(Thread.currentThread() + " hookOnSubscribe"); // main thread
                        request(1);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        System.out.println(Thread.currentThread() + " hookOnNext: " + value); // SUB 쓰레드
                        request(1);
                    }

                    @Override
                    protected void hookOnComplete() {
                        System.out.println(Thread.currentThread() + " hookOnComplete"); // SUB 쓰레드
                        latch.countDown();
                    }
                });

        latch.await();
    }

    /**
     * 동기식 코드를 subscribeOn 활용해 비동기적으로 처리하는 예제
     * Mono.fromCallable() + subscribeOn()을 사용해 블로킹 동기 코드를 별도 스레드에서 실행한다.
     * subscribeOn 영역을 제거하면 블로킹 코드가 main 스레드에서 실행되는거 확인 가능
     */
    @Test
    void asyncTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);

        Mono.fromCallable(() -> {
                    System.out.println("작업 시작: " + Thread.currentThread().getName());

                    try {
                        Thread.sleep(2_000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }

                    System.out.println("작업 종료: " + Thread.currentThread().getName());
                    return "success";
                }).subscribeOn(Schedulers.boundedElastic())
                .subscribe(result ->
                        System.out.println("결과: " + result + ", thread=" + Thread.currentThread().getName())
                );

        System.out.println("subscribe 이후, thread=" + Thread.currentThread().getName());

        latch.await();
    }
}
