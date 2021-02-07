package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class DataBasePrepareService {

    DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

    public void clearDataBaseEntries(){
        Connection connection = null;
        try{
            connection = dataBaseTestConfig.getConnection();

            //set parking entries to available
            connection.prepareStatement("update parking set available = true").execute();

            //clear ticket entries;
            connection.prepareStatement("truncate table ticket").execute();

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            dataBaseTestConfig.closeConnection(connection);
        }
    }

    public boolean tearDown() {
        Connection con = null;
        String deleteTickets = "delete FROM ticket";
        boolean result = false;
        try {
            con = dataBaseTestConfig.getConnection();
            PreparedStatement psTickets = con.prepareStatement(deleteTickets);

            result = psTickets.execute();
        } catch (Exception ex) {
        } finally {
            dataBaseTestConfig.closeConnection(con);
        }
        return result;
    }


}
