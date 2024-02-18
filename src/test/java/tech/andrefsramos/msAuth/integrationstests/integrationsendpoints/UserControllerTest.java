package tech.andrefsramos.msAuth.integrationstests.integrationsendpoints;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tech.andrefsramos.msAuth.configurations.TestConfiguration;
import tech.andrefsramos.msAuth.dtos.*;
import tech.andrefsramos.msAuth.integrationstests.testcontainers.AbstractIntegrationTest;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.sessionId;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest extends AbstractIntegrationTest {
    private static final List<String> ENDPOINTS = Arrays.asList(
            "/api/auth/v1/sign-in", // #0
            "/api/user/v1/find-all", //#1
            "/api/user/v1/find-by-id/", //#2
            "/api/user/v1/sign-out", //#3
            "/api/user/v1/update-password", //#4
            "/api/user/v1/update-status/", //#5
            "/api/user/v1/update-role/", //#6
            "/api/user/v1/delete/" //#7
    );
    private static RequestSpecification specification;
    private static RequestSpecification specificationUserMANAGER;
    private static UserDTO userDTOCOMMON;
    private static UserDTO userDTOMANAGER;

    @Test
    @Order(1)
    public void authenticationUserADMIN() {
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

        specification = new RequestSpecBuilder()
                .addHeader(TestConfiguration.HEADER_PARAM_ORIGEN, TestConfiguration.ORIGEN_LOCAL_HOST)
                .addHeader(TestConfiguration.HEADER_PARAM_AUTHORIZATION, accessTokenADMIN)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(2)
    public void createNewUserWithAdminAuthorization() {
        userDTOMANAGER = given().spec(specification)
                .basePath(ENDPOINTS.get(3))
                .port(TestConfiguration.SERVER_PORT)
                .contentType(TestConfiguration.CONTENT_TYPE_JSON)
                .body(new UserSignOutDTO("user.manager", "7891011"))
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .body()
                .as(UserDTO.class);

        validateCreateUser(userDTOMANAGER);
    }

    @Test
    @Order(3)
    public void updateRoleUser() {
        given().spec(specification)
                .basePath(ENDPOINTS.get(6) + userDTOMANAGER.getId() + "/MANAGER")
                .port(TestConfiguration.SERVER_PORT)
                .contentType(TestConfiguration.CONTENT_TYPE_JSON)
                .when()
                .put()
                .then()
                .statusCode(204);
    }

    @Test
    @Order(4)
    public void authenticationUserMANAGER() {
        SignInResponseDTO tokenMANAGER = given()
                .basePath(ENDPOINTS.get(0))
                .port(TestConfiguration.SERVER_PORT)
                .contentType(TestConfiguration.CONTENT_TYPE_JSON)
                .body(new UserSignInDTO("user.manager", "7891011"))
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(SignInResponseDTO.class);

        validateSignResponseDTO(tokenMANAGER);

        specificationUserMANAGER = new RequestSpecBuilder()
                .addHeader(TestConfiguration.HEADER_PARAM_ORIGEN, TestConfiguration.ORIGEN_LOCAL_HOST)
                .addHeader(TestConfiguration.HEADER_PARAM_AUTHORIZATION, tokenMANAGER.getAccessToken())
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Test
    @Order(5)
    public void createNewUserWithManagerAuthorization() {
        userDTOCOMMON = given().spec(specificationUserMANAGER)
                .basePath(ENDPOINTS.get(3))
                .port(TestConfiguration.SERVER_PORT)
                .contentType(TestConfiguration.CONTENT_TYPE_JSON)
                .body(new UserSignOutDTO("user.common", "123456"))
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .body()
                .as(UserDTO.class);

        validateCreateUser(userDTOCOMMON);
    }

    @Test
    @Order(6)
    public void updateStatusUser() {
        given().spec(specification)
                .basePath(ENDPOINTS.get(5) + userDTOCOMMON.getId())
                .port(TestConfiguration.SERVER_PORT)
                .contentType(TestConfiguration.CONTENT_TYPE_JSON)
                .when()
                .put()
                .then()
                .statusCode(204);
    }

    @Test
    @Order(7)
    public void updatePasswordUser() {
        given().spec(specification)
                .basePath(ENDPOINTS.get(4))
                .port(TestConfiguration.SERVER_PORT)
                .contentType(TestConfiguration.CONTENT_TYPE_JSON)
                .when()
                .body(new UpdatePasswordDTO(userDTOCOMMON.getId(), "newPassword"))
                .post()
                .then()
                .statusCode(204);
    }

    @Test
    @Order(8)
    public void findUserById() {
        UserDTO content = given().spec(specification)
                .basePath(ENDPOINTS.get(2) + userDTOMANAGER.getId())
                .port(TestConfiguration.SERVER_PORT)
                .contentType(TestConfiguration.CONTENT_TYPE_JSON)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(UserDTO.class);

        validateCreateUser(content);
    }

    @Test
    @Order(9)
    public void findAllUsers() {
        given().spec(specification)
                .basePath(ENDPOINTS.get(1))
                .port(TestConfiguration.SERVER_PORT)
                .contentType(TestConfiguration.CONTENT_TYPE_JSON)
                .when()
                .get()
                .then()
                .statusCode(200);
    }

    @Test
    @Order(10)
    public void updateStatusUserUnauthorized() {
        given().spec(specificationUserMANAGER)
                .basePath(ENDPOINTS.get(5) + userDTOCOMMON.getId())
                .port(TestConfiguration.SERVER_PORT)
                .contentType(TestConfiguration.CONTENT_TYPE_JSON)
                .when()
                .put()
                .then()
                .statusCode(403);
    }

    @Test
    @Order(11)
    public void updatePasswordUserUnauthorized() {
        given().spec(specificationUserMANAGER)
                .basePath(ENDPOINTS.get(4))
                .port(TestConfiguration.SERVER_PORT)
                .contentType(TestConfiguration.CONTENT_TYPE_JSON)
                .when()
                .body(new UpdatePasswordDTO(userDTOCOMMON.getId(), "newPassword"))
                .post()
                .then()
                .statusCode(403);
    }

    @Test
    @Order(12)
    public void updateRoleUserUnauthorized() {
        given().spec(specificationUserMANAGER)
                .basePath(ENDPOINTS.get(6) + userDTOMANAGER.getId() + "/MANAGER")
                .port(TestConfiguration.SERVER_PORT)
                .contentType(TestConfiguration.CONTENT_TYPE_JSON)
                .when()
                .put()
                .then()
                .statusCode(403);
    }

    @Test
    @Order(13)
    public void deleteUserByIdUnauthorized() {
        given().spec(specificationUserMANAGER)
                .basePath(ENDPOINTS.get(7) + userDTOCOMMON.getId())
                .port(TestConfiguration.SERVER_PORT)
                .contentType(TestConfiguration.CONTENT_TYPE_JSON)
                .when()
                .delete()
                .then()
                .statusCode(403);
    }

    @Test
    @Order(14)
    public void updateRoleUserInvalidRole() {
       given().spec(specification)
                .basePath(ENDPOINTS.get(6) + userDTOMANAGER.getId() + "/INVALID_ROLE")
                .port(TestConfiguration.SERVER_PORT)
                .contentType(TestConfiguration.CONTENT_TYPE_JSON)
                .when()
                .put()
                .then()
                .statusCode(400);
    }

    @Test
    @Order(15)
    public void deleteUserById() {
        given().spec(specification)
                .basePath(ENDPOINTS.get(7) + userDTOMANAGER.getId())
                .port(TestConfiguration.SERVER_PORT)
                .contentType(TestConfiguration.CONTENT_TYPE_JSON)
                .when()
                .delete()
                .then()
                .statusCode(204);
    }

    private void validateSignResponseDTO(SignInResponseDTO signInResponseDTO) {
        assertNotNull(signInResponseDTO);
        assertNotNull(signInResponseDTO.getUserName());
        assertTrue(signInResponseDTO.getAuthenticated());
        assertNotNull(signInResponseDTO.getCreated());
        assertNotNull(signInResponseDTO.getExpiration());
        assertNotNull(signInResponseDTO.getAccessToken());
        assertNotNull(signInResponseDTO.getRefreshToken());
    }

    private void validateCreateUser(UserDTO userDTO) {
        assertNotNull(userDTO);
        assertNotNull(userDTO.getUserName());
        assertTrue(userDTO.getEnable());
        assertNotNull(userDTO.getCreated());
        assertNotNull(userDTO.getId());
        assertNotNull(userDTO.getRoles());
    }
}
