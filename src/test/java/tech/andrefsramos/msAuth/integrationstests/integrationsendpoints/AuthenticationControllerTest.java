package tech.andrefsramos.msAuth.integrationstests.integrationsendpoints;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tech.andrefsramos.msAuth.configurations.TestConfiguration;
import tech.andrefsramos.msAuth.dtos.SignInResponseDTO;
import tech.andrefsramos.msAuth.dtos.UserSignInDTO;
import tech.andrefsramos.msAuth.integrationstests.testcontainers.AbstractIntegrationTest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
public class AuthenticationControllerTest extends AbstractIntegrationTest {
    private static final String SIGN_IN = "/api/auth/v1/sign-in";
    private static final String AUTH_BY_REFRESH_TOKEN = "/api/auth/v1/auth-by-refresh-token";
    private static final String VALIDATE_REQUEST_TOKEN = "/api/auth/v1/validate-request-token";
    private static RequestSpecification requestSpecification;
    private static SignInResponseDTO tokenADMIN;

    @Test
    @Order(1)
    public void authenticationByUsernameAndPassword() {
        tokenADMIN = given()
                .basePath(SIGN_IN)
                .port(TestConfiguration.SERVER_PORT)
                .contentType(TestConfiguration.CONTENT_TYPE_JSON)
                .body(new UserSignInDTO("admin", "admin123"))
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(SignInResponseDTO.class);

        validateSignResponseDTO();
    }

    @Test
    @Order(2)
    public void authenticationByRefreshToken() {
        tokenADMIN = given()
                .header(TestConfiguration.HEADER_PARAM_AUTHORIZATION, tokenADMIN.getRefreshToken())
                .basePath(AUTH_BY_REFRESH_TOKEN)
                .port(TestConfiguration.SERVER_PORT)
                .contentType(TestConfiguration.CONTENT_TYPE_JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(SignInResponseDTO.class);

        validateSignResponseDTO();
    }

    @Test
    @Order(3)
    public void validateRequestToken() {
        requestSpecification = new RequestSpecBuilder()
                .addHeader(TestConfiguration.HEADER_PARAM_ORIGEN, TestConfiguration.ORIGEN_LOCAL_HOST)
                .addHeader(TestConfiguration.HEADER_PARAM_AUTHORIZATION, tokenADMIN.getAccessToken())
                .setBasePath(VALIDATE_REQUEST_TOKEN)
                .setPort(TestConfiguration.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        given().spec(requestSpecification)
                .contentType(TestConfiguration.CONTENT_TYPE_JSON)
                .when()
                .get()
                .then()
                .statusCode(204);
    }

    @Test
    @Order(4)
    public void validateRequestTokenInvalidToken() {
        requestSpecification = new RequestSpecBuilder()
                .addHeader(TestConfiguration.HEADER_PARAM_AUTHORIZATION, "Test with invalid token")
                .addHeader(TestConfiguration.HEADER_PARAM_ORIGEN, TestConfiguration.ORIGEN_LOCAL_HOST)
                .setBasePath(VALIDATE_REQUEST_TOKEN)
                .setPort(TestConfiguration.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        String content = given().spec(requestSpecification)
                .contentType(TestConfiguration.CONTENT_TYPE_JSON)
                .when()
                .get()
                .then()
                .statusCode(401)
                .extract()
                .body()
                .asString();

        assertTrue(content.contains("Expired or invalid JWT token!"));
    }

    @Test
    @Order(5)
    public void authenticationByUsernameAndPasswordInvalid() {
        var content = given()
                .basePath(SIGN_IN)
                .port(TestConfiguration.SERVER_PORT)
                .contentType(TestConfiguration.CONTENT_TYPE_JSON)
                .body(new UserSignInDTO("invalidusername", "invalidpassword"))
                .when()
                .post()
                .then()
                .statusCode(401)
                .extract()
                .body()
                .asString();

        assertTrue(content.contains("invalid username/password supplied!"));
    }

    @Test
    @Order(6)
    public void authenticationByUsernameNulOrEmpty() {
        var content = given()
                .basePath(SIGN_IN)
                .port(TestConfiguration.SERVER_PORT)
                .contentType(TestConfiguration.CONTENT_TYPE_JSON)
                .body(new UserSignInDTO(null, "admin123"))
                .when()
                .post()
                .then()
                .statusCode(400)
                .extract()
                .body()
                .asString();

        assertTrue(content.contains("The userName field is mandatory."));
    }

    @Test
    @Order(7)
    public void authenticationByPasswordNulOrEmpty() {
        var content = given()
                .basePath(SIGN_IN)
                .port(TestConfiguration.SERVER_PORT)
                .contentType(TestConfiguration.CONTENT_TYPE_JSON)
                .body(new UserSignInDTO("admin", null))
                .when()
                .post()
                .then()
                .statusCode(400)
                .extract()
                .body()
                .asString();

        assertTrue(content.contains("The password field is mandatory."));
    }

    private void validateSignResponseDTO() {
        assertNotNull(tokenADMIN);
        assertNotNull(tokenADMIN.getUserName());
        assertTrue(tokenADMIN.getAuthenticated());
        assertNotNull(tokenADMIN.getCreated());
        assertNotNull(tokenADMIN.getExpiration());
        assertNotNull(tokenADMIN.getAccessToken());
        assertNotNull(tokenADMIN.getRefreshToken());
    }
}
