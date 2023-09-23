package reactor.reactiveOperation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.List;

/**
 * 조합 오퍼레이션
 */
public class CombinationOperationTest
{
    @Test
    void mergeFluxes()
    {
        Flux<String> characterFlux = Flux
            .just("Garfield", "Kojak", "Barbossa")
            .delayElements(Duration.ofMillis(500));

        Flux<String> foodFlux = Flux
            .just("Lasagna", "Lollipops", "Apples")
            .delaySubscription(Duration.ofMillis(250)) // delaySubscription() : delay 시간만큼 구독을 연기
            .delayElements(Duration.ofMillis(500)); // delayElements() : item 사이에 delay 시간


        Flux<String> mergedFlux = characterFlux.mergeWith(foodFlux);


        StepVerifier.create(mergedFlux)
            .expectNext("Garfield")
            .expectNext("Lasagna")
            .expectNext("Kojak")
            .expectNext("Lollipops")
            .expectNext("Barbossa")
            .expectNext("Apples")
            .verifyComplete();
    }

    /**
     * zip() 오퍼레이션을 사용할 경우 각 Flux 소스로부터 한 항목씩 번갈아 가져와 새로운 Flux를 생성한다.
     */
    @Test
    void zipFluxes()
    {
        Flux<String> characterFlux = Flux
            .just("Garfield", "Kojak", "Barbossa");
        Flux<String> foodFlux = Flux
            .just("Lasagna", "Lollipops", "Apples");


        Flux<Tuple2<String, String>> zippedFlux = Flux.zip(characterFlux, foodFlux);


        StepVerifier.create(zippedFlux)
            .expectNextMatches(p -> p.getT1().equals("Garfield") && p.getT2().equals("Lasagna"))
            .expectNextMatches(p -> p.getT1().equals("Kojak") && p.getT2().equals("Lollipops"))
            .expectNextMatches(p -> p.getT1().equals("Barbossa") && p.getT2().equals("Apples"))
            .verifyComplete();
    }

    /**
     * zip() 오퍼레이션은 정적인 생성 오퍼레이션이다. 따라서 캐릭터와 음식 Flux를 완벽하게 조합한다.
     * zip() 오퍼레이션을 통해 방출되는 값을 Tuple2가 아닌 다른 타입을 사용하고 싶다면 우리가 원하는 객체를 생성하는 함수를 zip()에 제공하면 된다.
     */
    @Test
    void zipFluxesToObject()
    {
        Flux<String> characterFlux = Flux
            .just("Garfield", "Kojak", "Barbossa");
        Flux<String> foodFlux = Flux
            .just("Lasagna", "Lollipops", "Apples");


        Flux<String> zippedFlux = Flux.zip(characterFlux, foodFlux, (c, f) -> c + " eats " + f);


        StepVerifier.create(zippedFlux)
            .expectNext("Garfield eats Lasagna")
            .expectNext("Kojak eats Lollipops")
            .expectNext("Barbossa eats Apples")
            .verifyComplete();
    }

    /**
     * 두 개의 Flux 객체가 있는데 이것을 결합하는 대신 먼저 값을 방출하는 소스 Flux의 값을 발행하는 새로운 Flux를 생성하고 싶다면 first() 오퍼레이션을 사용하면 된다.
     */
    @Test
    void firstFlux()
    {
        Flux<String> slowFlux = Flux.just("tortoise", "snail", "sloth").delaySubscription(Duration.ofMillis(100));
        Flux<String> fastFlux = Flux.just("hare", "cheetah", "squirrel");


        Flux<String> firstFlux = Flux.first(slowFlux, fastFlux);


        StepVerifier.create(firstFlux)
            .expectNext("hare")
            .expectNext("cheetah")
            .expectNext("squirrel")
            .verifyComplete();
    }

    @Test
    @DisplayName("collectList로 여러 개의 데이터를 하나의 List에 원소로 포함시켜 Mono를 리턴한다")
    void testCollectList() {
        Flux<String> flux = Flux.concat(
                Flux.just("a", "b", "c"),
                Flux.just("d", "e", "f"),
                Flux.just("h", "i"));

        Assertions.assertInstanceOf(Mono.class, flux.collectList());
        Assertions.assertInstanceOf(List.class, flux.collectList().block());
    }
}
