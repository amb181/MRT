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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import static metrics.Metrics.usersinfo;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import java.awt.Desktop;
import java.util.stream.Collectors;

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
    int[] days = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    String tid;
    List<String> tasks = new ArrayList<String>();
    List<String> tasktypes = new ArrayList<String>();
    List<String> teams = new ArrayList<String>();
    List<String> organizations = new ArrayList<String>();
    List<String> cus = new ArrayList<String>();
    List<String> user_teams = new ArrayList<String>();
    List<String> user_names = new ArrayList<String>();
    List<String> act_codes = new ArrayList<String>();
    List<String> regions = new ArrayList<String>();
    List<String> markets = new ArrayList<String>();
    List<String> netTeams = new ArrayList<String>();
    List<String> technologies = new ArrayList<String>();
    List<String> networks = new ArrayList<String>();
    String[] LMSignums = {"QIVAALC", "EJOSEBL", "TIMAFCC", "EMAUHER", "EOSCLUG", "EPANVIC"};
    String[] LMOrganizations = {"BNEW SAN SAB SDU MX MANA Project Supp 5", "BNEW SAN SAB SDU MX MANA", "BNEW SAN SAB SDU MX MANA Project Supp 1", "BNEW SAN SAB SDU MX MANA Project Supp 4", "BNEW SAN SAB SDU MX MANA Project Supp 2", "BNEW SAN SAB SDU MX MANA Project Supp 3"};
    String[] ActTypes = {"6000", "6001", "6002", "N/A"};

    ArrayList<String> teamsAndCUs = new ArrayList<>();
    ArrayList<String> taskIDandTasks = new ArrayList<>();
    ArrayList<String> netTeamsAndCUs = new ArrayList<>();
    ArrayList<String> netTeamCURegMark = new ArrayList<>(); // Team, CU, Reg, Market
    ArrayList<String> netMrktTech = new ArrayList<>(); // Market, Technology
    ArrayList<String> netwTeamSubn = new ArrayList<>(); // Team, Customer, Market, Network, Subnetwork
    ArrayList<String> marketTeamCU = new ArrayList<>(); //id, Market, Region, Team, CU
    ArrayList<String> servicePackageNames = new ArrayList<>();
    ArrayList<String> deliverableList = new ArrayList<>();
    ArrayList<String> projectSupportNames = new ArrayList<>();
    ArrayList<String> teamsCUSTasks = new ArrayList<>();

    /**
     * Creates new form Time_Review
     */
    public Time_Review() throws ParseException {
        initComponents();
        this.setExtendedState(this.MAXIMIZED_BOTH);
        setLookAndFeel();
        //this.setSize(jPView.getPreferredSize());
        this.setIconImage(new ImageIcon(getClass().getResource("/images/MRT_logo.png")).getImage());
        this.setTitle("MRT - Audit & Report - Review Metrics");
        jDLoading.setSize(350, 150);
        jDLoading.setTitle("MRT - Connecting to server");
        jDLoading.setIconImage(new ImageIcon(getClass().getResource("/images/MRT_logo.png")).getImage());
        jDLoading.setLocationRelativeTo(null);
        //setResizable(false);
        hiddenColumns = new HashMap();
        Date date = new Date();
        SimpleDateFormat dcn = new SimpleDateFormat("yyyy-MM-dd");
        String date_change = dcn.format(date);
        Date date_ = dcn.parse(date_change);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date_);
        week = cal.get(Calendar.WEEK_OF_YEAR) - 1;
        //System.out.println("Current week: " + week + ". Current year: " + Calendar.getInstance().get(Calendar.YEAR));
        //week = 52;
        for (int i = 1; i <= 52; i++) {
            jCBFrom.addItem(String.valueOf(i));  
        }
        System.out.println("Number of items: " + jCBFrom.getItemCount());
        //jCBFrom.setSelectedIndex(week);
        jCBYearFrom.setSelectedItem(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        jCBYearTo.setSelectedItem(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        
        jTableShowMetrics.setEnabled(false);
        jTUsersList.setEnabled(false);
        jTTasksList.setEnabled(false);
        jTableNetworks.setEnabled(false);
        jPEdit.setVisible(false);
        jPView.setVisible(true);
        jPSearch.setVisible(false);
        jPEditTask.setVisible(false);
        jPSearchTask.setVisible(false);
        jPNetworks.setVisible(false);
        jPMarket.setVisible(false);
        jPNetSearch.setVisible(false);
        jPSearchMrkt.setVisible(false);
        jTFSupTeam.setEditable(false);
        jTFSupCU.setEditable(false);
        jCBYearTo.setEnabled(false);
        AutoCompleteDecorator.decorate(jCBTaskSearch);
        AutoCompleteDecorator.decorate(jCBLineManager);
        AutoCompleteDecorator.decorate(jCBTeam);
        AutoCompleteDecorator.decorate(jCBCustomerUnit);
        AutoCompleteDecorator.decorate(jCBSupportedCU);
        AutoCompleteDecorator.decorate(jCBSupportedTeam);
        AutoCompleteDecorator.decorate(jCBTaskTeam);
        AutoCompleteDecorator.decorate(jCBUser);
        AutoCompleteDecorator.decorate(jCBMetricTeam);
        AutoCompleteDecorator.decorate(jCBNetTeam);
        AutoCompleteDecorator.decorate(jCBNetCustomer);
        AutoCompleteDecorator.decorate(jCBNetRegion);
        AutoCompleteDecorator.decorate(jCBNetMarket);
        AutoCompleteDecorator.decorate(jCBNetTech);
        AutoCompleteDecorator.decorate(jCBNetTeamSearch);
        AutoCompleteDecorator.decorate(jCBCUSearch);
        AutoCompleteDecorator.decorate(jCBMarketSearch);
        AutoCompleteDecorator.decorate(jCBNetSearch);
        AutoCompleteDecorator.decorate(jCBNetActCode);
        AutoCompleteDecorator.decorate(jCBOrgMetrics);
        AutoCompleteDecorator.decorate(jCBSearchUser);
        AutoCompleteDecorator.decorate(jCBServicePN);
        AutoCompleteDecorator.decorate(jCBDeliverable);
        AutoCompleteDecorator.decorate(jCBProjectSuppDom);
        AutoCompleteDecorator.decorate(jCBMarketList);
        jLLoading.setText("Initializing...");
        jDLoading.setVisible(true);
        jDLoading.setModal(false);
        jDLoading.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        GetTaskTypes();
        GetAllUsers();
        GetAllTasks();
        GetNetworksSearch();
        GetAllMarkets();
        jDLoading.dispose();
        JTableHeader header = jTableShowMetrics.getTableHeader();
        JTableHeader header1 = jTUsersList.getTableHeader();
        JTableHeader header2 = jTTasksList.getTableHeader();
        JTableHeader header3 = jTableNetworks.getTableHeader();
        header.setBackground(new Color(0, 130, 240));
        header.setForeground(Color.white);
        header1.setBackground(new Color(0, 130, 240));
        header1.setForeground(Color.white);
        header2.setBackground(new Color(0, 130, 240));
        header2.setForeground(Color.white);
        header3.setBackground(new Color(0, 130, 240));
        header3.setForeground(Color.white);
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
        jLYear = new javax.swing.JLabel();
        jCBYearFrom = new javax.swing.JComboBox<>();
        jLYearTo = new javax.swing.JLabel();
        jCBYearTo = new javax.swing.JComboBox<>();
        jBShowMetrics = new javax.swing.JButton();
        jLMetricTeams = new javax.swing.JLabel();
        jLMetricOrg = new javax.swing.JLabel();
        jCBOrgMetrics = new javax.swing.JComboBox<>();
        jLabel39 = new javax.swing.JLabel();
        jPEdit = new javax.swing.JPanel();
        jPUser = new javax.swing.JPanel();
        jLSignum = new javax.swing.JLabel();
        jTFSignum = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jTFName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTFLastName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jCBTeam = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jCBCustomerUnit = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jBSaveNewUser = new javax.swing.JButton();
        jLAccess = new javax.swing.JLabel();
        jCBAccess = new javax.swing.JComboBox<>();
        jLTeamSupp = new javax.swing.JLabel();
        jCBSupportedCU = new javax.swing.JComboBox<>();
        jLCUSupported = new javax.swing.JLabel();
        jCBSupportedTeam = new javax.swing.JComboBox<>();
        jBAddSupCU = new javax.swing.JButton();
        jBDeleteCUSupp = new javax.swing.JButton();
        jLSuppTeams = new javax.swing.JLabel();
        jLSuppCUs = new javax.swing.JLabel();
        jTFSupTeam = new javax.swing.JTextField();
        jTFSupCU = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jCBJobStage = new javax.swing.JComboBox<>();
        jLabel37 = new javax.swing.JLabel();
        jTFCATNum = new javax.swing.JTextField();
        jCBLineManager = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jCBAction = new javax.swing.JComboBox<>();
        jPSearch = new javax.swing.JPanel();
        jBDelete = new javax.swing.JButton();
        jBClear = new javax.swing.JButton();
        jBSearch = new javax.swing.JButton();
        jLSignumEdit = new javax.swing.JLabel();
        jCBSearchUser = new javax.swing.JComboBox<>();
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
        jLTaskSearch1 = new javax.swing.JLabel();
        jCBTeamTaskSearch = new javax.swing.JComboBox<>();
        jLabel38 = new javax.swing.JLabel();
        jCBCUTaskSearch = new javax.swing.JComboBox<>();
        jPTasks = new javax.swing.JPanel();
        jLTaskType = new javax.swing.JLabel();
        jCBTaskType = new javax.swing.JComboBox<>();
        jLTask = new javax.swing.JLabel();
        jTFTaskName = new javax.swing.JTextField();
        jBSaveTask = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jCBTaskTeam = new javax.swing.JComboBox<>();
        jLabel21 = new javax.swing.JLabel();
        jCBTaskCU = new javax.swing.JComboBox<>();
        jLTask1 = new javax.swing.JLabel();
        jCBServicePN = new javax.swing.JComboBox<>();
        jLTask2 = new javax.swing.JLabel();
        jCBDeliverable = new javax.swing.JComboBox<>();
        jLTask3 = new javax.swing.JLabel();
        jLTask4 = new javax.swing.JLabel();
        jTFLOE = new javax.swing.JTextField();
        jCBProjectSuppDom = new javax.swing.JComboBox<>();
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
        jBNetworkSearch = new javax.swing.JButton();
        jBNetSearch = new javax.swing.JButton();
        jBNetDelete = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        jCBNetSearch = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        jCBNetTeamSearch = new javax.swing.JComboBox<>();
        jLabel25 = new javax.swing.JLabel();
        jCBCUSearch = new javax.swing.JComboBox<>();
        jLabel26 = new javax.swing.JLabel();
        jCBMarketSearch = new javax.swing.JComboBox<>();
        jPNetEdit = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jTFPD = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTFNetwork = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jTFResponsible = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jTFSubnetwork = new javax.swing.JTextField();
        jBSaveNet = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jCBNetActCode = new javax.swing.JComboBox<>();
        jCBNetTeam = new javax.swing.JComboBox<>();
        jCBNetRegion = new javax.swing.JComboBox<>();
        jCBNetMarket = new javax.swing.JComboBox<>();
        jCBNetCustomer = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        jCBNetTech = new javax.swing.JComboBox<>();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableNetworks = new javax.swing.JTable(){
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
        jBNetTableCSV = new javax.swing.JButton();
        jPMarket = new javax.swing.JPanel();
        jPSearchMrkt = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jCBMarTeam = new javax.swing.JComboBox<>();
        jLabel29 = new javax.swing.JLabel();
        jCBMarCU = new javax.swing.JComboBox<>();
        jLabel30 = new javax.swing.JLabel();
        jCBMarRegion = new javax.swing.JComboBox<>();
        jLabel31 = new javax.swing.JLabel();
        jCBMarMrkt = new javax.swing.JComboBox<>();
        jBSearchMrkt = new javax.swing.JButton();
        jCBDeleteMrkt = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        jCBMrktAction = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jCBRegionMrkt = new javax.swing.JComboBox<>();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jBSaveMrkt = new javax.swing.JButton();
        jCBTeamMrkt = new javax.swing.JComboBox<>();
        jLabel35 = new javax.swing.JLabel();
        jCBCUMrkt = new javax.swing.JComboBox<>();
        jCBMarketList = new javax.swing.JComboBox<>();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuView = new javax.swing.JMenu();
        jMReview = new javax.swing.JMenuItem();
        jMenuTeams = new javax.swing.JMenu();
        jMICOP = new javax.swing.JMenuItem();
        jMIPSS = new javax.swing.JMenuItem();
        jMIScoping = new javax.swing.JMenuItem();
        jMISourcing = new javax.swing.JMenuItem();
        jMIVSS = new javax.swing.JMenuItem();
        jMenuEdit = new javax.swing.JMenu();
        jMEditUsers = new javax.swing.JMenuItem();
        jMEditTask = new javax.swing.JMenuItem();
        jMEditNetworks = new javax.swing.JMenuItem();
        jMEditMarkets = new javax.swing.JMenuItem();

        jPanelLoading.setBackground(new java.awt.Color(255, 255, 255));
        jPanelLoading.setPreferredSize(new java.awt.Dimension(365, 334));

        jLLoading.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLLoading.setText("Saving into the database...");

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Loading.gif"))); // NOI18N

        javax.swing.GroupLayout jPanelLoadingLayout = new javax.swing.GroupLayout(jPanelLoading);
        jPanelLoading.setLayout(jPanelLoadingLayout);
        jPanelLoadingLayout.setHorizontalGroup(
            jPanelLoadingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLLoading, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
        );
        jPanelLoadingLayout.setVerticalGroup(
            jPanelLoadingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLoadingLayout.createSequentialGroup()
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLLoading)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jDLoadingLayout = new javax.swing.GroupLayout(jDLoading.getContentPane());
        jDLoading.getContentPane().setLayout(jDLoadingLayout);
        jDLoadingLayout.setHorizontalGroup(
            jDLoadingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDLoadingLayout.createSequentialGroup()
                .addComponent(jPanelLoading, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jDLoadingLayout.setVerticalGroup(
            jDLoadingLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelLoading, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(1400, 915));
        setPreferredSize(new java.awt.Dimension(1493, 718));

        jPView.setBackground(new java.awt.Color(255, 255, 255));
        jPView.setMaximumSize(new java.awt.Dimension(1400, 915));
        jPView.setPreferredSize(new java.awt.Dimension(1917, 915));

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));

        jTableShowMetrics.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
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
        jRBSubnetwork.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jRBSubnetwork.setSelected(true);
        jRBSubnetwork.setText("Subnetwork");

        jRBSignum.setBackground(new java.awt.Color(255, 255, 255));
        jRBSignum.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jRBSignum.setSelected(true);
        jRBSignum.setText("Signum");

        jRBTaskID.setBackground(new java.awt.Color(255, 255, 255));
        jRBTaskID.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jRBTaskID.setSelected(true);
        jRBTaskID.setText("Task ID");
        jRBTaskID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBTaskIDActionPerformed(evt);
            }
        });

        jRBTask.setBackground(new java.awt.Color(255, 255, 255));
        jRBTask.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jRBTask.setSelected(true);
        jRBTask.setText("Task");

        jRBNetwork.setBackground(new java.awt.Color(255, 255, 255));
        jRBNetwork.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jRBNetwork.setSelected(true);
        jRBNetwork.setText("Network");

        jRBSAPBilling.setBackground(new java.awt.Color(255, 255, 255));
        jRBSAPBilling.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jRBSAPBilling.setSelected(true);
        jRBSAPBilling.setText("SAP Billing");

        jBShowPreview.setBackground(new java.awt.Color(0, 74, 173));
        jBShowPreview.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBShowPreview.setForeground(new java.awt.Color(255, 255, 255));
        jBShowPreview.setText("Show filtered data");
        jBShowPreview.setActionCommand("Show Preview");
        jBShowPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBShowPreviewActionPerformed(evt);
            }
        });

        jRBName.setBackground(new java.awt.Color(255, 255, 255));
        jRBName.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jRBName.setSelected(true);
        jRBName.setText("Name");
        jRBName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBNameActionPerformed(evt);
            }
        });

        jBGenerateCSV.setBackground(new java.awt.Color(0, 130, 240));
        jBGenerateCSV.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBGenerateCSV.setForeground(new java.awt.Color(255, 255, 255));
        jBGenerateCSV.setText("Export table");
        jBGenerateCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBGenerateCSVActionPerformed(evt);
            }
        });

        jLFileName.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLFileName.setText("File Name:");

        jTFFileName.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jCBUser.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBUser.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));

        jCBMetricTeam.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBMetricTeam.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Sourcing", "COP", "VSS", "PSS" }));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 130, 240), 1, true));

        jRBWeek.setBackground(new java.awt.Color(255, 255, 255));
        BGFrom.add(jRBWeek);
        jRBWeek.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jRBWeek.setSelected(true);
        jRBWeek.setText("Week");
        jRBWeek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBWeekActionPerformed(evt);
            }
        });

        jRBMonth.setBackground(new java.awt.Color(255, 255, 255));
        BGFrom.add(jRBMonth);
        jRBMonth.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jRBMonth.setText("Month");
        jRBMonth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBMonthActionPerformed(evt);
            }
        });

        jRBQuarter.setBackground(new java.awt.Color(255, 255, 255));
        BGFrom.add(jRBQuarter);
        jRBQuarter.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jRBQuarter.setText("Quarter");
        jRBQuarter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBQuarterActionPerformed(evt);
            }
        });

        jLFrom.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLFrom.setText("From:");

        jRBTo.setBackground(new java.awt.Color(255, 255, 255));
        jRBTo.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jRBTo.setText("To:");
        jRBTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBToActionPerformed(evt);
            }
        });

        jCBFrom.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBFrom.setToolTipText("");
        jCBFrom.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 255, 255)));
        jCBFrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBFromActionPerformed(evt);
            }
        });

        jCBTo.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBTo.setToolTipText("");
        jCBTo.setEnabled(false);
        jCBTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBToActionPerformed(evt);
            }
        });

        jLYear.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLYear.setText("Year: ");

        jCBYearFrom.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2020", "2021" }));

        jLYearTo.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLYearTo.setText("Year: ");

        jCBYearTo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2020", "2021" }));

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
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLYear)
                        .addGap(18, 18, 18)
                        .addComponent(jCBYearFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLYearTo)
                        .addGap(18, 18, 18)
                        .addComponent(jCBYearTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(34, Short.MAX_VALUE))
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
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jCBFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLYear)
                            .addComponent(jCBYearFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jCBTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLYearTo)
                            .addComponent(jCBYearTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 6, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jBShowMetrics.setBackground(new java.awt.Color(0, 74, 173));
        jBShowMetrics.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBShowMetrics.setForeground(new java.awt.Color(255, 255, 255));
        jBShowMetrics.setText("Show all metrics");
        jBShowMetrics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBShowMetricsActionPerformed(evt);
            }
        });

        jLMetricTeams.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLMetricTeams.setText("Team:");

        jLMetricOrg.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLMetricOrg.setText("Organization:");

        jCBOrgMetrics.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLabel39.setFont(new java.awt.Font("Ericsson Hilda", 0, 14)); // NOI18N
        jLabel39.setText("Filters");

        javax.swing.GroupLayout jPViewLayout = new javax.swing.GroupLayout(jPView);
        jPView.setLayout(jPViewLayout);
        jPViewLayout.setHorizontalGroup(
            jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPViewLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPViewLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1905, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPViewLayout.createSequentialGroup()
                        .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLMetricOrg)
                            .addGroup(jPViewLayout.createSequentialGroup()
                                .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jRBName)
                                    .addComponent(jLMetricTeams))
                                .addGap(55, 55, 55)
                                .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jCBUser, 0, 180, Short.MAX_VALUE)
                                    .addComponent(jCBMetricTeam, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jCBOrgMetrics, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(18, 18, 18)
                        .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPViewLayout.createSequentialGroup()
                                .addComponent(jRBNetwork)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jRBSubnetwork)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRBSAPBilling)
                                .addGap(32, 32, 32))
                            .addGroup(jPViewLayout.createSequentialGroup()
                                .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPViewLayout.createSequentialGroup()
                                        .addComponent(jRBSignum)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jRBTask)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jRBTaskID))
                                    .addComponent(jLabel39))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jBShowMetrics, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jBShowPreview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLFileName)
                        .addGap(18, 18, 18)
                        .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTFFileName, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jBGenerateCSV))
                        .addGap(151, 151, 151))))
        );
        jPViewLayout.setVerticalGroup(
            jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPViewLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPViewLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29))
                    .addGroup(jPViewLayout.createSequentialGroup()
                        .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPViewLayout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jRBName)
                                    .addComponent(jCBUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel39))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jRBSignum)
                                        .addComponent(jRBTask)
                                        .addComponent(jRBTaskID))
                                    .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jCBMetricTeam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLMetricTeams)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPViewLayout.createSequentialGroup()
                                .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jBShowPreview)
                                    .addComponent(jTFFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLFileName))
                                .addGap(18, 18, 18)
                                .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jBShowMetrics)
                                    .addComponent(jBGenerateCSV))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                                .addGroup(jPViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLMetricOrg)
                                    .addComponent(jCBOrgMetrics, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jRBSAPBilling)
                                    .addComponent(jRBNetwork)
                                    .addComponent(jRBSubnetwork))))
                        .addGap(18, 18, 18)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 744, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPEdit.setBackground(new java.awt.Color(255, 255, 255));
        jPEdit.setMaximumSize(new java.awt.Dimension(1400, 915));
        jPEdit.setPreferredSize(new java.awt.Dimension(1493, 718));

        jPUser.setBackground(new java.awt.Color(255, 255, 255));
        jPUser.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLSignum.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLSignum.setText("Signum:");

        jTFSignum.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel1.setText("Name:");

        jTFName.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel2.setText("Last Name:");

        jTFLastName.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel4.setText("Team:");

        jCBTeam.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBTeam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBTeamActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel5.setText("Customer Unit:");

        jCBCustomerUnit.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBCustomerUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBCustomerUnitActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel6.setText("Line Manager:");

        jBSaveNewUser.setBackground(new java.awt.Color(0, 130, 240));
        jBSaveNewUser.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBSaveNewUser.setForeground(new java.awt.Color(255, 255, 255));
        jBSaveNewUser.setText("Save");
        jBSaveNewUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSaveNewUserActionPerformed(evt);
            }
        });

        jLAccess.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLAccess.setText("Access:");

        jCBAccess.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBAccess.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Regular", "Admin" }));

        jLTeamSupp.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTeamSupp.setText("Another Team:");

        jCBSupportedCU.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBSupportedCU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBSupportedCUActionPerformed(evt);
            }
        });

        jLCUSupported.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLCUSupported.setText("Another CU:");

        jCBSupportedTeam.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBSupportedTeam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBSupportedTeamActionPerformed(evt);
            }
        });

        jBAddSupCU.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBAddSupCU.setText("Add Supported CU");
        jBAddSupCU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBAddSupCUActionPerformed(evt);
            }
        });

        jBDeleteCUSupp.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBDeleteCUSupp.setText("Delete Supported CU");
        jBDeleteCUSupp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBDeleteCUSuppActionPerformed(evt);
            }
        });

        jLSuppTeams.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLSuppTeams.setText("Supported Teams:");

        jLSuppCUs.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLSuppCUs.setText("Supported CUs:");

        jTFSupTeam.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jTFSupCU.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLabel36.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel36.setText("Job Stage:");

        jCBJobStage.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBJobStage.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "3", "4", "5", "N/A" }));

        jLabel37.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel37.setText("CAT's Number:");

        jTFCATNum.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jTFCATNum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTFCATNumActionPerformed(evt);
            }
        });

        jCBLineManager.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBLineManager.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Alcala Elizalde Ivan Gerardo", "Blanco Jose Alberto", "Campos Aldo", "Hernandez Mauricio", "Lugo Oscar", "Pantoja Victor" }));

        javax.swing.GroupLayout jPUserLayout = new javax.swing.GroupLayout(jPUser);
        jPUser.setLayout(jPUserLayout);
        jPUserLayout.setHorizontalGroup(
            jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPUserLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPUserLayout.createSequentialGroup()
                        .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLCUSupported)
                            .addComponent(jLTeamSupp))
                        .addGap(22, 22, 22)
                        .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jCBSupportedTeam, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jCBSupportedCU, 0, 226, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jBDeleteCUSupp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jBAddSupCU, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLSuppTeams)
                            .addComponent(jLSuppCUs))
                        .addGap(18, 18, 18)
                        .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTFSupTeam)
                            .addComponent(jTFSupCU, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jBSaveNewUser))
                    .addGroup(jPUserLayout.createSequentialGroup()
                        .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPUserLayout.createSequentialGroup()
                                .addComponent(jLSignum)
                                .addGap(59, 59, 59)
                                .addComponent(jTFSignum, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(jTFName, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPUserLayout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCBLineManager, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(56, 56, 56)
                        .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPUserLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(jTFLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel37)
                                .addGap(18, 18, 18)
                                .addComponent(jTFCATNum, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPUserLayout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addComponent(jCBTeam, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(jCBCustomerUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel36)
                                .addGap(18, 18, 18)
                                .addComponent(jCBJobStage, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPUserLayout.createSequentialGroup()
                        .addComponent(jLAccess)
                        .addGap(18, 18, 18)
                        .addComponent(jCBAccess, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPUserLayout.setVerticalGroup(
            jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPUserLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLSignum)
                    .addComponent(jTFSignum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jTFName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jTFLastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37)
                    .addComponent(jTFCATNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(jCBLineManager, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(jCBTeam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(jCBCustomerUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel36)
                        .addComponent(jCBJobStage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLAccess)
                    .addComponent(jCBAccess, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLTeamSupp)
                    .addComponent(jCBSupportedTeam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBAddSupCU)
                    .addComponent(jLSuppTeams)
                    .addComponent(jTFSupTeam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLCUSupported)
                    .addComponent(jCBSupportedCU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBDeleteCUSupp)
                    .addComponent(jLSuppCUs)
                    .addComponent(jTFSupCU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBSaveNewUser))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel7.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel7.setText("Choose action:");

        jCBAction.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBAction.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Add new user", "Edit user" }));
        jCBAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBActionActionPerformed(evt);
            }
        });

        jPSearch.setBackground(new java.awt.Color(255, 255, 255));

        jBDelete.setBackground(new java.awt.Color(199, 64, 56));
        jBDelete.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBDelete.setForeground(new java.awt.Color(255, 255, 255));
        jBDelete.setText("Delete");
        jBDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBDeleteActionPerformed(evt);
            }
        });

        jBClear.setBackground(new java.awt.Color(255, 255, 255));
        jBClear.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBClear.setText("Clear");
        jBClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBClearActionPerformed(evt);
            }
        });

        jBSearch.setBackground(new java.awt.Color(0, 130, 240));
        jBSearch.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBSearch.setForeground(new java.awt.Color(255, 255, 255));
        jBSearch.setText("Search");
        jBSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSearchActionPerformed(evt);
            }
        });

        jLSignumEdit.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLSignumEdit.setText("Name:");

        jCBSearchUser.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        javax.swing.GroupLayout jPSearchLayout = new javax.swing.GroupLayout(jPSearch);
        jPSearch.setLayout(jPSearchLayout);
        jPSearchLayout.setHorizontalGroup(
            jPSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLSignumEdit)
                .addGap(18, 18, 18)
                .addComponent(jCBSearchUser, 0, 188, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jBSearch)
                .addGap(18, 18, 18)
                .addComponent(jBClear)
                .addGap(53, 53, 53)
                .addComponent(jBDelete)
                .addContainerGap())
        );
        jPSearchLayout.setVerticalGroup(
            jPSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPSearchLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLSignumEdit)
                    .addComponent(jBSearch)
                    .addComponent(jBClear)
                    .addComponent(jBDelete)
                    .addComponent(jCBSearchUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTUsersList.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
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
        jBExportUserCSV.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBExportUserCSV.setForeground(new java.awt.Color(255, 255, 255));
        jBExportUserCSV.setText("Export to CSV file");
        jBExportUserCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBExportUserCSVActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel18.setText("Users in the database");

        javax.swing.GroupLayout jPEditLayout = new javax.swing.GroupLayout(jPEdit);
        jPEdit.setLayout(jPEditLayout);
        jPEditLayout.setHorizontalGroup(
            jPEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPEditLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPEditLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(jCBAction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPEditLayout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addGap(18, 18, 18)
                        .addComponent(jBExportUserCSV))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1911, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPEditLayout.setVerticalGroup(
            jPEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPEditLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jCBAction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addComponent(jPSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jBExportUserCSV))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPEditTask.setBackground(new java.awt.Color(255, 255, 255));
        jPEditTask.setMaximumSize(new java.awt.Dimension(1493, 718));
        jPEditTask.setPreferredSize(new java.awt.Dimension(1450, 912));

        jLChoose.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLChoose.setText("Choose action:");

        jCBTaskEdit.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBTaskEdit.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Add a new task", "Edit existing task" }));
        jCBTaskEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBTaskEditActionPerformed(evt);
            }
        });

        jPSearchTask.setBackground(new java.awt.Color(255, 255, 255));

        jBDeleteTask.setBackground(new java.awt.Color(199, 64, 56));
        jBDeleteTask.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBDeleteTask.setForeground(new java.awt.Color(255, 255, 255));
        jBDeleteTask.setText("Delete");
        jBDeleteTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBDeleteTaskActionPerformed(evt);
            }
        });

        jBClearTask.setBackground(new java.awt.Color(255, 255, 255));
        jBClearTask.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBClearTask.setText("Clear");
        jBClearTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBClearTaskActionPerformed(evt);
            }
        });

        jLTaskSearch.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTaskSearch.setText("Task:");

        jBSearchTask.setBackground(new java.awt.Color(0, 130, 240));
        jBSearchTask.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBSearchTask.setForeground(new java.awt.Color(255, 255, 255));
        jBSearchTask.setText("Search");
        jBSearchTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSearchTaskActionPerformed(evt);
            }
        });

        jCBTaskSearch.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBTaskSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBTaskSearchActionPerformed(evt);
            }
        });

        jLTaskSearch1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTaskSearch1.setText("Team:");

        jCBTeamTaskSearch.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBTeamTaskSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBTeamTaskSearchActionPerformed(evt);
            }
        });

        jLabel38.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel38.setText("CU:");

        jCBCUTaskSearch.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBCUTaskSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBCUTaskSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPSearchTaskLayout = new javax.swing.GroupLayout(jPSearchTask);
        jPSearchTask.setLayout(jPSearchTaskLayout);
        jPSearchTaskLayout.setHorizontalGroup(
            jPSearchTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPSearchTaskLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPSearchTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPSearchTaskLayout.createSequentialGroup()
                        .addGroup(jPSearchTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLTaskSearch1)
                            .addComponent(jLTaskSearch))
                        .addGap(18, 18, 18)
                        .addGroup(jPSearchTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPSearchTaskLayout.createSequentialGroup()
                                .addComponent(jCBTeamTaskSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel38)
                                .addGap(18, 18, 18)
                                .addComponent(jCBCUTaskSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jCBTaskSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 505, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPSearchTaskLayout.createSequentialGroup()
                        .addComponent(jBSearchTask)
                        .addGap(18, 18, 18)
                        .addComponent(jBClearTask)
                        .addGap(18, 18, 18)
                        .addComponent(jBDeleteTask)))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        jPSearchTaskLayout.setVerticalGroup(
            jPSearchTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPSearchTaskLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPSearchTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLTaskSearch1)
                    .addComponent(jCBTeamTaskSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38)
                    .addComponent(jCBCUTaskSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPSearchTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLTaskSearch)
                    .addComponent(jCBTaskSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPSearchTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBDeleteTask)
                    .addComponent(jBClearTask)
                    .addComponent(jBSearchTask))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPTasks.setBackground(new java.awt.Color(255, 255, 255));
        jPTasks.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLTaskType.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTaskType.setText("Task Type:");

        jCBTaskType.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLTask.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTask.setText("Task:");

        jTFTaskName.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jBSaveTask.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBSaveTask.setText("Save");
        jBSaveTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSaveTaskActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel14.setText("Team:");

        jCBTaskTeam.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBTaskTeam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBTaskTeamActionPerformed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel21.setText("CU:");

        jCBTaskCU.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLTask1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTask1.setText("Deliverable:");

        jCBServicePN.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLTask2.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTask2.setText("Service Package Name:");

        jCBDeliverable.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLTask3.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTask3.setText("Project Support Domain:");

        jLTask4.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTask4.setText("Level of Effort:");

        jTFLOE.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jCBProjectSuppDom.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

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
                                .addComponent(jLTask)
                                .addGap(18, 18, 18)
                                .addComponent(jTFTaskName, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPTasksLayout.createSequentialGroup()
                                .addGroup(jPTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLTask2)
                                    .addComponent(jLTask1)
                                    .addComponent(jLTask3)
                                    .addComponent(jLTask4))
                                .addGap(18, 18, 18)
                                .addGroup(jPTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jCBServicePN, 0, 353, Short.MAX_VALUE)
                                    .addComponent(jCBDeliverable, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jCBProjectSuppDom, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jTFLOE)))
                            .addGroup(jPTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPTasksLayout.createSequentialGroup()
                                    .addComponent(jLTaskType)
                                    .addGap(18, 18, 18)
                                    .addComponent(jCBTaskType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPTasksLayout.createSequentialGroup()
                                    .addComponent(jLabel14)
                                    .addGap(18, 18, 18)
                                    .addComponent(jCBTaskTeam, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(jLabel21)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jCBTaskCU, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 15, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPTasksLayout.setVerticalGroup(
            jPTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPTasksLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jCBTaskTeam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(jCBTaskCU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLTaskType)
                    .addComponent(jCBTaskType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLTask)
                    .addComponent(jTFTaskName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLTask2)
                    .addComponent(jCBServicePN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLTask1)
                    .addComponent(jCBDeliverable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLTask3)
                    .addComponent(jCBProjectSuppDom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLTask4)
                    .addComponent(jTFLOE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addComponent(jBSaveTask)
                .addContainerGap())
        );

        jTTasksList.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
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

        jLabel19.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel19.setText("Tasks in the database");

        jBExportTaskCSV.setBackground(new java.awt.Color(0, 74, 173));
        jBExportTaskCSV.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
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
                .addGroup(jPEditTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPEditTaskLayout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jBExportTaskCSV))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1285, Short.MAX_VALUE))
                .addContainerGap())
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
                        .addComponent(jPTasks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 286, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 855, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPNetworks.setBackground(new java.awt.Color(255, 255, 255));
        jPNetworks.setMaximumSize(new java.awt.Dimension(1493, 718));
        jPNetworks.setPreferredSize(new java.awt.Dimension(1493, 718));
        jPNetworks.setRequestFocusEnabled(false);

        jLNetAction.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLNetAction.setText("Choose action:");

        jCBNetAction.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBNetAction.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Add a new network", "Edit an existing network" }));
        jCBNetAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBNetActionActionPerformed(evt);
            }
        });

        jPNetSearch.setBackground(new java.awt.Color(255, 255, 255));

        jBNetworkSearch.setBackground(new java.awt.Color(0, 130, 240));
        jBNetworkSearch.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBNetworkSearch.setForeground(new java.awt.Color(255, 255, 255));
        jBNetworkSearch.setText("Search");
        jBNetworkSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBNetworkSearchActionPerformed(evt);
            }
        });

        jBNetSearch.setBackground(new java.awt.Color(255, 255, 255));
        jBNetSearch.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBNetSearch.setText("Clear");
        jBNetSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBNetSearchActionPerformed(evt);
            }
        });

        jBNetDelete.setBackground(new java.awt.Color(199, 64, 56));
        jBNetDelete.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBNetDelete.setForeground(new java.awt.Color(255, 255, 255));
        jBNetDelete.setText("Delete");
        jBNetDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBNetDeleteActionPerformed(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel23.setText("Network: ");

        jCBNetSearch.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBNetSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBNetSearchActionPerformed(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel24.setText("Team:");

        jCBNetTeamSearch.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBNetTeamSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBNetTeamSearchActionPerformed(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel25.setText("Customer Unit:");

        jCBCUSearch.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBCUSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBCUSearchActionPerformed(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel26.setText("Market:");

        jCBMarketSearch.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBMarketSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBMarketSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPNetSearchLayout = new javax.swing.GroupLayout(jPNetSearch);
        jPNetSearch.setLayout(jPNetSearchLayout);
        jPNetSearchLayout.setHorizontalGroup(
            jPNetSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPNetSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCBNetTeamSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCBCUSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCBMarketSearch, 0, 388, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCBNetSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(295, 295, 295)
                .addComponent(jBNetworkSearch)
                .addGap(18, 18, 18)
                .addComponent(jBNetSearch)
                .addGap(18, 18, 18)
                .addComponent(jBNetDelete)
                .addContainerGap())
        );
        jPNetSearchLayout.setVerticalGroup(
            jPNetSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPNetSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPNetSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBNetworkSearch)
                    .addComponent(jBNetSearch)
                    .addComponent(jBNetDelete)
                    .addComponent(jCBNetSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24)
                    .addComponent(jCBNetTeamSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25)
                    .addComponent(jLabel23)
                    .addComponent(jCBCUSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26)
                    .addComponent(jCBMarketSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPNetEdit.setBackground(new java.awt.Color(255, 255, 255));
        jPNetEdit.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLabel8.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel8.setText("PD:");

        jTFPD.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel10.setText("Network: ");

        jTFNetwork.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel11.setText("Activity code:");

        jLabel12.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel12.setText("Responsible:");

        jTFResponsible.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLabel13.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel13.setText("Subnetwork:");

        jTFSubnetwork.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jBSaveNet.setBackground(new java.awt.Color(0, 130, 240));
        jBSaveNet.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBSaveNet.setForeground(new java.awt.Color(255, 255, 255));
        jBSaveNet.setText("Save");
        jBSaveNet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSaveNetActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel9.setText("Team:");

        jLabel15.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel15.setText("Region:");

        jLabel16.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel16.setText("Market:");

        jLabel17.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel17.setText("Customer Unit:");

        jCBNetActCode.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jCBNetTeam.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBNetTeam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBNetTeamActionPerformed(evt);
            }
        });

        jCBNetRegion.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBNetRegion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBNetRegionActionPerformed(evt);
            }
        });

        jCBNetMarket.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBNetMarket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBNetMarketActionPerformed(evt);
            }
        });

        jCBNetCustomer.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBNetCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBNetCustomerActionPerformed(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel22.setText("Technology:");

        jCBNetTech.setEditable(true);
        jCBNetTech.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBNetTech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBNetTechActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPNetEditLayout = new javax.swing.GroupLayout(jPNetEdit);
        jPNetEdit.setLayout(jPNetEditLayout);
        jPNetEditLayout.setHorizontalGroup(
            jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPNetEditLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPNetEditLayout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addGap(18, 18, 18)
                        .addComponent(jCBNetTech, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jBSaveNet))
                    .addGroup(jPNetEditLayout.createSequentialGroup()
                        .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPNetEditLayout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(18, 18, 18)
                                .addComponent(jTFPD, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPNetEditLayout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(18, 18, 18)
                                .addComponent(jCBNetTeam, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPNetEditLayout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(18, 18, 18)
                                .addComponent(jTFNetwork, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel11)
                                .addGap(18, 18, 18)
                                .addComponent(jCBNetActCode, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel12)
                                .addGap(18, 18, 18)
                                .addComponent(jTFResponsible, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel13))
                            .addGroup(jPNetEditLayout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addGap(18, 18, 18)
                                .addComponent(jCBNetCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jCBNetRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jCBNetMarket, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addComponent(jTFSubnetwork, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPNetEditLayout.setVerticalGroup(
            jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPNetEditLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(jTFPD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10)
                        .addComponent(jTFNetwork, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11)
                        .addComponent(jCBNetActCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel12)
                    .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTFResponsible, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13)
                        .addComponent(jTFSubnetwork, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(27, 27, 27)
                .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(jCBNetTeam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(jCBNetRegion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17)
                            .addComponent(jCBNetCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(jCBNetMarket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPNetEditLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                        .addComponent(jBSaveNet)
                        .addContainerGap())
                    .addGroup(jPNetEditLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel22)
                            .addComponent(jCBNetTech, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jTableNetworks.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jTableNetworks.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane4.setViewportView(jTableNetworks);

        jBNetTableCSV.setBackground(new java.awt.Color(0, 74, 173));
        jBNetTableCSV.setForeground(new java.awt.Color(255, 255, 255));
        jBNetTableCSV.setText("Export table to CSV");
        jBNetTableCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBNetTableCSVActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPNetworksLayout = new javax.swing.GroupLayout(jPNetworks);
        jPNetworks.setLayout(jPNetworksLayout);
        jPNetworksLayout.setHorizontalGroup(
            jPNetworksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPNetworksLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPNetworksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1826, Short.MAX_VALUE)
                    .addGroup(jPNetworksLayout.createSequentialGroup()
                        .addComponent(jLNetAction)
                        .addGap(18, 18, 18)
                        .addComponent(jCBNetAction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPNetworksLayout.createSequentialGroup()
                        .addComponent(jPNetEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jBNetTableCSV))
                    .addComponent(jPNetSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(1463, Short.MAX_VALUE))
        );
        jPNetworksLayout.setVerticalGroup(
            jPNetworksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPNetworksLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPNetworksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLNetAction)
                    .addComponent(jCBNetAction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPNetworksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPNetworksLayout.createSequentialGroup()
                        .addComponent(jPNetSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPNetEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jBNetTableCSV))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPMarket.setBackground(new java.awt.Color(255, 255, 255));
        jPMarket.setPreferredSize(new java.awt.Dimension(1917, 915));

        jPSearchMrkt.setBackground(new java.awt.Color(255, 255, 255));
        jPSearchMrkt.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel28.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel28.setText("Team: ");

        jCBMarTeam.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBMarTeam.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sourcing", "COP", "VSS", "PSS" }));
        jCBMarTeam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBMarTeamActionPerformed(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel29.setText("Customer Unit: ");

        jCBMarCU.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBMarCU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBMarCUActionPerformed(evt);
            }
        });

        jLabel30.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel30.setText("Region:");

        jCBMarRegion.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBMarRegion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBMarRegionActionPerformed(evt);
            }
        });

        jLabel31.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel31.setText("Market:");

        jCBMarMrkt.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBMarMrkt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBMarMrktActionPerformed(evt);
            }
        });

        jBSearchMrkt.setBackground(new java.awt.Color(255, 255, 255));
        jBSearchMrkt.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBSearchMrkt.setText("Search");
        jBSearchMrkt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSearchMrktActionPerformed(evt);
            }
        });

        jCBDeleteMrkt.setBackground(new java.awt.Color(199, 64, 56));
        jCBDeleteMrkt.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBDeleteMrkt.setForeground(new java.awt.Color(255, 255, 255));
        jCBDeleteMrkt.setText("Delete");
        jCBDeleteMrkt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBDeleteMrktActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPSearchMrktLayout = new javax.swing.GroupLayout(jPSearchMrkt);
        jPSearchMrkt.setLayout(jPSearchMrktLayout);
        jPSearchMrktLayout.setHorizontalGroup(
            jPSearchMrktLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPSearchMrktLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCBMarTeam, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCBMarCU, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel30)
                .addGap(18, 18, 18)
                .addComponent(jCBMarRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel31)
                .addGap(18, 18, 18)
                .addComponent(jCBMarMrkt, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPSearchMrktLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jBSearchMrkt)
                .addGap(30, 30, 30)
                .addComponent(jCBDeleteMrkt)
                .addContainerGap())
        );
        jPSearchMrktLayout.setVerticalGroup(
            jPSearchMrktLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPSearchMrktLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPSearchMrktLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(jCBMarTeam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29)
                    .addComponent(jCBMarCU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30)
                    .addComponent(jCBMarRegion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31)
                    .addComponent(jCBMarMrkt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addGroup(jPSearchMrktLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBSearchMrkt)
                    .addComponent(jCBDeleteMrkt))
                .addContainerGap())
        );

        jLabel27.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel27.setText("Choose an action: ");

        jCBMrktAction.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBMrktAction.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Add a new market", "Edit an existing market" }));
        jCBMrktAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBMrktActionActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel32.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel32.setText("Region: ");

        jCBRegionMrkt.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBRegionMrkt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBRegionMrktActionPerformed(evt);
            }
        });

        jLabel33.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel33.setText("Market: ");

        jLabel34.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel34.setText("Team:");

        jBSaveMrkt.setBackground(new java.awt.Color(0, 130, 240));
        jBSaveMrkt.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBSaveMrkt.setForeground(new java.awt.Color(255, 255, 255));
        jBSaveMrkt.setText("Save");
        jBSaveMrkt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSaveMrktActionPerformed(evt);
            }
        });

        jCBTeamMrkt.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBTeamMrkt.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sourcing", "COP", "VSS", "PSS" }));
        jCBTeamMrkt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBTeamMrktActionPerformed(evt);
            }
        });

        jLabel35.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel35.setText("Customer Unit:");

        jCBCUMrkt.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBCUMrkt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBCUMrktActionPerformed(evt);
            }
        });

        jCBMarketList.setEditable(true);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel32)
                        .addGap(18, 18, 18)
                        .addComponent(jCBRegionMrkt, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addGap(18, 18, 18)
                        .addComponent(jCBTeamMrkt, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addGap(18, 18, 18)
                        .addComponent(jCBMarketList, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel35)
                        .addGap(18, 18, 18)
                        .addComponent(jCBCUMrkt, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 229, Short.MAX_VALUE)
                .addComponent(jBSaveMrkt)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBSaveMrkt)
                    .addComponent(jLabel34)
                    .addComponent(jCBTeamMrkt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35)
                    .addComponent(jCBCUMrkt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(jCBRegionMrkt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33)
                    .addComponent(jCBMarketList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPMarketLayout = new javax.swing.GroupLayout(jPMarket);
        jPMarket.setLayout(jPMarketLayout);
        jPMarketLayout.setHorizontalGroup(
            jPMarketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPMarketLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPMarketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPSearchMrkt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPMarketLayout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addGap(18, 18, 18)
                        .addComponent(jCBMrktAction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(767, Short.MAX_VALUE))
        );
        jPMarketLayout.setVerticalGroup(
            jPMarketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPMarketLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPMarketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(jCBMrktAction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPSearchMrkt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(685, Short.MAX_VALUE))
        );

        jMenuBar1.setBackground(new java.awt.Color(255, 255, 255));
        jMenuBar1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jMenuView.setBackground(new java.awt.Color(255, 255, 255));
        jMenuView.setText("View");
        jMenuView.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMenuView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuViewActionPerformed(evt);
            }
        });

        jMReview.setBackground(new java.awt.Color(255, 255, 255));
        jMReview.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMReview.setText("Review Logged Time");
        jMReview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMReviewActionPerformed(evt);
            }
        });
        jMenuView.add(jMReview);

        jMenuTeams.setBackground(new java.awt.Color(255, 255, 255));
        jMenuTeams.setText("Record Working Time");
        jMenuTeams.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jMICOP.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMICOP.setText("COP");
        jMICOP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMICOPActionPerformed(evt);
            }
        });
        jMenuTeams.add(jMICOP);

        jMIPSS.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMIPSS.setText("PSS");
        jMIPSS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIPSSActionPerformed(evt);
            }
        });
        jMenuTeams.add(jMIPSS);

        jMIScoping.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMIScoping.setText("Scoping");
        jMIScoping.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIScopingActionPerformed(evt);
            }
        });
        jMenuTeams.add(jMIScoping);

        jMISourcing.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMISourcing.setText("Sourcing");
        jMISourcing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMISourcingActionPerformed(evt);
            }
        });
        jMenuTeams.add(jMISourcing);

        jMIVSS.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMIVSS.setText("VSS");
        jMIVSS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIVSSActionPerformed(evt);
            }
        });
        jMenuTeams.add(jMIVSS);

        jMenuView.add(jMenuTeams);

        jMenuBar1.add(jMenuView);

        jMenuEdit.setBackground(new java.awt.Color(255, 255, 255));
        jMenuEdit.setText("Edit");
        jMenuEdit.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMenuEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuEditActionPerformed(evt);
            }
        });

        jMEditUsers.setBackground(new java.awt.Color(255, 255, 255));
        jMEditUsers.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMEditUsers.setText("Edit Users");
        jMEditUsers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMEditUsersActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMEditUsers);

        jMEditTask.setBackground(new java.awt.Color(255, 255, 255));
        jMEditTask.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMEditTask.setText("Edit Tasks");
        jMEditTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMEditTaskActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMEditTask);

        jMEditNetworks.setBackground(new java.awt.Color(255, 255, 255));
        jMEditNetworks.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMEditNetworks.setText("Edit Networks");
        jMEditNetworks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMEditNetworksActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMEditNetworks);

        jMEditMarkets.setBackground(new java.awt.Color(255, 255, 255));
        jMEditMarkets.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMEditMarkets.setText("Edit Markets");
        jMEditMarkets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMEditMarketsActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMEditMarkets);

        jMenuBar1.add(jMenuEdit);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPView, javax.swing.GroupLayout.DEFAULT_SIZE, 1922, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPNetworks, javax.swing.GroupLayout.PREFERRED_SIZE, 3295, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPEdit, javax.swing.GroupLayout.DEFAULT_SIZE, 5224, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPEditTask, javax.swing.GroupLayout.PREFERRED_SIZE, 1920, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 3304, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPMarket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPNetworks, javax.swing.GroupLayout.DEFAULT_SIZE, 949, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jPView, javax.swing.GroupLayout.DEFAULT_SIZE, 955, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPEdit, javax.swing.GroupLayout.DEFAULT_SIZE, 955, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPEditTask, javax.swing.GroupLayout.PREFERRED_SIZE, 950, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPMarket, javax.swing.GroupLayout.PREFERRED_SIZE, 955, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jBShowPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBShowPreviewActionPerformed
        // TODO add your handling code here:
        jLLoading.setText("Fetching metrics from database...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                ShowLoggedTime();
                jDLoading.dispose();
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
            jLYear.setVisible(false);
            jLYearTo.setVisible(false);
            jCBYearFrom.setVisible(false);
            jCBYearTo.setVisible(false);
        }
    }//GEN-LAST:event_jRBWeekActionPerformed

    private void jRBTaskIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBTaskIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRBTaskIDActionPerformed

    private void jRBToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBToActionPerformed
        // TODO add your handling code here:
        if (!jRBTo.isSelected()) {
            jCBTo.setEnabled(false);
            jCBYearTo.setEnabled(false);
        } else {
            jCBTo.setEnabled(true);
            jCBYearTo.setEnabled(true);
        }
    }//GEN-LAST:event_jRBToActionPerformed

    private void jCBFromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBFromActionPerformed
        // TODO add your handling code here:
        jCBTo.removeAllItems();

        int inicial = jCBFrom.getSelectedIndex();
        if (inicial == -1) {
            inicial = 0;
        }
        if (jRBWeek.isSelected()) {
            for (int i = inicial + 1; i < 52 + 1; i++) {
                jCBTo.addItem(Integer.toString(i));
                jCBTo.setSelectedIndex(jCBFrom.getSelectedIndex());
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
            jLYear.setVisible(true);
            jLYearTo.setVisible(true);
            jCBYearFrom.setVisible(true);
            jCBYearTo.setVisible(true);
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
            jLYear.setVisible(true);
            jLYearTo.setVisible(true);
            jCBYearFrom.setVisible(true);
            jCBYearTo.setVisible(true);
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
        jPMarket.setVisible(false);
        ResetUserFields();
        this.setTitle("MRT - Audit & Report - Add, Edit and Delete Users");
        //this.setSize(jPEdit.getPreferredSize());
    }//GEN-LAST:event_jMEditUsersActionPerformed

    private void jMReviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMReviewActionPerformed
        // TODO add your handling code here:
        jPView.setVisible(true);
        jPEdit.setVisible(false);
        jPEditTask.setVisible(false);
        jPNetworks.setVisible(false);
        jPMarket.setVisible(false);
        this.setTitle("MRT - Audit & Report - Review Metrics");
        //this.setSize(jPView.getPreferredSize());
    }//GEN-LAST:event_jMReviewActionPerformed

    private void jMenuViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuViewActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuViewActionPerformed

    private void jMEditTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMEditTaskActionPerformed
        // TODO add your handling code here:
        jPView.setVisible(false);
        jPEdit.setVisible(false);
        jPNetworks.setVisible(false);
        jPEditTask.setVisible(true);
        jPMarket.setVisible(false);
        // ResetTaskFields();
        this.setTitle("MRT - Audit & Report - Add, Edit and Delete Tasks");
        //this.setSize(jPEditTask.getPreferredSize());
    }//GEN-LAST:event_jMEditTaskActionPerformed

    private void jBDeleteTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDeleteTaskActionPerformed
        // TODO add your handling code here:
        int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete task?", "Delete?", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            jLLoading.setText("Deleting task from database...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DeleteTaskDB();
                    ResetTaskFields();
                    jLLoading.setText("Updating task's local table...");
                    GetAllTasks();
                    jDLoading.dispose();
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
            jCBTaskTeam.setEnabled(true);
            jCBTaskCU.setEnabled(true);
            ResetTaskFields();
        } else {
            jPSearchTask.setVisible(true);
            jCBTaskType.setEnabled(false);
            jTFTaskName.setEditable(false);
            jCBTaskTeam.setEnabled(false);
            jCBTaskCU.setEnabled(false);
            ResetTaskFields();
        }
    }//GEN-LAST:event_jCBTaskEditActionPerformed

    private void jBSaveTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSaveTaskActionPerformed
        // TODO add your handling code here:
        String loe = jTFLOE.getText();
        Pattern decimalPattern = Pattern.compile("^-?\\d{0,2}(\\.\\d{1,2})?");
        Matcher matcherLOE = decimalPattern.matcher(loe);
        boolean flagLOE = matcherLOE.matches();
        if (jTFTaskName.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Task field is empty!");
        } else if (!flagLOE && !loe.equals("")){
            JOptionPane.showMessageDialog(this, "LOE is not a number! Please type a number with maximum two decimals.");
        } else {
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
                }
            }).start();
            jDLoading.setVisible(true);
        }
    }//GEN-LAST:event_jBSaveTaskActionPerformed

    private void jBSearchTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSearchTaskActionPerformed
        // TODO add your handling code here:
        jLLoading.setText("Fetching task from database...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                SearchTaskDB();
                jDLoading.dispose();
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
            try {
                SaveTableCSV(fileName, model);
            } catch (IOException ex) {
                Logger.getLogger(Time_Review.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please type a name for the CSV file.");
        }
    }//GEN-LAST:event_jBGenerateCSVActionPerformed

    private void jMEditNetworksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMEditNetworksActionPerformed
        // TODO add your handling code here:
        jPView.setVisible(false);
        jPEdit.setVisible(false);
        jPEditTask.setVisible(false);
        jPNetworks.setVisible(true);
        jPMarket.setVisible(false);
        ResetNetworkFields();
        this.setTitle("MRT - Audit & Report - Add, Edit and Delete Networks");
        System.out.println("Width: " + jPNetworks.getWidth() + " Height: " + jPNetworks.getHeight());

//        this.setSize(jPNetworks.getMaximumSize());
    }//GEN-LAST:event_jMEditNetworksActionPerformed

    private void jCBNetActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBNetActionActionPerformed
        // TODO add your handling code here:
        if (jCBNetAction.getSelectedIndex() == 0) {
            jPNetSearch.setVisible(false);
            jTFNetwork.setEditable(true);
            jCBNetTeam.setEnabled(true);
            jTFPD.setEditable(true);
            ResetNetworkFields();
        } else {
            jPNetSearch.setVisible(true);
            jTFNetwork.setEditable(false);
            jCBNetTeam.setEnabled(false);
            jTFPD.setEditable(false);
            ResetNetworkFields();
        }
    }//GEN-LAST:event_jCBNetActionActionPerformed

    private void jBSaveNetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSaveNetActionPerformed
        // TODO add your handling code here:
        boolean flagNetwork = false;
        flagNetwork = ReviewFieldsState();
        if (flagNetwork) {
            jLLoading.setText("Saving network into database...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (jCBNetAction.getSelectedIndex() == 0) {
                        InsertIntoNetworkDB();
                    } else {
                        UpdateNetworksDB();
                    }
                    netTeamsAndCUs = new ArrayList<>();
                    //netTeamCURegMark = new ArrayList<>();
                    netwTeamSubn = new ArrayList<>();
                    
                    GetNetworksSearch();
                    ResetNetworkFields();
                    jDLoading.dispose();
                }
            }).start();
            jDLoading.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "There's an empty field! Please fill all the fields to execute this action.");
        }
    }//GEN-LAST:event_jBSaveNetActionPerformed

    private void jBNetworkSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNetworkSearchActionPerformed
        // TODO add your handling code here:
        jLLoading.setText("Fetching network from database...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                SearchNetworkDB();
                jDLoading.dispose();
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

    private void jBSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSearchActionPerformed
        // TODO add your handling code here:
        String signumEdit = jCBSearchUser.getItemAt(jCBSearchUser.getSelectedIndex());
        String[] names = signumEdit.split(",");
        String lastName1 = names[0];
        Pattern onewordPattern = Pattern.compile("[^A-Za-z]");
        Matcher matcherSignum = onewordPattern.matcher(signumEdit);
        boolean flagSignum = matcherSignum.find();
        flagSignum = false;
        if (signumEdit.equals("")) {
            JOptionPane.showMessageDialog(this, "The field is empty! Please type a signum to start editing.");
        } else {
            if (flagSignum) {
                JOptionPane.showMessageDialog(this, "Signum must contain only letters!");
            } else {
                jLLoading.setText("Looking for user's info...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Connection connection;
                        PreparedStatement preparedStatement;
                        ResultSet resultset;
                        try {
                            String sql = "";
                            sql = "SELECT Signum, Name, Last_Name, Organization, Team, Customer_Unit, Line_Manager, Access, Supporting_Team, Supporting_CU, Job_Stage, CATS_Number FROM users WHERE Last_Name = '" + lastName1 + "';";
                            System.out.println(sql);
                            connection = SQL_connection.getConnection();
                            preparedStatement = connection.prepareStatement(sql);
                            resultset = preparedStatement.executeQuery();
                            if (!resultset.next()) {
                                JOptionPane.showMessageDialog(Time_Review.this, "Signum not found!");
                                jCBSearchUser.setSelectedIndex(0);
                            } else {
                                String signum1 = resultset.getString("Signum");
                                String name1 = resultset.getString("Name");
                                String lastname1 = resultset.getString("Last_Name");
                                String organization1 = resultset.getString("Organization");
                                String team1 = resultset.getString("Team");
                                String customerunit1 = resultset.getString("Customer_Unit");
                                String linemanager1 = resultset.getString("Line_Manager");
                                String access1 = resultset.getString("Access");
                                String supportingTeam1 = resultset.getString("Supporting_Team");
                                String supportingCU1 = resultset.getString("Supporting_CU");
                                String workCenter1 = resultset.getString("CATS_Number");
                                String jobStage1 = resultset.getString("Job_Stage");

                                HashMap<String, String> CU_list = new HashMap<String, String>();

                                // Add keys and values (Team name, Prefix)
                                CU_list.put("COP", "C_");
                                CU_list.put("VSS", "V_");
                                CU_list.put("PSS", "P_");
                                CU_list.put("Scoping", "SCP_");                               

                                if (CU_list.containsKey(team1)) {
                                    customerunit1 = customerunit1.replace(CU_list.get(team1), "");                                    
                                }

                                jTFSignum.setText(signum1);
                                jTFName.setText(name1);
                                jTFLastName.setText(lastname1);
                                //jCBOrganization.setSelectedItem(organization1);
                                jCBTeam.setSelectedItem(team1);
                                jCBTeamActionPerformed(null);
                                jCBCustomerUnit.setSelectedItem(customerunit1);
                                for (int i = 0; i<5; i++){
                                    if (LMSignums[i].equals(linemanager1))
                                        jCBLineManager.setSelectedIndex(i);
                                };
                                jCBAccess.setSelectedIndex(Integer.parseInt(access1));
                                jTFSupTeam.setText(supportingTeam1);
                                jTFSupCU.setText(supportingCU1);
                                if (jobStage1.equals("N/A"))
                                    jCBJobStage.setSelectedIndex(3);
                                else{
                                    jobStage1 = jobStage1.substring(jobStage1.length() - 1);
                                    System.out.println("Job Stage: " + jobStage1);
                                    jCBJobStage.setSelectedItem(jobStage1);
                                }
                                jTFCATNum.setText(workCenter1);
                            }
                            connection.close();

                        } catch (SQLException e) {
                            JOptionPane.showMessageDialog(Time_Review.this, "An error ocurred during the look up! Please try again...");
                        }

                        jDLoading.dispose();
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
        String signumEdit = jCBSearchUser.getItemAt(jCBSearchUser.getSelectedIndex());
        Pattern onewordPattern = Pattern.compile("[^A-Za-z]");
        Matcher matcherSignum = onewordPattern.matcher(signumEdit);
        boolean flagSignum = matcherSignum.find();
        int reply;
        reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + signumEdit + "?", "Delete?", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            jLLoading.setText("Deleting user from database...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DeleteUserDB();
                    ResetUserFields();
                    jLLoading.setText("Updating user's local table...");
                    GetAllUsers();
                    jDLoading.dispose();
                }
            }).start();
            jDLoading.setVisible(true);
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
            ResetUserFields();
        } else if (jCBAction.getSelectedIndex() == 1) {
            jPUser.setVisible(true);
            jPSearch.setVisible(true);
            jTFSignum.setEditable(false);
            jTFName.setEditable(false);
            jTFLastName.setEditable(false);
            ResetUserFields();
        }
    }//GEN-LAST:event_jCBActionActionPerformed

    private void jCBSupportedCUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBSupportedCUActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBSupportedCUActionPerformed

    private void jBSaveNewUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSaveNewUserActionPerformed
        // TODO add your handling code here:
        System.out.println("Entrando a Save user button...");
        String signum = jTFSignum.getText();
        String name = jTFName.getText();
        String lastName = jTFLastName.getText();
        String catsNumber = jTFCATNum.getText();
        
        Pattern onewordPattern = Pattern.compile("[^A-Za-z]");
        Pattern lettersPattern = Pattern.compile("[^A-Za-z]->[A-Za-z]");
        Pattern numberPattern = Pattern.compile("[^0-9]");
        
        Matcher matcherSignum = onewordPattern.matcher(signum);
        Matcher matcherName = lettersPattern.matcher(name);
        Matcher matcherLastName = lettersPattern.matcher(lastName);
        Matcher matcherCATsNumber = numberPattern.matcher(catsNumber);
        
        boolean flagSignum = matcherSignum.find();
        boolean flagName = matcherName.find();
        boolean flagLastName = matcherLastName.find();
        boolean flagCATsNumber = matcherCATsNumber.find();

        if (!signum.equals("") && !name.equals("") && !lastName.equals("") && !catsNumber.equals("")) {
            if (flagSignum) {
                JOptionPane.showMessageDialog(this, "Signum must contain only letters!");
                jTFSignum.setText("");
            } else if (flagName) {
                JOptionPane.showMessageDialog(this, "Name must contain only letters!");
                jTFName.setText("");
            } else if (flagLastName) {
                JOptionPane.showMessageDialog(this, "Last name must contain only letters!");
                jTFLastName.setText("");
            } else if (flagCATsNumber){
                JOptionPane.showMessageDialog(this, "CATs Number must contain only numbers!");
                jTFCATNum.setText("");
            } else if (catsNumber.length() != 8){
                JOptionPane.showMessageDialog(this, "CATs Number must contain 8 digits!");
                jTFCATNum.setText("");
            } else {
                jLLoading.setText("Saving user into database...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (jCBAction.getSelectedIndex() == 0) {
                            InsertIntoUserDB();
                        } else {
                            UpdateUserDB();
                            //JOptionPane.showMessageDialog(this, "There's an empty field! Please fill every text field in order to save the user.");
                        }
                        jLLoading.setText("Updating user's local table...");
                        GetAllUsers();
                        ResetUserFields();
                        jDLoading.dispose();
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
        try {
            SaveTableCSV(fileName, model);
        } catch (IOException ex) {
            Logger.getLogger(Time_Review.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jBExportUserCSVActionPerformed

    private void jBExportTaskCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBExportTaskCSVActionPerformed
        // TODO add your handling code here:
        String fileName = "Metrics Tasks";
        TableModel model = jTTasksList.getModel();
        try {
            SaveTableCSV(fileName, model);
        } catch (IOException ex) {
            Logger.getLogger(Time_Review.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jBExportTaskCSVActionPerformed

    private void jBNetDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNetDeleteActionPerformed
        // TODO add your handling code here:
        jLLoading.setText("Deleting subnetwork from the database...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                DeleteNetwork();
                jLLoading.setText("Updating network's local table...");
                netTeamsAndCUs = new ArrayList<>();
                //netTeamCURegMark = new ArrayList<>();
                netwTeamSubn = new ArrayList<>();
                GetNetworksSearch();
                jDLoading.dispose();
            }
        }).start();
        jDLoading.setVisible(true);
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

    private void jBShowMetricsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBShowMetricsActionPerformed
        // TODO add your handling code here:
        jLLoading.setText("Fetching metrics from database...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                ShowMetrics();
                jDLoading.dispose();
            }
        }).start();
        jDLoading.setVisible(true);
    }//GEN-LAST:event_jBShowMetricsActionPerformed

    private void jCBTaskTeamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBTaskTeamActionPerformed
        // Update CUs combobox according to team
        jCBTaskCU.removeAllItems();
        ArrayList<String> customers = new ArrayList<>();
        String team = jCBTaskTeam.getSelectedItem().toString();
        for (int i = 0; i < teamsAndCUs.size(); i++) {
            if (i % 2 == 0) {
                if (team.equals(teamsAndCUs.get(i))) {
                    customers.add(teamsAndCUs.get(i + 1));
                }
            }
        }
        Collections.sort(customers);
        for (int i = 0; i < customers.size(); i ++){
            jCBTaskCU.addItem(customers.get(i));
        }
        // Update task id
        jCBTaskType.removeAllItems();
        jCBServicePN.removeAllItems();
        jCBDeliverable.removeAllItems();
        jCBProjectSuppDom.removeAllItems();
        UpdateTaskType(team);
    }//GEN-LAST:event_jCBTaskTeamActionPerformed

    private void UpdateTaskType(String team){
         // Update task id in Add Task panel
        String _team = "  "; 
        
        HashMap<String, String> list_teams = new HashMap<String, String>();
        list_teams.put("COP", "COP");
        list_teams.put("PSS", "NAT");
        list_teams.put("Scoping", "SCP");
        list_teams.put("SDU", "ADMIN");
        list_teams.put("Sourcing", "SE");
        list_teams.put("VSS", "VSS");
        
        if (team.equals("Sourcing")){            
            _team = "ST";                    
        }
        // Fill task type
        for (int i = 0; i < tasktypes.size(); i++){
            if (tasktypes.get(i).contains(list_teams.get(team)) || tasktypes.get(i).contains(_team)){
                jCBTaskType.addItem(tasktypes.get(i));
            }
        }
        // Fill service package name
        for (int i = 0; i < servicePackageNames.size(); i++){
            int _index_team = servicePackageNames.get(i).lastIndexOf("#");
            String aux_team = servicePackageNames.get(i).substring(_index_team);
            if (aux_team.replace("#", "").equals(team)){
                jCBServicePN.addItem(servicePackageNames.get(i).substring(0, _index_team));
            }
        }
        // Fill deliverable
        for (int i = 0; i < deliverableList.size(); i++){
            int _index_team = deliverableList.get(i).lastIndexOf("#");
            String aux_team = deliverableList.get(i).substring(_index_team);
            if (aux_team.replace("#", "").equals(team)){
                jCBDeliverable.addItem(deliverableList.get(i).substring(0, _index_team));
            }
        }
        // Fill project support domain
        for (int i = 0; i < projectSupportNames.size(); i++){
            int _index_team = projectSupportNames.get(i).lastIndexOf("#");
            String aux_team = projectSupportNames.get(i).substring(_index_team);
            if (aux_team.replace("#", "").equals(team)){
                jCBProjectSuppDom.addItem(projectSupportNames.get(i).substring(0, _index_team));
            }
        }
    }
    
    private void jBDeleteCUSuppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDeleteCUSuppActionPerformed
        // TODO add your handling code here:
        String normalTeam = jCBTeam.getItemAt(jCBTeam.getSelectedIndex());
        String supTeams = jTFSupTeam.getText();
        String supCUs = jTFSupCU.getText();
        String selTeam = jCBSupportedTeam.getItemAt(jCBSupportedTeam.getSelectedIndex());
        String selCU = jCBSupportedCU.getItemAt(jCBSupportedCU.getSelectedIndex());
        if (selTeam.equals("COP")) {
            selCU = "C_" + selCU;
        } else if (selTeam.equals("VSS")) {
            selCU = "V_" + selCU;
        } else if (selTeam.equals("PSS")) {
            selCU = "P_" + selCU;
        }

        if (supTeams.equals("N/A") && supCUs.equals("N/A")) {
            JOptionPane.showMessageDialog(this, "There are no CUs to delete...");
        }
        //(supTeams.contains(selTeam + "@") || supTeams.contains("@" + selTeam)) && 
        if (supCUs.contains("@" + selCU) || supCUs.contains(selCU + "@")) {
            if (supTeams.contains(selTeam + "@")) {
                supTeams = supTeams.replace(selTeam + "@", "");
            } else if (supTeams.contains("@" + selTeam)) {
                supTeams = supTeams.replace("@" + selTeam, "");
            } else if (supTeams.equals(selTeam)) {
                supTeams = "N/A";
            }

            if (supCUs.contains("@" + selCU)) {
                supCUs = supCUs.replaceFirst("@" + selCU, "");
            } else if (supCUs.contains(selCU + "@")) {
                supCUs = supCUs.replaceFirst(selCU + "@", "");
            }
        } else if (selTeam.equals(normalTeam)) {
            if (supCUs.contains("@" + selCU)) {
                supCUs = supCUs.replaceFirst("@" + selCU, "");
            } else if (supCUs.contains(selCU + "@")) {
                supCUs = supCUs.replaceFirst(selCU + "@", "");
            } else if (!supCUs.contains("@")) {
                supCUs = "N/A";
            } else if (!supCUs.contains(selCU)) {
                JOptionPane.showMessageDialog(this, "There is no supporting Team and CUs like the selected ones right now...");
            }
        } else if (!supCUs.contains("@")) {
            supCUs = "N/A";
            supTeams = "N/A";
        } else {
            JOptionPane.showMessageDialog(this, "There is no supporting Team and CUs like the selected ones right now...");
        }

        jTFSupTeam.setText(supTeams);
        jTFSupCU.setText(supCUs);
    }//GEN-LAST:event_jBDeleteCUSuppActionPerformed

    private void jCBSupportedTeamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBSupportedTeamActionPerformed
        // TODO add your handling code here:
        jCBSupportedCU.removeAllItems();

        String team1 = jCBSupportedTeam.getItemAt(jCBSupportedTeam.getSelectedIndex());
        for (int i = 0; i < teamsAndCUs.size(); i++) {
            if (i % 2 == 0) {
                if (team1.equals(teamsAndCUs.get(i))) {
                    jCBSupportedCU.addItem(teamsAndCUs.get(i + 1));
                }
            }
        }
    }//GEN-LAST:event_jCBSupportedTeamActionPerformed

    private void jCBTeamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBTeamActionPerformed
        // Add team to user's assignment
        jCBCustomerUnit.removeAllItems();

        String team = jCBTeam.getSelectedItem().toString();
        try {
            for (int i = 0; i < teamsAndCUs.size(); i++) {
                if (i % 2 == 0) {
                    if (team.equals(teamsAndCUs.get(i))) {
                        jCBCustomerUnit.addItem(teamsAndCUs.get(i + 1));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }//GEN-LAST:event_jCBTeamActionPerformed

    private void jBAddSupCUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBAddSupCUActionPerformed
        // TODO add your handling code here:
        String normalTeam = jCBTeam.getItemAt(jCBTeam.getSelectedIndex());
        String anotherTeam = jCBSupportedTeam.getItemAt(jCBSupportedTeam.getSelectedIndex());
        String anotherCU = jCBSupportedCU.getItemAt(jCBSupportedCU.getSelectedIndex());
        String supTeam = jTFSupTeam.getText();
        String supCU = jTFSupCU.getText();

        if (anotherTeam.equals("COP")) {
            anotherCU = "C_" + anotherCU;
        } else if (anotherTeam.equals("VSS")) {
            anotherCU = "V_" + anotherCU;
        } else if (anotherTeam.equals("PSS")) {
            anotherCU = "P_" + anotherCU;
        }

        if (!anotherTeam.equals(normalTeam)) {
            if (supTeam.equals("N/A")) {
                supTeam = anotherTeam;
            } else if (supTeam.contains(anotherTeam) || supTeam.contains(normalTeam)) {
                supTeam = supTeam;
            } else {
                supTeam = supTeam.concat("@" + anotherTeam);
            }
        }

        if (supCU.equals("N/A")) {
            supCU = anotherCU;
        } else {
            supCU = supCU.concat("@" + anotherCU);
        }

        jTFSupTeam.setText(supTeam);
        jTFSupCU.setText(supCU);
    }//GEN-LAST:event_jBAddSupCUActionPerformed

    private void jCBNetTeamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBNetTeamActionPerformed
        // TODO add your handling code here:
        jCBNetCustomer.removeAllItems();
        List<String> cus1 = new ArrayList<String>();
        String team1 = jCBNetTeam.getItemAt(jCBNetTeam.getSelectedIndex());
        try {
            for (int i = 0; i < netTeamsAndCUs.size(); i++) {
                if (i % 2 == 0) {
                    if (team1.equals(netTeamsAndCUs.get(i))) {
                        cus1.add(netTeamsAndCUs.get(i + 1));
                    }
                }
            }
        } catch (Exception e){
            System.out.println("Error: " + e);
        }
        Collections.sort(cus1);
        for (int i = 0; i < cus1.size(); i++)
            jCBNetCustomer.addItem(cus1.get(i));
    }//GEN-LAST:event_jCBNetTeamActionPerformed

    private void jCBNetCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBNetCustomerActionPerformed
        // TODO add your handling code here:
        List<String> regions1 = new ArrayList<String>();
        String reg1 = "";
        jCBNetRegion.removeAllItems();
        
        if (jCBNetCustomer.getItemCount() != 0) {
            try {
            String team1 = jCBNetTeam.getItemAt(jCBNetTeam.getSelectedIndex());
            String cu1 = jCBNetCustomer.getItemAt(jCBNetCustomer.getSelectedIndex());
            for (int i = 0; i < netTeamCURegMark.size(); i++) {// Team, CU, Region, Market, Technology
                if (i % 4 == 0) {
                    if (team1.equals(netTeamCURegMark.get(i)) && cu1.equals(netTeamCURegMark.get(i + 1))) {
                        reg1 = netTeamCURegMark.get(i + 2);
                        if (regions1.isEmpty()) 
                            regions1.add(reg1);
                        else 
                            if (!regions1.contains(reg1)) 
                                regions1.add(reg1);
                    }
                }
            }
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
            Collections.sort(regions1);
            for (int i = 0; i < regions1.size(); i++)
                jCBNetRegion.addItem(regions1.get(i));
        }
    }//GEN-LAST:event_jCBNetCustomerActionPerformed

    private void jCBNetRegionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBNetRegionActionPerformed
        // TODO add your handling code here:
        List<String> markets1 = new ArrayList<String>();
        String mar1 = "";
        jCBNetMarket.removeAllItems();
        if (jCBNetRegion.getItemCount() != 0) {
            try {
                String team1 = jCBNetTeam.getItemAt(jCBNetTeam.getSelectedIndex());
                String cu1 = jCBNetCustomer.getItemAt(jCBNetCustomer.getSelectedIndex());
                String reg1 = jCBNetRegion.getItemAt(jCBNetRegion.getSelectedIndex());
                for (int i = 0; i < netTeamCURegMark.size(); i++) {
                    if (i % 4 == 0) {
                        mar1 = netTeamCURegMark.get(i + 3);
                        if (team1.equals(netTeamCURegMark.get(i)) && cu1.equals(netTeamCURegMark.get(i + 1)) && reg1.equals(netTeamCURegMark.get(i + 2))) {// Team, CU, Region, Market, Technology
                            if (markets1.isEmpty()) 
                                markets1.add(mar1);
                            else 
                                if (!markets1.contains(mar1)) 
                                    markets1.add(mar1);
                        }
                    }
                } 
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
            Collections.sort(markets1);
            for (int i = 0; i < markets1.size(); i++)
                jCBNetMarket.addItem(markets1.get(i));
        }
    }//GEN-LAST:event_jCBNetRegionActionPerformed

    private void jCBNetMarketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBNetMarketActionPerformed
        // TODO add your handling code here:
        List<String> techs1 = new ArrayList<String>();
        String tech1 = "";
        jCBNetTech.removeAllItems();
        if (jCBNetMarket.getItemCount() != 0) {
            try {
                String mar1 = jCBNetMarket.getItemAt(jCBNetMarket.getSelectedIndex());
                for (int i = 0; i < netMrktTech.size(); i++) {
                    if (i % 2 == 0) {
                        tech1 = netMrktTech.get(i + 1);
                        if (mar1.equals(netMrktTech.get(i))) {
                            if (techs1.isEmpty())
                                techs1.add(tech1);
                            else 
                                if (!techs1.contains(tech1))
                                    techs1.add(tech1);
                        }
                    }
                }
                if (techs1.isEmpty()){
                    jCBNetTech.addItem("N/A");
                    techs1.add("N/A");
                }
                else{
                    Collections.sort(techs1);
                    for (int i = 0; i < techs1.size(); i++)
                        jCBNetTech.addItem(techs1.get(i));
                }
            } catch (Exception e) {
                System.out.println("Error: " + e);
            }
        }
    }//GEN-LAST:event_jCBNetMarketActionPerformed

    private void jMISourcingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMISourcingActionPerformed
        // TODO add your handling code here:
        StartDialog starting = new StartDialog();
        JDialog loading = starting.GetLoadingDialog();
        loading.setModal(true);
        loading.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        loading.toFront();
        loading.requestFocus();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Sourcing_Time_Report time_r = new Sourcing_Time_Report();
                    time_r.show();
                    time_r.setLocationRelativeTo(null);
                    loading.dispose();
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
                    loading.dispose();
                } catch (ParseException | IOException ex) {
                    Logger.getLogger(Sourcing_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
        loading.toFront();
        loading.requestFocus();
        loading.setVisible(true);
    }//GEN-LAST:event_jMISourcingActionPerformed

    private void jMICOPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMICOPActionPerformed
        // TODO add your handling code here:
        StartDialog starting = new StartDialog();
        JDialog loading = starting.GetLoadingDialog();
        loading.setModal(true);
        loading.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        loading.toFront();
        loading.requestFocus();
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
                    loading.dispose();
                } catch (ParseException | IOException ex) {
                    Logger.getLogger(Sourcing_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
        loading.toFront();
        loading.requestFocus();
        loading.setVisible(true);
    }//GEN-LAST:event_jMICOPActionPerformed

    private void jMIVSSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIVSSActionPerformed
        // TODO add your handling code here:
        StartDialog starting = new StartDialog();
        JDialog loading = starting.GetLoadingDialog();
        loading.setModal(true);
        loading.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        loading.toFront();
        loading.requestFocus();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    VSS_Time_Report time_r = new VSS_Time_Report();
                    time_r.show();
                    time_r.setLocationRelativeTo(null);
                    loading.dispose();
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
                } catch (ParseException | IOException ex) {
                    Logger.getLogger(Sourcing_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
        loading.toFront();
        loading.requestFocus();
        loading.setVisible(true);
    }//GEN-LAST:event_jMIVSSActionPerformed

    private void jCBNetTeamSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBNetTeamSearchActionPerformed
        // TODO add your handling code here:
        List<String> cus1 = new ArrayList<String>();
        String cu1 = "";
        jCBCUSearch.removeAllItems();
        if (jCBNetTeamSearch.getItemCount() != 0) {
            String team1 = jCBNetTeamSearch.getItemAt(jCBNetTeamSearch.getSelectedIndex());
            for (int i = 0; i < netwTeamSubn.size(); i++) {
                if (i % 5 == 0) {
                    cu1 = netwTeamSubn.get(i + 1);
                    if (team1.equals(netwTeamSubn.get(i))) {
                        if (cus1.isEmpty()) 
                            cus1.add(cu1);
                        else 
                            if (!cus1.contains(cu1))
                                cus1.add(cu1);
                    }
                }
            }
            Collections.sort(cus1);
            for (int i = 0; i < cus1.size(); i++)
                jCBCUSearch.addItem(cus1.get(i));
        }
    }//GEN-LAST:event_jCBNetTeamSearchActionPerformed

    private void jCBNetSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBNetSearchActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jCBNetSearchActionPerformed

    private void jCBCUSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBCUSearchActionPerformed
        // TODO add your handling code here:
        List<String> markets1 = new ArrayList<String>();
        String market1 = "";
        jCBMarketSearch.removeAllItems();
        if (jCBCUSearch.getItemCount() != 0) {
            String cu1 = jCBCUSearch.getItemAt(jCBCUSearch.getSelectedIndex());
            String team1 = jCBNetTeamSearch.getItemAt(jCBNetTeamSearch.getSelectedIndex());
            for (int i = 0; i < netwTeamSubn.size(); i++) {
                if (i % 5 == 0) {
                    market1 = netwTeamSubn.get(i + 2);
                    if (team1.equals(netwTeamSubn.get(i)) && cu1.equals(netwTeamSubn.get(i + 1))) {
                        if (markets1.isEmpty())
                            markets1.add(market1);
                        else 
                            if (!markets1.contains(market1)) 
                                markets1.add(market1);
                    }
                }
            }
            Collections.sort(markets1);
            for (int i = 0; i < markets1.size(); i++)
                jCBMarketSearch.addItem(markets1.get(i));
        }
    }//GEN-LAST:event_jCBCUSearchActionPerformed

    private void jCBMarketSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBMarketSearchActionPerformed
        // TODO add your handling code here:
        List<String> nets1 = new ArrayList<String>();
        String net1 = "";
        jCBNetSearch.removeAllItems();
        if (jCBMarketSearch.getItemCount() != 0) {
            String cu1 = jCBCUSearch.getItemAt(jCBCUSearch.getSelectedIndex());
            String team1 = jCBNetTeamSearch.getItemAt(jCBNetTeamSearch.getSelectedIndex());
            String mar1 = jCBMarketSearch.getItemAt(jCBMarketSearch.getSelectedIndex());
            for (int i = 0; i < netwTeamSubn.size(); i++) {
                if (i % 5 == 0) {
                    net1 = netwTeamSubn.get(i + 3);
                    if (team1.equals(netwTeamSubn.get(i)) && cu1.equals(netwTeamSubn.get(i + 1)) && mar1.equals(netwTeamSubn.get(i + 2))) {
                        if (nets1.isEmpty()) 
                            nets1.add(net1);
                        else 
                            if (!nets1.contains(net1)) 
                                nets1.add(net1);
                    }
                }
            }
            Collections.sort(nets1);
            for (int i = 0; i < nets1.size(); i++)
                jCBNetSearch.addItem(nets1.get(i));
        }
    }//GEN-LAST:event_jCBMarketSearchActionPerformed

    private void jMEditMarketsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMEditMarketsActionPerformed
        // TODO add your handling code here:
        jPView.setVisible(false);
        jPEdit.setVisible(false);
        jPNetworks.setVisible(false);
        jPEditTask.setVisible(false);
        jPMarket.setVisible(true);
        ResetTaskFields();
        this.setTitle("MRT - Audit & Report - Add, Edit and Delete Markets");
    }//GEN-LAST:event_jMEditMarketsActionPerformed

    private void jBSaveMrktActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSaveMrktActionPerformed
        // TODO add your handling code here:
        boolean flagNetwork = true;
        // Validar por equipo y cu que no se repita en esa Team - CU
        String teamName = jCBTeamMrkt.getItemAt(jCBTeamMrkt.getSelectedIndex());
        String cuName = jCBCUMrkt.getItemAt(jCBCUMrkt.getSelectedIndex());
        String regionName = jCBRegionMrkt.getItemAt(jCBRegionMrkt.getSelectedIndex());
        String mrktName = jCBMarketList.getItemAt(jCBMarketList.getSelectedIndex());
        
        System.out.println("Market: " + mrktName);
        if (mrktName != null)
            flagNetwork = false;
        else {
            String mrktNameText = String.valueOf(jCBMarketList.getEditor().getItem());
            for (int i = 0; i < marketTeamCU.size(); i++)
                if (i % 5 == 0)
                    if (mrktNameText.equals(marketTeamCU.get(i + 1)) && regionName.equals(marketTeamCU.get(i + 2)) && teamName.equals(marketTeamCU.get(i + 3)) && cuName.equals(marketTeamCU.get(i + 4)))
                        flagNetwork = false;
        }
        if (flagNetwork) {
            jLLoading.setText("Saving market into database...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (jCBMrktAction.getSelectedIndex() == 0) {
                        InsertIntoMarketDB();
                    } else {
                        UpdateMarketsDB();
                    }
                    marketTeamCU = new ArrayList<>();
                    jCBTeamMrkt.setSelectedIndex(0);
                    GetAllMarkets();
                    jDLoading.dispose();
                }
            }).start();
            jDLoading.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Either the market field is empty or this market already exists! Please try again.");
        }
    }//GEN-LAST:event_jBSaveMrktActionPerformed

    private void jBNetTableCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNetTableCSVActionPerformed
        // TODO add your handling code here:
        String fileName = "Networks table";
        TableModel model = jTableNetworks.getModel();
        try {
            SaveTableCSV(fileName, model);
        } catch (IOException ex) {
            Logger.getLogger(Time_Review.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jBNetTableCSVActionPerformed

    private void jCBMarTeamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBMarTeamActionPerformed
        // TODO add your handling code here:
        List<String> cus1 = new ArrayList<String>();
        String cu1 = "";
        jCBMarCU.removeAllItems();
        if (jCBMarTeam.getItemCount() > 0){
            String team1 = jCBMarTeam.getItemAt(jCBMarTeam.getSelectedIndex());
            for (int i = 0; i < marketTeamCU.size(); i++){
                if (i%5 == 0){
                    cu1 = marketTeamCU.get(i + 4);
                    if (team1.equals(marketTeamCU.get(i + 3))) {
                        if (cus1.isEmpty()) {
                            jCBMarCU.addItem(cu1);
                            cus1.add(cu1);
                        } else {
                            if (!cus1.contains(cu1)) {
                                jCBMarCU.addItem(cu1);
                                cus1.add(cu1);
                            }
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_jCBMarTeamActionPerformed

    private void jCBMarCUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBMarCUActionPerformed
        // TODO add your handling code here:
        List<String> regs = new ArrayList<String>();
        String reg1 = "";
        jCBMarRegion.removeAllItems();
        if (jCBMarCU.getItemCount() > 0){
            String team1 = jCBMarTeam.getItemAt(jCBMarTeam.getSelectedIndex());
            String cu1 = jCBMarCU.getItemAt(jCBMarCU.getSelectedIndex());
            for (int i = 0; i < marketTeamCU.size(); i++){
                if (i%5 == 0){
                    reg1 = marketTeamCU.get(i + 2);
                    if (team1.equals(marketTeamCU.get(i + 3)) && cu1.equals(marketTeamCU.get(i + 4))) {
                        if (regs.isEmpty()) {
                            jCBMarRegion.addItem(reg1);
                            regs.add(reg1);
                        } else {
                            if (!regs.contains(reg1)) {
                                jCBMarRegion.addItem(reg1);
                                regs.add(reg1);
                            }
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_jCBMarCUActionPerformed

    private void jCBMarMrktActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBMarMrktActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBMarMrktActionPerformed

    private void jCBMarRegionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBMarRegionActionPerformed
        // TODO add your handling code here:
        List<String> mars = new ArrayList<String>();
        String mar1 = "";
        jCBMarMrkt.removeAllItems();
        if (jCBMarRegion.getItemCount() > 0){
            String team1 = jCBMarTeam.getItemAt(jCBMarTeam.getSelectedIndex());
            String cu1 = jCBMarCU.getItemAt(jCBMarCU.getSelectedIndex());
            String reg1 = jCBMarRegion.getItemAt(jCBMarRegion.getSelectedIndex());
            for (int i = 0; i < marketTeamCU.size(); i++){
                if (i%5 == 0){
                    mar1 = marketTeamCU.get(i + 1);
                    if (reg1.equals(marketTeamCU.get(i + 2)) && team1.equals(marketTeamCU.get(i + 3)) && cu1.equals(marketTeamCU.get(i + 4))) {
                        if (mars.isEmpty()) {
                            jCBMarMrkt.addItem(mar1);
                            mars.add(mar1);
                        } else {
                            if (!mars.contains(mar1)) {
                                jCBMarMrkt.addItem(mar1);
                                mars.add(mar1);
                            }
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_jCBMarRegionActionPerformed

    private void jCBMrktActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBMrktActionActionPerformed
        // TODO add your handling code here:
        if (jCBMrktAction.getSelectedIndex() == 0){
            jPSearchMrkt.setVisible(false);
            jCBRegionMrkt.setEnabled(true);
            jCBTeamMrkt.setEnabled(true);
            jCBCUMrkt.setEnabled(true);
        }
        else{
            jPSearchMrkt.setVisible(true);
            jCBRegionMrkt.setEnabled(false);
            jCBTeamMrkt.setEnabled(false);
            jCBCUMrkt.setEnabled(false);
        }
    }//GEN-LAST:event_jCBMrktActionActionPerformed

    private void jCBTeamMrktActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBTeamMrktActionPerformed
        // TODO add your handling code here:
        List<String> cus1 = new ArrayList<String>();
        String cu1 = "";
        jCBCUMrkt.removeAllItems();
        if (jCBTeamMrkt.getItemCount() > 0){
            String team1 = jCBTeamMrkt.getItemAt(jCBTeamMrkt.getSelectedIndex());
            for (int i = 0; i < marketTeamCU.size(); i++){
                if (i%5 == 0){
                    cu1 = marketTeamCU.get(i + 4);
                    if (team1.equals(marketTeamCU.get(i + 3))) {
                        if (cus1.isEmpty()) {
                            jCBCUMrkt.addItem(cu1);
                            cus1.add(cu1);
                        } else {
                            if (!cus1.contains(cu1)) {
                                jCBCUMrkt.addItem(cu1);
                                cus1.add(cu1);
                            }
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_jCBTeamMrktActionPerformed

    private void jBSearchMrktActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSearchMrktActionPerformed
        // TODO add your handling code here:
        jLLoading.setText("Fetching market from database...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                SearchMarketkDB();
                jDLoading.dispose();
            }
        }).start();
        jDLoading.setVisible(true);
    }//GEN-LAST:event_jBSearchMrktActionPerformed

    private void jCBDeleteMrktActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBDeleteMrktActionPerformed
        // TODO add your handling code here:
        boolean flagNetwork = true;
        
        //flagNetwork = jTFMarket.getText();
        if (flagNetwork) {
            jLLoading.setText("Deleting market from database...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DeleteMarketDB();
                    marketTeamCU = new ArrayList<>();
                    jCBTeamMrkt.setSelectedIndex(0);
                    GetAllMarkets();
                    jDLoading.dispose();
                }
            }).start();
            jDLoading.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "There's an empty field! Please fill all the fields to execute this action.");
        }
    }//GEN-LAST:event_jCBDeleteMrktActionPerformed

    private void jTFCATNumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTFCATNumActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTFCATNumActionPerformed

    private void jCBTeamTaskSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBTeamTaskSearchActionPerformed
        // TODO add your handling code here:
        jCBCUTaskSearch.removeAllItems();
        List<String> cus1 = new ArrayList<String>();
        String team1 = jCBTeamTaskSearch.getItemAt(jCBTeamTaskSearch.getSelectedIndex());
        for (int i = 0; i < teamsCUSTasks.size(); i++) {
            if (i % 3 == 0) {
                if (team1.equals(teamsCUSTasks.get(i))) {
                    if (cus1.isEmpty()){
                        jCBCUTaskSearch.addItem(teamsCUSTasks.get(i + 1));
                        cus1.add(teamsCUSTasks.get(i + 1));
                    } else {
                        if (!cus1.contains(teamsCUSTasks.get(i + 1))){
                            jCBCUTaskSearch.addItem(teamsCUSTasks.get(i + 1));
                            cus1.add(teamsCUSTasks.get(i + 1));
                        }
                    }
                }
            }
        }
        jCBCUTaskSearch.setSelectedIndex(0);
    }//GEN-LAST:event_jCBTeamTaskSearchActionPerformed

    private void jCBCUTaskSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBCUTaskSearchActionPerformed
        // TODO add your handling code here:
        List<String> tasks1 = new ArrayList<String>();
        jCBTaskSearch.removeAllItems();
        String team1 = jCBTeamTaskSearch.getItemAt(jCBTeamTaskSearch.getSelectedIndex());
        String cu1 = jCBCUTaskSearch.getItemAt(jCBCUTaskSearch.getSelectedIndex());
        try {
                for (int i = 0; i < teamsCUSTasks.size(); i++) {
                if (i % 3 == 0) {
                    if (team1.equals(teamsCUSTasks.get(i)) && cu1.equals(teamsCUSTasks.get(i+1))) {
                        tasks1.add(teamsCUSTasks.get(i + 2));
                    }
                }
            }
            Collections.sort(tasks1);
            for (int i = 0; i < tasks1.size(); i++)
                jCBTaskSearch.addItem(tasks1.get(i));
        } catch (Exception e) {
            System.out.println(e);
        }
    }//GEN-LAST:event_jCBCUTaskSearchActionPerformed

    private void jCBCUMrktActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBCUMrktActionPerformed
        // TODO add your handling code here:
        List<String> regs = new ArrayList<String>();
        String reg1 = "";
        jCBRegionMrkt.removeAllItems();
        if (jCBCUMrkt.getItemCount() > 0){
            String team1 = jCBTeamMrkt.getItemAt(jCBTeamMrkt.getSelectedIndex());
            String cu1 = jCBCUMrkt.getItemAt(jCBCUMrkt.getSelectedIndex());
            for (int i = 0; i < marketTeamCU.size(); i++){
                if (i%5 == 0){
                    reg1 = marketTeamCU.get(i + 2);
                    if (team1.equals(marketTeamCU.get(i + 3)) && cu1.equals(marketTeamCU.get(i + 4))) {
                        if (regs.isEmpty()) {
                            jCBRegionMrkt.addItem(reg1);
                            regs.add(reg1);
                        } else {
                            if (!regs.contains(reg1)) {
                                jCBRegionMrkt.addItem(reg1);
                                regs.add(reg1);
                            }
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_jCBCUMrktActionPerformed

    private void jCBRegionMrktActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBRegionMrktActionPerformed
        // TODO add your handling code here:
        List<String> mars = new ArrayList<String>();
        String mar1 = "";
        jCBMarketList.removeAllItems();
        if (jCBRegionMrkt.getItemCount() > 0){
            String team1 = jCBTeamMrkt.getItemAt(jCBTeamMrkt.getSelectedIndex());
            String cu1 = jCBCUMrkt.getItemAt(jCBCUMrkt.getSelectedIndex());
            String reg1 = jCBRegionMrkt.getItemAt(jCBRegionMrkt.getSelectedIndex());
            for (int i = 0; i < marketTeamCU.size(); i++){
                if (i%5 == 0){
                    mar1 = marketTeamCU.get(i + 1);
                    if (reg1.equals(marketTeamCU.get(i + 2)) && team1.equals(marketTeamCU.get(i + 3)) && cu1.equals(marketTeamCU.get(i + 4))) {
                        if (mars.isEmpty()) {
                            jCBMarketList.addItem(mar1);
                            mars.add(mar1);
                        } else {
                            if (!mars.contains(mar1)) {
                                jCBMarketList.addItem(mar1);
                                mars.add(mar1);
                            }
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_jCBRegionMrktActionPerformed

    private void jCBNetTechActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBNetTechActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBNetTechActionPerformed

    private void jMIPSSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIPSSActionPerformed
        // TODO add your handling code here:
        StartDialog starting = new StartDialog();
        JDialog loading = starting.GetLoadingDialog();
        loading.setModal(true);
        loading.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        loading.toFront();
        loading.requestFocus();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PSS_Time_Report time_r = new PSS_Time_Report();
                    time_r.show();
                    time_r.setLocationRelativeTo(null);
                    loading.dispose();
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
                } catch (ParseException | IOException ex) {
                    Logger.getLogger(Sourcing_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
        loading.toFront();
        loading.requestFocus();
        loading.setVisible(true);
    }//GEN-LAST:event_jMIPSSActionPerformed

    private void jMIScopingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIScopingActionPerformed
        // Open Scoping frame       
        StartDialog starting = new StartDialog();
        JDialog loading = starting.GetLoadingDialog();
        loading.setModal(true);
        loading.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        loading.toFront();
        loading.requestFocus();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Scoping_Time_Report time_r = new Scoping_Time_Report();
                    time_r.show();
                    time_r.setLocationRelativeTo(null);
                    // Confirm exit window
                    time_r.setDefaultCloseOperation(time_r.DO_NOTHING_ON_CLOSE);
                    time_r.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                            if (JOptionPane.showConfirmDialog(time_r, "Are you sure you want to close this window?", "Exit Scoping",
                                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                                time_r.dispose();

                            }
                        }
                    });
                    loading.dispose();
                } catch (ParseException | IOException ex) {
                    Logger.getLogger(Sourcing_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
        loading.toFront();
        loading.requestFocus();
        loading.setVisible(true);
    }//GEN-LAST:event_jMIScopingActionPerformed

    private void GetTaskTypes() {
        // Get different task ids
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultset;
        try {
            String sql_task = null, task_id = "";
            sql_task = " SELECT Task_ID FROM tasks ORDER BY Task_ID ASC;";
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement(sql_task);
            resultset = preparedStatement.executeQuery();

            while (resultset.next()) {
                task_id = resultset.getString("Task_ID");
                tasktypes.add(task_id);
            }
            connection.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        // Remove numbers from task id
        for (int i = 0; i < tasktypes.size(); i++){
            String task_id = tasktypes.get(i);
            // Replace numbers
            int sub = task_id.lastIndexOf("-");
            String substring = task_id.substring(sub);
            task_id = task_id.replace(substring, "");
            tasktypes.set(i, task_id);
        }
        tasktypes = tasktypes.stream().distinct().collect(Collectors.toList());
    }
    
    
    private void GetNetworksSearch() {
        String netHeader[] = {"PD", "Network", "Activity Code", "Region", "Market", "Team", "Customer Unit", "Responsible", "Subnetwork", "Technology"};
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(netHeader);
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultset;
        String subnet, actcode, reg, markt, team, tech;
        String[] row = new String[10];
        try {
            String sql = "";
            sql = "SELECT PD, Network, Activity_Code, "
                    + "Region, Market, Team, Customer, "
                    + "Responsible, Subnetwork, Technology FROM networks ORDER BY Network asc;";
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultset = preparedStatement.executeQuery();
            while (resultset.next()) {
                String[] row1 = {resultset.getString("Team"), resultset.getString("Customer")};
                String[] row2 = {resultset.getString("Market"), resultset.getString("Technology")};
                String[] row3 = {resultset.getString("Team"), resultset.getString("Customer"), resultset.getString("Market"), resultset.getString("Network"), resultset.getString("Subnetwork")};
                for (int i = 0; i < 10; i++) {
                    row[i] = resultset.getString(i + 1);
                }
                if (netTeamsAndCUs.isEmpty()) {
                    List<String> newList = Arrays.asList(row1);
                    netTeamsAndCUs.addAll(newList);
                } else {
                    int noRepeats = 0;
                    for (int i = 0; i < netTeamsAndCUs.size(); i++) {
                        if (i % 2 == 0) {
                            if (netTeamsAndCUs.get(i).equals(row1[0]) && netTeamsAndCUs.get(i + 1).equals(row1[1])) {
                                noRepeats += 1;
                            }
                        }
                    }
                    if (noRepeats == 0) {
                        List<String> newList = Arrays.asList(row1);
                        netTeamsAndCUs.addAll(newList);
                    }
                }
                

                if (networks.isEmpty()) {
                    networks.add(resultset.getString("Network"));
                } else {
                    if (!networks.contains(resultset.getString("Network"))) {
                        networks.add(resultset.getString("Network"));
                    }
                }

                if (netwTeamSubn.isEmpty()) {
                    List<String> newList1 = Arrays.asList(row3);
                    netwTeamSubn.addAll(newList1);
                } else {
                    int numbRep = 0;
                    for (int i = 0; i < netwTeamSubn.size(); i++) {
                        if (i % 5 == 0) {
                            if (netwTeamSubn.get(i).equals(row3[0]) && netwTeamSubn.get(i + 1).equals(row3[1]) && netwTeamSubn.get(i + 2).equals(2) && netwTeamSubn.get(i + 3).equals(row3[3]) && netwTeamSubn.get(i + 4).equals(4)) {
                                numbRep += 1;
                            }
                        }
                    }
                    if (numbRep == 0) {
                        List<String> newList1 = Arrays.asList(row3);
                        netwTeamSubn.addAll(newList1);
                    }
                }
                
                if (netMrktTech.isEmpty()) {
                    List<String> newList1 = Arrays.asList(row2);
                    netMrktTech.addAll(newList1);
                } else {
                    int numbRep = 0;
                    for (int i = 0; i < netMrktTech.size(); i++) {
                        if (i % 2 == 0) {
                            if (netMrktTech.get(i).equals(row2[0]) && netMrktTech.get(i + 1).equals(row2[1])) {
                                numbRep += 1;
                            }
                        }
                    }
                    if (numbRep == 0) {
                        List<String> newList1 = Arrays.asList(row2);
                        netMrktTech.addAll(newList1);
                    }
                }

                subnet = resultset.getString("Subnetwork");
                actcode = resultset.getString("Activity_code");
                reg = resultset.getString("Region");
                markt = resultset.getString("Market");
                team = resultset.getString("Team");
                tech = resultset.getString("Technology");

                if (!act_codes.isEmpty()) {
                    if (!act_codes.contains(actcode)) {
                        act_codes.add(actcode);
                        jCBNetActCode.addItem(actcode);
                    }
                } else {
                    act_codes.add(actcode);
                    jCBNetActCode.addItem(actcode);
                }
                if (!netTeams.isEmpty()) {
                    if (!netTeams.contains(team)) {
                        netTeams.add(team);
                        jCBNetTeam.addItem(team);
                        jCBNetTeamSearch.addItem(team);
                    }
                } else {
                    netTeams.add(team);
                    jCBNetTeam.addItem(team);
                    jCBNetTeamSearch.addItem(team);
                }
                model.addRow(row);
            }
            jTableNetworks.setModel(model);
            resizeColumnWidth(jTableNetworks);
            connection.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        //jCBNetCustomerActionPerformed(null);
        //jCBNetRegionActionPerformed(null);
        //jCBNetMarketActionPerformed(null);
    }

    private void GetAllUsers() {
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultset;
        String[] column = {"Signum", "Last_Name", "Name", "Customer_Unit", "Team", "Organization", "Line_Manager", "Access", "Supporting_Team", "Supporting_CU", "Job_Stage", "Act_Type", "CATS_Number"};
        String[] row = new String[13];
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(column);
        try {

            String sql, org1, cu1, team1, name1, name2;
            String[] organi;
            List<String> orgs = new ArrayList<String>();
            sql = "SELECT Signum, Last_Name, Name, Customer_Unit, Team, Organization, Line_Manager, Access, Supporting_Team, Supporting_CU, Job_Stage, Act_Type, CATS_Number"
                    + " FROM users ORDER BY Last_Name asc;";
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultset = preparedStatement.executeQuery();
            while (resultset.next()) {
                for (int i = 0; i < 13; i++) {
                    row[i] = resultset.getString(i + 1);
                }
                org1 = resultset.getString("Organization");
                cu1 = resultset.getString("Customer_Unit");
                team1 = resultset.getString("Team");
                name1 = resultset.getString("Last_Name") + " " + resultset.getString("Name");
                name2 = resultset.getString("Last_Name") + ", " + resultset.getString("Name");
                if (!user_names.isEmpty()) {
                    if (!user_names.contains(name1)) {
                        user_names.add(name1);
                        jCBUser.addItem(name1);
                        jCBSearchUser.addItem(name2);
                    }
                } else {
                    user_names.add(name1);
                    jCBUser.addItem(name1);
                    jCBSearchUser.addItem(name2);
                }
                if (!organizations.isEmpty()) {
                    if (!organizations.contains(org1)) {
                        organizations.add(org1);
                        //jCBOrganization.addItem(org1);
                        organi = org1.split("MX ");
                        //if (organi.length > 1)
                        System.out.println("Org: " + organi[1] + ".");
                        orgs.add(organi[1]);
                    }
                } else {
                    organizations.add(org1);
                    //jCBOrganization.addItem(org1);
                    organi = org1.split("MX ");
                    System.out.println("Org: " + organi[1]);
                    orgs.add(organi[1]);
                }
                model.addRow(row);
            }
            Collections.sort(orgs);
            for (int i = 0; i<orgs.size(); i++){
                if (!orgs.get(i).equals("MANA"))
                    jCBOrgMetrics.addItem(orgs.get(i));
                else
                    jCBOrgMetrics.addItem("All");
            }
            jTUsersList.setModel(model);
            resizeColumnWidth(jTUsersList);
            //jTUsersList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error in GetAllUsers");
            System.out.println(e);
        }
    }

    private void GetAllTasks() {
        servicePackageNames.clear();
        deliverableList.clear();
        projectSupportNames.clear();
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultset;
        String[] column = {"Task_ID", "Task", "Team", "Customer Unit", "Service Package Name", 
            "Deliverable", "Project Support Domain", "LoE"};
        String[] row = new String[8];
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(column);
        try {
            String sql, taskid1, task1, team1, cu1;
            sql = "SELECT Task_ID, Task, Team, Customer_Unit, Service_Package_Name, Deliverable, "
                    + "Project_Support_Domain, LoE FROM tasks ORDER BY Team asc;";
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultset = preparedStatement.executeQuery();
            ArrayList<String> _teams = new ArrayList<>();
            while (resultset.next()) {
                String[] row1 = {resultset.getString("Team"), resultset.getString("Customer_Unit")};
                // If task has same name but different task_id
                if (teamsAndCUs.isEmpty()) {
                    List<String> newList = Arrays.asList(row1);
                    teamsAndCUs.addAll(newList);
                } else {
                    int noRepeats = 0;
                    for (int i = 0; i < teamsAndCUs.size(); i++) {
                        if (i % 2 == 0) {
                            if (teamsAndCUs.get(i).equals(row1[0]) && teamsAndCUs.get(i + 1).equals(row1[1])) {
                                noRepeats += 1;
                            }
                        }
                    }
                    if (noRepeats == 0) {
                        List<String> newList = Arrays.asList(row1);
                        teamsAndCUs.addAll(newList);
                    }
                }
                
                String spn = resultset.getString("Service_Package_Name");
                spn +="#" + resultset.getString("Team");
                if (servicePackageNames.size() == 0){
                    servicePackageNames.add(spn);
                } else {
                    if (!servicePackageNames.contains(spn)) {
                        servicePackageNames.add(spn);
                    }
                }
                
                String deliv = resultset.getString("Deliverable");
                deliv += "#" + resultset.getString("Team");
                if (deliverableList.size() == 0){
                    deliverableList.add(deliv);
                } else {
                    if (!deliverableList.contains(deliv)) {
                        deliverableList.add(deliv);
                    }
                }
                
                String psd = resultset.getString("Project_Support_Domain");
                psd += "#" + resultset.getString("Team");
                if (projectSupportNames.size() == 0){
                    projectSupportNames.add(psd);
                } else {
                    if (!projectSupportNames.contains(psd)) {
                        projectSupportNames.add(psd);
                    }
                }
                
                
                for (int i = 0; i < 8; i++) {
                    row[i] = resultset.getString(i + 1);
                }
                taskid1 = resultset.getString("Task_ID");
                task1 = resultset.getString("Task");
                team1 = resultset.getString("Team");
                cu1 = resultset.getString("Customer_Unit");
                if (!tasks.isEmpty()) {
                    if (!tasks.contains(task1)) {
                        tasks.add(task1);
                        String[] taskk = {taskid1, task1};
                        List<String> newList = Arrays.asList(taskk);
                        taskIDandTasks.addAll(newList);
                    } else {
                        task1 = task1 + "  ";
                        int flag = 0;
                        while (flag == 0){
                            if (!tasks.contains(task1)){
                                tasks.add(task1);
                                String[] taskk = {taskid1, task1};
                                List<String> newList = Arrays.asList(taskk);
                                taskIDandTasks.addAll(newList);
                                flag = 1;
                            }
                            else {
                                task1 = task1 + "  ";
                            }
                        }
                    }
                } else {
                    tasks.add(task1);
                    String[] taskk = {taskid1, task1};
                    List<String> newList = Arrays.asList(taskk);
                    taskIDandTasks.addAll(newList);
                }
                
                String[] row2 = {resultset.getString("Team"), resultset.getString("Customer_Unit"), task1};
                if (teamsCUSTasks.isEmpty()) {
                    List<String> newList2 = Arrays.asList(row2);
                    teamsCUSTasks.addAll(newList2);
                } else {
                    int noRepeats = 0;
                    for (int i = 0; i < teamsCUSTasks.size(); i++) {
                        if (i % 3 == 0) {
                            if (teamsCUSTasks.get(i).equals(row2[0]) && teamsCUSTasks.get(i + 1).equals(row2[1]) && teamsCUSTasks.get(i + 2).equals(row2[2])) {
                                noRepeats += 1;
                            }
                        }
                    }
                    if (noRepeats == 0) {
                        List<String> newList2 = Arrays.asList(row2);
                        teamsCUSTasks.addAll(newList2);
                    }
                }                
                if (!_teams.contains(team1)){
                   _teams.add(team1);
                }                              
                model.addRow(row);
            }
            for (int i = 0; i < _teams.size(); i++){
                teams.add(_teams.get(i));
                jCBTaskTeam.addItem(_teams.get(i));
                jCBTeam.addItem(_teams.get(i));
                jCBSupportedTeam.addItem(_teams.get(i));
                jCBTeamTaskSearch.addItem(_teams.get(i));
            }
            Collections.sort(tasks);
            Collections.sort(servicePackageNames);
            Collections.sort(deliverableList);
            Collections.sort(projectSupportNames);
            //for (int i = 0; i<tasks.size(); i++)
            //    jCBTaskSearch.addItem(tasks.get(i));
            jTTasksList.setModel(model);
            resizeColumnWidth(jTTasksList);
            //jTTasksList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            connection.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    
    private void GetAllMarkets(){
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultset;
        String markID1, mrkt1, reg1, team1, cu1;
        try{
            String sql = "SELECT id, Market, Region, Team, Customer_Unit FROM markets;";
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultset = preparedStatement.executeQuery();
            while (resultset.next()){
                markID1 = resultset.getString("id");
                mrkt1 = resultset.getString("Market");
                reg1 = resultset.getString("Region");
                team1 = resultset.getString("Team");
                cu1 = resultset.getString("Customer_Unit");
                //marketTeamCU
                String[] row1 = {markID1, mrkt1, reg1, team1, cu1};
                String[] row2 = {team1, cu1, reg1, mrkt1};
                if (marketTeamCU.isEmpty()) {
                    List<String> newList = Arrays.asList(row1);
                    marketTeamCU.addAll(newList);
                } else {
                    int noRepeats = 0;
                    for (int i = 0; i < marketTeamCU.size(); i++) {
                        if (i % 4 == 0) {
                            if (marketTeamCU.get(i).equals(row1[0]) && marketTeamCU.get(i + 1).equals(row1[1]) && marketTeamCU.get(i + 2).equals(row1[2]) && marketTeamCU.get(i + 3).equals(row1[3])) {
                                noRepeats += 1;
                            }
                        }
                    }
                    if (noRepeats == 0) {
                        List<String> newList = Arrays.asList(row1);
                        marketTeamCU.addAll(newList);
                    }
                }
                
                if (netTeamCURegMark.isEmpty()) {
                    List<String> newList1 = Arrays.asList(row2);
                    netTeamCURegMark.addAll(newList1);
                } else {
                    int nuRepeats = 0;
                    for (int i = 0; i < netTeamCURegMark.size(); i++) {
                        if (i % 4 == 0) {
                            if (netTeamCURegMark.get(i).equals(row2[0]) && netTeamCURegMark.get(i + 1).equals(row2[1]) && netTeamCURegMark.get(i + 2).equals(row2[2]) && netTeamCURegMark.get(i + 3).equals(row2[3])) {
                                nuRepeats += 1;
                            }
                        }
                    }
                    if (nuRepeats == 0) {
                        List<String> newList1 = Arrays.asList(row2);
                        netTeamCURegMark.addAll(newList1);
                    }
                }
                
                
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        
    }
    
    private void InsertIntoMarketDB() {
        Connection connection;
        PreparedStatement preparedStatement;
        //ResultSet resultset;
        try {
            String region = jCBRegionMrkt.getItemAt(jCBRegionMrkt.getSelectedIndex());
            String marketName = String.valueOf(jCBMarketList.getEditor().getItem());
            String teamMarket = jCBTeamMrkt.getItemAt(jCBTeamMrkt.getSelectedIndex());
            String cuMarket = jCBCUMrkt.getItemAt(jCBCUMrkt.getSelectedIndex());
            String id = "";
            
            String cu1 = cuMarket.replace("&", "").replace("-","");
            id = region.substring(0,3).toUpperCase() + "-" + teamMarket.substring(0,3).toUpperCase() + "-" + cu1.substring(0, 3).toUpperCase() + "-" + marketName.substring(0, 3).toUpperCase();
            int contador = 1;
            for (int i = 0; i < marketTeamCU.size(); i++){
                if (i % 5 == 0){
                    if (marketTeamCU.get(i).equals(id + String.valueOf(contador)))
                        contador ++;
                }
            }
            id = id + String.valueOf(contador);
            
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO markets (id, Market, Region, Team, Customer_Unit) "
                    + "VALUES (?, ?, ?, ?, ?)");

            preparedStatement.setObject(1, id);
            preparedStatement.setObject(2, marketName);
            preparedStatement.setObject(3, region);
            preparedStatement.setObject(4, teamMarket);
            preparedStatement.setObject(5, cuMarket);
            preparedStatement.executeUpdate();

            /*resultset = preparedStatement.getResultSet();
            if (resultset.next())
                System.out.println("Mensaje: " + resultset.getString("mens"));*/
            System.out.println("Query: " + preparedStatement);
            connection.close();

            JOptionPane.showMessageDialog(this, "Market saved successfully.");
        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Something went wrong, please try again later.");
        }
    }
    
    private void UpdateMarketsDB() {
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            String region = jCBMarRegion.getItemAt(jCBMarRegion.getSelectedIndex());
            String marketName = jCBMarMrkt.getItemAt(jCBMarMrkt.getSelectedIndex());
            String newMarket = jCBMarketList.getItemAt(jCBMarketList.getSelectedIndex());
            String teamMarket = jCBMarTeam.getItemAt(jCBMarTeam.getSelectedIndex());
            String cuMarket = jCBMarCU.getItemAt(jCBMarCU.getSelectedIndex());
            String id = "";
            for (int i = 0; i < marketTeamCU.size(); i++){
                if (i % 5 == 0){
                    if (marketName.equals(marketTeamCU.get(i + 1)) && region.equals(marketTeamCU.get(i + 2)) && teamMarket.equals(marketTeamCU.get(i + 3)) && cuMarket.equals(marketTeamCU.get(i + 4)))
                        id = marketTeamCU.get(i);
                }
            }

            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("UPDATE markets SET Market = ?"
                    + "WHERE id = ?");
            preparedStatement.setObject(1, newMarket);
            preparedStatement.setObject(2, id);
            preparedStatement.executeUpdate();
            System.out.println("Query: " + preparedStatement);
            connection.close();

            JOptionPane.showMessageDialog(this, "Market updated successfully.");
        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Something went wrong, please try again later.");
        }
    }
    
    private void SearchMarketkDB() {
        String region = jCBMarRegion.getItemAt(jCBMarRegion.getSelectedIndex());
        String marketName = jCBMarMrkt.getItemAt(jCBMarMrkt.getSelectedIndex());
        String teamMarket = jCBMarTeam.getItemAt(jCBMarTeam.getSelectedIndex());
        String cuMarket = jCBMarCU.getItemAt(jCBMarCU.getSelectedIndex());
        
        jCBTeamMrkt.setSelectedItem(teamMarket);
        jCBCUMrkt.setSelectedItem(cuMarket);
        jCBRegionMrkt.setSelectedItem(region);
        jCBMarketList.setSelectedItem(marketName);
    }
    
    private void DeleteMarketDB() {
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            String id = "";
            String teamMrkt = jCBMarTeam.getItemAt(jCBMarTeam.getSelectedIndex());
            String cuMrkt = jCBMarCU.getItemAt(jCBMarCU.getSelectedIndex());
            String regMrkt = jCBMarRegion.getItemAt(jCBMarRegion.getSelectedIndex());
            String marketName = jCBMarMrkt.getItemAt(jCBMarMrkt.getSelectedIndex());
            for (int i = 0; i < marketTeamCU.size(); i++)
                if (i % 5 == 0)
                    if (marketName.equals(marketTeamCU.get(i + 1)) && regMrkt.equals(marketTeamCU.get(i + 2)) && teamMrkt.equals(marketTeamCU.get(i + 3)) && cuMrkt.equals(marketTeamCU.get(i + 4)))
                        id = marketTeamCU.get(i);

            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("DELETE FROM markets "
                    + "WHERE id = ?");
            preparedStatement.setObject(1, id);
            preparedStatement.executeUpdate();
            System.out.println("Query: " + preparedStatement);
            connection.close();

            for (int i = 0; i < netTeamCURegMark.size(); i++){
                if (i % 4 == 0){
                    if (teamMrkt.equals(netTeamCURegMark.get(i)) && cuMrkt.equals(netTeamCURegMark.get(i + 1)) && regMrkt.equals(netTeamCURegMark.get(i + 2)) && marketName.equals(netTeamCURegMark.get(i + 3))){
                        for (int j = 0; j < 4; j++)
                            netTeamCURegMark.remove(i);
                        System.out.println("Deleting...");
                    }
                }
            }
            
            JOptionPane.showMessageDialog(this, "Market deleted successfully.");
        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Something went wrong, please try again later.");
        }
    }

    private void InsertIntoUserDB() {
        Connection connection;
        PreparedStatement preparedStatement;
        //ResultSet resultset;
        try {
            int access = jCBAccess.getSelectedIndex();
            String supportingCU = jTFSupCU.getText();
            String supportingTeam = jTFSupTeam.getText();
            String team1 = jCBTeam.getSelectedItem().toString();
            String cu1 = jCBCustomerUnit.getSelectedItem().toString();
            String cat1 = jTFCATNum.getText();
            String js1 = jCBJobStage.getItemAt(jCBJobStage.getSelectedIndex());
            String lmsignum1 = LMSignums[jCBLineManager.getSelectedIndex()];
            String org1 = LMOrganizations[jCBLineManager.getSelectedIndex()];
            String acttype1 = ActTypes[jCBJobStage.getSelectedIndex()];
            
            HashMap<String, String> team_list = new HashMap<String, String>();

            // Add keys and values (Team name, Prefix)
            team_list.put("COP", "C_");
            team_list.put("VSS", "V_");
            team_list.put("PSS", "P_");
            team_list.put("Scoping", "SCP_");  
            team_list.put("Sourcing", "");
            
            if (!team1.equals("SDU") && !cu1.equals("")){
                cu1 = team_list.get(team1) + cu1;                
            }           
            
            if (!js1.equals("N/A"))
                js1 = "Job Stage " + js1;
            
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO users (Signum, Last_Name, Name, Customer_Unit, Team, Organization, Line_Manager, Access, Supporting_Team, Supporting_CU, Job_Stage, Act_Type, CATS_Number) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            preparedStatement.setObject(1, jTFSignum.getText());
            preparedStatement.setObject(2, jTFLastName.getText());
            preparedStatement.setObject(3, jTFName.getText());
            preparedStatement.setObject(4, cu1);
            preparedStatement.setObject(5, team1);
            preparedStatement.setObject(6, org1);
            preparedStatement.setObject(7, lmsignum1);
            preparedStatement.setObject(8, access);
            preparedStatement.setObject(9, supportingTeam);
            preparedStatement.setObject(10, supportingCU);
            preparedStatement.setObject(11, js1);
            preparedStatement.setObject(12, acttype1);
            preparedStatement.setObject(13, cat1);
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
            String supportingCU = jTFSupCU.getText();
            String supportingTeam = jTFSupTeam.getText();
            String team1 = jCBTeam.getSelectedItem().toString();
            String cu1 = jCBCustomerUnit.getSelectedItem().toString();
            String cat1 = jTFCATNum.getText();
            String js1 = jCBJobStage.getItemAt(jCBJobStage.getSelectedIndex());
            String lmsignum1 = LMSignums[jCBLineManager.getSelectedIndex()];
            String org1 = LMOrganizations[jCBLineManager.getSelectedIndex()];
            String acttype1 = ActTypes[jCBJobStage.getSelectedIndex()];
            
            HashMap<String, String> CU_list = new HashMap<String, String>();

            // Add keys and values (Team name, Prefix)
            CU_list.put("COP", "C_");
            CU_list.put("VSS", "V_");
            CU_list.put("PSS", "P_");
            CU_list.put("Scoping", "SCP_");  
            CU_list.put("Sourcing", "");
            
            if (!team1.equals("SDU") && !cu1.equals("")){
                cu1 = CU_list.get(team1) + cu1;                
            }             
            
            if (!js1.equals("N/A"))
                js1 = "Job Stage " + js1;

            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("UPDATE users SET "
                    + "Customer_Unit = ?, Team = ?, Organization = ?, "
                    + "Line_Manager = ?, Access = ?, Supporting_Team = ?, "
                    + "Supporting_CU = ?, Job_Stage = ?, Act_type = ?, "
                    + "CATS_Number = ? "
                    + "WHERE Signum = ?;");
            preparedStatement.setObject(1, cu1);
            preparedStatement.setObject(2, team1);
            preparedStatement.setObject(3, org1);
            preparedStatement.setObject(4, lmsignum1);
            preparedStatement.setObject(5, access);
            preparedStatement.setObject(6, supportingTeam);
            preparedStatement.setObject(7, supportingCU);
            preparedStatement.setObject(8, js1);
            preparedStatement.setObject(9, acttype1);
            preparedStatement.setObject(10, cat1);
            preparedStatement.setObject(11, jTFSignum.getText());
            System.out.println("Query: " + preparedStatement);
            preparedStatement.executeUpdate();
            
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
                    + "WHERE Last_Name = ?");
            preparedStatement.setObject(1, jCBSearchUser.getItemAt(jCBSearchUser.getSelectedIndex()));
            preparedStatement.executeUpdate();
            System.out.println("Query: " + preparedStatement);
            connection.close();

            JOptionPane.showMessageDialog(this, "User deleted successfully.");
            jCBSearchUser.removeItemAt(jCBSearchUser.getSelectedIndex());
            jCBSearchUser.setSelectedIndex(0);
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
            int idd = 0, countidd = 0;
            String ID = "";
            String countID = "";
            String task = jTFTaskName.getText();
            String type = jCBTaskType.getItemAt(jCBTaskType.getSelectedIndex());
            String team = jCBTaskTeam.getItemAt(jCBTaskTeam.getSelectedIndex());
            String cu = jCBTaskCU.getItemAt(jCBTaskCU.getSelectedIndex());
            String spn = jCBServicePN.getItemAt(jCBServicePN.getSelectedIndex());
            String deliv = jCBDeliverable.getItemAt(jCBDeliverable.getSelectedIndex());
            String psd = jCBProjectSuppDom.getItemAt(jCBProjectSuppDom.getSelectedIndex());
            String loe = jTFLOE.getText();
            String billable = "Billable";
            int index = jCBTaskType.getSelectedIndex();
            if (index == 0) {
                billable = "Not Billable";
            }

            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("SELECT Task_ID FROM tasks "// COUNT(Task_ID) AS counter,
                    + "WHERE Task_ID LIKE '" + type + "%' ORDER BY Task_ID DESC LIMIT 1;");
            System.out.println("Query: " + preparedStatement);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                ID = resultSet.getString("Task_ID");
                //countID = resultSet.getString("counter");
            }
            int indexID = ID.lastIndexOf("-");
            String idd2 = ID.substring(indexID + 1);
            System.out.println("Length of ID: " + idd2.length());
            System.out.println("Latest Task_ID: " + ID);
            idd = Integer.valueOf(idd2) + 1;
            System.out.println("Length of Integer ID: " + String.valueOf(idd).length());
            ID = type + "-" + new String(new char[idd2.length() - String.valueOf(idd).length()]).replace("\0", "0") + idd;
            System.out.println("New ID: " + ID);
            if (loe.equals("")){
                loe = "-1.00";
            }
            preparedStatement = connection.prepareStatement("INSERT INTO tasks (Task_ID, Task, Team, Customer_Unit, SAP_Billable, Service_Package_Name, Deliverable, Project_Support_Domain, LoE) "
                    + "VALUES (?,?,?,?,?,?,?,?,?);");
            preparedStatement.setObject(1, ID);
            preparedStatement.setObject(2, task);
            preparedStatement.setObject(3, team);
            preparedStatement.setObject(4, cu);
            preparedStatement.setObject(5, billable);
            preparedStatement.setObject(6, spn);
            preparedStatement.setObject(7, deliv);
            preparedStatement.setObject(8, psd);
            preparedStatement.setObject(9, loe);
            preparedStatement.executeUpdate();
            System.out.println("Insert Query: " + preparedStatement);
            connection.close();

            JOptionPane.showMessageDialog(this, "Task with ID: " + ID + " saved successfully.");
            jCBTaskType.setSelectedIndex(0);
            jTFTaskName.setText("");
            jCBTaskTeam.setSelectedIndex(0);
            jTFLOE.setText("");
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
            String spn = jCBServicePN.getItemAt(jCBServicePN.getSelectedIndex());
            String deliv = jCBDeliverable.getItemAt(jCBDeliverable.getSelectedIndex());
            String psd = jCBProjectSuppDom.getItemAt(jCBProjectSuppDom.getSelectedIndex());
            String loe = jTFLOE.getText();

            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("UPDATE tasks SET Team = ?, Service_Package_Name = ?, Deliverable = ?, Project_Support_Domain = ?, LoE = ? "
                    + "WHERE Task = ?");
            preparedStatement.setObject(1, team);
            preparedStatement.setObject(2, spn);
            preparedStatement.setObject(3, deliv);
            preparedStatement.setObject(4, psd);
            preparedStatement.setObject(5, loe);
            preparedStatement.setObject(6, task);
            preparedStatement.executeUpdate();
            System.out.println("Query: " + preparedStatement);
            connection.close();

            JOptionPane.showMessageDialog(this, "Task updated successfully.");
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
            String task_id = "";
            String task = jCBTaskSearch.getSelectedItem().toString();
            //Get task id
            for (int i = 0; i < taskIDandTasks.size(); i++){
                if (i%2 == 0){
                    if (task.equals(taskIDandTasks.get(i+1))){
                        task_id = taskIDandTasks.get(i);
                    }
                }
            }
            tid = "";
            String taskname = "", team = "", cu = "", spn = "", deliv = "", psd = "", loe = "";
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("SELECT Task_ID, Task, Team, Customer_Unit, Service_Package_Name, Deliverable, Project_Support_Domain, LoE FROM tasks "
                    + "WHERE Task_ID = ?;");
            preparedStatement.setObject(1, task_id);
            System.out.println("Query: " + preparedStatement);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                tid = resultSet.getString("Task_ID");
                taskname = resultSet.getString("Task");
                team = resultSet.getString("Team");
                cu = resultSet.getString("Customer_Unit");
                spn = resultSet.getString("Service_Package_Name");
                deliv = resultSet.getString("Deliverable");
                psd = resultSet.getString("Project_Support_Domain");
                loe = resultSet.getString("LoE");
                
                int index = tid.lastIndexOf("-");
                String task_type = tid.substring(index);
                System.out.println(tid + taskname + team + cu + spn + deliv + psd + loe);
                jTFTaskName.setText(taskname);
                jCBTaskTeam.setSelectedItem(team);
                jCBTaskType.setSelectedItem(task_type);
                jCBTaskCU.setSelectedItem(cu);
                jCBServicePN.setSelectedItem(spn);
                jCBDeliverable.setSelectedItem(deliv);
                jCBProjectSuppDom.setSelectedItem(psd);
                jTFLOE.setText(loe);
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
            preparedStatement = connection.prepareStatement("DELETE FROM tasks "
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
            String activityCode = jCBNetActCode.getItemAt(jCBNetActCode.getSelectedIndex());
            String responsible = jTFResponsible.getText();
            String region = jCBNetRegion.getItemAt(jCBNetRegion.getSelectedIndex());
            String market = jCBNetMarket.getItemAt(jCBNetMarket.getSelectedIndex());
            String customer = jCBNetCustomer.getItemAt(jCBNetCustomer.getSelectedIndex());
            String subnetwork = jTFSubnetwork.getText();
            String team = jCBNetTeam.getItemAt(jCBNetTeam.getSelectedIndex());
            String technology = jCBNetTech.getSelectedItem().toString();
            System.out.println("Technology: " + technology);
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO networks (PD, Network, Activity_code, Region, Market, Customer, Responsible, Subnetwork, Team, Technology) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?);");
            preparedStatement.setObject(1, pd);
            preparedStatement.setObject(2, net);
            preparedStatement.setObject(3, activityCode);
            preparedStatement.setObject(4, region);
            preparedStatement.setObject(5, market);
            preparedStatement.setObject(6, customer);
            preparedStatement.setObject(7, responsible);
            preparedStatement.setObject(8, subnetwork);
            preparedStatement.setObject(9, team);
            preparedStatement.setObject(10, technology);
            preparedStatement.executeUpdate();
            connection.close();

            JOptionPane.showMessageDialog(this, "Network: " + net + " with Subnetwork: " + subnetwork + " saved successfully.");
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
            String team1 = jCBNetTeamSearch.getItemAt(jCBNetTeamSearch.getSelectedIndex());
            String net1 = jCBNetSearch.getItemAt(jCBNetSearch.getSelectedIndex());
            String pd = "", activity = "", responsible = "", subnetwork = "", region = "", market = "", customer = "", team = "", tech = "";
            String network = "";
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("SELECT PD, Network, Activity_code, Region, Market, Team, Customer, Responsible, Subnetwork, Technology FROM networks "
                    + "WHERE Network = ? AND Team = ?;");
            preparedStatement.setObject(1, net1);
            preparedStatement.setObject(2, team1);
            System.out.println("Query: " + preparedStatement);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                pd = resultSet.getString("PD");

                activity = resultSet.getString("Activity_code");
                network = resultSet.getString("Network");
                region = resultSet.getString("Region");
                market = resultSet.getString("Market");
                customer = resultSet.getString("Customer");
                responsible = resultSet.getString("Responsible");
                subnetwork = resultSet.getString("Subnetwork");
                team = resultSet.getString("Team");
                tech = resultSet.getString("Technology");

                jTFPD.setText(pd);
                jTFNetwork.setText(network);
                jCBNetActCode.setSelectedItem(activity);
                jTFResponsible.setText(responsible);
                jCBNetTeam.setSelectedItem(team);
                jCBNetCustomer.setSelectedItem(customer);
                jCBNetRegion.setSelectedItem(region);
                jCBNetMarket.setSelectedItem(market);
                jCBNetTech.setSelectedItem(tech);
                jTFSubnetwork.setText(subnetwork);
            } else {
                JOptionPane.showMessageDialog(this, "Network not found! Please try with another one");
            }

            connection.close();
        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Something went wrong, please try again later.");
        }
    }

    private void DeleteNetwork() {
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            String net1 = jCBNetSearch.getItemAt(jCBNetSearch.getSelectedIndex());
            String team1 = jCBNetTeamSearch.getItemAt(jCBNetTeamSearch.getSelectedIndex());
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("DELETE FROM networks "
                    + "WHERE Network = ? AND Team = ?;");
            preparedStatement.setObject(1, net1);
            preparedStatement.setObject(2, team1);
            preparedStatement.executeUpdate();
            System.out.println("Query: " + preparedStatement);
            connection.close();

            JOptionPane.showMessageDialog(this, "Subnetwork deleted successfully.");

        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Something went wrong, please try again later.");
        }
    }

    private void UpdateNetworksDB() {
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            String act1 = jCBNetActCode.getItemAt(jCBNetActCode.getSelectedIndex());
            String resp1 = jTFResponsible.getText();
            String subnet1 = jTFSubnetwork.getText();
            String cu1 = jCBNetCustomer.getItemAt(jCBNetCustomer.getSelectedIndex());
            String reg1 = jCBNetRegion.getItemAt(jCBNetRegion.getSelectedIndex());
            String mark1 = jCBNetMarket.getItemAt(jCBNetMarket.getSelectedIndex());
            String net1 = jTFNetwork.getText();
            String team1 = jCBNetTeam.getItemAt(jCBNetTeam.getSelectedIndex());
            String technology = jCBNetTech.getSelectedItem().toString();

            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("UPDATE networks SET Activity_code = ?, Responsible = ?, "
                    + "Subnetwork = ?,  Customer = ?, Region = ?, Market = ?, Technology = ? "
                    + "WHERE (Network = ?) AND (Team = ?);");
            preparedStatement.setObject(1, act1);
            preparedStatement.setObject(2, resp1);
            preparedStatement.setObject(3, subnet1);
            preparedStatement.setObject(4, cu1);
            preparedStatement.setObject(5, reg1);
            preparedStatement.setObject(6, mark1);
            preparedStatement.setObject(7, technology);
            preparedStatement.setObject(8, net1);
            preparedStatement.setObject(9, team1);
            System.out.println("Query: " + preparedStatement);
            preparedStatement.executeUpdate();
            connection.close();

            JOptionPane.showMessageDialog(this, "Network updated successfully.");
            ResetNetworkFields();
        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Something went wrong, please try again later.");
        }
    }

    private boolean ReviewFieldsState() {
        boolean flag = false;

        if (!jTFPD.getText().equals("") && !jTFNetwork.getText().equals("")
                && !jTFResponsible.getText().equals("") && !jTFSubnetwork.getText().equals("")) {
            flag = true;
        } 
        return flag;
    }

    private void ResetUserFields() {
        jTFSignum.setText("");
        jTFName.setText("");
        jTFLastName.setText("");
        //jCBOrganization.setSelectedIndex(0);
        jCBTeam.setSelectedIndex(0);
        jCBCustomerUnit.setSelectedIndex(0);
        jCBLineManager.setSelectedIndex(0);
        jCBSupportedCU.setSelectedIndex(0);
        //jTFSignumEdit.setText("");
        jTFSupTeam.setText("N/A");
        jTFSupCU.setText("N/A");
        jTFCATNum.setText("");
    }

    private void ResetNetworkFields() {
        jTFPD.setText("");
        jTFNetwork.setText("");
        jCBNetActCode.setSelectedIndex(0);
        jTFResponsible.setText("");
        jTFSubnetwork.setText("");
        //jCBNetTeam.setSelectedIndex(0);
        //jCBNetCustomer.setSelectedIndex(0);
        //jCBNetRegion.setSelectedIndex(0);
        //jCBNetMarket.setSelectedIndex(0);
        //jCBNetTech.setSelectedIndex(0);
        jCBNetTeamSearch.setSelectedIndex(0);
    }

    private void ResetTaskFields() {
        //jCBTeamTaskSearch.setSelectedIndex(0);
        //jCBTaskType.setSelectedIndex(0);
        jTFTaskName.setText("");
        jCBTaskTeam.setSelectedIndex(0);
        jCBServicePN.setSelectedIndex(0);
        jCBDeliverable.setSelectedIndex(0);
        jCBProjectSuppDom.setSelectedIndex(0);
        jTFLOE.setText("");
    }

    private void SaveTableCSV(String fileName, TableModel tableModel) throws IOException {
        String user = System.getProperty("user.name");
        String path = "C:\\Users\\" + user + "\\Documents\\" + fileName + ".csv";
        try (PrintWriter writer = new PrintWriter(new File(path))) {

            int columnas = tableModel.getColumnCount();
            int filas = tableModel.getRowCount();
            System.out.println("Row Count: " + filas);
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < columnas; j++) {
                sb.append(tableModel.getColumnName(j) + ",");
            }
            sb.append("\n");
            for (int i = 0; i < filas; i++) {
                for (int j = 0; j < columnas; j++) {
                    String s = String.valueOf(tableModel.getValueAt(i, j));
                    s = s.replace(",", " ");
                    s = s.replace("\n", " ");
                    sb.append("\"" + s + "\"" + ",");
                }
                sb.append("\n");
            }
            
            writer.write(sb.toString());
            System.out.println("Template generated successfully");
            int reply = JOptionPane.showConfirmDialog(null, "Do you want to open it?", "CSV created", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                Desktop.getDesktop().open(new File(path));
            } else {
                JOptionPane.showMessageDialog(this, "Template file was saved to " + path);
            }
            //JOptionPane.showMessageDialog(this, "CSV file was saved to " + path + " successfully.");
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

    private void ShowLoggedTime() {
        String parametros = new String();
        String orden = new String();
        String organization = new String();
        String week = "", name = "";
        List<String> columnas = new ArrayList<String>();
        String fileName = "Metrics ";
        String team1 = jCBMetricTeam.getItemAt(jCBMetricTeam.getSelectedIndex());
        String orgn1 = jCBOrgMetrics.getItemAt(jCBOrgMetrics.getSelectedIndex());
        team1 = team1.toLowerCase();
        String yearFrom1 = jCBYearFrom.getItemAt(jCBYearFrom.getSelectedIndex());
        String yearTo1 = jCBYearTo.getItemAt(jCBYearTo.getSelectedIndex());
        int rowCounter = 0;

        if (jRBSignum.isSelected()) {
            parametros = parametros + "Signum, ";
            columnas.add("Signum");
            orden = orden + "Signum, ";
        }
        if (jRBName.isSelected()) {
            parametros = parametros + "Name, ";
            columnas.add("Name");
            orden = orden + "Name, ";
            if (jCBUser.getSelectedIndex() != 0) {
                name = " AND Name = '" + jCBUser.getSelectedItem() + "'";
            }
        }
        /*if (jRBTeam.isSelected()) {
            parametros = parametros + "Team, ";
            columnas.add("Team");
            orden = orden + "Team, ";
            if (jCBMetricTeam.getSelectedIndex() != 0)
                team = " AND Team = '" + jCBMetricTeam.getSelectedItem() + "'";
        }*/
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
                week = "WHERE Week = " + weekFrom + " AND YEAR(Work_date) = " + yearFrom1;
                fileName = fileName + "Week " + weekFrom + " Year " + yearFrom1;
            } else {
                week = "WHERE (Week BETWEEN " + weekFrom + " AND " + weekTo + ") AND (YEAR(Work_date) BETWEEN " + yearFrom1 + " AND " + yearTo1 + ")";
                fileName = fileName + "From Week " + weekFrom + " TO " + weekTo + " Year Between " + yearFrom1 + " and " + yearTo1;
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
            date1 =  yearFrom1 + "-" + String.valueOf(mFrom) + "-01";
            if (jRBTo.isSelected()) {
                date2 = yearTo1 + "-" + String.valueOf(mTo) + "-" + String.valueOf(days[mTo - 1]);
                fileName = fileName + "From " + monthFrom + " To " + monthTo;
            } else {
                date2 = yearFrom1 + "-" + String.valueOf(mFrom) + "-" + String.valueOf(days[mFrom - 1]);
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
            date1 = yearFrom1 + "-" + quarterFrom[monthFrom] + "-01";
            if (jRBTo.isSelected()) {
                date2 = yearTo1 + "-" + quarterTo[monthTo] + "-" + daysQuarter[monthTo];
                fileName = fileName + "From Q" + monthFrom + " To Q" + monthTo;
            } else {
                date2 = yearFrom1 + "-" + quarterTo[monthFrom] + "-" + daysQuarter[monthFrom];
                fileName = fileName + "From Q" + monthFrom;
            }
            parametros = parametros + "Work_date, ";
            columnas.add("Month");
            orden = "MONTH(Work_date), " + orden;
            week = "WHERE Work_date BETWEEN '" + date1 + "' AND '" + date2 + "'";
        }
        
        if (orgn1 != "All"){
            organization = " AND Organization LIKE '%" + orgn1 + "'";
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
            String query;
            if (team1.equals("all")) {
                query = "SELECT " + parametros + "SUM(Logged_Time) FROM "
                        + "(SELECT Signum, Organization, Name, Task_ID, Task, Network, Subnetwork, SAP_Billing, Work_date, Logged_Time, Week "
                        + "FROM metrics_sourcing UNION ALL "
                        + "SELECT Signum, Organization, Name, Task_ID, Task, Network, Subnetwork, SAP_Billing, Work_date, Logged_Time, Week "
                        + "FROM metrics_cop UNION ALL "
                        + "SELECT Signum, Organization, Name, Task_ID, Task, Network, Subnetwork, SAP_Billing, Work_date, Logged_Time, Week "
                        + "FROM metrics_vss UNION ALL "
                        + "SELECT Signum, Organization, Name, Task_ID, Task, Network, Subnetwork, SAP_Billing, Work_date, Logged_Time, Week "
                        + "FROM metrics_pss) LOGGEDTIME "
                        + week + organization + name + " GROUP BY " + orden + " ORDER BY " + orden + " LIMIT 18446744073709551615;";
            } else {
                query = "SELECT " + parametros + "SUM(Logged_Time) FROM metrics_" + team1 + " " + week + organization + name + " GROUP BY " + orden + " ORDER BY " + orden + " LIMIT 18446744073709551615;";
            }
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
                    rowCounter += 1;
                    model.addRow(valores);
                } while (rs.next());
            }
            jTFFileName.setText(fileName);
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(Time_Review.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Total rows added: " + rowCounter);
        jTableShowMetrics.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTableShowMetrics.setModel(model);
        resizeColumnWidth(jTableShowMetrics);
    }

    private void ShowMetrics() {
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        String[] header = {"Region", "Organization", "Signum", "Name", "Customer Unit", "Requestor", "Task ID", "Task", "Network", "Subnetwork", "Activity Code", "SAP Billing",
            "Work Date", "Logged Time", "Week", "Market", "Technology", "FTR", "On Time", "Failed FTR Category", "Failed On Time", "Number of requests", "Comments"};
        //Technology, Customer_Unit, Market
        //Region, Organization, Signum, Name, Customer_Unit, Requestor, Task_ID, Task, Network, Subnetwork, Activity_Code, SAP_Billing, Work_Date, Logged_Time, Week, Market, Technology, FTR, On_Time, Failed_FTR_Category, Failed_On_Time, Num_Requests, Comments
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(header);
        String name1 = "", parametros = "", orden = "", fileName = "Metrics ", week = "";
        String team1 = jCBMetricTeam.getItemAt(jCBMetricTeam.getSelectedIndex());
        String orgn1 = jCBOrgMetrics.getItemAt(jCBOrgMetrics.getSelectedIndex());
        String yearFrom1 = jCBYearFrom.getItemAt(jCBYearFrom.getSelectedIndex());
        String yearTo1 = jCBYearTo.getItemAt(jCBYearTo.getSelectedIndex());
        int rowCounter = 0;
        String organization = new String();
        team1 = team1.toLowerCase();

        if (jCBUser.getSelectedIndex() != 0) {
            name1 = " AND Name = '" + jCBUser.getSelectedItem() + "'";
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
            date1 = yearFrom1 + "-" + String.valueOf(mFrom) + "-01";
            if (jRBTo.isSelected()) {
                date2 = yearTo1 + "-" + String.valueOf(mTo) + "-" + String.valueOf(days[mTo - 1]);
                fileName = fileName + " From " + monthFrom + " To " + monthTo;
            } else {
                date2 = yearFrom1 + "-" + String.valueOf(mFrom) + "-" + String.valueOf(days[mFrom - 1]);
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
        
        if (!orgn1.equals("All")){
            organization = " AND Organization LIKE '%" + orgn1 + "'";
        }

        try {
            String[] valores = new String[23];
            for (int j = 0; j < 23; j++) {
                valores[j] = "";
            }
            connection = SQL_connection.getConnection();
            String query = "";
            if (team1.equals("all")) {
                query = "SELECT * FROM ("
                        + "SELECT Region, Organization, Signum, Name, Customer_Unit, "
                        + "Requestor, Task_ID, Task, Network, Subnetwork, Activity_Code, "
                        + "SAP_Billing, Work_Date, Logged_Time, Week, Market, Technology, "
                        + "FTR, On_Time, Failed_FTR_Category, Failed_On_Time, Num_Requests, Comments "
                        + "FROM metrics_cop UNION ALL "
                        + "SELECT Region, Organization, Signum, Name, Customer_Unit, "
                        + "Requestor, Task_ID, Task, Network, Subnetwork, Activity_Code, "
                        + "SAP_Billing, Work_Date, Logged_Time, Week, Market, Technology, "
                        + "FTR, On_Time, Failed_FTR_Category, Failed_On_Time, Num_Requests, Comments "
                        + "FROM metrics_sourcing UNION ALL "
                        + "SELECT Region, Organization, Signum, Name, Customer_Unit, "
                        + "Requestor, Task_ID, Task, Network, Subnetwork, Activity_Code, "
                        + "SAP_Billing, Work_Date, Logged_Time, Week, Market, Technology, "
                        + "FTR, On_Time, Failed_FTR_Category, Failed_On_Time, Num_Requests, Comments "
                        + "FROM metrics_vss UNION ALL "
                        + "SELECT Region, Organization, Signum, Name, Customer_Unit, "
                        + "Requestor, Task_ID, Task, Network, Subnetwork, Activity_Code, "
                        + "SAP_Billing, Work_Date, Logged_Time, Week, Market, Technology, "
                        + "FTR, On_Time, Failed_FTR_Category, Failed_On_Time, Num_Requests, Comments "
                        + "FROM metrics_pss) METRICAS " + week + organization + name1 + " LIMIT 18446744073709551615;";
            } else {
                query = "SELECT Region, Organization, Signum, Name, Customer_Unit, "
                        + "Requestor, Task_ID, Task, Network, Subnetwork, Activity_Code, "
                        + "SAP_Billing, Work_Date, Logged_Time, Week, Market, Technology, "
                        + "FTR, On_Time, Failed_FTR_Category, Failed_On_Time, Num_Requests, "
                        + "Comments FROM metrics_" + team1 + " " + week + organization + name1 + " LIMIT 18446744073709551615;";
            }
            preparedStatement = connection.prepareStatement(query);
            System.out.println("Query: " + preparedStatement);
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                JOptionPane.showMessageDialog(this, "Metrics not found! Please try again later...");
            } else {
                do {
                    for (int j = 1; j < 24; j++) {
                        if (jRBQuarter.isSelected()) {
                            if (j == 12) {
                                for (int i = 0; i < 12; i++) {
                                    if (resultSet.getString(j).startsWith(monthSQL[i])) {
                                        valores[j - 1] = months[i];
                                    }
                                }
                            } else {
                                valores[j - 1] = resultSet.getString(j);
                            }
                        } else {
                            valores[j - 1] = resultSet.getString(j);
                        }
                    }
                    rowCounter += 1;
                    model.addRow(valores);
                } while (resultSet.next());
            }
            connection.close();
        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Something went wrong, please try again later...");
        }
        System.out.println("Total row number: " + rowCounter);
        jTableShowMetrics.setModel(model);
        resizeColumnWidth(jTableShowMetrics);
        jTableShowMetrics.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
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
    private javax.swing.JButton jBAddSupCU;
    private javax.swing.JButton jBClear;
    private javax.swing.JButton jBClearTask;
    private javax.swing.JButton jBDelete;
    private javax.swing.JButton jBDeleteCUSupp;
    private javax.swing.JButton jBDeleteTask;
    private javax.swing.JButton jBExportTaskCSV;
    private javax.swing.JButton jBExportUserCSV;
    private javax.swing.JButton jBGenerateCSV;
    private javax.swing.JButton jBNetDelete;
    private javax.swing.JButton jBNetSearch;
    private javax.swing.JButton jBNetTableCSV;
    private javax.swing.JButton jBNetworkSearch;
    private javax.swing.JButton jBSaveMrkt;
    private javax.swing.JButton jBSaveNet;
    private javax.swing.JButton jBSaveNewUser;
    private javax.swing.JButton jBSaveTask;
    private javax.swing.JButton jBSearch;
    private javax.swing.JButton jBSearchMrkt;
    private javax.swing.JButton jBSearchTask;
    private javax.swing.JButton jBShowMetrics;
    private javax.swing.JButton jBShowPreview;
    private javax.swing.JComboBox<String> jCBAccess;
    private javax.swing.JComboBox<String> jCBAction;
    private javax.swing.JComboBox<String> jCBCUMrkt;
    private javax.swing.JComboBox<String> jCBCUSearch;
    private javax.swing.JComboBox<String> jCBCUTaskSearch;
    private javax.swing.JComboBox<String> jCBCustomerUnit;
    private javax.swing.JButton jCBDeleteMrkt;
    private javax.swing.JComboBox<String> jCBDeliverable;
    private javax.swing.JComboBox<String> jCBFrom;
    private javax.swing.JComboBox<String> jCBJobStage;
    private javax.swing.JComboBox<String> jCBLineManager;
    private javax.swing.JComboBox<String> jCBMarCU;
    private javax.swing.JComboBox<String> jCBMarMrkt;
    private javax.swing.JComboBox<String> jCBMarRegion;
    private javax.swing.JComboBox<String> jCBMarTeam;
    private javax.swing.JComboBox<String> jCBMarketList;
    private javax.swing.JComboBox<String> jCBMarketSearch;
    private javax.swing.JComboBox<String> jCBMetricTeam;
    private javax.swing.JComboBox<String> jCBMrktAction;
    private javax.swing.JComboBox<String> jCBNetActCode;
    private javax.swing.JComboBox<String> jCBNetAction;
    private javax.swing.JComboBox<String> jCBNetCustomer;
    private javax.swing.JComboBox<String> jCBNetMarket;
    private javax.swing.JComboBox<String> jCBNetRegion;
    private javax.swing.JComboBox<String> jCBNetSearch;
    private javax.swing.JComboBox<String> jCBNetTeam;
    private javax.swing.JComboBox<String> jCBNetTeamSearch;
    private javax.swing.JComboBox<String> jCBNetTech;
    private javax.swing.JComboBox<String> jCBOrgMetrics;
    private javax.swing.JComboBox<String> jCBProjectSuppDom;
    private javax.swing.JComboBox<String> jCBRegionMrkt;
    private javax.swing.JComboBox<String> jCBSearchUser;
    private javax.swing.JComboBox<String> jCBServicePN;
    private javax.swing.JComboBox<String> jCBSupportedCU;
    private javax.swing.JComboBox<String> jCBSupportedTeam;
    private javax.swing.JComboBox<String> jCBTaskCU;
    private javax.swing.JComboBox<String> jCBTaskEdit;
    private javax.swing.JComboBox<String> jCBTaskSearch;
    private javax.swing.JComboBox<String> jCBTaskTeam;
    private javax.swing.JComboBox<String> jCBTaskType;
    private javax.swing.JComboBox<String> jCBTeam;
    private javax.swing.JComboBox<String> jCBTeamMrkt;
    private javax.swing.JComboBox<String> jCBTeamTaskSearch;
    private javax.swing.JComboBox<String> jCBTo;
    private javax.swing.JComboBox<String> jCBUser;
    private javax.swing.JComboBox<String> jCBYearFrom;
    private javax.swing.JComboBox<String> jCBYearTo;
    private javax.swing.JDialog jDLoading;
    private javax.swing.JLabel jLAccess;
    private javax.swing.JLabel jLCUSupported;
    private javax.swing.JLabel jLChoose;
    private javax.swing.JLabel jLFileName;
    private javax.swing.JLabel jLFrom;
    private javax.swing.JLabel jLLoading;
    private javax.swing.JLabel jLMetricOrg;
    private javax.swing.JLabel jLMetricTeams;
    private javax.swing.JLabel jLNetAction;
    private javax.swing.JLabel jLSignum;
    private javax.swing.JLabel jLSignumEdit;
    private javax.swing.JLabel jLSuppCUs;
    private javax.swing.JLabel jLSuppTeams;
    private javax.swing.JLabel jLTask;
    private javax.swing.JLabel jLTask1;
    private javax.swing.JLabel jLTask2;
    private javax.swing.JLabel jLTask3;
    private javax.swing.JLabel jLTask4;
    private javax.swing.JLabel jLTaskSearch;
    private javax.swing.JLabel jLTaskSearch1;
    private javax.swing.JLabel jLTaskType;
    private javax.swing.JLabel jLTeamSupp;
    private javax.swing.JLabel jLYear;
    private javax.swing.JLabel jLYearTo;
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
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuItem jMEditMarkets;
    private javax.swing.JMenuItem jMEditNetworks;
    private javax.swing.JMenuItem jMEditTask;
    private javax.swing.JMenuItem jMEditUsers;
    private javax.swing.JMenuItem jMICOP;
    private javax.swing.JMenuItem jMIPSS;
    private javax.swing.JMenuItem jMIScoping;
    private javax.swing.JMenuItem jMISourcing;
    private javax.swing.JMenuItem jMIVSS;
    private javax.swing.JMenuItem jMReview;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenu jMenuTeams;
    private javax.swing.JMenu jMenuView;
    private javax.swing.JPanel jPEdit;
    private javax.swing.JPanel jPEditTask;
    private javax.swing.JPanel jPMarket;
    private javax.swing.JPanel jPNetEdit;
    private javax.swing.JPanel jPNetSearch;
    private javax.swing.JPanel jPNetworks;
    private javax.swing.JPanel jPSearch;
    private javax.swing.JPanel jPSearchMrkt;
    private javax.swing.JPanel jPSearchTask;
    private javax.swing.JPanel jPTasks;
    private javax.swing.JPanel jPUser;
    private javax.swing.JPanel jPView;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
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
    private javax.swing.JRadioButton jRBTo;
    private javax.swing.JRadioButton jRBWeek;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextField jTFCATNum;
    private javax.swing.JTextField jTFFileName;
    private javax.swing.JTextField jTFLOE;
    private javax.swing.JTextField jTFLastName;
    private javax.swing.JTextField jTFName;
    private javax.swing.JTextField jTFNetwork;
    private javax.swing.JTextField jTFPD;
    private javax.swing.JTextField jTFResponsible;
    private javax.swing.JTextField jTFSignum;
    private javax.swing.JTextField jTFSubnetwork;
    private javax.swing.JTextField jTFSupCU;
    private javax.swing.JTextField jTFSupTeam;
    private javax.swing.JTextField jTFTaskName;
    private javax.swing.JTable jTTasksList;
    private javax.swing.JTable jTUsersList;
    private javax.swing.JTable jTableNetworks;
    private javax.swing.JTable jTableShowMetrics;
    // End of variables declaration//GEN-END:variables
}
