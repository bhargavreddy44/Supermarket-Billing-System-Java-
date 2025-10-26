package view;

import javax.swing.*;
import java.awt.*;

public class DashboardView extends JFrame {
    private JButton manageProductsButton;
    private JButton billingButton;
    private JButton viewSalesButton;
    private JButton generateReportButton;
    private JButton settingsButton;
    private JButton logoutButton;

    public DashboardView() {
        initializeComponents();
        setupLayout();
        attachListeners();
        configureFrame();
    }

    private void initializeComponents() {
        manageProductsButton = new JButton("Manage Products");
        billingButton = new JButton("Billing");
        viewSalesButton = new JButton("View Daily Sales");
        generateReportButton = new JButton("Generate Sales Report");
        settingsButton = new JButton("Settings");
        logoutButton = new JButton("Logout");
        
        // Style buttons
        manageProductsButton.setPreferredSize(new Dimension(200, 60));
        manageProductsButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        billingButton.setPreferredSize(new Dimension(200, 60));
        billingButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        viewSalesButton.setPreferredSize(new Dimension(200, 60));
        viewSalesButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        generateReportButton.setPreferredSize(new Dimension(200, 60));
        generateReportButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        settingsButton.setPreferredSize(new Dimension(200, 60));
        settingsButton.setFont(new Font("Arial", Font.BOLD, 14));
        
        logoutButton.setPreferredSize(new Dimension(150, 40));
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Title Panel
        JPanel titlePanel = new JPanel();
        JLabel welcomeLabel = new JLabel("Supermarket Billing System");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(welcomeLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        // Welcome message
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel msgLabel = new JLabel("Welcome to your Dashboard!");
        msgLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        contentPanel.add(msgLabel, gbc);

        // Manage Products Button
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(manageProductsButton, gbc);

        // Billing Button
        gbc.gridy = 2;
        contentPanel.add(billingButton, gbc);

        // View Sales Button
        gbc.gridy = 3;
        contentPanel.add(viewSalesButton, gbc);

        // Generate Report Button
        gbc.gridy = 4;
        contentPanel.add(generateReportButton, gbc);

        // Settings Button
        gbc.gridy = 5;
        contentPanel.add(settingsButton, gbc);

        add(contentPanel, BorderLayout.CENTER);

        // Logout Button at bottom
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.add(logoutButton);
        add(logoutPanel, BorderLayout.SOUTH);
    }

    private void attachListeners() {
        manageProductsButton.addActionListener(e -> openProductView());
        billingButton.addActionListener(e -> openBillingView());
        viewSalesButton.addActionListener(e -> openSalesReportView());
        generateReportButton.addActionListener(e -> openSalesReportView());
        settingsButton.addActionListener(e -> openSettingsView());
        
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                handleLogout();
            }
        });
    }

    private void openProductView() {
        this.setVisible(false);
        ProductView productView = new ProductView();
        productView.showWindow();
    }

    private void openBillingView() {
        this.setVisible(false);
        BillingView billingView = new BillingView();
        billingView.showWindow();
    }

    private void openSalesReportView() {
        this.setVisible(false);
        SalesReportView salesReportView = new SalesReportView();
        salesReportView.showWindow();
    }

    private void openSettingsView() {
        this.setVisible(false);
        SettingsView settingsView = new SettingsView();
        settingsView.showWindow();
    }

    private void handleLogout() {
        dispose();
        LoginView loginView = new LoginView();
        loginView.setVisible(true);
    }

    private void configureFrame() {
        setTitle("Dashboard - Supermarket Billing System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    public void showWindow() {
        setVisible(true);
    }
}
