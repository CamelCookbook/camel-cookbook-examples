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
package org.camelcookbook.runtimes.swarm;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.model.rest.RestBindingMode;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.container.runtime.cdi.ConfigViewProducingExtension;
import org.wildfly.swarm.container.runtime.cdi.ConfigurationValueProducer;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

@ApplicationScoped
@ContextName("cdi-context")
public class CamelRoutes extends RouteBuilder {

    @Inject
    @ConfigurationValue("swarm.camel.foo.name")
    private String name;

    @Override
    public void configure() throws Exception {

        restConfiguration().component("undertow")
            .contextPath("/rest").bindingMode(RestBindingMode.auto);

        rest()
            .get("/greetings/{name}").produces("text/plain")
                .route().transform(simple("Hello ${header.name} from " + name));
    }

}
