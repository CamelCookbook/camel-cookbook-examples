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

    private static Logger logger = LoggerFactory.getLogger(CustomTransactionalResource.class);

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
