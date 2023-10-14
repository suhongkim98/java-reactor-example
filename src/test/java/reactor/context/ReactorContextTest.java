package reactor.context;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

// 리액터 컨텍스트는 subscriber에 매핑된다. 즉 구독자마다 컨텍스트가 생성됨
public class ReactorContextTest {

    /**
     * Context 기본 예제
     * - contextWrite() Operator로 Context에 데이터 쓰기 작업을 할 수 있다.
     * - Context.put()으로 Context에 데이터를 쓸 수 있다.
     * - deferContextual() Operator로 Context에 데이터 읽기 작업을 할 수 있다.
     * - Context.get()으로 Context에서 데이터를 읽을 수 있다.
     * - transformDeferredContextual() Operator로 Operator 중간에서 Context에 데이터 읽기 작업을 할 수 있다.
     */
    @Test
    void testContextApi() throws InterruptedException {
        Mono.deferContextual(ctx ->
                        Mono.just("Hello" + " " + ctx.get("firstName"))
                                .doOnNext(data -> System.out.printf("# just doOnNext : %s\n", data))
                ).subscribeOn(Schedulers.boundedElastic())
                .publishOn(Schedulers.parallel())
                .transformDeferredContextual(
                        (mono, ctx) -> mono.map(data -> data + " " + ctx.get("lastName"))
                )
                .contextWrite(context -> context.put("lastName", "Jobs"))
                .contextWrite(context -> context.put("firstName", "Steve"))
                .subscribe(data -> System.out.printf("# onNext: %s", data));

        Thread.sleep(100L);
    }

    /**
     * Context의 특징 예제
     *  - Context는 Operator 체인의 아래에서부터 위로 전파된다.
     *      - 따라서 Operator 체인 상에서 Context read 메서드가 Context write 메서드 밑에 있을 경우에는 write된 값을 read할 수 없다.
     *      - 동일한 키에 대한 값을 중복해서 저장하면 Operator 체인에서 가장 위쪽에 위치한 contextWrite()이 저장한 값으로 덮어쓴다
     */
    @Test
    @DisplayName("Context read 메서드가 Context write 메서드 밑에 있을 경우에는 write된 값을 read할 수 없다.")
    void testReadContext() throws InterruptedException {

        String key1 = "company";
        String key2 = "name";

        Mono.deferContextual(ctx ->
                        Mono.just(ctx.get(key1))
                )
                .publishOn(Schedulers.parallel())
                .contextWrite(context -> context.put(key2, "Bill"))
                .transformDeferredContextual((mono, ctx) ->
                        mono.map(data -> data + ", " + ctx.getOrDefault(key2, "실패"))
                )
                .contextWrite(context -> context.put(key1, "Apple"))
                .subscribe(data -> System.out.printf("# onNext: %s\n", data));

        Thread.sleep(100L);
    }
}
