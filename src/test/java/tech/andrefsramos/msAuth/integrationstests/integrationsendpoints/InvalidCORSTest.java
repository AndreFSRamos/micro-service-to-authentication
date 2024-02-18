package tech.andrefsramos.msAuth.integrationstests.integrationsendpoints;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tech.andrefsramos.msAuth.configurations.TestConfiguration;
import tech.andrefsramos.msAuth.dtos.SignInResponseDTO;
import tech.andrefsramos.msAuth.dtos.UserSignInDTO;
import tech.andrefsramos.msAuth.integrationstests.testcontainers.AbstractIntegrationTest;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InvalidCORSTest extends AbstractIntegrationTest {
    private static final List<String> ENDPOINTS = Arrays.asList(
            "/api/auth/v1/sign-in", // #0
            "/api/user/v1/find-all", //#1
            "/api/user/v1/find-by-id/", //#2
            "/api/user/v1/sign-out", //#3
            "/api/user/v1/update-password", //#4
            "/api/user/v1/update-status/", //#5
            "/api/user/v1/update-role/", //#6
            "/api/user/v1/delete/", //#7
            "/api/auth/v1/validate-request-token" //#8
    );

    private static RequestSpecification specificationInvalidCORS;

    @Test
    @Order(1)
    public void authentication() {
        String accessTokenADMIN = given()
                .basePath(ENDPOINTS.get(0))
                .port(TestConfiguration.SERVER_PORT)
                .contentType(TestConfiguration.CONTENT_TYPE_JSON)
                .body(new UserSignInDTO("admin", "admin123"))
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(SignInResponseDTO.class)
                .getAccessToken();

        specificationInvalidCORS = new RequestSpecBuilder()
                .addHeader(TestConfiguration.HEADER_PARAM_ORIGEN, TestConfiguration.ORIGEN_ERROR)
                .addHeader(TestConfiguration.HEADER_PARAM_AUTHORIZATION, accessTokenADMIN)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(2)
    public void accessDeniedForInvalidCORDS() {
        ENDPOINTS.forEach(endpoint ->
            given().spec(specificationInvalidCORS)
                    .basePath(endpoint)
                    .port(TestConfiguration.SERVER_PORT)
                    .contentType(TestConfiguration.CONTENT_TYPE_JSON)
                    .when()
                    .get()
                    .then()
                    .statusCode(403)
        );
    }

}
