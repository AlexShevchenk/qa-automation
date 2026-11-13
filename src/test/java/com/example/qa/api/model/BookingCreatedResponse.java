package com.example.qa.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BookingCreatedResponse(int bookingid, Booking booking) {
}
