package reactiveStreams;

import org.example.subscriber.MyCustomSubscriber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 명령형 코드 <-> 리액티브 코드
 * 리액티브 프로그래밍은 명령형 프로그래밍의 한계를 해결할 수 있는 대안이 되는 패러다임이다.
 * 명령형 코드는 작업이 수행되는 동안 특히 원격지 서버로부터 데이터베이스에 데이터를 쓰거나 가져오는 것과 같은 것이라면 이 작업이 완료될 때까지 아무 것도 할 수 없다.(동기적)
 * 따라서 이 작업을 수행하는 스레드는 차단되며 이렇게 차단되는 스레드는 낭비다.
 * 리액티브 프로그래밍의 경우 순차적으로 수행되는 작업 단계를 나타낸 것이 아니라 데이터가 흘러가는 파이프라인이나 스트림을 표현한다.
 * 데이터 전체를 사용할 수 있을 때까지 기다리지 않고 사용 가능한 데이터가 있을 때마다 처리
 * <p>
 * 리액티브 스트림은 non-blocking(넌블럭킹) backPressure(역압)을 이용하여 비동기 서비스를 할 때 기본이 되는 스펙이다.
 * 차단되지 않는 백 프레셔를 갖는 비동기 스트림 처리의 표준을 제공하는 것이 목적, 비동기의 경계를 명확히하여 스트림 데이터의 교환을 효과적으로 관리한다.
 *
 * 리액티브 스트림은 4개의 인터페이스인 Publisher(발행자), Subscriber(구독자), Subscription(구독), Processor(프로세서)로 요약가능
    * 발행자(Publisher)는 데이터를 생성하고
    * 구독자(Subscriber)는 Subscription를 통해 Publisher에게 자신이 처리할 수 있는 만큼의 데이터를 요청하고 처리
    * 이때 발행자가 제공할 수 있는 데이터의 양은 무한(unbounded) 하고 순차적(sequential) 처리를 보장
 *
 * 리액티브 스트림의 대표적인 구현체로는 Reactor, RxJava 등등이 있으며 우리는 Reactor을 알아볼 것이다.
 */
public class ReactiveStreamsTest
{
    @Test
    @DisplayName("명령형 프로그래밍")
    void a()
    {
        String name = "Craig";
        String capitalName = name.toUpperCase();
        String greeting = "Hello, " + capitalName + "!";
        System.out.println(greeting);
    }

    @Test
    @DisplayName("리액티브 프로그래밍(Reactor)")
    void b()
    {
        Mono.just("Craig")              // 첫번째 모노를 생성하고 방출
            .map(n -> n.toUpperCase())       // 오퍼레이션에 전달되어 대문자로 변경하고 다른 Mono를 생성
            .map(cn -> "Hello, " + cn + "!") // 두 번째 Mono가 값을 방출하면 두 번째 map() 오퍼레이션에 전달되어 문자열 결합이 수행
            .subscribe(System.out::println); // subscribe() 호출에서는 세 번째 Mono를 구독하여 데이터를 수신하고 출력
    }

    @Test
    @DisplayName("Publisher")
    void publisher()
    {
        /**
         * Publisher는 무한한 data를 제공
         * 제공되어진 data는 Subscriber가 구독하는 형식으로 처리
         *
         * publisher 인터페이스는 subscribe 라는 추상 메서드를 가진다.
         * Publisher.subscribe(Subscriber)의 형식으로 data 제공자와 구독자가 연결을 맺게된다.
         *
         * Reactor 에서는 publisher의 구현체로 Flux와 Mono가 있다.
         */
        Flux<String> flux = Flux.just("A", "B");
        Mono<String> mono = Mono.just("A");

        Assertions.assertInstanceOf(Publisher.class, flux);
        Assertions.assertInstanceOf(Publisher.class, mono);
    }

    @Test
    @DisplayName("Subscriber")
    void subscriber()
    {
        /**
         * Subscriber가 구독을 신청하게 되면 Publisher로부터 이벤트를 수신받을 수 있다.
         * 이 이벤트들은 Subscriber 인터페이스의 메서드를 통해 전송된다.
            * onSubscribe, 구독시 최초에 한번만 호출
            * onNext, 구독자가 요구하는 데이터의 수 만큼 호출 (최대 java.lang.Long.MAX_VALUE)
            * onError, 에러 또는 더이상 처리할 수 없는 경우
            * onComplete, 	모든 처리가 정상적으로 완료된 경우
         *
         * Subscriber가 수신할 첫 번째 이벤트는 onSubscribe 메서드의 호출을 통해 이루어진다.
         * Publisher가 onSubscribe 메서드를 호출할 때 이 메서드의 인자로 Subscription 객체를 Subscriber에 전달한다.
         */
        Flux<String> flux = Flux.just("A", "B");

        MyCustomSubscriber myCustomSubscriber = new MyCustomSubscriber(); // 내가 만든 subscriber 구현체
        flux.subscribe(myCustomSubscriber); // onSubscribe 출력 확인
    }

    @Test
    @DisplayName("Subscription")
    void subscription()
    {
        /**
         * Subscriber는 Subscription 객체를 통해서 구독을 관리할 수 있다.
         * Publisher에서 Subscriber의 onSubscribe 메서드를 호출하며 매개변수로 subscription을 전달한다.
         *
         * Subscriber는 Subscription의 request()를 호출하여 데이터를 요청하거나, cancel()을 호출하여 데이터를 더 이상 수신하지 않거나 구독을 취소할 수 있다.
         *
         * request()를 호출할 때 Subscriber는 받고자 하는 데이터 항목 수를 나타내는 long 타입 값을 인자로 전달하는데 이것이 백프레셔이며, Subscriber가 처리할 수 있는 것보다 더 많은 데이터를 전송하는 것을 막아준다.
         * Subscriber의 데이터 요청이 완료되면 데이터가 스트림을 통해 전달되기 시작한다. 이 때 onNext() 메서드가 호출되어 Publisher가 전송하는 데이터가 Subscriber에게 전달되며, 에러 발생시 onError()가 호출된다.
         * 그리고 Publisher에서 전송할 데이터가 없고 더 이상 데이터를 생성하지 않는다면 Publisher가 onComplete()를 호출하여 작업이 끝났다고 Subscriber에게 알려준다.
         *
         *
         * MyCustomSubscriber 클래스 확인
         */

    }

    @Test
    @DisplayName("Processor")
    void processor()
    {
        // Processor 인터페이스는 Subscriber, Publisher 인터페이스를 결합한 것이다.
    }
}
