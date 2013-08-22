package org.camelcookbook.examples.transactions.utils;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.memory.MemoryPersistenceAdapter;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.commons.lang.Validate;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit Test aspect that creates an embedded ActiveMQ broker at the beginning of eac h test and shuts it down after.
 */
public class EmbeddedActiveMQBroker extends ExternalResource {

    private final Logger log = LoggerFactory.getLogger(EmbeddedActiveMQBroker.class);
    private final String brokerId;
    private BrokerService brokerService;
    private final String tcpConnectorUri;

    public EmbeddedActiveMQBroker(String brokerId) {
        Validate.notEmpty(brokerId, "brokerId is empty");
        this.brokerId = brokerId;
        tcpConnectorUri = "tcp://localhost:" + AvailablePortFinder.getNextAvailable();

        brokerService = new BrokerService();
        brokerService.setBrokerId(brokerId);
        brokerService.setPersistent(false);
        brokerService.setUseJmx(false);
        try {
            brokerService.setPersistenceAdapter(new MemoryPersistenceAdapter());
            brokerService.addConnector(tcpConnectorUri);
        } catch (Exception e) {
            throw new RuntimeException("Problem creating brokerService", e);
        }
    }

    @Override
    protected void before() throws Throwable {
        log.info("Starting embedded broker[{}] on {}", brokerId, tcpConnectorUri);
        brokerService.start();
    }

    @Override
    protected void after() {
        try {
            log.info("Stopping embedded broker[{}]", brokerId);
            brokerService.stop();
        } catch (Exception e) {
            throw new RuntimeException("Exception shutting down broker service", e);
        }
    }

    public String getTcpConnectorUri() {
        return tcpConnectorUri;
    }
}
