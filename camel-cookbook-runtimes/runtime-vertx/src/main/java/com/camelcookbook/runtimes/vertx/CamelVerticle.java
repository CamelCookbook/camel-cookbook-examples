/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.camelcookbook.runtimes.vertx;

import io.vertx.camel.CamelBridge;
import io.vertx.camel.CamelBridgeOptions;
import io.vertx.camel.OutboundMapping;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;

/**
 * Created by ceposta 
 * <a href="http://christianposta.com/blog>http://christianposta.com/blog</a>.
 */
public class CamelVerticle extends AbstractVerticle {

    private CamelContext camelContext;
    private CamelBridge bridge;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        // create camel context
        SimpleRegistry registry = new SimpleRegistry();
        camelContext = new DefaultCamelContext(registry);
        camelContext.addRoutes(new CamelRoutes());
        camelContext.start();

        bridge = CamelBridge.create(vertx, new CamelBridgeOptions(camelContext)
                .addOutboundMapping(OutboundMapping.fromVertx("greetings-counter").toCamel("seda:greetings"))
        ).start();
    }


    @Override
    public void stop() throws Exception {
        if (camelContext != null && bridge != null) {
            bridge.stop();
            camelContext.stop();
        }
    }
}
