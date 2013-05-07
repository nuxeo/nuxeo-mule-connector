/**
 * This file was automatically generated by the Mule Development Kit
 */
package org.nuxeo.mule;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleEvent;
import org.mule.construct.Flow;
import org.mule.tck.AbstractMuleTestCase;
import org.mule.tck.FunctionalTestCase;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.Documents;
import org.nuxeo.ecm.automation.test.EmbeddedAutomationServerFeature;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Deploy({ "org.nuxeo.ecm.platform.url.api", "org.nuxeo.ecm.platform.url.core",
        "org.nuxeo.ecm.platform.types.api", "org.nuxeo.ecm.platform.types.core" })
@Features(EmbeddedAutomationServerFeature.class)
@Jetty(port = 18080)
@RepositoryConfig(cleanup = Granularity.CLASS)
public class NuxeoConnectorTest extends FunctionalTestCase {

    @Override
    protected String getConfigResources() {
        return "mule-config.xml";
    }

    @Before
    public void setupMule() throws Exception {
        super.setUp();
    }

    @Inject
    HttpAutomationClient client;

    /**
     * Check that embedded Nuxeo server is actually started and able to respond
     * to Automation Calls
     *
     * @throws Exception
     */
    @Test
    public void testPingServer() throws Exception {

        Session session = client.getSession("Administrator", "Administrator");
        assertNotNull(session);
        System.out.print("Automation server started on " + client.getBaseUrl());
        System.out.print(session.getOperations().size()
                + " operations are deployed on the Nuxeo Server");
    }

    // @Test
    public void testDumpRepo() throws Exception {
        Flow flow = lookupFlowConstruct("nuxeoTestFlow1");
        MuleEvent event = AbstractMuleTestCase.getTestEvent(null);
        MuleEvent responseEvent = flow.process(event);
        Documents docs = (Documents) responseEvent.getMessage().getPayload();

        for (Document doc : docs) {
            System.out.println("-> " + doc.getTitle() + " - " + doc.getType()
                    + " - " + doc.getPath());
        }
    }

    /**
     * Runs a simple Flow and verify the output
     *
     * @throws Exception
     *
     */
    @Test
    public void testSimpleFlow() throws Exception {
        Flow flow = lookupFlowConstruct("nuxeoTestFlow1");
        MuleEvent event = AbstractMuleTestCase.getTestEvent(null);
        MuleEvent responseEvent = flow.process(event);
        Document doc = (Document) responseEvent.getMessage().getPayload();

        assertEquals("Mule Workspace", doc.getTitle());
        assertEquals("Some nice description", doc.getString("dc:description"));
    }

    /**
     * Runs a simple Flow with Converters and verify the output
     *
     * @throws Exception
     * @since 5.7
     */
    @Test
    public void testSimpleFlowWithTransformer() throws Exception {
        Flow flow = lookupFlowConstruct("nuxeoTestFlowWithConverter");
        MuleEvent event = AbstractMuleTestCase.getTestEvent(null);
        MuleEvent responseEvent = flow.process(event);
        Map<String, Object> map = (Map<String, Object>) responseEvent.getMessage().getPayload();
        assertEquals("Mule Workspace", map.get("dc:title"));
        assertEquals("Some nice description", map.get("dc:description"));
    }

    /**
     * Run the flow specified by name and assert equality on the expected output
     *
     * @param flowName The name of the flow to run
     * @param expect The expected output
     */
    protected <T> void runFlowAndExpect(String flowName, T expect)
            throws Exception {
        Flow flow = lookupFlowConstruct(flowName);
        MuleEvent event = AbstractMuleTestCase.getTestEvent(null);
        MuleEvent responseEvent = flow.process(event);

        assertEquals(expect, responseEvent.getMessage().getPayload());
    }

    /**
     * Run the flow specified by name using the specified payload and assert
     * equality on the expected output
     *
     * @param flowName The name of the flow to run
     * @param expect The expected output
     * @param payload The payload of the input event
     */
    protected <T, U> void runFlowWithPayloadAndExpect(String flowName,
            T expect, U payload) throws Exception {
        Flow flow = lookupFlowConstruct(flowName);
        MuleEvent event = AbstractMuleTestCase.getTestEvent(payload);
        MuleEvent responseEvent = flow.process(event);

        assertEquals(expect, responseEvent.getMessage().getPayload());
    }

    /**
     * Retrieve a flow by name from the registry
     *
     * @param name Name of the flow to retrieve
     */
    protected Flow lookupFlowConstruct(String name) {
        return (Flow) AbstractMuleTestCase.muleContext.getRegistry().lookupFlowConstruct(
                name);
    }
}
