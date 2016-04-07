package com.ameliant.training.day3;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.springframework.jms.connection.JmsTransactionManager;

public class TransactedJmsTxMgrRouteTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:out")
    MockEndpoint mockOut;

    @EndpointInject(uri = "mock:dlq")
    MockEndpoint mockDlq;

    @Override
    protected CamelContext createCamelContext() throws Exception {

        ActiveMQConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL("vm://myEmbeddedBroker?broker.persistent=false");

        JmsTransactionManager transactionManager = new JmsTransactionManager();
        transactionManager.setConnectionFactory(connectionFactory);

        ActiveMQComponent activeMQComponent = new ActiveMQComponent();
        activeMQComponent.setConnectionFactory(connectionFactory);
        activeMQComponent.setTransactionManager(transactionManager);

        SpringTransactionPolicy transactionPolicy = new SpringTransactionPolicy();
        transactionPolicy.setTransactionManager(transactionManager);
        transactionPolicy.setPropagationBehaviorName("PROPAGATION_REQUIRED");

        SimpleRegistry registry = new SimpleRegistry();
        registry.put("PROPAGATION_REQUIRED", transactionPolicy);

        CamelContext context = new DefaultCamelContext(registry);
        context.addComponent("jms", activeMQComponent);
        return context;
    }

    @Override
    protected RoutesBuilder[] createRouteBuilders() throws Exception {
        return new RouteBuilder[] {
            new TransactedJmsTxMgrRoute(), // the route under test
            new RouteBuilder() { // test harness
                @Override
                public void configure() throws Exception {
                    from("direct:in").routeId("harness.in")
                        .to("jms:in");

                    from("jms:out").routeId("harness.out")
                        .to("mock:out");

                    from("jms:ActiveMQ.DLQ").routeId("harness.dlq")
                            .to("mock:dlq");
                }
            }
        };
    }

    @Test
    public void testRoute() throws InterruptedException {
        mockDlq.setExpectedMessageCount(1);
        mockOut.setExpectedMessageCount(0);

        template.sendBody("direct:in", "Oslo");

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRoute_exception() throws InterruptedException {
        mockDlq.setExpectedMessageCount(1);
        mockDlq.expectedBodiesReceived("Boom");

        template.sendBody("direct:in", "Boom");

        assertMockEndpointsSatisfied();
    }
}
