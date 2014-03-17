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

package org.camelcookbook.monitoring.naming;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.management.DefaultManagementNamingStrategy;
import org.apache.camel.management.JmxSystemPropertyKeys;
import org.apache.camel.spi.ManagementAgent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class JmxNamingPatternTest {
    private static Logger LOG = LoggerFactory.getLogger(JmxNamingPatternTest.class);

    private JmxNamingPatternCamelApplication camelApp;
    private CamelContext context;
    private ProducerTemplate template;

    @Before
    public void setup() throws Exception {
        // Ensure JVM platform MBean Server will run
        System.setProperty(JmxSystemPropertyKeys.DISABLED, "false");

        camelApp = new JmxNamingPatternCamelApplication();

        camelApp.setup();

        context = camelApp.getContext();
        template = context.createProducerTemplate();

        // Force hostname to be "localhost" for testing purposes
        final DefaultManagementNamingStrategy naming = (DefaultManagementNamingStrategy) context.getManagementStrategy().getManagementNamingStrategy();
        naming.setHostName("localhost");
        naming.setDomainName("org.apache.camel");

        // setup the ManagementAgent to include the hostname
        context.getManagementStrategy().getManagementAgent().setIncludeHostName(true);

        camelApp.start();
    }

    @After
    public void tearDown() throws Exception {
        template = null;
        context = null;

        camelApp.tearDown();

        camelApp = null;
    }

    @Test
    public void testNamingPattern() throws Exception {
        final ManagementAgent managementAgent = context.getManagementStrategy().getManagementAgent();

        assertNotNull(managementAgent);

        final MBeanServer mBeanServer = managementAgent.getMBeanServer();
        assertNotNull(mBeanServer);

        final String mBeanServerDefaultDomain = managementAgent.getMBeanServerDefaultDomain();
        assertEquals("org.apache.camel", mBeanServerDefaultDomain);

        final String managementName = context.getManagementName();
        assertNotNull("CamelContext should have a management name if JMX is enabled", managementName);
        LOG.info("managementName = {}; name = {}", managementName, context.getName());
        assertTrue(managementName.startsWith("CustomName"));

        // Get the Camel Context MBean
        ObjectName onContext = ObjectName.getInstance(mBeanServerDefaultDomain + ":context=localhost/" + managementName + ",type=context,name=\"" + context.getName() + "\"");
        assertTrue("Should be registered", mBeanServer.isRegistered(onContext));

        // Get the first Route MBean by id
        ObjectName onRoute1 = ObjectName.getInstance(mBeanServerDefaultDomain + ":context=localhost/" + managementName + ",type=routes,name=\"first-route\"");
        LOG.info("Canonical Name = {}", onRoute1.getCanonicalName());
        assertTrue("Should be registered", mBeanServer.isRegistered(onRoute1));

        // Send a couple of messages to get some route statistics
        template.sendBody("direct:start", "Hello Camel");
        template.sendBody("direct:start", "Camel Rocks!");

        // Get an MBean attribute for the number of messages processed
        assertEquals(2L, mBeanServer.getAttribute(onRoute1, "ExchangesCompleted"));

        // Get the other Route MBean by id
        ObjectName onRoute2 = ObjectName.getInstance(mBeanServerDefaultDomain + ":context=localhost/" + managementName + ",type=routes,name=\"other-route\"");
        assertTrue("Should be registered", mBeanServer.isRegistered(onRoute2));

        // Get an MBean attribute for the number of messages processed
        assertEquals(0L, mBeanServer.getAttribute(onRoute2, "ExchangesCompleted"));

        // Send some messages to the other route
        template.sendBody("direct:startOther", "Hello Other Camel");
        template.sendBody("direct:startOther", "Other Camel Rocks!");

        // Verify that the MBean statistics updated correctly
        assertEquals(2L, mBeanServer.getAttribute(onRoute2, "ExchangesCompleted"));

        // Check this routes running state
        assertEquals("Started", mBeanServer.getAttribute(onRoute2, "State"));

        // Stop the route via JMX
        mBeanServer.invoke(onRoute2, "stop", null, null);

        // verify the route now shows its state as stopped
        assertEquals("Stopped", mBeanServer.getAttribute(onRoute2, "State"));
    }
}
