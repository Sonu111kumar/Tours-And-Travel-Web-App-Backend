package com.org.service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import com.org.dto.FlightDto;
import org.springframework.http.ResponseEntity;

import com.org.model.Flight;

public interface FlightService {
	public ResponseEntity<?> addFlight(FlightDto flightDto);

	public Iterable<Flight> viewAllFlight();

	public Flight viewFlight(Integer flightNumber);

	public Flight modifyFlight(Flight flight);

	public String removeFlight(Integer flightNumber);

    List<Flight> searchFlightsByCitiesAndDates(String departureCity, String arrivalCity, LocalDate departureDate, LocalDate returnDate);


}
