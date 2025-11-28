package com.goldenhour.service.salessys;

import com.goldenhour.categories.Model;
import com.goldenhour.categories.Sales;
import com.goldenhour.categories.Employee;
import com.goldenhour.storage.ReceiptHandler;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * SalesService - record new sale (one item per sale). Outlet is entered manually.
 * No stock deduction performed(can be added if required).
 */
public class SalesService {

    private static final String DATA_DIR = "data";
    private static final String SALES_CSV = DATA_DIR + "/sales.csv";
    private static final String MODELS_CSV = DATA_DIR + "/model.csv";
    private static final String OUTLETS_CSV = DATA_DIR + "/outlet.csv";
    private static final String CSV_HEADER = "date,time,customerName,model,quantity,subtotal,transactionMethod,employee";

    private final Scanner sc = new Scanner(System.in);

    // Ensure data/sales.csv exists and has header
    private void ensureSalesCsvExists() {
        try {
            Path dataDir = Paths.get(DATA_DIR);
            if (!Files.exists(dataDir)) Files.createDirectories(dataDir);

            Path salesPath = Paths.get(SALES_CSV);
            if (!Files.exists(salesPath)) {
                try (BufferedWriter bw = Files.newBufferedWriter(salesPath)) {
                    bw.write(CSV_HEADER);
                    bw.newLine();
                }
            } else {
                // ensure header present
                List<String> lines = Files.readAllLines(salesPath);
                if (lines.isEmpty() || !lines.get(0).equalsIgnoreCase(CSV_HEADER)) {
                    lines.add(0, CSV_HEADER);
                    Files.write(salesPath, lines);
                }
            }
        } catch (IOException e) {
            System.out.println("Error initializing sales.csv: " + e.getMessage());
        }
    }

    // Load outlet codes from data/outlet.csv (first column)
    private String[] loadOutletCodes() {
        Path p = Paths.get(OUTLETS_CSV);
        if (!Files.exists(p)) return new String[0];
        List<String> codes = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(p)) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 1 && !parts[0].trim().isEmpty()) {
                    codes.add(parts[0].trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading outlet.csv: " + e.getMessage());
        }
        return codes.toArray(new String[0]);
    }

    // Load models into map modelCode -> Model using Model.fromCSV
    private Map<String, Model> loadModels() {
        Map<String, Model> map = new HashMap<>();
        Path p = Paths.get(MODELS_CSV);
        if (!Files.exists(p)) return map;

        String[] outletCodes = loadOutletCodes();

        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                Model m = Model.fromCSV(line, outletCodes);
                if (m != null) map.put(m.getModelCode().toLowerCase(), m);
            }
        } catch (IOException e) {
            System.out.println("Error loading model.csv: " + e.getMessage());
        }
        return map;
    }

    /**
     * Interactive sale recording.
     * @param employee logged-in employee object (must not be null)
     */
    public void recordNewSale(Employee employee) {
        if (employee == null) {
            System.out.println("No logged-in employee provided.");
            return;
        }

        ensureSalesCsvExists();
        Map<String, Model> models = loadModels();

        System.out.println("=== Record New Sale ===");
        System.out.print("Customer Name: ");
        String customer = sc.nextLine().trim();

        // Outlet selection (Option B)
        System.out.print("Enter outlet code (e.g., C60): ");
        String outletCode = sc.nextLine().trim();

        System.out.print("Enter Model: ");
        String modelName = sc.nextLine().trim();

        int qty = 0;
        while (true) {
            System.out.print("Enter Quantity: ");
            String qStr = sc.nextLine().trim();
            try {
                qty = Integer.parseInt(qStr);
                if (qty <= 0) {
                    System.out.println("Quantity must be > 0, try again.");
                    continue;
                }
                break;
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number. Try again.");
            }
        }

        double unitPrice = 0.0;
        Model m = models.get(modelName.toLowerCase());
        if (m != null) {
            unitPrice = m.getPrice();
        } else {
            // fallback: ask user for unit price
            System.out.print("Unit price for this model not found in model.csv. Enter unit price (RM): ");
            try {
                unitPrice = Double.parseDouble(sc.nextLine().trim());
            } catch (Exception e) {
                unitPrice = 0.0;
            }
        }
        System.out.println("Unit Price: RM" + unitPrice);

        System.out.print("Enter transaction method (cash / card / e-wallet): ");
        String method = sc.nextLine().trim();

        double subtotal = unitPrice * qty;

        // Date/time
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String time = now.format(DateTimeFormatter.ofPattern("hh:mm a"));

        // Create Sales object
        Sales sale = new Sales(date, time, customer, modelName, qty, subtotal, method, employee.getName());

        // Append to CSV
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SALES_CSV, true))) {
            bw.write(sale.toCSV());
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to sales.csv: " + e.getMessage());
        }

        // Append to receipt using ReceiptHandler
        ReceiptHandler.writeSalesReceipt(sale, outletCode, unitPrice);

        System.out.println("\nSubtotal: RM" + subtotal);
        System.out.println("\nTransaction successful.");
        System.out.println("Sale recorded successfully.");
        System.out.println("Receipt saved and sales.csv updated.");
    }
}

