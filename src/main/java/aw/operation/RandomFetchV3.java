package aw.operation;

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

import static aw.rx.wrapper.MdcObservable.wrap;

public class RandomFetchV3 {

    final static Logger log = LoggerFactory.getLogger(RandomFetchV3.class);


    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        HttpServerOptions options = new HttpServerOptions();
        options.setPort(2020);
        vertx.createHttpServer(options).requestHandler(req -> {
            MDC.put("requestId", "" + Math.abs(new Random().nextLong()));
            String requestId = MDC.get("requestId");
            String fromAddress = req.remoteAddress().getDelegate().toString();
            log.info(fromAddress + " : Got request. requestId : "+ requestId);

            wrap(getRandomObservable(fromAddress, vertx)).zipWith(wrap(getRandomObservable(fromAddress, vertx)), (a, b) -> {
                log.info(fromAddress + " : got values: " + a + ", " + b + " this is before mapping this result to a new async call.");
                return a + " " + b;
            }).flatMap(res1 -> {
                log.info("Result of MDC zip : "+ res1);
                return getRandomObservable(fromAddress, vertx);
            }).map(res2 -> {
                log.info("Result of MDC flatMap : "+ res2);
                return res2;
            }).subscribe(v -> {
                log.info("Result of MDC map : "+ v);

                log.info(fromAddress + ": Ending response with data: " + v + ". For request with Id : "+ requestId);
                req.response().end(v + " response. For request Id : "+requestId);
            }, err -> {
                log.error("Error : ", err);
            }, () -> {
                log.info("Complete.");
            });
        }).listen();
    }

    private static Observable<Buffer> getRandomObservable(String id, Vertx vertx) {
        return Observable.create(sub -> {
            HttpClientRequest req = vertx.createHttpClient(new HttpClientOptions().setSsl(true)).getAbs("https://www.random.org/integers/?num=1&min=1&max=6&col=1&base=10&format=plain&rnd=new");
            req.headers().add(HttpHeaders.Names.USER_AGENT.toString(), "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36");
            req.exceptionHandler(e -> {
                sub.onError(e);
            });
            req.handler(resp -> {
                resp.bodyHandler(body -> {
                    sub.onNext(Buffer.buffer(body.toString().trim()));
                    sub.onCompleted();
                });
            }).end();
        });
    }


}
