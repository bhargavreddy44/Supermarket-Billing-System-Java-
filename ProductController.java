package controller;

import model.Product;
import java.io.*;
import java.util.*;

public class ProductController {
    private static final String CSV_FILE = "data/products.csv";
    private List<Product> products;

    public ProductController() {
        this.products = new ArrayList<>();
        loadProducts();
    }

    public void loadProducts() {
        File file = new File(CSV_FILE);
        
        // Create directory if it doesn't exist
        file.getParentFile().mkdirs();
        
        // Create file with header if it doesn't exist
        if (!file.exists()) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("id,name,category,price,quantity");
            } catch (IOException e) {
                System.err.println("Error creating CSV file: " + e.getMessage());
            }
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

    public void saveProducts() {
        File file = new File(CSV_FILE);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("id,name,category,price,quantity");
            for (Product product : products) {
                writer.println(String.format("%s,%s,%s,%.2f,%d",
                        product.getId(),
                        product.getName(),
                        product.getCategory(),
                        product.getPrice(),
                        product.getQuantity()));
            }
        } catch (IOException e) {
            System.err.println("Error saving products: " + e.getMessage());
            throw new RuntimeException("Failed to save products", e);
        }
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public void addProduct(Product p) {
        products.add(p);
        saveProducts();
    }

    public void updateProduct(Product p) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(p.getId())) {
                products.set(i, p);
                saveProducts();
                return;
            }
        }
    }

    public void deleteProduct(String id) {
        products.removeIf(p -> p.getId().equals(id));
        saveProducts();
    }

    public Product getProductById(String id) {
        for (Product p : products) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }
}
