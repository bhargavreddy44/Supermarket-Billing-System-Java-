package controller;

import model.Product;
import model.BilledItem;
import util.FileUtils;
import util.SimpleLogger;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import java.awt.Desktop;

public class BillingController {
    private static final String PRODUCTS_CSV = "data/products.csv";
    private static final String SALES_CSV = "data/sales.csv";
    private static final String BILLS_DIR = "bills/";
    private List<Product> products;

    public BillingController() {
        this.products = new ArrayList<>();
        FileUtils.ensureDirectories();
        loadProducts();
        ensureDirectoriesExist();
        SimpleLogger.logInfo("BillingController initialized");
    }

    private void ensureDirectoriesExist() {
        // Create sales.csv with header if it doesn't exist
        Path salesFile = Paths.get(SALES_CSV);
        if (!Files.exists(salesFile)) {
            try {
                FileUtils.safeWriteTextFile(salesFile, "date,total,discount,netTotal\n");
                SimpleLogger.logInfo("Created sales.csv with header");
            } catch (IOException e) {
                SimpleLogger.logError("Error creating sales.csv", e);
            }
        }
    }

    public void loadProducts() {
        File file = new File(PRODUCTS_CSV);
        
        if (!file.exists()) {
            System.err.println("Products CSV file not found: " + PRODUCTS_CSV);
            return;
        }

        products.clear();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // Skip header line
            reader.readLine();
            
            while ((line = reader.readLine()) != null && !line.trim().isEmpty()) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    try {
                        String id = parts[0].trim();
                        String name = parts[1].trim();
                        String category = parts[2].trim();
                        double price = Double.parseDouble(parts[3].trim());
                        int quantity = Integer.parseInt(parts[4].trim());
                        products.add(new Product(id, name, category, price, quantity));
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing product: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading products: " + e.getMessage());
        }
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public Product getProductById(String id) {
        for (Product p : products) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public void updateProductStock(String id, int qtySold) {
        for (Product product : products) {
            if (product.getId().equals(id)) {
                int newQuantity = product.getQuantity() - qtySold;
                if (newQuantity < 0) {
                    throw new IllegalArgumentException("Insufficient stock for product: " + id);
                }
                product.setQuantity(newQuantity);
                break;
            }
        }
        saveProductsToCSV();
    }

    private void saveProductsToCSV() {
        Path file = Paths.get(PRODUCTS_CSV);
        
        StringBuilder content = new StringBuilder();
        content.append("id,name,category,price,quantity\n");
        for (Product product : products) {
            content.append(String.format("%s,%s,%s,%.2f,%d\n",
                    product.getId(),
                    product.getName(),
                    product.getCategory(),
                    product.getPrice(),
                    product.getQuantity()));
        }
        
        try {
            FileUtils.safeWriteTextFile(file, content.toString());
            SimpleLogger.logInfo("Products CSV saved successfully");
        } catch (IOException e) {
            SimpleLogger.logError("Error saving products CSV", e);
            throw new RuntimeException("Failed to save products", e);
        }
    }

    public void saveBill(List<BilledItem> items, double discountPercent, double totalAmount, double netAmount) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        
        String timestamp = now.format(fileFormatter);
        String txtFileName = "Bill_" + timestamp + ".txt";
        String csvFileName = "Bill_" + timestamp + ".csv";
        
        Path txtBillFile = Paths.get(BILLS_DIR + txtFileName);
        Path csvBillFile = Paths.get(BILLS_DIR + csvFileName);
        
        try {
            // Create text bill content
            StringBuilder billContent = new StringBuilder();
            billContent.append("----------------------------\n");
            billContent.append("Supermarket Billing System\n");
            billContent.append("Date: ").append(now.format(dateFormatter)).append("  Time: ").append(now.format(timeFormatter)).append("\n");
            billContent.append("--------------------------------\n");
            
            // Items header
            billContent.append(String.format("%-6s %-12s %-8s %-4s %-10s%n", "ID", "Name", "Price", "Qty", "Subtotal"));
            
            // Items
            for (BilledItem item : items) {
                billContent.append(String.format("%-6s %-12s %-8.2f %-4d %-10.2f%n",
                        item.getId(),
                        item.getName(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getSubtotal()));
            }
            
            billContent.append("--------------------------------\n");
            billContent.append(String.format("Total: %.2f%n", totalAmount));
            billContent.append(String.format("Discount: %.0f%%%n", discountPercent));
            billContent.append(String.format("Net Amount: %.2f%n", netAmount));
            billContent.append("--------------------------------\n");
            billContent.append("Thank You! Visit Again.\n");
            billContent.append("----------------------------\n");
            
            // Save text bill atomically
            FileUtils.safeWriteTextFile(txtBillFile, billContent.toString());
            
            // Create CSV bill content
            StringBuilder csvContent = new StringBuilder();
            csvContent.append("ID,Name,Price,Quantity,Subtotal\n");
            for (BilledItem item : items) {
                csvContent.append(String.format("%s,%s,%.2f,%d,%.2f\n",
                        item.getId(),
                        item.getName(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getSubtotal()));
            }
            
            // Save CSV bill atomically
            FileUtils.safeWriteTextFile(csvBillFile, csvContent.toString());
            
            SimpleLogger.logInfo(String.format("Bill saved successfully: %s (Items: %d, Total: %.2f)", 
                    txtFileName, items.size(), netAmount));
            
        } catch (IOException e) {
            SimpleLogger.logError("Error saving bill", e);
            throw new RuntimeException("Failed to save bill", e);
        }
    }

    public void recordSale(double total, double discount, double netTotal) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        String saleRecord = String.format("%s,%.2f,%.2f,%.2f\n",
                now.format(formatter),
                total,
                discount,
                netTotal);
        
        try {
            FileUtils.safeAppendToFile(Paths.get(SALES_CSV), saleRecord);
            SimpleLogger.logInfo(String.format("Sale recorded: Total=%.2f, Discount=%.2f, Net=%.2f", 
                    total, discount, netTotal));
        } catch (IOException e) {
            SimpleLogger.logError("Error recording sale", e);
            throw new RuntimeException("Failed to record sale", e);
        }
    }

    public boolean validateStock(String productId, int requestedQuantity) {
        Product product = getProductById(productId);
        if (product == null) {
            return false;
        }
        return product.getQuantity() >= requestedQuantity;
    }
    
    /**
     * Shows a success dialog with option to open the bills folder
     */
    public void showBillSuccessDialog(JFrame parent, String billFileName) {
        String message = String.format("Bill generated successfully!\n\nFile: %s\n\nWould you like to open the bills folder?", billFileName);
        
        int option = JOptionPane.showConfirmDialog(parent, message, "Bill Generated", 
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        
        if (option == JOptionPane.YES_OPTION) {
            try {
                Desktop.getDesktop().open(new File(BILLS_DIR));
                SimpleLogger.logInfo("Opened bills folder for user");
            } catch (IOException e) {
                SimpleLogger.logError("Error opening bills folder", e);
                JOptionPane.showMessageDialog(parent, "Could not open bills folder.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
