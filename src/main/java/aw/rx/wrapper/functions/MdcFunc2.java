/*
 * Copyright © 2014 AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws
 * in the United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */

package aw.rx.wrapper.functions;

import org.slf4j.MDC;
import rx.functions.Func2;

/**
 * Created by sangadi on 5/16/2016.
 */
public class MdcFunc2<T1, T2, R> implements Func2<T1, T2, R> {

    private Func2<T1, T2, R> originalFunc;
    private String requestId;

    public MdcFunc2(Func2 originalFunc) {
        this.originalFunc = originalFunc;
        this.requestId = MDC.get("requestId");
    }

    @Override
    public R call(T1 t1, T2 t2) {
        MDC.put("requestId", requestId);
        return originalFunc.call(t1, t2);
    }
}

