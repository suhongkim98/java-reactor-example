package reactor.threadScheduling;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CountDownLatch;

/**
 * subscribeOn을 이용한 구독 처리 쓰레드 스케줄링
 * subscribeOn()을 사용하면 Subscriber가 시퀀스에 대한 request신호를 별도 스케줄러로 처리한다.(시퀀스를 실행할 스케줄러를 지정한다)
 */
public class SubscribeOnTest
{
    /**
     * subscribeOn()으로 지정한 스케줄러는 시퀀스의 request 요청 처리뿐만 아니라 첫 번째 publishOn() 지정 이전까지의 신호 처리를 실행한다.
     * 따라서 위 코드를 실행하면 Flux.range()가 생성한 시퀀스의 신호 발생뿐만 아니라 map() 실행, Subscriber의 next, complete 신호 처리를 "SUB" 스케줄러가 실행한다.
     * 참고로 시퀀스의 request 요청과 관련된 로그를 보기 위해 log() 메서드를 사용했다.
     */
    @Test
    void subscribeOnTest() throws InterruptedException
    {
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
}
