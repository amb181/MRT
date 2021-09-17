/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metrics;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

/**
 *
 * @author ealloem
 */
public class SQL_connection {
    public static Connection getConnection() {
        Connection connection = null;
        try {
            /*// Local test environment
            Class.forName("com.mysql.cj.jdbc.Driver");
            String connectionURL = "jdbc:mysql://localhost:3306/testing_metrics_db?userTimezone=true&serverTimezone=UTC";
            String user = "root";
            String password = "";*/
            
            // Test environment
            String connectionURL = "jdbc:mysql://146.250.85.96:3306/testing_metrics_db?userTimezone=true&serverTimezone=UTC";
            String user = "sourcing";
            String password = "S0urc1ngT3@m";
            
            // Production environment
            /*String connectionURL = "jdbc:mysql://146.250.85.96:3306/metrics_db?userTimezone=true&serverTimezone=UTC";
            String user = "sourcing";
            String password = "S0urc1ngT3@m";*/
            
            connection = DriverManager.getConnection(connectionURL, user, password);
            System.out.println("Successful connection to the database.");
        } catch(Exception e){
            JOptionPane.showMessageDialog(null, "<html><center>Something went wrong with the connection,<br>verify you have an internet connection and try again</html>", "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println(e);
            System.exit(0);
        }
        return connection;
    }
}
