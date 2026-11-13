package com.example.qa.api;

import com.example.qa.api.client.BookingClient;
import com.example.qa.api.model.Booking;
import com.example.qa.api.model.BookingCreatedResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("api")
class BookingApiTest {

    private final BookingClient client = new BookingClient();

    @Test
    @DisplayName("Test 3: created booking can be retrieved and matches the request")
    void createdBookingCanBeRetrieved() {
        Booking expected = BookingTestData.randomBooking();

        // 1-2. create + status
        BookingCreatedResponse created = client.create(expected)
                .then().statusCode(200)
                .extract().as(BookingCreatedResponse.class);

        // 3. booking id
        int bookingId = created.bookingid();
        assertThat(bookingId).as("booking id").isPositive();

        // the create response echoes the saved booking
        assertThat(created.booking()).usingRecursiveComparison().isEqualTo(expected);

        // 4-5. fetch and compare every field:
        // firstname, lastname, totalprice, depositpaid, bookingdates (+ additionalneeds)
        Booking actual = client.get(bookingId)
                .then().statusCode(200)
                .extract().as(Booking.class);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Extra: unknown booking id returns 404")
    void unknownBookingReturnsNotFound() {
        client.get(Integer.MAX_VALUE)
                .then().statusCode(404);
    }
}
