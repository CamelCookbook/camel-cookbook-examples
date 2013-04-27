package org.camelcookbook.examples.testing.notifybuilder;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.test.junit4.CamelSpringJUnit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class that demonstrates the fundamental interactions going on to verify that a route behaves as it should.
 */
@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration({"/META-INF/spring/fixedEndpoints-context.xml",
        "/spring/notifybuilder/test-jms-context.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FixedEndpointsNotifyBuilderSpringTest {

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Test
    public void testPayloadIsTransformed() throws InterruptedException {
        assertNotNull(jmsTemplate);

        final String messageText = "testMessage";

        NotifyBuilder notify = new NotifyBuilder(camelContext)
                .from("activemq:in")
                .whenDone(1)
                .create();

        sendMessageBody(messageText);
        assertTrue(notify.matches(10, TimeUnit.SECONDS));
    }

    private void sendMessageBody(final String messageText) {
        jmsTemplate.send("in", new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage(messageText);
                return textMessage;
            }
        });
    }

}
