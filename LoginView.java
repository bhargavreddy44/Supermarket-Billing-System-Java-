package view;

import javax.swing.*;
import java.awt.*;
import controller.LoginController;

public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;
    private LoginController controller;

    public LoginView() {
        controller = new LoginController(this);
        initializeComponents();
        setupLayout();
        attachListeners();
        configureFrame();
    }

    private void initializeComponents() {
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");
        exitButton = new JButton("Exit");
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Supermarket Billing System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, gbc);

        // Username Label
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Username:"), gbc);

        // Username Field
        gbc.gridx = 1;
        add(usernameField, gbc);

        // Password Label
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);

        // Password Field
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Buttons Panel
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);
        add(buttonPanel, gbc);

        // Default credentials hint
        gbc.gridy = 4;
        JLabel hintLabel = new JLabel("Default: admin / 1234");
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        hintLabel.setForeground(Color.GRAY);
        add(hintLabel, gbc);
    }

    private void attachListeners() {
        loginButton.addActionListener(e -> controller.handleLogin());

        exitButton.addActionListener(e -> System.exit(0));

        // Enter key to login
        passwordField.addActionListener(e -> controller.handleLogin());
    }

    private void configureFrame() {
        setTitle("Login - Supermarket Billing System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    // Getters
    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void showSuccessMessage() {
        JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showErrorMessage() {
        JOptionPane.showMessageDialog(this, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
        passwordField.setText("");
    }

    public void closeWindow() {
        dispose();
    }
}
