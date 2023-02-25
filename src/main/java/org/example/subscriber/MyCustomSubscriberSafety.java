package org.example.subscriber;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;

/**
 * Subscriber를 구현하는 안전한 방법
 * 리액터 프레임워크에서 미리 만들어둔, BaseSubscriber 추상클래스를 상속해서 구현한다.
 *
 * Subscriber의 onSubscribe, onNext, onError, onComplete 는 모두 재정의할 수 없게 final 로 선언되어 있다.
 *
 * 제공하는 hook 을 통해서, 상황에 따라 얼마든지 조정이 가능하다.
 */
public class MyCustomSubscriberSafety extends BaseSubscriber<String>
{
    @Override
    public void hookOnSubscribe(Subscription subscription) {
        System.out.println("onSubscribe, subscription " + subscription.hashCode());
        request(1); // 구독 후, 최초 요청
    }

    @Override
    public void hookOnNext(String s) {
        System.out.println("onNext = {}" + s);
        request(1); // 데이터 수신 후, 추가 요청
    }

    @Override
    public void hookOnComplete() {
        System.out.println("onComplete");
    }
}
