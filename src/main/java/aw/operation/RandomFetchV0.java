package aw.operation;/*
 * Copyright © 2014 AirWatch, LLC. All rights reserved.
 * This product is protected by copyright and intellectual property laws
 * in the United States and other countries as well as by international treaties.
 * AirWatch products may be covered by one or more patents listed at
 * http://www.vmware.com/go/patents.
 */

import io.netty.handler.codec.http.HttpHeaders;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpClientRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import rx.Observable;

import java.util.Random;

/**
 * Created by sangadi on 5/10/2016.
 */
public class RandomFetchV0 {

    final static Logger log = LoggerFactory.getLogger(RandomFetchV0.class);


    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        log.info("trace");
        log.debug("debug");

        HttpServerOptions options = new HttpServerOptions();
        options.setPort(2036);
        vertx.createHttpServer(options).requestHandler(req -> {
            MDC.put("requestId", ""+new Random().nextLong());
            log.info("req... "+req);
            log.debug("debug");
            getRandom(vertx).zipWith(getRandom(vertx), (a, b) -> a+" "+b ).map(a -> "resp... "+a.toString()).subscribe(v -> req.response().end(v));
        }).listen();


    }

    private static Observable<Buffer> getRandom(Vertx vertx) {
        return Observable.create( sub -> {
            log.info("obs.... ");
            HttpClientOptions options = new HttpClientOptions();
            options.setSsl(true);
            HttpClientRequest req = vertx.createHttpClient(options).getAbs("https://www.random.org/integers/?num=1&min=1&max=6&col=1&base=10&format=plain&rnd=new");
            req.headers().add(HttpHeaders.Names.USER_AGENT.toString(), "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36");
            req.handler(resp -> {
                log.info("resp... ");
                resp.bodyHandler(body -> sub.onNext(body));
            });
            req.end();
        });
    }

}
