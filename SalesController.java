package controller;

import model.Sale;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class SalesController {
    private static final String SALES_CSV = "data/sales.csv";
    private static final String REPORTS_DIR = "reports/";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public SalesController() {
        ensureReportsDirectoryExists();
    }

    private void ensureReportsDirectoryExists() {
        try {
            Files.createDirectories(Paths.get(REPORTS_DIR));
        } catch (IOException e) {
            System.err.println("Error creating reports directory: " + e.getMessage());
        }
    }

    public List<Sale> loadAllSales() {
        List<Sale> sales = new ArrayList<>();
        File file = new File(SALES_CSV);
        
        if (!file.exists()) {
            System.err.println("Sales CSV file not found: " + SALES_CSV);
            return sales;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // Skip header line
            reader.readLine();
            
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                try {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        LocalDateTime timestamp = LocalDateTime.parse(parts[0].trim(), DATE_TIME_FORMATTER);
                        double total = Double.parseDouble(parts[1].trim());
                        double discount = Double.parseDouble(parts[2].trim());
                        double netTotal = Double.parseDouble(parts[3].trim());
                        
                        sales.add(new Sale(timestamp, total, discount, netTotal));
                    }
                } catch (DateTimeParseException | NumberFormatException e) {
                    System.err.println("Error parsing sale line: " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading sales: " + e.getMessage());
        }
        
        // Sort by timestamp (most recent first)
        sales.sort((s1, s2) -> s2.getTimestamp().compareTo(s1.getTimestamp()));
        return sales;
    }

    public List<Sale> filterSales(LocalDate from, LocalDate to) {
        List<Sale> allSales = loadAllSales();
        
        return allSales.stream()
                .filter(sale -> {
                    LocalDate saleDate = sale.getDate();
                    return !saleDate.isBefore(from) && !saleDate.isAfter(to);
                })
                .collect(Collectors.toList());
    }

    public List<Sale> getLastNDays(int days) {
        List<Sale> allSales = loadAllSales();
        if (allSales.isEmpty()) {
            return allSales;
        }
        
        LocalDate cutoffDate = LocalDate.now().minusDays(days - 1);
        
        return allSales.stream()
                .filter(sale -> !sale.getDate().isBefore(cutoffDate))
                .collect(Collectors.toList());
    }

    public Map<LocalDate, Double> computeDailyTotals(List<Sale> sales) {
        Map<LocalDate, Double> dailyTotals = new HashMap<>();
        
        for (Sale sale : sales) {
            LocalDate date = sale.getDate();
            dailyTotals.merge(date, sale.getNetTotal(), Double::sum);
        }
        
        return dailyTotals;
    }

    public double computeCumulativeTotal(List<Sale> sales) {
        return sales.stream()
                .mapToDouble(Sale::getNetTotal)
                .sum();
    }

    public double computeAllTimeTotal() {
        List<Sale> allSales = loadAllSales();
        return computeCumulativeTotal(allSales);
    }

    public void exportReport(List<Sale> sales, LocalDate from, LocalDate to) throws IOException {
        String fileName = String.format("report_%s_to_%s.csv", 
                from.format(DATE_FORMATTER), 
                to.format(DATE_FORMATTER));
        
        Path outputFile = Paths.get(REPORTS_DIR + fileName);
        
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outputFile))) {
            // Write header
            writer.println("Date,Time,Total,Discount,Net Total");
            
            // Write sales data
            for (Sale sale : sales) {
                writer.printf("%s,%s,%.2f,%.2f,%.2f%n",
                        sale.getDate().format(DATE_FORMATTER),
                        sale.getTime().format(TIME_FORMATTER),
                        sale.getTotal(),
                        sale.getDiscount(),
                        sale.getNetTotal());
            }
        }
    }

    public void exportReport(List<Sale> sales, String customFileName) throws IOException {
        Path outputFile = Paths.get(REPORTS_DIR + customFileName);
        
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outputFile))) {
            // Write header
            writer.println("Date,Time,Total,Discount,Net Total");
            
            // Write sales data
            for (Sale sale : sales) {
                writer.printf("%s,%s,%.2f,%.2f,%.2f%n",
                        sale.getDate().format(DATE_FORMATTER),
                        sale.getTime().format(TIME_FORMATTER),
                        sale.getTotal(),
                        sale.getDiscount(),
                        sale.getNetTotal());
            }
        }
    }

    public List<String> getAvailableReportFiles() {
        File reportsDir = new File(REPORTS_DIR);
        if (!reportsDir.exists()) {
            return new ArrayList<>();
        }
        
        File[] files = reportsDir.listFiles((dir, name) -> name.endsWith(".csv"));
        if (files == null) {
            return new ArrayList<>();
        }
        
        return Arrays.stream(files)
                .map(File::getName)
                .sorted()
                .collect(Collectors.toList());
    }

    public LocalDate parseDate(String dateString) throws DateTimeParseException {
        return LocalDate.parse(dateString.trim(), DATE_FORMATTER);
    }

    public String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    public String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }
}
