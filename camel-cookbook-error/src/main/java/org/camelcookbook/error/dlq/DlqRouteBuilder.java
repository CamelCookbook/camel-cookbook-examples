/*
 * Copyright (C) Scott Cranton and Jakub Korab
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

package org.camelcookbook.error.dlq;

import org.apache.camel.builder.RouteBuilder;
import org.camelcookbook.error.shared.FlakyProcessor;

public class DlqRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        errorHandler(deadLetterChannel("seda:error"));

        from("direct:start")
                .routeId("myDlqRoute")
            .setHeader("myHeader").constant("changed")
            .bean(FlakyProcessor.class)
            .to("mock:result");

        from("direct:multiroute")
                .routeId("myDlqMultistepRoute")
            .setHeader("myHeader").constant("multistep")
            .inOut("seda:flakyroute")
            .setHeader("myHeader").constant("changed")
            .to("mock:result");

        from("seda:flakyroute")
                .routeId("myFlakyRoute")
            .setHeader("myHeader").constant("flaky")
            .bean(FlakyProcessor.class);

        from("direct:multirouteOriginal")
                .routeId("myDlqMultistepOriginalRoute")
            .setHeader("myHeader").constant("multistep")
            .inOut("seda:flakyrouteOriginal")
            .setHeader("myHeader").constant("changed")
            .to("mock:result");

        from("seda:flakyrouteOriginal")
                .routeId("myFlakyRouteOriginal")
                .errorHandler(deadLetterChannel("seda:error").useOriginalMessage())
            .setHeader("myHeader").constant("flaky")
            .bean(FlakyProcessor.class);

        from("direct:routeSpecific")
                .routeId("myDlqSpecificRoute")
                .errorHandler(deadLetterChannel("seda:error"))
            .bean(FlakyProcessor.class)
            .to("mock:result");

        from("direct:useOriginal")
                .routeId("myDlqOriginalRoute")
                .errorHandler(deadLetterChannel("seda:error").useOriginalMessage())
            .setHeader("myHeader").constant("changed")
            .bean(FlakyProcessor.class)
            .to("mock:result");

        from("seda:error")
                .routeId("myDlqChannelRoute")
            .to("log:dlq?showAll=true&multiline=true")
            .to("mock:error");
    }
}
