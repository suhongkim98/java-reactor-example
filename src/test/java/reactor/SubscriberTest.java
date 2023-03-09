package reactor;

import org.example.subscriber.MyCustomSubscriber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SubscriberTest
{
    /**
     * 일반적인 Reactive Streams 에서의 Publisher 는 subscribe 를 실행할 때 subscriber 를 등록해준다.
     *  * Flux 에서 제공하는 팩토리 메서드는 subscriber 를 등록해주는 메서드도 있는 반면에,
     *  * subscriber 를 매개변수로 등록하지 않는 함수도 존재한다.
     *  * subscriber 가 필요없어서가 아니라, 내부 로직에서 자동으로 subscriber 를 만들어 준다.(new LambdaSubscriber<>(...) 를 통해 Subscriber를 생성)
            *  기본적으로 s.request(Long.MAX_VALUE) 이다.
     *
     *  * 그래서 개발자가 따로 subscriber 를 구현하지 않아도 된다.
     */
    @Test
    void nonSubscriber()
    {
        List<String> list = new ArrayList<>();

        Flux<String> flux = Flux.just("A", "B");
        flux.subscribe(list::add); // 내부적으로 subscriber를 구현해줌

        Assertions.assertEquals(2, list.size());
    }

    @Test
    @DisplayName("당연히 구독자는 여러명 있을 수 있다.")
    void nonSubscriber2()
    {
        List<String> list = new ArrayList<>();

        Flux<String> flux = Flux.just("A", "B");
        flux.subscribe(list::add); // 내부적으로 subscriber를 구현해줌
        flux.subscribe(list::add); // 내부적으로 subscriber를 구현해줌

        Assertions.assertEquals(4, list.size());
    }

    @Test
    void subscriber()
    {
        Flux<String> flux = Flux.just("A", "B");

        flux.subscribe(new MyCustomSubscriber()); // 커스텀으로 구현한 subscriber 사용
    }

    @Test
    void subscribe()
    {
        // Disposable subscribe() : 구독은 하지만, onNext, onError, onComplete 처리는 하지 않는다.
        Flux<String> flux = Flux.just("A", "B");
        Disposable disposable = flux.subscribe();
    }

    @Test
    void subscribe2()
    {
        // Disposable subscribe(Consumer<? super T> consumer) : onNext 만 처리한다.
        Flux<String> flux = Flux.just("A", "B");

        Disposable disposable = flux.subscribe(s -> System.out.println(s));
    }

    @Test
    void subscribe3()
    {
        // Disposable subscribe(Consumer<? super T> consumer, Consumer<? super Throwable> errorConsumer) : onNext, onError 만 처리한다.
        //errorConsumer 는 NotNull 이다.
        Flux<String> flux = Flux.just("A", "B");

        Disposable disposable = flux.subscribe(s -> System.out.println(s), s-> System.out.println("error"));
    }

    @Test
    @DisplayName("dispose로 구독 취소가 가능하다")
    void 구독취소_테스트() throws Exception {
        Disposable disposable = Flux.interval(Duration.ofMillis(500))
            .subscribe(i -> System.out.println(i));

        Thread.sleep(2000);

        disposable.dispose();
    }
}
