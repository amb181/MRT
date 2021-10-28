/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package metrics;

import com.toedter.calendar.JTextFieldDateEditor;
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
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author efgaorm
 */
public class Time_Management extends javax.swing.JFrame {

    private Map hiddenColumns;
    Connection connection;
    Statement statement;
    int week = 0;
    String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    int[] days = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    String tid;
    List<String> tasks = new ArrayList<String>();
    List<String> tasktypes = new ArrayList<String>();
    List<String> teams = new ArrayList<String>();
    List<String> organizations = new ArrayList<String>();
    List<String> cus = new ArrayList<String>();
    List<String> user_teams = new ArrayList<String>();
    List<String> act_codes = new ArrayList<String>();
    List<String> regions = new ArrayList<String>();
    List<String> markets = new ArrayList<String>();
    List<String> netTeams = new ArrayList<String>();
    List<String> technologies = new ArrayList<String>();
    List<String> networks = new ArrayList<String>();
    String[] LMSignums = {"QIVAALC", "EEDNPRE", "TIMAFCC", "EMAUHER", "EEDNPRE", "EPANVIC"};
    String[] LMOrganizations = {"BNEW SAN SAB SDU MX MANA Project Supp 1",
                                "BNEW SAN SAB SDU MX MANA Project Supp 2", 
                                "BNEW SAN SAB SDU MX MANA Project Supp 3",
                                "BNEW SAN SAB SDU MX MANA Project Supp 4",
                                "BNEW SAN SAB SDU MX MANA Project Supp 5", 
                                "BNEW SAN SAB SDU MX MANA",
                                "BNEW SAN SAB SDU MX MELA Site Eng 2",
                                "BNEW SAN SAS SDU Mexico VAS2"
                                };
    String[] ActTypes = {"6000", "6001", "6002", "N/A"};

    ArrayList<String> teamsAndCUs = new ArrayList<>();
    ArrayList<String> taskIDandTasks = new ArrayList<>();
    ArrayList<String> networksinfo = new ArrayList<>();
    ArrayList<String> users_info = new ArrayList<>();
    ArrayList<String> tasks_info = new ArrayList<>();
    ArrayList<String> service_packages = new ArrayList<>();
    ArrayList<String> deliverables = new ArrayList<>();
    ArrayList<String> marketsinfo = new ArrayList<>();
    ArrayList<String> netTeamCURegMark = new ArrayList<>(); // Team, CU, Reg, Market
    ArrayList<String> netMrktTech = new ArrayList<>(); // Market, Technology
    ArrayList<String> netwTeamSubn = new ArrayList<>(); // Team, Customer, Market, Network, Subnetwork
    ArrayList<String> marketTeamCU = new ArrayList<>(); //teams
    ArrayList<String> markTeams = new ArrayList<>(); //teams
    ArrayList<String> servicePackageNames = new ArrayList<>();
    ArrayList<String> deliverableList = new ArrayList<>();
    ArrayList<String> projectSupportNames = new ArrayList<>();
    ArrayList<String> teamsCUSTasks = new ArrayList<>();

    /**
     * Creates new form Time_Review
     */
    public Time_Management() throws ParseException {
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
        jPEditServicePackage.setVisible(false);
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
        AutoCompleteDecorator.decorate(jCBMarTeam);
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
//        GetTaskTypes();
        GetAllUsers();
        GetAllTasks();
        GetNetworksSearch();
        GetServicePackages();
        GetDeliverables();
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
        jFrame_Compliance = new javax.swing.JFrame();
        jB_Export_Compliance = new javax.swing.JButton();
        jcb_Org_compliance = new javax.swing.JComboBox<>();
        jL_select_team = new javax.swing.JLabel();
        jL_select_org = new javax.swing.JLabel();
        jcb_Team_compliance = new javax.swing.JComboBox<>();
        jcbWeek_Compliance = new javax.swing.JComboBox<>();
        jLWeek_Compliance = new javax.swing.JLabel();
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
        jLabel_Filters = new javax.swing.JLabel();
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
        jCBTeam_user = new javax.swing.JComboBox<>();
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
        jCBTeam_editTask = new javax.swing.JComboBox<>();
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
        jCBTeam_editnet = new javax.swing.JComboBox<>();
        jCBProjectName = new javax.swing.JComboBox<>();
        jLabel40 = new javax.swing.JLabel();
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
        jCBTeam_editmarket = new javax.swing.JComboBox<>();
        jPEditServicePackage = new javax.swing.JPanel();
        jPSearchPackage2 = new javax.swing.JPanel();
        jBDeleteTask5 = new javax.swing.JButton();
        jLpackage4 = new javax.swing.JLabel();
        jCBPackageSearch2 = new javax.swing.JComboBox<>();
        jCBPackage_CU2 = new javax.swing.JComboBox<>();
        jLTaskSearch10 = new javax.swing.JLabel();
        jCBTeamSearchPackage2 = new javax.swing.JComboBox<>();
        jLabel52 = new javax.swing.JLabel();
        jLpackage5 = new javax.swing.JLabel();
        jCBDeliverableSearch2 = new javax.swing.JComboBox<>();
        jBSearchTask5 = new javax.swing.JButton();
        jPAddPackage2 = new javax.swing.JPanel();
        jBSaveTask5 = new javax.swing.JButton();
        jCBDeliverables2 = new javax.swing.JComboBox<>();
        jLTaskSearch11 = new javax.swing.JLabel();
        jLServicePackage2 = new javax.swing.JLabel();
        jCBServicePackageAdd2 = new javax.swing.JComboBox<>();
        jCBTeam_package2 = new javax.swing.JComboBox<>();
        jLabel53 = new javax.swing.JLabel();
        jCBCUPackageAdd2 = new javax.swing.JComboBox<>();
        jLDeliverables2 = new javax.swing.JLabel();
        jCBPackage_billable3 = new javax.swing.JComboBox<>();
        jLBillable2 = new javax.swing.JLabel();
        jPEditServicePackage1 = new javax.swing.JPanel();
        jLChoose1 = new javax.swing.JLabel();
        jCBPackageAction = new javax.swing.JComboBox<>();
        jPSearchPackage1 = new javax.swing.JPanel();
        jBDeleteTask1 = new javax.swing.JButton();
        jLTaskSearch2 = new javax.swing.JLabel();
        jBSearchTask1 = new javax.swing.JButton();
        jCBServicePackageSearch = new javax.swing.JComboBox<>();
        jLTaskSearch3 = new javax.swing.JLabel();
        jCBTeamPackageSearch = new javax.swing.JComboBox<>();
        jLabel41 = new javax.swing.JLabel();
        jCBCUPackageSearch = new javax.swing.JComboBox<>();
        jPAddPackage1 = new javax.swing.JPanel();
        jLTaskType1 = new javax.swing.JLabel();
        jCBBillable_packageadd = new javax.swing.JComboBox<>();
        jLBillable1 = new javax.swing.JLabel();
        jBSaveTask2 = new javax.swing.JButton();
        jLabel42 = new javax.swing.JLabel();
        jCBTeam_addpackage = new javax.swing.JComboBox<>();
        jLabel43 = new javax.swing.JLabel();
        jCBCU_packageadd = new javax.swing.JComboBox<>();
        jCBTeam_editpackage = new javax.swing.JComboBox<>();
        jCBServicePackage_add = new javax.swing.JComboBox<>();
        jPSearchDeliverable = new javax.swing.JPanel();
        jBDeleteDeliverable = new javax.swing.JButton();
        jCBCUSearchDeliverable = new javax.swing.JComboBox<>();
        jLTaskSearch6 = new javax.swing.JLabel();
        jCBTeamSearchDeliverable = new javax.swing.JComboBox<>();
        jLabel49 = new javax.swing.JLabel();
        jLpackage3 = new javax.swing.JLabel();
        jCBDeliverableSearch1 = new javax.swing.JComboBox<>();
        jBSearchDeliverable = new javax.swing.JButton();
        jLpackage6 = new javax.swing.JLabel();
        jCBPackage_deliverablesearch = new javax.swing.JComboBox<>();
        jPAddDeliverable = new javax.swing.JPanel();
        jLTaskType2 = new javax.swing.JLabel();
        jCBDeliverable_add = new javax.swing.JComboBox<>();
        jLBillable3 = new javax.swing.JLabel();
        jBSaveDeliverable = new javax.swing.JButton();
        jLabel44 = new javax.swing.JLabel();
        jCBTeam_adddeliverable = new javax.swing.JComboBox<>();
        jLabel45 = new javax.swing.JLabel();
        jCBCU_adddeliverable = new javax.swing.JComboBox<>();
        jCBTeam_editdeliverable = new javax.swing.JComboBox<>();
        jCBServicePackage_adddeliverable = new javax.swing.JComboBox<>();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuView = new javax.swing.JMenu();
        jMReview = new javax.swing.JMenuItem();
        jMenuTeams = new javax.swing.JMenu();
        jMICOP = new javax.swing.JMenuItem();
        jMIFMS = new javax.swing.JMenuItem();
        jMIPSS = new javax.swing.JMenuItem();
        jMIScoping = new javax.swing.JMenuItem();
        jMISourcing = new javax.swing.JMenuItem();
        jMIVSS = new javax.swing.JMenuItem();
        jMenuEdit = new javax.swing.JMenu();
        jMEditUsers = new javax.swing.JMenuItem();
        jMEditTask = new javax.swing.JMenuItem();
        jMEditNetworks = new javax.swing.JMenuItem();
        jMEditMarkets = new javax.swing.JMenuItem();
        jMEditServicePackage = new javax.swing.JMenuItem();
        jMenuCompliance = new javax.swing.JMenu();
        jMComp_people = new javax.swing.JMenuItem();

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
                .addContainerGap(21, Short.MAX_VALUE))
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

        jB_Export_Compliance.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jB_Export_Compliance.setText("Search & Export");
        jB_Export_Compliance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jB_Export_ComplianceActionPerformed(evt);
            }
        });

        jcb_Org_compliance.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jL_select_team.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jL_select_team.setText("Select team:");

        jL_select_org.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jL_select_org.setText("Select org:");

        jcb_Team_compliance.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jcbWeek_Compliance.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLWeek_Compliance.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLWeek_Compliance.setText("Week:");

        javax.swing.GroupLayout jFrame_ComplianceLayout = new javax.swing.GroupLayout(jFrame_Compliance.getContentPane());
        jFrame_Compliance.getContentPane().setLayout(jFrame_ComplianceLayout);
        jFrame_ComplianceLayout.setHorizontalGroup(
            jFrame_ComplianceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFrame_ComplianceLayout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addComponent(jL_select_org)
                .addGroup(jFrame_ComplianceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jFrame_ComplianceLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jcb_Org_compliance, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(87, Short.MAX_VALUE))
                    .addGroup(jFrame_ComplianceLayout.createSequentialGroup()
                        .addGap(78, 78, 78)
                        .addComponent(jL_select_team)
                        .addGap(36, 36, 36)
                        .addComponent(jcb_Team_compliance, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(jFrame_ComplianceLayout.createSequentialGroup()
                .addGap(320, 320, 320)
                .addGroup(jFrame_ComplianceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jFrame_ComplianceLayout.createSequentialGroup()
                        .addComponent(jLWeek_Compliance)
                        .addGap(18, 18, 18)
                        .addComponent(jcbWeek_Compliance, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jB_Export_Compliance))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jFrame_ComplianceLayout.setVerticalGroup(
            jFrame_ComplianceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame_ComplianceLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jFrame_ComplianceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcb_Team_compliance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jL_select_team))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jFrame_ComplianceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jL_select_org)
                    .addComponent(jcb_Org_compliance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(44, 44, 44)
                .addGroup(jFrame_ComplianceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLWeek_Compliance)
                    .addComponent(jcbWeek_Compliance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                .addComponent(jB_Export_Compliance)
                .addGap(23, 23, 23))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

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
        jCBMetricTeam.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "COP", "FMS", "PSS", "Scoping", "Sourcing", "VSS" }));

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

        jLabel_Filters.setFont(new java.awt.Font("Ericsson Hilda", 0, 14)); // NOI18N
        jLabel_Filters.setText("Filters");

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
                                    .addComponent(jLabel_Filters))
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
                                    .addComponent(jLabel_Filters))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 784, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        jCBCustomerUnit.setEditable(true);
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
        jCBAccess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBAccessActionPerformed(evt);
            }
        });

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
        jCBLineManager.setToolTipText("");

        jCBTeam_user.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBTeam_user.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBTeam_userActionPerformed(evt);
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
                                .addGap(12, 12, 12)
                                .addComponent(jCBTeam_user, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                        .addComponent(jCBJobStage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jCBTeam_user, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
        jCBAction.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select...", "Add new user", "Edit user" }));
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
                .addComponent(jCBSearchUser, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1454, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPEditTask.setBackground(new java.awt.Color(255, 255, 255));
        jPEditTask.setMaximumSize(new java.awt.Dimension(1493, 718));
        jPEditTask.setPreferredSize(new java.awt.Dimension(1450, 912));

        jLChoose.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLChoose.setText("Choose action:");

        jCBTaskEdit.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBTaskEdit.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select action...", "Add a new task", "Edit existing task" }));
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
        jCBTeamTaskSearch.setToolTipText("");
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
                .addContainerGap(371, Short.MAX_VALUE))
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
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jPTasks.setBackground(new java.awt.Color(255, 255, 255));
        jPTasks.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLTaskType.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTaskType.setText("Task Type:");

        jCBTaskType.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBTaskType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBTaskTypeActionPerformed(evt);
            }
        });

        jLTask.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTask.setText("Task:");

        jTFTaskName.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jTFTaskName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTFTaskNameActionPerformed(evt);
            }
        });

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
        jCBTaskCU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBTaskCUActionPerformed(evt);
            }
        });

        jLTask1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTask1.setText("Deliverable:");

        jCBServicePN.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBServicePN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBServicePNActionPerformed(evt);
            }
        });

        jLTask2.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTask2.setText("Service Package Name:");

        jCBDeliverable.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBDeliverable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBDeliverableActionPerformed(evt);
            }
        });

        jLTask3.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTask3.setText("Project Support Domain:");

        jLTask4.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTask4.setText("Level of Effort:");

        jTFLOE.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jCBProjectSuppDom.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jCBTeam_editTask.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        javax.swing.GroupLayout jPTasksLayout = new javax.swing.GroupLayout(jPTasks);
        jPTasks.setLayout(jPTasksLayout);
        jPTasksLayout.setHorizontalGroup(
            jPTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPTasksLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPTasksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBSaveTask, javax.swing.GroupLayout.Alignment.TRAILING)
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
                                    .addComponent(jCBTeam_editTask, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                    .addComponent(jLabel21)
                    .addComponent(jCBTaskCU, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCBTaskTeam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCBTeam_editTask, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addGroup(jPEditTaskLayout.createSequentialGroup()
                        .addComponent(jLChoose)
                        .addGap(18, 18, 18)
                        .addComponent(jCBTaskEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPTasks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPEditTaskLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPEditTaskLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jBExportTaskCSV))
                    .addGroup(jPEditTaskLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 2352, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                        .addComponent(jScrollPane3)
                        .addContainerGap())
                    .addGroup(jPEditTaskLayout.createSequentialGroup()
                        .addComponent(jPSearchTask, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPTasks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 280, Short.MAX_VALUE))))
        );

        jPNetworks.setBackground(new java.awt.Color(255, 255, 255));
        jPNetworks.setMaximumSize(new java.awt.Dimension(1493, 718));
        jPNetworks.setPreferredSize(new java.awt.Dimension(1493, 718));
        jPNetworks.setRequestFocusEnabled(false);

        jLNetAction.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLNetAction.setText("Choose action:");

        jCBNetAction.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBNetAction.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select...", "Add a new network", "Edit an existing network" }));
        jCBNetAction.setToolTipText("");
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
                .addComponent(jCBMarketSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCBNetSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jBNetworkSearch)
                .addGap(18, 18, 18)
                .addComponent(jBNetSearch)
                .addGap(18, 18, 18)
                .addComponent(jBNetDelete)
                .addContainerGap(224, Short.MAX_VALUE))
        );
        jPNetSearchLayout.setVerticalGroup(
            jPNetSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPNetSearchLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPNetSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPNetSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jBNetworkSearch)
                        .addComponent(jBNetSearch)
                        .addComponent(jBNetDelete))
                    .addGroup(jPNetSearchLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jCBNetSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel24)
                        .addComponent(jCBNetTeamSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel25)
                        .addComponent(jLabel23)
                        .addComponent(jCBCUSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel26)
                        .addComponent(jCBMarketSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(12, Short.MAX_VALUE))
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

        jCBNetActCode.setEditable(true);
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

        jCBTeam_editnet.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jCBProjectName.setEditable(true);
        jCBProjectName.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLabel40.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel40.setText("Project name:");

        javax.swing.GroupLayout jPNetEditLayout = new javax.swing.GroupLayout(jPNetEdit);
        jPNetEdit.setLayout(jPNetEditLayout);
        jPNetEditLayout.setHorizontalGroup(
            jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPNetEditLayout.createSequentialGroup()
                .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPNetEditLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPNetEditLayout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(76, 76, 76)
                                .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTFSubnetwork, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTFPD, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabel13)
                            .addGroup(jPNetEditLayout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(18, 18, 18)
                                .addComponent(jCBTeam_editnet, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCBNetTeam, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPNetEditLayout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addGap(18, 18, 18)
                                .addComponent(jCBNetCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(36, 36, 36)
                                .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel15))
                                .addGap(18, 18, 18)
                                .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jCBNetActCode, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jCBNetRegion, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPNetEditLayout.createSequentialGroup()
                                .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel22)
                                    .addComponent(jLabel10))
                                .addGap(38, 38, 38)
                                .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTFNetwork, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPNetEditLayout.createSequentialGroup()
                                        .addComponent(jCBNetTech, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel40)))))
                        .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPNetEditLayout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel16))
                                .addGap(18, 18, 18)
                                .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTFResponsible)
                                    .addComponent(jCBNetMarket, 0, 260, Short.MAX_VALUE)))
                            .addGroup(jPNetEditLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jCBProjectName, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPNetEditLayout.createSequentialGroup()
                        .addGap(1319, 1319, 1319)
                        .addComponent(jBSaveNet)))
                .addGap(0, 24, Short.MAX_VALUE))
        );
        jPNetEditLayout.setVerticalGroup(
            jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPNetEditLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPNetEditLayout.createSequentialGroup()
                        .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jCBNetTeam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCBTeam_editnet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPNetEditLayout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTFPD, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPNetEditLayout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addGap(8, 8, 8)))
                                .addGap(18, 18, 18)
                                .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel13)
                                    .addComponent(jTFSubnetwork, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPNetEditLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel11)
                                    .addComponent(jCBNetActCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPNetEditLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel12)
                                    .addComponent(jTFResponsible, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPNetEditLayout.createSequentialGroup()
                        .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jCBNetCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17)
                            .addComponent(jLabel15)
                            .addComponent(jCBNetRegion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16)
                            .addComponent(jCBNetMarket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jTFNetwork, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(22, 22, 22)
                        .addGroup(jPNetEditLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel22)
                            .addComponent(jCBNetTech, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCBProjectName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel40))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jBSaveNet)
                .addContainerGap())
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
                .addGroup(jPNetworksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPNetworksLayout.createSequentialGroup()
                        .addComponent(jLNetAction)
                        .addGap(18, 18, 18)
                        .addComponent(jCBNetAction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPNetEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPNetworksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jBNetTableCSV)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 1853, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPNetSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(1436, Short.MAX_VALUE))
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
                .addGroup(jPNetworksLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPNetEdit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBNetTableCSV))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1556, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );

        jPMarket.setBackground(new java.awt.Color(255, 255, 255));
        jPMarket.setPreferredSize(new java.awt.Dimension(1917, 915));

        jPSearchMrkt.setBackground(new java.awt.Color(255, 255, 255));
        jPSearchMrkt.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel28.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel28.setText("Team: ");

        jCBMarTeam.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
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
                .addGap(18, 18, 18)
                .addComponent(jCBMarCU, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
        jCBMrktAction.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select...", "Add a new market", "Edit an existing market" }));
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
        jCBMarketList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBMarketListActionPerformed(evt);
            }
        });

        jCBTeam_editmarket.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

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
                        .addComponent(jCBTeamMrkt, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCBTeam_editmarket, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(149, 149, 149)
                        .addComponent(jLabel33))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(126, 126, 126)
                        .addComponent(jLabel35)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(81, 81, 81)
                        .addComponent(jCBMarketList, 0, 355, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addComponent(jCBCUMrkt, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                    .addComponent(jCBCUMrkt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCBTeam_editmarket, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addContainerGap(686, Short.MAX_VALUE))
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

        jPEditServicePackage.setBackground(new java.awt.Color(255, 255, 255));
        jPEditServicePackage.setMaximumSize(new java.awt.Dimension(1493, 718));
        jPEditServicePackage.setPreferredSize(new java.awt.Dimension(1450, 912));

        jPSearchPackage2.setBackground(new java.awt.Color(255, 255, 255));

        jBDeleteTask5.setBackground(new java.awt.Color(199, 64, 56));
        jBDeleteTask5.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBDeleteTask5.setForeground(new java.awt.Color(255, 255, 255));
        jBDeleteTask5.setText("Delete");
        jBDeleteTask5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBDeleteTask5ActionPerformed(evt);
            }
        });

        jLpackage4.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLpackage4.setText(" Service Package:");

        jCBPackageSearch2.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBPackageSearch2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBPackageSearch2ActionPerformed(evt);
            }
        });

        jCBPackage_CU2.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBPackage_CU2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBPackage_CU2ActionPerformed(evt);
            }
        });

        jLTaskSearch10.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTaskSearch10.setText("Team:");

        jCBTeamSearchPackage2.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBTeamSearchPackage2.setToolTipText("");
        jCBTeamSearchPackage2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBTeamSearchPackage2ActionPerformed(evt);
            }
        });

        jLabel52.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel52.setText("CU:");

        jLpackage5.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLpackage5.setText(" Deliverable:");

        jCBDeliverableSearch2.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBDeliverableSearch2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBDeliverableSearch2ActionPerformed(evt);
            }
        });

        jBSearchTask5.setBackground(new java.awt.Color(0, 130, 240));
        jBSearchTask5.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBSearchTask5.setForeground(new java.awt.Color(255, 255, 255));
        jBSearchTask5.setText("Search");
        jBSearchTask5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSearchTask5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPSearchPackage2Layout = new javax.swing.GroupLayout(jPSearchPackage2);
        jPSearchPackage2.setLayout(jPSearchPackage2Layout);
        jPSearchPackage2Layout.setHorizontalGroup(
            jPSearchPackage2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPSearchPackage2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPSearchPackage2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPSearchPackage2Layout.createSequentialGroup()
                        .addComponent(jLTaskSearch10)
                        .addGap(18, 18, 18)
                        .addComponent(jCBTeamSearchPackage2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel52)
                        .addGap(18, 18, 18)
                        .addComponent(jCBPackage_CU2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPSearchPackage2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPSearchPackage2Layout.createSequentialGroup()
                            .addComponent(jLpackage5, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jCBDeliverableSearch2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jBSearchTask5)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jBDeleteTask5)
                            .addGap(6, 6, 6))
                        .addGroup(jPSearchPackage2Layout.createSequentialGroup()
                            .addComponent(jLpackage4, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jCBPackageSearch2, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(85, Short.MAX_VALUE))
        );
        jPSearchPackage2Layout.setVerticalGroup(
            jPSearchPackage2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPSearchPackage2Layout.createSequentialGroup()
                .addGroup(jPSearchPackage2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPSearchPackage2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jCBPackage_CU2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jCBTeamSearchPackage2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel52))
                    .addComponent(jLTaskSearch10))
                .addGap(18, 18, 18)
                .addGroup(jPSearchPackage2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLpackage4)
                    .addComponent(jCBPackageSearch2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPSearchPackage2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLpackage5)
                    .addComponent(jCBDeliverableSearch2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 38, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPSearchPackage2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPSearchPackage2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBDeleteTask5)
                    .addComponent(jBSearchTask5))
                .addContainerGap())
        );

        jPAddPackage2.setBackground(new java.awt.Color(255, 255, 255));

        jBSaveTask5.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBSaveTask5.setText("Save");
        jBSaveTask5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSaveTask5ActionPerformed(evt);
            }
        });

        jCBDeliverables2.setEditable(true);
        jCBDeliverables2.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBDeliverables2.setToolTipText("");
        jCBDeliverables2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBDeliverables2ActionPerformed(evt);
            }
        });

        jLTaskSearch11.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTaskSearch11.setText("Team:");

        jLServicePackage2.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLServicePackage2.setText(" Service Package:");

        jCBServicePackageAdd2.setEditable(true);
        jCBServicePackageAdd2.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBServicePackageAdd2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBServicePackageAdd2ActionPerformed(evt);
            }
        });

        jCBTeam_package2.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBTeam_package2.setToolTipText("");
        jCBTeam_package2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBTeam_package2ActionPerformed(evt);
            }
        });

        jLabel53.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel53.setText("CU:");

        jCBCUPackageAdd2.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBCUPackageAdd2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBCUPackageAdd2ActionPerformed(evt);
            }
        });

        jLDeliverables2.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLDeliverables2.setText("Deliverables");

        jCBPackage_billable3.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBPackage_billable3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Billable", "Not Billable" }));
        jCBPackage_billable3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBPackage_billable3ActionPerformed(evt);
            }
        });

        jLBillable2.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLBillable2.setText("Billable");

        javax.swing.GroupLayout jPAddPackage2Layout = new javax.swing.GroupLayout(jPAddPackage2);
        jPAddPackage2.setLayout(jPAddPackage2Layout);
        jPAddPackage2Layout.setHorizontalGroup(
            jPAddPackage2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPAddPackage2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPAddPackage2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPAddPackage2Layout.createSequentialGroup()
                        .addComponent(jLTaskSearch11)
                        .addGap(18, 18, 18)
                        .addComponent(jCBTeam_package2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19)
                        .addComponent(jLabel53)
                        .addGap(18, 18, 18)
                        .addComponent(jCBCUPackageAdd2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPAddPackage2Layout.createSequentialGroup()
                        .addComponent(jLDeliverables2, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCBDeliverables2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLBillable2)
                        .addGap(18, 18, 18)
                        .addComponent(jCBPackage_billable3, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPAddPackage2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jBSaveTask5)
                        .addGroup(jPAddPackage2Layout.createSequentialGroup()
                            .addComponent(jLServicePackage2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jCBServicePackageAdd2, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(12, 12, 12))))
                .addContainerGap(246, Short.MAX_VALUE))
        );
        jPAddPackage2Layout.setVerticalGroup(
            jPAddPackage2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPAddPackage2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPAddPackage2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLTaskSearch11)
                    .addComponent(jCBTeam_package2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel53)
                    .addComponent(jCBCUPackageAdd2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(jPAddPackage2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLDeliverables2)
                    .addComponent(jCBDeliverables2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLBillable2)
                    .addComponent(jCBPackage_billable3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPAddPackage2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLServicePackage2)
                    .addComponent(jCBServicePackageAdd2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addComponent(jBSaveTask5))
        );

        jPEditServicePackage1.setBackground(new java.awt.Color(255, 255, 255));
        jPEditServicePackage1.setMaximumSize(new java.awt.Dimension(1493, 718));
        jPEditServicePackage1.setPreferredSize(new java.awt.Dimension(1450, 912));

        jLChoose1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLChoose1.setText("Choose action:");

        jCBPackageAction.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBPackageAction.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select...", "Add new service package", "Edit service package", "Add new deliverable", "Edit deliverable" }));
        jCBPackageAction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBPackageActionActionPerformed(evt);
            }
        });

        jPSearchPackage1.setBackground(new java.awt.Color(255, 255, 255));

        jBDeleteTask1.setBackground(new java.awt.Color(199, 64, 56));
        jBDeleteTask1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBDeleteTask1.setForeground(new java.awt.Color(255, 255, 255));
        jBDeleteTask1.setText("Delete");
        jBDeleteTask1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBDeleteTask1ActionPerformed(evt);
            }
        });

        jLTaskSearch2.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTaskSearch2.setText("Service Package:");

        jBSearchTask1.setBackground(new java.awt.Color(0, 130, 240));
        jBSearchTask1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBSearchTask1.setForeground(new java.awt.Color(255, 255, 255));
        jBSearchTask1.setText("Search");
        jBSearchTask1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSearchTask1ActionPerformed(evt);
            }
        });

        jCBServicePackageSearch.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLTaskSearch3.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTaskSearch3.setText("Team:");

        jCBTeamPackageSearch.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBTeamPackageSearch.setToolTipText("");
        jCBTeamPackageSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBTeamPackageSearchActionPerformed(evt);
            }
        });

        jLabel41.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel41.setText("CU:");

        jCBCUPackageSearch.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBCUPackageSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBCUPackageSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPSearchPackage1Layout = new javax.swing.GroupLayout(jPSearchPackage1);
        jPSearchPackage1.setLayout(jPSearchPackage1Layout);
        jPSearchPackage1Layout.setHorizontalGroup(
            jPSearchPackage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPSearchPackage1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLTaskSearch3)
                .addGap(18, 18, 18)
                .addComponent(jCBTeamPackageSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel41)
                .addGap(18, 18, 18)
                .addComponent(jCBCUPackageSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLTaskSearch2)
                .addGap(18, 18, 18)
                .addComponent(jCBServicePackageSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jBSearchTask1)
                .addGap(18, 18, 18)
                .addComponent(jBDeleteTask1)
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPSearchPackage1Layout.setVerticalGroup(
            jPSearchPackage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPSearchPackage1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPSearchPackage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLTaskSearch3)
                    .addComponent(jCBTeamPackageSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41)
                    .addComponent(jCBCUPackageSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLTaskSearch2)
                    .addComponent(jCBServicePackageSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBSearchTask1)
                    .addComponent(jBDeleteTask1))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jPAddPackage1.setBackground(new java.awt.Color(255, 255, 255));
        jPAddPackage1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLTaskType1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTaskType1.setText("Service Package:");

        jCBBillable_packageadd.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBBillable_packageadd.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Billable", "Not Billable" }));

        jLBillable1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLBillable1.setText("Billable");

        jBSaveTask2.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBSaveTask2.setText("Save");
        jBSaveTask2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSaveTask2ActionPerformed(evt);
            }
        });

        jLabel42.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel42.setText("Team:");

        jCBTeam_addpackage.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBTeam_addpackage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBTeam_addpackageActionPerformed(evt);
            }
        });

        jLabel43.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel43.setText("CU:");

        jCBCU_packageadd.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBCU_packageadd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBCU_packageaddActionPerformed(evt);
            }
        });

        jCBTeam_editpackage.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jCBServicePackage_add.setEditable(true);
        jCBServicePackage_add.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        javax.swing.GroupLayout jPAddPackage1Layout = new javax.swing.GroupLayout(jPAddPackage1);
        jPAddPackage1.setLayout(jPAddPackage1Layout);
        jPAddPackage1Layout.setHorizontalGroup(
            jPAddPackage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPAddPackage1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPAddPackage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPAddPackage1Layout.createSequentialGroup()
                        .addComponent(jLabel42)
                        .addGap(18, 18, 18)
                        .addComponent(jCBTeam_editpackage, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCBTeam_addpackage, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel43)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCBCU_packageadd, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLBillable1)
                        .addGap(18, 18, 18)
                        .addComponent(jCBBillable_packageadd, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(53, 64, Short.MAX_VALUE))
                    .addGroup(jPAddPackage1Layout.createSequentialGroup()
                        .addComponent(jLTaskType1)
                        .addGap(18, 18, 18)
                        .addComponent(jCBServicePackage_add, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jBSaveTask2)
                        .addGap(72, 72, 72))))
        );
        jPAddPackage1Layout.setVerticalGroup(
            jPAddPackage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPAddPackage1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPAddPackage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42)
                    .addComponent(jLabel43)
                    .addComponent(jCBCU_packageadd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCBTeam_addpackage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCBTeam_editpackage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLBillable1)
                    .addComponent(jCBBillable_packageadd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPAddPackage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPAddPackage1Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(jPAddPackage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLTaskType1)
                            .addComponent(jCBServicePackage_add, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPAddPackage1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                        .addComponent(jBSaveTask2)
                        .addGap(29, 29, 29))))
        );

        jPSearchDeliverable.setBackground(new java.awt.Color(255, 255, 255));

        jBDeleteDeliverable.setBackground(new java.awt.Color(199, 64, 56));
        jBDeleteDeliverable.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBDeleteDeliverable.setForeground(new java.awt.Color(255, 255, 255));
        jBDeleteDeliverable.setText("Delete");
        jBDeleteDeliverable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBDeleteDeliverableActionPerformed(evt);
            }
        });

        jCBCUSearchDeliverable.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBCUSearchDeliverable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBCUSearchDeliverableActionPerformed(evt);
            }
        });

        jLTaskSearch6.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTaskSearch6.setText("Team:");

        jCBTeamSearchDeliverable.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBTeamSearchDeliverable.setToolTipText("");
        jCBTeamSearchDeliverable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBTeamSearchDeliverableActionPerformed(evt);
            }
        });

        jLabel49.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel49.setText("CU:");

        jLpackage3.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLpackage3.setText("Deliverable");

        jCBDeliverableSearch1.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jBSearchDeliverable.setBackground(new java.awt.Color(0, 130, 240));
        jBSearchDeliverable.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBSearchDeliverable.setForeground(new java.awt.Color(255, 255, 255));
        jBSearchDeliverable.setText("Search");
        jBSearchDeliverable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSearchDeliverableActionPerformed(evt);
            }
        });

        jLpackage6.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLpackage6.setText(" Service Package:");

        jCBPackage_deliverablesearch.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBPackage_deliverablesearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBPackage_deliverablesearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPSearchDeliverableLayout = new javax.swing.GroupLayout(jPSearchDeliverable);
        jPSearchDeliverable.setLayout(jPSearchDeliverableLayout);
        jPSearchDeliverableLayout.setHorizontalGroup(
            jPSearchDeliverableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPSearchDeliverableLayout.createSequentialGroup()
                .addGroup(jPSearchDeliverableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPSearchDeliverableLayout.createSequentialGroup()
                        .addComponent(jLpackage3, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCBDeliverableSearch1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPSearchDeliverableLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLTaskSearch6)
                        .addGap(18, 18, 18)
                        .addComponent(jCBTeamSearchDeliverable, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel49)
                        .addGap(18, 18, 18)
                        .addComponent(jCBCUSearchDeliverable, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jLpackage6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(jPSearchDeliverableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCBPackage_deliverablesearch, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPSearchDeliverableLayout.createSequentialGroup()
                        .addComponent(jBSearchDeliverable)
                        .addGap(18, 18, 18)
                        .addComponent(jBDeleteDeliverable)))
                .addContainerGap())
        );
        jPSearchDeliverableLayout.setVerticalGroup(
            jPSearchDeliverableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPSearchDeliverableLayout.createSequentialGroup()
                .addGroup(jPSearchDeliverableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPSearchDeliverableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jCBCUSearchDeliverable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jCBTeamSearchDeliverable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel49)
                        .addComponent(jLpackage6)
                        .addComponent(jCBPackage_deliverablesearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLTaskSearch6))
                .addGap(18, 18, 18)
                .addGroup(jPSearchDeliverableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLpackage3)
                    .addComponent(jCBDeliverableSearch1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBDeleteDeliverable)
                    .addComponent(jBSearchDeliverable))
                .addGap(0, 18, Short.MAX_VALUE))
        );

        jPAddDeliverable.setBackground(new java.awt.Color(255, 255, 255));
        jPAddDeliverable.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        jLTaskType2.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLTaskType2.setText("Service Package:");

        jCBDeliverable_add.setEditable(true);
        jCBDeliverable_add.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jLBillable3.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLBillable3.setText("Deliverables");

        jBSaveDeliverable.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jBSaveDeliverable.setText("Save");
        jBSaveDeliverable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBSaveDeliverableActionPerformed(evt);
            }
        });

        jLabel44.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel44.setText("Team:");

        jCBTeam_adddeliverable.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBTeam_adddeliverable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBTeam_adddeliverableActionPerformed(evt);
            }
        });

        jLabel45.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jLabel45.setText("CU:");

        jCBCU_adddeliverable.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBCU_adddeliverable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBCU_adddeliverableActionPerformed(evt);
            }
        });

        jCBTeam_editdeliverable.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jCBServicePackage_adddeliverable.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jCBServicePackage_adddeliverable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBServicePackage_adddeliverableActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPAddDeliverableLayout = new javax.swing.GroupLayout(jPAddDeliverable);
        jPAddDeliverable.setLayout(jPAddDeliverableLayout);
        jPAddDeliverableLayout.setHorizontalGroup(
            jPAddDeliverableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPAddDeliverableLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPAddDeliverableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jBSaveDeliverable)
                    .addGroup(jPAddDeliverableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPAddDeliverableLayout.createSequentialGroup()
                            .addComponent(jLBillable3)
                            .addGap(18, 18, 18)
                            .addComponent(jCBDeliverable_add, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPAddDeliverableLayout.createSequentialGroup()
                            .addComponent(jLTaskType2)
                            .addGap(18, 18, 18)
                            .addComponent(jCBServicePackage_adddeliverable, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPAddDeliverableLayout.createSequentialGroup()
                            .addComponent(jLabel44)
                            .addGap(18, 18, 18)
                            .addComponent(jCBTeam_editdeliverable, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jCBTeam_adddeliverable, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel45)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jCBCU_adddeliverable, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPAddDeliverableLayout.setVerticalGroup(
            jPAddDeliverableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPAddDeliverableLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPAddDeliverableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44)
                    .addComponent(jLabel45)
                    .addComponent(jCBCU_adddeliverable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCBTeam_adddeliverable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCBTeam_editdeliverable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPAddDeliverableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLTaskType2)
                    .addComponent(jCBServicePackage_adddeliverable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPAddDeliverableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLBillable3)
                    .addComponent(jCBDeliverable_add, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jBSaveDeliverable)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPEditServicePackage1Layout = new javax.swing.GroupLayout(jPEditServicePackage1);
        jPEditServicePackage1.setLayout(jPEditServicePackage1Layout);
        jPEditServicePackage1Layout.setHorizontalGroup(
            jPEditServicePackage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPEditServicePackage1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPEditServicePackage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPEditServicePackage1Layout.createSequentialGroup()
                        .addComponent(jLChoose1)
                        .addGap(18, 18, 18)
                        .addComponent(jCBPackageAction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPAddPackage1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPSearchPackage1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPSearchDeliverable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPAddDeliverable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(708, Short.MAX_VALUE))
        );
        jPEditServicePackage1Layout.setVerticalGroup(
            jPEditServicePackage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPEditServicePackage1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPEditServicePackage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLChoose1)
                    .addComponent(jCBPackageAction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPSearchPackage1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPAddPackage1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPSearchDeliverable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPAddDeliverable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(290, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPEditServicePackageLayout = new javax.swing.GroupLayout(jPEditServicePackage);
        jPEditServicePackage.setLayout(jPEditServicePackageLayout);
        jPEditServicePackageLayout.setHorizontalGroup(
            jPEditServicePackageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPEditServicePackageLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPEditServicePackageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPSearchPackage2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPAddPackage2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPEditServicePackage1, javax.swing.GroupLayout.PREFERRED_SIZE, 1920, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPEditServicePackageLayout.setVerticalGroup(
            jPEditServicePackageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPEditServicePackageLayout.createSequentialGroup()
                .addComponent(jPEditServicePackage1, javax.swing.GroupLayout.PREFERRED_SIZE, 950, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPSearchPackage2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPAddPackage2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        jMIFMS.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMIFMS.setText("FMS");
        jMIFMS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMIFMSActionPerformed(evt);
            }
        });
        jMenuTeams.add(jMIFMS);

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

        jMEditServicePackage.setBackground(new java.awt.Color(255, 255, 255));
        jMEditServicePackage.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMEditServicePackage.setText("Edit Service Packages");
        jMEditServicePackage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMEditServicePackageActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMEditServicePackage);

        jMenuBar1.add(jMenuEdit);

        jMenuCompliance.setBackground(new java.awt.Color(255, 255, 255));
        jMenuCompliance.setText("Compliance");
        jMenuCompliance.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N

        jMComp_people.setBackground(new java.awt.Color(255, 255, 255));
        jMComp_people.setFont(new java.awt.Font("Ericsson Hilda", 0, 18)); // NOI18N
        jMComp_people.setText("Missing Hours");
        jMComp_people.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMComp_peopleActionPerformed(evt);
            }
        });
        jMenuCompliance.add(jMComp_people);

        jMenuBar1.add(jMenuCompliance);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPView, javax.swing.GroupLayout.DEFAULT_SIZE, 1922, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPNetworks, javax.swing.GroupLayout.PREFERRED_SIZE, 3295, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 1, Short.MAX_VALUE))
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
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(10, 10, 10)
                    .addComponent(jPEditServicePackage, javax.swing.GroupLayout.PREFERRED_SIZE, 1920, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(3294, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPNetworks, javax.swing.GroupLayout.DEFAULT_SIZE, 1900, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jPView, javax.swing.GroupLayout.DEFAULT_SIZE, 1906, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPEdit, javax.swing.GroupLayout.DEFAULT_SIZE, 1906, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPEditTask, javax.swing.GroupLayout.PREFERRED_SIZE, 950, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 956, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPMarket, javax.swing.GroupLayout.PREFERRED_SIZE, 955, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(10, 10, 10)
                    .addComponent(jPEditServicePackage, javax.swing.GroupLayout.PREFERRED_SIZE, 950, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(946, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jBShowPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBShowPreviewActionPerformed
        // Show filtered data:
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
        // Fill To combobox:
        jCBTo.removeAllItems();

        int inicial = jCBFrom.getSelectedIndex();
        if (inicial == -1) {
            inicial = 0;
        }
        if (jRBWeek.isSelected()) {
            for (int i = inicial + 1; i < 52 + 1; i++) {
                jCBTo.addItem(Integer.toString(i));
                jCBTo.getSelectedItem().toString();
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
        jPEditServicePackage.setVisible(false);
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
        jCBServicePackage_add.setVisible(false);
        jCBDeliverableSearch1.setVisible(false);
        jCBServicePackageSearch.setVisible(false);
        jPMarket.setVisible(false);
        jPEditServicePackage.setVisible(false);
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
        jPEditServicePackage.setVisible(false);
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
        // Select action...:
        if (jCBTaskEdit.getSelectedIndex() == 0) {
            hide_task_comboboxes();
        
        // Add new task:
        } else if (jCBTaskEdit.getSelectedItem().toString().equals("Add a new task")){
            jPSearchTask.setVisible(false);
            jPTasks.setVisible(true);
            jCBTaskType.setEnabled(true);
            jTFTaskName.setEditable(true);
            jCBTaskTeam.setEnabled(true);
            jCBTaskCU.setEnabled(true);
            jCBTeam_editTask.setVisible(false);
            ResetTaskFields();  
            
            //Fill team combo box
            jCBTaskTeam.removeAllItems();
            jCBTaskTeam.addItem("Select a team...");
            
            ArrayList<String> teams = new ArrayList<>();
            
            for (int i = 2; i < tasks_info.size(); i += 9){
                if (!teams.contains(tasks_info.get(i))){
                    teams.add(tasks_info.get(i));
                }            
            }
            Collections.sort(teams);
            for (int i = 0; i < teams.size(); i++){
                jCBTaskTeam.addItem(teams.get(i));
            }
            
        // Edit task:    
        } else if (jCBTaskEdit.getSelectedItem().toString().equals("Edit existing task")){
            jPSearchTask.setVisible(true);
            jPTasks.setVisible(true);
            jCBTaskType.setEnabled(false);
            jTFTaskName.setEditable(false);
            jCBTaskTeam.setEnabled(false);
            jCBTaskCU.setEnabled(false);
            jCBTaskTeam.setVisible(false);
            jCBTeam_editTask.setVisible(true);
            jCBTeam_editTask.setEnabled(false);
            ResetTaskFields();
            
            
            //Fill team combo box
            jCBTeamTaskSearch.removeAllItems();
            jCBTeamTaskSearch.addItem("Select a team...");
            
            ArrayList<String> teams = new ArrayList<>();
            
            for (int i = 2; i < tasks_info.size(); i += 9){
                if (!teams.contains(tasks_info.get(i))){
                    teams.add(tasks_info.get(i));
                }            
            }
            Collections.sort(teams);
            for (int i = 0; i < teams.size(); i++){
                jCBTeamTaskSearch.addItem(teams.get(i));
            }
        }    
       
    }//GEN-LAST:event_jCBTaskEditActionPerformed

    private void jBSaveTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSaveTaskActionPerformed
        // Save task:
        String loe = jTFLOE.getText();
        Pattern decimalPattern = Pattern.compile("^-?\\d{0,2}(\\.\\d{1,2})?");
        Matcher matcherLOE = decimalPattern.matcher(loe);
        boolean flagLOE = matcherLOE.matches();
        if (jTFTaskName.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Task field is empty!");
        } else if (!flagLOE && !loe.equals("")) {
            JOptionPane.showMessageDialog(this, "LOE is not a number! Please type a number with maximum two decimals.");
        } else {
            jLLoading.setText("Saving task into database...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (jCBTaskEdit.getSelectedIndex() == 1) {
                        InsertTaskIntoDB();
                    } else {
                        UpdateTaskIntoDB();
                    }
                    jLLoading.setText("Updating task's local table...");
                    tasks_info.clear();
                    GetAllTasks();

                    jDLoading.dispose();
                }
            }).start();
            jDLoading.setVisible(true);
        }
    }//GEN-LAST:event_jBSaveTaskActionPerformed

    private void jBSearchTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSearchTaskActionPerformed
        // Search task in Edit :
        jLLoading.setText("Fetching task from database...");        
        
        String team = jCBTeamTaskSearch.getSelectedItem().toString();
        String cu = jCBCUTaskSearch.getSelectedItem().toString();
        String task = jCBTaskSearch.getSelectedItem().toString();
        
        for (int i=2; i < tasks_info.size(); i += 9 ){
            if (tasks_info.get(i).equals(team) && (tasks_info.get(i+1).equals(cu) && (tasks_info.get(i-1).equals(task)))){
                jCBTeam_editTask.addItem(tasks_info.get(i));
                jCBTaskCU.addItem(tasks_info.get(i+1));
                jCBTaskType.addItem(tasks_info.get(i-2));
                jTFTaskName.setText(tasks_info.get(i-1));
            }
        }
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
                Logger.getLogger(Time_Management.class.getName()).log(Level.SEVERE, null, ex);
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
        jPEditServicePackage.setVisible(false);
        ResetNetworkFields();
        this.setTitle("MRT - Audit & Report - Add, Edit and Delete Networks");
        System.out.println("Width: " + jPNetworks.getWidth() + " Height: " + jPNetworks.getHeight());

//        this.setSize(jPNetworks.getMaximumSize());
    }//GEN-LAST:event_jMEditNetworksActionPerformed

    private void jCBNetActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBNetActionActionPerformed
        // Select action for networks:
        if (jCBNetAction.getSelectedItem().toString().equals("Add a new network")) {
            jPNetSearch.setVisible(false);
            jTFNetwork.setEditable(true);
            jCBNetTeam.setEnabled(true);
            jCBNetTeam.setVisible(true);
            jCBTeam_editnet.setVisible(false);
            jTFPD.setEditable(true);
            ResetNetworkFields();
            // Fill Team in Add new network
            jCBNetTeam.removeAllItems();
            jCBNetTeam.addItem("Select a team"); 
            ArrayList<String> _teams = new ArrayList<String>();
            for (int i = 0; i < networksinfo.size(); i= i+11){
                if (!_teams.contains(networksinfo.get(i))){
                    _teams.add(networksinfo.get(i));
                }            
            }
            Collections.sort(_teams);
            for (int i = 0; i < _teams.size(); i++){
                jCBNetTeam.addItem(_teams.get(i));
            }
            
        } else {
            jPNetSearch.setVisible(true);
            jTFNetwork.setEditable(false);
            jCBNetTeam.setEnabled(false);
            jCBNetTeam.setVisible(false);
            jCBTeam_editnet.setVisible(true);
            jTFPD.setEditable(false); 
            // Fill teams in Edit network
            jCBNetTeamSearch.removeAllItems();
            jCBNetTeamSearch.addItem("Select a team");
            ArrayList<String> _teams1 = new ArrayList<String>();
            for (int i = 0; i < networksinfo.size(); i= i+11){
                if (!_teams1.contains(networksinfo.get(i))){
                    _teams1.add(networksinfo.get(i));
                }            
            }
            Collections.sort(_teams1);
            for (int i = 0; i < _teams1.size(); i++){
                jCBNetTeamSearch.addItem(_teams1.get(i));
            }
        }
    }//GEN-LAST:event_jCBNetActionActionPerformed

    private void jBSaveNetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSaveNetActionPerformed
        // Save button for Add new network:
        boolean flagNetwork = false;
        flagNetwork = ReviewFieldsState();
        if (flagNetwork) {
            jLLoading.setText("Saving network into database...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    networksinfo.clear();
                    if (jCBNetAction.getSelectedIndex() == 1) {
                        InsertIntoNetworkDB();
                    } else {
                        UpdateNetworksDB();
                    }                                       
                    networksinfo.clear();
                    GetNetworksSearch();
                    jDLoading.dispose();
                }
            }).start();
            jDLoading.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "There's an empty field! Please fill all the fields to execute this action.");
        }
    }//GEN-LAST:event_jBSaveNetActionPerformed

    private void jBNetworkSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNetworkSearchActionPerformed
        // Search button in Edit Network        
        String team = jCBNetTeamSearch.getSelectedItem().toString();
        String cu = jCBCUSearch.getSelectedItem().toString();
        String market = jCBMarketSearch.getSelectedItem().toString();
        String network = jCBNetSearch.getSelectedItem().toString(); 
        
        
        for (int i=4; i < networksinfo.size(); i += 11 ){
            if (networksinfo.get(i).equals(network) && (networksinfo.get(i-4).equals(team) && (networksinfo.get(i-3).equals(cu)) && (networksinfo.get(i-1).equals(market)))){
                jCBTeam_editnet.addItem(networksinfo.get(i-4));
                //Clean comboboxes                
                jCBNetTeam.removeAllItems();
                jCBNetCustomer.removeAllItems();
                jCBNetRegion.removeAllItems();
                jCBNetMarket.removeAllItems();
                jCBNetActCode.removeAllItems();
                jCBNetTech.removeAllItems();
                jCBProjectName.removeAllItems();
                // Fill with new information                
                jCBNetCustomer.addItem(networksinfo.get(i-3));
                jCBNetRegion.addItem(networksinfo.get(i-2));
                jCBNetMarket.addItem(networksinfo.get(i-1));
                jTFNetwork.setText(networksinfo.get(i));
                jTFSubnetwork.setText(networksinfo.get(i+1));
                jCBNetActCode.addItem(networksinfo.get(i+2));
                jCBNetTech.addItem(networksinfo.get(i+3));
                jTFPD.setText(networksinfo.get(i+4));
                jTFResponsible.setText(networksinfo.get(i+5)); 
                jCBProjectName.addItem(networksinfo.get(i+6));
            }
        }
        
        
        /*jLLoading.setText("Fetching network from database...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                SearchNetworkDB();
                jDLoading.dispose();
            }
        }).start();
        jDLoading.setVisible(true);*/
    }//GEN-LAST:event_jBNetworkSearchActionPerformed

    private void jBNetSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBNetSearchActionPerformed
        // TODO add your handling code here:
        ResetNetworkFields();        
    }//GEN-LAST:event_jBNetSearchActionPerformed

    private void jCBTaskSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBTaskSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBTaskSearchActionPerformed

    private void jBSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSearchActionPerformed
        // Search User button:
        String signumEdit = jCBSearchUser.getSelectedItem().toString();
        String name = "", job_stage = "";
        ArrayList<String> teams = new ArrayList<String>();       
        
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
                
                for (int i = 0; i < users_info.size(); i += 13){
                    name = users_info.get(i + 1) + ", " + users_info.get(i + 2);                    
                    
                    if (signumEdit.equals(name)){ 
                        
                        // Remove items from comboboxes
                        jCBLineManager.removeAllItems();
                        jCBTeam_user.removeAllItems();
                        jCBCustomerUnit.removeAllItems();
                        jCBJobStage.removeAllItems();
                        jTFSignum.removeAll();
                        
                        //Fill information from user
                        jTFSignum.setText(users_info.get(i));
                        jTFName.setText(users_info.get(i + 2));
                        jTFLastName.setText(users_info.get(i + 1));
                        jTFCATNum.setText(users_info.get(i + 12));
                        jCBTeam_user.addItem(users_info.get(i + 4));
                        teams.add(users_info.get(i + 4));
                        jTFSupTeam.setText(users_info.get(i + 8));
                        jTFSupCU.setText(users_info.get(i + 9));
                        job_stage = users_info.get(i + 10);
                        
                        //Fill job stage combo box
                        if (!job_stage.contains("Job Stage ") || job_stage.contains("Job stage")){
                          job_stage = users_info.get(i + 10);  
                        } else {
                            job_stage = users_info.get(i + 10).toLowerCase();
                            job_stage = job_stage.replace("job stage ", "");
                        }
                        jCBJobStage.addItem(job_stage);
                        
                        //Fill CU information
                        String cu = users_info.get(i + 3);
                        String team = users_info.get(i + 4);
                        
                        if (cu.equals("SDLM") || cu.equals("SPM")){
                            jCBCustomerUnit.addItem(users_info.get(i + 3));
                        }else{
                            
                            // Removing prefix from CU 
                            HashMap<String, String> cu_list = new HashMap<>();

                            // Add keys and values (Team name, Prefix)
                            cu_list.put("COP", "C_");
                            cu_list.put("VSS", "V_");
                            cu_list.put("PSS", "P_");
                            cu_list.put("Scoping", "SCP_");

                            if (cu_list.containsKey(team)) {
                                cu = cu.replace(cu_list.get(team), "");
                                jCBCustomerUnit.addItem(cu);
                            }else {
                                jCBCustomerUnit.addItem(users_info.get(i + 3));
                            }
                        }
                        
                        //Search manager name according to signum  
                        for (int n = 0; n < users_info.size(); n += 13){
                            if (users_info.get(i+6).equals(users_info.get(n)))
                                jCBLineManager.addItem(users_info.get(n + 1) + " " + users_info.get(n + 2));                            
                        }
                        
                        // Set access info
                        if (users_info.get(i + 7).equals("0")){
                            jCBAccess.setSelectedIndex(0);
                        } else{
                            jCBAccess.setSelectedIndex(1);
                        }
                        
                        
                    }
                }
                // Fill the rest of the teams in Team Combobox
                for (int i = 2; i < tasks_info.size(); i += 9)
                    if (!teams.contains(tasks_info.get(i)))
                        teams.add(tasks_info.get(i));
                
                for (int i = 1; i < teams.size(); i ++){
                    jCBTeam_user.addItem(teams.get(i));  
                }                
                
            }
        }

    }//GEN-LAST:event_jBSearchActionPerformed

    private void jBClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBClearActionPerformed
        // TODO add your handling code here:
        ResetUserFields();
    }//GEN-LAST:event_jBClearActionPerformed

    private void jBDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDeleteActionPerformed
        // Delete user:
        String signumEdit = jCBSearchUser.getSelectedItem().toString();
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
                    users_info.clear();
                    GetAllUsers();
                    jDLoading.dispose();
                }
            }).start();
            jDLoading.setVisible(true);
        }
    }//GEN-LAST:event_jBDeleteActionPerformed

    private void jCBActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBActionActionPerformed
        // Add new user option
        if (jCBAction.getSelectedIndex() == 1) {
            jPUser.setVisible(true);
            jPSearch.setVisible(false);
            jTFSignum.setEditable(true);
            jCBTeam_user.setVisible(false);
            jTFName.setEditable(true);
            jTFLastName.setEditable(true);
            ResetUserFields();
            
            jCBLineManager.removeAllItems();
            // Fill line managers list in Add new user
            jCBLineManager.addItem("Select an option...");
            
            ArrayList<String> managers = new ArrayList<String>();
            String lm_name = "";
            
            for (int i = 4; i < users_info.size(); i += 13) {
                if (users_info.get(i).equals("SDLM")){
                    lm_name = users_info.get(i - 3) + " " + users_info.get(i - 2);
                    if (!managers.contains(lm_name))
                        managers.add(lm_name);
                }
            }
            
            Collections.sort(managers);
            for (int i = 0; i < managers.size(); i++){
                jCBLineManager.addItem(managers.get(i));
            }
            
            // Fill teams combo box and supported team
            jCBSupportedTeam.addItem("Select a team...");
            jCBTeam.addItem("Select a team...");
            
            ArrayList<String> teams = new ArrayList<String>();
            
            
            for (int i = 2; i < tasks_info.size(); i += 9){
                if (!teams.contains(tasks_info.get(i)))
                    teams.add(tasks_info.get(i));                
            }
            Collections.sort(teams);
            for (int i = 0; i < teams.size(); i++){
                jCBSupportedTeam.addItem(teams.get(i));
                jCBTeam.addItem(teams.get(i));
            }
            
           // Edit user
        } else if (jCBAction.getSelectedIndex() == 2) {
            jPUser.setVisible(true);
            jCBTeam.setVisible(false);
            jPSearch.setVisible(true);
            jCBTeam_user.setVisible(true);
            jTFSignum.setEditable(false);
            jTFName.setEditable(false);
            jTFLastName.setEditable(false);
            ResetUserFields();
            
            jCBSearchUser.removeAllItems();
            // Fill user list in Edit user
            jCBSearchUser.addItem("Select an option...");
            
            ArrayList<String> users_list = new ArrayList<String>();
            String username = "";
            
            for (int i = 0; i < users_info.size(); i += 13) {                
                username = users_info.get(i + 1) + ", " + users_info.get(i + 2);
                if (!users_list.contains(username))
                    users_list.add(username);
               
            }      
            users_list.remove("Blanco, Jose Alberto");
            Collections.sort(users_list);
            for (int i = 0; i < users_list.size(); i++){
                jCBSearchUser.addItem(users_list.get(i));
            }
            
            // Fill supported teams
            jCBSupportedTeam.addItem("Select a team...");
            
            ArrayList<String> teams = new ArrayList<String>();
            
            
            for (int i = 2; i < tasks_info.size(); i += 9){
                if (!teams.contains(tasks_info.get(i)))
                    teams.add(tasks_info.get(i));                
            }
            Collections.sort(teams);
            for (int i = 0; i < teams.size(); i++){
                jCBSupportedTeam.addItem(teams.get(i));
            }
            
        } else
            jCBTeam_user.setVisible(false);
            ResetUserFields();
    }//GEN-LAST:event_jCBActionActionPerformed

    private void jCBSupportedCUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBSupportedCUActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBSupportedCUActionPerformed

    private void jBSaveNewUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSaveNewUserActionPerformed
        // TODO add your handling code here:
        String signum = jTFSignum.getText();
        String name = jTFName.getText();
        String lastName = jTFLastName.getText();
        String catsNumber = jTFCATNum.getText();
        ArrayList<String> customer_units = new ArrayList<String>();
        String cu = "";
        
        if (jCBCustomerUnit.getItemCount() != 0){
            cu = jCBCustomerUnit.getSelectedItem().toString();
        }
        
        for (int i = 3; i < tasks_info.size(); i += 9){
            if (!customer_units.contains(tasks_info.get(i)))
                customer_units.add(tasks_info.get(i));
        }
        
        
        System.out.println("el arreglo: " + customer_units);
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
            } else if (!customer_units.contains(cu)) {
                JOptionPane.showMessageDialog(this, "This is not an available Customer Unit!");                
            } else if (flagLastName) {
                JOptionPane.showMessageDialog(this, "Last name must contain only letters!");
                jTFLastName.setText("");
            } else if (flagCATsNumber) {
                JOptionPane.showMessageDialog(this, "CATs Number must contain only numbers!");
                jTFCATNum.setText("");
            } else if (catsNumber.length() != 8) {
                JOptionPane.showMessageDialog(this, "CATs Number must contain 8 digits!");
                jTFCATNum.setText("");
            } else {
                jLLoading.setText("Saving user into database...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (jCBAction.getSelectedIndex() == 1) {
                            InsertIntoUserDB();
                        } else {
                            UpdateUserDB();
                            //JOptionPane.showMessageDialog(this, "There's an empty field! Please fill every text field in order to save the user.");
                        }
                        jLLoading.setText("Updating user's local table...");
                        users_info.clear();
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
            Logger.getLogger(Time_Management.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jBExportUserCSVActionPerformed

    private void jBExportTaskCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBExportTaskCSVActionPerformed
        // TODO add your handling code here:
        String fileName = "Metrics Tasks";
        TableModel model = jTTasksList.getModel();
        try {
            SaveTableCSV(fileName, model);
        } catch (IOException ex) {
            Logger.getLogger(Time_Management.class.getName()).log(Level.SEVERE, null, ex);
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
                networksinfo.clear();
                GetNetworksSearch();
                jDLoading.dispose();
            }       
        }).start();
        jDLoading.setVisible(true);
        ResetNetworkFields();
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
        // Show all metrics button:
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
        ArrayList<String> cus = new ArrayList<>();
        String team = "";
        
        if (jCBTaskTeam.getItemCount() != 0){
            team = jCBTaskTeam.getSelectedItem().toString();
        } 
        jCBTaskCU.removeAllItems();   
        for (int i = 2; i < tasks_info.size(); i += 9){ 
            if (!cus.contains(tasks_info.get(i + 1)))
                if (team.equals(tasks_info.get(i))) {
                    cus.add(tasks_info.get(i + 1));
                }            
        }
        Collections.sort(cus);
        for (int i = 0; i < cus.size(); i++) {
            jCBTaskCU.addItem(cus.get(i));
        }
        // Update task id
//        jCBTaskType.removeAllItems();
//        jCBServicePN.removeAllItems();
//        jCBDeliverable.removeAllItems();
//        jCBProjectSuppDom.removeAllItems();
//        UpdateTaskType(team);
    }//GEN-LAST:event_jCBTaskTeamActionPerformed

    private void UpdateTaskType(String team) {
        // Update task id in Add Task panel
        String _team = "  ";

        HashMap<String, String> list_teams = new HashMap<String, String>();
        list_teams.put("COP", "COP");
        list_teams.put("PSS", "NAT");
        list_teams.put("Scoping", "SCP");
        list_teams.put("SDU", "ADMIN");
        list_teams.put("Sourcing", "SE");
        list_teams.put("VSS", "VSS");

        if (team.equals("Sourcing")) {
            _team = "ST";
        }
        // Fill task type
        for (int i = 0; i < tasktypes.size(); i++) {
            if (tasktypes.get(i).contains(list_teams.get(team)) || tasktypes.get(i).contains(_team)) {
                jCBTaskType.addItem(tasktypes.get(i));
            }
        }
        // Fill service package name
//        for (int i = 0; i < servicePackageNames.size(); i++) {
//            int _index_team = servicePackageNames.get(i).lastIndexOf("#");
//            String aux_team = servicePackageNames.get(i).substring(_index_team);
//            if (aux_team.replace("#", "").equals(team)) {
//                jCBServicePN.addItem(servicePackageNames.get(i).substring(0, _index_team));
//            }
//        }
        // Fill deliverable
        for (int i = 0; i < deliverableList.size(); i++) {
            int _index_team = deliverableList.get(i).lastIndexOf("#");
            String aux_team = deliverableList.get(i).substring(_index_team);
            if (aux_team.replace("#", "").equals(team)) {
                jCBDeliverable.addItem(deliverableList.get(i).substring(0, _index_team));
            }
        }
        // Fill project support domain
        for (int i = 0; i < projectSupportNames.size(); i++) {
            int _index_team = projectSupportNames.get(i).lastIndexOf("#");
            String aux_team = projectSupportNames.get(i).substring(_index_team);
            if (aux_team.replace("#", "").equals(team)) {
                jCBProjectSuppDom.addItem(projectSupportNames.get(i).substring(0, _index_team));
            }
        }
    }

    private void jBDeleteCUSuppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDeleteCUSuppActionPerformed
        // Delete Support CU
        String normalTeam = jCBTeam.getItemAt(jCBTeam.getSelectedIndex());
        String supTeams = jTFSupTeam.getText();
        String supCUs = jTFSupCU.getText();
        String selTeam = jCBSupportedTeam.getSelectedItem().toString();
        String selCU = jCBSupportedCU.getSelectedItem().toString();
        
        HashMap<String, String> cu_list = new HashMap<>();

        // Add keys and values (Team name, Prefix)
        cu_list.put("COP", "C_");
        cu_list.put("VSS", "V_");
        cu_list.put("PSS", "P_");
        cu_list.put("Scoping", "SCP_");

        if (cu_list.containsKey(selTeam)){
            selCU = cu_list.get(selTeam) + selCU;
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
        // Fill supported cu 
        jCBSupportedCU.removeAllItems();
        
        ArrayList<String> cus = new ArrayList<String>();
        String team = "";
        
        if (jCBSupportedTeam.getItemCount() != 0){
            team = jCBSupportedTeam.getSelectedItem().toString();
        }
        
        for (int i = 0; i < tasks_info.size(); i += 9){
            if (team.equals(tasks_info.get(i + 2)))
                if (!cus.contains(tasks_info.get(i + 3)))
                    cus.add(tasks_info.get(i + 3));
        }
        Collections.sort(cus);
        
        for (int i = 0; i < cus.size(); i++){
            jCBSupportedCU.addItem(cus.get(i));
        }
        
    }//GEN-LAST:event_jCBSupportedTeamActionPerformed

    private void jCBTeamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBTeamActionPerformed
        // Add team to user's assignment
        jCBCustomerUnit.removeAllItems();
        ArrayList<String> cus = new ArrayList<>();
        String team = "";
        
        if (jCBTeam.getItemCount() != 0){
            team = jCBTeam.getSelectedItem().toString();
        }  
        
        for (int i = 3; i < tasks_info.size(); i = i + 9) {
            if (team.equals(tasks_info.get(i - 1)))
                if (!cus.contains(tasks_info.get(i))) {
                    cus.add(tasks_info.get(i));
                }
        }
        
        Collections.sort(cus);
        for (int i = 0; i < cus.size(); i++) {
            jCBCustomerUnit.addItem(cus.get(i));
        }
        
    }//GEN-LAST:event_jCBTeamActionPerformed

    private void jBAddSupCUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBAddSupCUActionPerformed
        // Add supported CU:
        String normalTeam = "";
        if (jCBAction.getSelectedItem().toString().equals("Edit user")){
            normalTeam = jCBTeam_user.getSelectedItem().toString();
        }else{
            normalTeam = jCBTeam.getSelectedItem().toString();
        }        
        String anotherTeam = jCBSupportedTeam.getSelectedItem().toString();
        String anotherCU = jCBSupportedCU.getSelectedItem().toString();
        String supTeam = jTFSupTeam.getText();
        String supCU = jTFSupCU.getText();
        
        HashMap<String, String> cu_list = new HashMap<>();

        // Add keys and values (Team name, Prefix)
        cu_list.put("COP", "C_");
        cu_list.put("VSS", "V_");
        cu_list.put("PSS", "P_");
        cu_list.put("Scoping", "SCP_");

        if (cu_list.containsKey(anotherTeam)){
            anotherCU = cu_list.get(anotherTeam) + anotherCU;
        }
//        if (anotherTeam.equals("COP")) {
//            anotherCU = "C_" + anotherCU;
//        } else if (anotherTeam.equals("VSS")) {
//            anotherCU = "V_" + anotherCU;
//        } else if (anotherTeam.equals("PSS")) {
//            anotherCU = "P_" + anotherCU;
//        }

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
        // Add CU in add new network
        jCBNetCustomer.removeAllItems();
        ArrayList<String> CU_list = new ArrayList<>();
        String team = "";
        if (jCBNetTeam.getItemCount() != 0){
            team = jCBNetTeam.getSelectedItem().toString();
        }        
        jCBNetCustomer.removeAllItems();
        for (int i = 1; i < networksinfo.size(); i = i + 11) {
            if (!CU_list.contains(networksinfo.get(i))) {
                if (networksinfo.get(i - 1).equals(team)) {
                    CU_list.add(networksinfo.get(i));
                }
            }
        }
        Collections.sort(CU_list);
        for (int i = 0; i < CU_list.size(); i++) {
            jCBNetCustomer.addItem(CU_list.get(i));
        }
    }//GEN-LAST:event_jCBNetTeamActionPerformed

    private void jCBNetCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBNetCustomerActionPerformed
        //Fill Customer Unit in Add new network
        ArrayList<String> regions1 = new ArrayList<String>();
        jCBNetRegion.removeAllItems();        
        String team = "", cu1 = "";        
        
        if (jCBNetTeam.getItemCount() != 0 && jCBNetCustomer.getItemCount() != 0){
            team = jCBNetTeam.getSelectedItem().toString();  
            cu1 = jCBNetCustomer.getSelectedItem().toString();
        }                
        for (int i = 0; i < marketsinfo.size(); i = i + 5) {
            if (team.equals(marketsinfo.get(i + 3)) && cu1.equals(marketsinfo.get(i + 4))) {
                if (!regions1.contains(marketsinfo.get(i + 2))) {
                    regions1.add(marketsinfo.get(i + 2));
                }
            }
        
        }
        Collections.sort(regions1);
        for (int i = 0; i < regions1.size(); i++) {
            jCBNetRegion.addItem(regions1.get(i));
        }
        
        //Fill project name combo box
        jCBProjectName.removeAllItems();
        ArrayList<String> projects = new ArrayList<String>();       
        
        
        if (jCBNetTeam.getItemCount() != 0 && jCBNetCustomer.getItemCount() != 0){
            team = jCBNetTeam.getSelectedItem().toString();
            cu1 = jCBNetCustomer.getSelectedItem().toString();
        }
        for (int i = 0; i < networksinfo.size(); i += 11){
            if (team.equals(networksinfo.get(i)) && cu1.equals(networksinfo.get(i + 1))){
                if (!projects.contains(networksinfo.get(i + 10))){
                    projects.add(networksinfo.get(i + 10));
                }
            }
            
        }
        Collections.sort(projects);
        for (int i = 0; i < projects.size(); i++){
            jCBProjectName.addItem(projects.get(i));
        }
        
        // Fill activity code in Add new network
        ArrayList<String> activity_code = new ArrayList<String>();
        jCBNetActCode.removeAllItems();       
        
        if (jCBNetTeam.getItemCount() != 0 && jCBNetCustomer.getItemCount() != 0){
            team = jCBNetTeam.getSelectedItem().toString();  
            cu1 = jCBNetCustomer.getSelectedItem().toString();
        }                
        for (int i = 0; i < networksinfo.size(); i = i + 11) {// Team, CU
            if (team.equals(networksinfo.get(i)) && cu1.equals(networksinfo.get(i + 1))) {
                if (!activity_code.contains(networksinfo.get(i + 6))) {
                    activity_code.add(networksinfo.get(i + 6));
                }
            }
        }
        
        Collections.sort(activity_code);
        for (int i = 0; i < activity_code.size(); i++) {
            jCBNetActCode.addItem(activity_code.get(i));
        }
    }//GEN-LAST:event_jCBNetCustomerActionPerformed

    private void jCBNetRegionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBNetRegionActionPerformed
        // TODO add your handling code here:
        List<String> markets = new ArrayList<String>();
        jCBNetMarket.removeAllItems();
        String mar1 = "", team1 = "", cu1 = "", reg1 = "";
        
        if (jCBNetTeam.getItemCount() != 0 && jCBNetCustomer.getItemCount() != 0 && jCBNetRegion.getItemCount() != 0) {
            team1 = jCBNetTeam.getSelectedItem().toString();
            cu1 = jCBNetCustomer.getSelectedItem().toString();
            reg1 = jCBNetRegion.getSelectedItem().toString();
        }
              
        for (int i = 0; i < marketsinfo.size(); i = i + 5) {
            if (team1.equals(marketsinfo.get(i + 3)) && cu1.equals(marketsinfo.get(i + 4)) && reg1.equals(marketsinfo.get(i + 2))) {
                mar1 = marketsinfo.get(i + 3);
                if (!markets.contains(marketsinfo.get(i+1))) {
                    markets.add(marketsinfo.get(i+1));
                
                }
            }
        }
    
        Collections.sort(markets);
        for (int i = 0; i < markets.size(); i++) {
            jCBNetMarket.addItem(markets.get(i));
        }
        
    }//GEN-LAST:event_jCBNetRegionActionPerformed

    private void jCBNetMarketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBNetMarketActionPerformed
        // Fill technology combobox for add new network:
        List<String> techs1 = new ArrayList<String>();
        String tech1 = "";
        jCBNetTech.removeAllItems();
        String mar1 = "", team1 = "", cu1 = "", reg1 = "";
        
        if (jCBNetTeam.getItemCount() != 0 && jCBNetCustomer.getItemCount() != 0 && jCBNetRegion.getItemCount() != 0 && jCBNetMarket.getItemCount() != 0)  {
            team1 = jCBNetTeam.getSelectedItem().toString();
            cu1 = jCBNetCustomer.getSelectedItem().toString();
            reg1 = jCBNetRegion.getSelectedItem().toString();
            mar1 = jCBNetMarket.getSelectedItem().toString();
        }
        
        for (int i = 0; i < networksinfo.size(); i = i + 11) {             
            if (team1.equals(networksinfo.get(i)) && cu1.equals(networksinfo.get(i + 1)) && reg1.equals(networksinfo.get(i + 2))) {
                if (!techs1.contains(networksinfo.get(i + 7)))
                    techs1.add(networksinfo.get(i+7));
            }
        }
        if (techs1.isEmpty()) {
            jCBNetTech.addItem("N/A");
            techs1.add("N/A");
        } 
        Collections.sort(techs1);
        
        for (int i = 0; i < techs1.size(); i++) {
            jCBNetTech.addItem(techs1.get(i));
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
        List<String> CU_list = new ArrayList<String>();
        String team = "";
        if (jCBNetTeamSearch.getItemCount() != 0){
           team = jCBNetTeamSearch.getSelectedItem().toString(); 
        }        
        jCBCUSearch.removeAllItems();        
        for (int i = 1; i < networksinfo.size(); i = i + 11) {
            if (!CU_list.contains(networksinfo.get(i))) {
                if (networksinfo.get(i - 1).equals(team)) {
                    CU_list.add(networksinfo.get(i));
                }
            }
        }
        Collections.sort(CU_list);
        for (int i = 0; i < CU_list.size(); i++) {
            jCBCUSearch.addItem(CU_list.get(i));
        }
    }//GEN-LAST:event_jCBNetTeamSearchActionPerformed

    private void jCBNetSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBNetSearchActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_jCBNetSearchActionPerformed

    private void jCBCUSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBCUSearchActionPerformed
        // Fill Market in search network
        List<String> markets1 = new ArrayList<String>();
        String market1 = "";
        jCBMarketSearch.removeAllItems();
        if (jCBCUSearch.getItemCount() != 0) {
            String cu1 = jCBCUSearch.getSelectedItem().toString();
            String team1 = jCBNetTeamSearch.getSelectedItem().toString();
            for (int i = 0; i < networksinfo.size(); i = i + 11) {
                if (team1.equals(networksinfo.get(i)) && cu1.equals(networksinfo.get(i + 1))) {
                    market1 = networksinfo.get(i + 3);
                    if (markets1.isEmpty()) {
                        markets1.add(market1);
                    } else {
                        if (!markets1.contains(market1)) {
                            markets1.add(market1);
                        }
                    }
                }
            }
            Collections.sort(markets1);
            for (int i = 0; i < markets1.size(); i++) {
                jCBMarketSearch.addItem(markets1.get(i));
            }
        }
    }//GEN-LAST:event_jCBCUSearchActionPerformed

    private void jCBMarketSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBMarketSearchActionPerformed
        // To ADD networks in Edit Networks
        List<String> networks = new ArrayList<String>();
        jCBNetSearch.removeAllItems();
        if (jCBMarketSearch.getItemCount() != 0) {
            String cu1 = jCBCUSearch.getSelectedItem().toString();
            String team1 = jCBNetTeamSearch.getSelectedItem().toString();
            String mar1 = jCBMarketSearch.getSelectedItem().toString();
            for (int i = 4; i < networksinfo.size(); i = i + 11) {
                if (team1.equals(networksinfo.get(i - 4)) && cu1.equals(networksinfo.get(i - 3)) && mar1.equals(networksinfo.get(i - 1))) {
                    if (!networks.contains(networksinfo.get(i))) {
                        networks.add(networksinfo.get(i));
                    }
                }
            }
            Collections.sort(networks);
            for (int i = 0; i < networks.size(); i++) {
                jCBNetSearch.addItem(networks.get(i));
            }
        }
    }//GEN-LAST:event_jCBMarketSearchActionPerformed

    private void jMEditMarketsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMEditMarketsActionPerformed
        // TODO add your handling code here:
        jPView.setVisible(false);
        jPEdit.setVisible(false);
        jPNetworks.setVisible(false);
        jPEditTask.setVisible(false);
        jPMarket.setVisible(true);
        jPEditServicePackage.setVisible(false);
        ResetTaskFields();
        this.setTitle("MRT - Audit & Report - Add, Edit and Delete Markets");
    }//GEN-LAST:event_jMEditMarketsActionPerformed

    private void jBSaveMrktActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSaveMrktActionPerformed
        // Save new market:
        boolean flagNetwork = true;
        // Validar por equipo y cu que no se repita en ese Team - CU
        String teamName = jCBTeamMrkt.getItemAt(jCBTeamMrkt.getSelectedIndex());
        String cuName = jCBCUMrkt.getItemAt(jCBCUMrkt.getSelectedIndex());
        String regionName = jCBRegionMrkt.getItemAt(jCBRegionMrkt.getSelectedIndex());
        String mrktName = jCBMarketList.getSelectedItem().toString();

        if (mrktName == null) {
            flagNetwork = false;
        } else {
            String mrktNameText = String.valueOf(jCBMarketList.getEditor().getItem());
            System.out.println("Market en el field: " + mrktNameText);
            for (int i = 0; i < marketsinfo.size(); i = i + 5) {
                if (mrktNameText.equals(marketsinfo.get(i + 1)) && regionName.equals(marketsinfo.get(i + 2)) && teamName.equals(marketsinfo.get(i + 3)) && cuName.equals(marketsinfo.get(i + 4))) {
                    flagNetwork = false;
                }
            }
        }
        System.out.println("la falg " + flagNetwork);
        if (flagNetwork) {
            jLLoading.setText("Saving market into database...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (jCBMrktAction.getSelectedIndex() == 1) {
                        InsertIntoMarketDB();
                    } else {
                        UpdateMarketsDB();
                    }
                    marketsinfo = new ArrayList<>();
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
            Logger.getLogger(Time_Management.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jBNetTableCSVActionPerformed

    private void jCBMarTeamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBMarTeamActionPerformed
        // Add CU in Edit market
        jCBMarCU.removeAllItems();
        ArrayList<String> cu = new ArrayList<>();
        String team = "";
        if (jCBMarTeam.getItemCount() != 0){
            team = jCBMarTeam.getSelectedItem().toString();
        }      
        
        for (int i = 0; i < marketsinfo.size(); i = i + 5) {
            if (marketsinfo.get(i + 3).equals(team))
                if (!cu.contains(marketsinfo.get(i + 4)))                  
                    cu.add(marketsinfo.get(i + 4));    
        }
        Collections.sort(cu);
        for (int i = 0; i < cu.size(); i++) {
            jCBMarCU.addItem(cu.get(i));
        }
    }//GEN-LAST:event_jCBMarTeamActionPerformed

    private void jCBMarCUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBMarCUActionPerformed
        // TODO add your handling code here:
        List<String> regs = new ArrayList<String>();
        String reg1 = "";
        jCBMarRegion.removeAllItems();
        String team1 = jCBMarTeam.getItemAt(jCBMarTeam.getSelectedIndex());
        String cu1 = jCBMarCU.getItemAt(jCBMarCU.getSelectedIndex());
        for (int i = 0; i < marketsinfo.size(); i = i + 5) {
            reg1 = marketsinfo.get(i + 2);
            if (team1.equals(marketsinfo.get(i + 3)) && cu1.equals(marketsinfo.get(i + 4))) {
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
    }//GEN-LAST:event_jCBMarCUActionPerformed

    private void jCBMarMrktActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBMarMrktActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBMarMrktActionPerformed

    private void jCBMarRegionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBMarRegionActionPerformed
        // TODO add your handling code here:
        List<String> mars = new ArrayList<String>();
        String mar1 = "";
        jCBMarMrkt.removeAllItems();
        if (jCBMarRegion.getItemCount() > 0) {
            String team1 = jCBMarTeam.getItemAt(jCBMarTeam.getSelectedIndex());
            String cu1 = jCBMarCU.getItemAt(jCBMarCU.getSelectedIndex());
            String reg1 = jCBMarRegion.getItemAt(jCBMarRegion.getSelectedIndex());
            for (int i = 0; i < marketsinfo.size(); i++) {
                if (i % 5 == 0) {
                    mar1 = marketsinfo.get(i + 1);
                    if (reg1.equals(marketsinfo.get(i + 2)) && team1.equals(marketsinfo.get(i + 3)) && cu1.equals(marketsinfo.get(i + 4))) {
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
        if (jCBMrktAction.getSelectedIndex() == 1) {
            jPSearchMrkt.setVisible(false);
            jCBRegionMrkt.setEnabled(true);
            jCBTeamMrkt.setEnabled(true);
            jCBTeam_editmarket.setVisible(false);
            jCBCUMrkt.setEnabled(true);
            //Fill combobox Team
            jCBTeamMrkt.removeAllItems();
            jCBTeamMrkt.addItem("Select a team");
            ArrayList<String> _teams = new ArrayList<String>();
            for (int i = 0; i < networksinfo.size(); i=i + 11){
                if (!_teams.contains(networksinfo.get(i))){
                    _teams.add(networksinfo.get(i));
                }            
            }
            Collections.sort(_teams);
            for (int i = 0; i < _teams.size(); i++){
                jCBTeamMrkt.addItem(_teams.get(i));
            }
            
        } else {
            jPSearchMrkt.setVisible(true);
            jCBRegionMrkt.setEnabled(false);
            jCBTeamMrkt.setEnabled(false);
            jCBTeamMrkt.setVisible(false);
            jCBTeam_editmarket.setVisible(true);
            jCBCUMrkt.setEnabled(false);
            //Add team in Edit Market
            jCBMarTeam.removeAllItems();
            jCBMarTeam.addItem("Select a team");
            ArrayList<String> _teams = new ArrayList<String>();
            for (int i = 0; i < networksinfo.size(); i=i + 11){
                if (!_teams.contains(networksinfo.get(i))){
                    _teams.add(networksinfo.get(i));
                }            
            }
            Collections.sort(_teams);
            for (int i = 0; i < _teams.size(); i++){
                jCBMarTeam.addItem(_teams.get(i));
            }
        }
    }//GEN-LAST:event_jCBMrktActionActionPerformed

    private void jCBTeamMrktActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBTeamMrktActionPerformed
        // Add CU in Add new market
        jCBCUMrkt.removeAllItems();
        ArrayList<String> cu_list = new ArrayList<>();
        String team = "";
        if (jCBTeamMrkt.getItemCount() != 0){
            team = jCBTeamMrkt.getSelectedItem().toString();
        }        
        jCBNetCustomer.removeAllItems();
        for (int i = 1; i < networksinfo.size(); i = i + 11) {
            if (!cu_list .contains(networksinfo.get(i))) {
                if (networksinfo.get(i - 1).equals(team)) {
                    cu_list .add(networksinfo.get(i));
                }
            }
        }
        Collections.sort(cu_list);
        for (int i = 0; i < cu_list.size(); i++) {
            jCBCUMrkt.addItem(cu_list.get(i));
        }
    }//GEN-LAST:event_jCBTeamMrktActionPerformed

    private void jBSearchMrktActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSearchMrktActionPerformed
        // Button search, edit market
        String team = jCBMarTeam.getSelectedItem().toString();
        String cu = jCBMarCU.getSelectedItem().toString();
        String region = jCBMarRegion.getSelectedItem().toString();
        String market = jCBMarMrkt.getSelectedItem().toString();
        
        for (int i = 0; i < marketsinfo.size(); i += 5){
            if (marketsinfo.get(i + 3).equals(team) && (marketsinfo.get(i + 4).equals(cu) && (marketsinfo.get(i + 2).equals(region)) && (marketsinfo.get(i + 1).equals(market)))){
                jCBTeam_editmarket.addItem(marketsinfo.get(i + 3));
                jCBCUMrkt.addItem(marketsinfo.get(i + 4));
                jCBRegionMrkt.addItem(marketsinfo.get(i + 2));
                jCBMarketList.addItem(marketsinfo.get(i + 1));
                
            }
                
        }  
      
//        jLLoading.setText("Fetching market from database...");
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                SearchMarketkDB();
//                jDLoading.dispose();
//            }
//        }).start();
//        jDLoading.setVisible(true);
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
                    marketsinfo = new ArrayList<>();
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
        // Fill cu combobox in edit task
        ArrayList<String> cus = new ArrayList<>();
        String team = "";
        
        if (jCBTeamTaskSearch.getItemCount() != 0){
            team = jCBTeamTaskSearch.getSelectedItem().toString();
        }     
        
        jCBCUTaskSearch.removeAllItems();
        for (int i = 3; i < tasks_info.size(); i = i + 9) {
            if (!cus.contains(tasks_info.get(i))) {
                if (tasks_info.get(i - 1).equals(team)) {
                    cus.add(tasks_info.get(i));
                }
            }
        }
        Collections.sort(cus);
        for (int i = 0; i < cus.size(); i++) {
            jCBCUTaskSearch.addItem(cus.get(i));
        }

    }//GEN-LAST:event_jCBTeamTaskSearchActionPerformed

    private void jCBCUTaskSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBCUTaskSearchActionPerformed
        // Fill task combo box to search task:
        List<String> tasks1 = new ArrayList<>();
        jCBTaskSearch.removeAllItems();
        String team = "", cu = "";
        
        if (jCBTeamTaskSearch.getItemCount() != 0 && jCBCUTaskSearch.getItemCount() != 0){
            team = jCBTeamTaskSearch.getSelectedItem().toString();
            cu = jCBCUTaskSearch.getSelectedItem().toString();
        }
        
        for (int i = 1; i < tasks_info.size(); i += 9){
            if (team.equals(tasks_info.get(i+1)) && cu.equals(tasks_info.get(i+2))){
                if (!tasks1.contains(tasks_info.get(i)))
                    tasks1.add(tasks_info.get(i));
            }
        }
        Collections.sort(tasks1);
        for (int i = 0; i < tasks1.size(); i++) {
            jCBTaskSearch.addItem(tasks1.get(i));
        }
    }//GEN-LAST:event_jCBCUTaskSearchActionPerformed

    private void jCBCUMrktActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBCUMrktActionPerformed
        // Add Regions in Add market
        jCBRegionMrkt.removeAllItems();
        ArrayList<String> regions = new ArrayList<>();
        String team = "", cu = "";
        if (jCBTeamMrkt.getItemCount() != 0 && jCBCUMrkt.getItemCount() != 0){
            team = jCBTeamMrkt.getSelectedItem().toString();
            cu = jCBCUMrkt.getSelectedItem().toString();
        }      
        
        for (int i = 0; i < marketsinfo.size(); i = i + 5) {
            if (marketsinfo.get(i + 3).equals(team) && marketsinfo.get(i + 4).equals(cu))
                if (!regions.contains(marketsinfo.get(i + 2)))                  
                    regions.add(marketsinfo.get(i + 2));    
        }
        Collections.sort(regions);
        for (int i = 0; i < regions.size(); i++) {
            jCBRegionMrkt.addItem(regions.get(i));
        }
    }//GEN-LAST:event_jCBCUMrktActionPerformed

    private void jCBRegionMrktActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBRegionMrktActionPerformed
        // Add markets in Add market
        jCBMarketList.removeAllItems();
        ArrayList<String> markets = new ArrayList<>();
        String team = "", cu = "", reg = "";
        if (jCBTeamMrkt.getItemCount() != 0 && jCBCUMrkt.getItemCount() != 0 && jCBRegionMrkt.getItemCount() != 0){
            team = jCBTeamMrkt.getSelectedItem().toString();
            cu = jCBCUMrkt.getSelectedItem().toString();
            reg = jCBRegionMrkt.getSelectedItem().toString();
        }      
        
        for (int i = 0; i < marketsinfo.size(); i = i + 5) {
            if (marketsinfo.get(i + 3).equals(team) && marketsinfo.get(i + 4).equals(cu) && marketsinfo.get(i + 2).equals(reg))
                if (!markets.contains(marketsinfo.get(i + 1)))                  
                    markets.add(marketsinfo.get(i + 1));    
        }
        Collections.sort(markets);
        for (int i = 0; i < markets.size(); i++) {
            jCBMarketList.addItem(markets.get(i));
        }
    }//GEN-LAST:event_jCBRegionMrktActionPerformed

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

    private void jCBMarketListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBMarketListActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBMarketListActionPerformed

    private void jCBAccessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBAccessActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBAccessActionPerformed

    private void jCBTeam_userActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBTeam_userActionPerformed
        // Fill cu
//        String team1 = "";
//        ArrayList<String> cus = new ArrayList<String>();         
//        jCBCustomerUnit.removeAllItems();
//        if (jCBTeam_user.getItemCount() != 0){
//            team1 = jCBTeam_user.getSelectedItem().toString();
//        }   
//        
//        for (int i = 3; i < tasks_info.size(); i += 9)
//            if (team1.equals(tasks_info.get(i - 1)))
//                if (!cus.contains(tasks_info.get(i)))
//                    cus.add(tasks_info.get(i));
//
//        for (int i = 1; i < cus.size(); i ++){
//            jCBCustomerUnit.addItem(cus.get(i));  
//        }
    }//GEN-LAST:event_jCBTeam_userActionPerformed

    private void jCBTaskCUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBTaskCUActionPerformed
        // Fill task type combo box:
        jCBServicePN.removeAllItems();
        jCBTaskType.removeAllItems();
        String team = "";
        String cu = "";
        ArrayList<String> packages = new ArrayList<>();
        ArrayList<String> task_types = new ArrayList<>();
        
        System.out.println("packages: " + service_packages);
        
        if (jCBTaskTeam.getItemCount() != 0 && jCBTaskCU.getItemCount() != 0){
            team = jCBTaskTeam.getSelectedItem().toString();
            cu = jCBTaskCU.getSelectedItem().toString();           
        }        
       
        for (int i = 1; i < service_packages.size(); i += 5) {
            //System.out.println("este deberia ser package: " + service_packages.get(i + 2));
            if (!packages.contains(service_packages.get(i + 2))) {
                if (service_packages.get(i).equals(team)) {
                    packages.add(service_packages.get(i + 2));
                }
            }
        }
        Collections.sort(packages);
        for (int i = 0; i < packages.size(); i++) {
            jCBServicePN.addItem(packages.get(i));
        }

        // Fill task type
        for (int i = 0; i < tasks_info.size(); i += 9) {
            if (tasks_info.get(i+2).equals(team)) {                
                String task_id = tasks_info.get(i);
                // Replace numbers
                int sub = task_id.lastIndexOf("-");
                String substring = task_id.substring(sub);
                task_id = task_id.replace(substring, "");                
                if (!task_types.contains(task_id))
                    task_types.add(task_id);                           
            }        
        }
        
        System.out.println("task types: " + task_types);
        for (int x = 0; x < task_types.size(); x++){
            jCBTaskType.addItem(task_types.get(x));
        }
        
    }//GEN-LAST:event_jCBTaskCUActionPerformed

    private void jTFTaskNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTFTaskNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTFTaskNameActionPerformed

    private void jCBTaskTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBTaskTypeActionPerformed
        // Fill Service package combo box:
//        jCBServicePN.removeAllItems();
//        String team = "";
//        String cu = "";
//        ArrayList<String> packages = new ArrayList<String>();
//        System.out.println("packages: " + service_packages);
//        
//        if (jCBTaskTeam.getItemCount() != 0 && jCBTaskCU.getItemCount() != 0){
//            team = jCBTaskTeam.getSelectedItem().toString();
//            cu = jCBTaskCU.getSelectedItem().toString();
//        }        
//       
//        for (int i = 4; i < service_packages.size(); i = i + 5) {
//            if (!packages.contains(service_packages.get(i))) {
//                if (service_packages.get(i - 2).equals(team)) {
//                    packages.add(service_packages.get(i));
//                }
//            }
//        }
//        System.out.println("llena los packages: " + packages);
//        Collections.sort(packages);
//        for (int i = 0; i < packages.size(); i++) {
//            jCBServicePN.addItem(packages.get(i));
//        }
        
    }//GEN-LAST:event_jCBTaskTypeActionPerformed

    private void jCBServicePNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBServicePNActionPerformed
        // Fill deliverables combo box:
        jCBDeliverable.removeAllItems();
        String team = "";
        String cu = "";
        String s_package = "";
        ArrayList<String> deliverables_list = new ArrayList<>();
        
        if (jCBTaskTeam.getItemCount() != 0 && jCBTaskCU.getItemCount() != 0 && jCBServicePN.getItemCount() != 0){
            team = jCBTaskTeam.getSelectedItem().toString();
            cu = jCBTaskCU.getSelectedItem().toString();
            s_package = jCBServicePN.getSelectedItem().toString();
        } 
        
        for (int i = 1; i < deliverables.size(); i = i + 5) {
            if (!deliverables_list.contains(deliverables.get(i))) {
                if (deliverables.get(i + 2).equals(team)) {
                    deliverables_list.add(deliverables.get(i));
                }
            }
        }
        Collections.sort(deliverables_list);
        for (int i = 0; i < deliverables_list.size(); i++) {
            jCBDeliverable.addItem(deliverables_list.get(i));
        }
 
    }//GEN-LAST:event_jCBServicePNActionPerformed

    private void jCBDeliverableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBDeliverableActionPerformed
        // fill Project Support domain combo box:
        jCBProjectSuppDom.removeAllItems();
        String team = "";        
        ArrayList<String> project_domain = new ArrayList<>();
        
        if (jCBTaskTeam.getItemCount() != 0){
            team = jCBTaskTeam.getSelectedItem().toString();
            
        } 
        
        for (int i = 7; i < tasks_info.size(); i = i + 9) {
            if (!project_domain.contains(tasks_info.get(i))) {
                if (tasks_info.get(i - 5).equals(team)) {
                    project_domain.add(tasks_info.get(i));
                }
            }
        }
        Collections.sort(project_domain);
        for (int i = 0; i < project_domain.size(); i++) {
            jCBProjectSuppDom.addItem(project_domain.get(i));
        }
        
    }//GEN-LAST:event_jCBDeliverableActionPerformed

    private void jMComp_peopleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMComp_peopleActionPerformed
        // Menu item to select people with missing time reported within a specific time frame
        // Initialize frame
        jFrame_Compliance.setTitle("Search for period of time");
        jFrame_Compliance.setIconImage(new ImageIcon(getClass().getResource("/images/MRT_logo.png")).getImage());
        jFrame_Compliance.setSize(800, 300);
        jFrame_Compliance.setResizable(false);
        jFrame_Compliance.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        jFrame_Compliance.setVisible(true);
        jFrame_Compliance.setLocationRelativeTo(null);

        // Init jcombobox Team and Org
        jcb_Team_compliance.addItem("All");
        jcb_Org_compliance.addItem("All");
        for (int i = 0; i < teams.size(); i++) {
            jcb_Team_compliance.addItem(teams.get(i));
        }
        for (int i = 0; i < organizations.size(); i++) {
            jcb_Org_compliance.addItem(organizations.get(i));
        }
        // Week combobox
        Calendar now = Calendar.getInstance();
        int current_week = 0;
        current_week = now.get(Calendar.WEEK_OF_YEAR);
        if (now.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            current_week = current_week - 1;
        }
        // Set current week panel 2
        for (int i = 1; i <= current_week; i++) {
            jcbWeek_Compliance.addItem(String.valueOf(i));
        }
        jcbWeek_Compliance.setSelectedIndex(current_week - 1);
    }//GEN-LAST:event_jMComp_peopleActionPerformed

    private void jB_Export_ComplianceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jB_Export_ComplianceActionPerformed
        // Export compliance report button
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Check selected team
                List<String> teams_list = new ArrayList<String>();
                ArrayList<String> data = new ArrayList<>();
                PreparedStatement preparedStatement;
                
                String _week = jcbWeek_Compliance.getSelectedItem().toString();
                String _org = jcb_Org_compliance.getSelectedItem().toString();

                if (jcb_Team_compliance.getSelectedItem().toString().equals("All")) {
                    for (int i = 0; i < teams.size(); i++){
                        teams_list.add("%" + teams.get(i) + "%");
                    }
                } else {
                    teams_list.add("%" + jcb_Team_compliance.getSelectedItem().toString() + "%");
                }
                
                if (jcb_Org_compliance.getSelectedItem().toString().equals("All")) {
                    _org = "%%";
                } else {
                    _org = "%" + jcb_Org_compliance.getSelectedItem().toString() + "%";
                }
                String sql_table = "", sql = "";
                for (int i = 0; i < teams_list.size(); i++) {
                    sql_table = "metrics_" + teams_list.get(i).toLowerCase().replace("%", "");
                    Connection connection = SQL_connection.getConnection();
                    ResultSet resultSet;

                    sql = "SELECT t1.Signum, t1.Name, IFNULL(t2.Week, '" + _week + "') as Week, IFNULL(t2.Time, '0') as Time\n" +
                            "FROM (\n" +
                            "	SELECT Total.Signum, Total.Name, Correct.Week\n" +
                            "	FROM (\n" +
                            "		SELECT Signum, concat(Last_Name, ' ', Name) as Name\n" +
                            "		FROM users\n" +
                            "		WHERE (Team LIKE ? OR Supporting_Team LIKE ?)\n" +
                            "			AND Organization LIKE ?\n" +
                            "	) as Total\n" +
                            "	LEFT JOIN (\n" +
                            "		SELECT Signum, Name, Week, SUM(Logged_Time) as Time\n" +
                            "		FROM " + sql_table + "\n" +
                            "		WHERE Week = ?\n" +
                            "			AND Organization LIKE ?\n" +
                            "		GROUP BY Signum, Week\n" +
                            "		HAVING SUM(Logged_Time) >= 41.25\n" +
                            "	) as Correct\n" +
                            "	ON Total.Signum = UPPER(Correct.Signum)\n" +
                            "	WHERE Correct.Signum IS NULL\n" +
                            ") as t1\n" +
                            "LEFT JOIN (\n" +
                            "	SELECT Signum, Name, Week, SUM(Logged_Time) as Time\n" +
                            "	FROM " + sql_table + "\n" +
                            "	WHERE Week = ?\n" +
                            "		AND Organization LIKE ?\n" +
                            "	GROUP BY Signum, Week\n" +
                            "	HAVING SUM(Logged_Time) < 41.25\n" +
                            ") as t2\n" +
                            "ON t1.Signum = t2.Signum;";

                    try {
                        preparedStatement = connection.prepareStatement(sql);

                        // Place parameters in query
                        preparedStatement.setString(1, teams_list.get(i));
                        preparedStatement.setString(2, teams_list.get(i));
                        preparedStatement.setString(3, _org);
                        preparedStatement.setString(4, _week);
                        preparedStatement.setString(5, _org);
                        preparedStatement.setString(6, _week);
                        preparedStatement.setString(7, _org);

                        // Execute query
                        System.out.println("Compliance query: " + preparedStatement);
                        resultSet = preparedStatement.executeQuery();
                        // Fetch results
                        data.add(sql_table);
                        data.add("");
                        data.add("");
                        data.add("");
                        while (resultSet.next()) {
                            for (int j = 0; j < 4; j++) {
                                data.add(resultSet.getString(j + 1));
                            }
                        }
                        connection.close();
                        System.out.println("Data: " + data);
                    } catch (SQLException ex) {
                        Logger.getLogger(Time_Management.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println("Error in " + sql_table);
                        continue;
                    }
                }
                // Create csv with data
                String path = "C:\\Users\\" + usersinfo.get(0) + "\\Documents\\Result_compliance_" + usersinfo.get(0) + "_" +
                        jcb_Team_compliance.getSelectedItem().toString() + ".csv";
                try (PrintWriter writer = new PrintWriter(new File(path))) {
                    StringBuilder sb = new StringBuilder();
                    // Header
                    sb.append("Signum" + ',');
                    sb.append("Name" + ',');
                    sb.append("Week" + ',');
                    sb.append("Time" + ',');
                    sb.append('\n');
                    // Fill rows
                    for (int i = 1; i < data.size() + 1; i++) {
                        sb.append("\"" + data.get(i - 1) + "\"");
                        if (((i % 4) == 0)) {
                            //sb.append(",");
                            sb.append("\n");
                        } else {
                            sb.append(",");
                        }
                    }
                    writer.write(sb.toString());
                    System.out.println("File writed successfully");
                    int reply = JOptionPane.showConfirmDialog(null, "Do you want to open it?", "Compliance file created", JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        Desktop.getDesktop().open(new File(path));
                    } else {
                        JOptionPane.showMessageDialog(Time_Management.this, "File was saved to " + path);
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(FMS_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(Time_Management.this, "File could not be created");
                    jDLoading.dispose();
                    jFrame_Compliance.dispose();
                    return;
                } catch (IOException ex) {
                    Logger.getLogger(Time_Management.class.getName()).log(Level.SEVERE, null, ex);
                }
                jDLoading.dispose();
            }
        }
        ).start();
        jDLoading.setVisible(true);
        jFrame_Compliance.dispose();
    }//GEN-LAST:event_jB_Export_ComplianceActionPerformed

    private void jMIFMSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMIFMSActionPerformed
        // Open FMS recording window
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
                    FMS_Time_Report time_r = new FMS_Time_Report();
                    time_r.show();
                    time_r.setLocationRelativeTo(null);
                    loading.dispose();
                    // Confirm exit window
                    time_r.setDefaultCloseOperation(time_r.DO_NOTHING_ON_CLOSE);
                    time_r.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                            if (JOptionPane.showConfirmDialog(time_r, "Are you sure you want to close this window?", "Exit FMS",
                                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                                time_r.dispose();

                            }
                        }
                    });
                } catch (ParseException | IOException ex) {
                    Logger.getLogger(FMS_Time_Report.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
        loading.toFront();
        loading.requestFocus();
        loading.setVisible(true);
    }//GEN-LAST:event_jMIFMSActionPerformed

    private void jCBPackage_billable3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBPackage_billable3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBPackage_billable3ActionPerformed

    private void jCBCUPackageAdd2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBCUPackageAdd2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBCUPackageAdd2ActionPerformed

    private void jCBTeam_package2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBTeam_package2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBTeam_package2ActionPerformed

    private void jCBServicePackageAdd2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBServicePackageAdd2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBServicePackageAdd2ActionPerformed

    private void jCBDeliverables2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBDeliverables2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBDeliverables2ActionPerformed

    private void jBSaveTask5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSaveTask5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jBSaveTask5ActionPerformed

    private void jBSearchTask5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSearchTask5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jBSearchTask5ActionPerformed

    private void jCBDeliverableSearch2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBDeliverableSearch2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBDeliverableSearch2ActionPerformed

    private void jCBTeamSearchPackage2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBTeamSearchPackage2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBTeamSearchPackage2ActionPerformed

    private void jCBPackage_CU2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBPackage_CU2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBPackage_CU2ActionPerformed

    private void jCBPackageSearch2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBPackageSearch2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCBPackageSearch2ActionPerformed

    private void jBDeleteTask5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDeleteTask5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jBDeleteTask5ActionPerformed

    private void jCBPackageActionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBPackageActionActionPerformed
        
        if (jCBPackageAction.getSelectedIndex() == 0){
            jPSearchPackage1.setVisible(false);
            jPAddPackage1.setVisible(false);
            jPSearchDeliverable.setVisible(false);
            jPAddDeliverable.setVisible(false);           
            
            
        // Add new service package:
        }else if (jCBPackageAction.getSelectedIndex() == 1){
            jPSearchPackage1.setVisible(false);
            jPAddPackage1.setVisible(true);
            jCBTeam_addpackage.setVisible(true);
            jPSearchDeliverable.setVisible(false);
            jPAddDeliverable.setVisible(false);
            jCBTeam_editpackage.setVisible(false);
            jCBTeam_editpackage.setEnabled(false);

            //Fill team combo box
            jCBTeam_addpackage.removeAllItems();
            jCBTeam_addpackage.addItem("Select a team...");

            ArrayList<String> teams = new ArrayList<>();

            for (int i = 1; i < service_packages.size(); i += 5){
                if (!teams.contains(service_packages.get(i))){
                    teams.add(service_packages.get(i));
                }
            }
            Collections.sort(teams);
            for (int i = 0; i < teams.size(); i++){
                jCBTeam_addpackage.addItem(teams.get(i));
            }

            // Edit service package:
        } else if (jCBPackageAction.getSelectedIndex() == 2){
            jPSearchPackage1.setVisible(true);
            jPAddPackage1.setVisible(true);
            jPSearchDeliverable.setVisible(false);
            jPAddDeliverable.setVisible(false);
            jCBTeam_editpackage.setVisible(true);
            jCBTeam_editpackage.setEnabled(false);
            jCBTeam_addpackage.setVisible(false);

            //Fill team combo box
            jCBTeamPackageSearch.removeAllItems();
            jCBTeamPackageSearch.addItem("Select a team...");

            ArrayList<String> teams = new ArrayList<>();

            for (int i = 1; i < service_packages.size(); i += 5){
                if (!teams.contains(service_packages.get(i))){
                    teams.add(service_packages.get(i));
                }
            }
            Collections.sort(teams);
            for (int i = 0; i < teams.size(); i++){
                jCBTeamPackageSearch.addItem(teams.get(i));
            }
        }else if (jCBPackageAction.getSelectedIndex() == 3){
            jPSearchPackage1.setVisible(false);
            jPSearchDeliverable.setVisible(false);
            jPAddPackage1.setVisible(false);
            jPAddDeliverable.setVisible(true);
            jCBTeam_editdeliverable.setVisible(false);

            //Fill team combo box
            jCBTeam_adddeliverable.removeAllItems();
            jCBTeam_adddeliverable.addItem("Select a team...");

            ArrayList<String> teams = new ArrayList<>();

            for (int i = 3; i < deliverables.size(); i += 5){
                if (!teams.contains(deliverables.get(i))){
                    teams.add(deliverables.get(i));
                }
            }
            Collections.sort(teams);
            for (int i = 0; i < teams.size(); i++){
                jCBTeam_adddeliverable.addItem(teams.get(i));
            }

        }else if (jCBPackageAction.getSelectedIndex() == 4){
            jPSearchPackage1.setVisible(false);
            jPSearchDeliverable.setVisible(true);
            jPAddPackage1.setVisible(false);
            jPAddDeliverable.setVisible(true);
            jCBTeam_adddeliverable.setVisible(false);
            jCBTeam_editdeliverable.setVisible(true);
            jCBServicePackage_adddeliverable.setEnabled(false);
            jCBCU_adddeliverable.setEnabled(false);

            //Fill team combo box
            jCBTeamSearchDeliverable.removeAllItems();
            jCBTeamSearchDeliverable.addItem("Select a team...");

            ArrayList<String> teams = new ArrayList<>();

            for (int i = 3; i < deliverables.size(); i += 5){
                if (!teams.contains(deliverables.get(i))){
                    teams.add(deliverables.get(i));
                }
            }
            Collections.sort(teams);
            for (int i = 0; i < teams.size(); i++){
                jCBTeamSearchDeliverable.addItem(teams.get(i));
            }

        }
                
    }//GEN-LAST:event_jCBPackageActionActionPerformed

    private void jBDeleteTask1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDeleteTask1ActionPerformed
        // Delete service package
        boolean flagNetwork = true;

        //flagNetwork = jTFMarket.getText();
        if (flagNetwork) {
            jLLoading.setText("Deleting from database...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DeletePackage();
                    service_packages.clear();
                    GetServicePackages();
                    jDLoading.dispose();
                }
            }).start();
            jDLoading.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a service package");
        }
    }//GEN-LAST:event_jBDeleteTask1ActionPerformed

    private void jBSearchTask1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSearchTask1ActionPerformed
        // Search button in Edit Service Package
        String team = jCBTeamPackageSearch.getSelectedItem().toString();
        String cu = jCBCUPackageSearch.getSelectedItem().toString();
        String s_packages = jCBServicePackageSearch.getSelectedItem().toString();

        for (int i = 0; i < service_packages.size(); i = i + 5) {
            if (team.equals(service_packages.get(i + 1)) && cu.equals(service_packages.get(i + 2)) && s_packages.equals(service_packages.get(i + 3))) {
                jCBTeam_editpackage.removeAllItems();
                jCBCU_packageadd.removeAllItems();
                jCBServicePackage_add.removeAllItems();
                if (team.equals("SDU")){
                    jCBBillable_packageadd.setSelectedItem(1);
                } else
                jCBBillable_packageadd.setSelectedItem(0);
                jCBTeam_editpackage.addItem(service_packages.get(i+1));
                jCBCU_packageadd.addItem(service_packages.get(i+2));
                jCBServicePackage_add.addItem(service_packages.get(i+3));

            }
        }
    }//GEN-LAST:event_jBSearchTask1ActionPerformed

    private void jCBTeamPackageSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBTeamPackageSearchActionPerformed
        // Fill CU in Search package :
        jCBCUPackageSearch.removeAllItems();
        String team = "";
        ArrayList<String> cus = new ArrayList<>();

        if (jCBTeamPackageSearch.getItemCount() != 0)
        team = jCBTeamPackageSearch.getSelectedItem().toString();

        for (int i = 2; i < service_packages.size(); i += 5){
            if (team.equals(service_packages.get(i - 1)))
            if (!cus.contains(service_packages.get(i))){
                cus.add(service_packages.get(i));
            }
        }
        Collections.sort(cus);
        for (int i = 0; i < cus.size(); i++){
            jCBCUPackageSearch.addItem(cus.get(i));
        }
    }//GEN-LAST:event_jCBTeamPackageSearchActionPerformed

    private void jCBCUPackageSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBCUPackageSearchActionPerformed
        // Fill packages in search service packages:
        jCBServicePackageSearch.removeAllItems();
        String team = "", cu = "";
        ArrayList<String> s_packages = new ArrayList<>();

        if (jCBTeamPackageSearch.getItemCount() != 0 && jCBCUPackageSearch.getItemCount() != 0){
            team = jCBTeamPackageSearch.getSelectedItem().toString();
            cu = jCBCUPackageSearch.getSelectedItem().toString();
        }

        for (int i = 3; i < service_packages.size(); i += 5){
            if (team.equals(service_packages.get(i - 2)) && cu.equals(service_packages.get(i-1)))
            if (!s_packages.contains(service_packages.get(i))){
                s_packages.add(service_packages.get(i));
            }
        }
        Collections.sort(s_packages);
        for (int i = 0; i < s_packages.size(); i++){
            jCBServicePackageSearch.addItem(s_packages.get(i));
        }
    }//GEN-LAST:event_jCBCUPackageSearchActionPerformed

    private void jBDeleteDeliverableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBDeleteDeliverableActionPerformed
        // Delete deliverable:
        boolean flagNetwork = true;

        //flagNetwork = jTFMarket.getText();
        if (flagNetwork) {
            jLLoading.setText("Deleting from database...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DeleteDeliverable();
                    deliverables.clear();
                    GetDeliverables();
                    jDLoading.dispose();
                }
            }).start();
            jDLoading.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a service package");
        }
    }//GEN-LAST:event_jBDeleteDeliverableActionPerformed

    private void jCBCUSearchDeliverableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBCUSearchDeliverableActionPerformed
        // Fill service package combo box in seach deliverable:
        jCBPackage_deliverablesearch.removeAllItems();
        String team = "", cu = "";
        ArrayList<String> s_packages = new ArrayList<>();

        if (jCBTeamSearchDeliverable.getItemCount() != 0 && jCBCUSearchDeliverable.getItemCount() != 0){
            team = jCBTeamSearchDeliverable.getSelectedItem().toString();
            cu = jCBCUSearchDeliverable.getSelectedItem().toString();

        }

        for (int i = 2; i < deliverables.size(); i += 5){
            if (team.equals(deliverables.get(i + 1)) && cu.equals(deliverables.get(i + 2)))
            if (!s_packages.contains(deliverables.get(i))){
                s_packages.add(deliverables.get(i));
            }
        }
        Collections.sort(s_packages);
        for (int i = 0; i < s_packages.size(); i++){
            jCBPackage_deliverablesearch.addItem(s_packages.get(i));
        }

    }//GEN-LAST:event_jCBCUSearchDeliverableActionPerformed

    private void jCBTeamSearchDeliverableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBTeamSearchDeliverableActionPerformed
        // Fill CU in search deliverable:
        jCBCUSearchDeliverable.removeAllItems();
        String team = "";
        ArrayList<String> cus = new ArrayList<>();

        if (jCBTeamSearchDeliverable.getItemCount() != 0)
        team = jCBTeamSearchDeliverable.getSelectedItem().toString();

        for (int i = 4; i < deliverables.size(); i += 5){
            if (team.equals(deliverables.get(i - 1)))
            if (!cus.contains(deliverables.get(i))){
                cus.add(deliverables.get(i));
            }
        }
        Collections.sort(cus);
        for (int i = 0; i < cus.size(); i++){
            jCBCUSearchDeliverable.addItem(cus.get(i));
        }
    }//GEN-LAST:event_jCBTeamSearchDeliverableActionPerformed

    private void jBSearchDeliverableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSearchDeliverableActionPerformed
        // Search deliverable:
        String team = jCBTeamSearchDeliverable.getSelectedItem().toString();
        String cu = jCBCUSearchDeliverable.getSelectedItem().toString();
        String s_packages = jCBPackage_deliverablesearch.getSelectedItem().toString();
        String _deliverables = jCBDeliverableSearch1.getSelectedItem().toString();

        for (int i = 0; i < deliverables.size(); i = i + 5) {
            if (team.equals(deliverables.get(i + 3)) && cu.equals(deliverables.get(i + 4))
                && s_packages.equals(deliverables.get(i + 2)) && _deliverables.equals(deliverables.get(i + 1))) {
                jCBTeam_editdeliverable.removeAllItems();
                jCBCU_adddeliverable.removeAllItems();
                jCBServicePackage_adddeliverable.removeAllItems();
                jCBDeliverable_add.removeAllItems();

                jCBTeam_editdeliverable.addItem(deliverables.get(i+3));
                jCBCU_adddeliverable.addItem(deliverables.get(i+4));
                jCBServicePackage_adddeliverable.addItem(deliverables.get(i+2));
                jCBDeliverable_add.addItem(deliverables.get(i+1));

            }
        }
    }//GEN-LAST:event_jBSearchDeliverableActionPerformed

    private void jCBPackage_deliverablesearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBPackage_deliverablesearchActionPerformed
        // Fill deliverable in deliverable search:
        jCBDeliverableSearch1.removeAllItems();
        String team = "", cu= "", s_p = "";
        ArrayList<String> _deliverables = new ArrayList<>();

        if (jCBTeamSearchDeliverable.getItemCount() != 0 && jCBCUSearchDeliverable.getItemCount() != 0 && jCBPackage_deliverablesearch.getItemCount() != 0){
            team = jCBTeamSearchDeliverable.getSelectedItem().toString();
            cu = jCBCUSearchDeliverable.getSelectedItem().toString();
            s_p = jCBPackage_deliverablesearch.getSelectedItem().toString();
        }

        for (int i = 1; i < deliverables.size(); i += 5){
            if (team.equals(deliverables.get(i+2)) && cu.equals(deliverables.get(i+3)) && s_p.equals(deliverables.get(i+1)))
            if (!_deliverables.contains(deliverables.get(i))){
                _deliverables.add(deliverables.get(i));
            }
        }
        Collections.sort(_deliverables);
        for (int i = 0; i < _deliverables.size(); i++){
            jCBDeliverableSearch1.addItem(_deliverables.get(i));
        }
    }//GEN-LAST:event_jCBPackage_deliverablesearchActionPerformed

    private void jBSaveDeliverableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSaveDeliverableActionPerformed
        //Save deliverable:
        boolean flagPackage = true;
        // Validar por equipo y cu que no se repita en ese Team - CU
        String team = jCBTeam_adddeliverable.getSelectedItem().toString();
        String cu = jCBCU_adddeliverable.getSelectedItem().toString();
        String s_package = jCBServicePackage_adddeliverable.getSelectedItem().toString();
        String _deliverable = jCBDeliverable_add.getSelectedItem().toString();
        String msg = "";

        if (_deliverable == null || _deliverable.equals(" ") || _deliverable.equals("")) {
            flagPackage = false;
            msg = "Deliverable is empty!";
        } else {
            for (int i = 0; i < deliverables.size(); i = i + 5) {
                if (team.equals(deliverables.get(i + 3)) && cu.equals(deliverables.get(i + 4))
                    && s_package.equals(deliverables.get(i + 2)) && _deliverable.equals(deliverables.get(i + 1))) {
                    flagPackage = false;
                    msg = "Deliverable already exists!";
                }
            }
        }
        if (flagPackage) {
            jLLoading.setText("Saving Deliverable into database...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (jCBPackageAction.getSelectedIndex() == 3) {
                        InsertDeliverable();
                    } else if (jCBPackageAction.getSelectedIndex() == 4){
                        UpdateDeliverable();
                    }
                    deliverables.clear();
                    GetDeliverables();
                    jDLoading.dispose();
                }
            }).start();
            jDLoading.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, msg);
        }
    }//GEN-LAST:event_jBSaveDeliverableActionPerformed

    private void jCBTeam_adddeliverableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBTeam_adddeliverableActionPerformed
        // Fill cu in add deliverable:
        jCBCU_adddeliverable.removeAllItems();
        String team = "";
        ArrayList<String> cus = new ArrayList<>();

        if (jCBTeam_adddeliverable.getItemCount() != 0)
        team = jCBTeam_adddeliverable.getSelectedItem().toString();

        for (int i = 4; i < deliverables.size(); i += 5){
            if (team.equals(deliverables.get(i - 1)))
            if (!cus.contains(deliverables.get(i))){
                cus.add(deliverables.get(i));
            }
        }
        Collections.sort(cus);
        for (int i = 0; i < cus.size(); i++){
            jCBCU_adddeliverable.addItem(cus.get(i));
        }
    }//GEN-LAST:event_jCBTeam_adddeliverableActionPerformed

    private void jCBCU_adddeliverableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBCU_adddeliverableActionPerformed
        // Fill service package:
        jCBServicePackage_adddeliverable.removeAllItems();
        String team = "", cu= "";
        ArrayList<String> s_packages = new ArrayList<>();

        if (jCBTeam_adddeliverable.getItemCount() != 0 && jCBCU_adddeliverable.getItemCount() != 0){
            team = jCBTeam_adddeliverable.getSelectedItem().toString();
            cu = jCBCU_adddeliverable.getSelectedItem().toString();
        }

        for (int i = 3; i < service_packages.size(); i += 5){
            if (team.equals(service_packages.get(i - 2)) && cu.equals(service_packages.get(i-1)))
            if (!s_packages.contains(service_packages.get(i))){
                s_packages.add(service_packages.get(i));
            }
        }
        Collections.sort(s_packages);
        for (int i = 0; i < s_packages.size(); i++){
            jCBServicePackage_adddeliverable.addItem(s_packages.get(i));
        }
    }//GEN-LAST:event_jCBCU_adddeliverableActionPerformed

    private void jCBServicePackage_adddeliverableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBServicePackage_adddeliverableActionPerformed
        // Fill deliverables combo box in add deliverable:
        jCBDeliverable_add.removeAllItems();
        String team = "", cu= "", s_p = "";
        ArrayList<String> _deliverables = new ArrayList<>();

        if (jCBTeam_adddeliverable.getItemCount() != 0 && jCBCU_adddeliverable.getItemCount() != 0 && jCBServicePackage_adddeliverable.getItemCount() != 0){
            team = jCBTeam_adddeliverable.getSelectedItem().toString();
            cu = jCBCU_adddeliverable.getSelectedItem().toString();
            s_p = jCBServicePackage_adddeliverable.getSelectedItem().toString();
        }

        for (int i = 1; i < deliverables.size(); i += 5){
            if (team.equals(deliverables.get(i+2)) && cu.equals(deliverables.get(i+3)) && s_p.equals(deliverables.get(i+1)))
            if (!_deliverables.contains(deliverables.get(i))){
                _deliverables.add(deliverables.get(i));
            }
        }
        Collections.sort(_deliverables);
        for (int i = 0; i < _deliverables.size(); i++){
            jCBDeliverable_add.addItem(_deliverables.get(i));
        }
    }//GEN-LAST:event_jCBServicePackage_adddeliverableActionPerformed

    private void jMEditServicePackageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMEditServicePackageActionPerformed
        // When user select service package:
        jPView.setVisible(false);
        jPEdit.setVisible(false);
        jPEditTask.setVisible(false);
        jPNetworks.setVisible(false);
        jPMarket.setVisible(false);
        jPEditServicePackage.setVisible(true);
        this.setTitle("MRT - Audit & Report - Add, Edit and Delete Service Packages and Deliverables");
    }//GEN-LAST:event_jMEditServicePackageActionPerformed

    private void jCBCU_packageaddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBCU_packageaddActionPerformed
        // Fill Service Package information:
        jCBServicePackage_add.removeAllItems();
        String team = "", cu = "";
        ArrayList<String> s_packages = new ArrayList<>();

        if (jCBTeam_addpackage.getItemCount() != 0 && jCBCU_packageadd.getItemCount() != 0){
            team = jCBTeam_addpackage.getSelectedItem().toString();
            cu = jCBCU_packageadd.getSelectedItem().toString();
        }

        for (int i = 3; i < service_packages.size(); i += 5){
            if (team.equals(service_packages.get(i - 2)) && cu.equals(service_packages.get(i-1)))
            if (!s_packages.contains(service_packages.get(i))){
                s_packages.add(service_packages.get(i));
            }
        }
        Collections.sort(s_packages);
        for (int i = 0; i < s_packages.size(); i++){
            jCBServicePackage_add.addItem(s_packages.get(i));
        }
    }//GEN-LAST:event_jCBCU_packageaddActionPerformed

    private void jCBTeam_addpackageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBTeam_addpackageActionPerformed
        // Fill CU information in add service package
        jCBCU_packageadd.removeAllItems();
        String team = "";
        ArrayList<String> cus = new ArrayList<>();

        if (jCBTeam_addpackage.getItemCount() != 0)
        team = jCBTeam_addpackage.getSelectedItem().toString();

        for (int i = 2; i < service_packages.size(); i += 5){
            if (team.equals(service_packages.get(i - 1)))
            if (!cus.contains(service_packages.get(i))){
                cus.add(service_packages.get(i));
            }
        }
        Collections.sort(cus);
        for (int i = 0; i < cus.size(); i++){
            jCBCU_packageadd.addItem(cus.get(i));
        }
    }//GEN-LAST:event_jCBTeam_addpackageActionPerformed

    private void jBSaveTask2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBSaveTask2ActionPerformed
        //Save service package
        boolean flagPackage = true;
        // Validar por equipo y cu que no se repita en ese Team - CU
        String team = jCBTeam_addpackage.getSelectedItem().toString();
        String cu = jCBCU_packageadd.getSelectedItem().toString();
        String s_package = jCBServicePackage_add.getSelectedItem().toString();
        String billable = jCBBillable_packageadd.getSelectedItem().toString();
        String msg = "";

        if (s_package == null || s_package.equals(" ") || s_package.equals("")) {
            flagPackage = false;
            msg = "Service Package is empty!";
        } else {
            for (int i = 0; i < service_packages.size(); i = i + 5) {
                if (team.equals(service_packages.get(i + 1)) && cu.equals(service_packages.get(i + 2)) && s_package.equals(service_packages.get(i + 3)) && billable.equals(service_packages.get(i + 4))) {
                    flagPackage = false;
                    msg = "Service Package already exists!";
                }
            }
        }
        if (flagPackage) {
            jLLoading.setText("Saving service package into database...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (jCBPackageAction.getSelectedIndex() == 1) {
                        InsertServicePackage();
                    } else if (jCBPackageAction.getSelectedIndex() == 2){
                        UpdatePackages();
                    }
                    service_packages.clear();
                    GetServicePackages();
                    jDLoading.dispose();
                }
            }).start();
            jDLoading.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, msg);
        }
    }//GEN-LAST:event_jBSaveTask2ActionPerformed

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
        for (int i = 0; i < tasktypes.size(); i++) {
            String task_id = tasktypes.get(i);
            // Replace numbers
            int sub = task_id.lastIndexOf("-");
            System.out.println("sub: " + sub);
            String substring = task_id.substring(sub);
            System.out.println("substring: " + substring);
            task_id = task_id.replace(substring, "");
            System.out.println("task id: " + task_id);
            tasktypes.set(i, task_id);
        }
        tasktypes = tasktypes.stream().distinct().collect(Collectors.toList());
    }

    private void GetNetworksSearch() {
        String netHeader[] = {"PD", "Network", "Activity Code", "Region", "Market", "Team", "Customer Unit", "Responsible", "Subnetwork", "Technology", "Project"};
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(netHeader);
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultset;
        String actcode, team, subnet, reg, markt, tech;
        String[] row = new String[11];
        try {
            String sql = "";
            sql = "SELECT PD, Network, Activity_Code, "
                    + "Region, Market, Team, Customer, "
                    + "Responsible, Subnetwork, Technology, Project FROM networks ORDER BY Team asc;";
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultset = preparedStatement.executeQuery();            
            while (resultset.next()) {
                String[] row1 = {resultset.getString("Team"), resultset.getString("Customer"), resultset.getString("Region"), resultset.getString("Market"), resultset.getString("Network"),
                    resultset.getString("Subnetwork"), resultset.getString("Activity_code"), resultset.getString("Technology"), resultset.getString("PD"),
                    resultset.getString("Responsible"), resultset.getString("Project")};
                List<String> newList1 = Arrays.asList(row1);

                networksinfo.addAll(newList1);

                for (int i = 0; i < 11; i++) {
                    row[i] = resultset.getString(i + 1);
                }
                if (networks.isEmpty()) {
                    networks.add(resultset.getString("Network"));
                } else {
                    if (!networks.contains(resultset.getString("Network"))) {
                        networks.add(resultset.getString("Network"));
                    }
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
    
    private void GetServicePackages() {        
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultset;
        
        try {
            String sql = "";
            sql = "SELECT id, Team, Customer_Unit, Service_Package, Billable FROM service_package ORDER BY Team asc;";
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultset = preparedStatement.executeQuery();            
            while (resultset.next()) {
                String[] row1 = {resultset.getString("id"), resultset.getString("Team"), resultset.getString("Customer_Unit"), 
                    resultset.getString("Service_Package"), resultset.getString("Billable")};
                List<String> newList1 = Arrays.asList(row1);

                service_packages.addAll(newList1);

                
            }
                       
           
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    
    private void InsertDeliverable() {
        Connection connection;
        PreparedStatement preparedStatement;
        //ResultSet resultset;
        try {
            String team = jCBTeam_adddeliverable.getSelectedItem().toString();
            String cu = jCBCU_adddeliverable.getSelectedItem().toString();
            String s_package = jCBServicePackage_adddeliverable.getSelectedItem().toString();
            String _deliverable = jCBDeliverable_add.getSelectedItem().toString();
            String id = "";
            

            String cu1 = cu.replace("&", "").replace("-", "");            
                
            id = team.substring(0, 3).toUpperCase() + "-" + cu1.substring(0, 3).toUpperCase() + "-1";
            int contador = 1;
            for (int i = 0; i < deliverables.size(); i = i + 5) {
                if (deliverables.get(i).equals(id + String.valueOf(contador))) {
                    contador++;
                }
            }

            id = id + String.valueOf(contador);

            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO deliverables (id, Deliverable, Service_Package, Team, Customer_Unit) "
                    + "VALUES (?, ?, ?, ?, ?)");

            preparedStatement.setObject(1, id);
            preparedStatement.setObject(2, _deliverable);
            preparedStatement.setObject(3, s_package);
            preparedStatement.setObject(4, team);
            preparedStatement.setObject(5, cu);
            preparedStatement.executeUpdate();

            System.out.println("Query: " + preparedStatement);
            connection.close();

            JOptionPane.showMessageDialog(this, "Deliverable saved successfully.");
        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Something went wrong, please try again later.");
        }
    }

    private void DeleteDeliverable() {
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            String team = jCBTeamSearchDeliverable.getSelectedItem().toString();
            String cu = jCBCUSearchDeliverable.getSelectedItem().toString();
            String s_packages = jCBPackage_deliverablesearch.getSelectedItem().toString();
            String _deliverables = jCBDeliverableSearch1.getSelectedItem().toString();
            String id = "";
            
            for (int i = 0; i < deliverables.size(); i+=5) {
                if (team.equals(deliverables.get(i + 1)) && cu.equals(deliverables.get(i + 2)) 
                        && s_packages.equals(deliverables.get(i + 3)) && _deliverables.equals(deliverables.get(i + 3))) {
                    id = deliverables.get(i);
                }
            }

            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("DELETE FROM deliverables "
                    + "WHERE id = ?");
            preparedStatement.setObject(1, id);
            preparedStatement.executeUpdate();
            System.out.println("Query: " + preparedStatement);
            connection.close();


            JOptionPane.showMessageDialog(this, "Deliverable deleted successfully.");
        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Something went wrong, please try again later.");
        }
    }
    
    private void UpdateDeliverable() {
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            String team = jCBTeamSearchDeliverable.getSelectedItem().toString();
            String cu = jCBCUSearchDeliverable.getSelectedItem().toString();
            String s_package = jCBPackage_deliverablesearch.getSelectedItem().toString(); 
            String _deliverable = jCBDeliverableSearch1.getSelectedItem().toString();
            String new_deliverable = jCBDeliverable_add.getSelectedItem().toString();
            String id = "";
            
            for (int i = 0; i < deliverables.size(); i+=5) {
                if (team.equals(deliverables.get(i + 3)) && cu.equals(deliverables.get(i + 4)) 
                        && s_package.equals(deliverables.get(i + 2)) && _deliverable.equals(deliverables.get(i + 1))) {
                    id = deliverables.get(i);
                }
                
            }
            
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("UPDATE deliverables SET Deliverable = ?"
                    + "WHERE id = ?");
            preparedStatement.setObject(1, new_deliverable);
            preparedStatement.setObject(2, id);
            preparedStatement.executeUpdate();
            System.out.println("Query: " + preparedStatement);
            connection.close();

            JOptionPane.showMessageDialog(this, "Deliverable updated successfully.");
        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Something went wrong, please try again later.");
        }
    }
    
    
    
    private void InsertServicePackage() {
        Connection connection;
        PreparedStatement preparedStatement;
        //ResultSet resultset;
        try {
            String team = jCBTeam_addpackage.getSelectedItem().toString();
            String cu = jCBCU_packageadd.getSelectedItem().toString();
            String s_package = jCBServicePackage_add.getSelectedItem().toString();
            String billable = jCBBillable_packageadd.getSelectedItem().toString();
            String id = "";
            

            String cu1 = cu.replace("&", "").replace("-", "");            
                
            id = team.substring(0, 3).toUpperCase() + "-" + cu1.substring(0, 3).toUpperCase() + "-1";
            int contador = 1;
            for (int i = 0; i < service_packages.size(); i = i + 5) {
                if (service_packages.get(i).equals(id + String.valueOf(contador))) {
                    contador++;
                }
            }

            id = id + String.valueOf(contador);

            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO service_package (id, Team, Customer_Unit, Service_Package, Billable) "
                    + "VALUES (?, ?, ?, ?, ?)");

            preparedStatement.setObject(1, id);
            preparedStatement.setObject(2, team);
            preparedStatement.setObject(3, cu);
            preparedStatement.setObject(4, s_package);
            preparedStatement.setObject(5, billable);
            preparedStatement.executeUpdate();

            System.out.println("Query: " + preparedStatement);
            connection.close();

            JOptionPane.showMessageDialog(this, "Service Package saved successfully.");
        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Something went wrong, please try again later.");
        }
    }

    private void DeletePackage() {
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            String team = jCBTeamPackageSearch.getSelectedItem().toString();
            String cu = jCBCUPackageSearch.getSelectedItem().toString();
            String s_package = jCBServicePackageSearch.getSelectedItem().toString();
            String id = "";
            
            for (int i = 0; i < service_packages.size(); i+=5) {
                if (team.equals(service_packages.get(i + 1)) && cu.equals(service_packages.get(i + 2)) && s_package.equals(service_packages.get(i + 3))) {
                    id = service_packages.get(i);
                }
                
            }

            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("DELETE FROM service_package "
                    + "WHERE id = ?");
            preparedStatement.setObject(1, id);
            preparedStatement.executeUpdate();
            System.out.println("Query: " + preparedStatement);
            connection.close();


            JOptionPane.showMessageDialog(this, "Service Package deleted successfully.");
        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Something went wrong, please try again later.");
        }
    }
    
    private void UpdatePackages() {
        Connection connection;
        PreparedStatement preparedStatement;
        try {
            String team = jCBTeamPackageSearch.getSelectedItem().toString();
            String cu = jCBCUPackageSearch.getSelectedItem().toString();
            String s_package = jCBServicePackageSearch.getSelectedItem().toString();
            String new_package = jCBServicePackage_add.getSelectedItem().toString();
            String id = "";
            
            for (int i = 0; i < service_packages.size(); i+=5) {
                if (team.equals(service_packages.get(i + 1)) && cu.equals(service_packages.get(i + 2)) && s_package.equals(service_packages.get(i + 3))) {
                    id = service_packages.get(i);
                }
                
            }

            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("UPDATE service_package SET Service_Package = ?"
                    + "WHERE id = ?");
            preparedStatement.setObject(1, new_package);
            preparedStatement.setObject(2, id);
            preparedStatement.executeUpdate();
            System.out.println("Query: " + preparedStatement);
            connection.close();

            JOptionPane.showMessageDialog(this, "Service Package updated successfully.");
        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Something went wrong, please try again later.");
        }
    }
    
    
    private void GetDeliverables() {        
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultset;
        
        try {
            String sql = "";
            sql = "SELECT id, Deliverable, Service_Package, Team, Customer_Unit FROM deliverables ORDER BY Team asc;";
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultset = preparedStatement.executeQuery();            
            while (resultset.next()) {
                String[] row1 = {resultset.getString("id"), resultset.getString("Deliverable"), resultset.getString("Service_Package"), 
                    resultset.getString("Team"), resultset.getString("Customer_Unit")};
                List<String> newList1 = Arrays.asList(row1);

                deliverables.addAll(newList1);

            }
        } catch (SQLException e) {
            System.out.println(e);
        }
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
            String[] organi = new String[100];
            List<String> orgs = new ArrayList<String>();
            sql = "SELECT Signum, Last_Name, Name, Customer_Unit, Team, Organization, Line_Manager, Access, Supporting_Team, Supporting_CU, Job_Stage, Act_Type, CATS_Number"
                    + " FROM users ORDER BY Last_Name asc;";
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultset = preparedStatement.executeQuery();
            while (resultset.next()) {               
                String[] row1 = {resultset.getString("Signum"), resultset.getString("Last_Name"), resultset.getString("Name"), resultset.getString("Customer_Unit"), resultset.getString("Team"),
                    resultset.getString("Organization"), resultset.getString("Line_Manager"), resultset.getString("Access"), resultset.getString("Supporting_Team"), resultset.getString("Supporting_CU"),
                    resultset.getString("Job_Stage"), resultset.getString("Act_Type"), resultset.getString("CATS_Number")};
                
                List<String> newList1 = Arrays.asList(row1);

                users_info.addAll(newList1);
                
                for (int i = 0; i < 13; i++) {
                    row[i] = resultset.getString(i + 1);
                }
                org1 = resultset.getString("Organization");
                cu1 = resultset.getString("Customer_Unit");
                team1 = resultset.getString("Team");
                
                if (!teams.contains(team1)){
                    teams.add(team1);
                }
                Collections.sort(teams);
                
                if (!organizations.isEmpty()) {
                    if (!organizations.contains(org1)) {
                        organizations.add(org1);
                        //jCBOrganization.addItem(org1);
                        if (org1.contains("MX")){
                            organi = org1.split("MX ");
                        } else if (org1.contains("Mexico")) {
                            organi = org1.split("Mexico ");
                        }
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
                Collections.sort(organizations);
                model.addRow(row);
                
                //Delete Director signum to not displaying in Table
                for (int i = 0; i < model.getRowCount(); i++){
                    if (((String)model.getValueAt(i, 0)).equals("EJOSEBL"))
                        model.removeRow(i);
                }
            }
            Collections.sort(orgs);
            for (int i = 0; i < orgs.size(); i++) {
                if (!orgs.get(i).equals("MANA")) {
                    jCBOrgMetrics.addItem(orgs.get(i));
                } else {
                    jCBOrgMetrics.addItem("All");
                }
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
        tasks.clear();
        servicePackageNames.clear();
        deliverableList.clear();
        projectSupportNames.clear();
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultset;
        String[] column = {"Task_ID", "Task", "Team", "Customer Unit", "SAP_Billable", "Service Package Name",
            "Deliverable", "Project Support Domain", "LoE"};
        String[] row = new String[8];
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(column);
        try {
            String sql, taskid1, task1, team1, cu1;
            sql = "SELECT Task_ID, Task, Team, Customer_Unit, SAP_Billable, Service_Package_Name, Deliverable, "
                    + "Project_Support_Domain, LoE FROM tasks ORDER BY Team asc;";
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultset = preparedStatement.executeQuery();
            ArrayList<String> _teams = new ArrayList<>();
            while (resultset.next()) {
                String[] row4 = {resultset.getString("Task_ID"), resultset.getString("Task"), resultset.getString("Team"), resultset.getString("Customer_Unit"),
                    resultset.getString("SAP_Billable"), resultset.getString("Service_Package_Name"), resultset.getString("Deliverable"),
                    resultset.getString("Project_Support_Domain"), resultset.getString("LoE")};
                
                List<String> newList1 = Arrays.asList(row4);

                tasks_info.addAll(newList1);
                
                String[] row1 = {resultset.getString("Team"), resultset.getString("Customer_Unit")};
               

                String spn = resultset.getString("Service_Package_Name");
                spn += "#" + resultset.getString("Team");
                if (servicePackageNames.size() == 0) {
                    servicePackageNames.add(spn);
                } else {
                    if (!servicePackageNames.contains(spn)) {
                        servicePackageNames.add(spn);
                    }
                }

                String deliv = resultset.getString("Deliverable");
                deliv += "#" + resultset.getString("Team");
                if (deliverableList.size() == 0) {
                    deliverableList.add(deliv);
                } else {
                    if (!deliverableList.contains(deliv)) {
                        deliverableList.add(deliv);
                    }
                }

                String psd = resultset.getString("Project_Support_Domain");
                psd += "#" + resultset.getString("Team");
                if (projectSupportNames.size() == 0) {
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
                        while (flag == 0) {
                            if (!tasks.contains(task1)) {
                                tasks.add(task1);
                                String[] taskk = {taskid1, task1};
                                List<String> newList = Arrays.asList(taskk);
                                taskIDandTasks.addAll(newList);
                                flag = 1;
                            } else {
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

                
                model.addRow(row);
            }
            
            Collections.sort(tasks);
            Collections.sort(servicePackageNames);
            Collections.sort(deliverableList);
            Collections.sort(projectSupportNames);
            /*for (int i = 0; i < tasks.size(); i++) {
                jCBTaskSearch.addItem(tasks.get(i));
            }*/
            jTTasksList.setModel(model);
            resizeColumnWidth(jTTasksList);
            //jTTasksList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            connection.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void GetAllMarkets() {
        Connection connection;
        PreparedStatement preparedStatement;
        ResultSet resultset;
        String markID1, mrkt1, reg1, team1, cu1;
        try {
            String sql = "SELECT id, Market, Region, Team, Customer_Unit FROM markets;";
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            resultset = preparedStatement.executeQuery();            
            while (resultset.next()) {
                markID1 = resultset.getString("id");
                mrkt1 = resultset.getString("Market");
                reg1 = resultset.getString("Region");
                team1 = resultset.getString("Team");
                cu1 = resultset.getString("Customer_Unit");
                //marketTeamCU
                String[] row1 = {markID1, mrkt1, reg1, team1, cu1};
                
                List<String> newList = Arrays.asList(row1);
                marketsinfo.addAll(newList);

                
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
            String region = jCBRegionMrkt.getSelectedItem().toString();
            String marketName = String.valueOf(jCBMarketList.getEditor().getItem());
            String teamMarket = jCBTeamMrkt.getSelectedItem().toString();
            String cuMarket = jCBCUMrkt.getSelectedItem().toString();
            String id = "", id_market = "";
            

            String cu1 = cuMarket.replace("&", "").replace("-", "");
            int lenght_market = marketName.length();            
            if (lenght_market < 3){
               id_market = marketName.substring(0, 2).toUpperCase(); 
            } else{
                id_market = marketName.substring(0, 3).toUpperCase();
            }
                
            id = region.substring(0, 3).toUpperCase() + "-" + teamMarket.substring(0, 3).toUpperCase() + "-" + cu1.substring(0, 3).toUpperCase() + "-" + id_market;
            int contador = 1;
            for (int i = 0; i < marketsinfo.size(); i = i + 5) {
                if (marketsinfo.get(i).equals(id + String.valueOf(contador))) {
                    contador++;
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
            for (int i = 0; i < marketsinfo.size(); i++) {
                if (i % 5 == 0) {
                    if (marketName.equals(marketsinfo.get(i + 1)) && region.equals(marketsinfo.get(i + 2)) && teamMarket.equals(marketsinfo.get(i + 3)) && cuMarket.equals(marketsinfo.get(i + 4))) {
                        id = marketsinfo.get(i);
                    }
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
            for (int i = 0; i < marketsinfo.size(); i++) {
                if (i % 5 == 0) {
                    if (marketName.equals(marketsinfo.get(i + 1)) && regMrkt.equals(marketsinfo.get(i + 2)) && teamMrkt.equals(marketsinfo.get(i + 3)) && cuMrkt.equals(marketsinfo.get(i + 4))) {
                        id = marketsinfo.get(i);
                    }
                }
            }

            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("DELETE FROM markets "
                    + "WHERE id = ?");
            preparedStatement.setObject(1, id);
            preparedStatement.executeUpdate();
            System.out.println("Query: " + preparedStatement);
            connection.close();

            for (int i = 0; i < netTeamCURegMark.size(); i++) {
                if (i % 4 == 0) {
                    if (teamMrkt.equals(netTeamCURegMark.get(i)) && cuMrkt.equals(netTeamCURegMark.get(i + 1)) && regMrkt.equals(netTeamCURegMark.get(i + 2)) && marketName.equals(netTeamCURegMark.get(i + 3))) {
                        for (int j = 0; j < 4; j++) {
                            netTeamCURegMark.remove(i);
                        }
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
            String acttype1 = ActTypes[jCBJobStage.getSelectedIndex()];
            String manager_name = jCBLineManager.getSelectedItem().toString();
            String name = "", lmsignum = "", organization = "";
            
            for (int i = 0; i < users_info.size(); i += 13){
                name = users_info.get(i + 1) + " " + users_info.get(i + 2);
                if (manager_name.equals(name))
                    lmsignum = users_info.get(i);
                if (lmsignum.equals(users_info.get(i + 6)))
                    organization = users_info.get(i + 5);
            }

            HashMap<String, String> team_list = new HashMap<String, String>();

            // Add keys and values (Team name, Prefix)
            team_list.put("COP", "C_");
            team_list.put("VSS", "V_");
            team_list.put("PSS", "P_");
            team_list.put("Scoping", "SCP_");
            team_list.put("Sourcing", "");

            if (!team1.equals("SDU") && !cu1.equals("")) {
                cu1 = team_list.get(team1) + cu1;
            }

            if (!js1.equals("N/A")) {
                js1 = "Job Stage " + js1;
            }

            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO users (Signum, Last_Name, Name, Customer_Unit, Team, Organization, Line_Manager, Access, Supporting_Team, Supporting_CU, Job_Stage, Act_Type, CATS_Number) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            preparedStatement.setObject(1, jTFSignum.getText().toUpperCase());
            preparedStatement.setObject(2, jTFLastName.getText());
            preparedStatement.setObject(3, jTFName.getText());
            preparedStatement.setObject(4, cu1);
            preparedStatement.setObject(5, team1);
            preparedStatement.setObject(6, organization);
            preparedStatement.setObject(7, lmsignum);
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
               
            JOptionPane.showMessageDialog(this, "User saved successfully.");
            ResetUserFields();
        } catch (SQLException e) {
            System.out.println(e);            
            if (e.toString().contains("Duplicate entry"))
                JOptionPane.showMessageDialog(this, "The user you tried to save already exists");
            else
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
            //String team1 = jCBTeam.getSelectedItem().toString();
            String team1 = jCBTeam_user.getSelectedItem().toString();
            String cu1 = jCBCustomerUnit.getSelectedItem().toString();
            String cat1 = jTFCATNum.getText();
            String js1 = jCBJobStage.getSelectedItem().toString();            
            String acttype1 = ActTypes[jCBJobStage.getSelectedIndex()];
            
            String manager_name = jCBLineManager.getSelectedItem().toString();
            String name = "", lmsignum = "", organization = "";
            
            for (int i = 0; i < users_info.size(); i += 13){
                name = users_info.get(i + 1) + " " + users_info.get(i + 2);
                if (manager_name.equals(name))
                    lmsignum = users_info.get(i);
                if (lmsignum.equals(users_info.get(i + 6)))
                    organization = users_info.get(i + 5);
            }

            HashMap<String, String> CU_list = new HashMap<String, String>();

            // Add keys and values (Team name, Prefix)
            CU_list.put("COP", "C_");
            CU_list.put("VSS", "V_");
            CU_list.put("PSS", "P_");
            CU_list.put("Scoping", "SCP_");
            CU_list.put("Sourcing", "");

            if (!team1.equals("SDU") && !cu1.equals("")) {
                cu1 = CU_list.get(team1) + cu1;
            }

            if (!js1.equals("N/A")) {
                js1 = "Job Stage " + js1;
            }

            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("UPDATE users SET "
                    + "Customer_Unit = ?, Team = ?, Organization = ?, "
                    + "Line_Manager = ?, Access = ?, Supporting_Team = ?, "
                    + "Supporting_CU = ?, Job_Stage = ?, Act_type = ?, "
                    + "CATS_Number = ? "
                    + "WHERE Signum = ?;");
            preparedStatement.setObject(1, cu1);
            preparedStatement.setObject(2, team1);
            preparedStatement.setObject(3, organization);
            preparedStatement.setObject(4, lmsignum);
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

            JOptionPane.showMessageDialog(this, "User updated successfully.");
            ResetUserFields();
        } catch (SQLException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(this, "Something went wrong, please try again later.");
        }
    }

    private void DeleteUserDB() {
        Connection connection;
        PreparedStatement preparedStatement;
        String name = jCBSearchUser.getSelectedItem().toString();
        String[] lastname = name.split(",");
        String lastname1 = lastname[0];
        try {
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("DELETE FROM users "
                    + "WHERE Last_Name = ?");
            preparedStatement.setObject(1, lastname1);
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
            String type = jCBTaskType.getSelectedItem().toString();
            String team = jCBTaskTeam.getSelectedItem().toString();
            String cu = jCBTaskCU.getSelectedItem().toString();
            String spn = jCBServicePN.getSelectedItem().toString();
            String deliv = jCBDeliverable.getSelectedItem().toString();
            String psd = jCBProjectSuppDom.getSelectedItem().toString();
            String loe = jTFLOE.getText();
            String billable = "Billable";
            int index = jCBTaskType.getSelectedIndex();
            if (index == 0) {
                billable = "Not Billable";
            }

            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("SELECT Task_ID FROM tasks "// COUNT(Task_ID) AS counter,
                    + "WHERE Task_ID LIKE '" + type + "%' AND Service_Package_Name LIKE '%" + spn + "%' ORDER BY Task_ID DESC LIMIT 1;");
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
            if (loe.equals("")) {
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
            String task = jCBTaskSearch.getSelectedItem().toString();
            String team = jCBTaskTeam.getSelectedItem().toString();
            String spn = jCBServicePN.getSelectedItem().toString();
            String deliv = jCBDeliverable.getSelectedItem().toString();
            String psd = jCBProjectSuppDom.getSelectedItem().toString();
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
            String teamsearch = jCBTeamTaskSearch.getSelectedItem().toString();
            String cusearch = jCBCUTaskSearch.getSelectedItem().toString();
            String tasksearch = jCBTaskSearch.getSelectedItem().toString();

            for (int i = 0; i < tasks_info.size(); i += 9 ){
                if (tasks_info.get(i + 2).equals(teamsearch) && (tasks_info.get(i+3).equals(cusearch) && (tasks_info.get(i+1).equals(tasksearch)))){
                    task_id = tasks_info.get(i);
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
                //jTFTaskName.setText(taskname);
                //jCBTaskTeam.setSelectedItem(team);
                //jCBTaskType.setSelectedItem(task_type);
                //jCBTaskCU.setSelectedItem(cu);
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
            String activityCode = jCBNetActCode.getSelectedItem().toString();
            String responsible = jTFResponsible.getText();
            String region = jCBNetRegion.getItemAt(jCBNetRegion.getSelectedIndex());
            String market = jCBNetMarket.getItemAt(jCBNetMarket.getSelectedIndex());
            String customer = jCBNetCustomer.getItemAt(jCBNetCustomer.getSelectedIndex());
            String subnetwork = jTFSubnetwork.getText();
            String team = jCBNetTeam.getItemAt(jCBNetTeam.getSelectedIndex());
            String technology = jCBNetTech.getSelectedItem().toString();
            String project = jCBProjectName.getSelectedItem().toString();
            System.out.println("Selection : " + activityCode);
            
            if (activityCode == null || activityCode.equals("") )             
                JOptionPane.showMessageDialog(this, "Activity code is empty!");
            else if (technology == null || technology.equals(""))
                JOptionPane.showMessageDialog(this, "Technology is empty!");
            else if (project.equals("") || project.equals(""))
                JOptionPane.showMessageDialog(this, "Project Name is empty!");
            
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("INSERT INTO networks (PD, Network, Activity_code, Region, Market, Customer, Responsible, Subnetwork, Team, Technology, Project) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,?);");
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
            preparedStatement.setObject(11, project);
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
                System.out.println("Customer found  " + customer);
                responsible = resultSet.getString("Responsible");
                subnetwork = resultSet.getString("Subnetwork");
                team = resultSet.getString("Team");
                System.out.println("Team found  " + team);
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
            String act1 = jCBNetActCode.getSelectedItem().toString();
            String resp1 = jTFResponsible.getText();
            String subnet1 = jTFSubnetwork.getText();
            String cu1 = jCBNetCustomer.getSelectedItem().toString();
            String reg1 = jCBNetRegion.getSelectedItem().toString();
            String mark1 = jCBNetMarket.getSelectedItem().toString();
            String net1 = jTFNetwork.getText();
            String team1 = jCBTeam_editnet.getSelectedItem().toString();
            String technology = jCBNetTech.getSelectedItem().toString();
            String project = jCBProjectName.getSelectedItem().toString();  
                       
            connection = SQL_connection.getConnection();
            preparedStatement = connection.prepareStatement("UPDATE networks SET Activity_code = ?, Responsible = ?, "
                    + "Subnetwork = ?,  Customer = ?, Region = ?, Market = ?, Technology = ?, Project = ? "
                    + "WHERE (Network = ?) AND (Team = ?);");
            preparedStatement.setObject(1, act1);
            preparedStatement.setObject(2, resp1);
            preparedStatement.setObject(3, subnet1);
            preparedStatement.setObject(4, cu1);
            preparedStatement.setObject(5, reg1);
            preparedStatement.setObject(6, mark1);
            preparedStatement.setObject(7, technology);
            preparedStatement.setObject(8, project);
            preparedStatement.setObject(9, net1);
            preparedStatement.setObject(10, team1);
            
            
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
                && !jTFResponsible.getText().equals("") && !jTFSubnetwork.getText().equals("") && !jCBProjectName.getSelectedItem().toString().equals("") &&
                !jCBNetActCode.getSelectedItem().toString().equals("") && !jCBNetTech.getSelectedItem().toString().equals("")) {
            flag = true;
        }
        return flag;
    }

    private void ResetUserFields() {
        jTFSignum.setText("");
        jTFName.setText("");
        jTFLastName.setText("");
        //jCBOrganization.setSelectedIndex(0);
//        jCBTeam.setSelectedIndex(0);
//        jCBCustomerUnit.setSelectedIndex(0);
//        jCBLineManager.setSelectedIndex(0);
//        jCBSupportedCU.setSelectedIndex(0);
        //jTFSignumEdit.setText("");
        jTFSupTeam.setText("N/A");
        jTFSupCU.setText("N/A");
        jTFCATNum.setText("");
    }

    private void ResetNetworkFields() {
        jTFPD.setText("");
        jTFNetwork.setText("");
        //jCBNetActCode.setSelectedIndex(0);
        jTFResponsible.setText("");
        jTFSubnetwork.setText("");
        //jCBNetTeam.setSelectedIndex(0);
        //jCBNetCustomer.setSelectedIndex(0);
        //jCBNetRegion.setSelectedIndex(0);
        //jCBNetMarket.setSelectedIndex(0);
        //jCBNetTech.setSelectedIndex(0);
        //jCBNetTeamSearch.setSelectedIndex(0);
    }

    private void ResetTaskFields() {
        //jCBTeamTaskSearch.setSelectedIndex(0);
        //jCBTaskType.setSelectedIndex(0);
        jTFTaskName.setText("");
        //jCBTaskTeam.setSelectedIndex(0);
//        jCBServicePN.setSelectedIndex(0);
//        jCBDeliverable.setSelectedIndex(0);
//        jCBProjectSuppDom.setSelectedIndex(0);
        jTFLOE.setText("");
    }
     private void hide_task_comboboxes() {
        jCBTeamTaskSearch.setVisible(false);
        jCBCUTaskSearch.setVisible(false);
        jCBTaskSearch.setVisible(false);
        jCBTaskTeam.setEnabled(false);
        jCBTaskCU.setEnabled(false);
        jCBTaskType.setEnabled(false);
        jCBServicePN.setEnabled(false);
        jCBDeliverable.setEnabled(false);
        jCBProjectSuppDom.setEnabled(false);
        jCBTeam_editTask.setVisible(false);        
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
        String name_month = "";
        String team1 = jCBMetricTeam.getSelectedItem().toString();
        String orgn1 = jCBOrgMetrics.getSelectedItem().toString();
        team1 = team1.toLowerCase();
        String yearFrom1 = jCBYearFrom.getSelectedItem().toString();
        String yearTo1 = jCBYearTo.getSelectedItem().toString();
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
            String weekFrom = jCBFrom.getSelectedItem().toString();
            String weekTo = jCBTo.getSelectedItem().toString();

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
            String monthFrom = jCBFrom.getSelectedItem().toString();
            String monthTo = jCBTo.getSelectedItem().toString();
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
            int monthFrom = Integer.parseInt(jCBFrom.getSelectedItem().toString()) - 1;
            int monthTo = Integer.parseInt(jCBTo.getSelectedItem().toString()) - 1;
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
            parametros = parametros + "MONTH(Work_date), ";
            columnas.add("Month");
            orden = "MONTH(Work_date), " + orden;
            week = "WHERE Work_date BETWEEN '" + date1 + "' AND '" + date2 + "'";
        }

        if (orgn1 != "All") {
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
        
        HashMap<String, String> months_dic = new HashMap<>();

        months_dic.put("1", "January");
        months_dic.put("2", "February");
        months_dic.put("3", "March");
        months_dic.put("4", "April");
        months_dic.put("5", "May");
        months_dic.put("6", "June");
        months_dic.put("7", "July");
        months_dic.put("8", "August");
        months_dic.put("9", "September");
        months_dic.put("10", "October");
        months_dic.put("11", "November");
        months_dic.put("12", "December");

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
                        + week + organization + name + " GROUP BY " + orden + " ORDER BY " + orden + " ;";
            } else {
                query = "SELECT " + parametros + "SUM(Logged_Time) FROM metrics_" + team1 + " " + week + organization + name + " GROUP BY " + orden + " ORDER BY " + orden + ";";
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
                                    valores[j - 1] = months_dic.get(rs.getString(j));
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
            Logger.getLogger(Time_Management.class.getName()).log(Level.SEVERE, null, ex);
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
        String team1 = jCBMetricTeam.getSelectedItem().toString();
        String orgn1 = jCBOrgMetrics.getSelectedItem().toString();
        String yearFrom1 = jCBYearFrom.getSelectedItem().toString();
        String yearTo1 = jCBYearTo.getSelectedItem().toString();
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
            String weekFrom = jCBFrom.getSelectedItem().toString();
            String weekTo = jCBTo.getSelectedItem().toString();

            if (!jRBTo.isSelected()) {
                week = "WHERE Week = " + weekFrom + " AND YEAR(Work_date) = " + yearFrom1;
                fileName = fileName + " Week " + weekFrom;
            } else {
                week = "WHERE Week BETWEEN " + weekFrom + " AND " + weekTo;
                fileName = fileName + " From Week " + weekFrom + " TO " + weekTo;
            }
        }
        if (jRBMonth.isSelected()) {
            String date1, date2;
            String monthFrom = jCBFrom.getSelectedItem().toString();
            String monthTo = jCBTo.getSelectedItem().toString();
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
            date1 = yearFrom1 + "-" + quarterFrom[monthFrom] + "-01";
            if (jRBTo.isSelected()) {
                date2 = yearTo1 + "-" + quarterTo[monthTo] + "-" + daysQuarter[monthTo];
                fileName = fileName + " From Q" + monthFrom + " To Q" + monthTo;
            } else {
                date2 = yearTo1 + "-" + quarterTo[monthFrom] + "-" + daysQuarter[monthFrom];
                fileName = fileName + " From Q" + monthFrom;
            }
            parametros = parametros + "Work_date, ";
            orden = "MONTH(Work_date), " + orden;
            week = "WHERE Work_date BETWEEN '" + date1 + "' AND '" + date2 + "'";
        }

        if (!orgn1.equals("All")) {
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
                        + "FROM metrics_fms UNION ALL "
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
                        + "FROM metrics_scoping UNION ALL "
                        + "SELECT Region, Organization, Signum, Name, Customer_Unit, "
                        + "Requestor, Task_ID, Task, Network, Subnetwork, Activity_Code, "
                        + "SAP_Billing, Work_Date, Logged_Time, Week, Market, Technology, "
                        + "FTR, On_Time, Failed_FTR_Category, Failed_On_Time, Num_Requests, Comments "
                        + "FROM metrics_pss) METRICAS " + week + organization + name1 + ";";
            } else {
                query = "SELECT Region, Organization, Signum, Name, Customer_Unit, "
                        + "Requestor, Task_ID, Task, Network, Subnetwork, Activity_Code, "
                        + "SAP_Billing, Work_Date, Logged_Time, Week, Market, Technology, "
                        + "FTR, On_Time, Failed_FTR_Category, Failed_On_Time, Num_Requests, "
                        + "Comments FROM metrics_" + team1 + " " + week + organization + name1 + " ;";
            }
            preparedStatement = connection.prepareStatement(query);
            System.out.println("Query: " + preparedStatement);
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                JOptionPane.showMessageDialog(this, "Metrics not found! Please try again later...");
            } else {
                do {
                    for (int j = 1; j < 24; j++) {
                          valores[j - 1] = resultSet.getString(j);
                        
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
            java.util.logging.Logger.getLogger(Time_Management.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Time_Management.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Time_Management.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Time_Management.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Time_Management().setVisible(true);
                } catch (ParseException ex) {
                    Logger.getLogger(Time_Management.class.getName()).log(Level.SEVERE, null, ex);
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
    private javax.swing.JButton jBDeleteDeliverable;
    private javax.swing.JButton jBDeleteTask;
    private javax.swing.JButton jBDeleteTask1;
    private javax.swing.JButton jBDeleteTask5;
    private javax.swing.JButton jBExportTaskCSV;
    private javax.swing.JButton jBExportUserCSV;
    private javax.swing.JButton jBGenerateCSV;
    private javax.swing.JButton jBNetDelete;
    private javax.swing.JButton jBNetSearch;
    private javax.swing.JButton jBNetTableCSV;
    private javax.swing.JButton jBNetworkSearch;
    private javax.swing.JButton jBSaveDeliverable;
    private javax.swing.JButton jBSaveMrkt;
    private javax.swing.JButton jBSaveNet;
    private javax.swing.JButton jBSaveNewUser;
    private javax.swing.JButton jBSaveTask;
    private javax.swing.JButton jBSaveTask2;
    private javax.swing.JButton jBSaveTask5;
    private javax.swing.JButton jBSearch;
    private javax.swing.JButton jBSearchDeliverable;
    private javax.swing.JButton jBSearchMrkt;
    private javax.swing.JButton jBSearchTask;
    private javax.swing.JButton jBSearchTask1;
    private javax.swing.JButton jBSearchTask5;
    private javax.swing.JButton jBShowMetrics;
    private javax.swing.JButton jBShowPreview;
    private javax.swing.JButton jB_Export_Compliance;
    private javax.swing.JComboBox<String> jCBAccess;
    private javax.swing.JComboBox<String> jCBAction;
    private javax.swing.JComboBox<String> jCBBillable_packageadd;
    private javax.swing.JComboBox<String> jCBCUMrkt;
    private javax.swing.JComboBox<String> jCBCUPackageAdd2;
    private javax.swing.JComboBox<String> jCBCUPackageSearch;
    private javax.swing.JComboBox<String> jCBCUSearch;
    private javax.swing.JComboBox<String> jCBCUSearchDeliverable;
    private javax.swing.JComboBox<String> jCBCUTaskSearch;
    private javax.swing.JComboBox<String> jCBCU_adddeliverable;
    private javax.swing.JComboBox<String> jCBCU_packageadd;
    private javax.swing.JComboBox<String> jCBCustomerUnit;
    private javax.swing.JButton jCBDeleteMrkt;
    private javax.swing.JComboBox<String> jCBDeliverable;
    private javax.swing.JComboBox<String> jCBDeliverableSearch1;
    private javax.swing.JComboBox<String> jCBDeliverableSearch2;
    private javax.swing.JComboBox<String> jCBDeliverable_add;
    private javax.swing.JComboBox<String> jCBDeliverables2;
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
    javax.swing.JComboBox<String> jCBNetActCode;
    private javax.swing.JComboBox<String> jCBNetAction;
    private javax.swing.JComboBox<String> jCBNetCustomer;
    private javax.swing.JComboBox<String> jCBNetMarket;
    private javax.swing.JComboBox<String> jCBNetRegion;
    private javax.swing.JComboBox<String> jCBNetSearch;
    private javax.swing.JComboBox<String> jCBNetTeam;
    private javax.swing.JComboBox<String> jCBNetTeamSearch;
    private javax.swing.JComboBox<String> jCBNetTech;
    private javax.swing.JComboBox<String> jCBOrgMetrics;
    private javax.swing.JComboBox<String> jCBPackageAction;
    private javax.swing.JComboBox<String> jCBPackageSearch2;
    private javax.swing.JComboBox<String> jCBPackage_CU2;
    private javax.swing.JComboBox<String> jCBPackage_billable3;
    private javax.swing.JComboBox<String> jCBPackage_deliverablesearch;
    private javax.swing.JComboBox<String> jCBProjectName;
    private javax.swing.JComboBox<String> jCBProjectSuppDom;
    private javax.swing.JComboBox<String> jCBRegionMrkt;
    private javax.swing.JComboBox<String> jCBSearchUser;
    private javax.swing.JComboBox<String> jCBServicePN;
    private javax.swing.JComboBox<String> jCBServicePackageAdd2;
    private javax.swing.JComboBox<String> jCBServicePackageSearch;
    private javax.swing.JComboBox<String> jCBServicePackage_add;
    private javax.swing.JComboBox<String> jCBServicePackage_adddeliverable;
    private javax.swing.JComboBox<String> jCBSupportedCU;
    private javax.swing.JComboBox<String> jCBSupportedTeam;
    private javax.swing.JComboBox<String> jCBTaskCU;
    private javax.swing.JComboBox<String> jCBTaskEdit;
    private javax.swing.JComboBox<String> jCBTaskSearch;
    private javax.swing.JComboBox<String> jCBTaskTeam;
    private javax.swing.JComboBox<String> jCBTaskType;
    private javax.swing.JComboBox<String> jCBTeam;
    private javax.swing.JComboBox<String> jCBTeamMrkt;
    private javax.swing.JComboBox<String> jCBTeamPackageSearch;
    private javax.swing.JComboBox<String> jCBTeamSearchDeliverable;
    private javax.swing.JComboBox<String> jCBTeamSearchPackage2;
    private javax.swing.JComboBox<String> jCBTeamTaskSearch;
    private javax.swing.JComboBox<String> jCBTeam_adddeliverable;
    private javax.swing.JComboBox<String> jCBTeam_addpackage;
    private javax.swing.JComboBox<String> jCBTeam_editTask;
    private javax.swing.JComboBox<String> jCBTeam_editdeliverable;
    private javax.swing.JComboBox<String> jCBTeam_editmarket;
    private javax.swing.JComboBox<String> jCBTeam_editnet;
    private javax.swing.JComboBox<String> jCBTeam_editpackage;
    private javax.swing.JComboBox<String> jCBTeam_package2;
    private javax.swing.JComboBox<String> jCBTeam_user;
    private javax.swing.JComboBox<String> jCBTo;
    private javax.swing.JComboBox<String> jCBUser;
    private javax.swing.JComboBox<String> jCBYearFrom;
    private javax.swing.JComboBox<String> jCBYearTo;
    private javax.swing.JDialog jDLoading;
    private javax.swing.JFrame jFrame_Compliance;
    private javax.swing.JLabel jLAccess;
    private javax.swing.JLabel jLBillable1;
    private javax.swing.JLabel jLBillable2;
    private javax.swing.JLabel jLBillable3;
    private javax.swing.JLabel jLCUSupported;
    private javax.swing.JLabel jLChoose;
    private javax.swing.JLabel jLChoose1;
    private javax.swing.JLabel jLDeliverables2;
    private javax.swing.JLabel jLFileName;
    private javax.swing.JLabel jLFrom;
    private javax.swing.JLabel jLLoading;
    private javax.swing.JLabel jLMetricOrg;
    private javax.swing.JLabel jLMetricTeams;
    private javax.swing.JLabel jLNetAction;
    private javax.swing.JLabel jLServicePackage2;
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
    private javax.swing.JLabel jLTaskSearch10;
    private javax.swing.JLabel jLTaskSearch11;
    private javax.swing.JLabel jLTaskSearch2;
    private javax.swing.JLabel jLTaskSearch3;
    private javax.swing.JLabel jLTaskSearch6;
    private javax.swing.JLabel jLTaskType;
    private javax.swing.JLabel jLTaskType1;
    private javax.swing.JLabel jLTaskType2;
    private javax.swing.JLabel jLTeamSupp;
    private javax.swing.JLabel jLWeek_Compliance;
    private javax.swing.JLabel jLYear;
    private javax.swing.JLabel jLYearTo;
    private javax.swing.JLabel jL_select_org;
    private javax.swing.JLabel jL_select_team;
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_Filters;
    private javax.swing.JLabel jLpackage3;
    private javax.swing.JLabel jLpackage4;
    private javax.swing.JLabel jLpackage5;
    private javax.swing.JLabel jLpackage6;
    private javax.swing.JMenuItem jMComp_people;
    private javax.swing.JMenuItem jMEditMarkets;
    private javax.swing.JMenuItem jMEditNetworks;
    private javax.swing.JMenuItem jMEditServicePackage;
    private javax.swing.JMenuItem jMEditTask;
    private javax.swing.JMenuItem jMEditUsers;
    private javax.swing.JMenuItem jMICOP;
    private javax.swing.JMenuItem jMIFMS;
    private javax.swing.JMenuItem jMIPSS;
    private javax.swing.JMenuItem jMIScoping;
    private javax.swing.JMenuItem jMISourcing;
    private javax.swing.JMenuItem jMIVSS;
    private javax.swing.JMenuItem jMReview;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuCompliance;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenu jMenuTeams;
    private javax.swing.JMenu jMenuView;
    private javax.swing.JPanel jPAddDeliverable;
    private javax.swing.JPanel jPAddPackage1;
    private javax.swing.JPanel jPAddPackage2;
    private javax.swing.JPanel jPEdit;
    private javax.swing.JPanel jPEditServicePackage;
    private javax.swing.JPanel jPEditServicePackage1;
    private javax.swing.JPanel jPEditTask;
    private javax.swing.JPanel jPMarket;
    private javax.swing.JPanel jPNetEdit;
    private javax.swing.JPanel jPNetSearch;
    private javax.swing.JPanel jPNetworks;
    private javax.swing.JPanel jPSearch;
    private javax.swing.JPanel jPSearchDeliverable;
    private javax.swing.JPanel jPSearchMrkt;
    private javax.swing.JPanel jPSearchPackage1;
    private javax.swing.JPanel jPSearchPackage2;
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
    private javax.swing.JComboBox<String> jcbWeek_Compliance;
    private javax.swing.JComboBox<String> jcb_Org_compliance;
    private javax.swing.JComboBox<String> jcb_Team_compliance;
    // End of variables declaration//GEN-END:variables
}
