package nl.axians.camel.oauth2;

import org.apache.camel.Exchange;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.mockserver.model.HttpRequest;
import org.mockserver.verify.VerificationTimes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.Parameter.param;
import static org.mockserver.model.ParameterBody.params;

/**
 * Test that {@link OAuth2Endpoint} uses cached token when not expired
 */
public class CachedTokenTest extends BaseOAuth2Test {

    @Test
    public void Should_Retrieve_Token_From_Cache()
    {
        // Arrange
        HttpRequest request = request().withMethod("POST").withPath("/token");
        mockServer.when(request)
                .respond(response().withStatusCode(200)
                        .withBody("{\"access_token\":\"1234567890\",\"token_type\":\"Bearer\",\"expires_in\":3600}"));

        // Act
        template.sendBody("direct:start", null);
        template.sendBody("direct:start", null);

        // Assert
        mockServer.verify(request, VerificationTimes.exactly(1));
        mockServer.verify(request().withHeader("Authorization", "Basic " + "Y2xpZW50OnNlY3JldA=="));
        mockServer.verify(request().withBody(params(param("scope", "scope"),
                param("grant_type", "client_credentials"))));
        mockServer.verify(request().withHeader("Content-Type", "application/x-www-form-urlencoded"));

        assertThat(getMockEndpoint("mock:result").getExchanges().size()).isEqualTo(2);
        final Exchange exchange = getMockEndpoint("mock:result").getExchanges().getLast();
        assertThat(exchange).isNotNull();
        assertThat(exchange.getIn().getHeader("Authorization")).isEqualTo("Bearer 1234567890");
    }

}
