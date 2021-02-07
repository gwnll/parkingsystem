package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.apache.commons.lang.time.DateUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TicketDaoIT {

    private static TicketDAO ticketDAO;

    private static Ticket ticket;

    private static DataBasePrepareService dataBasePrepareService;

    @BeforeAll
    private static void setUp() {
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = new DataBaseTestConfig();
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void before() {
        this.ticket = new Ticket();
        this.ticket.setVehicleRegNumber("ABCDE");
        this.ticket.setInTime(DateUtils.round(new Date(), Calendar.SECOND));
        ParkingSpot parkingSpot = new ParkingSpot(5, ParkingType.BIKE, true);
        this.ticket.setParkingSpot(parkingSpot);
        System.out.println("MOT FACILE A RETROUVER");
    }

    @AfterAll
    private static void tearDown() {
        dataBasePrepareService.tearDown();
    }

    @Test
    public void saveTicketTest() {
        ticketDAO.saveTicket(ticket);
        Ticket result = ticketDAO.getTicket("ABCDE");
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    public void getTicketTest() {
        ticketDAO.saveTicket(ticket);
        Ticket result = ticketDAO.getTicket("ABCDE");
        Assertions.assertThat(result.getVehicleRegNumber()).isEqualTo(ticket.getVehicleRegNumber());
    }

    @Test
    public void getTicketsTest() {
        ticketDAO.saveTicket(ticket);
        List<Ticket> result = ticketDAO.getTickets("ABCDE");
        Assertions.assertThat(result).isNotEmpty();
    }

    @Test
    public void updateTicketTest() {
        ticketDAO.saveTicket(ticket);
        Date outTime = DateUtils.round(new Date(), Calendar.SECOND);
        Ticket updatedTicket = ticketDAO.getTicket("ABCDE");
        updatedTicket.setOutTime(outTime);
        FareCalculatorService fareCalculatorService = new FareCalculatorService(ticketDAO);
        fareCalculatorService.calculateFare(updatedTicket);
        ticketDAO.updateTicket(updatedTicket);
        boolean test = ticketDAO.updateTicket(updatedTicket);
        double price = updatedTicket.getPrice();
        Assertions.assertThat(test).isTrue();
        Assertions.assertThat(price).isNotNull();
    }
}
