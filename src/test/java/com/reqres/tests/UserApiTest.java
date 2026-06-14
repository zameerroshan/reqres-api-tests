package com.reqres.tests;

import com.reqres.models.CreateUserRequest;
import com.reqres.utils.RestAssuredSetup;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * UserApiTest — validates the /api/users endpoint.
 *
 * Tests:
 *   TC-API-01 : GET  /users?page=2  → 200 + list structure validated
 *   TC-API-02 : POST /users         → 201 + created resource fields validated
 *   TC-API-05 : GET  /users/999     → 404  (resource not found — distinct from validation failure)
 */
public class UserApiTest {

    @BeforeClass
    public void setUp() {
        RestAssuredSetup.getRequestSpec();
    }

    // ── TC-API-01: GET Users List (Success) ──────────────────────────────────

    /**
     * Assertions:
     *   - Status 200
     *   - "page" field equals 2 (matches query param)
     *   - "data" array is not empty
     *   - First user has id, email (containing @), first_name, last_name
     *   - "total" is a positive integer
     */
    @Test(description = "TC-API-01: GET /users?page=2 returns 200 with valid user list")
    public void testGetUsersSuccess() {
        given()
            .spec(RestAssuredSetup.getRequestSpec())
            .queryParam("page", 2)
        .when()
            .get("/users")
        .then()
            .statusCode(200)
            .body("page",              equalTo(2))
            .body("data",              not(empty()))
            .body("data[0].id",        notNullValue())
            .body("data[0].email",     containsString("@"))
            .body("data[0].first_name",not(emptyOrNullString()))
            .body("data[0].last_name", not(emptyOrNullString()))
            .body("total",             greaterThan(0));
    }

    // ── TC-API-02: POST Create User (Success) ────────────────────────────────

    /**
     * Assertions:
     *   - Status 201
     *   - name and job in response match what was sent
     *   - id is present (server-assigned, non-empty string)
     *   - createdAt is present
     */
    @Test(description = "TC-API-02: POST /users returns 201 with created resource details")
    public void testCreateUserSuccess() {
        CreateUserRequest body = new CreateUserRequest("Zameer", "QA Engineer");

        Response response =
            given()
                .spec(RestAssuredSetup.getRequestSpec())
                .body(body)
            .when()
                .post("/users")
            .then()
                .statusCode(201)
                .body("name",      equalTo("Zameer"))
                .body("job",       equalTo("QA Engineer"))
                .body("id",        notNullValue())
                .body("createdAt", notNullValue())
                .extract().response();

        String createdId = response.jsonPath().getString("id");
        Assert.assertNotNull(createdId, "Created user ID should not be null");
        Assert.assertFalse(createdId.isEmpty(), "Created user ID should not be empty");
    }

    // ── TC-API-05: GET Non-existent User (Resource Not Found) ────────────────

    /**
     * NOTE: This is a resource-not-found test (404), NOT a validation failure test.
     * The true validation failure test (missing required field → 400 + error body)
     * is in RegisterApiTest → testRegisterMissingPassword().
     *
     * Assertions:
     *   - Status 404
     *   - Response body is an empty object {}
     */
    @Test(description = "TC-API-05: GET /users/999 returns 404 for non-existent user")
    public void testGetSingleUserNotFound() {
        Response response =
            given()
                .spec(RestAssuredSetup.getRequestSpec())
            .when()
                .get("/users/999")
            .then()
                .statusCode(404)
                .extract().response();

        String body = response.getBody().asString().trim();
        Assert.assertEquals(body, "{}",
                "Response body should be an empty JSON object for non-existent user");
    }
}
