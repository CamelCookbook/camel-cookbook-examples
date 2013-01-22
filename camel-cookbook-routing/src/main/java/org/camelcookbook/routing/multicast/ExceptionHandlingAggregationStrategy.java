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

package org.camelcookbook.routing.multicast;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aggregation strategy that concatenates String responses.
 */
public class ExceptionHandlingAggregationStrategy implements AggregationStrategy {
    private Logger logger = LoggerFactory.getLogger(ExceptionHandlingAggregationStrategy.class);

    @Override
    public Exchange aggregate(Exchange exchange1, Exchange exchange2) {
        if (exchange2.isFailed()) {
            // error - work out what to do
            Exception ex = exchange2.getException();
            if (exchange1 == null) {
                exchange2.setException(null);
                exchange2.setProperty("multicast.exception", ex);
                return exchange2;
            } else {
                exchange1.setProperty("multicast.exception", ex);
                return exchange1;
            }
        } else {
            if (exchange1 == null) {
                return exchange2;
            } else {
                String body1 = exchange1.getIn().getBody(String.class);
                String body2 = exchange2.getIn().getBody(String.class);
                String merged = (body1 == null) ? body2 : body1 + "," + body2;
                exchange1.getIn().setBody(merged);
                return exchange1;
            }
        }
    }

}
