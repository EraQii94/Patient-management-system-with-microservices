import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class AuthIntegrationTest {

    // Set up the base URI for RestAssured
    @BeforeAll
    static void setup(){
        RestAssured.baseURI = "http://localhost:4004";
    }

    @Test
    public void shouldReturnOKWithValidToken(){
        // 1- arrange
        /// Get a valid token first
        // 2- act
        /// Use the token to call the /validate endpoint
        // 3- assert
        /// Verify the response status is 200 OK


        String loginPayload = """
                {
                    "email": "testuser@test.com",
                    "password": "password123"
                }
                """;

        Response response = given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract()
                .response();


        System.out.println("Generated Token: " + response.jsonPath().getString("token"));

    }

    @Test
    public void shouldReturnUnauthorizedWithValidToken(){
        // 1- arrange
        /// Get a valid token first
        // 2- act
        /// Use the token to call the /validate endpoint
        // 3- assert
        /// Verify the response status is 200 OK


        String loginPayload = """
                {
                    "email": "invalidEmail@test.com",
                    "password": "password123"
                }
                """;

        given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(401);
    }


}
