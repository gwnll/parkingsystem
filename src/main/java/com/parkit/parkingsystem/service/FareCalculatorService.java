package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    private final TicketDAO ticketDAO;

    public FareCalculatorService(TicketDAO ticketDAO) {
        this.ticketDAO = ticketDAO;
    }

    public void calculateFare(Ticket ticket){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        double inHour = (double) ticket.getInTime().getTime();
        double outHour = (double) ticket.getOutTime().getTime();

        double duration = (outHour - inHour) / 1000 / 60 / 60 ;

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(computeFare(duration, Fare.CAR_RATE_PER_HOUR, ticket));
                break;
            }
            case BIKE: {
                ticket.setPrice(computeFare(duration, Fare.BIKE_RATE_PER_HOUR, ticket));
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }
    }

    private double computeFare(double duration, double fare, Ticket ticket) {
        if (vehicleStaysLessThanThirtyMinutes(duration)) {
            return 0;
        } else if (recurrentVehicle(ticket)) {
            double value = (duration * fare) - ((duration * fare) * 0.05);
            return Math.floor(value * 100) / 100;
        }
        return duration * fare;
    }

    private boolean vehicleStaysLessThanThirtyMinutes(double duration) {
        return duration < 0.5;
    }

    private boolean recurrentVehicle (Ticket ticket) {
        return ticketDAO.getTickets(ticket.getVehicleRegNumber()).size() >= 2;
    }
}