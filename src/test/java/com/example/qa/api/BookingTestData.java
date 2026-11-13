package com.example.qa.api;

import com.example.qa.api.model.Booking;
import com.example.qa.api.model.BookingDates;

import java.time.LocalDate;
import java.util.UUID;

final class BookingTestData {

    private BookingTestData() {
    }

    /** A valid booking with unique names so parallel/repeated runs never get confused. */
    static Booking randomBooking() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        LocalDate checkin = LocalDate.now().plusDays(10);
        return new Booking(
                "Jim" + suffix,
                "Brown" + suffix,
                150 + (int) (Math.random() * 500),
                true,
                new BookingDates(checkin.toString(), checkin.plusDays(5).toString()),
                "Breakfast");
    }
}
