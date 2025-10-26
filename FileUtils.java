package util;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileUtils {
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
    
    /**
     * Ensures all required directories exist
     */
    public static void ensureDirectories() {
        String[] directories = {"data", "bills", "reports", "backup", "logs"};
        
        for (String dir : directories) {
            try {
                Files.createDirectories(Paths.get(dir));
            } catch (IOException e) {
                System.err.println("Error creating directory " + dir + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Safely writes text content to a file using atomic write (write to temp file then move)
     */
    public static void safeWriteTextFile(Path file, String content) throws IOException {
        // Create parent directories if they don't exist
        Files.createDirectories(file.getParent());
        
        // Write to temporary file first
        Path tempFile = file.resolveSibling(file.getFileName() + ".tmp");
        
        try {
            Files.write(tempFile, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            
            // Atomically move temp file to final location
            Files.move(tempFile, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            // Clean up temp file if it exists
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException cleanupException) {
                // Ignore cleanup errors
            }
            throw e;
        }
    }
    
    /**
     * Generates a timestamped filename with the given prefix and extension
     */
    public static String timestampedFilename(String prefix, String extension) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        return String.format("%s_%s.%s", prefix, timestamp, extension);
    }
    
    /**
     * Recursively copies a directory and all its contents
     */
    public static void copyDirectory(Path source, Path destination) throws IOException {
        Files.createDirectories(destination);
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(source)) {
            for (Path entry : stream) {
                Path targetPath = destination.resolve(entry.getFileName());
                
                if (Files.isDirectory(entry)) {
                    copyDirectory(entry, targetPath);
                } else {
                    Files.copy(entry, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
    
    /**
     * Safely appends content to a file
     */
    public static void safeAppendToFile(Path file, String content) throws IOException {
        Files.createDirectories(file.getParent());
        Files.write(file, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
    
    /**
     * Gets the size of a file in bytes
     */
    public static long getFileSize(Path file) {
        try {
            return Files.size(file);
        } catch (IOException e) {
            return 0;
        }
    }
    
    /**
     * Checks if a file exists and is readable
     */
    public static boolean isFileReadable(Path file) {
        return Files.exists(file) && Files.isReadable(file);
    }
    
    /**
     * Creates a backup manifest file listing backed-up files and their sizes
     */
    public static void createBackupManifest(Path backupDir, Path[] backedUpFiles) throws IOException {
        StringBuilder manifest = new StringBuilder();
        manifest.append("Backup Manifest\n");
        manifest.append("===============\n");
        manifest.append("Backup Date: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
        manifest.append("Files Backed Up:\n\n");
        
        long totalSize = 0;
        for (Path file : backedUpFiles) {
            long size = getFileSize(file);
            totalSize += size;
            manifest.append(String.format("%-50s %10d bytes\n", file.toString(), size));
        }
        
        manifest.append("\nTotal Size: ").append(totalSize).append(" bytes\n");
        
        Path manifestFile = backupDir.resolve("backup_manifest.txt");
        safeWriteTextFile(manifestFile, manifest.toString());
    }
}
