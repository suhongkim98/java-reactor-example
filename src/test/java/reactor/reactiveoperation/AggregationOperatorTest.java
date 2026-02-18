package reactor.reactiveoperation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

/**
 * 로직 오퍼레이션
 */
public class AggregationOperatorTest
{
    /**
     * all로 조건검사 가능
     */
    @Test
    void all()
    {
        Flux<String> animalFlux = Flux.just("aardvark", "elephant", "koala", "eagle", "kangaroo");
        Mono<Boolean> hasAMono = animalFlux.all(a -> a.contains("a"));
        StepVerifier.create(hasAMono)
            .expectNext(true) // 모든 동물에 a가 포함되어있기 때문에 true
            .verifyComplete();


        Mono<Boolean> hasKMono = animalFlux.all(a -> a.contains("k"));
        StepVerifier.create(hasKMono)
            .expectNext(false) // 모든 동물에 k가 포함되어있지 않기 때문에 false
            .verifyComplete();
    }

    @Test
    void any()
    {
        Flux<String> animalFlux = Flux.just("aardvark", "elephant", "koala", "eagle", "kangaroo");

        Mono<Boolean> hasTMono = animalFlux.any(a -> a.contains("t"));
        StepVerifier.create(hasTMono)
            .expectNext(true) // t가 들어있는 동물이 하나 이상이므로 true
            .verifyComplete();


        Mono<Boolean> hasZMono = animalFlux.any(a -> a.contains("z"));
        StepVerifier.create(hasZMono)
            .expectNext(false) // z가 들어있는 동물이 하나도 없으므로 false
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
