package controller;

import util.FileUtils;
import util.SimpleLogger;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;

public class BackupController {
    private static final String BACKUP_DIR = "backup/";
    private static final String DATA_DIR = "data/";
    private static final String BILLS_DIR = "bills/";
    private static final DateTimeFormatter BACKUP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    
    private ScheduledExecutorService scheduler;
    private boolean autoBackupEnabled = false;
    private long backupIntervalHours = 24;
    private LocalDateTime lastBackupTime;
    private boolean lastBackupSuccess = false;
    
    public BackupController() {
        FileUtils.ensureDirectories();
        SimpleLogger.logInfo("BackupController initialized");
    }
    
    /**
     * Runs an immediate backup of data and bills directories
     */
    public boolean runBackup() {
        try {
            SimpleLogger.logInfo("Starting backup process");
            
            String timestamp = LocalDateTime.now().format(BACKUP_FORMATTER);
            Path backupPath = Paths.get(BACKUP_DIR + "backup_" + timestamp);
            
            // Create backup directory
            Files.createDirectories(backupPath);
            
            List<Path> backedUpFiles = new ArrayList<>();
            
            // Backup data directory
            Path dataBackupPath = backupPath.resolve("data");
            if (Files.exists(Paths.get(DATA_DIR))) {
                FileUtils.copyDirectory(Paths.get(DATA_DIR), dataBackupPath);
                collectFiles(dataBackupPath, backedUpFiles);
                SimpleLogger.logInfo("Data directory backed up to: " + dataBackupPath);
            }
            
            // Backup bills directory
            Path billsBackupPath = backupPath.resolve("bills");
            if (Files.exists(Paths.get(BILLS_DIR))) {
                FileUtils.copyDirectory(Paths.get(BILLS_DIR), billsBackupPath);
                collectFiles(billsBackupPath, backedUpFiles);
                SimpleLogger.logInfo("Bills directory backed up to: " + billsBackupPath);
            }
            
            // Create backup manifest
            Path[] filesArray = backedUpFiles.toArray(new Path[0]);
            FileUtils.createBackupManifest(backupPath, filesArray);
            
            lastBackupTime = LocalDateTime.now();
            lastBackupSuccess = true;
            
            SimpleLogger.logInfo("Backup completed successfully: " + backupPath);
            return true;
            
        } catch (IOException e) {
            lastBackupSuccess = false;
            SimpleLogger.logError("Backup failed", e);
            return false;
        }
    }
    
    /**
     * Collects all files from a directory recursively
     */
    private void collectFiles(Path directory, List<Path> fileList) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    collectFiles(entry, fileList);
                } else {
                    fileList.add(entry);
                }
            }
        } catch (IOException e) {
            SimpleLogger.logError("Error collecting files from: " + directory, e);
        }
    }
    
    /**
     * Schedules automatic backups
     */
    public void scheduleBackup(long initialDelayHours, long intervalHours) {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        
        scheduler = Executors.newScheduledThreadPool(1);
        autoBackupEnabled = true;
        backupIntervalHours = intervalHours;
        
        scheduler.scheduleAtFixedRate(
            this::runScheduledBackup,
            initialDelayHours,
            intervalHours,
            TimeUnit.HOURS
        );
        
        SimpleLogger.logInfo(String.format("Scheduled backup enabled: initial delay %d hours, interval %d hours", 
                initialDelayHours, intervalHours));
    }
    
    /**
     * Runs a scheduled backup
     */
    private void runScheduledBackup() {
        SimpleLogger.logInfo("Running scheduled backup");
        runBackup();
    }
    
    /**
     * Stops scheduled backups
     */
    public void stopScheduledBackup() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        autoBackupEnabled = false;
        SimpleLogger.logInfo("Scheduled backup stopped");
    }
    
    /**
     * Gets the status of the last backup
     */
    public String getLastBackupStatus() {
        if (lastBackupTime == null) {
            return "No backup performed yet";
        }
        
        String status = lastBackupSuccess ? "Success" : "Failed";
        return String.format("Last backup: %s (%s)", 
                lastBackupTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                status);
    }
    
    /**
     * Gets the backup directory path
     */
    public String getBackupDirectory() {
        return BACKUP_DIR;
    }
    
    /**
     * Checks if auto backup is enabled
     */
    public boolean isAutoBackupEnabled() {
        return autoBackupEnabled;
    }
    
    /**
     * Gets the backup interval in hours
     */
    public long getBackupIntervalHours() {
        return backupIntervalHours;
    }
    
    /**
     * Sets the backup interval
     */
    public void setBackupIntervalHours(long hours) {
        this.backupIntervalHours = hours;
        SimpleLogger.logInfo("Backup interval set to: " + hours + " hours");
    }
    
    /**
     * Gets the number of backup directories
     */
    public int getBackupCount() {
        try {
            Path backupPath = Paths.get(BACKUP_DIR);
            if (!Files.exists(backupPath)) {
                return 0;
            }
            
            int count = 0;
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(backupPath, "backup_*")) {
                for (Path entry : stream) {
                    if (Files.isDirectory(entry)) {
                        count++;
                    }
                }
            }
            return count;
        } catch (IOException e) {
            SimpleLogger.logError("Error counting backups", e);
            return 0;
        }
    }
    
    /**
     * Cleans up old backups, keeping only the most recent N backups
     */
    public void cleanupOldBackups(int keepCount) {
        try {
            Path backupPath = Paths.get(BACKUP_DIR);
            if (!Files.exists(backupPath)) {
                return;
            }
            
            List<Path> backupDirs = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(backupPath, "backup_*")) {
                for (Path entry : stream) {
                    if (Files.isDirectory(entry)) {
                        backupDirs.add(entry);
                    }
                }
            }
            
            // Sort by creation time (newest first)
            backupDirs.sort((a, b) -> {
                try {
                    return Files.getLastModifiedTime(b).compareTo(Files.getLastModifiedTime(a));
                } catch (IOException e) {
                    return 0;
                }
            });
            
            // Remove old backups
            for (int i = keepCount; i < backupDirs.size(); i++) {
                Path oldBackup = backupDirs.get(i);
                FileUtils.copyDirectory(oldBackup, Paths.get("/dev/null")); // This will fail, but we'll catch it
                try {
                    deleteDirectory(oldBackup);
                    SimpleLogger.logInfo("Cleaned up old backup: " + oldBackup.getFileName());
                } catch (IOException e) {
                    SimpleLogger.logError("Failed to delete old backup: " + oldBackup, e);
                }
            }
            
        } catch (IOException e) {
            SimpleLogger.logError("Error during backup cleanup", e);
        }
    }
    
    /**
     * Recursively deletes a directory
     */
    private void deleteDirectory(Path directory) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    deleteDirectory(entry);
                } else {
                    Files.delete(entry);
                }
            }
        }
        Files.delete(directory);
    }
}
