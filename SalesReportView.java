package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import controller.SalesController;
import model.Sale;

public class SalesReportView extends JFrame {
    private SalesController controller;
    
    // Date input components
    private JTextField fromDateField;
    private JTextField toDateField;
    
    // Buttons
    private JButton filterButton;
    private JButton refreshButton;
    private JButton exportButton;
    private JButton backButton;
    
    // Table components
    private JTable salesTable;
    private DefaultTableModel salesTableModel;
    
    // Summary labels
    private JLabel salesCountLabel;
    private JLabel rangeTotalLabel;
    private JLabel allTimeTotalLabel;
    
    // Data
    private List<Sale> currentSales;
    private LocalDate currentFromDate;
    private LocalDate currentToDate;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public SalesReportView() {
        controller = new SalesController();
        initializeComponents();
        setupLayout();
        attachListeners();
        configureFrame();
        loadInitialData();
    }

    private void initializeComponents() {
        // Date input fields
        fromDateField = new JTextField(12);
        toDateField = new JTextField(12);
        
        // Buttons
        filterButton = new JButton("Filter");
        refreshButton = new JButton("Refresh");
        exportButton = new JButton("Export Report");
        backButton = new JButton("Back to Dashboard");
        
        // Table setup
        String[] columns = {"Date", "Time", "Total", "Discount", "Net Total"};
        salesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        salesTable = new JTable(salesTableModel);
        salesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Summary labels
        salesCountLabel = new JLabel("Displayed Sales Count: 0");
        rangeTotalLabel = new JLabel("Total for range: ₹0.00");
        allTimeTotalLabel = new JLabel("Cumulative Total (all time): ₹0.00");
        
        // Style labels
        Font labelFont = new Font("Arial", Font.BOLD, 12);
        salesCountLabel.setFont(labelFont);
        rangeTotalLabel.setFont(labelFont);
        allTimeTotalLabel.setFont(labelFont);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Daily Sales Tracking & Reports");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Control panel
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Date range inputs
        gbc.gridx = 0;
        gbc.gridy = 0;
        controlPanel.add(new JLabel("From Date (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        controlPanel.add(fromDateField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        controlPanel.add(new JLabel("To Date (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        controlPanel.add(toDateField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(filterButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(exportButton);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        controlPanel.add(buttonPanel, gbc);
        
        // Back button
        gbc.gridy = 3;
        controlPanel.add(backButton, gbc);
        
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        
        // Table panel
        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3));
        summaryPanel.add(salesCountLabel);
        summaryPanel.add(rangeTotalLabel);
        summaryPanel.add(allTimeTotalLabel);
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        
        mainPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }

    private void attachListeners() {
        filterButton.addActionListener(e -> handleFilter());
        refreshButton.addActionListener(e -> handleRefresh());
        exportButton.addActionListener(e -> handleExport());
        backButton.addActionListener(e -> handleBack());
        
        // Enter key support for date fields
        fromDateField.addActionListener(e -> handleFilter());
        toDateField.addActionListener(e -> handleFilter());
    }

    private void configureFrame() {
        setTitle("Sales Report - Supermarket Billing System");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
    }

    private void loadInitialData() {
        // Load last 7 days by default
        currentSales = controller.getLastNDays(7);
        if (!currentSales.isEmpty()) {
            currentFromDate = currentSales.get(currentSales.size() - 1).getDate();
            currentToDate = currentSales.get(0).getDate();
            
            fromDateField.setText(currentFromDate.format(DATE_FORMATTER));
            toDateField.setText(currentToDate.format(DATE_FORMATTER));
        } else {
            // If no sales, set to current date
            LocalDate today = LocalDate.now();
            fromDateField.setText(today.format(DATE_FORMATTER));
            toDateField.setText(today.format(DATE_FORMATTER));
            currentFromDate = today;
            currentToDate = today;
        }
        
        updateTable();
        updateSummary();
    }

    private void handleFilter() {
        try {
            String fromText = fromDateField.getText().trim();
            String toText = toDateField.getText().trim();
            
            if (fromText.isEmpty() || toText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both from and to dates!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            LocalDate from = controller.parseDate(fromText);
            LocalDate to = controller.parseDate(toText);
            
            if (from.isAfter(to)) {
                JOptionPane.showMessageDialog(this, "From date cannot be after To date!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            currentFromDate = from;
            currentToDate = to;
            currentSales = controller.filterSales(from, to);
            
            updateTable();
            updateSummary();
            
            if (currentSales.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No sales found for the selected date range.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date format! Please use yyyy-MM-dd format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRefresh() {
        loadInitialData();
        JOptionPane.showMessageDialog(this, "Data refreshed!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleExport() {
        if (currentSales.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "No sales data to export. Do you want to export an empty report?",
                    "Empty Report",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        try {
            controller.exportReport(currentSales, currentFromDate, currentToDate);
            
            String fileName = String.format("report_%s_to_%s.csv", 
                    currentFromDate.format(DATE_FORMATTER), 
                    currentToDate.format(DATE_FORMATTER));
            
            JOptionPane.showMessageDialog(this, 
                    String.format("Report exported successfully!\nFile: reports/%s\nRecords: %d", 
                            fileName, currentSales.size()), 
                    "Export Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error exporting report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleBack() {
        dispose();
        DashboardView dashboard = new DashboardView();
        dashboard.showWindow();
    }

    private void updateTable() {
        salesTableModel.setRowCount(0);
        
        for (Sale sale : currentSales) {
            salesTableModel.addRow(new Object[]{
                    sale.getDate().format(DATE_FORMATTER),
                    sale.getTime().toString(),
                    String.format("%.2f", sale.getTotal()),
                    String.format("%.2f", sale.getDiscount()),
                    String.format("%.2f", sale.getNetTotal())
            });
        }
    }

    private void updateSummary() {
        int count = currentSales.size();
        double rangeTotal = controller.computeCumulativeTotal(currentSales);
        double allTimeTotal = controller.computeAllTimeTotal();
        
        salesCountLabel.setText(String.format("Displayed Sales Count: %d", count));
        rangeTotalLabel.setText(String.format("Total for range: ₹%.2f", rangeTotal));
        allTimeTotalLabel.setText(String.format("Cumulative Total (all time): ₹%.2f", allTimeTotal));
    }

    public void showWindow() {
        setVisible(true);
    }
}
