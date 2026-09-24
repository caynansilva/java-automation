package com.caynan.qa.utils;

import com.caynan.qa.types.RequestType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public final class Helper {

    private Helper() {
        // Utility class - should not be instantiated.
    }

    // =========================================================
    // REQUEST
    // =========================================================

    public static Response sendAPIRequest(
            RequestType method,
            String url,
            Object body,
            Map<String, String> headers
    ) {
        return sendAPIRequest(
                method,
                url,
                body,
                headers,
                null
        );
    }

    public static Response sendAPIRequest(
            RequestType method,
            String url,
            Object body,
            Map<String, String> headers,
            Map<String, ?> queryParams
    ) {

        RequestSpecification request = given();

        if (headers != null && !headers.isEmpty()) {
            request.headers(headers);
        }

        if (queryParams != null && !queryParams.isEmpty()) {
            request.queryParams(queryParams);
        }

        if (body != null) {
            request.body(body);
        }

        return executeRequest(method, url, request);
    }

    private static Response executeRequest(
            RequestType method,
            String url,
            RequestSpecification request
    ) {

        return switch (method) {
            case GET -> request.get(url);
            case POST -> request.post(url);
            case PUT -> request.put(url);
            case PATCH -> request.patch(url);
            case DELETE -> request.delete(url);
        };
    }

    // =========================================================
    // AUTHENTICATION
    // =========================================================

    public static Response sendAPIRequestWithBearerToken(
            RequestType method,
            String url,
            String token,
            Object body,
            Map<String, String> headers
    ) {

        RequestSpecification request = given()
                .auth()
                .oauth2(token);

        if (headers != null && !headers.isEmpty()) {
            request.headers(headers);
        }

        if (body != null) {
            request.body(body);
        }

        return executeRequest(method, url, request);
    }

    // =========================================================
    // RESPONSE BODY
    // =========================================================

    public static String parseResponseToString(Response response) {
        validateResponse(response);

        return response.asString();
    }

    public static String prettyPrintResponse(Response response) {
        validateResponse(response);

        return response.asPrettyString();
    }

    public static JsonPath getJsonPath(Response response) {
        validateResponse(response);

        return response.jsonPath();
    }

    // =========================================================
    // JSON VALUE
    // =========================================================

    public static <T> T getResponseValue(
            Response response,
            String jsonPath
    ) {
        validateResponse(response);

        return response.jsonPath().get(jsonPath);
    }

    public static String getResponseString(
            Response response,
            String jsonPath
    ) {
        validateResponse(response);

        return response.jsonPath().getString(jsonPath);
    }

    public static Integer getResponseInt(
            Response response,
            String jsonPath
    ) {
        validateResponse(response);

        return response.jsonPath().getInt(jsonPath);
    }

    public static Boolean getResponseBoolean(
            Response response,
            String jsonPath
    ) {
        validateResponse(response);

        return response.jsonPath().getBoolean(jsonPath);
    }

    public static <T> List<T> getResponseList(
            Response response,
            String jsonPath
    ) {
        validateResponse(response);

        List<T> result = response.jsonPath().getList(jsonPath);

        return result != null
                ? result
                : Collections.emptyList();
    }

    // =========================================================
    // RESPONSE INFO
    // =========================================================

    public static int getStatusCode(Response response) {
        validateResponse(response);

        return response.statusCode();
    }

    public static String getContentType(Response response) {
        validateResponse(response);

        return response.contentType();
    }

    public static long getResponseTime(Response response) {
        validateResponse(response);

        return response.time();
    }

    public static String getHeader(
            Response response,
            String headerName
    ) {
        validateResponse(response);

        return response.getHeader(headerName);
    }

    public static String getCookie(
            Response response,
            String cookieName
    ) {
        validateResponse(response);

        return response.getCookie(cookieName);
    }

    // =========================================================
    // RESPONSE CONTAINS
    // =========================================================

    public static boolean responseContains(
            Response response,
            String value
    ) {
        validateResponse(response);

        return response.asString().contains(value);
    }

    public static boolean responseContainsKey(
            Response response,
            String jsonPath
    ) {
        validateResponse(response);

        try {
            return response.jsonPath().get(jsonPath) != null;
        } catch (Exception ignored) {
            return false;
        }
    }

    // =========================================================
    // VALIDATIONS
    // =========================================================

    public static void assertStatusCode(
            Response response,
            int expectedStatusCode
    ) {
        validateResponse(response);

        int actualStatusCode = response.statusCode();

        if (actualStatusCode != expectedStatusCode) {
            throw new AssertionError(
                    "Expected status code: "
                            + expectedStatusCode
                            + " but received: "
                            + actualStatusCode
            );
        }
    }

    public static void assertResponseContains(
            Response response,
            String expectedValue
    ) {
        validateResponse(response);

        if (!responseContains(response, expectedValue)) {
            throw new AssertionError(
                    "Response does not contain expected value: "
                            + expectedValue
            );
        }
    }

    public static void assertJsonPathEquals(
            Response response,
            String jsonPath,
            Object expectedValue
    ) {
        validateResponse(response);

        Object actualValue =
                response.jsonPath().get(jsonPath);

        if (expectedValue == null && actualValue == null) {
            return;
        }

        if (expectedValue == null
                || !expectedValue.equals(actualValue)) {

            throw new AssertionError(
                    "JSON Path validation failed."
                            + "\nPath: " + jsonPath
                            + "\nExpected: " + expectedValue
                            + "\nActual: " + actualValue
            );
        }
    }

    public static void assertHeaderExists(
            Response response,
            String headerName
    ) {
        validateResponse(response);

        if (!response.headers().hasHeaderWithName(headerName)) {
            throw new AssertionError(
                    "Expected header was not found: "
                            + headerName
            );
        }
    }

    public static void assertResponseTimeBelow(
            Response response,
            long maxMilliseconds
    ) {
        validateResponse(response);

        long responseTime = response.time();

        if (responseTime > maxMilliseconds) {
            throw new AssertionError(
                    "Response time exceeded expected limit."
                            + "\nMaximum: "
                            + maxMilliseconds
                            + " ms"
                            + "\nActual: "
                            + responseTime
                            + " ms"
            );
        }
    }

    // =========================================================
    // DESERIALIZATION
    // =========================================================

    public static <T> T parseResponse(
            Response response,
            Class<T> type
    ) {
        validateResponse(response);

        return response.as(type);
    }

    // =========================================================
    // INTERNAL VALIDATION
    // =========================================================

    private static void validateResponse(Response response) {

        if (response == null) {
            throw new IllegalArgumentException(
                    "Response cannot be null."
            );
        }
    }
}