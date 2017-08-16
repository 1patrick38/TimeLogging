package util;

import sample.TimeRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBase {

    // auxiliary class
    private DataBase() {
    }

    // TODO: rename table
    public static void saveOnClose(String gehen, String kommen, String zeit, String tag) {
        if(zeit.equalsIgnoreCase("00:00")) return;
        try {
            Class.forName("org.h2.Driver");
            Connection con = DriverManager.getConnection("jdbc:h2:~/test", "test", "");
            Statement stmt = con.createStatement();
            createTable(con, stmt);
            stmt.executeUpdate("INSERT INTO table1(day,start,end, time) VALUES ( '" + tag + "', '" + kommen + "','" + gehen + "', '" + zeit + "' )");
            stmt.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createTable(Connection con, Statement stmt) throws SQLException {
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS table1 (id bigint auto_increment, day varchar(10), start varchar(20), end varchar(20), time varchar(20) )");
    }

    // TODO: extract query and change * to named fields
    public static List<TimeRecord> readData() {
        try {
            Class.forName("org.h2.Driver");
            Connection con = DriverManager.getConnection("jdbc:h2:~/test", "test", "");
            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM table1");

            List<TimeRecord> data = new ArrayList<>();
            while (rs.next()) {
                List<String> row = new ArrayList<>();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                TimeRecord record = new TimeRecord();
                record.setDatum(row.get(1));
                record.setKommen(row.get(2));
                record.setGehen(row.get(3));
                record.setZeit(row.get(4));
                data.add(record);
            }
            System.out.println(data);
            stmt.close();
            con.close();
            return data;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ArrayList<>();
    }
}
