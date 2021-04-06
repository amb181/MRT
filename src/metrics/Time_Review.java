/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metrics;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.plaf.MenuBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author efgaorm
 */
public class Time_Review extends javax.swing.JFrame {

    private Map hiddenColumns;
    Connection connection;
    Statement statement;
    int week = 0;
    String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    String[] monthSQL = {"2020-01", "2020-02", "2020-03", "2020-04", "2020-05", "2020-06", "2020-07", "2020-08", "2020-09", "2020-10", "2020-11", "2020-12"};
    int[] days = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    String tid;
    List<String> tasks = new ArrayList<String>();
    List<String> teams = new ArrayList<String>();
    List<String> organizations = new ArrayList<String>();
    List<String> cus = new ArrayList<String>();
    List<String> user_teams = new ArrayList<String>();
    List<String> user_names = new ArrayList<String>();

    /**
     * Creates new form Time_Review
     */
    public Time_Review() throws ParseException {
        long startTime = System.currentTimeMillis();
        initComponents();
        setLookAndFeel();
        this.setSize(jPView.getPreferredSize());
        this.setIconImage(new ImageIcon(getClass().getResource("/images/MRT_logo.png")).getImage());
        this.setTitle("MRT - Audit & Report - Sourcing - Review Metrics");
        jDLoading.setSize(600, 150);
        jDLoading.setTitle("MRT - Connecting to server");
        jDLoading.setIconImage(new ImageIcon(getClass().getResource("/images/MRT_logo.png")).getImage());
        jDLoading.setLocationRelativeTo(null);
        setResizable(false);
        hiddenColumns = new HashMap();
        Date date = new Date();
        SimpleDateFormat dcn = new SimpleDateFormat("yyyy-MM-dd");
        String date_change = dcn.format(date);
        int i;
        Date date_ = dcn.parse(date_change);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date_);
        week = cal.get(Calendar.WEEK_OF_YEAR);
        for (i = 1; i <= week; i++) {
            jCBFrom.addItem(String.valueOf(i));
        }
        jCBFrom.setSelectedIndex(week - 1);
        jTableShowMetrics.setEnabled(false);
        jTUsersList.setEnabled(false);
        jTTasksList.setEnabled(false);
        jPEdit.setVisible(false);
        jPView.setVisible(true);
        jPSearch.setVisible(false);
        jPEditTask.setVisible(false);
        jCBSupportedCU.setEnabled(false);
        jPSearchTask.setVisible(false);
        jPNetworks.setVisible(false);
        jPNetSearch.setVisible(false);
        AutoCompleteDecorator.decorate(jCBTaskSearch);
        AutoCompleteDecorator.decorate(jCBNetSearch);
        AutoCompleteDecorator.decorate(jCBOrganization);
        AutoCompleteDecorator.decorate(jCBTeam);
        AutoCompleteDecorator.decorate(jCBCustomerUnit);
        AutoCompleteDecorator.decorate(jCBSupportedCU);
        AutoCompleteDecorator.decorate(jCBTaskTeam);
        AutoCompleteDecorator.decorate(jCBUser);
        AutoCompleteDecorator.decorate(jCBMetricTeam);
        jLLoading.setText("Initializing admin's actions...");
        jDLoading.setVisible(true);
        GetAllUsers();
        GetAllTasks();
        GetNetworksSearch();
        jDLoading.dispose();
        long endTime0 = System.currentTimeMillis();
        System.out.println("Time from init to before DB consultation: " + (endTime0 - startTime));
        JTableHeader header = jTableShowMetrics.getTableHeader();
        JTableHeader header1 = jTUsersList.getTableHeader();
        JTableHeader header2 = jTTasksList.getTableHeader();
        header.setBackground(new Color(0, 130, 240));
        header.setForeground(Color.white);
        header1.setBackground(new Color(0, 130, 240));
        header1.setForeground(Color.white);
        header2.setBackground(new Color(0, 130, 240));
        header2.setForeground(Color.white);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BGFrom = new javax.swing.ButtonGroup();
        jDLoading = new javax.swing.JDialog();
        jPanelLoading = new javax.swing.JPanel();
        jLLoading = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jPView = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableShowMetrics = new javax.swing.JTable(){
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
        jRBSubnetwork = new javax.swing.JRadioButton();
        jRBTeam = new javax.swing.JRadioButton();
        jRBSignum = new javax.swing.JRadioButton();
        jRBTaskID = new javax.swing.JRadioButton();
        jRBTask = new javax.swing.JRadioButton();
        jRBNetwork = new javax.swing.JRadioButton();
        jRBSAPBilling = new javax.swing.JRadioButton();
        jBShowPreview = new javax.swing.JButton();
        jRBName = new javax.swing.JRadioButton();
        jBGenerateCSV = new javax.swing.JButton();
        jLFileName = new javax.swing.JLabel();
        jTFFileName = new javax.swing.JTextField();
        jCBUser = new javax.swing.JComboBox<>();
        jCBMetricTeam = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jRBWeek = new javax.swing.JRadioButton();
        jRBMonth = new javax.swing.JRadioButton();
        jRBQuarter = new javax.swing.JRadioButton();
        jLFrom = new javax.swing.JLabel();
        jRBTo = new javax.swing.JRadioButton();
        jCBFrom = new javax.swing.JComboBox<>();
        jCBTo = new javax.swing.JComboBox<>();
        jBCSVMetrics = new javax.swing.JButton();
        jPEdit = new javax.swing.JPanel();
        jPUser = new javax.swing.JPanel();
        jLSignum = new javax.swing.JLabel();
        jTFSignum = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jTFName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTFLastName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jCBOrganization = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jCBTeam = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jCBCustomerUnit = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jTFLineManager = new javax.swing.JTextField();
        jBSaveNewUser = new javax.swing.JButton();
        jLAccess = new javax.swing.JLabel();
        jCBAccess = new javax.swing.JComboBox<>();
        jLOtherCU = new javax.swing.JLabel();
        jCBOtherCU = new javax.swing.JComboBox<>();
        jLCUSupported = new javax.swing.JLabel();
        jCBSupportedCU = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jCBAction = new javax.swing.JComboBox<>();
        jPSearch = new javax.swing.JPanel();
        jBDelete = new javax.swing.JButton();
        jBClear = new javax.swing.JButton();
        jBSearch = new javax.swing.JButton();
        jTFSignumEdit = new javax.swing.JTextField();
        jLSignumEdit = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTUsersList = new javax.swing.JTable(){
            @Override

            public Component prepareRenderer (TableCellRenderer renderer, int rowIndex, int columnIndex){
                Component component = super.prepareRenderer(renderer, rowIndex, columnIndex);

                if(rowIndex%2 == 0){
                    component.setBackground(Color.WHITE);
                    component.setForeground(Color.BLACK);
                }else{
                    component.setBackground(new Color(191,226,255));
                    component.setForeground(Color.BLACK);
                }

                return component;
            }
        };
        jBExportUserCSV = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jPEditTask = new javax.swing.JPanel();
        jLChoose = new javax.swing.JLabel();
        jCBTaskEdit = new javax.swing.JComboBox<>();
        jPSearchTask = new javax.swing.JPanel();
        jBDeleteTask = new javax.swing.JButton();
        jBClearTask = new javax.swing.JButton();
        jLTaskSearch = new javax.swing.JLabel();
        jBSearchTask = new javax.swing.JButton();
        jCBTaskSearch = new javax.swing.JComboBox<>();
        jPTasks = new javax.swing.JPanel();
        jLTaskType = new javax.swing.JLabel();
        jCBTaskType = new javax.swing.JComboBox<>();
        jLTask = new javax.swing.JLabel();
        jTFTaskName = new javax.swing.JTextField();
        jBSaveTask = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jCBTaskTeam = new javax.swing.JComboBox<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTTasksList = new javax.swing.JTable(){
            @Override

            public Component prepareRenderer (TableCellRenderer renderer, int rowIndex, int columnIndex){
                Component component = super.prepareRenderer(renderer, rowIndex, columnIndex);

                if(rowIndex%2 == 0){
                    component.setBackground(Color.WHITE);
                    component.setForeground(Color.BLACK);
                }else{
                    component.setBackground(new Color(191,226,255));
                    component.setForeground(Color.BLACK);
                }

                return component;
            }
        };
        jLabel19 = new javax.swing.JLabel();
        jBExportTaskCSV = new javax.swing.JButton();
        jPNetworks = new javax.swing.JPanel();
        jLNetAction = new javax.swing.JLabel();
        jCBNetAction = new javax.swing.JComboBox<>();
        jPNetSearch = new javax.swing.JPanel();
        jLNetworkSearch = new javax.swing.JLabel();
        jCBNetSearch = new javax.swing.JComboBox<>();
        jBNetworkSearch = new javax.swing.JButton();
        jBNetSearch = new javax.swing.JButton();
        jBNetDelete = new javax.swing.JButton();
        jPNetEdit = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jTFPD = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTFNetwork = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTFActivityCode = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jTFResponsible = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jTFSubnetwork = new javax.swing.JTextField();
        jBSaveNet = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jTFNetTeam = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jTFNetRegion = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jTFNetMarket = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTFNetCustomer = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuView = new javax.swing.JMenu();
        jMReview = new javax.swing.JMenuItem();
        jMRecordMetrics = new javax.swing.JMenuItem();
        jMenuEdit = new javax.swing.JMenu();
        jMEditUsers = new javax.swing.JMenuItem();
        jMEditTask = new javax.swing.JMenuItem();
        jMEditNetworks = new javax.swing.JMenuItem();

        jPanelLoading.setBackground(new java.awt.Color(255, 255, 255));

        jLLoading.setText("Saving into the database...");

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Loading.gif"))); // NOI18N

        javax.swing.GroupLayout jPanelLoadingLayout = new javax.swing.GroupLayout(jPanelLoading);
        jPanelLoading.setLayout(jPanelLoadingLayout);
        jPanelLoadingLayout.setHorizontalGroup(
            jPanelLoadingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLoadingLayout.createSequentialGroup()
                .addGap(164, 164, 164)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLoadingLayout.createSequentialGroup()
                .addContainerGap(228, Short.MAX_VALUE)
                .addComponent(jLLoading)
                .addGap(207, 207, 207))
        );
        jPanelLoadingLayout.setVerticalGroup(
            jPanelLoadingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLoadingLayout.createSequentialGroup()
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLLoading, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(298, 298, 298))
        );

        javax.swing.GroupLayout jDLoadingLayout = new javax.swing.GroupLayout(jDLoading.getContentPane());
        jDLoading.getContentPane().setLayout(jDLoadingLayout);
        jDLoadingLayout.setHorizontalGroup(
            jDLoadingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelLoading, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jDLoadingLayout.setVerticalGroup(
            jDLoadingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDLoadingLayout.createSequentialGroup()
                .addComponent(jPanelLoading, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(1488, 720));

        jPView.setBackground(new java.awt.Color(255, 255, 255));
        jPView.setPreferredSize(new java.awt.Dimension(1493, 718));

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));

        jTableShowMetrics.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Signum", "Name", "Organization", "Task ID", "Task", "Network", "Subnetwork", "SAP Billing", "Work date", "Logged Time"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableShowMetrics.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTableShowMetrics);

        jRBSubnetwork.setBackground(new java.awt.Color(255, 255, 255));
        jRBSubnetwork.setSelected(true);
        jRBSubnetwork.setText("Subnetwork");

        jRBTeam.setBackground(new java.awt.Color(255, 255, 255));
        jRBTeam.setSelected(true);
        jRBTeam.setText("Team");
        jRBTeam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBTeamActionPerformed(evt);
            }
        });

        jRBSignum.setBackground(new java.awt.Color(255, 255, 255));
        jRBSignum.setSelected(true);
        jRBSignum.setText("Signum");

        jRBTaskID.setBackground(new java.awt.Color(255, 255, 255));
        jRBTaskID.setSelected(true);
        jRBTaskID.setText("Task ID");
        jRBTaskID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBTaskIDActionPerformed(evt);
            }
        });

        jRBTask.setBackground(new java.awt.Color(255, 255, 255));
        jRBTask.setSelected(true);
        jRBTask.setText("Task");

        jRBNetwork.setBackground(new java.awt.Color(255, 255, 255));
        jRBNetwork.setSelected(true);
        jRBNetwork.setText("Network");

        jRBSAPBilling.setBackground(new java.awt.Color(255, 255, 255));
        jRBSAPBilling.setSelected(true);
        jRBSAPBilling.setText("SAP Billing");

        jBShowPreview.setBackground(new java.awt.Color(0, 74, 173));
        jBShowPreview.setForeground(new java.awt.Color(255, 255, 255));
        jBShowPreview.setText("Show logged time");
        jBShowPreview.setActionCommand("Show Preview");
        jBShowPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBShowPreviewActionPerformed(evt);
            }
        });

        jRBName.setBackground(new java.awt.Color(255, 255, 255));
        jRBName.setSelected(true);
        jRBName.setText("Name");
        jRBName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBNameActionPerformed(evt);
            }
        });

        jBGenerateCSV.setBackground(new java.awt.Color(0, 130, 240));
        jBGenerateCSV.setForeground(new java.awt.Color(255, 255, 255));
        jBGenerateCSV.setText("Export logged time");
        jBGenerateCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBGenerateCSVActionPerformed(evt);
            }
        });

        jLFileName.setText("File Name:");

        jCBUser.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jCBMetricTeam.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 130, 240), 1, true));

        jRBWeek.setBackground(new java.awt.Color(255, 255, 255));
        BGFrom.add(jRBWeek);
        jRBWeek.setSelected(true);
        jRBWeek.setText("Week");
        jRBWeek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBWeekActionPerformed(evt);
            }
        });

        jRBMonth.setBackground(new java.awt.Color(255, 255, 255));
        BGFrom.add(jRBMonth);
        jRBMonth.setText("Month");
        jRBMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBMonthActionPerformed(evt);
            }
        });

        jRBQuarter.setBackground(new java.awt.Color(255, 255, 255));
        BGFrom.add(jRBQuarter);
        jRBQuarter.setText("Quarter");
        jRBQuarter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBQuarterActionPerformed(evt);
            }
        });

        jLFrom.setText("From:");

        jRBTo.setBackground(new java.awt.Color(255, 255, 255));
        jRBTo.setText("To:");
        jRBTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBToActionPerformed(evt);
            }
        });

        jCBFrom.setToolTipText("");
        jCBFrom.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 255, 255)));
        jCBFrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBFromActionPerformed(evt);
            }
        });

        jCBTo.setToolTipText("");
        jCBTo.setEnabled(false);
        jCBTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBToActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRBMonth)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jRBWeek)
                        .addGap(49, 49, 49)
                        .addComponent(jRBQuarter)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRBTo)
                    .addComponent(jLFrom))
                .addGap(56, 56, 56)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCBTo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCBFrom, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(130, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jRBWeek)
                            .addComponent(jRBQuarter)
                            .addComponent(jLFrom))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jRBMonth)
                            .addComponent(jRBTo)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jCBFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jCBTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 6, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jBCSVMetrics.setBackground(new java.awt.Color(0, 130, 240));
        jBCSVMetrics.setForeground(new java.awt.Color(255, 255, 255));
        jBCSVMetrics.setText("Export metrics");
        jBCSVMetrics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBCSVMetricsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPViewLayout = new javax.swing.GroupLayout(jPView);
        jPView.setLayout(jPViewLayout);
        jPViewLayout.setHorizontalGroup(
            jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPViewLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPViewLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1438, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(40, Short.MAX_VALUE))
                    .addGroup(jPViewLayout.createSequentialGroup()
                        .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPViewLayout.createSequentialGroup()
                                .addComponent(jRBNetwork)
                                .addGap(18, 18, 18)
                                .addComponent(jRBSubnetwork)
                                .addGap(18, 18, 18)
                                .addComponent(jRBTaskID)
                                .addGap(18, 18, 18)
                                .addComponent(jRBTask)
                                .addGap(18, 18, 18)
                                .addComponent(jRBSAPBilling)
                                .addGap(126, 126, 126))
                            .addGroup(jPViewLayout.createSequentialGroup()
                                .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jRBSignum)
                                    .addGroup(jPViewLayout.createSequentialGroup()
                                        .addComponent(jRBName)
                                        .addGap(33, 33, 33)
                                        .addComponent(jCBUser, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jRBTeam)
                                .addGap(30, 30, 30)
                                .addComponent(jCBMetricTeam, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(131, 131, 131)))
                        .addGap(8, 8, 8)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jBShowPreview)
                            .addGroup(jPViewLayout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(jBCSVMetrics)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPViewLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLFileName)
                .addGap(18, 18, 18)
                .addComponent(jTFFileName, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBGenerateCSV)
                .addGap(115, 115, 115))
        );
        jPViewLayout.setVerticalGroup(
            jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPViewLayout.createSequentialGroup()
                .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPViewLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPViewLayout.createSequentialGroup()
                                .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPViewLayout.createSequentialGroup()
                                        .addComponent(jRBSignum)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jRBName)
                                            .addComponent(jCBUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jRBTeam)
                                        .addComponent(jCBMetricTeam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jRBNetwork)
                                    .addComponent(jRBSubnetwork)
                                    .addComponent(jRBTaskID)
                                    .addComponent(jRBTask)
                                    .addComponent(jRBSAPBilling)))
                            .addGroup(jPViewLayout.createSequentialGroup()
                                .addComponent(jBShowPreview)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jBCSVMetrics))))
                    .addGroup(jPViewLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 461, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBGenerateCSV)
                    .addComponent(jTFFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLFileName))
                .addContainerGap(64, Short.MAX_VALUE))
        );

        jPEdit.setBackground(new java.awt.Color(255, 255, 255));
        jPEdit.setPreferredSize(new java.awt.Dimension(1488, 710));

        jPUser.setBackground(new java.awt.Color(255, 255, 255));
        jPUser.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 130, 240)));

        jLSignum.setText("Signum:");

        jLabel1.setText("Name:");

        jLabel2.setText("Last Name:");

        jLabel3.setText("Organization:");

        jLabel4.setText("Team:");

        jLabel5.setText("Customer Unit:");

        jCBCustomerUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBCustomerUnitActionPerformed(evt);
            }
        });

        jLabel6.setText("Line Manager Signum:");

        jBSaveNewUser.setBackground(new java.awt.Color(0, 130, 240));
        jBSaveNewUser.setForeground(new java.awt.Color(255, 255, 255));
        jBSaveNewUser.setText("Save");
        jBSaveNewUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSaveNewUserActionPerformed(evt);
            }
        });

        jLAccess.setText("Access:");

        jCBAccess.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Regular", "Admin" }));

        jLOtherCU.setText("Supporting in another CU:");

        jCBOtherCU.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "No", "Yes" }));
        jCBOtherCU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBOtherCUActionPerformed(evt);
            }
        });

        jLCUSupported.setText("CU Supported:");

        jCBSupportedCU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBSupportedCUActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPUserLayout = new javax.swing.GroupLayout(jPUser);
        jPUser.setLayout(jPUserLayout);
        jPUserLayout.setHorizontalGroup(
            jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPUserLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPUserLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jBSaveNewUser))
                    .addGroup(jPUserLayout.createSequentialGroup()
                        .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPUserLayout.createSequentialGroup()
                                .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLSignum)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4))
                                .addGap(18, 18, 18)
                                .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPUserLayout.createSequentialGroup()
                                        .addComponent(jTFName, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                        .addGap(56, 56, 56)
                                        .addComponent(jLabel2)
                                        .addGap(18, 18, 18)
                                        .addComponent(jTFLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jCBOrganization, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jTFSignum, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPUserLayout.createSequentialGroup()
                                        .addComponent(jCBTeam, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(57, 57, 57)
                                        .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel5)
                                            .addComponent(jLCUSupported))
                                        .addGap(18, 18, 18)
                                        .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jCBCustomerUnit, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jCBSupportedCU, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                            .addGroup(jPUserLayout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTFLineManager, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPUserLayout.createSequentialGroup()
                                .addComponent(jLAccess)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jCBAccess, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPUserLayout.createSequentialGroup()
                                .addComponent(jLOtherCU)
                                .addGap(18, 18, 18)
                                .addComponent(jCBOtherCU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPUserLayout.setVerticalGroup(
            jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPUserLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTFLineManager, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPUserLayout.createSequentialGroup()
                        .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLSignum)
                            .addComponent(jTFSignum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jTFName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(jTFLastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jCBOrganization, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jCBTeam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(jCBCustomerUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel6)))
                .addGap(18, 18, 18)
                .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLAccess)
                    .addComponent(jCBAccess, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLOtherCU)
                    .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jCBOtherCU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLCUSupported)
                        .addComponent(jCBSupportedCU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addComponent(jBSaveNewUser)
                .addContainerGap())
        );

        jLabel7.setText("Choose action:");

        jCBAction.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Add new user", "Edit user" }));
        jCBAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBActionActionPerformed(evt);
            }
        });

        jPSearch.setBackground(new java.awt.Color(255, 255, 255));

        jBDelete.setBackground(new java.awt.Color(199, 64, 56));
        jBDelete.setForeground(new java.awt.Color(255, 255, 255));
        jBDelete.setText("Delete");
        jBDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBDeleteActionPerformed(evt);
            }
        });

        jBClear.setBackground(new java.awt.Color(255, 255, 255));
        jBClear.setText("Clear");
        jBClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBClearActionPerformed(evt);
            }
        });

        jBSearch.setBackground(new java.awt.Color(0, 130, 240));
        jBSearch.setForeground(new java.awt.Color(255, 255, 255));
        jBSearch.setText("Search");
        jBSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSearchActionPerformed(evt);
            }
        });

        jTFSignumEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTFSignumEditActionPerformed(evt);
            }
        });

        jLSignumEdit.setText("Signum:");

        javax.swing.GroupLayout jPSearchLayout = new javax.swing.GroupLayout(jPSearch);
        jPSearch.setLayout(jPSearchLayout);
        jPSearchLayout.setHorizontalGroup(
            jPSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLSignumEdit)
                .addGap(18, 18, 18)
                .addComponent(jTFSignumEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jBSearch)
                .addGap(18, 18, 18)
                .addComponent(jBClear)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 141, Short.MAX_VALUE)
                .addComponent(jBDelete)
                .addContainerGap())
        );
        jPSearchLayout.setVerticalGroup(
            jPSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPSearchLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLSignumEdit)
                    .addComponent(jTFSignumEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBSearch)
                    .addComponent(jBClear)
                    .addComponent(jBDelete))
                .addContainerGap())
        );

        jTUsersList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTUsersList);

        jBExportUserCSV.setBackground(new java.awt.Color(0, 74, 173));
        jBExportUserCSV.setForeground(new java.awt.Color(255, 255, 255));
        jBExportUserCSV.setText("Export to CSV file");
        jBExportUserCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBExportUserCSVActionPerformed(evt);
            }
        });

        jLabel18.setText("Users in the database");

        javax.swing.GroupLayout jPEditLayout = new javax.swing.GroupLayout(jPEdit);
        jPEdit.setLayout(jPEditLayout);
        jPEditLayout.setHorizontalGroup(
            jPEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPEditLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPEditLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(jCBAction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPEditLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jBExportUserCSV))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPEditLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 739, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPEditLayout.setVerticalGroup(
            jPEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPEditLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jCBAction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(jBExportUserCSV))
                .addGap(2, 2, 2)
                .addGroup(jPEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPEditLayout.createSequentialGroup()
                        .addComponent(jPSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 560, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(103, Short.MAX_VALUE))
        );

        jPEditTask.setBackground(new java.awt.Color(255, 255, 255));
        jPEditTask.setPreferredSize(new java.awt.Dimension(1200, 690));

        jLChoose.setText("Choose action:");

        jCBTaskEdit.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Add a new task", "Edit existing task" }));
        jCBTaskEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBTaskEditActionPerformed(evt);
            }
        });

        jPSearchTask.setBackground(new java.awt.Color(255, 255, 255));

        jBDeleteTask.setBackground(new java.awt.Color(199, 64, 56));
        jBDeleteTask.setForeground(new java.awt.Color(255, 255, 255));
        jBDeleteTask.setText("Delete");
        jBDeleteTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBDeleteTaskActionPerformed(evt);
            }
        });

        jBClearTask.setBackground(new java.awt.Color(255, 255, 255));
        jBClearTask.setText("Clear");
        jBClearTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBClearTaskActionPerformed(evt);
            }
        });

        jLTaskSearch.setText("Task:");

        jBSearchTask.setBackground(new java.awt.Color(0, 130, 240));
        jBSearchTask.setForeground(new java.awt.Color(255, 255, 255));
        jBSearchTask.setText("Search");
        jBSearchTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSearchTaskActionPerformed(evt);
            }
        });

        jCBTaskSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBTaskSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPSearchTaskLayout = new javax.swing.GroupLayout(jPSearchTask);
        jPSearchTask.setLayout(jPSearchTaskLayout);
        jPSearchTaskLayout.setHorizontalGroup(
            jPSearchTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPSearchTaskLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLTaskSearch)
                .addGap(18, 18, 18)
                .addComponent(jCBTaskSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBSearchTask)
                .addGap(18, 18, 18)
                .addComponent(jBClearTask)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 72, Short.MAX_VALUE)
                .addComponent(jBDeleteTask)
                .addContainerGap())
        );
        jPSearchTaskLayout.setVerticalGroup(
            jPSearchTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPSearchTaskLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPSearchTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBDeleteTask)
                    .addComponent(jBClearTask)
                    .addComponent(jLTaskSearch)
                    .addComponent(jBSearchTask)
                    .addComponent(jCBTaskSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPTasks.setBackground(new java.awt.Color(255, 255, 255));
        jPTasks.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 130, 240)));

        jLTaskType.setText("Task Type:");

        jCBTaskType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ADMIN", "SE-PA", "ST-DEV", "ST-PA" }));
        jCBTaskType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBTaskTypeActionPerformed(evt);
            }
        });

        jLTask.setText("Task:");

        jBSaveTask.setBackground(new java.awt.Color(0, 130, 240));
        jBSaveTask.setForeground(new java.awt.Color(255, 255, 255));
        jBSaveTask.setText("Save");
        jBSaveTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSaveTaskActionPerformed(evt);
            }
        });

        jLabel14.setText("Team:");

        javax.swing.GroupLayout jPTasksLayout = new javax.swing.GroupLayout(jPTasks);
        jPTasks.setLayout(jPTasksLayout);
        jPTasksLayout.setHorizontalGroup(
            jPTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPTasksLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPTasksLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jBSaveTask))
                    .addGroup(jPTasksLayout.createSequentialGroup()
                        .addGroup(jPTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPTasksLayout.createSequentialGroup()
                                .addComponent(jLTaskType)
                                .addGap(18, 18, 18)
                                .addComponent(jCBTaskType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPTasksLayout.createSequentialGroup()
                                .addComponent(jLTask)
                                .addGap(18, 18, 18)
                                .addComponent(jTFTaskName, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPTasksLayout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addGap(18, 18, 18)
                                .addComponent(jCBTaskTeam, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 15, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPTasksLayout.setVerticalGroup(
            jPTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPTasksLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLTaskType)
                    .addComponent(jCBTaskType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLTask)
                    .addComponent(jTFTaskName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jCBTaskTeam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jBSaveTask)
                .addContainerGap())
        );

        jTTasksList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(jTTasksList);

        jLabel19.setText("Tasks in the database");

        jBExportTaskCSV.setBackground(new java.awt.Color(0, 74, 173));
        jBExportTaskCSV.setForeground(new java.awt.Color(255, 255, 255));
        jBExportTaskCSV.setText("Export to CSV file");
        jBExportTaskCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBExportTaskCSVActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPEditTaskLayout = new javax.swing.GroupLayout(jPEditTask);
        jPEditTask.setLayout(jPEditTaskLayout);
        jPEditTaskLayout.setHorizontalGroup(
            jPEditTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPEditTaskLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPEditTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPSearchTask, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPTasks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPEditTaskLayout.createSequentialGroup()
                        .addComponent(jLChoose)
                        .addGap(18, 18, 18)
                        .addComponent(jCBTaskEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPEditTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPEditTaskLayout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jBExportTaskCSV))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 519, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPEditTaskLayout.setVerticalGroup(
            jPEditTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPEditTaskLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPEditTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLChoose)
                    .addComponent(jCBTaskEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(jBExportTaskCSV))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPEditTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPEditTaskLayout.createSequentialGroup()
                        .addComponent(jPSearchTask, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPTasks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(116, Short.MAX_VALUE))
        );

        jPNetworks.setBackground(new java.awt.Color(255, 255, 255));
        jPNetworks.setMaximumSize(new java.awt.Dimension(680, 510));
        jPNetworks.setPreferredSize(new java.awt.Dimension(1460, 690));

        jLNetAction.setText("Choose action:");

        jCBNetAction.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Add a new network", "Edit an existing network" }));
        jCBNetAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBNetActionActionPerformed(evt);
            }
        });

        jPNetSearch.setBackground(new java.awt.Color(255, 255, 255));

        jLNetworkSearch.setText("Network:");

        jCBNetSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBNetSearchActionPerformed(evt);
            }
        });

        jBNetworkSearch.setText("Search");
        jBNetworkSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBNetworkSearchActionPerformed(evt);
            }
        });

        jBNetSearch.setText("Clear");
        jBNetSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBNetSearchActionPerformed(evt);
            }
        });

        jBNetDelete.setText("Delete");
        jBNetDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBNetDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPNetSearchLayout = new javax.swing.GroupLayout(jPNetSearch);
        jPNetSearch.setLayout(jPNetSearchLayout);
        jPNetSearchLayout.setHorizontalGroup(
            jPNetSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPNetSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLNetworkSearch)
                .addGap(18, 18, 18)
                .addComponent(jCBNetSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jBNetworkSearch)
                .addGap(18, 18, 18)
                .addComponent(jBNetSearch)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 120, Short.MAX_VALUE)
                .addComponent(jBNetDelete)
                .addContainerGap())
        );
        jPNetSearchLayout.setVerticalGroup(
            jPNetSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPNetSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPNetSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLNetworkSearch)
                    .addComponent(jCBNetSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBNetworkSearch)
                    .addComponent(jBNetSearch)
                    .addComponent(jBNetDelete))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPNetEdit.setBackground(new java.awt.Color(255, 255, 255));
        jPNetEdit.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 130, 240)));

        jLabel8.setText("PD:");

        jLabel10.setText("Network: ");

        jLabel11.setText("Activity code:");

        jLabel12.setText("Responsible:");

        jLabel13.setText("Subnetwork:");

        jBSaveNet.setText("Save");
        jBSaveNet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSaveNetActionPerformed(evt);
            }
        });

        jLabel9.setText("Team:");

        jTFNetTeam.setToolTipText("");

        jLabel15.setText("Region:");

        jLabel16.setText("Market:");

        jLabel17.setText("Customer:");

        javax.swing.GroupLayout jPNetEditLayout = new javax.swing.GroupLayout(jPNetEdit);
        jPNetEdit.setLayout(jPNetEditLayout);
        jPNetEditLayout.setHorizontalGroup(
            jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPNetEditLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPNetEditLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jBSaveNet))
                    .addGroup(jPNetEditLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(jTFPD, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel10)
                        .addGap(18, 18, 18)
                        .addComponent(jTFNetwork, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPNetEditLayout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(18, 18, 18)
                        .addComponent(jTFResponsible, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPNetEditLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(18, 18, 18)
                        .addComponent(jTFActivityCode, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPNetEditLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addGap(18, 18, 18)
                        .addComponent(jTFSubnetwork, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPNetEditLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(18, 18, 18)
                        .addComponent(jTFNetTeam, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPNetEditLayout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(18, 18, 18)
                        .addComponent(jTFNetRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel16)
                        .addGap(18, 18, 18)
                        .addComponent(jTFNetMarket, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel17)
                        .addGap(18, 18, 18)
                        .addComponent(jTFNetCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPNetEditLayout.setVerticalGroup(
            jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPNetEditLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTFPD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jTFNetwork, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jTFActivityCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(jTFResponsible, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel15)
                    .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTFNetRegion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel16)
                        .addComponent(jTFNetMarket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel17)
                        .addComponent(jTFNetCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jTFSubnetwork, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTFNetTeam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addComponent(jBSaveNet)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPNetworksLayout = new javax.swing.GroupLayout(jPNetworks);
        jPNetworks.setLayout(jPNetworksLayout);
        jPNetworksLayout.setHorizontalGroup(
            jPNetworksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPNetworksLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPNetworksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPNetSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPNetworksLayout.createSequentialGroup()
                        .addComponent(jLNetAction)
                        .addGap(18, 18, 18)
                        .addComponent(jCBNetAction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPNetEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(801, Short.MAX_VALUE))
        );
        jPNetworksLayout.setVerticalGroup(
            jPNetworksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPNetworksLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPNetworksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLNetAction)
                    .addComponent(jCBNetAction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPNetSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPNetEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jMenuBar1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jMenuView.setBackground(new java.awt.Color(255, 255, 255));
        jMenuView.setText("View");
        jMenuView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuViewActionPerformed(evt);
            }
        });

        jMReview.setBackground(new java.awt.Color(255, 255, 255));
        jMReview.setText("Review Logged Time");
        jMReview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMReviewActionPerformed(evt);
            }
        });
        jMenuView.add(jMReview);

        jMRecordMetrics.setBackground(new java.awt.Color(255, 255, 255));
        jMRecordMetrics.setText("Record Working Metrics");
        jMRecordMetrics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMRecordMetricsActionPerformed(evt);
            }
        });
        jMenuView.add(jMRecordMetrics);

        jMenuBar1.add(jMenuView);

        jMenuEdit.setBackground(new java.awt.Color(255, 255, 255));
        jMenuEdit.setText("Edit");
        jMenuEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuEditActionPerformed(evt);
            }
        });

        jMEditUsers.setBackground(new java.awt.Color(255, 255, 255));
        jMEditUsers.setText("Edit Users");
        jMEditUsers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMEditUsersActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMEditUsers);

        jMEditTask.setBackground(new java.awt.Color(255, 255, 255));
        jMEditTask.setText("Edit Tasks");
        jMEditTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMEditTaskActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMEditTask);

        jMEditNetworks.setBackground(new java.awt.Color(255, 255, 255));
        jMEditNetworks.setText("Edit Networks");
        jMEditNetworks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMEditNetworksActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMEditNetworks);

        jMenuBar1.add(jMenuEdit);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPEditTask, javax.swing.GroupLayout.PREFERRED_SIZE, 1215, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPNetworks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 1196, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPEditTask, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPNetworks, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 686, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 747, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRBTeamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBTeamActionPerformed
        // TODO add your handling code here:
        if (jRBTeam.isSelected()) {
            jCBMetricTeam.setEnabled(true);
        } else {
            jCBMetricTeam.setEnabled(false);
        }
    }//GEN-LAST:event_jRBTeamActionPerformed

    private void jBShowPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBShowPreviewActionPerformed
        // TODO add your handling code here:
        long startTime = System.currentTimeMillis();
        jLLoading.setText("Fetching metrics from database...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                UpdateMetricsTable();
                jDLoading.dispose();
                long endTime = System.currentTimeMillis();
                System.out.println("Time taken to get metrics: " + (endTime - startTime));
            }
        }).start();
        jDLoading.setVisible(true);
    }//GEN-LAST:event_jBShowPreviewActionPerformed

    private void jRBWeekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBWeekActionPerformed
        // TODO add your handling code here:
        jCBFrom.removeAllItems();
        if (jRBWeek.isSelected()) {
            int i = 0;
            for (i = 1; i <= week; i++) {
                jCBFrom.addItem(String.valueOf(i));
            }
            jCBFrom.setSelectedIndex(week - 1);
        }
    }//GEN-LAST:event_jRBWeekActionPerformed

    private void jRBTaskIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBTaskIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRBTaskIDActionPerformed

    private void jRBToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBToActionPerformed
        // TODO add your handling code here:
        if (!jRBTo.isSelected()) {
            jCBTo.setEnabled(false);
        } else {
            jCBTo.setEnabled(true);
        }
    }//GEN-LAST:event_jRBToActionPerformed

    private void jCBFromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBFromActionPerformed
        // TODO add your handling code here:
        jCBTo.removeAllItems();

        int inicial = jCBFrom.getSelectedIndex();
        System.out.println("Index selected: " + Integer.toString(inicial));
        if (inicial == -1) {
            inicial = 0;
        }
        if (jRBWeek.isSelected()) {
            for (int i = inicial + 1; i < week + 1; i++) {
                jCBTo.addItem(Integer.toString(i));
            }
        }

        if (jRBMonth.isSelected()) {
            for (int i = inicial; i < 12; i++) {
                jCBTo.addItem(months[i]);
            }
        }

        if (jRBQuarter.isSelected()) {
            for (int i = inicial + 1; i < 5; i++) {
                jCBTo.addItem(Integer.toString(i));
            }
        }
    }//GEN-LAST:event_jCBFromActionPerformed

    private void jRBMonthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBMonthActionPerformed
        // TODO add your handling code here:
        jCBFrom.removeAllItems();
        if (jRBMonth.isSelected()) {
            for (int i = 0; i < 12; i++) {
                jCBFrom.addItem(months[i]);
            }
            jCBFrom.setSelectedIndex(0);
        }

    }//GEN-LAST:event_jRBMonthActionPerformed

    private void jRBQuarterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBQuarterActionPerformed
        // TODO add your handling code here:
        jCBFrom.removeAllItems();
        if (jRBQuarter.isSelected()) {
            for (int i = 1; i <= 4; i++) {
                jCBFrom.addItem(Integer.toString(i));
            }
            jCBFrom.setSelectedIndex(0);
        }
    }//GEN-LAST:event_jRBQuarterActionPerformed

    private void jMenuEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuEditActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuEditActionPerformed

    private void jMEditUsersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMEditUsersActionPerformed
        // TODO add your handling code here:
        jPView.setVisible(false);
        jPEdit.setVisible(true);
        jPEditTask.setVisible(false);
        jPNetworks.setVisible(false);
        ResetUserFields();
        this.setTitle("MRT - Audit & Report - Sourcing - Add, Edit and Delete Users");
        this.setSize(jPEdit.getPreferredSize());
    }//GEN-LAST:event_jMEditUsersActionPerformed

    private void jMReviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMReviewActionPerformed
        // TODO add your handling code here:
        jPView.setVisible(true);
        jPEdit.setVisible(false);
        jPEditTask.setVisible(false);
        jPNetworks.setVisible(false);
        this.setTitle("MRT - Audit & Report - Sourcing - Review Metrics");
        this.setSize(jPView.getPreferredSize());
    }//GEN-LAST:event_jMReviewActionPerformed

    private void jMenuViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuViewActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuViewActionPerformed

    private void jMEditTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMEditTaskActionPerformed
        // TODO add your handling code here:
        jPView.setVisible(false);
        jPEdit.setVisible(false);
        jPEditTask.setVisible(true);
        ResetTaskFields();
        this.setTitle("MRT - Audit & Report - Sourcing - Add, Edit and Delete Tasks");
        this.setSize(jPEditTask.getPreferredSize());
    }//GEN-LAST:event_jMEditTaskActionPerformed

    private void jBDeleteTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDeleteTaskActionPerformed
        // TODO add your handling code here:
        int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete task?", "Delete?", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            long startTime = System.currentTimeMillis();
            jLLoading.setText("Deleting task from database...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DeleteTaskDB();
                    ResetTaskFields();
                    jLLoading.setText("Updating task's local table...");
                    GetAllTasks();
                    jDLoading.dispose();
                    long endTime = System.currentTimeMillis();
                    System.out.println("Time taken to delete metrics: " + (endTime - startTime));
                }
            }).start();
            jDLoading.setVisible(true);
        }
    }//GEN-LAST:event_jBDeleteTaskActionPerformed

    private void jCBTaskEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBTaskEditActionPerformed
        // TODO add your handling code here:
        if (jCBTaskEdit.getSelectedIndex() == 0) {
            jPSearchTask.setVisible(false);
            jCBTaskType.setEnabled(true);
            jTFTaskName.setEditable(true);
            ResetTaskFields();
        } else {
            jPSearchTask.setVisible(true);
            jCBTaskType.setEnabled(false);
            jTFTaskName.setEditable(false);
            ResetTaskFields();
        }
    }//GEN-LAST:event_jCBTaskEditActionPerformed

    private void jBSaveTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSaveTaskActionPerformed
        // TODO add your handling code here:
        if (jTFTaskName.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Task field is empty! Please type a task name to save it.");
        } else {
            long startTime = System.currentTimeMillis();
            jLLoading.setText("Saving task into database...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (jCBTaskEdit.getSelectedIndex() == 0) {
                        InsertTaskIntoDB();
                    } else {
                        UpdateTaskIntoDB();
                    }
                    jLLoading.setText("Updating task's local table...");
                    GetAllTasks();
                    jDLoading.dispose();
                    long endTime = System.currentTimeMillis();
                    System.out.println("Time taken to delete metrics: " + (endTime - startTime));
                }
            }).start();
            jDLoading.setVisible(true);
        }
    }//GEN-LAST:event_jBSaveTaskActionPerformed

    private void jCBTaskTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBTaskTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBTaskTypeActionPerformed

    private void jBSearchTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSearchTaskActionPerformed
        // TODO add your handling code here:
        long startTime = System.currentTimeMillis();
        jLLoading.setText("Fetching task from database...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                SearchTaskDB();
                jDLoading.dispose();
                long endTime = System.currentTimeMillis();
                System.out.println("Time taken to get metrics: " + (endTime - startTime));
            }
        }).start();
        jDLoading.setVisible(true);
    }//GEN-LAST:event_jBSearchTaskActionPerformed

    private void jBClearTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBClearTaskActionPerformed
        // TODO add your handling code here:
        jCBTaskSearch.setSelectedIndex(0);
        jCBTaskType.setSelectedIndex(0);
        jTFTaskName.setText("");
        jCBTaskTeam.setSelectedIndex(0);
    }//GEN-LAST:event_jBClearTaskActionPerformed

    private void jBGenerateCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBGenerateCSVActionPerformed
        // TODO add your handling code here:
        String fileName = jTFFileName.getText();
        TableModel model = jTableShowMetrics.getModel();
        if (!jTFFileName.getText().equals("")) {
            SaveTableCSV(fileName, model);
        } else {
            JOptionPane.showMessageDialog(this, "Please type a name for the CSV file.");
        }
    }//GEN-LAST:event_jBGenerateCSVActionPerformed

    private void jMRecordMetricsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMRecordMetricsActionPerformed
        // TODO add your handling code here:
        try {
            Sourcing_Time_Report str = new Sourcing_Time_Report();
            str.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            str.show();
            str.setLocationRelativeTo(null);

        } catch (ParseException ex) {
            Logger.getLogger(Metrics.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Time_Review.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jMRecordMetricsActionPerformed

    private void jMEditNetworksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMEditNetworksActionPerformed
        // TODO add your handling code here:
        jPView.setVisible(false);
        jPEdit.setVisible(false);
        jPEditTask.setVisible(false);
        jPNetworks.setVisible(true);
        ResetNetworkFields();
        this.setTitle("MRT - Audit & Report - Sourcing - Add, Edit and Delete Networks");
        System.out.println("Width: " + jPNetworks.getWidth() + " Height: " + jPNetworks.getHeight());

        this.setSize(jPNetworks.getMaximumSize());
    }//GEN-LAST:event_jMEditNetworksActionPerformed

    private void jCBNetActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBNetActionActionPerformed
        // TODO add your handling code here:
        if (jCBNetAction.getSelectedIndex() == 0) {
            jPNetSearch.setVisible(false);
            jTFNetwork.setEditable(true);
            ResetNetworkFields();
        } else {
            jPNetSearch.setVisible(true);
            jTFNetwork.setEditable(false);
            ResetNetworkFields();
        }
    }//GEN-LAST:event_jCBNetActionActionPerformed

    private void jCBNetSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBNetSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBNetSearchActionPerformed

    private void jBSaveNetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSaveNetActionPerformed
        // TODO add your handling code here:
        boolean flagNetwork = false;
        flagNetwork = ReviewFieldsState();
        if (flagNetwork) {
            long startTime = System.currentTimeMillis();
            jLLoading.setText("Saving network into database...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (jCBNetAction.getSelectedIndex() == 0) {
                        InsertIntoNetworkDB();
                    } else {
                        UpdateNetworksDB();
                    }
                    ResetNetworkFields();
                    jDLoading.dispose();
                    long endTime = System.currentTimeMillis();
                    System.out.println("Time taken to save a network: " + (endTime - startTime));
                }
            }).start();
            jDLoading.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "There's an empty field! Please fill all the fields to execute this action.");
        }
    }//GEN-LAST:event_jBSaveNetActionPerformed

    private void jBNetworkSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNetworkSearchActionPerformed
        // TODO add your handling code here:
        long startTime = System.currentTimeMillis();
        jLLoading.setText("Fetching network from database...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                SearchNetworkDB();
                jDLoading.dispose();
                long endTime = System.currentTimeMillis();
                System.out.println("Time taken to look for a network: " + (endTime - startTime));
            }
        }).start();
        jDLoading.setVisible(true);
    }//GEN-LAST:event_jBNetworkSearchActionPerformed

    private void jBNetSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNetSearchActionPerformed
        // TODO add your handling code here:
        ResetNetworkFields();
    }//GEN-LAST:event_jBNetSearchActionPerformed

    private void jCBTaskSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBTaskSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBTaskSearchActionPerformed

    private void jTFSignumEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTFSignumEditActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTFSignumEditActionPerformed

    private void jBSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSearchActionPerformed
        // TODO add your handling code here:
        String signumEdit = jTFSignumEdit.getText();
        Pattern onewordPattern = Pattern.compile("[^A-Za-z]");
        Matcher matcherSignum = onewordPattern.matcher(signumEdit);
        boolean flagSignum = matcherSignum.find();

        if (signumEdit.equals("")) {
            JOptionPane.showMessageDialog(this, "The field is empty! Please type a signum to start editing.");
        } else {
            if (flagSignum) {
                JOptionPane.showMessageDialog(this, "Signum must contain only letters!");
            } else {
                long startTime = System.currentTimeMillis();
                jLLoading.setText("Looking for user's info...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Connection connection;
                        PreparedStatement preparedStatement;
                        ResultSet resultset;
                        try {
                            String sql = "";
                            sql = "SELECT Signum, Name, Last_Name, Organization, Team, Customer_Unit, Line_Manager, Access, Supporting_CU FROM users WHERE Signum = '" + signumEdit + "';";
                            System.out.println(sql);
                            connection = SQL_connection.getConnection();
                            preparedStatement = connection.prepareStatement(sql);
                            resultset = preparedStatement.executeQuery();
                            if (!resultset.next()) {
                                JOptionPane.showMessageDialog(Time_Review.this, "Signum not found!");
                                jTFSignumEdit.setText("");
                            } else {
                                String signum1 = resultset.getString("Signum");
                                String name1 = resultset.getString("Name");
                                String lastname1 = resultset.getString("Last_Name");
                                String organization1 = resultset.getString("Organization");
                                String team1 = resultset.getString("Team");
                                String customerunit1 = resultset.getString("Customer_Unit");
                                String linemanager1 = resultset.getString("Line_Manager");
                                String access1 = resultset.getString("Access");
                                String supportingCU1 = resultset.getString("Supporting_CU");

                                jTFSignum.setText(signum1);
                                jTFName.setText(name1);
                                jTFLastName.setText(lastname1);
                                jCBOrganization.setSelectedItem(organization1);
                                jCBTeam.setSelectedItem(team1);
                                jCBCustomerUnit.setSelectedItem(customerunit1);
                                jTFLineManager.setText(linemanager1);
                                jCBAccess.setSelectedIndex(Integer.parseInt(access1));
                                if (supportingCU1.equals("NA")) {
                                    jCBOtherCU.setSelectedIndex(0);
                                    jCBSupportedCU.setEnabled(false);
                                } else {
                                    jCBOtherCU.setSelectedIndex(1);
                                    jCBSupportedCU.setEnabled(true);
                                    jCBSupportedCU.setSelectedItem(supportingCU1);
                                }
                                // jCBOrganization.addItem(row);
                            }
                            connection.close();
                            long endTime = System.currentTimeMillis();
                            System.out.println("Time taken to do User Search: " + (endTime - startTime));

                        } catch (SQLException e) {
                            JOptionPane.showMessageDialog(Time_Review.this, "An error ocurred during the look up! Please try again...");
                        }

                        jDLoading.dispose();
                        long endTime = System.currentTimeMillis();
                        System.out.println("Time taken to search a user: " + (endTime - startTime));
                    }
                }).start();
                jDLoading.setVisible(true);
            }
        }
    }//GEN-LAST:event_jBSearchActionPerformed

    private void jBClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBClearActionPerformed
        // TODO add your handling code here:
        ResetUserFields();
    }//GEN-LAST:event_jBClearActionPerformed

    private void jBDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDeleteActionPerformed
        // TODO add your handling code here:
        String signumEdit = jTFSignumEdit.getText();
        Pattern onewordPattern = Pattern.compile("[^A-Za-z]");
        Matcher matcherSignum = onewordPattern.matcher(signumEdit);
        boolean flagSignum = matcherSignum.find();
        int reply;
        if (signumEdit.equals("")) {
            JOptionPane.showMessageDialog(this, "The field is empty! Please type a signum to start editing.");
        } else {
            if (flagSignum) {
                JOptionPane.showMessageDialog(this, "Signum must contain only letters!");
            } else {
                reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + signumEdit + "?", "Delete?", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    long startTime = System.currentTimeMillis();
                    jLLoading.setText("Deleting user from database...");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DeleteUserDB();
                            ResetUserFields();
                            jLLoading.setText("Updating user's local table...");
                            GetAllUsers();
                            jDLoading.dispose();
                            long endTime = System.currentTimeMillis();
                            System.out.println("Time taken to delete a user: " + (endTime - startTime));
                        }
                    }).start();
                    jDLoading.setVisible(true);
                }
            }
        }
    }//GEN-LAST:event_jBDeleteActionPerformed

    private void jCBActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBActionActionPerformed
        // TODO add your handling code here:
        if (jCBAction.getSelectedIndex() == 0) {
            jPUser.setVisible(true);
            jPSearch.setVisible(false);
            jTFSignum.setEditable(true);
            jTFName.setEditable(true);
            jTFLastName.setEditable(true);
            jTFLineManager.setEditable(true);
            ResetUserFields();
        } else if (jCBAction.getSelectedIndex() == 1) {
            jPUser.setVisible(true);
            jPSearch.setVisible(true);
            jTFSignum.setEditable(false);
            jTFName.setEditable(false);
            jTFLastName.setEditable(false);
            jTFLineManager.setEditable(false);
            ResetUserFields();
        }
    }//GEN-LAST:event_jCBActionActionPerformed

    private void jCBSupportedCUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBSupportedCUActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBSupportedCUActionPerformed

    private void jCBOtherCUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBOtherCUActionPerformed
        // TODO add your handling code here:
        if (jCBOtherCU.getSelectedIndex() == 0) {
            jCBSupportedCU.setEnabled(false);
        } else {
            jCBSupportedCU.setEnabled(true);
        }
    }//GEN-LAST:event_jCBOtherCUActionPerformed

    private void jBSaveNewUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSaveNewUserActionPerformed
        // TODO add your handling code here:
        System.out.println("Entrando a Save user button...");
        String signum = jTFSignum.getText();
        String name = jTFName.getText();
        String lastName = jTFLastName.getText();
        String lineManager = jTFLineManager.getText();
        Pattern onewordPattern = Pattern.compile("[^A-Za-z]");
        Pattern lettersPattern = Pattern.compile("[^A-Za-z]->[A-Za-z]");

        Matcher matcherSignum = onewordPattern.matcher(signum);
        Matcher matcherName = lettersPattern.matcher(name);
        Matcher matcherLastName = lettersPattern.matcher(lastName);
        Matcher matcherLineManager = lettersPattern.matcher(lineManager);
        boolean flagSignum = matcherSignum.find();
        boolean flagName = matcherName.find();
        boolean flagLastName = matcherLastName.find();
        boolean flagLineManager = matcherLineManager.find();

        if (!signum.equals("") && !name.equals("") && !lastName.equals("") && !lineManager.equals("")) {
            if (flagSignum) {
                JOptionPane.showMessageDialog(this, "Signum must contain only letters!");
                jTFSignum.setText("");
            } else if (flagName) {
                JOptionPane.showMessageDialog(this, "Name must contain only letters!");
                jTFName.setText("");
            } else if (flagLastName) {
                JOptionPane.showMessageDialog(this, "Last name must contain only letters!");
                jTFLastName.setText("");
            } else if (flagLineManager) {
                JOptionPane.showMessageDialog(this, "Line manager signum must contain only letters!");
                jTFLineManager.setText("");
            } else {
                long startTime = System.currentTimeMillis();
                jLLoading.setText("Saving user into database...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (jCBAction.getSelectedIndex() == 0) {
                            InsertIntoUserDB();
                        } else {
                            UpdateUserDB();
                        }
                        jLLoading.setText("Updating user's local table...");
                        GetAllUsers();
                        jDLoading.dispose();
                        long endTime = System.currentTimeMillis();
                        System.out.println("Time taken to save a user: " + (endTime - startTime));
                    }
                }).start();
                jDLoading.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "There's an empty field! Please fill every text field in order to save the user.");
        }
    }//GEN-LAST:event_jBSaveNewUserActionPerformed

    private void jCBCustomerUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBCustomerUnitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBCustomerUnitActionPerformed

    private void jBExportUserCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBExportUserCSVActionPerformed
        // TODO add your handling code here:
        String fileName = "Metrics Users";
        TableModel model = jTUsersList.getModel();
        SaveTableCSV(fileName, model);
    }//GEN-LAST:event_jBExportUserCSVActionPerformed

    private void jBExportTaskCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBExportTaskCSVActionPerformed
        // TODO add your handling code here:
        String fileName = "Metrics Tasks";
        TableModel model = jTTasksList.getModel();
        SaveTableCSV(fileName, model);
    }//GEN-LAST:event_jBExportTaskCSVActionPerformed

    private void jBNetDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNetDeleteActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_jBNetDeleteActionPerformed

    private void jCBToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBToActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBToActionPerformed

    private void jRBNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBNameActionPerformed
        // TODO add your handling code here:
        if (jRBName.isSelected()) {
            jCBUser.setEnabled(true);
        } else {
            jCBUser.setEnabled(false);
        }
    }//GEN-LAST:event_jRBNameActionPerformed

    private void jBCSVMetricsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBCSVMetricsActionPerformed
        // TODO add your handling code here:
        long startTime = System.currentTimeMillis();
        jLLoading.setText("Fetching metrics from database...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                ExportMetricsCSV();
                jDLoading.dispose();
                long endTime = System.currentTimeMillis();
                System.out.println("Time taken to save metrics: " + (endTime - startTime));
            }
        }).start();
        jDLoading.setVisible(true);
    }//GEN-LAST:event_jBCSVMetricsActionPerformed

    private void GetTasks() {
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultset;
        try {
            String sql_task = null, task = "";
            sql_task = " SELECT DISTINCT Task FROM tasks_sourcing ORDER BY Task ASC;";
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement(sql_task);
            resultset = preparedStatement.executeQuery();

            while (resultset.next()) {
                task = resultset.getString("Task");
                jCBTaskSearch.addItem(task);
            }
            connection.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void GetOrganization() {
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultset;
        try {
            String sql = "";
            sql = "SELECT DISTINCT Organization FROM users ORDER BY Organization asc;";
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultset = preparedStatement.executeQuery();
            while (resultset.next()) {
                String row = resultset.getString("Organization");
                jCBOrganization.addItem(row);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void GetTeam() {
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultset;
        try {
            String sql = "";
            sql = "SELECT DISTINCT Team FROM users ORDER BY Team asc;";
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultset = preparedStatement.executeQuery();
            while (resultset.next()) {
                String row = resultset.getString("Team");
                jCBTeam.addItem(row);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void GetTaskTeam() {
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultset;
        try {
            String sql = "";
            sql = "SELECT DISTINCT Team FROM tasks_sourcing ORDER BY Team asc;";
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultset = preparedStatement.executeQuery();
            while (resultset.next()) {
                String row = resultset.getString("Team");
                jCBTaskTeam.addItem(row);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void GetCustomerUnit() {
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultset;
        try {
            String sql = "";
            sql = "SELECT DISTINCT Customer_Unit FROM users ORDER BY Customer_Unit asc;";
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultset = preparedStatement.executeQuery();
            while (resultset.next()) {
                String row = resultset.getString("Customer_Unit");
                jCBCustomerUnit.addItem(row);
                jCBSupportedCU.addItem(row);
            }
            connection.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void GetNetworksSearch() {
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultset;
        try {
            String sql = "";
            sql = "SELECT DISTINCT Network FROM networks ORDER BY Network asc;";
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultset = preparedStatement.executeQuery();
            while (resultset.next()) {
                String row = resultset.getString("Network");
                jCBNetSearch.addItem(row);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void GetAllUsers() {
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultset;
        String[] column = {"Signum", "Last Name", "Name", "Customer Unit", "Team", "Organization", "Line Manager", "Supporting CU"};
        String[] row = new String[8];
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(column);
        try {

            String sql, org1, cu1, team1, name1;
            sql = "SELECT Signum, Last_Name, Name, Customer_Unit, Team, Organization, Line_Manager, Supporting_CU FROM users ORDER BY Last_Name asc;";
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultset = preparedStatement.executeQuery();
            while (resultset.next()) {
                for (int i = 0; i < 8; i++) {
                    row[i] = resultset.getString(i + 1);
                }
                org1 = resultset.getString("Organization");
                cu1 = resultset.getString("Customer_Unit");
                team1 = resultset.getString("Team");
                name1 = resultset.getString("Last_Name") + " " + resultset.getString("Name");
                if (!user_names.isEmpty()) {
                    if (!user_names.contains(name1)) {
                        user_names.add(name1);
                        jCBUser.addItem(name1);
                    }
                } else {
                    user_names.add(name1);
                    jCBUser.addItem(name1);
                }
                if (!organizations.isEmpty()) {
                    if (!organizations.contains(org1)) {
                        organizations.add(org1);
                        jCBOrganization.addItem(org1);
                    }
                } else {
                    organizations.add(org1);
                    jCBOrganization.addItem(org1);
                }
                if (!cus.isEmpty()) {
                    if (!cus.contains(cu1)) {
                        cus.add(cu1);
                        jCBCustomerUnit.addItem(cu1);
                        jCBSupportedCU.addItem(cu1);
                    }
                } else {
                    cus.add(cu1);
                    jCBCustomerUnit.addItem(cu1);
                    jCBSupportedCU.addItem(cu1);
                }
                if (!user_teams.isEmpty()) {
                    if (!user_teams.contains(team1)) {
                        user_teams.add(team1);
                        jCBTeam.addItem(team1);
                        jCBMetricTeam.addItem(team1);
                    }
                } else {
                    user_teams.add(team1);
                    jCBTeam.addItem(team1);
                    jCBMetricTeam.addItem(team1);
                }
                model.addRow(row);
            }
            jTUsersList.setModel(model);
            resizeColumnWidth(jTUsersList);
            jTUsersList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            connection.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void GetAllTasks() {
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultset;
        String[] column = {"Task_ID", "Task", "Team"};
        String[] row = new String[3];
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(column);
        try {

            String sql, task1, team1;
            sql = "SELECT Task_ID, Task, Team FROM tasks_sourcing ORDER BY Task_ID asc;";
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultset = preparedStatement.executeQuery();
            while (resultset.next()) {
                for (int i = 0; i < 3; i++) {
                    row[i] = resultset.getString(i + 1);
                }
                task1 = resultset.getString("Task");
                team1 = resultset.getString("Team");
                if (!tasks.isEmpty()) {
                    if (!tasks.contains(task1)) {
                        tasks.add(task1);
                        jCBTaskSearch.addItem(task1);
                    }
                } else {
                    tasks.add(task1);
                    jCBTaskSearch.addItem(task1);
                }
                if (!teams.isEmpty()) {
                    if (!teams.contains(team1)) {
                        teams.add(team1);
                        jCBTaskTeam.addItem(team1);
                    }
                } else {
                    teams.add(team1);
                    jCBTaskTeam.addItem(team1);
                }
                model.addRow(row);
            }
            jTTasksList.setModel(model);
            resizeColumnWidth(jTTasksList);
            jTTasksList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            connection.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void InsertIntoUserDB() {
        Connection connection;
        PreparedStatement preparedStatement;
        //ResultSet resultset;
        try {
            int access = jCBAccess.getSelectedIndex();
            String supportingCU = "NA";
            if (jCBOtherCU.getSelectedIndex() == 1) {
                supportingCU = jCBSupportedCU.getItemAt(jCBSupportedCU.getSelectedIndex());
            }

            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO users (Signum, Last_Name, Name, Customer_Unit, Team, Organization, Line_Manager, Access, Supporting_CU) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

            preparedStatement.setObject(1, jTFSignum.getText());
            preparedStatement.setObject(2, jTFLastName.getText());
            preparedStatement.setObject(3, jTFName.getText());
            preparedStatement.setObject(4, jCBCustomerUnit.getItemAt(jCBCustomerUnit.getSelectedIndex()));
            preparedStatement.setObject(5, jCBTeam.getItemAt(jCBTeam.getSelectedIndex()));
            preparedStatement.setObject(6, jCBOrganization.getItemAt(jCBOrganization.getSelectedIndex()));
            preparedStatement.setObject(7, jTFLineManager.getText());
            preparedStatement.setObject(8, access);
            preparedStatement.setObject(9, supportingCU);
            preparedStatement.executeUpdate();

            /*resultset = preparedStatement.getResultSet();
            if (resultset.next())
                System.out.println("Mensaje: " + resultset.getString("mens"));*/
            System.out.println("Query: " + preparedStatement);
            connection.close();

            //JOptionPane.showMessageDialog(this, "User saved successfully.");
            ResetUserFields();
        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Something went wrong, please try again later.");
        }
    }

    private void UpdateUserDB() {
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            int access = jCBAccess.getSelectedIndex();
            String supportingCU = "NA";
            if (jCBOtherCU.getSelectedIndex() == 1) {
                supportingCU = jCBSupportedCU.getItemAt(jCBSupportedCU.getSelectedIndex());
            }

            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("UPDATE users SET Customer_Unit = ?, Team = ?, Organization = ?, Access = ?, Supporting_CU = ? "
                    + "WHERE Signum = ?");
            preparedStatement.setObject(1, jCBCustomerUnit.getItemAt(jCBCustomerUnit.getSelectedIndex()));
            preparedStatement.setObject(2, jCBTeam.getItemAt(jCBTeam.getSelectedIndex()));
            preparedStatement.setObject(3, jCBOrganization.getItemAt(jCBOrganization.getSelectedIndex()));
            preparedStatement.setObject(4, access);
            preparedStatement.setObject(5, supportingCU);
            preparedStatement.setObject(6, jTFSignum.getText());
            preparedStatement.executeUpdate();
            System.out.println("Query: " + preparedStatement);
            connection.close();

            //JOptionPane.showMessageDialog(this, "User saved successfully.");
            ResetUserFields();
        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Something went wrong, please try again later.");
        }
    }

    private void DeleteUserDB() {
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("DELETE FROM users "
                    + "WHERE Signum = ?");
            preparedStatement.setObject(1, jTFSignumEdit.getText());
            preparedStatement.executeUpdate();
            System.out.println("Query: " + preparedStatement);
            connection.close();

            JOptionPane.showMessageDialog(this, "User deleted successfully.");

            jTFSignumEdit.setText("");
        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Something went wrong, please try again later.");
        }
    }

    private void InsertTaskIntoDB() {
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            int idd = 0;
            String ID = "";
            String task = jTFTaskName.getText();
            String type = jCBTaskType.getItemAt(jCBTaskType.getSelectedIndex());
            String team = jCBTaskTeam.getItemAt(jCBTaskTeam.getSelectedIndex());
            String billable = "Billable";
            int index = jCBTaskType.getSelectedIndex();
            if (index == 0) {
                billable = "Not Billable";
            }

            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("SELECT COUNT(Task_ID) AS Amount FROM tasks_sourcing "
                    + "WHERE Task_ID LIKE '" + type + "%';");
            System.out.println("Query: " + preparedStatement);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                ID = resultSet.getString("Amount");
            }
            System.out.println("Amount: " + ID);
            idd = Integer.valueOf(ID) + 1;
            if (idd < 10) {
                ID = type + "-00" + String.valueOf(idd);
            } else {
                ID = type + "-0" + String.valueOf(idd);
            }
            // connection.close();

            // connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO tasks_sourcing (Task_ID, Task, Team, SAP_Billable) "
                    + "VALUES (?,?,?,?);");
            preparedStatement.setObject(1, ID);
            preparedStatement.setObject(2, task);
            preparedStatement.setObject(3, team);
            preparedStatement.setObject(4, billable);
            preparedStatement.executeUpdate();
            System.out.println("Insert Query: " + preparedStatement);
            connection.close();

            JOptionPane.showMessageDialog(this, "Task with ID: " + ID + " saved successfully.");
            jCBTaskType.setSelectedIndex(0);
            jTFTaskName.setText("");
            jCBTaskTeam.setSelectedIndex(0);
        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Something went wrong, please try again later.");
        }
    }

    private void UpdateTaskIntoDB() {
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            String task = jCBTaskSearch.getItemAt(jCBTaskSearch.getSelectedIndex());
            String team = jCBTaskTeam.getItemAt(jCBTaskTeam.getSelectedIndex());

            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("UPDATE tasks_sourcing SET Team = ? "
                    + "WHERE Task = ?");
            preparedStatement.setObject(1, team);
            preparedStatement.setObject(2, task);
            preparedStatement.executeUpdate();
            System.out.println("Query: " + preparedStatement);
            connection.close();

            JOptionPane.showMessageDialog(this, "Task saved successfully.");
            ResetTaskFields();
        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Something went wrong, please try again later.");
        }
    }

    private void SearchTaskDB() {
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            String tname = jCBTaskSearch.getItemAt(jCBTaskSearch.getSelectedIndex());
            tid = "";
            String taskname = "", team = "";
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("SELECT Task_ID, Task, Team FROM tasks_sourcing "
                    + "WHERE Task = ?;");
            preparedStatement.setObject(1, tname);
            System.out.println("Query: " + preparedStatement);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                tid = resultSet.getString("Task_ID");
                taskname = resultSet.getString("Task");
                team = resultSet.getString("Team");

                for (int i = 0; i < 4; i++) {
                    if (tid.startsWith(jCBTaskType.getItemAt(i))) {
                        jCBTaskType.setSelectedIndex(i);
                    }
                }
                jTFTaskName.setText(taskname);
                jCBTaskTeam.setSelectedItem(team);
            } else {
                JOptionPane.showMessageDialog(this, "Task not found! Please try with another Task ID.");
            }

            connection.close();
        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Something went wrong, please try again later.");
        }
    }

    private void DeleteTaskDB() {
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("DELETE FROM tasks_sourcing "
                    + "WHERE Task = ?");
            preparedStatement.setObject(1, jCBTaskSearch.getSelectedItem());
            preparedStatement.executeUpdate();
            System.out.println("Query: " + preparedStatement);
            connection.close();

            JOptionPane.showMessageDialog(this, "Task deleted successfully.");
            //jCB.setText("");
        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Something went wrong, please try again later.");
        }
    }

    private void InsertIntoNetworkDB() {
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            String pd = jTFPD.getText();
            String net = jTFNetwork.getText();
            String activityCode = jTFActivityCode.getText();
            String responsible = jTFResponsible.getText();
            String region = jTFNetRegion.getText();
            String market = jTFNetMarket.getText();
            String customer = jTFNetCustomer.getText();
            String subnetwork = jTFSubnetwork.getText();
            String team = jTFNetTeam.getText();

            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO networks (PD, Network, Activity_code, Region, Market, Customer, Responsible, Subnetwork, Team) "
                    + "VALUES (?,?,?,?,?,?,?,?,?);");
            preparedStatement.setObject(1, pd);
            preparedStatement.setObject(2, net);
            preparedStatement.setObject(3, activityCode);
            preparedStatement.setObject(4, region);
            preparedStatement.setObject(5, market);
            preparedStatement.setObject(6, customer);
            preparedStatement.setObject(7, responsible);
            preparedStatement.setObject(8, subnetwork);
            preparedStatement.setObject(8, team);
            preparedStatement.executeUpdate();
            connection.close();

            JOptionPane.showMessageDialog(this, "Network: " + net + " saved successfully.");
            ResetNetworkFields();
        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Something went wrong, please try again later.");
        }
    }

    private void SearchNetworkDB() {
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            String net = jCBNetSearch.getItemAt(jCBNetSearch.getSelectedIndex());
            String pd = "", activity = "", responsible = "", subnetwork = "", region = "", market = "", customer = "", team = "";
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("SELECT PD, Network, Activity_code, Region, Market, Customer, Responsible, Subnetwork, Team FROM networks "
                    + "WHERE Network = ?;");
            preparedStatement.setObject(1, net);
            System.out.println("Query: " + preparedStatement);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                pd = resultSet.getString("PD");
                activity = resultSet.getString("Activity_code");
                region = resultSet.getString("Region");
                market = resultSet.getString("Market");
                customer = resultSet.getString("Customer");
                responsible = resultSet.getString("Responsible");
                subnetwork = resultSet.getString("Subnetwork");
                team = resultSet.getString("Team");

                jTFPD.setText(pd);
                jTFNetwork.setText(net);
                jTFActivityCode.setText(activity);
                jTFResponsible.setText(responsible);
                jTFNetRegion.setText(region);
                jTFNetMarket.setText(market);
                jTFNetCustomer.setText(customer);
                jTFSubnetwork.setText(subnetwork);
                jTFNetTeam.setText(team);
            } else {
                JOptionPane.showMessageDialog(this, "Network not found! Please try with another one");
            }

            connection.close();
        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Something went wrong, please try again later.");
        }
    }

    private void UpdateNetworksDB() {
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            String task = jCBTaskSearch.getItemAt(jCBTaskSearch.getSelectedIndex());
            String team = jCBTaskTeam.getItemAt(jCBTaskTeam.getSelectedIndex());

            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("UPDATE network SET Team = ? "
                    + "WHERE Task = ?");
            preparedStatement.setObject(1, team);
            preparedStatement.setObject(2, task);
            preparedStatement.executeUpdate();
            System.out.println("Query: " + preparedStatement);
            connection.close();

            JOptionPane.showMessageDialog(this, "Task saved successfully.");
            ResetTaskFields();
        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Something went wrong, please try again later.");
        }
    }

    private boolean ReviewFieldsState() {
        boolean flag = false;

        if (!jTFPD.getText().equals("") && !jTFNetwork.getText().equals("") && !jTFActivityCode.getText().equals("")
                && !jTFResponsible.getText().equals("") && !jTFSubnetwork.getText().equals("") && !jTFNetTeam.getText().equals("")
                && !jTFNetRegion.getText().equals("") && !jTFNetMarket.getText().equals("") && !jTFNetCustomer.getText().equals("")) {
            return true;
        } else {
            return flag;
        }
    }

    private void ResetUserFields() {
        jTFSignum.setText("");
        jTFName.setText("");
        jTFLastName.setText("");
        jCBOrganization.setSelectedIndex(0);
        jCBTeam.setSelectedIndex(0);
        jCBCustomerUnit.setSelectedIndex(0);
        jTFLineManager.setText("");
        jCBOtherCU.setSelectedIndex(0);
        jCBSupportedCU.setEnabled(false);
        jCBSupportedCU.setSelectedIndex(0);
        jTFSignumEdit.setText("");
    }

    private void ResetNetworkFields() {
        jTFPD.setText("");
        jTFNetwork.setText("");
        jTFActivityCode.setText("");
        jTFNetRegion.setText("");
        jTFNetMarket.setText("");
        jTFNetCustomer.setText("");
        jTFResponsible.setText("");
        jTFSubnetwork.setText("");
        jTFNetTeam.setText("");
    }

    private void ResetTaskFields() {
        jCBTaskSearch.setSelectedIndex(0);
        jCBTaskType.setSelectedIndex(0);
        jTFTaskName.setText("");
        jCBTaskTeam.setSelectedIndex(0);
    }

    private void SaveTableCSV(String fileName, TableModel tableModel) {
        String user = System.getProperty("user.name");
        String path = "C:\\Users\\" + user + "\\Documents\\" + fileName + ".csv";
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
                    System.out.println("Valor 0: " + s);
                    s = s.replace(",", "");
                    System.out.println("Valor: " + s);
                    sb.append(s + ",");
                }
                sb.append("\n");
            }
            writer.write(sb.toString());
            System.out.println("Template generated successfully");
            JOptionPane.showMessageDialog(this, "CSV file was saved to " + path + " successfully.");
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void resizeColumnWidth(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 100; // Min width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 1, width);
            }
            if (width > 300) {
                width = 300;
            }
            columnModel.getColumn(column).setPreferredWidth(width);
        }
    }

    private void UpdateMetricsTable() {
        String parametros = new String();
        String orden = new String();
        String week = "", name = "", team = "";
        List<String> columnas = new ArrayList<String>();
        String fileName = "Metrics ";

        if (jRBSignum.isSelected()) {
            parametros = parametros + "metrics_sourcing.Signum, ";
            columnas.add("Signum");
            orden = orden + "metrics_sourcing.Signum, ";
        }
        if (jRBName.isSelected()) {
            parametros = parametros + "metrics_sourcing.Name, ";
            columnas.add("Name");
            orden = orden + "metrics_sourcing.Name, ";
            if (jCBUser.getSelectedIndex() != 0) {
                name = " AND metrics_sourcing.Name = '" + jCBUser.getSelectedItem() + "'";
            }
        }
        if (jRBTeam.isSelected()) {
            parametros = parametros + "users.Team, ";
            columnas.add("Team");
            orden = orden + "users.Team, ";
            if (jCBMetricTeam.getSelectedIndex() != 0) {
                team = " AND users.Team = '" + jCBMetricTeam.getSelectedItem() + "'";
            }
        }
        if (jRBTaskID.isSelected()) {
            parametros = parametros + "Task_ID, ";
            columnas.add("Task ID");
            orden = orden + "Task_ID, ";
        }
        if (jRBTask.isSelected()) {
            parametros = parametros + "Task, ";
            columnas.add("Task");
            fileName = fileName + "Task ";
            orden = orden + "Task, ";
        }
        if (jRBNetwork.isSelected()) {
            parametros = parametros + "Network, ";
            columnas.add("Network");
            orden = orden + "Network, ";
        }
        if (jRBSubnetwork.isSelected()) {
            parametros = parametros + "Subnetwork, ";
            columnas.add("Subnetwork");
            orden = orden + "Subnetwork, ";
        }
        if (jRBSAPBilling.isSelected()) {
            parametros = parametros + "SAP_Billing, ";
            columnas.add("SAP Billing");
            orden = orden + "SAP_Billing, ";
        }
        if (jRBWeek.isSelected()) {
            parametros = parametros + "Work_date, ";
            columnas.add("Work date");
            orden = "Work_date, " + orden;
            String weekFrom = jCBFrom.getItemAt(jCBFrom.getSelectedIndex());
            String weekTo = jCBTo.getItemAt(jCBTo.getSelectedIndex());

            if (!jRBTo.isSelected()) {
                week = "WHERE Week = " + weekFrom;
                fileName = fileName + "Week " + weekFrom;
            } else {
                week = "WHERE Week BETWEEN " + weekFrom + " AND " + weekTo;
                fileName = fileName + "From Week " + weekFrom + " TO " + weekTo;
            }
        }
        if (jRBMonth.isSelected()) {
            String date1, date2;
            String monthFrom = jCBFrom.getItemAt(jCBFrom.getSelectedIndex());
            String monthTo = jCBTo.getItemAt(jCBTo.getSelectedIndex());
            int mFrom = 0, mTo = 0;
            for (int i = 0; i < 12; i++) {
                if (monthFrom.equals(months[i])) {
                    mFrom = i + 1;
                }
                if (monthTo.equals(months[i])) {
                    mTo = i + 1;
                }
            }
            date1 = "2020-" + String.valueOf(mFrom) + "-01";
            if (jRBTo.isSelected()) {
                date2 = "2020-" + String.valueOf(mTo) + "-" + String.valueOf(days[mTo - 1]);
                fileName = fileName + "From " + monthFrom + " To " + monthTo;
            } else {
                date2 = "2020-" + String.valueOf(mFrom) + "-" + String.valueOf(days[mFrom - 1]);
                fileName = fileName + "From " + monthFrom;
            }
            parametros = parametros + "Week, ";
            columnas.add("Week");
            orden = "Week, " + orden;
            week = "WHERE Work_date BETWEEN '" + date1 + "' AND '" + date2 + "'";
        }
        if (jRBQuarter.isSelected()) {
            String date1, date2;
            int monthFrom = Integer.parseInt(jCBFrom.getItemAt(jCBFrom.getSelectedIndex())) - 1;
            int monthTo = Integer.parseInt(jCBTo.getItemAt(jCBTo.getSelectedIndex())) - 1;
            String[] daysQuarter = {"31", "30", "30", "31"};
            String[] quarterFrom = {"1", "4", "7", "10"};
            String[] quarterTo = {"3", "6", "9", "12"};
            date1 = "2020-" + quarterFrom[monthFrom] + "-01";
            if (jRBTo.isSelected()) {
                date2 = "2020-" + quarterTo[monthTo] + "-" + daysQuarter[monthTo];
                fileName = fileName + "From Q" + monthFrom + " To Q" + monthTo;
            } else {
                date2 = "2020-" + quarterTo[monthFrom] + "-" + daysQuarter[monthFrom];
                fileName = fileName + "From Q" + monthFrom;
            }
            parametros = parametros + "Work_date, ";
            columnas.add("Month");
            orden = "MONTH(Work_date), " + orden;
            week = "WHERE Work_date BETWEEN '" + date1 + "' AND '" + date2 + "'";
        }

        /*if (jRBSignum.isSelected() || jRBName.isSelected()) {
            orden = "Signum, " + orden;
        }*/
        orden = orden.substring(0, orden.length() - 2);
        columnas.add("Logged Time");
        String[] column = new String[columnas.size()];
        columnas.toArray(column);
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(column);

        try {
            String[] valores = new String[columnas.size()];
            for (int j = 0; j < columnas.size(); j++) {
                valores[j] = "";
            }
            connection = SQL_connection.getConnection();
            statement = connection.createStatement();
            String query = "SELECT " + parametros + "SUM(Logged_Time) FROM metrics_sourcing "
                    + "INNER JOIN users ON users.Signum = metrics_sourcing.Signum "
                    + week + name + team + " GROUP BY " + orden + " ORDER BY " + orden;
            System.out.println(query);
            ResultSet rs = statement.executeQuery(query);
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "There are no metrics registered during this period... Try another one");
            } else {
                do {
                    for (int j = 1; j < columnas.size() + 1; j++) {
                        if (jRBQuarter.isSelected()) {
                            if (j == columnas.size() - 1) {
                                for (int i = 0; i < 12; i++) {
                                    if (rs.getString(j).startsWith(monthSQL[i])) {
                                        valores[j - 1] = months[i];
                                    }
                                }
                            } else {
                                valores[j - 1] = rs.getString(j);
                            }
                        } else {
                            valores[j - 1] = rs.getString(j);
                        }
                        /*if(j>columnas.size()-1)
                        valores[j-1] = rs.getString(j);
                    else{
                        if(valores[j-1].equals("")){
                            valores[j-1] = rs.getString(j);
                            valores2[j-1]= rs.getString(j);
                        }
                        else if (valores2[j-1].equals(rs.getString(j)))
                            valores[j-1] = " ";
                        else if (!valores2[j-1].equals(rs.getString(j))){
                            valores[j-1] = rs.getString(j);
                            valores2[j-1]= rs.getString(j);
                        }
                    }*/
                    }
                    model.addRow(valores);
                } while (rs.next());
            }
            jTFFileName.setText(fileName);
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(Time_Review.class.getName()).log(Level.SEVERE, null, ex);
        }
        jTableShowMetrics.setModel(model);
        resizeColumnWidth(jTableShowMetrics);
    }

    private void ExportMetricsCSV() {
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        StringBuilder sb = new StringBuilder();
        String user = System.getProperty("user.name");
        String path = "C:\\Users\\" + user + "\\Documents\\";
        String[] header = {"Region", "Organization", "Signum", "Name", "Requestor", "Task ID", "Task", "Network", "Subnetwork", "Activity Code", "SAP Billing",
            "Work Date", "Logged Time", "Week", "FTR", "On Time", "Failed FTR Category", "Failed On Time", "Number of requests", "Comments"};
        String name1 = "", parametros = "", orden = "", fileName = "Metrics ", week = "";

        if (jCBUser.getSelectedIndex() != 0) {
            name1 = " AND metrics_sourcing.Name = '" + jCBUser.getSelectedItem() + "'";
            fileName = fileName + jCBUser.getSelectedItem();
        }

        if (jRBWeek.isSelected()) {
            parametros = parametros + "Work_date, ";
            orden = "Work_date, " + orden;
            String weekFrom = jCBFrom.getItemAt(jCBFrom.getSelectedIndex());
            String weekTo = jCBTo.getItemAt(jCBTo.getSelectedIndex());

            if (!jRBTo.isSelected()) {
                week = "WHERE Week = " + weekFrom;
                fileName = fileName + " Week " + weekFrom;
            } else {
                week = "WHERE Week BETWEEN " + weekFrom + " AND " + weekTo;
                fileName = fileName + " From Week " + weekFrom + " TO " + weekTo;
            }
        }
        if (jRBMonth.isSelected()) {
            String date1, date2;
            String monthFrom = jCBFrom.getItemAt(jCBFrom.getSelectedIndex());
            String monthTo = jCBTo.getItemAt(jCBTo.getSelectedIndex());
            int mFrom = 0, mTo = 0;
            for (int i = 0; i < 12; i++) {
                if (monthFrom.equals(months[i])) {
                    mFrom = i + 1;
                }
                if (monthTo.equals(months[i])) {
                    mTo = i + 1;
                }
            }
            date1 = "2020-" + String.valueOf(mFrom) + "-01";
            if (jRBTo.isSelected()) {
                date2 = "2020-" + String.valueOf(mTo) + "-" + String.valueOf(days[mTo - 1]);
                fileName = fileName + " From " + monthFrom + " To " + monthTo;
            } else {
                date2 = "2020-" + String.valueOf(mFrom) + "-" + String.valueOf(days[mFrom - 1]);
                fileName = fileName + " From " + monthFrom;
            }
            parametros = parametros + "Week, ";
            orden = "Week, " + orden;
            week = "WHERE Work_date BETWEEN '" + date1 + "' AND '" + date2 + "'";
        }
        if (jRBQuarter.isSelected()) {
            String date1, date2;
            int monthFrom = Integer.parseInt(jCBFrom.getItemAt(jCBFrom.getSelectedIndex())) - 1;
            int monthTo = Integer.parseInt(jCBTo.getItemAt(jCBTo.getSelectedIndex())) - 1;
            String[] daysQuarter = {"31", "30", "30", "31"};
            String[] quarterFrom = {"1", "4", "7", "10"};
            String[] quarterTo = {"3", "6", "9", "12"};
            date1 = "2020-" + quarterFrom[monthFrom] + "-01";
            if (jRBTo.isSelected()) {
                date2 = "2020-" + quarterTo[monthTo] + "-" + daysQuarter[monthTo];
                fileName = fileName + " From Q" + monthFrom + " To Q" + monthTo;
            } else {
                date2 = "2020-" + quarterTo[monthFrom] + "-" + daysQuarter[monthFrom];
                fileName = fileName + " From Q" + monthFrom;
            }
            parametros = parametros + "Work_date, ";
            orden = "MONTH(Work_date), " + orden;
            week = "WHERE Work_date BETWEEN '" + date1 + "' AND '" + date2 + "'";
        }

        try {
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM metrics_sourcing " + week + name1 + ";");
            System.out.println("Query: " + preparedStatement);
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                JOptionPane.showMessageDialog(this, "Metrics not found! Please try again later...");
            } else {
                for (int i = 0; i < 20; i++) {
                    sb.append(header[i] + ",");
                }
                sb.append("\n");
                do {
                    for (int j = 2; j < 22; j++) {
                        if (resultSet.getString(j).contains(",")) {
                            sb.append("\"" + resultSet.getString(j) + "\",");
                        } else {
                            sb.append(resultSet.getString(j) + ",");
                        }
                    }
                    sb.append("\n");
                } while (resultSet.next());
                path = path + fileName + ".csv";
                try (PrintWriter writer = new PrintWriter(new File(path))) {
                    writer.write(sb.toString());
                    System.out.println("Template generated successfully");
                    JOptionPane.showMessageDialog(this, "CSV file was saved to " + path + " successfully.");
                } catch (FileNotFoundException e) {
                    System.out.println(e.getMessage());
                }
            }
            connection.close();
        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Something went wrong, please try again later...");
        }
    }

    private void setLookAndFeel() {
        //MenuBarUI mbui;
        jMenuBar1.setBorder(null);
        jMenuBar1.setBorderPainted(false);
        jMenuBar1.setBackground(Color.white);
        //jMenuBar1.setUI(mbui);

        //jMenuBar1.setFocusPainted(false);
        //jMenuBar1.setContentAreaFilled(false);
    }

    private void hideColumns(String columnName) {
        TableColumnModel tcm = jTableShowMetrics.getColumnModel();
        int index = tcm.getColumnIndex(columnName);
        TableColumn column = tcm.getColumn(index);
        hiddenColumns.put(columnName, column);
        hiddenColumns.put(":" + columnName, new Integer(index));
        tcm.removeColumn(column);
    }

    private void showColumns(String columnName) {
        TableColumnModel tcm = jTableShowMetrics.getColumnModel();
        Object o = hiddenColumns.remove(columnName);
        if (o == null) {
            return;
        }
        tcm.addColumn((TableColumn) o);
        o = hiddenColumns.remove(":" + columnName);
        if (o == null) {
            return;
        }
        int column = ((Integer) o).intValue();
        int lastColumn = tcm.getColumnCount() - 1;
        if (column < lastColumn) {
            tcm.moveColumn(lastColumn, column);
        }
    }

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
            java.util.logging.Logger.getLogger(Time_Review.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Time_Review.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Time_Review.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Time_Review.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Time_Review().setVisible(true);
                } catch (ParseException ex) {
                    Logger.getLogger(Time_Review.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup BGFrom;
    private javax.swing.JButton jBCSVMetrics;
    private javax.swing.JButton jBClear;
    private javax.swing.JButton jBClearTask;
    private javax.swing.JButton jBDelete;
    private javax.swing.JButton jBDeleteTask;
    private javax.swing.JButton jBExportTaskCSV;
    private javax.swing.JButton jBExportUserCSV;
    private javax.swing.JButton jBGenerateCSV;
    private javax.swing.JButton jBNetDelete;
    private javax.swing.JButton jBNetSearch;
    private javax.swing.JButton jBNetworkSearch;
    private javax.swing.JButton jBSaveNet;
    private javax.swing.JButton jBSaveNewUser;
    private javax.swing.JButton jBSaveTask;
    private javax.swing.JButton jBSearch;
    private javax.swing.JButton jBSearchTask;
    private javax.swing.JButton jBShowPreview;
    private javax.swing.JComboBox<String> jCBAccess;
    private javax.swing.JComboBox<String> jCBAction;
    private javax.swing.JComboBox<String> jCBCustomerUnit;
    private javax.swing.JComboBox<String> jCBFrom;
    private javax.swing.JComboBox<String> jCBMetricTeam;
    private javax.swing.JComboBox<String> jCBNetAction;
    private javax.swing.JComboBox<String> jCBNetSearch;
    private javax.swing.JComboBox<String> jCBOrganization;
    private javax.swing.JComboBox<String> jCBOtherCU;
    private javax.swing.JComboBox<String> jCBSupportedCU;
    private javax.swing.JComboBox<String> jCBTaskEdit;
    private javax.swing.JComboBox<String> jCBTaskSearch;
    private javax.swing.JComboBox<String> jCBTaskTeam;
    private javax.swing.JComboBox<String> jCBTaskType;
    private javax.swing.JComboBox<String> jCBTeam;
    private javax.swing.JComboBox<String> jCBTo;
    private javax.swing.JComboBox<String> jCBUser;
    private javax.swing.JDialog jDLoading;
    private javax.swing.JLabel jLAccess;
    private javax.swing.JLabel jLCUSupported;
    private javax.swing.JLabel jLChoose;
    private javax.swing.JLabel jLFileName;
    private javax.swing.JLabel jLFrom;
    private javax.swing.JLabel jLLoading;
    private javax.swing.JLabel jLNetAction;
    private javax.swing.JLabel jLNetworkSearch;
    private javax.swing.JLabel jLOtherCU;
    private javax.swing.JLabel jLSignum;
    private javax.swing.JLabel jLSignumEdit;
    private javax.swing.JLabel jLTask;
    private javax.swing.JLabel jLTaskSearch;
    private javax.swing.JLabel jLTaskType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuItem jMEditNetworks;
    private javax.swing.JMenuItem jMEditTask;
    private javax.swing.JMenuItem jMEditUsers;
    private javax.swing.JMenuItem jMRecordMetrics;
    private javax.swing.JMenuItem jMReview;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenu jMenuView;
    private javax.swing.JPanel jPEdit;
    private javax.swing.JPanel jPEditTask;
    private javax.swing.JPanel jPNetEdit;
    private javax.swing.JPanel jPNetSearch;
    private javax.swing.JPanel jPNetworks;
    private javax.swing.JPanel jPSearch;
    private javax.swing.JPanel jPSearchTask;
    private javax.swing.JPanel jPTasks;
    private javax.swing.JPanel jPUser;
    private javax.swing.JPanel jPView;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelLoading;
    private javax.swing.JRadioButton jRBMonth;
    private javax.swing.JRadioButton jRBName;
    private javax.swing.JRadioButton jRBNetwork;
    private javax.swing.JRadioButton jRBQuarter;
    private javax.swing.JRadioButton jRBSAPBilling;
    private javax.swing.JRadioButton jRBSignum;
    private javax.swing.JRadioButton jRBSubnetwork;
    private javax.swing.JRadioButton jRBTask;
    private javax.swing.JRadioButton jRBTaskID;
    private javax.swing.JRadioButton jRBTeam;
    private javax.swing.JRadioButton jRBTo;
    private javax.swing.JRadioButton jRBWeek;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField jTFActivityCode;
    private javax.swing.JTextField jTFFileName;
    private javax.swing.JTextField jTFLastName;
    private javax.swing.JTextField jTFLineManager;
    private javax.swing.JTextField jTFName;
    private javax.swing.JTextField jTFNetCustomer;
    private javax.swing.JTextField jTFNetMarket;
    private javax.swing.JTextField jTFNetRegion;
    private javax.swing.JTextField jTFNetTeam;
    private javax.swing.JTextField jTFNetwork;
    private javax.swing.JTextField jTFPD;
    private javax.swing.JTextField jTFResponsible;
    private javax.swing.JTextField jTFSignum;
    private javax.swing.JTextField jTFSignumEdit;
    private javax.swing.JTextField jTFSubnetwork;
    private javax.swing.JTextField jTFTaskName;
    private javax.swing.JTable jTTasksList;
    private javax.swing.JTable jTUsersList;
    private javax.swing.JTable jTableShowMetrics;
    // End of variables declaration//GEN-END:variables
}
