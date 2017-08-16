package util;

import sample.TimeRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DataBase {

    // auxiliary class
    private DataBase(){}

    public static void saveOnClose() {

    }

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
                System.out.println("Record added " + record);
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
