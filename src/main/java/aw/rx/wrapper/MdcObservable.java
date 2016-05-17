package aw.rx.wrapper;

import aw.rx.wrapper.functions.MDCFunc;
import org.slf4j.MDC;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

public class MdcObservable<T> {

    private Observable<T> original;

    private MdcObservable(Observable<T> original) {
        this.original = original;
    }

    public static <T> MdcObservable<T> wrap(Observable<T> original) {
        return new MdcObservable(original);
    }

    public final <T2, R> MdcObservable<R> zipWith(MdcObservable<? extends T2> wrap, Func2<? super T, ? super T2, ? extends R> zipFunction) {
        return wrap(this.original.zipWith(wrap.original, new MDCFunc<>(zipFunction)));
    }

    public final <T2, R> MdcObservable<R> flatMap(Func1<? super T, ? extends Observable<? extends R>> func) {
        return wrap(this.original.flatMap(new MDCFunc<>(func)));
    }

    public final <T2, R> MdcObservable<R> map(Func1<? super T, ? extends R> func) {
        return wrap(this.original.map(new MDCFunc<>(func)));
    }

    public final Subscription subscribe(final Action1<? super T> onNext, final Action1<Throwable> onError, final Action0 onCompleted) {
        String requestId = MDC.get("requestId");
        return original.subscribe(v -> {
            if (onNext == null) {
                throw new IllegalArgumentException("onNext can not be null");
            }
            MDC.put("requestId", requestId);
            onNext.call(v);
        }, err -> {
            if (err == null) {
                throw new IllegalArgumentException("onError can not be null");
            }
            MDC.put("requestId", requestId);
            onError.call(err);
        }, () -> {
            if (onCompleted == null) {
                throw new IllegalArgumentException("onCompleted can not be null");
            }
            MDC.put("requestId", requestId);
            onCompleted.call();
        });
    }

}