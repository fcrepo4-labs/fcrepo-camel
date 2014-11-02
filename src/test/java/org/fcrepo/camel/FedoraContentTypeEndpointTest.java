package org.fcrepo.camel;

import org.apache.camel.Produce;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class FedoraContentTypeEndpointTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    @Test
    public void testContentTypeTurtle() throws Exception {
        template.sendBody(null);

        resultEndpoint.expectedHeaderReceived("Content-Type", "text/turtle");
        resultEndpoint.expectedMessageCount(1);

        resultEndpoint.assertIsSatisfied();
    }

    @Test
    public void testContentTypeN3() throws Exception {
        template.sendBodyAndHeader(null, "Content-Type", "application/n-triples");

        resultEndpoint.expectedHeaderReceived("Content-Type", "text/turtle");
        resultEndpoint.expectedMessageCount(1);

        resultEndpoint.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws IOException {
                Properties props = new Properties();

                InputStream in = getClass().getResourceAsStream("/org.fcrepo.properties");
                props.load(in);
                in.close();

                String fcrepo_url = props.getProperty("fcrepo.url").replaceAll("http://", "");

                from("direct:start")
                    .to("fcrepo:" + fcrepo_url + "?contentType=text/turtle")
                    .to("mock:result");
            }
        };
    }
}

