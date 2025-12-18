package com.org.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.org.model.Airport;

import java.util.Optional;

@Repository
public interface AirportDao extends CrudRepository<Airport, Long> {
  Optional<Airport> findByairportName(String airportName);
}
