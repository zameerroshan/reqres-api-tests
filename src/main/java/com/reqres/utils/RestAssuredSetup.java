package com.reqres.utils;

import com.reqres.config.ApiConfigReader;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RestAssuredSetup {
    public static RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ApiConfigReader.getBaseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addHeader("User-Agent", "PostmanRuntime/7.32.3")
                .addHeader("x-api-key", ApiConfigReader.getApiKey())
                .build();
    }
}
