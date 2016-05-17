package aw.operation;

import io.vertx.core.http.HttpClientOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpClientRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sangadi on 5/12/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class RandomFetchV3Test {
    final Logger log = LoggerFactory.getLogger(RandomFetchV3.class);

    @Test
    public void testMain() throws Exception {
        int count = 5;
        int i = 0;
        do {
            makeRequest(getRequest());
            i++;
        } while (i < count);
        Thread.sleep(count * 20_000);
    }

    private HttpClientRequest getRequest() {
        Vertx vertx = Vertx.vertx();
        return vertx.createHttpClient(new HttpClientOptions()).getAbs("http://localhost:2020");
    }

    private void makeRequest(HttpClientRequest req0) {
        req0.handler(resp -> {
            resp.bodyHandler(body -> {
                log.info("result of zip : " + body.toString());
            });
        }).end();
    }

}