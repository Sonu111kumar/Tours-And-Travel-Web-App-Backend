package com.org.dto;

import lombok.Data;

import java.util.Date;
@Data
public class FlightDto {


    private Integer flightId;
    private String flightName;
    private String flightModel;
    private String flightType;
    private Integer economyCapacity;
    private Integer businessCapacity;
    private Integer firstCapacity;
    private Integer economyPremiumCapacity;
    private Double economyPrice;
    private Double businessPrice;
    private Double firstPrice;
    private Double economyPremiumPrice;
    private String departureAirport;
    private String arrivalAirport;

    private Date departureDatetime;
    private Date arrivalDatetime;
}
