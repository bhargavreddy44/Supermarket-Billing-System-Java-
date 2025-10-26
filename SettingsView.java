package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import controller.BackupController;
import util.SimpleLogger;

public class SettingsView extends JFrame {
    private BackupController backupController;
    
    // UI Components
    private JCheckBox autoBackupCheckBox;
    private JSpinner intervalSpinner;
    private JButton runBackupButton;
    private JButton backButton;
    private JLabel statusLabel;
    private JLabel backupCountLabel;
    private JButton cleanupButton;
    private JSpinner cleanupSpinner;
    
    public SettingsView() {
        backupController = new BackupController();
        initializeComponents();
        setupLayout();
        attachListeners();
        configureFrame();
        updateStatus();
    }
    
    private void initializeComponents() {
        // Auto backup checkbox
        autoBackupCheckBox = new JCheckBox("Enable Automatic Backup");
        autoBackupCheckBox.setSelected(backupController.isAutoBackupEnabled());
        
        // Interval spinner (hours)
        SpinnerNumberModel intervalModel = new SpinnerNumberModel(24, 1, 168, 1); // 1 hour to 1 week
        intervalSpinner = new JSpinner(intervalModel);
        intervalSpinner.setValue((int) backupController.getBackupIntervalHours());
        
        // Buttons
        runBackupButton = new JButton("Run Backup Now");
        backButton = new JButton("Back to Dashboard");
        cleanupButton = new JButton("Cleanup Old Backups");
        
        // Cleanup spinner (keep count)
        SpinnerNumberModel cleanupModel = new SpinnerNumberModel(5, 1, 50, 1);
        cleanupSpinner = new JSpinner(cleanupModel);
        
        // Labels
        statusLabel = new JLabel("Status: Checking...");
        backupCountLabel = new JLabel("Backup Count: 0");
        
        // Style components
        Font labelFont = new Font("Arial", Font.BOLD, 12);
        statusLabel.setFont(labelFont);
        backupCountLabel.setFont(labelFont);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Title
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("System Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Backup section
        JPanel backupSection = new JPanel(new GridBagLayout());
        backupSection.setBorder(BorderFactory.createTitledBorder("Backup Settings"));
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        backupSection.add(autoBackupCheckBox, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        backupSection.add(new JLabel("Backup Interval (hours):"), gbc);
        
        gbc.gridx = 1;
        backupSection.add(intervalSpinner, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        backupSection.add(runBackupButton, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        backupSection.add(statusLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        backupSection.add(backupCountLabel, gbc);
        
        // Cleanup section
        JPanel cleanupSection = new JPanel(new GridBagLayout());
        cleanupSection.setBorder(BorderFactory.createTitledBorder("Backup Cleanup"));
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        cleanupSection.add(new JLabel("Keep last N backups:"), gbc);
        
        gbc.gridx = 1;
        cleanupSection.add(cleanupSpinner, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        cleanupSection.add(cleanupButton, gbc);
        
        // Add sections to main panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(backupSection, gbc);
        
        gbc.gridy = 1;
        mainPanel.add(cleanupSection, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Bottom panel with back button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void attachListeners() {
        autoBackupCheckBox.addActionListener(e -> handleAutoBackupToggle());
        runBackupButton.addActionListener(e -> handleRunBackup());
        cleanupButton.addActionListener(e -> handleCleanup());
        backButton.addActionListener(e -> handleBack());
        
        // Update status when interval changes
        intervalSpinner.addChangeListener(e -> {
            if (autoBackupCheckBox.isSelected()) {
                updateBackupSchedule();
            }
        });
    }
    
    private void configureFrame() {
        setTitle("Settings - Supermarket Billing System");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
    }
    
    private void handleAutoBackupToggle() {
        if (autoBackupCheckBox.isSelected()) {
            updateBackupSchedule();
        } else {
            backupController.stopScheduledBackup();
            SimpleLogger.logInfo("Auto backup disabled by user");
        }
        updateStatus();
    }
    
    private void updateBackupSchedule() {
        int intervalHours = (Integer) intervalSpinner.getValue();
        backupController.setBackupIntervalHours(intervalHours);
        
        // Stop existing schedule and start new one
        backupController.stopScheduledBackup();
        backupController.scheduleBackup(0, intervalHours); // Start immediately, then repeat
        
        SimpleLogger.logInfo("Auto backup schedule updated: every " + intervalHours + " hours");
        updateStatus();
    }
    
    private void handleRunBackup() {
        runBackupButton.setEnabled(false);
        runBackupButton.setText("Running Backup...");
        
        // Run backup in background thread
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return backupController.runBackup();
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(SettingsView.this, 
                                "Backup completed successfully!", 
                                "Backup Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(SettingsView.this, 
                                "Backup failed. Check logs for details.", 
                                "Backup Failed", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(SettingsView.this, 
                            "Backup error: " + e.getMessage(), 
                            "Backup Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
                
                runBackupButton.setEnabled(true);
                runBackupButton.setText("Run Backup Now");
                updateStatus();
            }
        };
        
        worker.execute();
    }
    
    private void handleCleanup() {
        int keepCount = (Integer) cleanupSpinner.getValue();
        
        int confirm = JOptionPane.showConfirmDialog(this,
                String.format("This will delete old backups, keeping only the last %d backups.\nContinue?", keepCount),
                "Confirm Cleanup",
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            cleanupButton.setEnabled(false);
            cleanupButton.setText("Cleaning...");
            
            // Run cleanup in background thread
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    backupController.cleanupOldBackups(keepCount);
                    return null;
                }
                
                @Override
                protected void done() {
                    JOptionPane.showMessageDialog(SettingsView.this, 
                            "Cleanup completed!", 
                            "Cleanup Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    cleanupButton.setEnabled(true);
                    cleanupButton.setText("Cleanup Old Backups");
                    updateStatus();
                }
            };
            
            worker.execute();
        }
    }
    
    private void handleBack() {
        dispose();
        DashboardView dashboard = new DashboardView();
        dashboard.showWindow();
    }
    
    private void updateStatus() {
        statusLabel.setText("Status: " + backupController.getLastBackupStatus());
        backupCountLabel.setText("Backup Count: " + backupController.getBackupCount());
        
        // Update checkbox state
        autoBackupCheckBox.setSelected(backupController.isAutoBackupEnabled());
    }
    
    public void showWindow() {
        setVisible(true);
        updateStatus();
    }
}
