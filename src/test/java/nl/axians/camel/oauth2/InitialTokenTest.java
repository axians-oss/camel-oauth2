package nl.axians.camel.oauth2;

import org.apache.camel.Exchange;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.junit.jupiter.api.Test;
import org.mockserver.model.HttpRequest;
import org.mockserver.verify.VerificationTimes;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

/**
 * Test that {@link OAuth2Endpoint} retrieves an initial token when no cached token is available.
 */
public class InitialTokenTest extends BaseOAuth2Test {

    @Test
    public void Should_Retrieve_Initial_Token()
    {
        // Arrange
        HttpRequest request = request().withMethod("POST").withPath("/token");
        mockServer.when(request)
                .respond(response().withStatusCode(200)
                        .withBody("{\"access_token\":\"1234567890\",\"token_type\":\"Bearer\",\"expires_in\":3600}"));

        // Act
        template.sendBody("direct:initialToken", null);

        // Assert
        mockServer.verify(request, VerificationTimes.exactly(1));
        mockServer.verify(request().withHeader("Authorization", "Basic " + "Y2xpZW50OnNlY3JldA=="));
        mockServer.verify(request().withBody("scope=scope&grant_type=client_credentials", StandardCharsets.UTF_8));
        mockServer.verify(request().withHeader("Content-Type", "application/x-www-form-urlencoded"));

        final Exchange exchange = getMockEndpoint("mock:result").getExchanges().getFirst();
        assertThat(exchange).isNotNull();
        assertThat(exchange.getIn().getHeader("Authorization")).isEqualTo("Bearer 1234567890");
    }

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new RouteBuilder() {

            @Override
            public void configure() {
                from("direct:initialToken")
                        .to("oauth2://test?clientId=client&clientSecret=secret&accessTokenUrl=http://localhost:1080/token&scope=scope")
                        .to("mock:result");
            }

        };
    }

}
