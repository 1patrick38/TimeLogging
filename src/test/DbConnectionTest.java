import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DbConnectionTest {
    public static void main(String[] args) {
        try {
            Class.forName("org.h2.Driver");
            Connection con = DriverManager.getConnection("jdbc:h2:~/test", "test", "");
            Statement stmt = con.createStatement();
            //stmt.executeUpdate( "DROP TABLE table1" );
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS table1 (id bigint auto_increment, day varchar(10), start varchar(20), end varchar(20) )");
            stmt.executeUpdate("INSERT INTO table1(day,start,end) VALUES ( 'Montag', '10:00','12:00' )");
            stmt.executeUpdate("INSERT INTO table1(day,start,end) VALUES ( 'Dienstag', '11:00','12:00' )");

            ResultSet rs = stmt.executeQuery("SELECT * FROM table1");

            ObservableList<ObservableList> data = FXCollections.observableArrayList();
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                data.add(row);
                System.out.println("Row added " + row);
            }
            System.out.println(data);
            stmt.execute("DROP ALL OBJECTS");
            stmt.close();
            con.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
