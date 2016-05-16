package aw.rx.wrapper;

import aw.rx.wrapper.functions.MDCFunc1;
import aw.rx.wrapper.functions.MdcFunc2;
import org.slf4j.MDC;
import rx.Observable;
import rx.Subscription;
import rx.functions.*;

public class MdcObservable<T> {

    private Observable<T> original;

    private MdcObservable(Observable<T> original) {
        this.original = original;
    }

    public static <T> MdcObservable<T> wrap(Observable<T> original) {
        return new MdcObservable(original);
    }

    public final <T2, R> MdcObservable<R> zipWith(MdcObservable<? extends T2> wrap, Func2<? super T, ? super T2, ? extends R> zipFunction) {
//		String requestId = MDC.get("requestId");
//		return (MdcObservable<R>) wrap(this.original.zipWith(wrap.original, (v1, v2) -> {
//			MDC.put("requestId", requestId);
//			return zipFunction.call(v1, v2);
//		}));

        return (MdcObservable<R>) wrap(this.original.zipWith(wrap.original, new MdcFunc2<>(zipFunction)));
    }

    public final <T2, R> MdcObservable<R> flatMap(Func1<? super T, ? extends Observable<? extends R>> func) {
//        String requestId = MDC.get("requestId");
//        return wrap(this.original.flatMap(t -> {
//            MDC.put("requestId", requestId);
//            return func.call(t);
//        }));

        return wrap(this.original.flatMap(new MDCFunc1<>(func)));
    }


    public final <T2, R> MdcObservable<R> map(Func1<? super T, ? extends R> func) {
//        String requestId = MDC.get("requestId");
//        return wrap(this.original.map(t -> {
//            MDC.put("requestId", requestId);
//            return func.call(t);
//        }));

        return wrap(this.original.map(new MDCFunc1<>(func)));
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
            if (err == null){
                throw new IllegalArgumentException("onError can not be null");
            }
            MDC.put("requestId", requestId);
            onError.call(err);
        }, () ->{
            MDC.put("requestId", requestId);
            onCompleted.call();
        });
    }

}