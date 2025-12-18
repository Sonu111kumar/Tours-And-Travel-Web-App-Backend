package com.org.service;

import com.org.dao.AirportDao;
import com.org.dto.FlightDto;
import com.org.exceptions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.org.model.Airport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.org.dao.FlightDao;
import com.org.exceptions.RecordAlreadyPresentException;
import com.org.model.Flight;

@Service
public class FlightServiceImpl implements FlightService {
	@Autowired
	FlightDao flightDao;

	@Autowired
	AirportDao airportDao;
	/*
	 * add a flight
	 */
	@Override
	public ResponseEntity<Flight> addFlight(FlightDto flightDto) {
		Optional<Flight> findById = flightDao.findById(flightDto.getFlightId());
		Optional<Airport> departureAirport = airportDao.findByairportName(flightDto.getDepartureAirport());
		Optional<Airport> arrivalAirport = airportDao.findByairportName(flightDto.getArrivalAirport());
		try {
		if (!findById.isPresent() && departureAirport.isPresent() && arrivalAirport.isPresent()) {
			Flight flight = new Flight();
			flight.setFlightId(flightDto.getFlightId());
			flight.setFlightName(flightDto.getFlightName());
			flight.setFlightModel(flightDto.getFlightModel());
			flight.setFlightType(flightDto.getFlightType());
			flight.setEconomyCapacity(flightDto.getEconomyCapacity());
			flight.setBusinessCapacity(flightDto.getBusinessCapacity());
			flight.setFirstCapacity(flightDto.getFirstCapacity());
			flight.setEconomyPremiumCapacity(flightDto.getEconomyPremiumCapacity());
			flight.setEconomyPrice(flightDto.getEconomyPrice());
			flight.setBusinessPrice(flightDto.getBusinessPrice());
			flight.setFirstPrice(flightDto.getFirstPrice());
			flight.setEconomyPremiumPrice(flightDto.getEconomyPremiumPrice());
			flight.setDepartureAirport(departureAirport.get());
			flight.setArrivalAirport(arrivalAirport.get());
			flight.setArrivalDatetime(flightDto.getArrivalDatetime());
			flight.setDepartureDatetime(flightDto.getDepartureDatetime());

			flightDao.save(flight);
			return new ResponseEntity<Flight>(flight,HttpStatus.OK);
		} else
			throw new RecordAlreadyPresentException("Flight with number: " + flightDto.getFlightId() + " already present");
	}
		catch(RecordAlreadyPresentException e)
		{
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	/*
	 * view all flights
	 */
	@Override
	public Iterable<Flight> viewAllFlight() {
		return flightDao.findAll();
	}


	/*
	 * search a flight
	 */
	@Override
	public Flight viewFlight(Integer flightNumber) {
		Optional<Flight> findById = flightDao.findById(flightNumber);
		if (findById.isPresent()) {
			return findById.get();
		}
		else
			throw new RecordNotFoundException("Flight with number: " + flightNumber + " not exists");
	    }
		/*catch(RecordNotFoundException e)
		{
			return new ResponseEntity(e.getMessage(),HttpStatus.NOT_FOUND);
		}*/

	/*
	 * modify a flight
	 */
	@Override
	public Flight modifyFlight(Flight flight) {
		Optional<Flight> findById = flightDao.findById(flight.getFlightId());
		if (findById.isPresent()) {
			flightDao.save(flight);
		} else
			throw new RecordNotFoundException("Flight with number: " + flight.getFlightId() + " not exists");
		return flight;
	}

	/*
	 * remove a flight
	 */
	public String removeFlight(Integer flightNumber) {
		Optional<Flight> findById = flightDao.findById(flightNumber);
		if (findById.isPresent()) {
			flightDao.deleteById(flightNumber);
			return "Flight removed!!";
		} else
			throw new RecordNotFoundException("Flight with number: " + flightNumber + " not exists");

	}
	@Override

	public List<Flight> searchFlightsByCitiesAndDates(String departureCity, String arrivalCity, LocalDate departureDate, LocalDate returnDate) {
		if (returnDate != null) {
			return flightDao.searchFlightsByCitiesAndDates(departureCity, arrivalCity, departureDate, returnDate);
		} else {
			return flightDao.searchOneWayFlightsByCitiesAndDates(departureCity, arrivalCity, departureDate);
		}
	}

}
