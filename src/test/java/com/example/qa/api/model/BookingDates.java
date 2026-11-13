package com.example.qa.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Dates are kept as ISO strings (yyyy-MM-dd) exactly as the API sends them. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BookingDates(String checkin, String checkout) {
}
