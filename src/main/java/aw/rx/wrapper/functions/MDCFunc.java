/*
 * Copyright © 2014 AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws
 * in the United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */

package aw.rx.wrapper.functions;

import org.slf4j.MDC;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by sangadi on 5/16/2016.
 */
public class MDCFunc<T1, T2, R> implements Func1<T1, R>, Func2<T1, T2, R> {
    private Func1<T1, R> func1;
    private Func2<T1, T2, R> func2;
    private String requestId;

    public MDCFunc(Func1 func1) {
        this.func1 = func1;
        this.requestId = MDC.get("requestId");
    }

    public MDCFunc(Func2 func2) {
        this.func2 = func2;
        this.requestId = MDC.get("requestId");
    }

    @Override
    public R call(T1 o) {
        MDC.put("requestId", requestId);
        return func1.call(o);
    }

    @Override
    public R call(T1 t1, T2 t2) {
        MDC.put("requestId", requestId);
        return func2.call(t1, t2);
    }
}
