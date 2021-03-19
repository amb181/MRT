/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metrics;

import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
import static metrics.Metrics.usersinfo;
import static metrics.Metrics.localversion;
import static metrics.Metrics.usersinfo;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author ealloem
 */
public final class VSS_Time_Report extends javax.swing.JFrame {

    /**
     * Creates new form VSS_Time_Report
     *
     * @throws java.text.ParseException
     * @throws java.io.IOException
     */
    ArrayList<String> tasks_info = new ArrayList<>();
    ArrayList<String> networks_info = new ArrayList<>();
    ArrayList<String> metrics_vss_info = new ArrayList<>();
    ArrayList<String> mrkts_bulk = new ArrayList<>();
    ArrayList<String> metrics_for_ess = new ArrayList<>();
    int current_week = 0, times_in_edit = 0;
    float hours = 0;
    boolean all_markets_checkbox = false;
    String saved = "Data saved successfully!";

    public VSS_Time_Report() throws ParseException, IOException {
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
        // Get available teams
        GetTeams();
        jSpinnerBulk.setVisible(false);
        jBMarketsBulk.setVisible(false);
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
        AutoCompleteDecorator.decorate(jcbMarket);
        // Set date field to non-editable and get current week
        JTextFieldDateEditor editor = (JTextFieldDateEditor) jDateChooser1.getDateEditor();
        JTextFieldDateEditor editor1 = (JTextFieldDateEditor) jDateChooser2.getDateEditor();
        JTextFieldDateEditor editor2 = (JTextFieldDateEditor) jDateChooser_MarketBulk.getDateEditor();
        Date date = new Date();
        jDateChooser1.setDate(date);
        jDateChooser2.setDate(date);
        jDateChooser_MarketBulk.setDate(date);
        editor.setEditable(false);
        editor1.setEditable(false);
        editor2.setEditable(false);
        Calendar now = Calendar.getInstance();
        current_week = now.get(Calendar.WEEK_OF_YEAR);
        // Set current week panel 2
        for (int i = 1; i <= current_week; i++) {
            jcbWeek1.addItem(String.valueOf(i));
        }
        jcbWeek1.setSelectedIndex(current_week - 1);
        GetHours();
        this.setTitle("VSS               " + usersinfo.get(0) + " | " + usersinfo.get(4) + " | " + usersinfo.get(1) + " | " + "Week: " + current_week + " | "
                + "Hours: " + hours);
        jLabelVersion.setText("v_" + localversion);
        // Get week from new jDateChooser and put it in week textfield
        SimpleDateFormat dcn = new SimpleDateFormat("yyyy-MM-dd");
        String date_change = dcn.format(jDateChooser1.getDate());
        String date_change1 = dcn.format(jDateChooser2.getDate());
        String date_change2 = dcn.format(jDateChooser_MarketBulk.getDate());
        int week = 0;
        Date date_ = dcn.parse(date_change);
        Date date_1 = dcn.parse(date_change1);
        Date date_2 = dcn.parse(date_change2);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date_);
        cal.setTime(date_1);
        cal.setTime(date_2);
        week = cal.get(Calendar.WEEK_OF_YEAR);
        jTextFieldWeek.setText(String.valueOf(week));
        jTextFieldWeek_MarketBulk.setText(String.valueOf(week));
        // Update week number every time user clics new date
        jDateChooser1.addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                String date_change = dcn.format(jDateChooser1.getDate());
                int week = 0;
                Date date_ = null;
                try {
                    date_ = dcn.parse(date_change);
                } catch (ParseException ex) {
                    Logger.getLogger(VSS_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
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
        jcbSubnet.addItem("Select a subnetwork...");
        // Populate jcbCU with possible CUs for this team
        String[] cus = usersinfo.get(8).split("@");
        String cu = null;
        if (usersinfo.get(2).equals("V_AT&T") || usersinfo.get(2).equals("V_Sprint") || usersinfo.get(2).equals("V_T-Mobile")
                || usersinfo.get(2).equals("V_Verizon")) {
            cu = usersinfo.get(2).replace("V_", "");
            jcbCU.addItem(cu);
        }
        for (int i = 0; i < cus.length; i++) {
            if (cus[i].equals("V_AT&T") || cus[i].equals("V_Sprint") || cus[i].equals("V_T-Mobile")
                    || cus[i].equals("V_Verizon")) {
                cu = cus[i].replace("V_", "");
                jcbCU.addItem(cu);
            }
        }
        // Populate jcbTask1 with All default
        ArrayList<String> check_list1 = new ArrayList<>();
        jcbTask1.addItem("Select an activity...");
        String task1 = null;
        for (int i = 1; i < tasks_info.size(); i += 5) {
            task1 = tasks_info.get(i);
            if (task1.contains("  ")) {
                task1 = task1.replace("  ", "");
            }
            if (!check_list1.contains(task1)) {
                check_list1.add(task1);
                jcbTask1.addItem(task1);
            }
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
        if (jcbCU.getSelectedItem().toString().equals("AT&T")) {
            jcbTech.setVisible(false);
            jLabelTech.setVisible(false);
        }

        // Market Bulk
        // Update week number every time user clics new date
        jDateChooser_MarketBulk.addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                String date_change = dcn.format(jDateChooser_MarketBulk.getDate());
                int week = 0;
                Date date_ = null;
                try {
                    date_ = dcn.parse(date_change);
                } catch (ParseException ex) {
                    Logger.getLogger(VSS_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(date_);
                week = cal.get(Calendar.WEEK_OF_YEAR);
                if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    week = week - 1;
                }
                jTextFieldWeek_MarketBulk.setText(String.valueOf(week));
            }
        });
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
        jFrameCatalog = new javax.swing.JFrame();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableCatalog = new javax.swing.JTable(){
            @Override

            public Component prepareRenderer (TableCellRenderer renderer, int rowIndex, int columnIndex){
                Component component = super.prepareRenderer(renderer, rowIndex, columnIndex);

                if(rowIndex%2 == 0){
                    component.setBackground(Color.WHITE);
                    component.setForeground(Color.BLACK);
                }else{//185,223,255
                    component.setBackground(new Color(191,226,255));
                    component.setForeground(Color.BLACK);
                }

                return component;
            }
        };
        jB_Export_Net_Catalog = new javax.swing.JButton();
        jFrameMarketsBulk = new javax.swing.JFrame();
        jPanelMarketBulk = new javax.swing.JPanel();
        jcbRegionBulk = new javax.swing.JComboBox<>();
        jLRegionBulk = new javax.swing.JLabel();
        jPanelMarketSelection = new javax.swing.JPanel();
        jLabelNum_req_Bulk = new javax.swing.JLabel();
        jLHoursForLoggedTime_MarketBulk = new javax.swing.JLabel();
        jTextFieldWeek_MarketBulk = new javax.swing.JTextField();
        jLTimeMarket_Bulk = new javax.swing.JLabel();
        jcbOnTime_MarketBulk = new javax.swing.JComboBox<>();
        jcbFailedFTR_MarketBulk = new javax.swing.JComboBox<>();
        jLWeek_MarketBulk = new javax.swing.JLabel();
        jLDate_MarketBulk = new javax.swing.JLabel();
        jLTask_MarketBulk = new javax.swing.JLabel();
        jLOnTime_MarketBulk = new javax.swing.JLabel();
        jTextFieldRequestor_MarketBulk = new javax.swing.JTextField();
        jLFaildeOnTime_MarketBulk = new javax.swing.JLabel();
        jcbFailedOnTime_MarketBulk = new javax.swing.JComboBox<>();
        jcbFTR_MarketBulk = new javax.swing.JComboBox<>();
        jcbTask_MarketBulk = new javax.swing.JComboBox<>();
        jDateChooser_MarketBulk = new com.toedter.calendar.JDateChooser();
        jLFailFTR_MarketBulk = new javax.swing.JLabel();
        jTextFieldTime_MarketBulk = new javax.swing.JTextField();
        jLRequestor_MarketBulk = new javax.swing.JLabel();
        jLFTR_MarketBulk = new javax.swing.JLabel();
        jLTaskID_MarketBulk = new javax.swing.JLabel();
        jTextFieldTask_ID_MarketBulk = new javax.swing.JTextField();
        jLComments_MarketBulk = new javax.swing.JLabel();
        jTextFieldNumReqMarketBulk = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextFieldComments_MarketBulk = new javax.swing.JTextArea();
        jLabelMarketsBulk = new javax.swing.JLabel();
        jBAddBulkMarket = new javax.swing.JButton();
        jBCancel_MarketBulk = new javax.swing.JButton();
        jFrameHistory = new javax.swing.JFrame();
        jDateChooser_Start = new com.toedter.calendar.JDateChooser();
        jDateChooser_End = new com.toedter.calendar.JDateChooser();
        jL_StartDate = new javax.swing.JLabel();
        jL_EndDate = new javax.swing.JLabel();
        jB_ExportDates = new javax.swing.JButton();
        jcb_Team_history = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jB_Save = new javax.swing.JButton();
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

        };
        jSpinnerBulk = new javax.swing.JSpinner();
        jLActivity = new javax.swing.JLabel();
        jTextFieldActivity = new javax.swing.JTextField();
        jcbSubnet = new javax.swing.JComboBox<>();
        jTextFieldNetwork = new javax.swing.JTextField();
        jLabelHoursDaysH1 = new javax.swing.JLabel();
        jLabelHoursDaysB1 = new javax.swing.JLabel();
        jcbCU = new javax.swing.JComboBox<>();
        jLCU = new javax.swing.JLabel();
        jcbRegion = new javax.swing.JComboBox<>();
        jLabelRegion = new javax.swing.JLabel();
        jcbMarket = new javax.swing.JComboBox<>();
        jLabelMarket = new javax.swing.JLabel();
        jcbTech = new javax.swing.JComboBox<>();
        jLabelTech = new javax.swing.JLabel();
        jBCatalog = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextFieldComments = new javax.swing.JTextArea();
        jLabelTeam = new javax.swing.JLabel();
        jBMarketsBulk = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLTask1 = new javax.swing.JLabel();
        jcbTask1 = new javax.swing.JComboBox<>();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jLDate1 = new javax.swing.JLabel();
        jLWeek1 = new javax.swing.JLabel();
        jLSAP_Billing1 = new javax.swing.JLabel();
        jcbSAP1 = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableSeeMetrics = new javax.swing.JTable()
        {
        }
        ;
        jBSearch = new javax.swing.JButton();
        jCheckBoxWeek = new javax.swing.JCheckBox();
        jButtonUpdate = new javax.swing.JButton();
        jBDeleteRow1 = new javax.swing.JButton();
        jcbWeek1 = new javax.swing.JComboBox<>();
        jBClearTable2 = new javax.swing.JButton();
        jLabelHoursDaysH = new javax.swing.JLabel();
        jLabelHoursDaysB = new javax.swing.JLabel();
        jcbTeam1 = new javax.swing.JComboBox<>();
        jLabelTeam1 = new javax.swing.JLabel();
        jBExport = new javax.swing.JButton();
        jBHistory = new javax.swing.JButton();
        jB_ESS = new javax.swing.JButton();
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
        jMenuItem_SavedTemplate = new javax.swing.JMenuItem();
        jMenuItemSaveTemplate = new javax.swing.JMenuItem();
        jMenuItemGenerate = new javax.swing.JMenuItem();
        jMenuItemOpen = new javax.swing.JMenuItem();
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
            .addComponent(jLabelLoading1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        jPanelLoadingLayout.setVerticalGroup(
            jPanelLoadingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLoadingLayout.createSequentialGroup()
                .addComponent(jLabelLoading1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelLoading, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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

        jTableCatalog.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PD", "Network", "Activity code", "Region", "Market", "Customer", "Responsible", "Subnetwork", "Team", "Technology"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTableCatalog);

        jB_Export_Net_Catalog.setText("Download");
        jB_Export_Net_Catalog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_Export_Net_CatalogActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jFrameCatalogLayout = new javax.swing.GroupLayout(jFrameCatalog.getContentPane());
        jFrameCatalog.getContentPane().setLayout(jFrameCatalogLayout);
        jFrameCatalogLayout.setHorizontalGroup(
            jFrameCatalogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
            .addGroup(jFrameCatalogLayout.createSequentialGroup()
                .addGap(456, 456, 456)
                .addComponent(jB_Export_Net_Catalog, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 456, Short.MAX_VALUE))
        );
        jFrameCatalogLayout.setVerticalGroup(
            jFrameCatalogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrameCatalogLayout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 467, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jB_Export_Net_Catalog)
                .addGap(0, 12, Short.MAX_VALUE))
        );

        jcbRegionBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jcbRegionBulk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbRegionBulkActionPerformed(evt);
            }
        });

        jLRegionBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLRegionBulk.setText("Region:");

        jPanelMarketSelection.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jPanelMarketSelection.setMaximumSize(new java.awt.Dimension(331, 500));
        jPanelMarketSelection.setMinimumSize(new java.awt.Dimension(331, 500));

        javax.swing.GroupLayout jPanelMarketSelectionLayout = new javax.swing.GroupLayout(jPanelMarketSelection);
        jPanelMarketSelection.setLayout(jPanelMarketSelectionLayout);
        jPanelMarketSelectionLayout.setHorizontalGroup(
            jPanelMarketSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 331, Short.MAX_VALUE)
        );
        jPanelMarketSelectionLayout.setVerticalGroup(
            jPanelMarketSelectionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabelNum_req_Bulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabelNum_req_Bulk.setText("Number of requests: ");

        jLHoursForLoggedTime_MarketBulk.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLHoursForLoggedTime_MarketBulk.setText("hours");

        jTextFieldWeek_MarketBulk.setEditable(false);
        jTextFieldWeek_MarketBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLTimeMarket_Bulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTimeMarket_Bulk.setText("Total Logged Time:");

        jcbOnTime_MarketBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jcbOnTime_MarketBulk.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "N/A", "Yes", "No" }));

        jcbFailedFTR_MarketBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jcbFailedFTR_MarketBulk.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "N/A", "Human Error", "Lack of process adhereance", "Missing training", "Technical issues", "Tool related error", "Wrong inputs" }));

        jLWeek_MarketBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLWeek_MarketBulk.setText("Week:");

        jLDate_MarketBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLDate_MarketBulk.setText("Date:");

        jLTask_MarketBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTask_MarketBulk.setText("Task:");

        jLOnTime_MarketBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLOnTime_MarketBulk.setText("On Time:");

        jTextFieldRequestor_MarketBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLFaildeOnTime_MarketBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLFaildeOnTime_MarketBulk.setText("Failed on time:");

        jcbFailedOnTime_MarketBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jcbFailedOnTime_MarketBulk.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "N/A", "Yes", "No" }));

        jcbFTR_MarketBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jcbFTR_MarketBulk.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "N/A", "Yes", "No" }));

        jcbTask_MarketBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jcbTask_MarketBulk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbTask_MarketBulkActionPerformed(evt);
            }
        });

        jDateChooser_MarketBulk.setDateFormatString("yyyy-MM-dd");
        jDateChooser_MarketBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLFailFTR_MarketBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLFailFTR_MarketBulk.setText("Failed FTR Category:");

        jTextFieldTime_MarketBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLRequestor_MarketBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLRequestor_MarketBulk.setText("Requestor:");

        jLFTR_MarketBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLFTR_MarketBulk.setText("FTR:");

        jLTaskID_MarketBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTaskID_MarketBulk.setText("Task ID:");

        jTextFieldTask_ID_MarketBulk.setEditable(false);
        jTextFieldTask_ID_MarketBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLComments_MarketBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLComments_MarketBulk.setText("Comments:");

        jTextFieldNumReqMarketBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jTextFieldComments_MarketBulk.setColumns(20);
        jTextFieldComments_MarketBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jTextFieldComments_MarketBulk.setRows(5);
        jScrollPane5.setViewportView(jTextFieldComments_MarketBulk);

        jLabelMarketsBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabelMarketsBulk.setText("Markets:");

        jBAddBulkMarket.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBAddBulkMarket.setText("Add");
        jBAddBulkMarket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBAddBulkMarketActionPerformed(evt);
            }
        });

        jBCancel_MarketBulk.setBackground(new java.awt.Color(255, 1, 1));
        jBCancel_MarketBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBCancel_MarketBulk.setForeground(new java.awt.Color(255, 255, 255));
        jBCancel_MarketBulk.setText("Cancel");
        jBCancel_MarketBulk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCancel_MarketBulkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelMarketBulkLayout = new javax.swing.GroupLayout(jPanelMarketBulk);
        jPanelMarketBulk.setLayout(jPanelMarketBulkLayout);
        jPanelMarketBulkLayout.setHorizontalGroup(
            jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMarketBulkLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanelMarketBulkLayout.createSequentialGroup()
                        .addComponent(jLFailFTR_MarketBulk, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbFailedFTR_MarketBulk, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLRequestor_MarketBulk, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLWeek_MarketBulk, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelMarketBulkLayout.createSequentialGroup()
                                .addComponent(jLFaildeOnTime_MarketBulk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jcbFailedOnTime_MarketBulk, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelMarketBulkLayout.createSequentialGroup()
                                .addComponent(jLOnTime_MarketBulk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jcbOnTime_MarketBulk, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jTextFieldRequestor_MarketBulk, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelMarketBulkLayout.createSequentialGroup()
                                        .addComponent(jLTimeMarket_Bulk)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jTextFieldTime_MarketBulk, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLHoursForLoggedTime_MarketBulk))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jTextFieldWeek_MarketBulk, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelMarketBulkLayout.createSequentialGroup()
                                                .addComponent(jLDate_MarketBulk)
                                                .addGap(116, 116, 116)
                                                .addComponent(jDateChooser_MarketBulk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelMarketBulkLayout.createSequentialGroup()
                                                .addComponent(jLFTR_MarketBulk)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jcbFTR_MarketBulk, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelMarketBulkLayout.createSequentialGroup()
                                                .addComponent(jLabelNum_req_Bulk)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jTextFieldNumReqMarketBulk, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelMarketBulkLayout.createSequentialGroup()
                                                .addComponent(jLRegionBulk)
                                                .addGap(18, 18, 18)
                                                .addComponent(jcbRegionBulk, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))))))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelMarketBulkLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jBCancel_MarketBulk, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelMarketsBulk)
                    .addComponent(jPanelMarketSelection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMarketBulkLayout.createSequentialGroup()
                        .addComponent(jLTaskID_MarketBulk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTextFieldTask_ID_MarketBulk, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanelMarketBulkLayout.createSequentialGroup()
                        .addComponent(jLTask_MarketBulk)
                        .addGap(18, 18, 18)
                        .addComponent(jcbTask_MarketBulk, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE)
                    .addGroup(jPanelMarketBulkLayout.createSequentialGroup()
                        .addComponent(jLComments_MarketBulk)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelMarketBulkLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jBAddBulkMarket, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanelMarketBulkLayout.setVerticalGroup(
            jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelMarketBulkLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelMarketBulkLayout.createSequentialGroup()
                        .addGap(84, 84, 84)
                        .addGroup(jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelMarketBulkLayout.createSequentialGroup()
                                .addGroup(jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLTaskID_MarketBulk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jTextFieldTask_ID_MarketBulk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLTask_MarketBulk)
                                    .addComponent(jcbTask_MarketBulk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(191, 191, 191))
                            .addGroup(jPanelMarketBulkLayout.createSequentialGroup()
                                .addGap(76, 76, 76)
                                .addComponent(jLComments_MarketBulk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(156, 156, 156)
                        .addComponent(jBAddBulkMarket, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28))
                    .addGroup(jPanelMarketBulkLayout.createSequentialGroup()
                        .addGroup(jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLRegionBulk)
                            .addComponent(jcbRegionBulk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelMarketsBulk))
                        .addGap(12, 12, 12)
                        .addGroup(jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanelMarketSelection, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanelMarketBulkLayout.createSequentialGroup()
                                .addGroup(jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jTextFieldNumReqMarketBulk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabelNum_req_Bulk))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jDateChooser_MarketBulk, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLDate_MarketBulk))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLWeek_MarketBulk)
                                    .addComponent(jTextFieldWeek_MarketBulk, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLTimeMarket_Bulk)
                                    .addComponent(jLHoursForLoggedTime_MarketBulk)
                                    .addComponent(jTextFieldTime_MarketBulk, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(12, 12, 12)
                                .addGroup(jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLRequestor_MarketBulk)
                                    .addComponent(jTextFieldRequestor_MarketBulk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLOnTime_MarketBulk)
                                    .addComponent(jcbOnTime_MarketBulk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLFaildeOnTime_MarketBulk)
                                    .addComponent(jcbFailedOnTime_MarketBulk, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLFTR_MarketBulk)
                                    .addComponent(jcbFTR_MarketBulk, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanelMarketBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLFailFTR_MarketBulk)
                                    .addComponent(jcbFailedFTR_MarketBulk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jBCancel_MarketBulk, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(25, 25, 25))))))
        );

        javax.swing.GroupLayout jFrameMarketsBulkLayout = new javax.swing.GroupLayout(jFrameMarketsBulk.getContentPane());
        jFrameMarketsBulk.getContentPane().setLayout(jFrameMarketsBulkLayout);
        jFrameMarketsBulkLayout.setHorizontalGroup(
            jFrameMarketsBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelMarketBulk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jFrameMarketsBulkLayout.setVerticalGroup(
            jFrameMarketsBulkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrameMarketsBulkLayout.createSequentialGroup()
                .addComponent(jPanelMarketBulk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 18, Short.MAX_VALUE))
        );

        jDateChooser_Start.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jDateChooser_End.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jL_StartDate.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jL_StartDate.setText("Select the starting date:");

        jL_EndDate.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jL_EndDate.setText("Select the ending date:");

        jB_ExportDates.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jB_ExportDates.setText("Search & Export");
        jB_ExportDates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_ExportDatesActionPerformed(evt);
            }
        });

        jcb_Team_history.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel1.setText("Select a team:");

        javax.swing.GroupLayout jFrameHistoryLayout = new javax.swing.GroupLayout(jFrameHistory.getContentPane());
        jFrameHistory.getContentPane().setLayout(jFrameHistoryLayout);
        jFrameHistoryLayout.setHorizontalGroup(
            jFrameHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrameHistoryLayout.createSequentialGroup()
                .addGroup(jFrameHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jFrameHistoryLayout.createSequentialGroup()
                        .addGap(121, 121, 121)
                        .addComponent(jB_ExportDates))
                    .addGroup(jFrameHistoryLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jFrameHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jFrameHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFrameHistoryLayout.createSequentialGroup()
                                    .addComponent(jL_StartDate)
                                    .addGap(31, 31, 31))
                                .addGroup(jFrameHistoryLayout.createSequentialGroup()
                                    .addComponent(jL_EndDate)
                                    .addGap(38, 38, 38)))
                            .addGroup(jFrameHistoryLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(jFrameHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jDateChooser_End, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                            .addComponent(jDateChooser_Start, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jcb_Team_history, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jFrameHistoryLayout.setVerticalGroup(
            jFrameHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrameHistoryLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jFrameHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcb_Team_history, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(jFrameHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jL_StartDate)
                    .addComponent(jDateChooser_Start, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54)
                .addGroup(jFrameHistoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jDateChooser_End, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jL_EndDate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 78, Short.MAX_VALUE)
                .addComponent(jB_ExportDates)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jPanel1.setMaximumSize(new java.awt.Dimension(0, 0));
        jPanel1.setPreferredSize(new java.awt.Dimension(1560, 915));

        jB_Save.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jB_Save.setText("Save");
        jB_Save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_SaveActionPerformed(evt);
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
                "CU", "Region", "Market", "Signum", "Requestor", "Task ID", "Task", "Network", "Subnetwork", "Activity code", "Technology", "SAP Billing", "Work date", "Logged Time", "Week", "FTR", "On Time", "Failed FTR", "Failed On Time", "Number of requests", "Comments"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, false, false, false, false, false, false, false, true, true, false, false, false, false, false, false, true
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
            jTableAddMetrics.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTableAddMetrics.getColumnModel().getColumn(1).setPreferredWidth(80);
            jTableAddMetrics.getColumnModel().getColumn(2).setPreferredWidth(250);
            jTableAddMetrics.getColumnModel().getColumn(3).setPreferredWidth(100);
            jTableAddMetrics.getColumnModel().getColumn(4).setPreferredWidth(130);
            jTableAddMetrics.getColumnModel().getColumn(5).setPreferredWidth(250);
            jTableAddMetrics.getColumnModel().getColumn(6).setPreferredWidth(350);
            jTableAddMetrics.getColumnModel().getColumn(7).setPreferredWidth(120);
            jTableAddMetrics.getColumnModel().getColumn(8).setPreferredWidth(120);
            jTableAddMetrics.getColumnModel().getColumn(9).setPreferredWidth(140);
            jTableAddMetrics.getColumnModel().getColumn(10).setPreferredWidth(150);
            jTableAddMetrics.getColumnModel().getColumn(11).setPreferredWidth(120);
            jTableAddMetrics.getColumnModel().getColumn(12).setPreferredWidth(150);
            jTableAddMetrics.getColumnModel().getColumn(13).setPreferredWidth(120);
            jTableAddMetrics.getColumnModel().getColumn(14).setPreferredWidth(90);
            jTableAddMetrics.getColumnModel().getColumn(15).setPreferredWidth(80);
            jTableAddMetrics.getColumnModel().getColumn(16).setPreferredWidth(80);
            jTableAddMetrics.getColumnModel().getColumn(17).setPreferredWidth(250);
            jTableAddMetrics.getColumnModel().getColumn(18).setPreferredWidth(150);
            jTableAddMetrics.getColumnModel().getColumn(19).setPreferredWidth(200);
            jTableAddMetrics.getColumnModel().getColumn(20).setPreferredWidth(350);
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

        jLabelHoursDaysH1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLabelHoursDaysB1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jcbCU.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jcbCU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbCUActionPerformed(evt);
            }
        });

        jLCU.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLCU.setText("Customer Unit:");

        jcbRegion.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jcbRegion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbRegionActionPerformed(evt);
            }
        });

        jLabelRegion.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabelRegion.setText("Region:");

        jcbMarket.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jcbMarket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbMarketActionPerformed(evt);
            }
        });

        jLabelMarket.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabelMarket.setText("Market:");

        jcbTech.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jcbTech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbTechActionPerformed(evt);
            }
        });

        jLabelTech.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabelTech.setText("Technology:");

        jBCatalog.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBCatalog.setText("Catalog");
        jBCatalog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCatalogActionPerformed(evt);
            }
        });

        jTextFieldComments.setColumns(20);
        jTextFieldComments.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jTextFieldComments.setRows(5);
        jScrollPane4.setViewportView(jTextFieldComments);

        jLabelTeam.setFont(new java.awt.Font("Ericsson Hilda", 1, 36)); // NOI18N
        jLabelTeam.setForeground(new java.awt.Color(0, 153, 153));
        jLabelTeam.setText("VSS Metrics");

        jBMarketsBulk.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBMarketsBulk.setText("Different markets");
        jBMarketsBulk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBMarketsBulkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jTextFieldRequestor, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
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
                                            .addComponent(jLActivity))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jRSingle)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jRBulk)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jSpinnerBulk, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelHoursDaysH1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabelHoursDaysB1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jBMarketsBulk)
                                        .addGap(0, 0, Short.MAX_VALUE))))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLComments)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 1155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jBAddTable)
                                .addGap(0, 205, Short.MAX_VALUE)))
                        .addGap(19, 19, 19))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jBDeleteRow)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jBClearTable)
                        .addGap(634, 634, 634)
                        .addComponent(jLabelTeam)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jB_Save)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jcbSAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLCU, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLSAP_Billing, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jcbCU, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabelRegion)
                                .addGap(18, 18, 18)
                                .addComponent(jcbRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabelMarket)
                                .addGap(18, 18, 18)
                                .addComponent(jcbMarket, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabelTech)
                                .addGap(18, 18, 18)
                                .addComponent(jcbTech, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jBCatalog))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLTaskID)
                                    .addComponent(jTextFieldTask_ID, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(7, 7, 7)
                                        .addComponent(jcbTask, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLTask)
                                        .addGap(350, 350, 350)))
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
                                .addComponent(jLRequestor)))
                        .addContainerGap(195, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcbCU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLCU)
                    .addComponent(jcbRegion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelRegion)
                    .addComponent(jcbMarket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelMarket)
                    .addComponent(jcbTech, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelTech)
                    .addComponent(jBCatalog))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                                    .addComponent(jcbSAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabelHoursDaysH1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(jLabelHoursDaysB1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jTextFieldActivity))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jRSingle)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jRBulk)
                                .addComponent(jSpinnerBulk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jBMarketsBulk)))
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLComments)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBAddTable))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 612, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelTeam, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jBClearTable)
                                .addComponent(jBDeleteRow))
                            .addComponent(jB_Save))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
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
                "id", "CU", "Region", "Market", "Signum", "Requestor", "Task ID", "Task", "Technology", "Network", "Subnetwork", "Activity code", "SAP Billing", "Work date", "Logged Time", "Week", "FTR", "On Time", "Failed FTR", "Failed On Time", "Number of requests", "Comments"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true, false, false, false, false, false, false, false, true, true, true, false, false, false, false, false, true
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
            jTableSeeMetrics.getColumnModel().getColumn(1).setPreferredWidth(100);
            jTableSeeMetrics.getColumnModel().getColumn(2).setPreferredWidth(80);
            jTableSeeMetrics.getColumnModel().getColumn(3).setPreferredWidth(250);
            jTableSeeMetrics.getColumnModel().getColumn(4).setPreferredWidth(100);
            jTableSeeMetrics.getColumnModel().getColumn(5).setPreferredWidth(130);
            jTableSeeMetrics.getColumnModel().getColumn(6).setPreferredWidth(250);
            jTableSeeMetrics.getColumnModel().getColumn(7).setPreferredWidth(350);
            jTableSeeMetrics.getColumnModel().getColumn(8).setPreferredWidth(150);
            jTableSeeMetrics.getColumnModel().getColumn(9).setPreferredWidth(120);
            jTableSeeMetrics.getColumnModel().getColumn(10).setPreferredWidth(120);
            jTableSeeMetrics.getColumnModel().getColumn(11).setPreferredWidth(140);
            jTableSeeMetrics.getColumnModel().getColumn(12).setPreferredWidth(120);
            jTableSeeMetrics.getColumnModel().getColumn(13).setPreferredWidth(150);
            jTableSeeMetrics.getColumnModel().getColumn(14).setPreferredWidth(120);
            jTableSeeMetrics.getColumnModel().getColumn(15).setPreferredWidth(90);
            jTableSeeMetrics.getColumnModel().getColumn(16).setPreferredWidth(80);
            jTableSeeMetrics.getColumnModel().getColumn(17).setPreferredWidth(80);
            jTableSeeMetrics.getColumnModel().getColumn(18).setPreferredWidth(250);
            jTableSeeMetrics.getColumnModel().getColumn(19).setPreferredWidth(150);
            jTableSeeMetrics.getColumnModel().getColumn(20).setPreferredWidth(200);
            jTableSeeMetrics.getColumnModel().getColumn(21).setPreferredWidth(350);
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

        jButtonUpdate.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jButtonUpdate.setText("Update");
        jButtonUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdateActionPerformed(evt);
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

        jBHistory.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBHistory.setText("History");
        jBHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBHistoryActionPerformed(evt);
            }
        });

        jB_ESS.setBackground(new java.awt.Color(204, 255, 204));
        jB_ESS.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jB_ESS.setText("Export in ESS format");
        jB_ESS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_ESSActionPerformed(evt);
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
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLSAP_Billing1)
                                .addGap(13, 13, 13))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabelTeam1)
                                .addGap(18, 18, 18)))
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
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 242, Short.MAX_VALUE)
                                        .addComponent(jBSearch))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                        .addGap(371, 371, 371)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabelHoursDaysB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jBHistory))
                                            .addComponent(jLabelHoursDaysH, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addGap(19, 19, 19))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jBDeleteRow1)
                        .addGap(18, 18, 18)
                        .addComponent(jBClearTable2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jB_ESS)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jBExport)
                        .addGap(34, 34, 34)
                        .addComponent(jButtonUpdate)
                        .addContainerGap())))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(11, 11, 11)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1378, Short.MAX_VALUE)
                    .addGap(11, 11, 11)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabelHoursDaysH, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelHoursDaysB, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jcbTeam1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jBHistory)))
                    .addComponent(jLabelTeam1))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 680, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jBDeleteRow1)
                            .addComponent(jBClearTable2))
                        .addGap(36, 36, 36))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonUpdate)
                            .addComponent(jBExport)
                            .addComponent(jB_ESS))
                        .addGap(35, 35, 35))))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(173, 173, 173)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 672, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(70, Short.MAX_VALUE)))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 178, Short.MAX_VALUE)
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
                            .addComponent(jLabelDescAbout, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabelSupport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jMenu1.setText("File");
        jMenu1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jMenuItem_SavedTemplate.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMenuItem_SavedTemplate.setText("Use saved template");
        jMenuItem_SavedTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_SavedTemplateActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem_SavedTemplate);

        jMenuItemSaveTemplate.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMenuItemSaveTemplate.setText("Save as template");
        jMenuItemSaveTemplate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveTemplateActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemSaveTemplate);

        jMenuItemGenerate.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMenuItemGenerate.setText("Create csv ...");
        jMenuItemGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGenerateActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemGenerate);

        jMenuItemOpen.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMenuItemOpen.setText("Import csv ...");
        jMenuItemOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOpenActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemOpen);

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
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1560, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 960, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 966, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jB_SaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_SaveActionPerformed
        // Action for Save button
        if (jTableAddMetrics.getEditingRow() != -1) {
            jTableAddMetrics.getCellEditor().stopCellEditing();// In case there's selected a field in the table
        }
        // Start thread
        jLabelLoading.setText("Saving your metrics into database...");
        new Thread(new Runnable() {
            @Override
            public void run() {

                int rows = jTableAddMetrics.getRowCount();
                System.out.println("Rows to insert: " + rows);
                boolean error = false;
                DefaultTableModel tblModel = (DefaultTableModel) jTableAddMetrics.getModel();

                // Validate jAddMetrics table's info
                for (int row = 0; row < rows; row++) {
                    String cu = (String) jTableAddMetrics.getValueAt(0, 0);
                    String region = (String) jTableAddMetrics.getValueAt(0, 1);
                    String market = (String) jTableAddMetrics.getValueAt(0, 2);
                    String signum = (String) jTableAddMetrics.getValueAt(0, 3);
                    signum = signum.toLowerCase(); // In case user has signum in uppercase
                    String requestor = (String) jTableAddMetrics.getValueAt(0, 4);
                    String task_id = (String) jTableAddMetrics.getValueAt(0, 5);
                    String task = (String) jTableAddMetrics.getValueAt(0, 6);
                    String net = (String) jTableAddMetrics.getValueAt(0, 7);
                    String subnet = (String) jTableAddMetrics.getValueAt(0, 8);
                    String act = (String) jTableAddMetrics.getValueAt(0, 9);
                    String tech = (String) jTableAddMetrics.getValueAt(0, 10);
                    String sap = (String) jTableAddMetrics.getValueAt(0, 11);
                    String w_date = (String) jTableAddMetrics.getValueAt(0, 12);
                    String time = (String) jTableAddMetrics.getValueAt(0, 13);
                    String week = (String) jTableAddMetrics.getValueAt(0, 14);
                    String ftr = (String) jTableAddMetrics.getValueAt(0, 15);
                    String on_time = (String) jTableAddMetrics.getValueAt(0, 16);
                    String f_ftr = (String) jTableAddMetrics.getValueAt(0, 17);
                    String f_on_time = (String) jTableAddMetrics.getValueAt(0, 18);
                    String requests = (String) jTableAddMetrics.getValueAt(0, 19);
                    String comments = (String) jTableAddMetrics.getValueAt(0, 20);
                    // Validate every cell is containing valid user info or is not empty
                    String failed = "";
                    //Requestor format
                    Pattern prequestor1 = Pattern.compile("^[a-zA-Z ]+$");          // Regular name
                    Pattern prequestor2 = Pattern.compile("^([A-Za-z/]*)$");        // N/A
                    Matcher mrequestor1 = prequestor1.matcher(requestor);
                    Matcher mrequestor2 = prequestor2.matcher(requestor);
                    boolean brequestor1 = mrequestor1.find();
                    boolean brequestor2 = mrequestor2.find();
                    // Find task in tasks_info
                    int task_index = 0;
                    for (int i = 0; i < tasks_info.size(); i++) {
                        if (tasks_info.get(i).equals(task)) {
                            task_index = i;
                            break;
                        }
                    }
                    // Find subnet in networks_info
                    // Find net in networks_info
                    int net_index = 0;
                    for (int i = 0; i < networks_info.size(); i++) {
                        if (networks_info.get(i).equals(net)) {
                            net_index = i;
                            break;
                        }
                    }

                    //Date format
                    Pattern pdate = Pattern.compile("^(([0-9][0-9][0-9][0-9]-)*([0-9][0-9]-)*([0-9][0-9]))$");  // Date format YYYY-MM-DD
                    Matcher mdate = pdate.matcher(w_date);
                    boolean bdate = mdate.find();
                    //Time format
                    Pattern ptime = Pattern.compile("^\\s*(?=.*[1-9])\\d*(?:\\.\\d{1,2})?\\s*$");
                    Matcher mtime = ptime.matcher(time);
                    boolean btime = mtime.find();
                    // Week according to date
                    SimpleDateFormat dcn = new SimpleDateFormat("yyyy-MM-dd");
                    Date date_ = new Date();
                    try {
                        date_ = dcn.parse(w_date);
                    } catch (ParseException ex) {
                        Logger.getLogger(VSS_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("Error parsing date " + w_date);
                    }
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date_);
                    int correct_week = cal.get(Calendar.WEEK_OF_YEAR);

                    // List for ftr, on_time, f_ftr and f_on_time
                    List<String> ftr_list = Arrays.asList("N/A", "Yes", "No");
                    List<String> on_time_list = Arrays.asList("N/A", "Yes", "No");
                    List<String> f_ftr_list = Arrays.asList("N/A", "Human error", "Lack of process adhereance", "Missing training",
                            "Technical issues", "Tool related error", "Wrong inputs");
                    List<String> f_on_time_list = Arrays.asList("N/A", "Yes", "No");

                    // Requests format
                    Pattern prequests = Pattern.compile("^[0-9]");
                    Matcher mrequests = prequests.matcher(requests);
                    boolean brequests = mrequests.find();

                    // Start validation
                    if (task_index > 0) {
                        failed = Validate_task_net(task_index, net_index, cu, region, market, task_id, task, sap, net, subnet, act, tech);
                    } else {
                        failed = "Task: " + task;
                    }

                    if (!signum.equals(usersinfo.get(0)) || signum.equals("")) {      // Signum -> usersinfo
                        failed = "Signum " + signum;
                    }
                    if ((!brequestor1 && !brequestor2) || requestor.equals("")) {       // Requestor -> only alphabet characters 
                        failed = "Requestor " + requestor;
                    }

                    if (!bdate || w_date.equals("")) {                                              // Date -> format YYYY-MM-DD
                        System.out.println("Date: " + w_date);
                        failed = "Date " + w_date;
                    }

                    if (!btime || time.equals("")) {                                                // Time -> decimal format
                        failed = "Time " + time;
                    }

                    int dayOfWeek = Check_Day(w_date);
                    if (dayOfWeek == 7) {
                        correct_week = correct_week - 1;
                    }
                    System.out.println("Day " + dayOfWeek + " Week: " + correct_week);
                    if (!week.equals(String.valueOf(correct_week)) || week.equals("")) {           // Week -> Validate week for date 
                        failed = "Week " + week;
                    }

                    if (!ftr_list.contains(ftr) || ftr.equals("")) {                               // FTR -> Not empty or in list                                      // Week -> corresponding to date
                        failed = "FTR " + ftr;
                    }
                    if (!on_time_list.contains(on_time) || on_time.equals("")) {                   // On time -> Not empty or in list                                      // Week -> corresponding to date
                        failed = "On time " + on_time;
                    }
                    if (!f_ftr_list.contains(f_ftr) || f_ftr.equals("")) {                         // Failed FTR -> Not empty or in list                                      // Week -> corresponding to date
                        failed = "Failed FTR " + f_ftr;
                    }
                    if (!f_on_time_list.contains(f_on_time) || f_on_time.equals("")) {             // Failed On time -> Not empty or in list                                      // Week -> corresponding to date
                        failed = "Failed on time " + f_on_time;
                    }

                    if (!brequests || requests.equals("") || requests.equals("0")) {               // Requests -> Number format                                      // Week -> corresponding to date
                        failed = "Num of requests " + requests;
                    }

                    // If something failed show where it did
                    int current_row = row + 1;
                    if (!failed.equals("")) {
                        JOptionPane.showMessageDialog(VSS_Time_Report.this, "Please verify your info near " + failed + " in row " + current_row + ".");
                        error = true;
                        System.out.println("Error near " + failed + " in row " + current_row);
                        break;
                    }
                    int res = 0;
                    if (error == false) { // If true if before this should handle it
                        if (rows == 0) {
                            JOptionPane.showMessageDialog(VSS_Time_Report.this, "Nothing saved");
                        } else {    // Insert into database
                            jLabelLoading.setText("Saving your metrics into database...");
                            res = InsertIntoDB(cu, region, market, signum, requestor, task_id, task, net, subnet, act, tech, sap,
                                    w_date, time, week, ftr, on_time, f_ftr, f_on_time, requests, comments);
                            System.out.println("Result of function: " + res);
                            if (res == 0) {      // if failed
                                break;
                            } else {
                                tblModel.removeRow(0);
                            }
                        }
                    }
                }
                // Refresh total hours
                GetHours();
                GetDailyHours();
                System.out.println("Total hours this week: " + hours);
                VSS_Time_Report.this.setTitle("VSS               " + usersinfo.get(0) + " | " + usersinfo.get(4)
                        + " | " + usersinfo.get(1) + " | " + "Week: " + current_week + " | " + "Hours: " + hours);
                jDLoading.dispose();
                if (error) {
                    return;
                } else {
                    JOptionPane.showMessageDialog(VSS_Time_Report.this, saved);
                    ClearDataPanel1();
                }
            }
        }).start();
        jDLoading.setVisible(true);
    }//GEN-LAST:event_jB_SaveActionPerformed

    private String Validate_task_net(int task_index, int net_index, String cu, String region, String market,
            String task_id, String task, String sap, String net, String subnet, String act, String tech) {
        String failed = "";
        if (!task_id.equals(tasks_info.get(task_index - 1))) {           // Task ID
            task_index = ValidateTask_Save(task_id, task);
            if ((task_index == 0) || (!task_id.equals(tasks_info.get(task_index - 1)))) {           // Task ID
                return failed = "Task ID: " + task_id;
            }
        }
        if (task_id.contains("ADMIN")) {
            if (!cu.equals("SDU")) {
                return failed = "CU: " + cu;                                // CU
            }
            if (!region.equals("SDU")) {
                return failed = "Region: " + region;                        // Region
            }
            if (!market.equals("SDU")) {
                return failed = "Market: " + market;                        // Market
            }
            if (!net.equals("0")) {                                  // Network
                return failed = "Network: " + net;
            }
            if (!subnet.equals("0")) {                               // Subnetwork
                return failed = "Subnetwork: " + subnet;
            }
            if (!act.equals("0")) {                                  // Activity
                return failed = "Activity: " + act;
            }
            if (!tech.equals("N/A")) {                                  // Technology
                return failed = "Technology: " + tech;
            }
        } else {
            if (!cu.equals(tasks_info.get(task_index + 2))) {
                return failed = "CU: " + cu;
            }
            if (!net.equals(networks_info.get(net_index))) {
                return failed = "Network: " + net;
            }
            if (!region.equals(networks_info.get(net_index + 2))) {
                return failed = "Region: " + region;
            }
            if (!market.equals(networks_info.get(net_index + 3))) {
                return failed = "Market: " + market;
            }
            if (!subnet.equals(networks_info.get(net_index + 5))) {
                return failed = "Subnetwork: " + subnet;
            }
            if (!act.equals(networks_info.get(net_index + 1))) {
                return failed = "Activity: " + act;
            }
            if (!tech.equals(networks_info.get(net_index + 7))) {
                return failed = "Technology: " + tech;
            }
        }

        if (!sap.equals(tasks_info.get(task_index + 3))) {            // SAP
            return failed = "SAP: " + sap;
        }

        if (!task.equals(tasks_info.get(task_index))) {          // Task
            String task_copy = tasks_info.get(task_index);
            // Check for tasks with "same name"
            if (task_copy.contains("  ")) {
                task_copy = task_copy.replaceAll("  ", "");
            }
            if (!task_copy.equals(task)) {
                return failed = "Task: " + task;
            }
        }
        return failed;
    }

    private int ValidateTask_Save(String task_id, String task) {
        // Check if there is a task with the same name
        int new_task_index = 0;
        // Look for task with same name
        for (int i = 1; i < tasks_info.size(); i++) {
            String task_copy = tasks_info.get(i);
            // Check for tasks with "same name"
            if (task_copy.contains(task) && tasks_info.get(i - 1).equals(task_id)) {
                new_task_index = i;
                break;
            }
        }
        return new_task_index;
    }

    private int InsertIntoDB(String cu, String region, String market, String signum, String requestor, String task_id,
            String task, String net, String subnet, String act, String tech, String sap, String date, String time, String week,
            String ftr, String on_time, String f_ftr, String f_on_time, String requests, String comments) {

        Connection connection;
        CallableStatement callableStatement;
        try {
            connection = SQL_connection.getConnection();
            callableStatement = connection.prepareCall("CALL register_metrics_vss"
                    + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,? ,?, ?, ?);");

            Float.parseFloat(time);
            Integer.parseInt(week);
            Integer.parseInt(requests);

            String org = usersinfo.get(4);
            String name = usersinfo.get(1);

            if (comments.isEmpty() || comments.equals("")) {
                comments = "N/A";
            }

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
            callableStatement.setObject(16, on_time);
            callableStatement.setObject(17, f_ftr);
            callableStatement.setObject(18, f_on_time);
            callableStatement.setObject(19, requests);
            callableStatement.setObject(20, comments);
            callableStatement.setObject(21, tech);
            callableStatement.setObject(22, cu);
            callableStatement.setObject(23, market);
            callableStatement.registerOutParameter(24, java.sql.Types.INTEGER);
            callableStatement.executeUpdate();

            int result = callableStatement.getInt(24);
            if (result == 0) {
                saved = "Unable to save " + task + " with time " + time + ".\nYou may have exceeded 24 hours.\nPlease verify rows were saved before this one.";
            } else {
                saved = "Data saved successfully!";
            }
            System.out.println("Entered: " + callableStatement + "\nResult: " + result);

            callableStatement.close();
            connection.close();

            return result;
        } catch (SQLException e) {
            System.out.println(e);
            saved = "Something went wrong, please try again later.";
            return 0;
        }
    }

    private void ClearDataPanel1() {
        // Clear fields
        jcbCU.setSelectedItem(0);
        jcbRegion.setSelectedItem(0);
        jcbMarket.setSelectedItem(0);
        jcbTech.setSelectedItem(0);
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
        jcbTeam1.addItem("VSS");
        if (!usersinfo.get(7).equals("N/A")) {
            for (int i = 0; i < teams.size(); i++) {
                if (!teams.get(i).equals("VSS")) {
                    jcbTeam1.addItem(teams.get(i));

                    JMenuItem menu = new JMenuItem(teams.get(i));
                    menu.setFont(new Font("Ericsson Hilda", 0, 18));
                    jMenu4.add(menu);
                    menu.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent ev) {
                            String clicked_on = menu.getText();
                            try {
                                // Del menu seleccionado abrir su clase
                                if (clicked_on.equals("Sourcing")) {
                                    jDLoading.setModal(true);
                                    jDLoading.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Sourcing_Time_Report time_r = new Sourcing_Time_Report();
                                                time_r.show();
                                                time_r.setLocationRelativeTo(null);
                                                // Confirm exit window
                                                time_r.setDefaultCloseOperation(time_r.DO_NOTHING_ON_CLOSE);
                                                time_r.addWindowListener(new java.awt.event.WindowAdapter() {
                                                    @Override
                                                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                                                        if (JOptionPane.showConfirmDialog(time_r, "Are you sure you want to close this window?", "Exit Sourcing",
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
                                } else if (clicked_on.equals("COP")) {
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
        int year = Calendar.getInstance().get(Calendar.YEAR);
        try {
            connection = SQL_connection.getConnection();
            String sql_user_time = "SELECT SUM(Logged_Time) AS Hours FROM metrics_vss WHERE Signum='"
                    + usersinfo.get(0) + "' AND Week=" + current_week + " AND Work_date LIKE '" + year + "-%';";
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
        // Get VSS tasks
        try {
            String sql_task = null;

            sql_task = "SELECT * "
                    + "FROM tasks "
                    + "WHERE Team='SDU' OR Team='VSS' "
                    + "ORDER BY Task ASC;";

            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement(sql_task);
            resultset = preparedStatement.executeQuery();
            System.out.println(preparedStatement);
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
                    + "FROM networks " // CHANGE THIS TABLE
                    + "WHERE Team='VSS';";
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement(sql_task);
            resultset = preparedStatement.executeQuery();

            while (resultset.next()) {
                String[] row = {resultset.getString("Network"), resultset.getString("Activity_code"),
                    resultset.getString("Region"), resultset.getString("Market"), resultset.getString("Customer"),
                    resultset.getString("Subnetwork"), resultset.getString("Team"), resultset.getString("Technology")};
                List<String> newList = Arrays.asList(row);

                networks_info.addAll(newList);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void GetDailyHours() {
        try {
            int year = Calendar.getInstance().get(Calendar.YEAR);
            Connection connection = SQL_connection.getConnection();
            ResultSet resultSet;

            String sql = "SELECT SUM(Logged_Time) AS Hours, WEEKDAY(Work_date) AS Day "
                    + "FROM metrics_vss "
                    + "WHERE Signum = ? AND Week = '" + current_week + "' AND Work_date LIKE '" + year + "-%'"
                    + "GROUP BY Work_date ORDER BY Work_date;";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, usersinfo.get(0));
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
            int year = Calendar.getInstance().get(Calendar.YEAR);
            Connection connection = SQL_connection.getConnection();
            ResultSet resultSet;

            String sql = "SELECT SUM(Logged_Time) AS Hours, WEEKDAY(Work_date) AS Day "
                    + "FROM metrics_vss "
                    + "WHERE Signum = ? AND Week = ? AND Work_date LIKE '" + year + "-%'"
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
        // Add button, info to jTable 
        // Validate data type in date, time, requestor and requests textfields
        Pattern ptime = Pattern.compile("^\\s*(?=.*[1-9])\\d*(?:\\.\\d{1,2})?\\s*$");
        String stime = jTextFieldTime.getText();
        Matcher mtime = ptime.matcher(stime);
        boolean btime = mtime.find();
        // Requests format
        Pattern preq = Pattern.compile("[1-9]+");
        String sreq = jTextFieldRequests.getText();
        Matcher mreq = preq.matcher(sreq);
        boolean breq = mreq.find();
        //Requestor format
        String sreqtr = jTextFieldRequestor.getText();
        Pattern prequestor1 = Pattern.compile("^[a-zA-Z ]+$");          // Regular name
        Matcher mrequestor1 = prequestor1.matcher(sreqtr);
        boolean brequestor = mrequestor1.find();

        int entries = 1;
        boolean error = false;
        int validate_date = Integer.parseInt(jTextFieldWeek.getText());
        int thresh1 = current_week - 2;
        int thresh2 = current_week + 2;

        if (jcbTask.getSelectedItem().equals("Select an activity...")) {
            JOptionPane.showMessageDialog(this, "Choose an activity!");
            error = true;
        }
        if (jcbSubnet.getSelectedItem().equals("Select a subnetwork...")) {
            JOptionPane.showMessageDialog(this, "Choose a subnetwork!");
            error = true;
        }
        if (jcbRegion.getSelectedItem().equals("Select a region...")) {
            JOptionPane.showMessageDialog(this, "Choose a region!");
            error = true;
        }
        if (jcbMarket.getSelectedItem().equals("Select a market...")) {
            JOptionPane.showMessageDialog(this, "Choose a market!");
            error = true;
        }
        if (jTextFieldRequests.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Number of requests cannot be empty!");
            error = true;
        }
        if (jTextFieldRequests.getText().equals("0")) {
            JOptionPane.showMessageDialog(this, "Number of requests cannot be zero!");
            error = true;
        }
        if (jTextFieldComments.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add a comment!");
            error = true;
        }
        if (!breq) {
            JOptionPane.showMessageDialog(this, "Number of requests must be a number!");
            jTextFieldRequests.setText("");
            error = true;
        }
        if (stime.equals("") || !btime) {
            JOptionPane.showMessageDialog(this, "Logged Time must be a number! \nEx: 2 or 8.25");
            jTextFieldTime.setText("");
            error = true;
        }
        if (jcbTech.isVisible() && jcbTech.getSelectedItem().toString().equals("Select a technology...")) {
            JOptionPane.showMessageDialog(this, "Choose a technology!");
            error = true;
        }
        if (!brequestor && !sreqtr.equals("")) {
            JOptionPane.showMessageDialog(this, "Requestor must be a a name!");
            jTextFieldRequestor.setText("");
            error = true;
        }
        if (validate_date < thresh1 || validate_date > thresh2) {
            JOptionPane.showMessageDialog(this, "You can only add data from week " + thresh1 + " until week " + thresh2);

        }
        if (!error) {
            // Get combo boxes values
            String cu = "";
            String region = (String) jcbRegion.getSelectedItem();
            String market = (String) jcbMarket.getSelectedItem();
            String tech = (String) jcbTech.getSelectedItem();
            if (!jcbTech.isVisible()) {
                tech = "N/A";
            }
            String sap = (String) jcbSAP.getSelectedItem();
            if (sap.equals("Billable")) {
                cu = (String) jcbCU.getSelectedItem();
            } else {
                cu = "SDU";
            }
            String task_id = (String) jTextFieldTask_ID.getText();
            String task = (String) jcbTask.getSelectedItem();
            if (task.contains("  ")) {
                task = task.replace("  ", "");
            }
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
            String subnet = (String) jcbSubnet.getSelectedItem();
            String net = (String) jTextFieldNetwork.getText();
            String act = (String) jTextFieldActivity.getText();
            String comments = (String) jTextFieldComments.getText();

            if (requestor.isEmpty() || requestor.equals("")) {
                requestor = "N/A";
            }
            // if bulk or single
            if (jRBulk.isSelected()) {
                int req = 0;
                entries = (int) jSpinnerBulk.getValue();
                BigDecimal entries_bd = new BigDecimal(entries);
                jRSingle.setSelected(false);
                jSpinnerBulk.setVisible(true);
                jBMarketsBulk.setVisible(true);
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

            String[] data = {cu, region, market, usersinfo.get(0), requestor, task_id, task, net, subnet, act,
                tech, sap, date, time_added, week, ftr, on_time, fail_ftr, fail_on_time, requests, comments};
            DefaultTableModel tblModel = (DefaultTableModel) jTableAddMetrics.getModel();

            for (int i = 0; i < entries; i++) {
                tblModel.addRow(data);
            }
            // Set all indices to 0
            jTextFieldTime.setText("");
            jTextFieldRequests.setText("");
            jTextFieldRequestor.setText("");
            jTextFieldComments.setText("");
            jcbFTR.setSelectedIndex(0);
            jcbOnTime.setSelectedIndex(0);
            jcbFailedFTR.setSelectedIndex(0);
            jcbFailedOnTime.setSelectedIndex(0);
            jRSingle.setSelected(true);
            jRBulk.setSelected(false);
            jSpinnerBulk.setVisible(false);
            jBMarketsBulk.setVisible(false);
            jSpinnerBulk.setValue(((SpinnerNumberModel) jSpinnerBulk.getModel()).getMinimum());
        } else {
            return;
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
        ClearDataPanel1();
        JOptionPane.showMessageDialog(this, "Table is empty.");
    }//GEN-LAST:event_jBClearTableActionPerformed

    private void jcbTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbTaskActionPerformed
        // Task combobox action, fill task id based on task
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
        jBMarketsBulk.setVisible(false);
    }//GEN-LAST:event_jRSingleActionPerformed

    private void jRBulkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBulkActionPerformed
        // Radio button 'Bulk' action
        jRBulk.setSelected(true);
        jRSingle.setSelected(false);
        jSpinnerBulk.setVisible(true);
        jBMarketsBulk.setVisible(true);
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
            String imported_file = fc.getSelectedFile().toString();
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
                        if (i < 6) {
                            finalValues.add(values[i]);
                            j = j + 1;
                        } else {
                            if (j == 6) {
                                if (values[i + 1].matches("\\d+")) {
                                    full_task += values[i];
                                    full_task = full_task.replace("\"", "");
                                    finalValues.add(full_task);
                                    j += 1;
                                } else {
                                    full_task += values[i] + ",";
                                }
                            } else if (j == 20) {
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
                                j += 1;
                            }
                        }
                    }
                    //Date format
                    Pattern pdate = Pattern.compile("^(([0-9][0-9][0-9][0-9]-)*([0-9][0-9]-)*([0-9][0-9]))$");  // Date format YYYY-MM-DD
                    Matcher mdate = pdate.matcher(finalValues.get(12));
                    boolean bdate = mdate.find();
                    if (!bdate) {
                        // Change date format
                        SimpleDateFormat dcn = new SimpleDateFormat("MM/dd/yyyy");
                        SimpleDateFormat dcn1 = new SimpleDateFormat("yyyy-MM-dd");
                        Date new_date = null;
                        try {
                            new_date = dcn.parse(finalValues.get(12));
                        } catch (ParseException ex) {
                            Logger.getLogger(VSS_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        finalValues.set(12, dcn1.format(new_date));
                    }
                    String[] row = new String[finalValues.size()];
                    finalValues.toArray(row);
                    tblModel.addRow(row);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(VSS_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(VSS_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
        String cu = jcbCU.getSelectedItem().toString();
        String path = "C:\\Users\\" + usersinfo.get(0) + "\\Documents\\Reporting_time_vss_" + usersinfo.get(0) + "_template.csv";
        try (PrintWriter writer = new PrintWriter(new File(path))) {

            StringBuilder sb = new StringBuilder();
            SimpleDateFormat dcn = new SimpleDateFormat("MM/dd/yyyy");
            String date_change = dcn.format(jDateChooser1.getDate());
            System.out.println("Date: " + date_change);

            // Header
            sb.append("Customer_Unit" + ',');
            sb.append("Region" + ',');
            sb.append("Market" + ',');
            sb.append("Signum" + ',');
            sb.append("Requestor" + ',');
            sb.append("Task_ID" + ',');
            sb.append("Task" + ',');
            sb.append("Network" + ',');
            sb.append("Subnetwork" + ',');
            sb.append("Activity_code" + ',');
            sb.append("Technology" + ',');
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
            // Get first 2 tasks to put in file
            int count = 0;
            ArrayList<String> tasks = new ArrayList<String>();
            ArrayList<String> nets = new ArrayList<String>();
            for (int i = 1; i < tasks_info.size(); i += 5) {
                if (tasks_info.get(i + 2).equals("SDU") || tasks_info.get(i + 2).equals(cu)) {
                    tasks.add(tasks_info.get(i - 1));   // Task ID
                    tasks.add(tasks_info.get(i));       // Task
                    tasks.add(tasks_info.get(i + 3));   // Billable or not
                }
            }
            for (int i = 5; i < networks_info.size(); i += 8) {
                if (networks_info.get(i - 1).equals(cu)) {
                    nets.add(networks_info.get(i - 5));   // Net
                    nets.add(networks_info.get(i));       // Subnet
                    nets.add(networks_info.get(i - 4));   // Act
                    nets.add(networks_info.get(i + 2));   // Tech
                    nets.add(networks_info.get(i - 2));   // Market
                    nets.add(networks_info.get(i - 3));   // Region
                }
            }
            while (count < 2) {
                // Insert row
                if (tasks.get(0).contains("ADMIN")) {
                    sb.append("SDU" + ',');                             // CU
                    sb.append("SDU" + ',');                             // Region
                    sb.append("SDU" + ',');                             // Market
                } else {
                    sb.append(cu + ',');
                    sb.append(nets.get(5 + (count * 6)) + ',');
                    sb.append(nets.get(4 + (count * 6)) + ',');
                }
                sb.append(usersinfo.get(0) + ',');                      // Signum
                sb.append("N/A" + ',');                                 // Requestor
                sb.append(tasks.get(0 + (count * 3)) + ',');            // Task ID
                sb.append(tasks.get(1 + (count * 3)) + ',');            // Task
                if (tasks.get(0 + (count * 3)).contains("ADMIN")) {
                    sb.append("0" + ',');
                    sb.append("0" + ',');
                    sb.append("0" + ',');
                    sb.append("N/A" + ',');
                } else {
                    sb.append(nets.get(0 + (count * 6)) + ',');  // Network
                    sb.append(nets.get(1 + (count * 6)) + ',');  // Subetwork
                    sb.append(nets.get(2 + (count * 6)) + ',');  // Activity
                    sb.append(nets.get(3 + (count * 6)) + ',');  // Technology
                }
                sb.append(tasks.get(2 + (count * 3)) + ',');    // SAP
                sb.append(date_change + ',');                   // Date
                sb.append("8.25" + ',');                        // Time
                sb.append(current_week);                        // Week
                sb.append(',');
                sb.append("N/A" + ',');                         // FTR
                sb.append("N/A" + ',');                         // On time
                sb.append("N/A" + ',');                         // Failed ftr
                sb.append("N/A" + ',');                         // Failed on time
                sb.append("1" + ',');                           // Num of requests
                sb.append("N/A" + ',');                         // Comments
                sb.append('\n');
                count += 1;
            }
            writer.write(sb.toString());
            System.out.println("Template generated successfully");
            int reply = JOptionPane.showConfirmDialog(null, "Do you want to see it?", "CSV created", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                Desktop.getDesktop().open(new File(path));
            } else {
                JOptionPane.showMessageDialog(this, "Template file was saved to " + path);
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(VSS_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
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
        jcbMarket.setSelectedIndex(0);
        GetDailyHours1();
        // Hide id
        TableColumnModel tcm = jTableSeeMetrics.getColumnModel();
        if (times_in_edit == 0) {
            tcm.removeColumn(tcm.getColumn(0));
        }
        times_in_edit += 1;
        DefaultTableModel tblModel = (DefaultTableModel) jTableSeeMetrics.getModel();
        tblModel.setRowCount(0);
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

    private void jBSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSearchActionPerformed
        // Button Search action
        if (jTableSeeMetrics.getEditingRow() != -1) {
            jTableSeeMetrics.getCellEditor().stopCellEditing();// In case there's selected a field in the table
        }
        metrics_for_ess.clear();
        jLabelLoading.setText("Loading...");
        DefaultTableModel dtm;
        jBDeleteRow1.setVisible(true);
        jButtonUpdate.setVisible(true);
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
                    jButtonUpdate.setVisible(false);
                } else {
                    a = 1;
                }

                metrics_vss_info.clear();
                DefaultTableModel model = (DefaultTableModel) jTableSeeMetrics.getModel();
                model.setRowCount(0);
                Connection connection = SQL_connection.getConnection();
                ResultSet resultSet;
                ResultSetMetaData rsm;
                int year = Calendar.getInstance().get(Calendar.YEAR);
                String qdate = null, sap = null;
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
                        String sql = "SELECT id, Customer_Unit, Region, Market, Signum, Requestor, Task_ID, "
                                + "Task, Technology, Network, Subnetwork, Activity_code, SAP_Billing, Work_date, "
                                + "Logged_Time, Week, FTR, On_Time, Failed_FTR_Category, Failed_On_Time, Num_requests, Comments "
                                + "FROM " + sql_table + " "
                                + "WHERE Signum=? AND " + qdate + " AND Task LIKE ? AND SAP_Billing LIKE ? ORDER BY Work_date ASC;";

                        PreparedStatement preparedStatement = connection.prepareStatement(sql);
                        String task = jcbTask1.getSelectedItem().toString();
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
                                metrics_for_ess.add(resultSet.getString(i + 1));
                            }
                            data.add(rows);

                            String[] row = {resultSet.getString("id"), resultSet.getString("Signum"), resultSet.getString("Requestor"),
                                resultSet.getString("Work_date"), resultSet.getString("Logged_Time"), resultSet.getString("Week"),
                                resultSet.getString("Comments")};
                            List<String> newList = Arrays.asList(row);

                            metrics_vss_info.addAll(newList);
                        }
                        System.out.println("Metrics: " + metrics_vss_info);

                        for (int i = 0; i < data.size(); i++) {
                            dtm.addRow(data.get(i));
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(VSS_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    j += 1;
                }
                try {
                    connection.close();
                } catch (SQLException ex) {
                    Logger.getLogger(VSS_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                }
                //
                jDLoading.dispose();
                if (model.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(VSS_Time_Report.this, "No data available for current selection");
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

    private void jButtonUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateActionPerformed
        // Button 'Update' action code
        if (jTableSeeMetrics.getEditingRow() != -1) {
            jTableSeeMetrics.getCellEditor().stopCellEditing();// In case there's selected a field in the table
        }

        jLabelLoading.setText("Updating metrics into database...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                String team = jcbTeam1.getSelectedItem().toString();
                Connection connection;
                CallableStatement callableStatement;
                boolean error = false;
                int rows = jTableSeeMetrics.getRowCount();

                if (rows > 0) {
                    connection = SQL_connection.getConnection();
                    int id = 0;

                    ArrayList<String> compare = new ArrayList<>();
                    String failed = "";

                    for (int row = 0; row < rows; row++) {
                        String signum = (String) jTableSeeMetrics.getValueAt(row, 3);
                        String requestor = (String) jTableSeeMetrics.getValueAt(row, 4);
                        String date = String.valueOf(jTableSeeMetrics.getValueAt(row, 12));
                        String time = String.valueOf(jTableSeeMetrics.getValueAt(row, 13));
                        String week = String.valueOf(jTableSeeMetrics.getValueAt(row, 14));
                        String comments = (String) jTableSeeMetrics.getValueAt(row, 20);
                        id = (Integer) jTableSeeMetrics.getModel().getValueAt(row, 0);

                        //Requestor format
                        Pattern prequestor1 = Pattern.compile("^[a-zA-Z ]+$");          // Regular name
                        Pattern prequestor2 = Pattern.compile("^([A-Za-z/]*)$");        // N/A
                        Matcher mrequestor1 = prequestor1.matcher(requestor);
                        Matcher mrequestor2 = prequestor2.matcher(requestor);
                        boolean brequestor1 = mrequestor1.find();
                        boolean brequestor2 = mrequestor2.find();
                        //Date format
                        Pattern pdate = Pattern.compile("^(([0-9][0-9][0-9][0-9]-)*([0-9][0-9]-)*([0-9][0-9]))$");  // Date format YYYY-MM-DD
                        Matcher mdate = pdate.matcher(date);
                        boolean bdate = mdate.find();
                        //Time format
                        Pattern ptime = Pattern.compile("^\\s*(?=.*[1-9])\\d*(?:\\.\\d{1,2})?\\s*$");
                        Matcher mtime = ptime.matcher(time);
                        boolean btime = mtime.find();
                        // Week according to date
                        SimpleDateFormat dcn = new SimpleDateFormat("yyyy-MM-dd");
                        Date date_ = new Date();
                        try {
                            date_ = dcn.parse(date);
                        } catch (ParseException ex) {
                            Logger.getLogger(VSS_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println("Error parsing date " + date);
                        }
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date_);
                        int correct_week = cal.get(Calendar.WEEK_OF_YEAR);

                        // Validate
                        if (!brequestor1 && !brequestor2) {
                            failed = "Requestor";
                        }
                        if (!bdate) {
                            failed = "Date";
                        }
                        if (!btime) {
                            failed = "Time";
                        }
                        int dayOfWeek = Check_Day(date);
                        if (dayOfWeek == 7) {
                            correct_week = correct_week - 1;
                        }
                        System.out.println("Day " + dayOfWeek + " Week: " + correct_week);
                        if (!week.equals(String.valueOf(correct_week)) || week.equals("")) {           // Week -> Validate week for date 
                            failed = "Week " + week;
                        }
                        if (comments.equals("") || comments.isEmpty()) {
                            failed = "Comments";
                        }
                        // If something failed show where it did and finish flow
                        if (!failed.equals("")) {
                            JOptionPane.showMessageDialog(VSS_Time_Report.this, "Please verify your info near " + failed);
                            error = true;
                            System.out.println("Error near " + failed);
                        }
                        ////
                        if (error) {
                            System.out.println("Failed to update.");
                            jDLoading.dispose();
                            return;
                        }
                        // Check if there were any changes to only update those
                        compare.add(Integer.toString(id));
                        compare.add(signum);
                        compare.add(requestor);
                        compare.add(date);
                        compare.add(time);
                        compare.add(week);
                        compare.add(comments);;
                    }

                    ArrayList<String> ids = new ArrayList<>();
                    int id_ch = 0;
                    for (int i = 0; i < metrics_vss_info.size(); i += 7) {
                        System.out.println(metrics_vss_info.get(i) + " | " + compare.get(i));
                        if (metrics_vss_info.get(i).equals(compare.get(i))) {
                            if (!(metrics_vss_info.get(i + 2).equals(compare.get(i + 2))) // Requestor
                                    || !(metrics_vss_info.get(i + 3).equals(compare.get(i + 3))) // Date
                                    || !(metrics_vss_info.get(i + 4).equals(compare.get(i + 4))) // Time
                                    || !(metrics_vss_info.get(i + 5).equals(compare.get(i + 5))) // Week
                                    || !(metrics_vss_info.get(i + 6).equals(compare.get(i + 6)))) { // Comments
                                ids.add(compare.get(i));
                                ids.add(compare.get(i + 1));
                                ids.add(compare.get(i + 2));
                                ids.add(compare.get(i + 3));
                                ids.add(compare.get(i + 4));
                                ids.add(compare.get(i + 5));
                                ids.add(compare.get(i + 6));
                                id_ch += 1;
                            }
                        }
                    }
                    System.out.println("IDs changed: " + id_ch);
                    System.out.println(metrics_vss_info);
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
                                    + "(?, ?, ?, ?, ?, ?, ?, ?);");
                            for (int row = 0; row < id_ch; row++) {
                                callableStatement.setObject(1, ids.get(0 + (6 + 1) * row));     // id
                                callableStatement.setObject(2, ids.get(1 + (6 + 1) * row));     // signum
                                callableStatement.setObject(3, ids.get(2 + (6 + 1) * row));     // requestor
                                callableStatement.setObject(4, ids.get(3 + (6 + 1) * row));     // date
                                callableStatement.setObject(5, ids.get(4 + (6 + 1) * row));     // time
                                callableStatement.setObject(6, ids.get(5 + (6 + 1) * row));     // week
                                callableStatement.setObject(7, ids.get(6 + (6 + 1) * row));     // comments

                                callableStatement.registerOutParameter(8, java.sql.Types.INTEGER);
                                callableStatement.executeUpdate();

                                int result = callableStatement.getInt(8);
                                if (result == 0) {
                                    saved = "Unable to update one or more rows because it would exceed 24 hours.";
                                } else {
                                    saved = "Data saved successfully!";
                                }
                                System.out.println("Entered: " + callableStatement + "\nResult: " + result);
                                // Update info in metrics_for_ess
                                int i = metrics_for_ess.indexOf(ids.get(0 + (6 + 1) * row));
                                System.out.println("ID for ESS: " + i);
                                metrics_for_ess.set(i + 5, ids.get(2 + (6 + 1) * row));      // Requestor
                                metrics_for_ess.set(i + 13, ids.get(3 + (6 + 1) * row));     // Date
                                metrics_for_ess.set(i + 14, ids.get(4 + (6 + 1) * row));     // Time
                                metrics_for_ess.set(i + 15, ids.get(5 + (6 + 1) * row));     // Week
                                metrics_for_ess.set(i + 21, ids.get(6 + (6 + 1) * row));     // Comments
                            }
                            callableStatement.close();
                            connection.close();
                            compare.clear();
                        } catch (SQLException e) {
                            System.out.println(e);
                            saved = "Something went wrong, please try again later.";
                        }
                    } else {
                        JOptionPane.showMessageDialog(VSS_Time_Report.this, "Nothing changed.");
                        jDLoading.dispose();
                        return;
                    }
                }
                // Refresh total hours
                GetDailyHours1();
                GetHours();
                VSS_Time_Report.this.setTitle("VSS               " + usersinfo.get(0) + " | " + usersinfo.get(4) + " | " + usersinfo.get(1) + " | " + "Week: " + current_week + " | "
                        + "Hours: " + hours);
                saved = "Data saved successfully!";
                jDLoading.dispose();
                JOptionPane.showMessageDialog(VSS_Time_Report.this, saved);
            }
        }).start();
        jDLoading.setVisible(true);
    }//GEN-LAST:event_jButtonUpdateActionPerformed

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
                        for (int z = 0; z < selected_rows.length; z++) {
                            System.out.println("Fila " + selected_rows[z]);
                        }
                        int x = 0;
                        for (int j = 0; j < selected_rows.length; j++) {
                            // Compare to get selected row's id 
                            int row = selected_rows[j];
                            System.out.println("Selected row: " + (row - x));
                            String id_ = jTableSeeMetrics.getModel().getValueAt((row - x), 0).toString();
                            System.out.println("id_" + id_);
                            int id = Integer.parseInt(id_);
                            System.out.println("ID to delete: " + id);

                            // Delete row from list
                            int i = metrics_vss_info.indexOf(id_);
                            int a = 0;
                            System.out.println("Index:" + i);
                            // Delete 6 elements in metrics_cop_info
                            while (a < 7) {
                                metrics_vss_info.remove(i);
                                a += 1;
                            }
                            // Delete 22 elements in metrics_for_ess
                            int in = metrics_for_ess.indexOf(id_);
                            a = 0;
                            while (a < 22) {
                                metrics_for_ess.remove(in);
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
                                Logger.getLogger(VSS_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            x += 1;
                        } // End for
                        try {
                            connection.close();
                        } catch (SQLException ex) {
                            Logger.getLogger(VSS_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        GetDailyHours1();
                        JOptionPane.showMessageDialog(VSS_Time_Report.this, "Row deleted.");
                        System.out.println("After delete row: " + metrics_vss_info);
                    }
                } else {
                    JOptionPane.showMessageDialog(VSS_Time_Report.this, "Select one row.");
                }
                jDLoading.dispose();
            }
        }).start();
        jDLoading.setVisible(true);
    }//GEN-LAST:event_jBDeleteRow1ActionPerformed

    private void jcbSAPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbSAPActionPerformed
        // SAP combobox action
        String sap = (String) jcbSAP.getSelectedItem();
        jcbTask.removeAllItems();
        jcbTask.addItem("Select an activity...");
        ArrayList<String> check_list = new ArrayList<>();
        String task = null;

        if (sap.equals("Not Billable")) {
            jcbRegion.removeAllItems();
            jcbRegion.addItem("SDU");
            jcbMarket.removeAllItems();
            jcbMarket.addItem("SDU");
            jcbTech.removeAllItems();
            jcbTech.addItem("N/A");
            // Fill tasks
            for (int i = 1; i < tasks_info.size(); i += 5) {
                task = tasks_info.get(i);
                if (tasks_info.get(i + 2).equals("SDU")) {
                    if (!check_list.contains(task)) {
                        check_list.add(task);
                        jcbTask.addItem(task);
                    }
                }
            }
        } else {
            // Fill tasks
            for (int i = 1; i < tasks_info.size(); i += 5) {
                task = tasks_info.get(i);

                if (tasks_info.get(i + 2).equals(jcbCU.getSelectedItem().toString())) {
                    if (!check_list.contains(task)) {
                        check_list.add(task);
                        jcbTask.addItem(task);
                    }
                }
            }
            // Fill region
            jcbRegion.removeAllItems();
            jcbRegion.addItem("Select a region...");

            ArrayList<String> regions = new ArrayList<>();
            for (int i = 2; i < networks_info.size(); i += 8) {
                if (!regions.contains(networks_info.get(i))) {
                    if (networks_info.get(i + 2).equals(jcbCU.getSelectedItem().toString())) {
                        regions.add(networks_info.get(i));

                    }
                }
            }
            Collections.sort(regions);
            for (int i = 0; i < regions.size(); i++) {
                jcbRegion.addItem(regions.get(i));
            }
            // Fill market
            jcbMarket.removeAllItems();
            jcbMarket.addItem("Select a market...");
            String region = null;
            if (jcbRegion.getItemCount() != 0) {
                region = jcbRegion.getSelectedItem().toString();
            }
            ArrayList<String> mrkts = new ArrayList<>();
            for (int i = 3; i < networks_info.size(); i += 8) {
                if (!mrkts.contains(networks_info.get(i))) {
                    if (networks_info.get(i + 1).equals(jcbCU.getSelectedItem().toString())) {
                        if (networks_info.get(i - 1).equals(region)) {
                            mrkts.add(networks_info.get(i));
                        }
                    }
                }
            }
            Collections.sort(mrkts);
            for (int i = 0; i < mrkts.size(); i++) {
                jcbMarket.addItem(mrkts.get(i));
            }
        }

        // Refresh jcbSubnetwork
        jcbSubnet.removeAllItems();
        if (jcbSAP.getSelectedIndex() == 0) {
            jcbSubnet.addItem("Select a subnetwork...");
            for (int i = 5; i < networks_info.size(); i += 8) {
                if (networks_info.get(i - 1).equals(jcbCU.getSelectedItem().toString())) { // Mostrar solo para CU seleccionada
                    if (networks_info.get(i - 3).equals(jcbRegion.getSelectedItem().toString())) {
                        if (networks_info.get(i - 2).equals(jcbMarket.getSelectedItem().toString())) {
                            jcbSubnet.addItem(networks_info.get(i));
                        }
                    }
                }
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
        // jcbCU action, fills regions and tasks, and shows/hide tech
        if (jcbSAP.getSelectedItem().toString().equals("Not Billable")) {
            jcbRegion.removeAllItems();
            jcbRegion.addItem("SDU");
            jcbMarket.removeAllItems();
            jcbMarket.addItem("SDU");
        } else {
            jcbRegion.removeAllItems();
            jcbRegion.addItem("Select a region...");

            ArrayList<String> regions = new ArrayList<>();
            for (int i = 2; i < networks_info.size(); i += 8) {
                if (!regions.contains(networks_info.get(i))) {
                    if (networks_info.get(i + 2).equals(jcbCU.getSelectedItem().toString())) {
                        regions.add(networks_info.get(i));

                    }
                }
            }
            Collections.sort(regions);
            for (int i = 0; i < regions.size(); i++) {
                jcbRegion.addItem(regions.get(i));
            }

            // Fill tasks
            String sap = (String) jcbSAP.getSelectedItem();
            jcbTask.removeAllItems();
            jcbTask.addItem("Select an activity...");

            ArrayList<String> check_list = new ArrayList<>();
            String task = null;
            for (int i = 1; i < tasks_info.size(); i += 5) {
                task = tasks_info.get(i);

                if (tasks_info.get(i + 2).equals(jcbCU.getSelectedItem().toString()) && sap.equals("Billable")) {
                    if (!check_list.contains(task)) {
                        check_list.add(task);
                        jcbTask.addItem(task);
                    }
                } else if (tasks_info.get(i + 2).equals("SDU") && sap.equals("Not Billable")) {
                    if (!check_list.contains(task)) {
                        check_list.add(task);
                        jcbTask.addItem(task);
                    }
                }
            }

            // Tech jcombobox
            if (!jcbCU.getSelectedItem().toString().equals("AT&T")) {
                jcbTech.setVisible(true);
                jLabelTech.setVisible(true);
            } else {
                jcbTech.setVisible(false);
                jLabelTech.setVisible(false);
            }
        }
    }//GEN-LAST:event_jcbCUActionPerformed

    private void jcbTeam1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbTeam1ActionPerformed
        // jcbTeam1 action, fill tasks in 'Edit' window
        String switch_ = (String) jcbSAP1.getSelectedItem();
        String team = (String) jcbTeam1.getSelectedItem();
        ArrayList<String> check_list = new ArrayList<>();
        jcbTask1.removeAllItems();
        jcbTask1.addItem("Select an activity...");
        String task = null;
        if (team.equals("VSS")) {
            for (int i = 0; i < tasks_info.size(); i++) {
                String check = tasks_info.get(i);
                if (switch_.equals("All")) {
                    if (check.equals("Billable") || check.equals("Not Billable")) {
                        int a = i - 3;
                        task = tasks_info.get(a);
                        if (task.contains("  ")) {
                            task = task.replace("  ", "");
                        }
                        if (!check_list.contains(task)) {
                            check_list.add(task);
                            jcbTask1.addItem(task);
                        }
                    }
                } else if (switch_.equals("Billable")) {
                    if (check.equals(switch_)) {
                        int a = i - 3;
                        task = tasks_info.get(a);
                        if (task.contains("  ")) {
                            task = task.replace("  ", "");
                        }
                        if (!check_list.contains(task)) {
                            check_list.add(task);
                            jcbTask1.addItem(task);
                        }
                    }
                } else {
                    if (check.equals(switch_)) {
                        int a = i - 3;
                        task = tasks_info.get(a);
                        if (task.contains("  ")) {
                            task = task.replace("  ", "");
                        }
                        if (!check_list.contains(task)) {
                            check_list.add(task);
                            jcbTask1.addItem(task);
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_jcbTeam1ActionPerformed

    private void jcbSAP1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbSAP1ActionPerformed
        // jcbSAP1 action, fill tasks in 'Edit' window
        String switch_ = (String) jcbSAP1.getSelectedItem();
        String team = (String) jcbTeam1.getSelectedItem();
        ArrayList<String> check_list = new ArrayList<>();
        jcbTask1.removeAllItems();
        jcbTask1.addItem("Select an activity...");
        String task = null;
        if (team.equals("VSS")) {
            for (int i = 0; i < tasks_info.size(); i++) {
                String check = tasks_info.get(i);
                if (switch_.equals("All")) {
                    if (check.equals("Billable") || check.equals("Not Billable")) {
                        int a = i - 3;
                        task = tasks_info.get(a);
                        if (task.contains("  ")) {
                            task = task.replace("  ", "");
                        }
                        if (!check_list.contains(task)) {
                            check_list.add(task);
                            jcbTask1.addItem(task);
                        }
                    }
                } else if (switch_.equals("Billable")) {
                    if (check.equals(switch_)) {
                        int a = i - 3;
                        task = tasks_info.get(a);
                        if (task.contains("  ")) {
                            task = task.replace("  ", "");
                        }
                        if (!check_list.contains(task)) {
                            check_list.add(task);
                            jcbTask1.addItem(task);
                        }
                    }
                } else {
                    if (check.equals(switch_)) {
                        int a = i - 3;
                        task = tasks_info.get(a);
                        if (task.contains("  ")) {
                            task = task.replace("  ", "");
                        }
                        if (!check_list.contains(task)) {
                            check_list.add(task);
                            jcbTask1.addItem(task);
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_jcbSAP1ActionPerformed

    private void jBCatalogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCatalogActionPerformed
        // Catalog button action
        jFrameCatalog.setTitle("Networks Catalog");
        jFrameCatalog.setIconImage(new ImageIcon(getClass().getResource("/images/MRT_logo.png")).getImage());
        jFrameCatalog.setLocationRelativeTo(null);
        jFrameCatalog.setSize(1300, 600);
        JTableHeader header = jTableCatalog.getTableHeader();
        header.setBackground(new Color(0, 130, 240));
        header.setForeground(Color.white);
        header.setFont(new Font("Ericsson Hilda", 0, 20));
        try {
            String cu = jcbCU.getSelectedItem().toString();
            DefaultTableModel model = (DefaultTableModel) jTableCatalog.getModel();
            model.setRowCount(0);
            Connection connection = SQL_connection.getConnection();
            ResultSet resultSet = null;
            ResultSetMetaData rsm = null;
            String sql = "SELECT * "
                    + "FROM networks " // CHANGE this table
                    + "WHERE Team = 'VSS' AND Customer = '" + cu + "' "
                    + "ORDER BY Customer, Region, Market;";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
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
            }
            for (int i = 0; i < data.size(); i++) {
                model.addRow(data.get(i));
            }

        } catch (SQLException ex) {
            Logger.getLogger(VSS_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
        }
        jFrameCatalog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        jFrameCatalog.setVisible(true);
    }//GEN-LAST:event_jBCatalogActionPerformed

    private void jcbMarketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbMarketActionPerformed
        // Populate technology and subnetwork
        jcbTech.removeAllItems();
        jcbTech.addItem("Select a technology...");

        String market = null;
        String region = null;
        if (jcbMarket.getItemCount() != 0) {
            market = jcbMarket.getSelectedItem().toString();
        }
        if (jcbRegion.getItemCount() != 0) {
            region = jcbRegion.getSelectedItem().toString();
        }
        ArrayList<String> techs = new ArrayList<>();
        for (int i = 7; i < networks_info.size(); i += 8) {
            if (!techs.contains(networks_info.get(i))) {
                if (networks_info.get(i - 3).equals(jcbCU.getSelectedItem().toString())) {
                    if (networks_info.get(i - 5).equals(region)) {
                        if (networks_info.get(i - 4).equals(market)) {
                            techs.add(networks_info.get(i));
                        }
                    }
                }
            }
        }
        Collections.sort(techs);
        for (int i = 0; i < techs.size(); i++) {
            jcbTech.addItem(techs.get(i));
        }
        // Refresh jcbSubnetwork
        jcbSubnet.removeAllItems();
        if (jcbSAP.getSelectedIndex() == 0) {
            jcbSubnet.addItem("Select a subnetwork...");
            for (int i = 5; i < networks_info.size(); i += 8) {
                if (networks_info.get(i - 1).equals(jcbCU.getSelectedItem().toString())) { // Mostrar solo para CU seleccionada
                    if (networks_info.get(i - 3).equals(region)) {
                        if (networks_info.get(i - 2).equals(market)) {
                            jcbSubnet.addItem(networks_info.get(i));
                        }
                    }
                }
            }
        } else {
            jcbSubnet.addItem("0");
        }
    }//GEN-LAST:event_jcbMarketActionPerformed

    private void jcbRegionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbRegionActionPerformed
        // combobox Region action
        // Populate Market list and check for duplicates
        jcbMarket.removeAllItems();
        jcbMarket.addItem("Select a market...");
        String region = null;
        if (jcbRegion.getItemCount() != 0) {
            region = jcbRegion.getSelectedItem().toString();
        }
        ArrayList<String> mrkts = new ArrayList<>();
        for (int i = 3; i < networks_info.size(); i += 8) {
            if (!mrkts.contains(networks_info.get(i))) {
                if (networks_info.get(i + 1).equals(jcbCU.getSelectedItem().toString())) {
                    if (networks_info.get(i - 1).equals(region)) {
                        mrkts.add(networks_info.get(i));
                    }
                }
            }
        }
        Collections.sort(mrkts);
        for (int i = 0; i < mrkts.size(); i++) {
            jcbMarket.addItem(mrkts.get(i));
        }
    }//GEN-LAST:event_jcbRegionActionPerformed

    private void jcbTechActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbTechActionPerformed
        // Technology combobox action, fill subnetwork
        String market = null;
        String region = null;
        String tech = null;
        if (jcbMarket.getItemCount() != 0) {
            market = jcbMarket.getSelectedItem().toString();
        }
        if (jcbRegion.getItemCount() != 0) {
            region = jcbRegion.getSelectedItem().toString();
        }
        if (jcbTech.getItemCount() != 0) {
            tech = jcbTech.getSelectedItem().toString();
        }

        jcbSubnet.removeAllItems();
        if (jcbSAP.getSelectedIndex() == 0) {
            jcbSubnet.addItem("Select a subnetwork...");
            for (int i = 5; i < networks_info.size(); i += 8) {
                if (networks_info.get(i - 1).equals(jcbCU.getSelectedItem().toString())) { // Mostrar solo para CU seleccionada
                    if (networks_info.get(i - 3).equals(region)) {
                        if (networks_info.get(i - 2).equals(market)) {
                            if (networks_info.get(i + 2).equals(tech)) {
                                jcbSubnet.addItem(networks_info.get(i));
                            }
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_jcbTechActionPerformed

    private void SaveTableCSV(String fileName, TableModel tableModel) throws IOException {
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
                    sb.append("\"" + s + "\"");
                    sb.append(',');
                }
                sb.append("\n");
            }
            writer.write(sb.toString());
            System.out.println("Metrics file saved successfully");
            int reply = JOptionPane.showConfirmDialog(null, "Do you want to see it?", "File created", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                Desktop.getDesktop().open(new File(path));
            } else {
                JOptionPane.showMessageDialog(this, "File was saved to " + path);
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void jBExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBExportActionPerformed
        // Export button, save what's in table to csv
        int rows = jTableSeeMetrics.getRowCount();
        if (rows > 0) {
            String week = jcbWeek1.getSelectedItem().toString();
            String team = jcbTeam1.getSelectedItem().toString();
            String fileName = usersinfo.get(0) + "_metrics_" + team + "_week_" + week;
            TableModel model = jTableSeeMetrics.getModel();
            try {
                SaveTableCSV(fileName, model);
            } catch (IOException ex) {
                Logger.getLogger(VSS_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Table is empty!");
        }
    }//GEN-LAST:event_jBExportActionPerformed

    private void jBMarketsBulkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBMarketsBulkActionPerformed
        // Markets Bulk button action
        // Initialize frame
        jFrameMarketsBulk.setTitle("Bulk for markets");
        jFrameMarketsBulk.setIconImage(new ImageIcon(getClass().getResource("/images/MRT_logo.png")).getImage());
        jFrameMarketsBulk.setSize(1300, 600);
        jFrameMarketsBulk.setResizable(false);
        jFrameMarketsBulk.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        jFrameMarketsBulk.setVisible(true);
        jFrameMarketsBulk.setLocationRelativeTo(null);

        // Load regions for CU
        jcbRegionBulk.removeAllItems();
        jcbRegionBulk.addItem("Select a region...");

        ArrayList<String> regions = new ArrayList<>();
        for (int i = 2; i < networks_info.size(); i += 8) {
            if (!regions.contains(networks_info.get(i))) {
                if (networks_info.get(i + 2).equals(jcbCU.getSelectedItem().toString())) {
                    regions.add(networks_info.get(i));

                }
            }
        }
        Collections.sort(regions);
        for (int i = 0; i < regions.size(); i++) {
            jcbRegionBulk.addItem(regions.get(i));
        }

        // Fill tasks
        jcbTask_MarketBulk.removeAllItems();
        jcbTask_MarketBulk.addItem("Select an activity...");

        ArrayList<String> check_list = new ArrayList<>();
        String task = null;
        for (int i = 1; i < tasks_info.size(); i += 5) {
            task = tasks_info.get(i);

            if (tasks_info.get(i + 2).equals(jcbCU.getSelectedItem().toString())) {
                if (!check_list.contains(task)) {
                    check_list.add(task);
                    jcbTask_MarketBulk.addItem(task);
                }
            }
        }
    }//GEN-LAST:event_jBMarketsBulkActionPerformed

    private void jcbRegionBulkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbRegionBulkActionPerformed
        // Region Bulk combobox action
        mrkts_bulk.clear();
        jPanelMarketSelection.removeAll();
        jPanelMarketSelection.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        // Get markets for specified region
        String region = "Select a region...";
        if (jcbRegionBulk.getItemCount() != 0) {
            region = jcbRegionBulk.getSelectedItem().toString();
        }
        ArrayList<String> mrkts = new ArrayList<>();
        for (int i = 3; i < networks_info.size(); i += 8) {
            if (!mrkts.contains(networks_info.get(i))) {
                if (networks_info.get(i + 1).equals(jcbCU.getSelectedItem().toString())) {
                    if (networks_info.get(i - 1).equals(region)) {
                        mrkts.add(networks_info.get(i));
                    }
                }
            }
        }
        Collections.sort(mrkts);

        // Add 'All' checkbox
        all_markets_checkbox = false;
        if (!region.equals("Select a region...")) {
            JCheckBox all = new JCheckBox("All", false);
            all.setFont(new java.awt.Font("Ericsson Hilda", Font.PLAIN, 18));
            jPanelMarketSelection.add(all, gbc);
            // Action for 'All' checkbox
            all.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    JCheckBox cb = (JCheckBox) event.getSource();
                    if (cb.isSelected()) {
                        all_markets_checkbox = true;
                        Change_Checkboxes_MarketBulk(jPanelMarketSelection);
                    } else {
                        all_markets_checkbox = false;
                        Change_Checkboxes_MarketBulk(jPanelMarketSelection);
                    }
                }
            });
        }

        // Add market checkboxes to Panel
        for (int i = 0; i < mrkts.size(); i++) {
            JCheckBox market = new JCheckBox(mrkts.get(i), all_markets_checkbox);
            market.setFont(new java.awt.Font("Ericsson Hilda", Font.PLAIN, 18));
            jPanelMarketSelection.add(market, gbc);
            if (all_markets_checkbox == true) {
                mrkts_bulk.add(market.getText());
            }
            // Action for each checkbox
            market.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    JCheckBox cb = (JCheckBox) event.getSource();
                    if (cb.isSelected()) {
                        if (!Arrays.asList(mrkts_bulk).contains(market.getText())) {
                            mrkts_bulk.add(market.getText());
                        }
                    } else {
                        int index = mrkts_bulk.indexOf(market.getText());
                        mrkts_bulk.remove(index);
                    }
                }
            });
        }

        // Refresh Panel to show checkboxes
        jPanelMarketSelection.revalidate();
        jPanelMarketSelection.repaint();
    }//GEN-LAST:event_jcbRegionBulkActionPerformed

    public void Change_Checkboxes_MarketBulk(JPanel jPanelMarketSelection) {
        mrkts_bulk.clear();
        // Review every checkbox in jPanel
        for (Component component : jPanelMarketSelection.getComponents()) {
            if (component instanceof JCheckBox) {
                JCheckBox c = (JCheckBox) component;
                if (all_markets_checkbox) {
                    c.setSelected(true);
                    if (!Arrays.asList(mrkts_bulk).contains(c.getText())) {
                        mrkts_bulk.add(c.getText());
                    }
                } else {
                    c.setSelected(false);
                    mrkts_bulk.remove(c.getText());
                }
            }
        }
    }

    public void ClearMarketBulk() {
        Date date = new Date();
        jcbRegionBulk.setSelectedIndex(0);
        jTextFieldNumReqMarketBulk.setText("");
        jDateChooser_MarketBulk.setDate(date);
        jTextFieldTime_MarketBulk.setText("");
        jTextFieldRequestor_MarketBulk.setText("");
        jcbOnTime_MarketBulk.setSelectedIndex(0);
        jcbFailedOnTime_MarketBulk.setSelectedIndex(0);
        jcbFTR_MarketBulk.setSelectedIndex(0);
        jcbFailedFTR_MarketBulk.setSelectedIndex(0);
        jcbTask_MarketBulk.setSelectedIndex(0);
        jTextFieldComments_MarketBulk.setText("");
    }

    private void jcbTask_MarketBulkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbTask_MarketBulkActionPerformed
        // Task combobox action Market_Bulk, fill task id based on task
        String task = (String) jcbTask_MarketBulk.getSelectedItem();
        for (int i = 0; i < tasks_info.size(); i++) {
            String task_list_element = tasks_info.get(i);
            if (task_list_element.equals(task)) {
                int a = i - 1;
                jTextFieldTask_ID_MarketBulk.setText(tasks_info.get(a));
            }
        }
        if (jcbTask_MarketBulk.getSelectedIndex() == 0) {
            jTextFieldTask_ID_MarketBulk.setText("");
        }
    }//GEN-LAST:event_jcbTask_MarketBulkActionPerformed

    private void jBAddBulkMarketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBAddBulkMarketActionPerformed
        // Add button from Bulk for markets
        Boolean correct_flow = true;
        // Validate region
        if (jcbRegionBulk.getSelectedItem().equals("Select a region...")) {
            JOptionPane.showMessageDialog(this, "Please select a region");
            correct_flow = false;
        }
        // Validate task
        if (jcbTask_MarketBulk.getSelectedItem().equals("Select an activity...")) {
            JOptionPane.showMessageDialog(this, "Please select a task");
            correct_flow = false;
        }
        // Validate num of requests field
        Pattern preq = Pattern.compile("[^0-9]");
        String sreq = jTextFieldNumReqMarketBulk.getText();
        Matcher mreq = preq.matcher(sreq);
        boolean breq = mreq.find();

        if (jTextFieldNumReqMarketBulk.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Number of requests cannot be empty!");
            correct_flow = false;
        } else if (breq) {
            JOptionPane.showMessageDialog(this, "Number of requests must be a number!");
            jTextFieldNumReqMarketBulk.setText("");
            correct_flow = false;
        }
        // Validate logged time
        Pattern ptime = Pattern.compile("[^0-9.0-9]");
        String stime = jTextFieldTime_MarketBulk.getText();
        Matcher mtime = ptime.matcher(stime);
        boolean btime = mtime.find();
        if (stime.equals("") || btime) {
            JOptionPane.showMessageDialog(this, "Logged Time must be a number! \nEx: 2 or 8.25");
            jTextFieldTime_MarketBulk.setText("");
            correct_flow = false;
        } else if (Double.parseDouble(stime) > 24) {
            JOptionPane.showMessageDialog(this, "Logged Time cannot be higher than 24");
            jTextFieldTime_MarketBulk.setText("");
            correct_flow = false;
        }
        // Validate requestor
        Pattern preqtr = Pattern.compile("[^A-Za-z ]");
        String sreqtr = jTextFieldRequestor_MarketBulk.getText();
        Matcher mreqtr = preqtr.matcher(sreqtr);
        boolean breqtr = mreqtr.find();
        if (breqtr) {
            JOptionPane.showMessageDialog(this, "Requestor must be a a name!");
            jTextFieldRequestor_MarketBulk.setText("");
            correct_flow = false;
        }
        // Validate date
        int validate_date = Integer.parseInt(jTextFieldWeek_MarketBulk.getText());
        int thresh1 = current_week - 3;
        int thresh2 = current_week + 2;
        if (validate_date < thresh1 || validate_date > thresh2) {
            JOptionPane.showMessageDialog(this, "You can only add data from week " + thresh1 + " until week " + thresh2);
            correct_flow = false;
        }
        // //

        // Fill rows
        String cu = (String) jcbCU.getSelectedItem();
        String region = (String) jcbRegionBulk.getSelectedItem();
        String sap = "Billable";
        String task_id = (String) jTextFieldTask_ID_MarketBulk.getText();
        String task = (String) jcbTask_MarketBulk.getSelectedItem();
        if (task.contains("  ")) {
            task = task.replace("  ", "");
        }
        SimpleDateFormat dcn = new SimpleDateFormat("yyyy-MM-dd");
        String date = dcn.format(jDateChooser_MarketBulk.getDate());
        BigDecimal time = new BigDecimal(jTextFieldTime_MarketBulk.getText());
        String week = (String) jTextFieldWeek_MarketBulk.getText();
        String requestor = (String) jTextFieldRequestor_MarketBulk.getText();
        String ftr = (String) jcbFTR_MarketBulk.getSelectedItem();
        String on_time = (String) jcbOnTime_MarketBulk.getSelectedItem();
        String fail_ftr = (String) jcbFailedFTR_MarketBulk.getSelectedItem();
        String fail_on_time = (String) jcbFailedOnTime_MarketBulk.getSelectedItem();
        String requests = (String) jTextFieldNumReqMarketBulk.getText();
        String comments = (String) jTextFieldComments_MarketBulk.getText();

        if (requestor.isEmpty() || requestor.equals("")) {
            requestor = "N/A";
        }
        // Count rows to divide time
        int total_rows = 0;
        for (int z = 2; z < networks_info.size(); z += 8) {
            if (networks_info.get(z + 4).equals("VSS") && networks_info.get(z + 2).equals(cu)) {
                for (int i = 0; i < mrkts_bulk.size(); i++) {
                    if (region.equals(networks_info.get(z)) && mrkts_bulk.get(i).equals(networks_info.get(z + 1))) {
                        total_rows += 1;
                    }
                }
            }
        }
        // Divide time by the number of entries
        BigDecimal entries_bd = new BigDecimal(total_rows);
        // Round to 2 decimals
        time = time.divide(entries_bd, 2, RoundingMode.CEILING);
        String time_added = String.valueOf(time);
        // Insert into table
        if (correct_flow == true) {
            for (int i = 0; i < mrkts_bulk.size(); i++) {
                // Fill market i
                String market = mrkts_bulk.get(i);
                // Count networks in market i
                ArrayList<String> markets_nets = new ArrayList<>();
                for (int z = 2; z < networks_info.size(); z += 8) {
                    if (networks_info.get(z + 4).equals("VSS") && networks_info.get(z + 2).equals(cu)) {
                        if (region.equals(networks_info.get(z)) && market.equals(networks_info.get(z + 1))) {
                            markets_nets.add(networks_info.get(z - 2)); // Network
                            markets_nets.add(networks_info.get(z + 3)); // Subnetwork
                            markets_nets.add(networks_info.get(z - 1)); // Activity code
                            markets_nets.add(networks_info.get(z + 5)); // Technology
                        }
                    }
                }
                // Add networks in market
                for (int j = 0; j < markets_nets.size(); j += 4) {
                    String net = markets_nets.get(j);
                    String subnet = markets_nets.get(j + 1);
                    String act = markets_nets.get(j + 2);
                    String tech = markets_nets.get(j + 3);

                    // Data in row
                    String[] data = {cu, region, market, usersinfo.get(0), requestor, task_id, task, net, subnet, act,
                        tech, sap, date, time_added, week, ftr, on_time, fail_ftr, fail_on_time, requests, comments};

                    // Add row to table
                    DefaultTableModel tblModel = (DefaultTableModel) jTableAddMetrics.getModel();
                    tblModel.addRow(data);
                }
            }

            ClearMarketBulk();
            jFrameMarketsBulk.dispose();
            this.setEnabled(true);
            this.toFront();
        }
    }//GEN-LAST:event_jBAddBulkMarketActionPerformed

    private void jBCancel_MarketBulkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCancel_MarketBulkActionPerformed
        // Cancel button for market bulk
        this.setEnabled(true);
        jFrameMarketsBulk.dispose();
    }//GEN-LAST:event_jBCancel_MarketBulkActionPerformed

    private void jBHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBHistoryActionPerformed
        // History button
        // Initialize frame
        jFrameHistory.setTitle("Search for period of time");
        jFrameHistory.setIconImage(new ImageIcon(getClass().getResource("/images/MRT_logo.png")).getImage());
        jFrameHistory.setSize(400, 300);
        jFrameHistory.setResizable(false);
        jFrameHistory.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        jFrameHistory.setVisible(true);
        jFrameHistory.setLocationRelativeTo(null);

        // Init jcombobox CU
        String[] teams_ = usersinfo.get(7).split("@");
        List<String> teams = new ArrayList<String>(Arrays.asList(teams_));
        teams.add(usersinfo.get(3));
        jcb_Team_history.addItem("VSS");
        if (!usersinfo.get(7).equals("N/A")) {
            for (int i = 0; i < teams.size(); i++) {
                if (!teams.get(i).equals("VSS")) {
                    jcb_Team_history.addItem(teams.get(i));
                }
            }
        }
        jcb_Team_history.addItem("All");
        // Init jDataChoosers
        JTextFieldDateEditor editor_start = (JTextFieldDateEditor) jDateChooser_Start.getDateEditor();
        JTextFieldDateEditor editor_end = (JTextFieldDateEditor) jDateChooser_End.getDateEditor();
        editor_start.setEditable(false);
        editor_end.setEditable(false);
        Date date = new Date();
        jDateChooser_Start.setDate(date);
        jDateChooser_End.setDate(date);
    }//GEN-LAST:event_jBHistoryActionPerformed

    private void jB_ExportDatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_ExportDatesActionPerformed
        // Export history button
        jLabelLoading.setText("Loading...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Check selected team
                List<String> teams = new ArrayList<>();
                ArrayList<String> data = new ArrayList<>();
                PreparedStatement preparedStatement;

                if (jcb_Team_history.getSelectedItem().toString().equals("All")) {
                    String[] teams_ = usersinfo.get(7).split("@");
                    teams = new ArrayList<String>(Arrays.asList(teams_));
                    teams.add(usersinfo.get(3)); // Add Sourcing
                } else {
                    teams.add(jcb_Team_history.getSelectedItem().toString());
                }
                String sql_table = "", sql = "";
                for (int k = 0; k < teams.size(); k++) {
                    sql_table = "metrics_" + teams.get(k).toLowerCase();
                    Connection connection = SQL_connection.getConnection();
                    ResultSet resultSet;

                    sql = "SELECT Customer_Unit, Region, Market, Signum, Requestor, Task_ID, "
                            + "Task, Network, Subnetwork, Activity_code, Technology, SAP_Billing, Work_date, "
                            + "Logged_Time, Week, FTR, On_Time, Failed_FTR_Category, Failed_On_Time, Num_requests, Comments "
                            + "FROM " + sql_table + " "
                            + "WHERE Signum=? AND Work_date BETWEEN ? AND ?;";

                    try {
                        preparedStatement = connection.prepareStatement(sql);

                        SimpleDateFormat dcn = new SimpleDateFormat("yyyy-MM-dd");
                        String date_start = dcn.format(jDateChooser_Start.getDate());
                        String date_end = dcn.format(jDateChooser_End.getDate());
                        // Place parameters int query
                        preparedStatement.setString(1, usersinfo.get(0));
                        preparedStatement.setString(2, date_start);
                        preparedStatement.setString(3, date_end);

                        // Execute query
                        System.out.println("History query: " + preparedStatement);
                        resultSet = preparedStatement.executeQuery();
                        // Fetch results   
                        while (resultSet.next()) {
                            for (int i = 0; i < 21; i++) {
                                data.add(resultSet.getString(i + 1));
                            }
                        }
                        connection.close();
                        System.out.println("Data: " + data);
                    } catch (SQLException ex) {
                        Logger.getLogger(Sourcing_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                // Create csv with data
                String path = "C:\\Users\\" + usersinfo.get(0) + "\\Documents\\Result_reporting_time_" + usersinfo.get(0) + ".csv";
                try (PrintWriter writer = new PrintWriter(new File(path))) {
                    StringBuilder sb = new StringBuilder();
                    // Header
                    sb.append("Customer_Unit" + ',');
                    sb.append("Region" + ',');
                    sb.append("Market" + ',');
                    sb.append("Signum" + ',');
                    sb.append("Requestor" + ',');
                    sb.append("Task_ID" + ',');
                    sb.append("Task" + ',');
                    sb.append("Network" + ',');
                    sb.append("Subnetwork" + ',');
                    sb.append("Activity_code" + ',');
                    sb.append("Technology" + ',');
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
                    // Fill rows
                    for (int i = 1; i < data.size() + 1; i++) {
                        sb.append("\"" + data.get(i - 1) + "\"");
                        if (((i % 21) == 0)) {
                            //sb.append(",");
                            sb.append("\n");
                        } else {
                            sb.append(",");
                        }
                    }
                    writer.write(sb.toString());
                    System.out.println("File writed successfully");
                    int reply = JOptionPane.showConfirmDialog(null, "Do you want to see it?", "History file created", JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        Desktop.getDesktop().open(new File(path));
                    } else {
                        JOptionPane.showMessageDialog(VSS_Time_Report.this, "File was saved to " + path);
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(VSS_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(VSS_Time_Report.this, "File could not be created");
                    jDLoading.dispose();
                    jFrameHistory.dispose();
                    return;
                } catch (IOException ex) {
                    Logger.getLogger(VSS_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                }
                jDLoading.dispose();
            }
        }
        ).start();
        jDLoading.setVisible(true);
        jFrameHistory.dispose();
    }//GEN-LAST:event_jB_ExportDatesActionPerformed

    private void jMenuItem_SavedTemplateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_SavedTemplateActionPerformed
        // Import previously saved template to table
        jPanel1.setVisible(true);
        jPanel2.setVisible(false);
        jPanel3.setVisible(false);
        DefaultTableModel tblModel = (DefaultTableModel) jTableAddMetrics.getModel();
        tblModel.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\" + usersinfo.get(0) + "\\Documents\\MRT\\54v3d_73mp1473_v55.csv"))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                List<String> finalValues = new ArrayList<String>();
                String full_task = "";
                String full_comment = "";
                int commas_size = values.length, j = 0;

                for (int i = 0; i < commas_size; i++) {
                    if (i < 6) {
                        finalValues.add(values[i]);
                        j = j + 1;
                    } else {
                        if (j == 6) {                   // Task
                            if (values[i + 1].matches("\\d+")) {
                                full_task += values[i];
                                full_task = full_task.replace("\"", "");
                                finalValues.add(full_task);
                                j += 1;
                            } else {
                                full_task += values[i] + ",";
                            }
                        } else if (j == 20) {           // Comments
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
                //Date format
                Pattern pdate = Pattern.compile("^(([0-9][0-9][0-9][0-9]-)*([0-9][0-9]-)*([0-9][0-9]))$");  // Date format YYYY-MM-DD
                Matcher mdate = pdate.matcher(finalValues.get(12));
                boolean bdate = mdate.find();
                if (!bdate) {
                    // Change date format
                    SimpleDateFormat dcn = new SimpleDateFormat("MM/dd/yyyy");
                    Date new_date = null;
                    try {
                        new_date = dcn.parse(finalValues.get(12));
                    } catch (ParseException ex) {
                        Logger.getLogger(VSS_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    SimpleDateFormat dcn1 = new SimpleDateFormat("yyyy-MM-dd");
                    finalValues.set(12, dcn1.format(new_date));
                }
                String[] row = new String[finalValues.size()];
                finalValues.toArray(row);
                tblModel.addRow(row);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VSS_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "File does not exist!");
        } catch (IOException ex) {
            Logger.getLogger(VSS_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "File cannot be used!");
        }
    }//GEN-LAST:event_jMenuItem_SavedTemplateActionPerformed

    private void jMenuItemSaveTemplateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveTemplateActionPerformed
        // Menu to save table as template
        if (jTableAddMetrics.getEditingRow() != -1) {
            jTableAddMetrics.getCellEditor().stopCellEditing();// In case there's selected a field in the table
        }
        jLabelLoading.setText("Loading...");
        if (jPanel1.isVisible()) {
            int rows = jTableAddMetrics.getRowCount();
            if (rows > 0) {
                ArrayList data = new ArrayList<>();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Create csv with data
                        String path = "C:\\Users\\" + usersinfo.get(0) + "\\Documents\\MRT\\54v3d_73mp1473_v55.csv";
                        try (PrintWriter writer = new PrintWriter(new File(path))) {
                            StringBuilder sb = new StringBuilder();
                            // Header
                            sb.append("Customer_Unit" + ',');
                            sb.append("Region" + ',');
                            sb.append("Market" + ',');
                            sb.append("Signum" + ',');
                            sb.append("Requestor" + ',');
                            sb.append("Task_ID" + ',');
                            sb.append("Task" + ',');
                            sb.append("Network" + ',');
                            sb.append("Subnetwork" + ',');
                            sb.append("Activity_code" + ',');
                            sb.append("Technology" + ',');
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
                            // Get info from table rows
                            int i = 0;
                            for (int row = 0; row < rows; row++) {
                                // Get each row's info
                                while (i <= 20) {
                                    data.add((String) jTableAddMetrics.getValueAt(row, i));
                                    i += 1;
                                }
                                i = 0;
                            }

                            // Fill rows
                            for (int j = 1; j < data.size() + 1; j++) {
                                sb.append(data.get(j - 1));
                                if (((j % 21) == 0)) {
                                    sb.append(",");
                                    sb.append("\n");
                                } else {
                                    sb.append(",");
                                }
                            }
                            writer.write(sb.toString());
                            System.out.println("Template saved successfully");
                            JOptionPane.showMessageDialog(VSS_Time_Report.this, "Templated saved successfully");
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(VSS_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(VSS_Time_Report.this, "Info could not be saved");
                            jDLoading.dispose();
                            return;
                        }
                        jDLoading.dispose();
                    }
                }).start();
                jDLoading.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Nothing to save!");
            }
        }
    }//GEN-LAST:event_jMenuItemSaveTemplateActionPerformed

    private int Check_Day(String date) {
        // Get task worked day
        Calendar c = Calendar.getInstance();
        int dayOfWeek;
        try {
            c.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(date));

        } catch (ParseException ex) {
            Logger.getLogger(Sourcing_Time_Report.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == 1) { // If Sunday
            dayOfWeek = 7;
        } else {
            dayOfWeek = dayOfWeek - 1;
        }

        return dayOfWeek;
    }

    private void jB_ESSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_ESSActionPerformed
        // Button to export in ESS format
        int rows = jTableSeeMetrics.getRowCount();
        if (rows > 0) {
            String date = "";
            if (jDateChooser2.isVisible() && !jCheckBoxWeek.isVisible()) {
                date = String.valueOf(jDateChooser2.getDate());
            } else {
                date = "week_" + jcbWeek1.getSelectedItem().toString();
            }

            // Create csv with data
            String path = "C:\\Users\\" + usersinfo.get(0) + "\\Documents\\" + usersinfo.get(0) + "_metrics_ess_" + date + ".csv";
            System.out.println("ESS: " + metrics_for_ess);
            // Sum time with same network and activity (Productive, Training, Meeting, etc)
            ArrayList<String> sum_metrics = new ArrayList<>();
            // Iterate every saved task in metrics_for_ess
            int day;
            String activity = "", task_ = "", subnet = "";
            for (int i = 7; i < metrics_for_ess.size(); i += 22) {
                day = Check_Day(metrics_for_ess.get(i + 6));
                activity = "";
                subnet = metrics_for_ess.get(i + 3);
                task_ = metrics_for_ess.get(i);
                // Add requested Abs from ESS (4)
                if (task_.equals("Annual Leave")) {
                    task_ = "Annual Leave (2000)";
                } else if (task_.equals("Non-Operational meeting")) {
                    task_ = "Meeting (1310)";
                } else if (task_.equals("On the Job Training")) {
                    task_ = "Training (1410)";
                } else if (task_.equals("Marriage Leave")) {
                    task_ = "Marriage Leave (2135)";
                } else if (task_.equals("Paternity Leave")) {
                    task_ = "Paternity Leave (2203)";
                } else if (task_.equals("Funeral Leave")) {
                    task_ = "Funeral Leave (2152)";
                } else {    // Not ADMIN
                    task_ = "Productive hours (1000)";
                    activity = usersinfo.get(10);
                }
                if (subnet.equals("0")) {
                    subnet = "";
                }
                // Add first task
                if (sum_metrics.isEmpty()) {
                    sum_metrics.add(subnet);    // Subnetwork
                    sum_metrics.add(activity);                      // Activity
                    sum_metrics.add(task_);                         // Task
                    int z = 1;
                    while (z < 8) {
                        if (z != day) {
                            sum_metrics.add("0");
                        } else {
                            sum_metrics.add(metrics_for_ess.get(i + 7));    // Time in right day
                        }
                        z += 1;
                    }
                    //System.out.println("\nPrimera: " + task_ + " time: " + metrics_for_ess.get(i + 6) + " day: " + day);
                    //System.out.println("SUM: " + sum_metrics); // Print array contents
                    continue;
                }
                // Check if task already in array and group activities
                boolean new_task = true;
                for (int j = 0; j < sum_metrics.size(); j += 10) {
                    if (sum_metrics.get(j + 2).equals(task_) && sum_metrics.get(j).equals(subnet)) { // If same task
                        //System.out.println("\nSumando: " + sum_metrics.get(j + 2) + " | " + task_ + " | day: " + day);
                        String new_time = String.valueOf(Float.parseFloat(metrics_for_ess.get(i + 7)) + Float.parseFloat(sum_metrics.get(j + 2 + day)));
                        sum_metrics.set(j + 2 + day, new_time);
                        //System.out.println("SUM: " + sum_metrics); // Print array contents
                        new_task = false;
                        break;
                    }
                }
                // New task in file
                if (new_task) {
                    sum_metrics.add(subnet);    // Subnetwork
                    sum_metrics.add(activity);                      // Activity
                    sum_metrics.add(task_);                         // Task
                    int z = 1;
                    while (z < 8) {
                        if (z != day) {
                            sum_metrics.add("0");
                        } else {
                            sum_metrics.add(metrics_for_ess.get(i + 7));    // Time in right day
                        }
                        z += 1;
                    }
                    //System.out.println("\nNueva: " + task_);
                    //System.out.println("SUM: " + sum_metrics); // Print array contents
                }
            }

            try (PrintWriter writer = new PrintWriter(new File(path))) {
                StringBuilder sb = new StringBuilder();
                // New line
                sb.append("US format\n");
                sb.append('\n');
                // Header
                sb.append("LOC" + ',');
                sb.append("Act_Type" + ',');
                sb.append("Rec_Order" + ',');
                sb.append("Network" + ',');
                sb.append("Activity" + ',');
                sb.append("Sub_Op" + ',');
                sb.append("Abs_or_Att_type" + ',');
                sb.append("OT_Comp" + ',');
                sb.append("Total" + ',');
                sb.append("MO" + ',');
                sb.append("TU" + ',');
                sb.append("WE" + ',');
                sb.append("TH" + ',');
                sb.append("FR" + ',');
                sb.append("SA" + ',');
                sb.append("SU" + ',');
                sb.append('\n');
                // Fill rows

                // Print with ',' .replace(".", ",")        //////////////////////////////////////////
                for (int i = 0; i < sum_metrics.size(); i += 10) {
                    sb.append("HC" + ',');
                    sb.append("" + ',');
                    sb.append("" + ',');
                    // Network and Activity
                    sb.append(sum_metrics.get(i) + ',');
                    sb.append(sum_metrics.get(i + 1) + ',');
                    // Sub Op
                    sb.append("" + ',');
                    // Abs or Att type
                    sb.append(sum_metrics.get(i + 2) + ',');
                    // OT Comp
                    sb.append("" + ',');
                    // Total
                    sb.append("" + ',');
                    // Week
                    sb.append("\"" + sum_metrics.get(i + 3).replace(".", ",") + "\"" + ',');
                    sb.append("\"" + sum_metrics.get(i + 4).replace(".", ",") + "\"" + ',');
                    sb.append("\"" + sum_metrics.get(i + 5).replace(".", ",") + "\"" + ',');
                    sb.append("\"" + sum_metrics.get(i + 6).replace(".", ",") + "\"" + ',');
                    sb.append("\"" + sum_metrics.get(i + 7).replace(".", ",") + "\"" + ',');
                    sb.append("\"" + sum_metrics.get(i + 8).replace(".", ",") + "\"" + ',');
                    sb.append("\"" + sum_metrics.get(i + 9).replace(".", ",") + "\"" + ',');
                    // New line
                    sb.append("\n");
                }

                // New line to separate formats
                sb.append('\n');
                sb.append('\n');
                sb.append("MX format\n");
                sb.append('\n');
                sb.append("LOC" + ',');
                sb.append("Act_Type" + ',');
                sb.append("Rec_Order" + ',');
                sb.append("Network" + ',');
                sb.append("Activity" + ',');
                sb.append("Sub_Op" + ',');
                sb.append("Abs_or_Att_type" + ',');
                sb.append("OT_Comp" + ',');
                sb.append("Total" + ',');
                sb.append("MO" + ',');
                sb.append("TU" + ',');
                sb.append("WE" + ',');
                sb.append("TH" + ',');
                sb.append("FR" + ',');
                sb.append("SA" + ',');
                sb.append("SU" + ',');
                sb.append('\n');
                // Print with '.'
                for (int i = 0; i < sum_metrics.size(); i += 10) {
                    sb.append("HC" + ',');
                    sb.append("" + ',');
                    sb.append("" + ',');
                    // Network and Activity
                    sb.append(sum_metrics.get(i) + ',');
                    sb.append(sum_metrics.get(i + 1) + ',');
                    // Sub Op
                    sb.append("" + ',');
                    // Abs or Att type
                    sb.append(sum_metrics.get(i + 2) + ',');
                    // OT Comp
                    sb.append("" + ',');
                    // Total
                    sb.append("" + ',');
                    // Week
                    sb.append("\"" + sum_metrics.get(i + 3) + "\"" + ',');
                    sb.append("\"" + sum_metrics.get(i + 4) + "\"" + ',');
                    sb.append("\"" + sum_metrics.get(i + 5) + "\"" + ',');
                    sb.append("\"" + sum_metrics.get(i + 6) + "\"" + ',');
                    sb.append("\"" + sum_metrics.get(i + 7) + "\"" + ',');
                    sb.append("\"" + sum_metrics.get(i + 8) + "\"" + ',');
                    sb.append("\"" + sum_metrics.get(i + 9) + "\"" + ',');
                    // New line
                    sb.append("\n");
                }
                writer.write(sb.toString());
                System.out.println("File writed successfully");
                int reply = JOptionPane.showConfirmDialog(null, "Do you want to see it?", "ESS template saved", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    Desktop.getDesktop().open(new File(path));
                } else {
                    JOptionPane.showMessageDialog(this, "Template file was saved to " + path);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(VSS_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(VSS_Time_Report.this, "File could not be created");
                jDLoading.dispose();
                jFrameHistory.dispose();
                return;
            } catch (IOException ex) {
                Logger.getLogger(VSS_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Table is empty!");
        }
    }//GEN-LAST:event_jB_ESSActionPerformed

    private void jB_Export_Net_CatalogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_Export_Net_CatalogActionPerformed
        // Export networks catalog
        int rows = jTableCatalog.getRowCount();
        if (rows > 0) {
            String team = jcbTeam1.getSelectedItem().toString();
            String fileName = "networks_" + team + "_catalog";
            TableModel model = jTableCatalog.getModel();
            try {
                SaveTableCSV(fileName, model);
            } catch (IOException ex) {
                Logger.getLogger(COP_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Table is empty!");
        }
    }//GEN-LAST:event_jB_Export_Net_CatalogActionPerformed

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
            java.util.logging.Logger.getLogger(VSS_Time_Report.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VSS_Time_Report.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VSS_Time_Report.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VSS_Time_Report.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new VSS_Time_Report().setVisible(true);

                } catch (ParseException | IOException ex) {
                    Logger.getLogger(VSS_Time_Report.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBAddBulkMarket;
    private javax.swing.JButton jBAddTable;
    private javax.swing.JButton jBCancel_MarketBulk;
    private javax.swing.JButton jBCatalog;
    private javax.swing.JButton jBClearTable;
    private javax.swing.JButton jBClearTable2;
    private javax.swing.JButton jBDeleteRow;
    private javax.swing.JButton jBDeleteRow1;
    private javax.swing.JButton jBExport;
    private javax.swing.JButton jBHistory;
    private javax.swing.JButton jBMarketsBulk;
    private javax.swing.JButton jBSearch;
    private javax.swing.JButton jB_ESS;
    private javax.swing.JButton jB_ExportDates;
    private javax.swing.JButton jB_Export_Net_Catalog;
    private javax.swing.JButton jB_Save;
    private javax.swing.JButton jButtonUpdate;
    private javax.swing.JCheckBox jCheckBoxWeek;
    private javax.swing.JDialog jDLoading;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private com.toedter.calendar.JDateChooser jDateChooser_End;
    private com.toedter.calendar.JDateChooser jDateChooser_MarketBulk;
    private com.toedter.calendar.JDateChooser jDateChooser_Start;
    private javax.swing.JFrame jFrameCatalog;
    private javax.swing.JFrame jFrameHistory;
    private javax.swing.JFrame jFrameMarketsBulk;
    private javax.swing.JLabel jLActivity;
    private javax.swing.JLabel jLCU;
    private javax.swing.JLabel jLComments;
    private javax.swing.JLabel jLComments_MarketBulk;
    private javax.swing.JLabel jLDate;
    private javax.swing.JLabel jLDate1;
    private javax.swing.JLabel jLDate_MarketBulk;
    private javax.swing.JLabel jLFTR;
    private javax.swing.JLabel jLFTR_MarketBulk;
    private javax.swing.JLabel jLFailFTR;
    private javax.swing.JLabel jLFailFTR_MarketBulk;
    private javax.swing.JLabel jLFaildeOnTime;
    private javax.swing.JLabel jLFaildeOnTime_MarketBulk;
    private javax.swing.JLabel jLHoursForLoggedTime;
    private javax.swing.JLabel jLHoursForLoggedTime_MarketBulk;
    private javax.swing.JLabel jLNetwork;
    private javax.swing.JLabel jLOnTime;
    private javax.swing.JLabel jLOnTime_MarketBulk;
    private javax.swing.JLabel jLRegionBulk;
    private javax.swing.JLabel jLRequestor;
    private javax.swing.JLabel jLRequestor_MarketBulk;
    private javax.swing.JLabel jLRequests;
    private javax.swing.JLabel jLSAP_Billing;
    private javax.swing.JLabel jLSAP_Billing1;
    private javax.swing.JLabel jLTask;
    private javax.swing.JLabel jLTask1;
    private javax.swing.JLabel jLTaskID;
    private javax.swing.JLabel jLTaskID_MarketBulk;
    private javax.swing.JLabel jLTask_MarketBulk;
    private javax.swing.JLabel jLTime;
    private javax.swing.JLabel jLTimeMarket_Bulk;
    private javax.swing.JLabel jLWeek;
    private javax.swing.JLabel jLWeek1;
    private javax.swing.JLabel jLWeek_MarketBulk;
    private javax.swing.JLabel jL_EndDate;
    private javax.swing.JLabel jL_StartDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelDagaz;
    private javax.swing.JLabel jLabelDescAbout;
    private javax.swing.JLabel jLabelDescDagaz;
    private javax.swing.JLabel jLabelDescNaudiz;
    private javax.swing.JLabel jLabelDescRaido;
    private javax.swing.JLabel jLabelHoursDaysB;
    private javax.swing.JLabel jLabelHoursDaysB1;
    private javax.swing.JLabel jLabelHoursDaysH;
    private javax.swing.JLabel jLabelHoursDaysH1;
    private javax.swing.JLabel jLabelLoading;
    private javax.swing.JLabel jLabelLoading1;
    private javax.swing.JLabel jLabelLogo;
    private javax.swing.JLabel jLabelMarket;
    private javax.swing.JLabel jLabelMarketsBulk;
    private javax.swing.JLabel jLabelNaudiz;
    private javax.swing.JLabel jLabelNum_req_Bulk;
    private javax.swing.JLabel jLabelRaido;
    private javax.swing.JLabel jLabelRegion;
    private javax.swing.JLabel jLabelSubnetwork;
    private javax.swing.JLabel jLabelSupport;
    private javax.swing.JLabel jLabelTeam;
    private javax.swing.JLabel jLabelTeam1;
    private javax.swing.JLabel jLabelTech;
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
    private javax.swing.JMenuItem jMenuItemSaveTemplate;
    private javax.swing.JMenuItem jMenuItem_SavedTemplate;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanelLoading;
    private javax.swing.JPanel jPanelMarketBulk;
    private javax.swing.JPanel jPanelMarketSelection;
    private javax.swing.JRadioButton jRBulk;
    private javax.swing.JRadioButton jRSingle;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSpinner jSpinnerBulk;
    private javax.swing.JTable jTableAddMetrics;
    private javax.swing.JTable jTableCatalog;
    private javax.swing.JTable jTableSeeMetrics;
    private javax.swing.JTextField jTextFieldActivity;
    private javax.swing.JTextArea jTextFieldComments;
    private javax.swing.JTextArea jTextFieldComments_MarketBulk;
    private javax.swing.JTextField jTextFieldNetwork;
    private javax.swing.JTextField jTextFieldNumReqMarketBulk;
    private javax.swing.JTextField jTextFieldRequestor;
    private javax.swing.JTextField jTextFieldRequestor_MarketBulk;
    private javax.swing.JTextField jTextFieldRequests;
    private javax.swing.JTextField jTextFieldTask_ID;
    private javax.swing.JTextField jTextFieldTask_ID_MarketBulk;
    private javax.swing.JTextField jTextFieldTime;
    private javax.swing.JTextField jTextFieldTime_MarketBulk;
    private javax.swing.JTextField jTextFieldWeek;
    private javax.swing.JTextField jTextFieldWeek_MarketBulk;
    private javax.swing.JComboBox<String> jcbCU;
    private javax.swing.JComboBox<String> jcbFTR;
    private javax.swing.JComboBox<String> jcbFTR_MarketBulk;
    private javax.swing.JComboBox<String> jcbFailedFTR;
    private javax.swing.JComboBox<String> jcbFailedFTR_MarketBulk;
    private javax.swing.JComboBox<String> jcbFailedOnTime;
    private javax.swing.JComboBox<String> jcbFailedOnTime_MarketBulk;
    private javax.swing.JComboBox<String> jcbMarket;
    private javax.swing.JComboBox<String> jcbOnTime;
    private javax.swing.JComboBox<String> jcbOnTime_MarketBulk;
    private javax.swing.JComboBox<String> jcbRegion;
    private javax.swing.JComboBox<String> jcbRegionBulk;
    private javax.swing.JComboBox<String> jcbSAP;
    private javax.swing.JComboBox<String> jcbSAP1;
    private javax.swing.JComboBox<String> jcbSubnet;
    private javax.swing.JComboBox<String> jcbTask;
    private javax.swing.JComboBox<String> jcbTask1;
    private javax.swing.JComboBox<String> jcbTask_MarketBulk;
    private javax.swing.JComboBox<String> jcbTeam1;
    private javax.swing.JComboBox<String> jcbTech;
    private javax.swing.JComboBox<String> jcbWeek1;
    private javax.swing.JComboBox<String> jcb_Team_history;
    // End of variables declaration//GEN-END:variables
}
