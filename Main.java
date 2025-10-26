import view.LoginView;
import util.SimpleLogger;
import controller.BackupController;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    private static BackupController backupController;
    
    public static void main(String[] args) {
        // Initialize logger
        SimpleLogger.logInfo("Application starting");
        
        // Set system look and feel
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            SimpleLogger.logError("Error setting look and feel", e);
        }

        // Initialize backup controller
        backupController = new BackupController();
        
        // Create and show the login window
        javax.swing.SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
            
            // Add shutdown hook for graceful exit
            loginView.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    shutdown();
                }
            });
        });
        
        // Add JVM shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(Main::shutdown));
    }
    
    private static void shutdown() {
        SimpleLogger.logInfo("Application shutting down");
        
        // Stop scheduled backups
        if (backupController != null) {
            backupController.stopScheduledBackup();
        }
        
        // Close logger
        SimpleLogger.close();
    }
}
