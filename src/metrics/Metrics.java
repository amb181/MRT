/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metrics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

/**
 *
 * @author ealloem
 */
public class Metrics {

    /**
     * @throws java.io.IOException
     * @throws java.text.ParseException
     * @throws java.lang.InterruptedException
     */
    public static ArrayList<String> usersinfo = new ArrayList<>();
    public static String localversion = null;

    public static void main(String[] args) throws IOException {
        StartDialog starting = new StartDialog();
        JDialog log = starting.GetDialog();
        log.setVisible(true);
        JDialog loading = starting.GetLoadingDialog();

        if (GetVersion() == true) {
            // Get user's info
            Metrics metrics = new Metrics();
            metrics.GetUsersInfo();
            // Get date
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            System.out.println(dtf.format(now));
            String date = dtf.format(now);
            date = date.replace("/", "_").replace(" ", "_").replace(":", "_");
            // Write log every time the program runs and create folder if not exists
            String user_log = usersinfo.get(0);
            //String user_log = "ealloem";
            File dir = new File("C:\\Users\\" + user_log + "\\Documents\\MRT");
            
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // Find log files and delete them
            File fList[] = dir.listFiles();
            // Search .log files
            for (int i = 0; i < fList.length; i++) {
                if (fList[i].getName().endsWith(".log")) {
                    // and deletes
                    fList[i].delete();
                }
            }
            // Print log
            PrintStream out = new PrintStream(new FileOutputStream("C:\\Users\\" + user_log + "\\Documents\\MRT\\" + usersinfo.get(0) + "_" + date + ".log"));
            System.setOut(out);
            
            // System.out.println("Java version: " + System.getProperty("java.version"));
            System.out.println("MRT version: " + localversion);
            
            if (usersinfo.get(6).equals("1")) { // If user is admin
                try {
                    log.dispose();
                    Time_Review t_rev = new Time_Review();
                    t_rev.show();
                    t_rev.setLocationRelativeTo(null);

                } catch (ParseException ex) {
                    Logger.getLogger(Metrics.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else { // Regular user
                loading.setModal(true);
                loading.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                loading.toFront();
                loading.requestFocus();

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            log.dispose(); //Hide MRT logo

                            if (usersinfo.get(3).equals("Sourcing")) {
                                Sourcing_Time_Report time_r = new Sourcing_Time_Report();
                                time_r.show();
                                time_r.setLocationRelativeTo(null);
                            } else if (usersinfo.get(3).equals("COP")) {
                                COP_Time_Report time_r = new COP_Time_Report();
                                time_r.show();
                                time_r.setLocationRelativeTo(null);
                            } else if (usersinfo.get(3).equals("VSS")) {
                                VSS_Time_Report time_r = new VSS_Time_Report();
                                time_r.show();
                                time_r.setLocationRelativeTo(null);
                            } else if (usersinfo.get(3).equals("PSS")) {
                                PSS_Time_Report time_r = new PSS_Time_Report();
                                time_r.show();
                                time_r.setLocationRelativeTo(null);
                            }
                            loading.dispose();
                        } catch (ParseException | IOException ex) {
                            Logger.getLogger(Sourcing_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }).start();
                loading.setVisible(true);
            }
        } else {
            log.dispose();
            starting.show();
        }
    }

    public ArrayList<String> GetUsersInfo() {
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultset;
        String user = System.getProperty("user.name");
        //String user = "EISLLIN";
        System.out.println("Current user: " + user);

        try {
            connection = SQL_connection.getConnection();
            String sql_user_team = " SELECT * FROM users WHERE Signum='" + user + "';";
            preparedStatement = connection.prepareStatement(sql_user_team);
            resultset = preparedStatement.executeQuery();
            String last_name = null, name = null, cu = null, team = null, org = null, lm = null, access = null,
                    job_stage = null, act_type = null, cats_number = null, supp_team = null, supp_cu = null;
            while (resultset.next()) {
                last_name = resultset.getString("Last_Name");
                name = resultset.getString("Name");
                cu = resultset.getString("Customer_Unit");
                team = resultset.getString("Team");
                org = resultset.getString("Organization");
                lm = resultset.getString("Line_Manager");
                access = resultset.getString("Access");
                supp_team = resultset.getString("Supporting_Team");
                supp_cu = resultset.getString("Supporting_CU");
                job_stage = resultset.getString("Job_Stage");
                act_type = resultset.getString("Act_Type");
                cats_number = resultset.getString("CATS_Number");
                String full_name = last_name + " " + name;

                usersinfo.add(user);        // 0
                usersinfo.add(full_name);   // 1
                usersinfo.add(cu);          // 2
                usersinfo.add(team);        // 3
                usersinfo.add(org);         // 4
                usersinfo.add(lm);          // 5
                usersinfo.add(access);      // 6
                usersinfo.add(supp_team);   // 7
                usersinfo.add(supp_cu);     // 8
                usersinfo.add(job_stage);   // 9
                usersinfo.add(act_type);    // 10
                usersinfo.add(cats_number); // 11
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return usersinfo;
    }

    public static boolean GetVersion() {
        boolean flagVersion = false;
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultset;
        localversion = "3.1"; //Version
        try {
            connection = SQL_connection.getConnection();
            String sql_version = "SELECT version FROM source_control ORDER BY version DESC LIMIT 1;";
            preparedStatement = connection.prepareStatement(sql_version);
            resultset = preparedStatement.executeQuery();
            String version = "";
            if (resultset.next()) {
                version = resultset.getString("version");
            }
            if (localversion.equals(version)) {
                flagVersion = true;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return flagVersion;
    }
}
