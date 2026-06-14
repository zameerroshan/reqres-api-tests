package com.reqres.tests;

import com.reqres.models.RegisterRequest;
import com.reqres.utils.RestAssuredSetup;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * RegisterApiTest — validates the POST /api/register endpoint.
 *
 * Why this endpoint for the failure test?
 * ─────────────────────────────────────────────────────────────────
 * The assignment specifies: "missing field, bad input, or unauthorized request"
 *
 * GET /users/999 returning 404 is a not-found scenario — it only proves the
 * server rejects unknown IDs. It does not test input validation.
 *
 * POST /register with a missing password proves the server:
 *   1. Parses the request body
 *   2. Detects a required field is absent
 *   3. Returns 400 (not 404) with a structured error message
 *
 * That is a true validation failure — and it mirrors real finance app testing
 * where a transaction missing an amount field must be explicitly rejected.
 *
 * Tests in this class:
 *   TC-API-03 : Successful registration with valid email + password → 200
 *   TC-API-04 : Registration with missing password field        → 400 + error message
 */
public class RegisterApiTest {

    @BeforeClass
    public void setUp() {
        RestAssuredSetup.getRequestSpec();
    }

    // ── TC-API-03: Successful Registration ──────────────────────────────────

    /**
     * POST /api/register with a valid email + password.
     *
     * Reqres.in only accepts pre-seeded emails. Using the documented test email.
     *
     * Assertions:
     *   - Status code is 200
     *   - Response contains "id" (integer, assigned by server)
     *   - Response contains "token" (non-empty string)
     *   - Response does NOT contain an "error" field
     */
    @Test(description = "TC-API-03: Valid registration returns 200 with id and token")
    public void testRegisterSuccess() {
        RegisterRequest body = new RegisterRequest(
                "eve.holt@reqres.in",
                "pistol"
        );

        given()
            .spec(RestAssuredSetup.getRequestSpec())
            .body(body)
        .when()
            .post("/register")
        .then()
            .statusCode(200)
            .body("id",    notNullValue())
            .body("token", not(emptyOrNullString()))
            .body("$",     not(hasKey("error")));   // no error field on success
    }

    // ── TC-API-04: Missing Password — True Validation Failure ────────────────

    /**
     * POST /api/register with email supplied but password field entirely absent.
     *
     * This is a genuine input-validation failure, not a not-found scenario.
     * The server must detect the missing required field and reject the request
     * with a 400 and a descriptive error message in the body.
     *
     * Request body sent:
     *   { "email": "eve.holt@reqres.in" }          ← password intentionally missing
     *
     * Assertions:
     *   - Status code is 400 (Bad Request — invalid input, not missing resource)
     *   - Response body contains "error" key
     *   - Error message is exactly "Missing password"
     *   - Response does NOT contain "id" or "token" (no partial success)
     */
    @Test(description = "TC-API-04: Registration with missing password returns 400 and error message")
    public void testRegisterMissingPassword() {
        // Factory method on the POJO omits the password field from JSON serialisation
        RegisterRequest body = RegisterRequest.withEmailOnly("eve.holt@reqres.in");

        Response response =
            given()
                .spec(RestAssuredSetup.getRequestSpec())
                .body(body)
            .when()
                .post("/register")
            .then()
                .statusCode(400)
                .body("error", equalTo("Missing password"))
                .body("$", not(hasKey("id")))       // no id returned on failure
                .body("$", not(hasKey("token")))    // no token returned on failure
                .extract().response();

        // Programmatic re-assertion for clarity in test report output
        String errorMessage = response.jsonPath().getString("error");
        Assert.assertEquals(
                errorMessage,
                "Missing password",
                "Error body should say 'Missing password' when password field is absent"
        );
    }
}
