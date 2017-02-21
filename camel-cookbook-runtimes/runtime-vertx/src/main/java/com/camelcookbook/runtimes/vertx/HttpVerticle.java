/*
 * Copyright (C) Scott Cranton, Jakub Korab, and Christian Posta
 * https://github.com/CamelCookbook
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.camelcookbook.runtimes.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.util.HashMap;
import java.util.Map;

public class HttpVerticle extends AbstractVerticle {
    @Override
    public void start(Future<Void> startFuture) throws Exception {

        Router router = Router.router(vertx);
        router.route("/greetings/:name").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            Map<String, Object> values = new HashMap<>();
            values.put("name", routingContext.request().params().get("name"));
            values.put("increment", 1);

            // publish a message that we got a new counter to anyone listening
            // on the eventbus ... the eventbus is our bridge to Camel
            vertx.eventBus().publish("greetings-counter", new JsonObject(values));

            response.putHeader("content-type", "text/html")
                    .end("<h1>Hello from vertx</h1>");
        });

        int port = config().getInteger("http.port", 8080);

        vertx.createHttpServer().requestHandler(router::accept)
                .listen(port, result -> {
                    if (result.succeeded()) {
                        startFuture.complete();
                    } else {
                        startFuture.fail(result.cause());
                    }
                });
    }
}
