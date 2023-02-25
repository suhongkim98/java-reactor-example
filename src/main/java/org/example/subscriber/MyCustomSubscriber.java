package org.example.subscriber;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * 리액티브 스트림 표준 Subscriber 인터페이스를 직접 구현하는 것은 쉬운 일이 아니다.
 * 제시하는 표준 스펙을 모두 준수해야하고, TCK(Technology Compatibility Kit) 테스트코드를 통과해야한다.
 * 아래는 동작은 하지만, 잘못된 구현이다.
 * this.subscription.request(n) 에서 숫자가 0 이하인지, 구독(subscription) 상태는 정상인지 등을 확인하지 않고, 1개씩 계속 요청했기 때문이다.
 *
 * 안전한 구현은 MyCustomSubscriberSafety 확인
 */
public class MyCustomSubscriber implements Subscriber<String>
{
    private volatile Subscription subscription;

    @Override
    public void onSubscribe(Subscription subscription) {
        System.out.println("onSubscribe, subscription " + subscription.hashCode());
        this.subscription = subscription;
        this.subscription.request(1); // 구독 후, 최초 요청
    }

    @Override
    public void onNext(String s) {
        System.out.println("onNext = " + s);
        this.subscription.request(1); // 데이터 수신 후, 추가 요청
    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onComplete() {
        System.out.println("onComplete");
    }
}
