package reactor.reactiveOperation;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

/**
 * 변환, 전환 오퍼레이션
 */
public class TransferOperationTest
{
    /**
     * Flux로부터 데이터가 전달될 때 이것을 필터링하는 가장 기본적인 방법은 맨 앞부터 원하는 개수의 항목을 무시하는 것이다.
     * 이 때 skip() 오퍼레이션을 사용한다.
     */
    @Test
    void skipAFew()
    {
        Flux<String> skipFlux = Flux.just("one", "two", "skip a few", "ninety nine", "one hundred").skip(3);


        StepVerifier.create(skipFlux)
            .expectNext("ninety nine", "one hundred")
            .verifyComplete();
    }

    /**
     * take() 오퍼레이션은 처음부터 지정된 수의 항목만을 방출한다.
     */
    @Test
    void take()
    {
        Flux<String> nationalParkFlux = Flux.just("Yellowstone", "Yosemite", "Grand Canyon", "Zion", "Grand Teton").take(3);


        StepVerifier.create(nationalParkFlux)
            .expectNext("Yellowstone", "Yosemite", "Grand Canyon")
            .verifyComplete();
    }

    /**
     * 필터 조건을 걸어 필터링 가능
     */
    @Test
    void filter()
    {
        Flux<String> nationalParkFlux = Flux.just("Yellowstone", "Yosemite", "Grand Canyon", "Zion", "Grand Teton")
            .filter(np -> !np.contains(" "));


        StepVerifier.create(nationalParkFlux)
            .expectNext("Yellowstone", "Yosemite", "Zion")
            .verifyComplete();
    }

    /**
     * distinct를 통해 중복 제거
     */
    @Test
    void distinct()
    {
        Flux<String> animalFlux = Flux.just("dog", "cat", "bird", "dog", "bird", "anteater").distinct();


        StepVerifier.create(animalFlux)
            .expectNext("dog", "cat", "bird", "anteater")
            .verifyComplete();
    }

    /**
     * 매핑하기, 가장 많이 사용!
     * Flux나 Mono에 가장 많이 사용하는 오퍼레이션 중 하나는 발행된 항목을 다른 형태나 타입으로 변환하는 것이다.
     * 리액터의 타입은 이런 목적으로 map()과 flatMap() 오퍼레이션을 제공한다.
     *
     * 중요한 것은 각 항목의 소스 Flux로부터 발행될 때 동기적으로 매핑이 수행된다는 것이다. 따라서 비동기적으로 매핑을 수행하고 싶다면 flatMap() 오퍼레이션을 사용해야한다.
     */
    @Test
    void map()
    {
        Flux<String> flux = Flux.just("Micheal Jordan", "Scottie Pippen", "Steve Kerr")
            .map(p -> p + "kk");

        StepVerifier.create(flux)
            .expectNext("Micheal Jordankk")
            .expectNext("Scottie Pippenkk")
            .expectNext("Steve Kerrkk")
            .verifyComplete();
    }

    /**
     * flatMap은 flatten + map이다.
     * flatMap() 오퍼레이션에서는 각 객체를 새로운 Mono나 Flux로 매핑하며, 해당 Mono나 Flux의 결과는 하나의 새로운 Flux가 된다.
     * 즉 각각의 원소가 스트림을 구성하고 flatten(평탄화) 과정을 통해 하나의 스트림으로 다시 합친다.
     * flatMap()을 subscribeOn()과 함께 사용하면 리액터 타입의 변환을 비동기적으로 수행할 수 있다.
     *
     * subscribeOn()을 호출하지 않는다면 이전 map()과 동일하게 동기적으로 처리가 되겠지만 subscribeOn()을 호출함으로써 map() 오퍼레이션이 비동기적으로 처리된다.
     * flatMap()이나 subscribeOn()을 사용할 때의 장점은 다수의 병행 스레드에 작업을 분할하여 스트림의 처리량을 증가시킬 수 있다는 것이다.
     * 그러나 작업이 병행으로 수행되므로 어떤 작업이 먼저 끝날지 보장이 안되어 결과 Flux에서 방출되는 항목의 순서를 알 방법이 없다.
     */
    @Test
    void flatMap()
    {
        Flux<String> flux = Flux.just("Micheal Jordan", "Scottie Pippen", "Steve Kerr")
            .flatMap(n -> Mono.just(n).map(p -> p + "kk"))
                .subscribeOn(Schedulers.parallel());

        List<String> list = Arrays.asList(
            "Micheal Jordankk",
            "Scottie Pippenkk",
            "Steve Kerrkk");


        StepVerifier.create(flux)
            .expectNextMatches(p -> list.contains(p))
            .expectNextMatches(p -> list.contains(p))
            .expectNextMatches(p -> list.contains(p))
            .verifyComplete();
    }
}
