package svm.simbirsoft.helpers;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class BaseRequests {
    private static final String BASE_URL = "http://localhost:8080";
    private static RequestSpecification baseSpec;

    static {
        baseSpec = new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .build();
    }

    public static Response post(String endpoint, Object body) {
        return RestAssured.given()
                .spec(baseSpec)
                .body(body)
                .post(endpoint);
    }

    public static Response get(String endpoint) {
        return RestAssured.given()
                .spec(baseSpec)
                .get(endpoint);
    }

    public static Response delete(String endpoint) {
        return RestAssured.given()
                .spec(baseSpec)
                .delete(endpoint);
    }

    public static Response patch(String endpoint, Object body) {
        return RestAssured.given()
                .spec(baseSpec)
                .body(body)
                .patch(endpoint);
    }
}