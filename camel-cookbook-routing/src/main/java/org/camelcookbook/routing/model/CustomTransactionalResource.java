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

package org.camelcookbook.routing.model;

import org.apache.camel.Exchange;
import org.apache.camel.spi.Synchronization;
import org.apache.camel.spi.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO determine if this is really needed at this point, might be useful later
 */
public class CustomTransactionalResource {

    private static final Logger logger = LoggerFactory.getLogger(CustomTransactionalResource.class);

    public void initTransaction(Exchange exchange) {
        UnitOfWork unitOfWork = exchange.getUnitOfWork();
        unitOfWork.addSynchronization(new Synchronization() {
            @Override
            public void onComplete(Exchange exchange) {
                logger.info("Custom transactional task completed - committing");
            }

            @Override
            public void onFailure(Exchange exchange) {
                logger.info("Custom transactional task failed - rolling back");
            }
        });
    }

}
