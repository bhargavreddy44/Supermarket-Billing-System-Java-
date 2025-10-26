package util;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SimpleLogger {
    private static final String LOG_FILE = "logs/app.log";
    private static final DateTimeFormatter LOG_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static PrintWriter logWriter;
    private static boolean initialized = false;
    
    static {
        initializeLogger();
    }
    
    private static void initializeLogger() {
        try {
            // Ensure logs directory exists
            FileUtils.ensureDirectories();
            
            // Initialize log writer
            logWriter = new PrintWriter(new FileWriter(LOG_FILE, true));
            initialized = true;
            
            logInfo("Logger initialized");
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
            initialized = false;
        }
    }
    
    /**
     * Logs an info message
     */
    public static void logInfo(String message) {
        log("INFO", message, null);
    }
    
    /**
     * Logs an error message with exception
     */
    public static void logError(String message, Exception exception) {
        log("ERROR", message, exception);
    }
    
    /**
     * Logs a warning message
     */
    public static void logWarning(String message) {
        log("WARN", message, null);
    }
    
    /**
     * Logs a debug message
     */
    public static void logDebug(String message) {
        log("DEBUG", message, null);
    }
    
    private static void log(String level, String message, Exception exception) {
        if (!initialized) {
            System.err.println("Logger not initialized: " + level + " - " + message);
            return;
        }
        
        try {
            String timestamp = LocalDateTime.now().format(LOG_FORMATTER);
            String logEntry = String.format("[%s] %s: %s", timestamp, level, message);
            
            // Write to file
            logWriter.println(logEntry);
            
            // If there's an exception, log the stack trace
            if (exception != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                exception.printStackTrace(pw);
                logWriter.println(sw.toString());
            }
            
            // Flush to ensure immediate write
            logWriter.flush();
            
            // Also print to console for development
            System.out.println(logEntry);
            if (exception != null) {
                exception.printStackTrace();
            }
            
        } catch (Exception e) {
            System.err.println("Error writing to log: " + e.getMessage());
        }
    }
    
    /**
     * Closes the logger and flushes any remaining data
     */
    public static void close() {
        if (logWriter != null) {
            logInfo("Logger shutting down");
            logWriter.close();
            initialized = false;
        }
    }
    
    /**
     * Gets the current log file path
     */
    public static String getLogFilePath() {
        return LOG_FILE;
    }
    
    /**
     * Checks if logger is properly initialized
     */
    public static boolean isInitialized() {
        return initialized;
    }
}
