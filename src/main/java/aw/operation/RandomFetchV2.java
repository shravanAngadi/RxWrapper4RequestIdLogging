package aw.operation;

import io.netty.handler.codec.http.HttpHeaders;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.core.http.HttpClientRequest;


import java.util.Random;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;


import rx.Observable;

public class RandomFetchV2 {

    final static Logger log = LoggerFactory.getLogger(RandomFetchV2.class);


    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        log.trace("trace");

        HttpServerOptions options = new HttpServerOptions();
        options.setPort(2036);
        vertx.createHttpServer(options).requestHandler(req -> {
            MDC.put("requestId", ""+new Random().nextLong());
            String fromAddress = req.remoteAddress().getDelegate().toString();
            log.info(fromAddress+": Got request.");
            String requestId = MDC.get("requestId");
            getRandom(fromAddress, vertx).zipWith(getRandom(fromAddress, vertx), (a, b) -> {
            	MDC.put("requestId", requestId);
            	log.info(fromAddress+": got values: "+a+ ", "+b);
            	return a+" "+b;
            } ).subscribe(v -> {
            	MDC.put("requestId", requestId);
            	log.info(fromAddress+": Ending response with data: "+v);
            	req.response().end(v);
            });
        }).listen();


    }

    private static Observable<Buffer> getRandom(String id, Vertx vertx) {
        return Observable.create( sub -> {
//            log.info(id+": Observable create.");
            HttpClientOptions options = new HttpClientOptions();
            options.setSsl(true);
            HttpClientRequest req = vertx.createHttpClient(options).getAbs("https://www.random.org/integers/?num=1&min=1&max=6&col=1&base=10&format=plain&rnd=new");
            req.headers().add(HttpHeaders.Names.USER_AGENT.toString(), "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36");
            req.handler(resp -> {
//                log.info(id+": Got response for random no.");
                resp.bodyHandler(body -> {
//                	log.info(id+": Got body: "+body.toString().trim());
                	sub.onNext(Buffer.buffer(body.toString().trim()));
                });
            });
            req.end();
//            log.info(id+": Request end to get random no.");
        });
    }

}
