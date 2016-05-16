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

/**
 * Created by sangadi on 5/16/2016.
 */
public class MDCFunc1<T, R> implements Func1<T, R> {

    private Func1<T, R> func1;
    private String requestId;

    public MDCFunc1(Func1 func1) {
        this.func1 = func1;
        this.requestId = MDC.get("requestId");
    }

    @Override
    public R call(T o) {
        MDC.put("requestId", requestId);
        return func1.call(o);
    }
}
