package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import controller.BillingController;
import model.Product;
import model.BilledItem;

public class BillingView extends JFrame {
    private BillingController controller;
    
    // Product table components
    private JTable productTable;
    private DefaultTableModel productTableModel;
    
    // Bill table components
    private JTable billTable;
    private DefaultTableModel billTableModel;
    
    // Input components
    private JTextField quantityField;
    private JTextField discountField;
    
    // Buttons
    private JButton addToBillButton;
    private JButton generateBillButton;
    private JButton backButton;
    private JButton refreshButton;
    
    // Labels for totals
    private JLabel totalLabel;
    private JLabel discountLabel;
    private JLabel netTotalLabel;
    
    // Data
    private List<BilledItem> currentBill;
    private double currentTotal;
    private double currentDiscount;
    private double currentNetTotal;

    public BillingView() {
        controller = new BillingController();
        currentBill = new ArrayList<>();
        initializeComponents();
        setupLayout();
        attachListeners();
        configureFrame();
        loadProductData();
        updateTotals();
    }

    private void initializeComponents() {
        // Product table
        String[] productColumns = {"ID", "Name", "Category", "Price", "Stock"};
        productTableModel = new DefaultTableModel(productColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(productTableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Bill table
        String[] billColumns = {"ID", "Name", "Price", "Qty", "Subtotal"};
        billTableModel = new DefaultTableModel(billColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        billTable = new JTable(billTableModel);
        billTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Input fields
        quantityField = new JTextField(10);
        discountField = new JTextField(10);
        
        // Buttons
        addToBillButton = new JButton("Add to Bill");
        generateBillButton = new JButton("Generate Bill");
        backButton = new JButton("Back to Dashboard");
        refreshButton = new JButton("Refresh Products");
        
        // Labels
        totalLabel = new JLabel("Total: $0.00");
        discountLabel = new JLabel("Discount: 0%");
        netTotalLabel = new JLabel("Net Total: $0.00");
        
        // Style labels
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        totalLabel.setFont(labelFont);
        discountLabel.setFont(labelFont);
        netTotalLabel.setFont(labelFont);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Billing System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        
        // Left panel - Products
        JPanel leftPanel = new JPanel(new BorderLayout());
        
        // Product table
        JScrollPane productScrollPane = new JScrollPane(productTable);
        productScrollPane.setPreferredSize(new Dimension(400, 200));
        leftPanel.add(productScrollPane, BorderLayout.CENTER);
        
        // Product controls
        JPanel productControlPanel = new JPanel(new FlowLayout());
        productControlPanel.add(new JLabel("Quantity:"));
        productControlPanel.add(quantityField);
        productControlPanel.add(addToBillButton);
        productControlPanel.add(refreshButton);
        leftPanel.add(productControlPanel, BorderLayout.SOUTH);
        
        // Right panel - Bill
        JPanel rightPanel = new JPanel(new BorderLayout());
        
        // Bill table
        JScrollPane billScrollPane = new JScrollPane(billTable);
        billScrollPane.setPreferredSize(new Dimension(400, 200));
        rightPanel.add(billScrollPane, BorderLayout.CENTER);
        
        // Bill controls
        JPanel billControlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Discount input
        gbc.gridx = 0;
        gbc.gridy = 0;
        billControlPanel.add(new JLabel("Discount %:"), gbc);
        gbc.gridx = 1;
        billControlPanel.add(discountField, gbc);
        
        // Totals
        gbc.gridx = 0;
        gbc.gridy = 1;
        billControlPanel.add(totalLabel, gbc);
        gbc.gridx = 1;
        billControlPanel.add(discountLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        billControlPanel.add(netTotalLabel, gbc);
        
        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        billControlPanel.add(generateBillButton, gbc);
        gbc.gridx = 1;
        billControlPanel.add(backButton, gbc);
        
        rightPanel.add(billControlPanel, BorderLayout.SOUTH);
        
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        
        add(mainPanel, BorderLayout.CENTER);
    }

    private void attachListeners() {
        addToBillButton.addActionListener(e -> handleAddToBill());
        generateBillButton.addActionListener(e -> handleGenerateBill());
        backButton.addActionListener(e -> handleBack());
        refreshButton.addActionListener(e -> handleRefresh());
        
        // Auto-calculate totals when discount changes
        discountField.addActionListener(e -> updateTotals());
        
        // Update totals when discount field loses focus
        discountField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                updateTotals();
            }
        });
    }

    private void configureFrame() {
        setTitle("Billing System - Supermarket Billing System");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(1000, 500);
        setLocationRelativeTo(null);
    }

    private void loadProductData() {
        controller.loadProducts();
        productTableModel.setRowCount(0);
        
        for (Product product : controller.getAllProducts()) {
            if (product.getQuantity() > 0) { // Only show products with stock
                productTableModel.addRow(new Object[]{
                    product.getId(),
                    product.getName(),
                    product.getCategory(),
                    String.format("%.2f", product.getPrice()),
                    product.getQuantity()
                });
            }
        }
    }

    private void handleAddToBill() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String quantityText = quantityField.getText().trim();
        if (quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter quantity!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String productId = productTableModel.getValueAt(selectedRow, 0).toString();
            String productName = productTableModel.getValueAt(selectedRow, 1).toString();
            double price = Double.parseDouble(productTableModel.getValueAt(selectedRow, 3).toString());
            int availableStock = Integer.parseInt(productTableModel.getValueAt(selectedRow, 4).toString());
            
            if (quantity > availableStock) {
                JOptionPane.showMessageDialog(this, "Insufficient stock! Available: " + availableStock, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if item already exists in bill
            BilledItem existingItem = null;
            for (BilledItem item : currentBill) {
                if (item.getId().equals(productId)) {
                    existingItem = item;
                    break;
                }
            }
            
            if (existingItem != null) {
                int newQuantity = existingItem.getQuantity() + quantity;
                if (newQuantity > availableStock) {
                    JOptionPane.showMessageDialog(this, "Total quantity exceeds available stock!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                existingItem.setQuantity(newQuantity);
            } else {
                BilledItem newItem = new BilledItem(productId, productName, price, quantity);
                currentBill.add(newItem);
            }
            
            updateBillTable();
            updateTotals();
            quantityField.setText("");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBillTable() {
        billTableModel.setRowCount(0);
        currentTotal = 0;
        
        for (BilledItem item : currentBill) {
            billTableModel.addRow(new Object[]{
                item.getId(),
                item.getName(),
                String.format("%.2f", item.getPrice()),
                item.getQuantity(),
                String.format("%.2f", item.getSubtotal())
            });
            currentTotal += item.getSubtotal();
        }
    }

    private void updateTotals() {
        try {
            currentDiscount = Double.parseDouble(discountField.getText().trim());
            if (currentDiscount < 0) currentDiscount = 0;
            if (currentDiscount > 100) currentDiscount = 100;
        } catch (NumberFormatException e) {
            currentDiscount = 0;
        }
        
        double discountAmount = currentTotal * (currentDiscount / 100);
        currentNetTotal = currentTotal - discountAmount;
        
        totalLabel.setText(String.format("Total: $%.2f", currentTotal));
        discountLabel.setText(String.format("Discount: %.0f%%", currentDiscount));
        netTotalLabel.setText(String.format("Net Total: $%.2f", currentNetTotal));
    }

    private void handleGenerateBill() {
        if (currentBill.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bill is empty! Add items first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
                this,
                String.format("Generate bill for $%.2f?", currentNetTotal),
                "Confirm Bill Generation",
                JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Update stock for all items
                for (BilledItem item : currentBill) {
                    controller.updateProductStock(item.getId(), item.getQuantity());
                }
                
                // Save bill file
                controller.saveBill(currentBill, currentDiscount, currentTotal, currentNetTotal);
                
                // Record sale
                controller.recordSale(currentTotal, currentDiscount, currentNetTotal);
                
                JOptionPane.showMessageDialog(this, "Bill generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Clear bill for next transaction
                currentBill.clear();
                updateBillTable();
                updateTotals();
                discountField.setText("");
                loadProductData(); // Refresh product stock
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error generating bill: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleRefresh() {
        loadProductData();
        JOptionPane.showMessageDialog(this, "Product data refreshed!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleBack() {
        dispose();
        DashboardView dashboard = new DashboardView();
        dashboard.showWindow();
    }

    public void showWindow() {
        setVisible(true);
    }
}
