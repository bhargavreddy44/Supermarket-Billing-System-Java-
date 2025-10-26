package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import controller.ProductController;
import model.Product;

public class ProductView extends JFrame {
    private ProductController controller;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JTextField idField, nameField, categoryField, priceField, quantityField;
    private JButton addButton, updateButton, deleteButton, refreshButton, backButton;

    public ProductView() {
        controller = new ProductController();
        initializeComponents();
        setupLayout();
        attachListeners();
        configureFrame();
        loadTableData();
    }

    private void initializeComponents() {
        // Input fields
        idField = new JTextField(15);
        nameField = new JTextField(15);
        categoryField = new JTextField(15);
        priceField = new JTextField(15);
        quantityField = new JTextField(15);

        // Buttons
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        refreshButton = new JButton("Refresh");
        backButton = new JButton("Back to Dashboard");

        // Table setup
        String[] columnNames = {"ID", "Name", "Category", "Price", "Quantity"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.getTableHeader().setReorderingAllowed(false);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Title
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Product Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Table in center
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        add(scrollPane, BorderLayout.CENTER);

        // Input panel on the left
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Labels and fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(categoryField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(quantityField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(buttonPanel, gbc);

        // Back button
        gbc.gridy = 6;
        inputPanel.add(backButton, gbc);

        add(inputPanel, BorderLayout.WEST);
    }

    private void attachListeners() {
        addButton.addActionListener(e -> handleAdd());
        updateButton.addActionListener(e -> handleUpdate());
        deleteButton.addActionListener(e -> handleDelete());
        refreshButton.addActionListener(e -> refreshData());
        backButton.addActionListener(e -> handleBack());

        // When table row is selected, populate fields
        productTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow >= 0) {
                idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                categoryField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                priceField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                quantityField.setText(tableModel.getValueAt(selectedRow, 4).toString());
            }
        });
    }

    private void configureFrame() {
        setTitle("Product Management - Supermarket Billing System");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(1100, 450);
        setLocationRelativeTo(null);
    }

    private void loadTableData() {
        controller.loadProducts();
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Product product : controller.getAllProducts()) {
            tableModel.addRow(new Object[]{
                    product.getId(),
                    product.getName(),
                    product.getCategory(),
                    String.format("%.2f", product.getPrice()),
                    product.getQuantity()
            });
        }
    }

    private void refreshData() {
        controller.loadProducts();
        refreshTable();
        clearFields();
        JOptionPane.showMessageDialog(this, "Data refreshed!", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        categoryField.setText("");
        priceField.setText("");
        quantityField.setText("");
        productTable.clearSelection();
    }

    private boolean validateInput() {
        if (idField.getText().trim().isEmpty() ||
            nameField.getText().trim().isEmpty() ||
            categoryField.getText().trim().isEmpty() ||
            priceField.getText().trim().isEmpty() ||
            quantityField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            Double.parseDouble(priceField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price must be a valid number!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            Integer.parseInt(quantityField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity must be a valid integer!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void handleAdd() {
        if (!validateInput()) {
            return;
        }

        String id = idField.getText().trim();
        
        // Check if product with this ID already exists
        if (controller.getProductById(id) != null) {
            JOptionPane.showMessageDialog(this, "Product with ID " + id + " already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Product product = new Product(
                id,
                nameField.getText().trim(),
                categoryField.getText().trim(),
                Double.parseDouble(priceField.getText().trim()),
                Integer.parseInt(quantityField.getText().trim())
        );

        controller.addProduct(product);
        refreshTable();
        clearFields();
        JOptionPane.showMessageDialog(this, "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleUpdate() {
        if (!validateInput()) {
            return;
        }

        String id = idField.getText().trim();
        
        if (controller.getProductById(id) == null) {
            JOptionPane.showMessageDialog(this, "Product with ID " + id + " not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Product product = new Product(
                id,
                nameField.getText().trim(),
                categoryField.getText().trim(),
                Double.parseDouble(priceField.getText().trim()),
                Integer.parseInt(quantityField.getText().trim())
        );

        controller.updateProduct(product);
        refreshTable();
        clearFields();
        JOptionPane.showMessageDialog(this, "Product updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleDelete() {
        int selectedRow = productTable.getSelectedRow();
        
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String id = tableModel.getValueAt(selectedRow, 0).toString();
        
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete product: " + id + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteProduct(id);
            refreshTable();
            clearFields();
            JOptionPane.showMessageDialog(this, "Product deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
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
