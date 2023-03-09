package reactor.threadScheduling;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CountDownLatch;

/**
 * publishOn을 이용한 신호 처리 쓰레드 스케줄링
 *
 * publishOn() 메서드를 이용하면 next, complete, error신호를 별도 쓰레드로 처리할 수 있다.
 * map(), flatMap() 등의 변환도 publishOn()이 지정한 쓰레드를 이용해서 처리한다.
 *
 */
public class PublishOnTest
{
    /**
     * 최초에 2개를 미리 가져올 때를 제외하면 나머지는 모두 publishOn()으로 전달한 스케줄러의 쓰레드가 처리하는 것을 알 수 있다.
     */
    @Test
    void publishOnTest() throws InterruptedException
    {
        CountDownLatch latch = new CountDownLatch(1);

        Flux.range(1, 6)
            .map(i -> {
                System.out.printf("%s, map 1: %d to %d\n", Thread.currentThread(), i, i + 10);
                return i + 10;
            })
            .publishOn(Schedulers.boundedElastic(), 2) // 두 번째 인자인 2는 스케줄러가 신호를 처리하기 전에 미리 가져올 (prefetch) 데이터 개수이다.
            .map(i -> {     // publishOn에서 지정한 PUB 스케줄러가 실행
                System.out.printf("%s, map 2: %d to %d\n", Thread.currentThread(), i, i + 10);
                return i + 10;
            })
            .subscribe(new BaseSubscriber<Integer>() {
                @Override
                protected void hookOnSubscribe(Subscription subscription) {
                    System.out.println(Thread.currentThread() + " hookOnSubscribe");
                    requestUnbounded();
                }

                @Override
                protected void hookOnNext(Integer value) {
                    System.out.println(Thread.currentThread() + " hookOnNext: " + value);
                }

                @Override
                protected void hookOnComplete() {
                    System.out.println(Thread.currentThread() + " hookOnComplete");
                    latch.countDown();
                }
            });

        latch.await();
    }

    /**
     * publishOn()에 지정한 스케줄러는 다음 publishOn()을 설정할 때까지 적용된다.
     * 첫 번째 publishOn()과 두 번째 publishOn() 사이의 map() 처리는 첫번째 스케줄러가 실행하고
     * 두 번째 publishOn() 이후의 map(), 신호 처리는 두번째 스케줄러가 실행한 것을 알 수 있다
     */
    @Test
    void publishOnDoubleTest() throws InterruptedException
    {
        CountDownLatch latch = new CountDownLatch(1);

        Flux.range(1, 6)
            .map(i -> {
                System.out.printf("%s, map 0: %d to %d\n", Thread.currentThread(), i, i + 10);
                return i + 10;
            })
            .publishOn(Schedulers.boundedElastic())
            .map(i -> {
                System.out.printf("%s, map 1: %d to %d\n", Thread.currentThread(), i, i + 10);
                return i + 10;
            })
            .publishOn(Schedulers.boundedElastic())
            .map(i -> {
                System.out.printf("%s, map 2: %d to %d\n", Thread.currentThread(), i, i + 10);
                return i + 10;
            })
            .subscribe(new BaseSubscriber<Integer>() {
                @Override
                protected void hookOnSubscribe(Subscription subscription) {
                    System.out.println(Thread.currentThread() + " hookOnSubscribe");
                    requestUnbounded();
                }

                @Override
                protected void hookOnNext(Integer value) {
                    System.out.println(Thread.currentThread() + " hookOnNext: " + value);
                }

                @Override
                protected void hookOnComplete() {
                    System.out.println(Thread.currentThread() + " hookOnComplete");
                    latch.countDown();
                }
            });

        latch.await();
    }
}
