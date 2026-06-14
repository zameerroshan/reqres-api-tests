package com.reqres.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body POJO for POST /api/register
 *
 * @JsonInclude(NON_NULL) ensures null fields are omitted from the serialised JSON.
 * This lets us intentionally send an incomplete body (missing password)
 * without adding a separate raw-map approach in the test.
 *
 * Full body  → { "email": "...", "password": "..." }
 * Missing PW → { "email": "..." }               (password field simply not set)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterRequest {

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    public RegisterRequest() {}

    /** Full registration — both fields present. */
    public RegisterRequest(String email, String password) {
        this.email    = email;
        this.password = password;
    }

    /** Partial registration — password intentionally omitted. */
    public static RegisterRequest withEmailOnly(String email) {
        RegisterRequest r = new RegisterRequest();
        r.email = email;
        return r;
    }

    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public void setEmail(String email)       { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}
