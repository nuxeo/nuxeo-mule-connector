/**
 * This file was automatically generated by the Mule Development Kit
 */
package org.nuxeo.mule;

import java.util.Map;

import org.junit.Test;
import org.mule.api.MuleEvent;
import org.mule.construct.Flow;
import org.mule.tck.AbstractMuleTestCase;
import org.mule.tck.FunctionalTestCase;
import org.nuxeo.ecm.automation.client.model.Document;

public class NuxeoConnectorTest extends FunctionalTestCase {
    @Override
    protected String getConfigResources() {
        return "mule-config.xml";
    }

    @Test
    public void testSimpleFlow() throws Exception {
        Flow flow = lookupFlowConstruct("nuxeoTestFlow1");
        MuleEvent event = AbstractMuleTestCase.getTestEvent(null);
        MuleEvent responseEvent = flow.process(event);
        Document doc = (Document) responseEvent.getMessage().getPayload();
        assertEquals("Mule Workspace", doc.getTitle());
    }

    @Test
    public void testSimpleFlowWithTransformer() throws Exception {
        Flow flow = lookupFlowConstruct("nuxeoTestFlowWithConverter");
        MuleEvent event = AbstractMuleTestCase.getTestEvent(null);
        MuleEvent responseEvent = flow.process(event);
        Map<String, Object> map = (Map<String, Object>) responseEvent.getMessage().getPayload();
        assertEquals("Mule Workspace", map.get("dc:title"));
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
