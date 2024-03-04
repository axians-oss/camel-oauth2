package nl.axians.camel.oauth2;

import org.apache.camel.test.junit5.CamelTestSupport;
import org.mockserver.integration.ClientAndServer;

/**
 * Base class for OAuth2 tests.
 */
public abstract class BaseOAuth2Test extends CamelTestSupport {

    protected ClientAndServer mockServer;
    protected int mockServerPort = 1080;

    @Override
    protected void doPreSetup() throws Exception {
        super.doPreSetup();
        mockServer = ClientAndServer.startClientAndServer(mockServerPort);
    }

    @Override
    protected void doPostTearDown() throws Exception {
        super.doPostTearDown();
        mockServer.stop();
    }

}
