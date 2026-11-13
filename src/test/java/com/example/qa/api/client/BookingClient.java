package com.example.qa.api.client;

import com.example.qa.api.model.Booking;
import com.example.qa.config.TestConfig;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/** Thin wrapper around the Restful Booker endpoints so tests read as intent, not HTTP plumbing. */
public class BookingClient {

    private final RequestSpecification spec;

    public BookingClient() {
        int timeout = TestConfig.apiTimeoutMs();
        RestAssuredConfig config = RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", timeout)
                .setParam("http.socket.timeout", timeout));

        this.spec = given()
                .config(config)
                .baseUri(TestConfig.apiBaseUrl())
                .contentType(ContentType.JSON)
                // GET /booking/{id} answers 418 unless an Accept header is sent
                .header("Accept", "application/json")
                .log().ifValidationFails();
    }

    public Response create(Booking booking) {
        return given().spec(spec).body(booking).post("/booking");
    }

    public Response get(int bookingId) {
        return given().spec(spec).get("/booking/{id}", bookingId);
    }
}
