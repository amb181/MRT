/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metrics;

import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import static metrics.Metrics.localversion;
import static metrics.Metrics.usersinfo;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author ealloem
 */
public final class Sourcing_Time_Report extends javax.swing.JFrame {

    /**
     * Creates new form Sourcing_Time_Report
     *
     * @throws java.text.ParseException
     * @throws java.io.IOException
     */
    ArrayList<String> tasks_info = new ArrayList<>();
    ArrayList<String> networks_info = new ArrayList<>();
    ArrayList<String> metrics_sourcing_info = new ArrayList<>();
    int current_week = 0, times_in_edit = 0;
    float hours = 0;
    String imported_file = null;
    boolean imported = false;
    String saved = "Data saved successfully!";

    public Sourcing_Time_Report() throws ParseException, IOException {
        initComponents();
        jPanel2.setVisible(false);
        jPanel3.setVisible(false);
        this.setVisible(true);
        setExtendedState(this.MAXIMIZED_BOTH);

        jDLoading.setSize(350, 150);
        jDLoading.setTitle("MRT - Connecting to the server");
        jDLoading.setIconImage(new ImageIcon(getClass().getResource("/images/MRT_logo.png")).getImage());
        jDLoading.setLocationRelativeTo(null);
        jLabelLoading.setText("Loading...");
        jLabelLoading.setHorizontalAlignment(JLabel.CENTER);
        jDLoading.setVisible(true);
        // See user's info
        System.out.println(usersinfo);
        // Get user's available tasks
        GetTasks();
        System.out.println(tasks_info);
        // Get user's available networks
        GetSubnetworks();
        System.out.println(networks_info);
        // Fill supporting teams menu
        GetTeams();
        jSpinnerBulk.setVisible(false);
        jPanel2.setVisible(false);
        // Set icon to window
        this.setIconImage(new ImageIcon(getClass().getResource("/images/MRT_logo.png")).getImage());

        // Allign info in tables
        jTableAddMetrics.setAutoResizeMode(jTableAddMetrics.AUTO_RESIZE_OFF);
        jTableAddMetrics.setRowSelectionAllowed(true);
        jTableAddMetrics.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jTableSeeMetrics.setAutoResizeMode(jTableSeeMetrics.AUTO_RESIZE_OFF);
        jTableSeeMetrics.setRowSelectionAllowed(true);
        jTableSeeMetrics.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        AutoCompleteDecorator.decorate(jcbTask);
        AutoCompleteDecorator.decorate(jcbTask1);
        // Set date field to non-editable and get current week
        JTextFieldDateEditor editor = (JTextFieldDateEditor) jDateChooser1.getDateEditor();
        JTextFieldDateEditor editor1 = (JTextFieldDateEditor) jDateChooser2.getDateEditor();
        Date date = new Date();
        jDateChooser1.setDate(date);
        jDateChooser2.setDate(date);
        editor.setEditable(false);
        editor1.setEditable(false);
        Calendar now = Calendar.getInstance();
        current_week = now.get(Calendar.WEEK_OF_YEAR);
        // Set current week panel 2
        for (int i = 1; i <= current_week; i++) {
            if (current_week == 1) {
                jcbWeek1.addItem("52");
            }
            jcbWeek1.addItem(String.valueOf(i));
        }
        jcbWeek1.setSelectedIndex(current_week - 1);
        GetHours();
        this.setTitle("SOURCING               " + usersinfo.get(0) + " | " + usersinfo.get(4) + " | " + usersinfo.get(1) + " | " + "Week: " + current_week + " | "
                + "Hours: " + hours);
        jLabelVersion.setText("v_" + localversion);
        // Get week from new jDateChooser and put it in week textfield
        SimpleDateFormat dcn = new SimpleDateFormat("yyyy-MM-dd");
        String date_change = dcn.format(jDateChooser1.getDate());
        String date_change1 = dcn.format(jDateChooser2.getDate());
        int week = 0;
        Date date_ = dcn.parse(date_change);
        Date date_1 = dcn.parse(date_change1);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date_);
        cal.setTime(date_1);
        week = cal.get(Calendar.WEEK_OF_YEAR);
        jTextFieldWeek.setText(String.valueOf(week));
        // Update week number every time user clics new date panel 1
        jDateChooser1.addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                String date_change = dcn.format(jDateChooser1.getDate());
                int week = 0;
                Date date_ = null;
                try {
                    date_ = dcn.parse(date_change);
                } catch (ParseException ex) {
                    Logger.getLogger(Sourcing_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(date_);
                week = cal.get(Calendar.WEEK_OF_YEAR);
                if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    week = week - 1;
                }
                jTextFieldWeek.setText(String.valueOf(week));
            }
        });
        // Populate jcbCU with possible CUs for this team
        String[] cus = usersinfo.get(8).split("@");
        if (usersinfo.get(2).equals("Sourcing Automation") || usersinfo.get(2).equals("IT Sourcing")) {
            jcbCU.addItem(usersinfo.get(2));
        }
        for (int i = 0; i < cus.length; i++) {
            if (cus[i].equals("Sourcing Automation") || cus[i].equals("IT Sourcing")) {
                jcbCU.addItem(cus[i]);
            }
        }
        // Populate jcbTask1 with All default
        jcbTask1.addItem("Select an activity...");
        for (int i = 2; i < tasks_info.size(); i += 5) {
            jcbTask1.addItem(tasks_info.get(i));
        }
        // Populate jcbNetwork with Billable default
        jcbSubnet.addItem("Select a subnetwork...");
        for (int i = 0; i < networks_info.size(); i += 7) {
            jcbSubnet.addItem(networks_info.get(i + 5));
        }
        // Get hours per day in label
        GetDailyHours();

        // Fit images in ABOUT option
        ImageIcon logo_ = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/MRT_logo.png")));
        Image logo_cast = logo_.getImage();
        Image logo_scaled = logo_cast.getScaledInstance(jLabelLogo.getWidth(), jLabelLogo.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon logo = new ImageIcon(logo_scaled);
        jLabelLogo.setIcon(logo);
        ImageIcon dagaz_ = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/dagaz.png")));
        Image dagaz_cast = dagaz_.getImage();
        Image dagaz_scaled = dagaz_cast.getScaledInstance(jLabelDagaz.getWidth(), jLabelDagaz.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon dagaz = new ImageIcon(dagaz_scaled);
        jLabelDagaz.setIcon(dagaz);
        ImageIcon raido_ = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/raido.png")));
        Image raido_cast = raido_.getImage();
        Image raido_scaled = raido_cast.getScaledInstance(jLabelRaido.getWidth(), jLabelRaido.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon raido = new ImageIcon(raido_scaled);
        jLabelRaido.setIcon(raido);
        ImageIcon naudiz_ = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/naudiz.png")));
        Image naudiz_cast = naudiz_.getImage();
        Image naudiz_scaled = naudiz_cast.getScaledInstance(jLabelNaudiz.getWidth(), jLabelNaudiz.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon naudiz = new ImageIcon(naudiz_scaled);
        jLabelNaudiz.setIcon(naudiz);
        // Set table headers
        JTableHeader header = jTableAddMetrics.getTableHeader();
        header.setBackground(new Color(0, 130, 240));
        header.setForeground(Color.white);
        header.setFont(new Font("Ericsson Hilda", 0, 20));
        JTableHeader header1 = jTableSeeMetrics.getTableHeader();
        header1.setBackground(new Color(0, 130, 240));
        header1.setForeground(Color.white);
        header1.setFont(new Font("Ericsson Hilda", 0, 20));
        jDLoading.setVisible(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDLoading = new javax.swing.JDialog();
        jPanelLoading = new javax.swing.JPanel();
        jLabelLoading = new javax.swing.JLabel();
        jLabelLoading1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jBInsert = new javax.swing.JButton();
        jLTaskID = new javax.swing.JLabel();
        jLTask = new javax.swing.JLabel();
        jcbTask = new javax.swing.JComboBox<>();
        jLSAP_Billing = new javax.swing.JLabel();
        jcbSAP = new javax.swing.JComboBox<>();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLDate = new javax.swing.JLabel();
        jLTime = new javax.swing.JLabel();
        jTextFieldTime = new javax.swing.JTextField();
        jLWeek = new javax.swing.JLabel();
        jLRequests = new javax.swing.JLabel();
        jTextFieldRequests = new javax.swing.JTextField();
        jLComments = new javax.swing.JLabel();
        jTextFieldComments = new javax.swing.JTextField();
        jBAddTable = new javax.swing.JButton();
        jBDeleteRow = new javax.swing.JButton();
        jBClearTable = new javax.swing.JButton();
        jLHoursForLoggedTime = new javax.swing.JLabel();
        jTextFieldWeek = new javax.swing.JTextField();
        jTextFieldRequestor = new javax.swing.JTextField();
        jLRequestor = new javax.swing.JLabel();
        jcbFTR = new javax.swing.JComboBox<>();
        jLFTR = new javax.swing.JLabel();
        jcbFailedFTR = new javax.swing.JComboBox<>();
        jLFailFTR = new javax.swing.JLabel();
        jcbOnTime = new javax.swing.JComboBox<>();
        jLOnTime = new javax.swing.JLabel();
        jTextFieldTask_ID = new javax.swing.JTextField();
        jcbFailedOnTime = new javax.swing.JComboBox<>();
        jLFaildeOnTime = new javax.swing.JLabel();
        jLNetwork = new javax.swing.JLabel();
        jLabelSubnetwork = new javax.swing.JLabel();
        jRSingle = new javax.swing.JRadioButton();
        jRBulk = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableAddMetrics = new javax.swing.JTable(){

        } ;
        jSpinnerBulk = new javax.swing.JSpinner();
        jLActivity = new javax.swing.JLabel();
        jTextFieldActivity = new javax.swing.JTextField();
        jcbSubnet = new javax.swing.JComboBox<>();
        jTextFieldNetwork = new javax.swing.JTextField();
        jLabelHoursDaysB1 = new javax.swing.JLabel();
        jLabelHoursDaysB2 = new javax.swing.JLabel();
        jLCU1 = new javax.swing.JLabel();
        jcbCU = new javax.swing.JComboBox<>();
        jLabelHoursDaysH1 = new javax.swing.JLabel();
        jLabelTeam = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLTask1 = new javax.swing.JLabel();
        jcbTask1 = new javax.swing.JComboBox<>();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jLDate1 = new javax.swing.JLabel();
        jLWeek1 = new javax.swing.JLabel();
        jLSAP_Billing1 = new javax.swing.JLabel();
        jcbSAP1 = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableSeeMetrics = new javax.swing.JTable(){
        }
        ;
        jBSearch = new javax.swing.JButton();
        jCheckBoxWeek = new javax.swing.JCheckBox();
        jBUpdate = new javax.swing.JButton();
        jBDeleteRow1 = new javax.swing.JButton();
        jcbWeek1 = new javax.swing.JComboBox<>();
        jBClearTable2 = new javax.swing.JButton();
        jLabelHoursDaysH = new javax.swing.JLabel();
        jLabelHoursDaysB = new javax.swing.JLabel();
        jcbTeam1 = new javax.swing.JComboBox<>();
        jLabelTeam1 = new javax.swing.JLabel();
        jBExport = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabelLogo = new javax.swing.JLabel();
        jLabelDagaz = new javax.swing.JLabel();
        jLabelRaido = new javax.swing.JLabel();
        jLabelNaudiz = new javax.swing.JLabel();
        jLabelDescDagaz = new javax.swing.JLabel();
        jLabelDescRaido = new javax.swing.JLabel();
        jLabelDescNaudiz = new javax.swing.JLabel();
        jLabelDescAbout = new javax.swing.JLabel();
        jLabelSupport = new javax.swing.JLabel();
        jLabelVersion = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemOpen = new javax.swing.JMenuItem();
        jMenuItemGenerate = new javax.swing.JMenuItem();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItemRecord = new javax.swing.JMenuItem();
        jMenuItemEdit = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenuItemMRT = new javax.swing.JMenuItem();

        jDLoading.setBackground(new java.awt.Color(255, 255, 255));

        jPanelLoading.setBackground(new java.awt.Color(255, 255, 255));

        jLabelLoading.setText("Saving...");

        jLabelLoading1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Loading.gif"))); // NOI18N

        javax.swing.GroupLayout jPanelLoadingLayout = new javax.swing.GroupLayout(jPanelLoading);
        jPanelLoading.setLayout(jPanelLoadingLayout);
        jPanelLoadingLayout.setHorizontalGroup(
            jPanelLoadingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelLoading, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabelLoading1, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
        );
        jPanelLoadingLayout.setVerticalGroup(
            jPanelLoadingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLoadingLayout.createSequentialGroup()
                .addComponent(jLabelLoading1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelLoading, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jDLoadingLayout = new javax.swing.GroupLayout(jDLoading.getContentPane());
        jDLoading.getContentPane().setLayout(jDLoadingLayout);
        jDLoadingLayout.setHorizontalGroup(
            jDLoadingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelLoading, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jDLoadingLayout.setVerticalGroup(
            jDLoadingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelLoading, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setMaximumSize(new java.awt.Dimension(0, 0));
        jPanel1.setPreferredSize(new java.awt.Dimension(1400, 915));

        jBInsert.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBInsert.setText("Save");
        jBInsert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBInsertActionPerformed(evt);
            }
        });

        jLTaskID.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTaskID.setText("Task ID:");

        jLTask.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTask.setText("Task:");

        jcbTask.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jcbTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbTaskActionPerformed(evt);
            }
        });

        jLSAP_Billing.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLSAP_Billing.setText("SAP Billing:");

        jcbSAP.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jcbSAP.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Billable", "Not Billable" }));
        jcbSAP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbSAPActionPerformed(evt);
            }
        });

        jDateChooser1.setDateFormatString("yyyy-MM-dd");
        jDateChooser1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLDate.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLDate.setText("Date:");

        jLTime.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTime.setText("Logged Time:");

        jTextFieldTime.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLWeek.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLWeek.setText("Week:");

        jLRequests.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLRequests.setText("Number of requests:");

        jTextFieldRequests.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLComments.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLComments.setText("Comments:");

        jTextFieldComments.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jBAddTable.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBAddTable.setText("Add");
        jBAddTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBAddTableActionPerformed(evt);
            }
        });

        jBDeleteRow.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBDeleteRow.setText("Delete row");
        jBDeleteRow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBDeleteRowActionPerformed(evt);
            }
        });

        jBClearTable.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBClearTable.setText("Clear");
        jBClearTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBClearTableActionPerformed(evt);
            }
        });

        jLHoursForLoggedTime.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLHoursForLoggedTime.setText("hours");

        jTextFieldWeek.setEditable(false);
        jTextFieldWeek.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jTextFieldRequestor.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLRequestor.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLRequestor.setText("Requestor:");

        jcbFTR.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jcbFTR.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "N/A", "Yes", "No" }));

        jLFTR.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLFTR.setText("FTR:");

        jcbFailedFTR.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jcbFailedFTR.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "N/A", "Human Error", "Lack of process adhereance", "Missing training", "Technical issues", "Tool related error", "Wrong inputs" }));

        jLFailFTR.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLFailFTR.setText("Failed FTR Category:");

        jcbOnTime.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jcbOnTime.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "N/A", "Yes", "No" }));

        jLOnTime.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLOnTime.setText("On Time:");

        jTextFieldTask_ID.setEditable(false);
        jTextFieldTask_ID.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jcbFailedOnTime.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jcbFailedOnTime.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "N/A", "Yes", "No" }));

        jLFaildeOnTime.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLFaildeOnTime.setText("Failed on time:");

        jLNetwork.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLNetwork.setText("Network:");

        jLabelSubnetwork.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabelSubnetwork.setText("Subnetwork:");

        jRSingle.setBackground(new java.awt.Color(255, 255, 255));
        jRSingle.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jRSingle.setSelected(true);
        jRSingle.setText("Single");
        jRSingle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRSingleActionPerformed(evt);
            }
        });

        jRBulk.setBackground(new java.awt.Color(255, 255, 255));
        jRBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jRBulk.setText("Bulk");
        jRBulk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBulkActionPerformed(evt);
            }
        });

        jScrollPane1.setAutoscrolls(true);

        jTableAddMetrics.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jTableAddMetrics.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Region", "Signum", "Requestor", "Task ID", "Task", "Network", "Subnetwork", "Activity code", "SAP Billing", "Work date", "Logged Time", "Week", "FTR", "On Time", "Failed FTR", "Failed On Time", "Number of requests", "Comments"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, false, false, false, false, false, false, true, false, false, false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableAddMetrics.setRowHeight(20);
        jTableAddMetrics.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableAddMetrics.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTableAddMetrics);
        if (jTableAddMetrics.getColumnModel().getColumnCount() > 0) {
            jTableAddMetrics.getColumnModel().getColumn(0).setPreferredWidth(80);
            jTableAddMetrics.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableAddMetrics.getColumnModel().getColumn(2).setPreferredWidth(130);
            jTableAddMetrics.getColumnModel().getColumn(3).setPreferredWidth(250);
            jTableAddMetrics.getColumnModel().getColumn(4).setPreferredWidth(350);
            jTableAddMetrics.getColumnModel().getColumn(5).setPreferredWidth(120);
            jTableAddMetrics.getColumnModel().getColumn(6).setPreferredWidth(120);
            jTableAddMetrics.getColumnModel().getColumn(7).setPreferredWidth(140);
            jTableAddMetrics.getColumnModel().getColumn(8).setPreferredWidth(120);
            jTableAddMetrics.getColumnModel().getColumn(9).setPreferredWidth(150);
            jTableAddMetrics.getColumnModel().getColumn(10).setPreferredWidth(120);
            jTableAddMetrics.getColumnModel().getColumn(11).setPreferredWidth(90);
            jTableAddMetrics.getColumnModel().getColumn(12).setPreferredWidth(80);
            jTableAddMetrics.getColumnModel().getColumn(13).setPreferredWidth(80);
            jTableAddMetrics.getColumnModel().getColumn(14).setPreferredWidth(250);
            jTableAddMetrics.getColumnModel().getColumn(15).setPreferredWidth(150);
            jTableAddMetrics.getColumnModel().getColumn(16).setPreferredWidth(200);
            jTableAddMetrics.getColumnModel().getColumn(17).setPreferredWidth(350);
        }

        jSpinnerBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jSpinnerBulk.setModel(new javax.swing.SpinnerNumberModel(2, 2, null, 1));

        jLActivity.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLActivity.setText("Activity:");

        jTextFieldActivity.setEditable(false);
        jTextFieldActivity.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jcbSubnet.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jcbSubnet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbSubnetActionPerformed(evt);
            }
        });

        jTextFieldNetwork.setEditable(false);
        jTextFieldNetwork.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLabelHoursDaysB1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLabelHoursDaysB2.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLCU1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLCU1.setText("Customer Unit:");

        jcbCU.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jcbCU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbCUActionPerformed(evt);
            }
        });

        jLabelHoursDaysH1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLabelTeam.setFont(new java.awt.Font("Ericsson Hilda", 1, 36)); // NOI18N
        jLabelTeam.setForeground(new java.awt.Color(0, 153, 153));
        jLabelTeam.setText("Sourcing Metrics");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jcbSAP, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLSAP_Billing)
                            .addComponent(jLCU1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcbCU, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLTaskID)
                                        .addGap(58, 58, 58)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLTask)
                                            .addComponent(jcbTask, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jTextFieldTask_ID, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLDate))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jTextFieldTime, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLHoursForLoggedTime))
                                    .addComponent(jLTime))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldWeek, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLWeek))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLRequestor)
                                    .addComponent(jTextFieldRequestor))))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelHoursDaysB1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelHoursDaysH1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(3, 3, 3)
                        .addComponent(jLabelHoursDaysB2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLComments)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldComments)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBAddTable)
                        .addGap(234, 234, 234))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcbFTR, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLFTR))
                        .addGap(30, 30, 30)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jcbOnTime, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLOnTime))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcbFailedFTR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLFailFTR, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcbFailedOnTime, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLFaildeOnTime))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLRequests)
                            .addComponent(jTextFieldRequests, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelSubnetwork)
                            .addComponent(jcbSubnet, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLNetwork)
                            .addComponent(jTextFieldNetwork, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldActivity, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLActivity))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRSingle)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jRBulk)
                                .addGap(4, 4, 4)
                                .addComponent(jSpinnerBulk, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(jBDeleteRow)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jBClearTable)
                                .addGap(508, 508, 508)
                                .addComponent(jLabelTeam)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jBInsert)))
                        .addGap(24, 24, 24))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jcbCU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLCU1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLDate)
                                    .addComponent(jLTime)
                                    .addComponent(jLWeek)
                                    .addComponent(jLRequestor))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldTime, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextFieldWeek, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLHoursForLoggedTime)
                                        .addComponent(jTextFieldRequestor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLTask)
                                    .addComponent(jLSAP_Billing)
                                    .addComponent(jLTaskID))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldTask_ID, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jcbTask, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jcbSAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabelHoursDaysH1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelHoursDaysB1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabelHoursDaysB2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jSpinnerBulk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldActivity)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jRSingle)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jRBulk))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLOnTime)
                                .addComponent(jLFailFTR)
                                .addComponent(jLFaildeOnTime)
                                .addComponent(jLRequests)
                                .addComponent(jLabelSubnetwork)
                                .addComponent(jLNetwork)
                                .addComponent(jLActivity))
                            .addComponent(jLFTR)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jcbFailedOnTime, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextFieldRequests, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jcbOnTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jcbFailedFTR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jcbSubnet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldNetwork, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jcbFTR, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldComments, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLComments)
                    .addComponent(jBAddTable))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 644, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jBClearTable)
                            .addComponent(jBDeleteRow))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jBInsert)
                        .addComponent(jLabelTeam))))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setMaximumSize(new java.awt.Dimension(1400, 915));
        jPanel2.setMinimumSize(new java.awt.Dimension(1400, 915));
        jPanel2.setPreferredSize(new java.awt.Dimension(1400, 915));

        jLTask1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTask1.setText("Task:");

        jcbTask1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jDateChooser2.setDateFormatString("yyyy-MM-dd");
        jDateChooser2.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLDate1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLDate1.setText("Date:");

        jLWeek1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLWeek1.setText("Week:");

        jLSAP_Billing1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLSAP_Billing1.setText("SAP Billing:");

        jcbSAP1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jcbSAP1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Billable", "Not Billable" }));
        jcbSAP1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbSAP1ActionPerformed(evt);
            }
        });

        jScrollPane2.setAutoscrolls(true);

        jTableSeeMetrics.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jTableSeeMetrics.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id", "Region", "Signum", "Requestor", "Task ID", "Task", "Network", "Subnetwork", "Activity code", "SAP Billing", "Work date", "Logged Time", "Week", "FTR", "On Time", "Failed FTR", "Failed On Time", "Number of requests", "Comments"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true, false, false, false, false, false, false, false, true, false, false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableSeeMetrics.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTableSeeMetrics.setRowHeight(20);
        jTableSeeMetrics.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jTableSeeMetrics.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(jTableSeeMetrics);
        if (jTableSeeMetrics.getColumnModel().getColumnCount() > 0) {
            jTableSeeMetrics.getColumnModel().getColumn(1).setPreferredWidth(80);
            jTableSeeMetrics.getColumnModel().getColumn(2).setPreferredWidth(100);
            jTableSeeMetrics.getColumnModel().getColumn(3).setPreferredWidth(130);
            jTableSeeMetrics.getColumnModel().getColumn(4).setPreferredWidth(250);
            jTableSeeMetrics.getColumnModel().getColumn(5).setPreferredWidth(350);
            jTableSeeMetrics.getColumnModel().getColumn(6).setPreferredWidth(120);
            jTableSeeMetrics.getColumnModel().getColumn(7).setPreferredWidth(120);
            jTableSeeMetrics.getColumnModel().getColumn(8).setPreferredWidth(140);
            jTableSeeMetrics.getColumnModel().getColumn(9).setPreferredWidth(120);
            jTableSeeMetrics.getColumnModel().getColumn(10).setPreferredWidth(150);
            jTableSeeMetrics.getColumnModel().getColumn(11).setPreferredWidth(120);
            jTableSeeMetrics.getColumnModel().getColumn(12).setPreferredWidth(90);
            jTableSeeMetrics.getColumnModel().getColumn(13).setPreferredWidth(80);
            jTableSeeMetrics.getColumnModel().getColumn(14).setPreferredWidth(80);
            jTableSeeMetrics.getColumnModel().getColumn(15).setPreferredWidth(250);
            jTableSeeMetrics.getColumnModel().getColumn(16).setPreferredWidth(150);
            jTableSeeMetrics.getColumnModel().getColumn(17).setPreferredWidth(200);
            jTableSeeMetrics.getColumnModel().getColumn(18).setPreferredWidth(350);
        }

        jBSearch.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBSearch.setText("Search");
        jBSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSearchActionPerformed(evt);
            }
        });

        jCheckBoxWeek.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBoxWeek.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCheckBoxWeek.setText("By week");
        jCheckBoxWeek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxWeekActionPerformed(evt);
            }
        });

        jBUpdate.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBUpdate.setText("Update");
        jBUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBUpdateActionPerformed(evt);
            }
        });

        jBDeleteRow1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBDeleteRow1.setText("Delete row");
        jBDeleteRow1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBDeleteRow1ActionPerformed(evt);
            }
        });

        jcbWeek1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jBClearTable2.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBClearTable2.setText("Clear");
        jBClearTable2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBClearTable2ActionPerformed(evt);
            }
        });

        jLabelHoursDaysH.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLabelHoursDaysB.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jcbTeam1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jcbTeam1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbTeam1ActionPerformed(evt);
            }
        });

        jLabelTeam1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabelTeam1.setText("Team:");

        jBExport.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBExport.setText("Export table");
        jBExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBExportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLSAP_Billing1)
                            .addComponent(jLabelTeam1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jcbTeam1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jcbSAP1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jLTask1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jcbTask1, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jCheckBoxWeek)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLDate1)
                                        .addGap(18, 18, 18)
                                        .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLWeek1)
                                        .addGap(18, 18, 18)
                                        .addComponent(jcbWeek1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 249, Short.MAX_VALUE)
                                        .addComponent(jBSearch))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addGap(371, 371, 371)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabelHoursDaysB, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabelHoursDaysH, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addGap(19, 19, 19))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jBDeleteRow1)
                                .addGap(18, 18, 18)
                                .addComponent(jBClearTable2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jBExport)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jBUpdate))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1370, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabelHoursDaysH, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jLabelHoursDaysB, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jcbTeam1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabelTeam1)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLTask1)
                        .addComponent(jcbTask1)
                        .addComponent(jLDate1)
                        .addComponent(jLWeek1)
                        .addComponent(jLSAP_Billing1)
                        .addComponent(jcbSAP1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jBSearch)
                        .addComponent(jcbWeek1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBoxWeek)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jBDeleteRow1)
                            .addComponent(jBClearTable2))
                        .addGap(36, 36, 36))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jBUpdate)
                            .addComponent(jBExport))
                        .addGap(35, 35, 35))))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setMaximumSize(new java.awt.Dimension(1400, 915));
        jPanel3.setMinimumSize(new java.awt.Dimension(1400, 915));

        jLabelRaido.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelRaido.setMaximumSize(new java.awt.Dimension(200, 200));
        jLabelRaido.setMinimumSize(new java.awt.Dimension(200, 200));
        jLabelRaido.setPreferredSize(new java.awt.Dimension(200, 200));

        jLabelDescDagaz.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabelDescDagaz.setText("Dagaz: Means \"Day\"");

        jLabelDescRaido.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabelDescRaido.setText("Raido: Means \"Journey\"");

        jLabelDescNaudiz.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabelDescNaudiz.setText("Naudiz: Means \"Need\"");

        jLabelDescAbout.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabelDescAbout.setText("<html>The idea of taking nordic runes to create the MRT logo comes from the Bluetooth logo since it is the most<br/>famous patent of Ericsson so, we decided to relate this desktop app to it.<br/><br/>MRT stands for \"Metrics Recording Tool\" and we can find those initials in the logo.<br/> <br/>This logo is formed with the three runes shown above, this runes resemblance to the letters M, R and T.<br/>They come from the Elder Futhark Runic alphabet and their meanings form the philosophy of this tool. <br/><br/>This philosophy is to meet the need of recording your daily journey. </html>");

        jLabelSupport.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabelSupport.setText("<html>Support:<br/>alejandro.molina.bermudez@ericsson.com<br/>rafael.gomez.sanchez@ericsson.com</html>");

        jLabelVersion.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabelVersion.setText("Version: ");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(121, 121, 121)
                        .addComponent(jLabelDescRaido))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelDescNaudiz)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGap(37, 37, 37)
                                    .addComponent(jLabelDescDagaz))
                                .addComponent(jLabelDagaz, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabelRaido, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabelNaudiz, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 383, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelDescAbout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(164, 164, 164)
                        .addComponent(jLabelLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(121, 121, 121))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelSupport, javax.swing.GroupLayout.PREFERRED_SIZE, 494, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelVersion)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabelVersion))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabelDagaz, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabelDescDagaz)
                                .addGap(18, 18, 18)
                                .addComponent(jLabelRaido, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabelDescRaido))
                            .addComponent(jLabelLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabelNaudiz, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelDescNaudiz)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jLabelDescAbout, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabelSupport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jMenu1.setText("File");
        jMenu1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jMenuItemOpen.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMenuItemOpen.setText("Import (csv file)");
        jMenuItemOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOpenActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemOpen);

        jMenuItemGenerate.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMenuItemGenerate.setText("Generate template ");
        jMenuItemGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGenerateActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemGenerate);

        jMenuItemExit.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMenuItemExit.setText("Exit");
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExitActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemExit);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenu2.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jMenuItemRecord.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMenuItemRecord.setText("Record working time");
        jMenuItemRecord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRecordActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItemRecord);

        jMenuItemEdit.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMenuItemEdit.setText("Edit working time");
        jMenuItemEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEditActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItemEdit);

        jMenuBar1.add(jMenu2);

        jMenu4.setText("Teams");
        jMenu4.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMenuBar1.add(jMenu4);

        jMenu3.setText("About");
        jMenu3.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jMenuItemMRT.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMenuItemMRT.setText("What is MRT?");
        jMenuItemMRT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMRTActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItemMRT);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1618, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1618, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 948, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 964, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jBInsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBInsertActionPerformed
        // Action for Save button
        if (jTableAddMetrics.getEditingRow() != -1) {
            jTableAddMetrics.getCellEditor().stopCellEditing();// In case there's selected a field in the table
        }
        int rows = jTableAddMetrics.getRowCount();
        boolean error = false;
        if (imported) { // If data imported from csv file
            System.out.println("File was imported");
            for (int row = 0; row < rows; row++) {
                String region = (String) jTableAddMetrics.getValueAt(row, 0);
                String signum = (String) jTableAddMetrics.getValueAt(row, 1);
                signum = signum.toLowerCase(); // In case user has signum in uppercase
                String requestor = (String) jTableAddMetrics.getValueAt(row, 2);
                String task_id = (String) jTableAddMetrics.getValueAt(row, 3);
                String task = (String) jTableAddMetrics.getValueAt(row, 4);
                String net = (String) jTableAddMetrics.getValueAt(row, 5);
                String subnet = (String) jTableAddMetrics.getValueAt(row, 6);
                String act = (String) jTableAddMetrics.getValueAt(row, 7);
                String sap = (String) jTableAddMetrics.getValueAt(row, 8);
                String w_date = (String) jTableAddMetrics.getValueAt(row, 9);
                String time = (String) jTableAddMetrics.getValueAt(row, 10);
                String week = (String) jTableAddMetrics.getValueAt(row, 11);
                String ftr = (String) jTableAddMetrics.getValueAt(row, 12);
                String on_time = (String) jTableAddMetrics.getValueAt(row, 13);
                String f_ftr = (String) jTableAddMetrics.getValueAt(row, 14);
                String f_on_time = (String) jTableAddMetrics.getValueAt(row, 15);
                String requests = (String) jTableAddMetrics.getValueAt(row, 16);
                String comments = (String) jTableAddMetrics.getValueAt(row, 17);
                // Validate every cell is containing valid user info or is not empty
                if (!(usersinfo.contains(signum) && tasks_info.contains(task_id) && tasks_info.contains(task)
                        && (networks_info.contains(net) || net.equals("0")) && (networks_info.contains(subnet) || subnet.equals("0"))
                        && (networks_info.contains(act) || act.equals("0")) && tasks_info.contains(sap))
                        || time.isEmpty() || time.equals("0") || time.equals("0.0") || week.isEmpty() || ftr.isEmpty() || on_time.isEmpty()
                        || f_ftr.isEmpty() || f_on_time.isEmpty() || requests.isEmpty() || comments.isEmpty() || requestor.isEmpty()
                        || region.isEmpty() || w_date.isEmpty()) {
                    int current_row = row + 1;
                    JOptionPane.showMessageDialog(this, "File has inconsistent information in row " + current_row + ", please register your hours with this tool to prevent errors.");
                    error = true;
                }
            }
            if (error == false) {
                jLabelLoading.setText("Saving your metrics into database...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        InsertIntoDB();
                        GetDailyHours();
                        jDLoading.dispose();
                        JOptionPane.showMessageDialog(Sourcing_Time_Report.this, saved);
                    }
                }).start();
                jDLoading.setVisible(true);
            }
        } else {
            if (rows == 0) {
                JOptionPane.showMessageDialog(this, "Nothing saved");
            } else {
                jLabelLoading.setText("Saving your metrics into database...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        InsertIntoDB();
                        GetDailyHours();
                        jDLoading.dispose();
                        JOptionPane.showMessageDialog(Sourcing_Time_Report.this, saved);
                    }
                }).start();
                jDLoading.setVisible(true);
            }
        }
    }//GEN-LAST:event_jBInsertActionPerformed

    private void InsertIntoDB() {
        Connection connection;
        CallableStatement callableStatement;
        try {
            int rows = jTableAddMetrics.getRowCount();
            connection = SQL_connection.getConnection();

            callableStatement = connection.prepareCall("CALL register_metrics_sourcing"
                    + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

            for (int row = 0; row < rows; row++) {
                String region = (String) jTableAddMetrics.getValueAt(row, 0);
                String signum = (String) jTableAddMetrics.getValueAt(row, 1);
                signum = signum.toLowerCase(); // In case user has signum in uppercase
                String requestor = (String) jTableAddMetrics.getValueAt(row, 2);
                String task_id = (String) jTableAddMetrics.getValueAt(row, 3);
                String task = (String) jTableAddMetrics.getValueAt(row, 4);
                String net = (String) jTableAddMetrics.getValueAt(row, 5);
                String subnet = (String) jTableAddMetrics.getValueAt(row, 6);
                String act = (String) jTableAddMetrics.getValueAt(row, 7);
                String sap = (String) jTableAddMetrics.getValueAt(row, 8);
                String date = (String) jTableAddMetrics.getValueAt(row, 9);
                // Format date to insert into DB
                date = date.replace('/', '-');
                String[] date_parts = date.split("-");
                String year = date_parts[0];
                String month = date_parts[1];
                String day = date_parts[2];
                if (year.contains("00")) {
                    year = year.replace("00", "20");
                }
                date = year + "-" + month + "-" + day;

                String time = (String) jTableAddMetrics.getValueAt(row, 10);
                String week = (String) jTableAddMetrics.getValueAt(row, 11);
                String ftr = (String) jTableAddMetrics.getValueAt(row, 12);
                String ot = (String) jTableAddMetrics.getValueAt(row, 13);
                String fail_ftr = (String) jTableAddMetrics.getValueAt(row, 14);
                String fail_ot = (String) jTableAddMetrics.getValueAt(row, 15);
                String req = (String) jTableAddMetrics.getValueAt(row, 16);
                String comments = (String) jTableAddMetrics.getValueAt(row, 17);
                String tech = "N/A";
                String cu = "N/A";
                String market = "N/A";
                Float.parseFloat(time);
                Integer.parseInt(week);
                Integer.parseInt(req);

                if (requestor.isEmpty() || requestor.equals("")) {
                    requestor = "N/A";
                }
                if (req.isEmpty() || req.equals("")) {
                    req = "0";
                }
                if (comments.isEmpty() || comments.equals("")) {
                    comments = "N/A";
                }
                String org = usersinfo.get(4);
                String name = usersinfo.get(1);

                callableStatement.setObject(1, region);
                callableStatement.setObject(2, org);
                callableStatement.setObject(3, signum);
                callableStatement.setObject(4, name);
                callableStatement.setObject(5, requestor);
                callableStatement.setObject(6, task_id);
                callableStatement.setObject(7, task);
                callableStatement.setObject(8, net);
                callableStatement.setObject(9, subnet);
                callableStatement.setObject(10, act);
                callableStatement.setObject(11, sap);
                callableStatement.setObject(12, date);
                callableStatement.setObject(13, time);
                callableStatement.setObject(14, week);
                callableStatement.setObject(15, ftr);
                callableStatement.setObject(16, ot);
                callableStatement.setObject(17, fail_ftr);
                callableStatement.setObject(18, fail_ot);
                callableStatement.setObject(19, req);
                callableStatement.setObject(20, comments);
                callableStatement.setObject(21, tech);
                callableStatement.setObject(22, cu);
                callableStatement.setObject(23, market);
                //callableStatement.registerOutParameter(21, java.sql.Types.INTEGER);
                callableStatement.addBatch();
            }
            callableStatement.executeBatch();
            System.out.println(callableStatement);
            ClearDataPanel1();
            // Clear table
            DefaultTableModel tblModel = (DefaultTableModel) jTableAddMetrics.getModel();
            tblModel.setRowCount(0);
            callableStatement.close();
            connection.close();

            // Refresh total hours
            GetHours();
            System.out.println("Total hours this week: " + hours);
            this.setTitle("SOURCING               " + usersinfo.get(0) + " | " + usersinfo.get(4) + " | " + usersinfo.get(1) + " | " + "Week: " + current_week + " | "
                    + "Hours: " + hours);
            saved = "Data saved successfully!";
        } catch (SQLException e) {
            System.out.println(e);
            saved = "Something went wrong, please try again later.";
            // Clear table
            DefaultTableModel tblModel = (DefaultTableModel) jTableAddMetrics.getModel();
            tblModel.setRowCount(0);
        }
        System.out.println("After insert: " + tasks_info);
    }

    private void ClearDataPanel1() {
        // Clear table
        DefaultTableModel tblModel = (DefaultTableModel) jTableAddMetrics.getModel();
        tblModel.setRowCount(0);
        // Clear fields
        jcbTask.setSelectedIndex(0);
        jcbSAP.setSelectedItem("Billable");
        jTextFieldTask_ID.setText("");
        jTextFieldTime.setText("");
        jcbSubnet.setSelectedIndex(0);
        jTextFieldNetwork.setText("");
        jTextFieldActivity.setText("");
        jTextFieldRequestor.setText("");
        jcbFTR.setSelectedIndex(0);
        jcbOnTime.setSelectedIndex(0);
        jcbFailedFTR.setSelectedIndex(0);
        jcbFailedOnTime.setSelectedIndex(0);
        jTextFieldRequests.setText("");
        jRSingle.setSelected(true);
        jRBulk.setSelected(false);
        // Populate jcbTask1 with Billable default
        jcbTask1.addItem("Select an activity...");
        for (int i = 0; i < tasks_info.size(); i++) {
            String check = tasks_info.get(i);
            if (check.equals("Billable")) {
                // System.out.println(i);
                int a = i - 3;
                jcbTask1.addItem(tasks_info.get(a));
            }
        }
        Date date = new Date();
        jDateChooser1.setDate(date);
    }

    private void GetTeams() {
        String[] teams_ = usersinfo.get(7).split("@");
        List<String> teams = new ArrayList<String>(Arrays.asList(teams_));
        teams.add(usersinfo.get(3));
        jcbTeam1.removeAllItems();
        jcbTeam1.addItem("Sourcing");
        if (!usersinfo.get(3).equals("Sourcing")) {
            jcbTeam1.addItem(usersinfo.get(3));
        }
        if (!usersinfo.get(7).equals("N/A")) {
            for (int i = 0; i < teams.size(); i++) {
                if (!teams.get(i).equals("Sourcing")) {
                    jcbTeam1.addItem(teams.get(i));
                    JMenuItem menu = new JMenuItem(teams.get(i));
                    menu.setFont(new Font("Ericsson Hilda", 0, 18));
                    jMenu4.add(menu);
                    menu.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent ev) {
                            String clicked_on = menu.getText();
                            try {
                                // Del menu seleccionado abrir su clase
                                if (clicked_on.equals("COP")) {
                                    jDLoading.setModal(true);
                                    jDLoading.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                COP_Time_Report time_r = new COP_Time_Report();
                                                time_r.show();
                                                time_r.setLocationRelativeTo(null);
                                                // Confirm exit window
                                                time_r.setDefaultCloseOperation(time_r.DO_NOTHING_ON_CLOSE);
                                                time_r.addWindowListener(new java.awt.event.WindowAdapter() {
                                                    @Override
                                                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                                                        if (JOptionPane.showConfirmDialog(time_r, "Are you sure you want to close this window?", "Exit COP",
                                                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                                                            time_r.dispose();

                                                        }
                                                    }
                                                });
                                                jDLoading.dispose();
                                                time_r.toFront();
                                                time_r.requestFocus();
                                            } catch (ParseException | IOException ex) {
                                                Logger.getLogger(Sourcing_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    }).start();
                                    jDLoading.setVisible(true);
                                } else if (clicked_on.equals("VSS")) {
                                    jDLoading.setModal(true);
                                    jDLoading.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                VSS_Time_Report time_r = new VSS_Time_Report();
                                                time_r.show();
                                                time_r.setLocationRelativeTo(null);
                                                // Confirm exit window
                                                time_r.setDefaultCloseOperation(time_r.DO_NOTHING_ON_CLOSE);
                                                time_r.addWindowListener(new java.awt.event.WindowAdapter() {
                                                    @Override
                                                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                                                        if (JOptionPane.showConfirmDialog(time_r, "Are you sure you want to close this window?", "Exit VSS",
                                                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                                                            time_r.dispose();

                                                        }
                                                    }
                                                });
                                                jDLoading.dispose();
                                                time_r.toFront();
                                                time_r.requestFocus();
                                            } catch (ParseException | IOException ex) {
                                                Logger.getLogger(Sourcing_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    }).start();
                                    jDLoading.setVisible(true);
                                }
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                        }
                    });

                }
            }
        }
        jcbTeam1.addItem("All teams");
        System.out.println("Available teams: " + teams);
    }

    private void GetHours() {
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultset;
        String time = null;
        try {
            connection = SQL_connection.getConnection();
            String sql_user_time = "SELECT SUM(Logged_Time) AS Hours FROM metrics_sourcing WHERE Signum='" + usersinfo.get(0) + "' AND Week=" + current_week + ";";
            preparedStatement = connection.prepareStatement(sql_user_time);
            resultset = preparedStatement.executeQuery();
            while (resultset.next()) {
                time = resultset.getString("Hours");
            }
            if (time != null && !time.isEmpty()) {
                hours = Float.parseFloat(time);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void GetTasks() {
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultset;
        // Get users info to request proper tasks
        try {
            String sql_task = null;

            sql_task = "SELECT * "
                    + "FROM tasks "
                    + "WHERE Team='SDU' OR Team='Sourcing' "
                    + "ORDER BY Task ASC;";

            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement(sql_task);
            resultset = preparedStatement.executeQuery();

            while (resultset.next()) {
                String[] row = {resultset.getString("Task_ID"), resultset.getString("Task"), resultset.getString("Team"),
                    resultset.getString("Customer_Unit"), resultset.getString("SAP_Billable")};
                // If task has same name but different task_id
                for (int i = 0; i < tasks_info.size(); i++) {
                    if (tasks_info.get(i).equals(row[1])) {
                        row[1] = row[1] + "  ";
                    }
                }
                List<String> newList = Arrays.asList(row);

                tasks_info.addAll(newList);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void GetSubnetworks() {
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultset;
        try {
            // Get users info to request proper tasks
            String sql_task = "SELECT * "
                    + "FROM networks "
                    + "WHERE Team = 'Sourcing';";
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement(sql_task);
            resultset = preparedStatement.executeQuery();

            while (resultset.next()) {
                String[] row = {resultset.getString("Network"), resultset.getString("Activity_code"),
                    resultset.getString("Region"), resultset.getString("Market"), resultset.getString("Customer"),
                    resultset.getString("Subnetwork"), resultset.getString("Team")};
                List<String> newList = Arrays.asList(row);

                networks_info.addAll(newList);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void GetDailyHours() {
        try {
            Connection connection = SQL_connection.getConnection();
            ResultSet resultSet;

            String sql = "SELECT SUM(Logged_Time) AS Hours, WEEKDAY(Work_date) AS Day "
                    + "FROM metrics_sourcing "
                    + "WHERE Signum = ? AND Week = ? "
                    + "GROUP BY Work_date ORDER BY Work_date;";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, usersinfo.get(0));
            preparedStatement.setInt(2, Integer.parseInt(jTextFieldWeek.getText()));
            System.out.println("Query: " + preparedStatement);
            resultSet = preparedStatement.executeQuery();

            // Show results  
            String[] time = {"0\t", "0\t", "0\t", "0\t", "0\t", "0\t", "0\t"};
            while (resultSet.next()) {
                int index = Integer.valueOf(resultSet.getString("Day"));
                time[index] = resultSet.getString("Hours") + "\t";
            }
            jLabelHoursDaysH1.setText("<html><pre>Mon\tTue\tWed\tThu\tFri\tSat\tSun</pre></html>");
            jLabelHoursDaysB1.setText("<html><pre>" + time[0] + time[1] + time[2] + time[3] + time[4] + time[5] + time[6] + "</pre></html>");

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void GetDailyHours1() {
        try {
            Connection connection = SQL_connection.getConnection();
            ResultSet resultSet;

            String sql = "SELECT SUM(Logged_Time) AS Hours, WEEKDAY(Work_date) AS Day "
                    + "FROM metrics_sourcing "
                    + "WHERE Signum = ? AND Week = ? "
                    + "GROUP BY Work_date ORDER BY Work_date;";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, usersinfo.get(0));
            preparedStatement.setInt(2, Integer.parseInt(jcbWeek1.getSelectedItem().toString()));
            System.out.println("Query: " + preparedStatement);
            resultSet = preparedStatement.executeQuery();

            // Show results  
            String[] time = {"0\t", "0\t", "0\t", "0\t", "0\t", "0\t", "0\t"};
            while (resultSet.next()) {
                int index = Integer.valueOf(resultSet.getString("Day"));
                time[index] = resultSet.getString("Hours") + "\t";
            }
            jLabelHoursDaysH.setText("<html><pre>Mon\tTue\tWed\tThu\tFri\tSat\tSun</pre></html>");
            jLabelHoursDaysB.setText("<html><pre>" + time[0] + time[1] + time[2] + time[3] + time[4] + time[5] + time[6] + "</pre></html>");

        } catch (Exception e) {
            System.out.println(e);
        }
    }


    private void jBAddTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBAddTableActionPerformed
        // Add info to jTable button
        // Validate data type in date, time, requestor and requests textfields
        Pattern ptime = Pattern.compile("[^0-9.0-9]");
        String stime = jTextFieldTime.getText();
        Matcher mtime = ptime.matcher(stime);
        boolean btime = mtime.find();
        Pattern preq = Pattern.compile("[^0-9]");
        String sreq = jTextFieldRequests.getText();
        Matcher mreq = preq.matcher(sreq);
        boolean breq = mreq.find();
        Pattern preqtr = Pattern.compile("[^A-Za-z ]");
        String sreqtr = jTextFieldRequestor.getText();
        Matcher mreqtr = preqtr.matcher(sreqtr);
        boolean breqtr = mreqtr.find();
        int entries = 1;

        jRSingle.setSelected(true);
        int validate_date = Integer.parseInt(jTextFieldWeek.getText());
        int thresh1 = current_week - 2;
        int thresh2 = current_week + 2;

        if (jcbTask.getSelectedItem().equals("Select an activity...")) {
            JOptionPane.showMessageDialog(this, "Choose an activity!");
        } else if (jcbSubnet.getSelectedItem().equals("Select a subnetwork...")) {
            JOptionPane.showMessageDialog(this, "Choose a subnetwork!");
        } else if (breq) {  // Add here condition if task must have number of req
            JOptionPane.showMessageDialog(this, "Number of requests must be a number!");
            jTextFieldRequests.setText("");
        } else if (jTextFieldRequests.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Number of requests cannot be empty!");
        } else if (jTextFieldRequests.getText().equals("0")) {
            JOptionPane.showMessageDialog(this, "Number of requests cannot be zero!");
        } else if (stime.equals("") || btime) {
            JOptionPane.showMessageDialog(this, "Logged Time must be a number! \nEx: 2 or 8.25");
            jTextFieldTime.setText("");
        } else if (breqtr) {
            JOptionPane.showMessageDialog(this, "Requestor must be a a name!");
            jTextFieldRequestor.setText("");
        } else if (validate_date < thresh1) {
            JOptionPane.showMessageDialog(this, "You can only add data from week " + thresh1 + " until week " + thresh2);
        } else if (validate_date > thresh2) {
            if (current_week == 1 || current_week == 2) {

            } else {
                JOptionPane.showMessageDialog(this, "You can only add data from week " + thresh1 + " until week " + thresh2);
            }
            System.out.println("Not doing anything");
        } else {
            // Get combo boxes values
            String sap = (String) jcbSAP.getSelectedItem();
            String task_id = (String) jTextFieldTask_ID.getText();
            String task = (String) jcbTask.getSelectedItem();

            // Check for tasks with "same name"
            if (task.contains("  ")) {
                task = task.replace("  ", "");
            }
            // Check if day is sunday to substract -1 to week
            SimpleDateFormat dcn = new SimpleDateFormat("yyyy-MM-dd");
            String date = dcn.format(jDateChooser1.getDate());
            BigDecimal time = new BigDecimal(jTextFieldTime.getText());
            String week = (String) jTextFieldWeek.getText();
            String requestor = (String) jTextFieldRequestor.getText();
            String ftr = (String) jcbFTR.getSelectedItem();
            String on_time = (String) jcbOnTime.getSelectedItem();
            String fail_ftr = (String) jcbFailedFTR.getSelectedItem();
            String fail_on_time = (String) jcbFailedOnTime.getSelectedItem();
            String requests = (String) jTextFieldRequests.getText();
            String net = (String) jTextFieldNetwork.getText();
            String subnet = (String) jcbSubnet.getSelectedItem();
            String act = (String) jTextFieldActivity.getText();
            String comments = (String) jTextFieldComments.getText();

            if (requestor.isEmpty() || requestor.equals("")) {
                requestor = "N/A";
            }
            if (requests.isEmpty() || requests.equals("")) {
                requests = "1";
            }
            if (comments.isEmpty() || comments.equals("")) {
                comments = "N/A";
            }
            // if bulk or single
            if (jRBulk.isSelected()) {
                int req = 0;
                entries = (int) jSpinnerBulk.getValue();
                BigDecimal entries_bd = new BigDecimal(entries);
                jRSingle.setSelected(false);
                jSpinnerBulk.setVisible(true);
                req = Integer.parseInt(jTextFieldRequests.getText()) / entries;
                if (req == 0) {
                    req = 1;
                }
                requests = String.valueOf(req);
                time = new BigDecimal(jTextFieldTime.getText());
                // Round to 2 decimals
                time = time.divide(entries_bd, 2, RoundingMode.CEILING);
            }
            String time_added = String.valueOf(time);
            // Sourcing has only MANA as Region
            String region = "MANA";
            String[] data = {region, usersinfo.get(0), requestor, task_id, task, net, subnet, act,
                sap, date, time_added, week, ftr, on_time, fail_ftr, fail_on_time, requests, comments};
            DefaultTableModel tblModel = (DefaultTableModel) jTableAddMetrics.getModel();

            for (int i = 0; i < entries; i++) {
                tblModel.addRow(data);
            }
            jTextFieldTime.setText("");
            jTextFieldRequests.setText("");
            jTextFieldRequestor.setText("");
            jTextFieldComments.setText("");
            jRSingle.setSelected(true);
            jRBulk.setSelected(false);
            jSpinnerBulk.setVisible(false);
            jSpinnerBulk.setValue(((SpinnerNumberModel) jSpinnerBulk.getModel()).getMinimum());
        }
    }//GEN-LAST:event_jBAddTableActionPerformed

    private void jBDeleteRowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDeleteRowActionPerformed
        // Delete selected row button (Record metrics)
        DefaultTableModel tblModel = (DefaultTableModel) jTableAddMetrics.getModel();
        // If selected row
        if (jTableAddMetrics.getSelectedRowCount() == 1) {          // If only one row is selected
            tblModel.removeRow(jTableAddMetrics.getSelectedRow());
        } else if (jTableAddMetrics.getSelectedRowCount() > 1) {    // If more than one row are selected
            int[] selected_rows = jTableAddMetrics.getSelectedRows();
            for (int i = 0; i < selected_rows.length; i++) {
                tblModel.removeRow(jTableAddMetrics.getSelectedRow());
            }
        } else {
            if (jTableAddMetrics.getRowCount() == 0) {
                // If table empty
                JOptionPane.showMessageDialog(this, "Table is empty.");
            } else if (jTableAddMetrics.getRowCount() == 1) {
                tblModel.removeRow(jTableAddMetrics.getRowCount() - 1);
                JOptionPane.showMessageDialog(this, "Table is empty.");
            } else {
                // Delete last row
                tblModel.removeRow(jTableAddMetrics.getRowCount() - 1);
            }
        }
    }//GEN-LAST:event_jBDeleteRowActionPerformed

    private void jBClearTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBClearTableActionPerformed
        // Clear table button
        DefaultTableModel tblModel = (DefaultTableModel) jTableAddMetrics.getModel();
        tblModel.setRowCount(0);
        JOptionPane.showMessageDialog(this, "Table is empty.");
        imported_file = null;
        imported = false;
    }//GEN-LAST:event_jBClearTableActionPerformed

    private void jcbTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbTaskActionPerformed
        //Set task id field
        String task = (String) jcbTask.getSelectedItem();
        for (int i = 0; i < tasks_info.size(); i++) {
            String task_list_element = tasks_info.get(i);
            if (task_list_element.equals(task)) {
                int a = i - 1;
                jTextFieldTask_ID.setText(tasks_info.get(a));
            }
        }
        if (jcbTask.getSelectedIndex() == 0) {
            jTextFieldTask_ID.setText("");
        }
    }//GEN-LAST:event_jcbTaskActionPerformed

    private void jRSingleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRSingleActionPerformed
        // Radio button 'Single' action
        jRSingle.setSelected(true);
        jRBulk.setSelected(false);
        jSpinnerBulk.setVisible(false);
    }//GEN-LAST:event_jRSingleActionPerformed

    private void jRBulkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBulkActionPerformed
        // Radio button 'Bulk' action
        jRBulk.setSelected(true);
        jRSingle.setSelected(false);
        jSpinnerBulk.setVisible(true);
        ((DefaultEditor) jSpinnerBulk.getEditor()).getTextField().setEditable(false);
    }//GEN-LAST:event_jRBulkActionPerformed

    private void jMenuItemOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenActionPerformed
        // Menu item 'Import'
        jPanel1.setVisible(true);
        jPanel2.setVisible(false);
        jPanel3.setVisible(false);
        final JFileChooser fc = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("CSV file", "csv");
        fc.setFileFilter(filter);
        int response = fc.showOpenDialog(this);
        if (response == JFileChooser.APPROVE_OPTION) {
            imported_file = fc.getSelectedFile().toString();
            System.out.println(imported_file);
            jPanel2.setVisible(false);
            jPanel1.setVisible(true);

            DefaultTableModel tblModel = (DefaultTableModel) jTableAddMetrics.getModel();
            tblModel.setRowCount(0);
            try (BufferedReader br = new BufferedReader(new FileReader(imported_file))) {
                String line;
                br.readLine();

                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    List<String> finalValues = new ArrayList<String>();
                    String full_task = "";
                    String full_comment = "";
                    int commas_size = values.length, j = 0;

                    for (int i = 0; i < commas_size; i++) {
                        if (i < 4) {
                            finalValues.add(values[i]);
                            j = j + 1;
                        } else {
                            if (j == 4) {
                                if (values[i + 1].matches("\\d+")) {
                                    full_task += values[i];
                                    full_task = full_task.replace("\"", "");
                                    finalValues.add(full_task);
                                    j += 1;
                                } else {
                                    full_task += values[i] + ",";
                                }
                            } else if (j == 17) {
                                if (i == commas_size - 1) {
                                    full_comment += values[i];
                                    full_comment = full_comment.replace("\"", "");
                                    finalValues.add(full_comment);
                                    j += 1;
                                } else {
                                    full_comment += values[i] + ",";

                                }
                            } else {
                                finalValues.add(values[i]);
                                j = j + 1;
                            }
                        }
                    }
                    // Change date format
                    SimpleDateFormat dcn = new SimpleDateFormat("MM/dd/yyyy");
                    Date new_date = null;
                    try {
                        new_date = dcn.parse(finalValues.get(9));
                    } catch (ParseException ex) {
                        Logger.getLogger(COP_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    SimpleDateFormat dcn1 = new SimpleDateFormat("yyyy-MM-dd");
                    finalValues.set(9, dcn1.format(new_date));

                    String[] row = new String[finalValues.size()];
                    finalValues.toArray(row);
                    tblModel.addRow(row);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Sourcing_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Sourcing_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        imported = true;
    }//GEN-LAST:event_jMenuItemOpenActionPerformed

    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExitActionPerformed
        // Menu exit button
        System.exit(0);
    }//GEN-LAST:event_jMenuItemExitActionPerformed

    private void jMenuItemGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGenerateActionPerformed
        // Menu option Generate template
        jPanel1.setVisible(true);
        jPanel2.setVisible(false);
        jPanel3.setVisible(false);
        String path = "C:\\Users\\" + usersinfo.get(0) + "\\Documents\\Reporting_time_" + usersinfo.get(0) + "_template.csv";
        try (PrintWriter writer = new PrintWriter(new File(path))) {
            StringBuilder sb = new StringBuilder();
            SimpleDateFormat dcn = new SimpleDateFormat("MM/dd/yyyy");
            String date_change = dcn.format(jDateChooser1.getDate());
            System.out.println("Date: " + date_change);

            // Header
            sb.append("Region" + ',');
            sb.append("Signum" + ',');
            sb.append("Requestor" + ',');
            sb.append("Task_ID" + ',');
            sb.append("Task" + ',');
            sb.append("Network" + ',');
            sb.append("Subnetwork" + ',');
            sb.append("Activity_code" + ',');
            sb.append("SAP_Billing" + ',');
            sb.append("Work_date" + ',');
            sb.append("Logged_Time" + ',');
            sb.append("Week" + ',');
            sb.append("FTR" + ',');
            sb.append("On_Time" + ',');
            sb.append("Failed_FTR_Category" + ',');
            sb.append("Failed_On_Time" + ',');
            sb.append("Number_of_requests" + ',');
            sb.append("Comments" + ',');
            sb.append('\n');
            // First row
            sb.append("MANA" + ',');
            sb.append(usersinfo.get(0) + ',');
            sb.append("N/A" + ',');
            sb.append(tasks_info.get(0) + ',');
            sb.append(tasks_info.get(1) + ',');
            if (tasks_info.get(0).contains("ADMIN")) {
                sb.append("0" + ',');
                sb.append("0" + ',');
                sb.append("0" + ',');
            } else {
                sb.append(networks_info.get(0) + ',');
                sb.append(networks_info.get(5) + ',');
                sb.append(networks_info.get(1) + ',');
            }
            sb.append(tasks_info.get(4) + ',');
            sb.append(date_change + ',');
            sb.append("8.25" + ',');
            sb.append(current_week);
            sb.append(',');
            sb.append("N/A" + ',');
            sb.append("N/A" + ',');
            sb.append("N/A" + ',');
            sb.append("N/A" + ',');
            sb.append("0" + ',');
            sb.append("N/A" + ',');
            sb.append('\n');
            // Second row
            sb.append("MANA" + ',');
            sb.append(usersinfo.get(0) + ',');
            sb.append("N/A" + ',');
            sb.append(tasks_info.get(5) + ',');
            sb.append(tasks_info.get(6) + ',');
            if (tasks_info.get(4).contains("ADMIN")) {
                sb.append("0" + ',');
                sb.append("0" + ',');
                sb.append("0" + ',');
            } else {
                sb.append(networks_info.get(0) + ',');
                sb.append(networks_info.get(5) + ',');
                sb.append(networks_info.get(1) + ',');
            }
            sb.append(tasks_info.get(9) + ',');
            sb.append(date_change + ',');
            sb.append("8.25" + ',');
            sb.append(current_week);
            sb.append(',');
            sb.append("N/A" + ',');
            sb.append("N/A" + ',');
            sb.append("N/A" + ',');
            sb.append("N/A" + ',');
            sb.append("0" + ',');
            sb.append("N/A" + ',');
            sb.append('\n');

            writer.write(sb.toString());
            System.out.println("Template generated successfully");
            JOptionPane.showMessageDialog(this, "Template file was saved to " + path);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }//GEN-LAST:event_jMenuItemGenerateActionPerformed

    private void jMenuItemEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEditActionPerformed
        // Menu 'Edit' option
        jPanel1.setVisible(false);
        jPanel2.setVisible(true);
        jPanel3.setVisible(false);
        jDateChooser2.setVisible(true);
        jLWeek1.setVisible(false);
        jcbWeek1.setVisible(false);
        jCheckBoxWeek.setSelected(false);
        jcbSAP1.setSelectedIndex(0);
        GetDailyHours1();
        // Hide id
        TableColumnModel tcm = jTableSeeMetrics.getColumnModel();
        if (times_in_edit == 0) {
            tcm.removeColumn(tcm.getColumn(0));
        }
        times_in_edit += 1;
        DefaultTableModel tblModel = (DefaultTableModel) jTableSeeMetrics.getModel();
        tblModel.setRowCount(0);
        GetDailyHours1();
    }//GEN-LAST:event_jMenuItemEditActionPerformed

    private void jMenuItemRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRecordActionPerformed
        // Menu 'Record' option
        jPanel3.setVisible(false);
        jPanel2.setVisible(false);
        jPanel1.setVisible(true);
        ClearDataPanel1();
        GetHours();
        GetDailyHours();
    }//GEN-LAST:event_jMenuItemRecordActionPerformed

    private void jcbSAP1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbSAP1ActionPerformed
        // SAP1 combobox action
        String switch_ = (String) jcbSAP1.getSelectedItem();
        String team = (String) jcbTeam1.getSelectedItem();
        jcbTask1.removeAllItems();
        jcbTask1.addItem("Select an activity...");
        if (team.equals("Sourcing")) {
            for (int i = 0; i < tasks_info.size(); i++) {
                String check = tasks_info.get(i);
                if (switch_.equals("All")) {
                    if (check.equals("Billable") || check.equals("Not Billable")) {
                        int a = i - 3;
                        jcbTask1.addItem(tasks_info.get(a));
                    }
                } else if (switch_.equals("Billable")) {
                    if (check.equals(switch_)) {
                        int a = i - 3;
                        jcbTask1.addItem(tasks_info.get(a));
                    }
                } else {
                    if (check.equals(switch_)) {
                        int a = i - 3;
                        jcbTask1.addItem(tasks_info.get(a));
                    }
                }
            }
        }
    }//GEN-LAST:event_jcbSAP1ActionPerformed

    private void jBSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSearchActionPerformed
        // Button Search action
        jLabelLoading.setText("Loading...");
        DefaultTableModel dtm;
        jBDeleteRow1.setVisible(true);
        jBUpdate.setVisible(true);
        dtm = (DefaultTableModel) this.jTableSeeMetrics.getModel();
        String team = (String) jcbTeam1.getSelectedItem();
        ArrayList<String> tables = new ArrayList<>();
        String[] strs = {"metrics_sourcing", "metrics_cop", "metrics_vss"};
        for (int i = 0; i < strs.length; i++) {
            tables.add(strs[i]); // Check for every table
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                GetDailyHours1();
                int a = 0, j = 0;
                if (team.equals("All teams")) {
                    a = tables.size();
                    jBDeleteRow1.setVisible(false);
                    jBUpdate.setVisible(false);
                } else {
                    a = 1;
                }

                metrics_sourcing_info.clear();
                DefaultTableModel model = (DefaultTableModel) jTableSeeMetrics.getModel();
                model.setRowCount(0);
                Connection connection = SQL_connection.getConnection();
                ResultSet resultSet;
                ResultSetMetaData rsm;

                String qdate = null, sap = null;
                int year = Calendar.getInstance().get(Calendar.YEAR);
                if (jCheckBoxWeek.isSelected()) {
                    qdate = "Week=? AND Work_date LIKE '" + year + "-%'";
                } else {
                    qdate = "Work_date=?";
                }

                while (j < a) {
                    try {
                        String sql_table = null;
                        if (a == 1) {
                            sql_table = "metrics_" + team.toLowerCase();
                        } else {
                            sql_table = tables.get(j);
                        }
                        String sql = "SELECT id, Region, Signum, Requestor, Task_ID, "
                                + "Task, Network, Subnetwork, Activity_code, SAP_Billing, date_format(Work_date, '%m/%d/%Y') AS Work_date, "
                                + "Logged_Time, Week, FTR, On_Time, Failed_FTR_Category, Failed_On_Time, Num_requests, Comments "
                                + "FROM " + sql_table + " "
                                + "WHERE Signum=? AND " + qdate + " AND Task LIKE ? AND SAP_Billing LIKE ? ORDER BY Work_date ASC;";

                        PreparedStatement preparedStatement = connection.prepareStatement(sql);
                        String task = jcbTask1.getSelectedItem().toString();
                        System.out.println(sql);
                        if (task.equals("Select an activity...")) {
                            task = "%%";
                        }
                        preparedStatement.setString(1, usersinfo.get(0));
                        if (jCheckBoxWeek.isSelected()) {
                            preparedStatement.setString(2, jcbWeek1.getSelectedItem().toString());
                        } else {
                            SimpleDateFormat dcn = new SimpleDateFormat("yyyy-MM-dd");
                            String date = dcn.format(jDateChooser2.getDate());
                            preparedStatement.setString(2, date);
                        }
                        preparedStatement.setString(3, task);

                        if (jcbSAP1.getSelectedItem().toString().equals("All")) {
                            sap = "%%";
                        } else {
                            sap = (String) jcbSAP1.getSelectedItem();
                        }
                        preparedStatement.setString(4, sap);
                        System.out.println("Query: " + preparedStatement);
                        resultSet = preparedStatement.executeQuery();
                        rsm = resultSet.getMetaData();

                        // Show results in table            
                        ArrayList<Object[]> data = new ArrayList<>();
                        while (resultSet.next()) {
                            Object[] rows = new Object[rsm.getColumnCount()];
                            for (int i = 0; i < rows.length; i++) {
                                rows[i] = resultSet.getObject(i + 1);
                            }
                            data.add(rows);

                            String[] row = {resultSet.getString("id"), resultSet.getString("Signum"), resultSet.getString("Requestor"),
                                resultSet.getString("Work_date"), resultSet.getString("Logged_Time"), resultSet.getString("Comments")};
                            List<String> newList = Arrays.asList(row);

                            metrics_sourcing_info.addAll(newList);
                        }
                        System.out.println("Metrics: " + metrics_sourcing_info);

                        for (int i = 0; i < data.size(); i++) {
                            dtm.addRow(data.get(i));
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(Sourcing_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    j += 1;
                }
                try {
                    connection.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Sourcing_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                }
                //
                jDLoading.dispose();
                if (model.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(Sourcing_Time_Report.this, "No data available for current selection");
                }
            }
        }).start();
        jDLoading.setVisible(true);
    }//GEN-LAST:event_jBSearchActionPerformed

    private void jCheckBoxWeekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxWeekActionPerformed
        // Checkbox week action
        if (jCheckBoxWeek.isSelected()) {
            jLDate1.setVisible(false);
            jDateChooser2.setVisible(false);
            jLWeek1.setVisible(true);
            jcbWeek1.setVisible(true);
        } else {
            jLWeek1.setVisible(false);
            jcbWeek1.setVisible(false);
            jLDate1.setVisible(true);
            jDateChooser2.setVisible(true);
        }
    }//GEN-LAST:event_jCheckBoxWeekActionPerformed

    private void jBUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBUpdateActionPerformed
        // Button 'Update' action code
        if (jTableSeeMetrics.getEditingRow() != -1) {
            jTableSeeMetrics.getCellEditor().stopCellEditing();// In case there's a selected field in the table
        }

        jLabelLoading.setText("Updating metrics into database...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String team = jcbTeam1.getSelectedItem().toString();
                Connection connection;
                CallableStatement callableStatement;
                int rows = jTableSeeMetrics.getRowCount();
                if (rows > 0) {
                    connection = SQL_connection.getConnection();
                    int id = 0;

                    ArrayList<String> compare = new ArrayList<>();
                    String signum = null, date = null, requestor = null, comments = null, time = null;

                    for (int row = 0; row < rows; row++) {
                        signum = (String) jTableSeeMetrics.getValueAt(row, 1);
                        requestor = (String) jTableSeeMetrics.getValueAt(row, 2);
                        date = (String) jTableSeeMetrics.getValueAt(row, 9);
                        time = String.valueOf(jTableSeeMetrics.getValueAt(row, 10));
                        comments = (String) jTableSeeMetrics.getValueAt(row, 17);
                        id = (Integer) jTableSeeMetrics.getModel().getValueAt(row, 0);
                        System.out.println(signum + requestor + date + time + comments + id);
                        if (time.equals("0") || time.equals("")) {
                            JOptionPane.showMessageDialog(Sourcing_Time_Report.this, "Logged Time cannot be 0.0");
                            jTableSeeMetrics.setValueAt("", row, 10);
                            jDLoading.dispose();
                            return;
                        }

                        // Check if there were any changes to only update those
                        compare.add(Integer.toString(id));
                        compare.add(signum);
                        compare.add(requestor);

                        SimpleDateFormat dcn = new SimpleDateFormat("MM/dd/yyyy");
                        Date new_date = null;
                        try {
                            new_date = dcn.parse(date);
                        } catch (ParseException ex) {
                            Logger.getLogger(COP_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        SimpleDateFormat dcn1 = new SimpleDateFormat("yyyy-MM-dd");
                        date = dcn1.format(new_date);
                        compare.add(date);

                        compare.add(time);
                        compare.add(comments);
                    }
                    ArrayList<String> ids = new ArrayList<>();
                    int id_ch = 0;
                    for (int i = 0; i < metrics_sourcing_info.size(); i += 6) {
                        System.out.println(metrics_sourcing_info.get(i) + " | " + compare.get(i));
                        if (metrics_sourcing_info.get(i).equals(compare.get(i))) {
                            if (!(metrics_sourcing_info.get(i + 2).equals(compare.get(i + 2))) || !(metrics_sourcing_info.get(i + 4).equals(compare.get(i + 4)))
                                    || !(metrics_sourcing_info.get(i + 5).equals(compare.get(i + 5)))) {
                                ids.add(compare.get(i));
                                ids.add(compare.get(i + 1));
                                ids.add(compare.get(i + 2));
                                ids.add(compare.get(i + 3));
                                ids.add(compare.get(i + 4));
                                ids.add(compare.get(i + 5));
                                id_ch += 1;
                            }
                        }
                    }
                    System.out.println("IDs changed: " + id_ch);
                    System.out.println(metrics_sourcing_info);
                    System.out.println(compare + "\n");
                    System.out.println(ids);
                    // Update database if changes were made
                    String sql = null;
                    if (team.equals("Sourcing")) {
                        sql = "update_metrics_sourcing";
                    } else if (team.equals("COP")) {
                        sql = "update_metrics_cop";
                    } else if (team.equals("VSS")) {
                        sql = "update_metrics_vss";
                    }

                    if (id_ch > 0) {
                        try {
                            callableStatement = connection.prepareCall("CALL " + sql
                                    + "(?, ?, ?, ?, ?, ?);");
                            for (int row = 0; row < id_ch; row++) {
                                callableStatement.setObject(1, ids.get(0 + 6 * row)); // id
                                callableStatement.setObject(2, ids.get(1 + 6 * row)); // signum
                                if (ids.get(2 + 6 * row).equals("")) {
                                    callableStatement.setObject(3, "N/A");
                                } else {
                                    callableStatement.setObject(3, ids.get(2 + 6 * row)); // requestor
                                }
                                callableStatement.setObject(4, ids.get(3 + 6 * row)); // date
                                callableStatement.setObject(5, ids.get(4 + 6 * row)); // time
                                if (ids.get(5 + 6 * row).equals("")) {
                                    callableStatement.setObject(6, "N/A");
                                } else {
                                    callableStatement.setObject(6, ids.get(5 + 6 * row)); // comments
                                }
                                callableStatement.addBatch();
                                System.out.println(callableStatement);
                                callableStatement.executeBatch();
                            }
                            callableStatement.close();
                            connection.close();

                            metrics_sourcing_info.clear();
                            compare.clear();
                            DefaultTableModel tblModel = (DefaultTableModel) jTableSeeMetrics.getModel();
                            tblModel.setRowCount(0);

                            // Refresh total hours
                            GetHours();
                            Sourcing_Time_Report.this.setTitle("Sourcing               " + usersinfo.get(0) + " | " + usersinfo.get(4) + " | " + usersinfo.get(1) + " | " + "Week: " + current_week + " | "
                                    + "Hours: " + hours);
                            saved = "Data saved successfully!";
                        } catch (SQLException e) {
                            System.out.println(e);
                            saved = "Something went wrong, please try again later.";
                        }
                    } else {
                        JOptionPane.showMessageDialog(Sourcing_Time_Report.this, "Nothing changed.");
                    }
                }
                GetDailyHours1();
                jDLoading.dispose();
                JOptionPane.showMessageDialog(Sourcing_Time_Report.this, saved);
            }
        }).start();
        jDLoading.setVisible(true);
    }//GEN-LAST:event_jBUpdateActionPerformed

    private void jBDeleteRow1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDeleteRow1ActionPerformed
        // Delete row panel 2 button
        jLabelLoading.setText("Deleting metrics from the database...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!jTableSeeMetrics.getSelectionModel().isSelectionEmpty()) { // If there is a selected row
                    int dialogButton = JOptionPane.YES_NO_OPTION;
                    int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this?\nThe information will be lost.", "Warning", dialogButton);
                    if (dialogResult == 0) { // If 'YES'
                        DefaultTableModel tblModel = (DefaultTableModel) jTableSeeMetrics.getModel();
                        Connection connection = SQL_connection.getConnection();
                        PreparedStatement preparedStatement;
                        
                        int[] selected_rows = jTableSeeMetrics.getSelectedRows();
                        for (int j=0; j < selected_rows.length; j++) {
                            // Compare to get selected row's id 
                            int row = selected_rows[j];
                            if (j > 0){
                                row = selected_rows[j] - 1;
                            }
                            System.out.println("Selected row: "+ row);
                            String id_ = jTableSeeMetrics.getModel().getValueAt(row, 0).toString();
                            System.out.println("id_"+id_);
                            int id = Integer.parseInt(id_);
                            System.out.println("ID to delete: " + id);

                            // Delete row from list
                            int i = metrics_sourcing_info.indexOf(id_);
                            int a = 0;
                            System.out.println("Index:" + i);
                            while (a < 6) { // 6 elements in metrics_sourcing_info
                                metrics_sourcing_info.remove(i);
                                a += 1;
                            }
                            String team = jcbTeam1.getSelectedItem().toString();
                            String sql = null;
                            if (team.equals("Sourcing")) {
                                sql = "metrics_sourcing";
                            } else if (team.equals("COP")) {
                                sql = "metrics_cop";
                            } else if (team.equals("VSS")) {
                                sql = "metrics_vss";
                            }

                            try {
                                // DELETE FROM SQL
                                preparedStatement = connection.prepareStatement("DELETE FROM " + sql + " WHERE id = ?");
                                preparedStatement.setInt(1, id);
                                preparedStatement.executeUpdate();
                                System.out.println(preparedStatement);
                                // Delete table row
                                tblModel.removeRow(jTableSeeMetrics.getSelectedRow());
                            } catch (SQLException ex) {
                                Logger.getLogger(Sourcing_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }// End for
                        try {
                            connection.close();
                        } catch (SQLException ex) {
                            Logger.getLogger(Sourcing_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        GetDailyHours1();
                        JOptionPane.showMessageDialog(Sourcing_Time_Report.this, "Row deleted.");
                        System.out.println("After delete row: " + metrics_sourcing_info);
                    } 
                } else {
                    JOptionPane.showMessageDialog(Sourcing_Time_Report.this, "Select one row.");
                }
                jDLoading.dispose();
            }
        }).start();
        jDLoading.setVisible(true);
    }//GEN-LAST:event_jBDeleteRow1ActionPerformed

    private void jcbSAPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbSAPActionPerformed
        // SAP combobox action
        String sap = (String) jcbSAP.getSelectedItem();
        String cu = (String) jcbCU.getSelectedItem();
        System.out.println("Look here: " + sap);
        jcbTask.removeAllItems();
        jcbTask.addItem("Select an activity...");
        for (int i = 0; i < tasks_info.size(); i++) {
            String check = tasks_info.get(i);
            if (check.equals(sap)) {
                String check_team = tasks_info.get(i - 1);
                if (sap.equals("Not Billable")) {
                    if (check_team.equals("SDU")) {
                        //System.out.println(i);
                        int a = i - 3;
                        jcbTask.addItem(tasks_info.get(a));
                    }
                } else {
                    if (check_team.equals(cu)) {
                        //System.out.println(i);
                        int a = i - 3;
                        jcbTask.addItem(tasks_info.get(a));
                    }
                }
            }
        }
        // Refresh jcbSubnetwork
        jcbSubnet.removeAllItems();
        if (jcbSAP.getSelectedIndex() == 0) {
            jcbSubnet.addItem("Select a subnetwork...");
            for (int i = 0; i < networks_info.size(); i += 7) {
                jcbSubnet.addItem(networks_info.get(i + 5));
            }
        } else {
            jcbSubnet.addItem("0");
        }

    }//GEN-LAST:event_jcbSAPActionPerformed

    private void jBClearTable2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBClearTable2ActionPerformed
        // Clear table 2 (see metrics) action
        DefaultTableModel tblModel = (DefaultTableModel) jTableSeeMetrics.getModel();
        tblModel.setRowCount(0);
    }//GEN-LAST:event_jBClearTable2ActionPerformed

    private void jMenuItemMRTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMRTActionPerformed
        // Menu ABOUT action
        jPanel1.setVisible(false);
        jPanel2.setVisible(false);
        jPanel3.setVisible(true);
    }//GEN-LAST:event_jMenuItemMRTActionPerformed

    private void jcbSubnetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbSubnetActionPerformed
        // Subnetwork combobox
        String network = (String) jcbSubnet.getSelectedItem();
        for (int i = 0; i < networks_info.size(); i++) {
            String network_list_item = networks_info.get(i);
            if (network_list_item.equals(network)) {
                //System.out.println(i);
                int a = i - 5;
                jTextFieldNetwork.setText(networks_info.get(a));
                int b = i - 4;
                jTextFieldActivity.setText(networks_info.get(b));
            }
        }
        if (jcbSubnet.getSelectedIndex() == 0 && jcbSAP.getSelectedIndex() == 1) {
            jTextFieldNetwork.setText("0");
            jTextFieldActivity.setText("0");
        } else if (jcbSubnet.getSelectedIndex() == 0 && jcbSAP.getSelectedIndex() == 0) {
            jTextFieldNetwork.setText("");
            jTextFieldActivity.setText("");
        }
    }//GEN-LAST:event_jcbSubnetActionPerformed

    private void jcbCUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbCUActionPerformed
        // jcbCU action, fills tasks combobox with CU's tasks
        jcbTask.removeAllItems();
        jcbTask.addItem("Select an activity...");
        for (int i = 0; i < tasks_info.size(); i++) {
            String check_billable = tasks_info.get(i);
            if (check_billable.equals("Billable")) {
                String check_cu = tasks_info.get(i - 1);
                if (check_cu.equals(jcbCU.getSelectedItem().toString())) {
                    int a = i - 3;
                    jcbTask.addItem(tasks_info.get(a));
                }
            }
        }
    }//GEN-LAST:event_jcbCUActionPerformed

    private void jcbTeam1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbTeam1ActionPerformed
        // jcbTeam1 action, fill tasks in 'Edit' window
        String switch_ = (String) jcbSAP1.getSelectedItem();
        String team = (String) jcbTeam1.getSelectedItem();
        jcbTask1.removeAllItems();
        jcbTask1.addItem("Select an activity...");
        if (team.equals("Sourcing")) {
            for (int i = 0; i < tasks_info.size(); i++) {
                String check = tasks_info.get(i);
                if (switch_.equals("All")) {
                    if (check.equals("Billable") || check.equals("Not Billable")) {
                        int a = i - 3;
                        jcbTask1.addItem(tasks_info.get(a));
                    }
                } else if (switch_.equals("Billable")) {
                    if (check.equals(switch_)) {
                        int a = i - 3;
                        jcbTask1.addItem(tasks_info.get(a));
                    }
                } else {
                    if (check.equals(switch_)) {
                        int a = i - 3;
                        jcbTask1.addItem(tasks_info.get(a));
                    }
                }
            }
        }
    }//GEN-LAST:event_jcbTeam1ActionPerformed

    private void SaveTableCSV(String fileName, TableModel tableModel) {
        String path = "C:\\Users\\" + usersinfo.get(0) + "\\Documents\\" + fileName + ".csv";
        try (PrintWriter writer = new PrintWriter(new File(path))) {

            int columnas = tableModel.getColumnCount();
            int filas = tableModel.getRowCount();
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < columnas; j++) {
                sb.append(tableModel.getColumnName(j) + ",");
            }
            sb.append("\n");
            for (int i = 0; i < filas; i++) {
                for (int j = 0; j < columnas; j++) {
                    String s = String.valueOf(tableModel.getValueAt(i, j));
                    s = s.replace(",", "");
                    sb.append(s + ",");
                }
                sb.append("\n");
            }
            writer.write(sb.toString());
            System.out.println("Metrics file saved successfully");
            JOptionPane.showMessageDialog(this, "File was saved to " + path + " successfully.");
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void jBExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBExportActionPerformed
        // Export button, save what's in table to csv
        String week = jcbWeek1.getSelectedItem().toString();
        String team = jcbTeam1.getSelectedItem().toString();
        String fileName = usersinfo.get(0) + "_metrics_" + team + "_week_" + week;
        TableModel model = jTableSeeMetrics.getModel();
        SaveTableCSV(fileName, model);
    }//GEN-LAST:event_jBExportActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Sourcing_Time_Report.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Sourcing_Time_Report.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Sourcing_Time_Report.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Sourcing_Time_Report.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Sourcing_Time_Report().setVisible(true);

                } catch (ParseException | IOException ex) {
                    Logger.getLogger(Sourcing_Time_Report.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBAddTable;
    private javax.swing.JButton jBClearTable;
    private javax.swing.JButton jBClearTable2;
    private javax.swing.JButton jBDeleteRow;
    private javax.swing.JButton jBDeleteRow1;
    private javax.swing.JButton jBExport;
    private javax.swing.JButton jBInsert;
    private javax.swing.JButton jBSearch;
    private javax.swing.JButton jBUpdate;
    private javax.swing.JCheckBox jCheckBoxWeek;
    private javax.swing.JDialog jDLoading;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLActivity;
    private javax.swing.JLabel jLCU1;
    private javax.swing.JLabel jLComments;
    private javax.swing.JLabel jLDate;
    private javax.swing.JLabel jLDate1;
    private javax.swing.JLabel jLFTR;
    private javax.swing.JLabel jLFailFTR;
    private javax.swing.JLabel jLFaildeOnTime;
    private javax.swing.JLabel jLHoursForLoggedTime;
    private javax.swing.JLabel jLNetwork;
    private javax.swing.JLabel jLOnTime;
    private javax.swing.JLabel jLRequestor;
    private javax.swing.JLabel jLRequests;
    private javax.swing.JLabel jLSAP_Billing;
    private javax.swing.JLabel jLSAP_Billing1;
    private javax.swing.JLabel jLTask;
    private javax.swing.JLabel jLTask1;
    private javax.swing.JLabel jLTaskID;
    private javax.swing.JLabel jLTime;
    private javax.swing.JLabel jLWeek;
    private javax.swing.JLabel jLWeek1;
    private javax.swing.JLabel jLabelDagaz;
    private javax.swing.JLabel jLabelDescAbout;
    private javax.swing.JLabel jLabelDescDagaz;
    private javax.swing.JLabel jLabelDescNaudiz;
    private javax.swing.JLabel jLabelDescRaido;
    private javax.swing.JLabel jLabelHoursDaysB;
    private javax.swing.JLabel jLabelHoursDaysB1;
    private javax.swing.JLabel jLabelHoursDaysB2;
    private javax.swing.JLabel jLabelHoursDaysH;
    private javax.swing.JLabel jLabelHoursDaysH1;
    private javax.swing.JLabel jLabelLoading;
    private javax.swing.JLabel jLabelLoading1;
    private javax.swing.JLabel jLabelLogo;
    private javax.swing.JLabel jLabelNaudiz;
    private javax.swing.JLabel jLabelRaido;
    private javax.swing.JLabel jLabelSubnetwork;
    private javax.swing.JLabel jLabelSupport;
    private javax.swing.JLabel jLabelTeam;
    private javax.swing.JLabel jLabelTeam1;
    private javax.swing.JLabel jLabelVersion;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemEdit;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemGenerate;
    private javax.swing.JMenuItem jMenuItemMRT;
    private javax.swing.JMenuItem jMenuItemOpen;
    private javax.swing.JMenuItem jMenuItemRecord;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelLoading;
    private javax.swing.JRadioButton jRBulk;
    private javax.swing.JRadioButton jRSingle;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSpinner jSpinnerBulk;
    private javax.swing.JTable jTableAddMetrics;
    private javax.swing.JTable jTableSeeMetrics;
    private javax.swing.JTextField jTextFieldActivity;
    private javax.swing.JTextField jTextFieldComments;
    private javax.swing.JTextField jTextFieldNetwork;
    private javax.swing.JTextField jTextFieldRequestor;
    private javax.swing.JTextField jTextFieldRequests;
    private javax.swing.JTextField jTextFieldTask_ID;
    private javax.swing.JTextField jTextFieldTime;
    private javax.swing.JTextField jTextFieldWeek;
    private javax.swing.JComboBox<String> jcbCU;
    private javax.swing.JComboBox<String> jcbFTR;
    private javax.swing.JComboBox<String> jcbFailedFTR;
    private javax.swing.JComboBox<String> jcbFailedOnTime;
    private javax.swing.JComboBox<String> jcbOnTime;
    private javax.swing.JComboBox<String> jcbSAP;
    private javax.swing.JComboBox<String> jcbSAP1;
    private javax.swing.JComboBox<String> jcbSubnet;
    private javax.swing.JComboBox<String> jcbTask;
    private javax.swing.JComboBox<String> jcbTask1;
    private javax.swing.JComboBox<String> jcbTeam1;
    private javax.swing.JComboBox<String> jcbWeek1;
    // End of variables declaration//GEN-END:variables
}
