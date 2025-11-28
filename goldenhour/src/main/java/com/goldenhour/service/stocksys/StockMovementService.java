package com.goldenhour.service.stocksys;

import com.goldenhour.categories.*;
import com.goldenhour.service.loginregister.AuthService;
import com.goldenhour.storage.*;
import com.goldenhour.utils.TimeUtils;
import com.goldenhour.dataload.DataLoad;

import java.util.*;

public class StockMovementService {
    public static void stockInOut(String type) {
        Scanner sc = new Scanner(System.in);
        List<Model> models = DataLoad.allModels; // reads full model list (with per-outlet stock)
        List<Outlet> outlets = DataLoad.allOutlets;
        List<String> movementDetails = new ArrayList<>();

        System.out.print("From: ");
        String from = sc.nextLine().trim();
        System.out.print("To: ");
        String to = sc.nextLine().trim();

        int totalQty = 0;

        while (true) {
            System.out.print("Enter Model Code (or 'done' to finish): ");
            String code = sc.nextLine().trim();
            if (code.equalsIgnoreCase("done")) break;

            System.out.print("Quantity: ");
            int qty;
            try {
                qty = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity. Try again.");
                continue;
            }

            Optional<Model> match = models.stream()
                    .filter(m -> m.getModelCode().equalsIgnoreCase(code))
                    .findFirst();

            if (match.isPresent()) {
                Model m = match.get();

                if (type.equalsIgnoreCase("Stock In")) {
                    // increase stock at 'to' outlet
                    int current = m.getStock(to);
                    m.setStock(to, current + qty);
                    current = m.getStock(from);
                    m.setStock(from, current - qty); // Error
                } else {
                    // decrease stock at 'from' outlet (ensure not negative)
                    int current = m.getStock(from);
                    if (current < qty) {
                        System.out.println("Insufficient stock at source outlet (" + from + "). Available: " + current);
                        continue;
                    }
                    m.setStock(from, current - qty); // Error
                    current = m.getStock(to);
                    m.setStock(to, current + qty);
                }

                movementDetails.add("- " + m.getModelCode() + " (Quantity:" + qty + ")");
                totalQty += qty;
            } else {
                System.out.println("Model not found: " + code);
            }
        }

        // Persist updated models back to CSV
        CSVHandler.writeStock(models); // implement to overwrite model CSV

        System.out.println();

        String fromName = "";
        String toName = "";

        // Get outlet names
        for (Outlet o : outlets) {
            if (o.getOutletCode().equals(from)) {
                fromName = o.getOutletName();
            } else if (o.getOutletCode().equals(to)) {
                toName = o.getOutletName();
            }
        }

        // Build receipt text

        String receipt = "=== " + type + " ===\n" +
                "Date: " + TimeUtils.getDate() + "\n" +
                "Time: " + TimeUtils.getTime() + "\n" +
                "From: " + from + " (" + fromName + ")\n" +
                "To: " + to + " (" + toName + ")\n" +
                "Models:\n" + String.join("\n", movementDetails) + "\n" +
                "Total Quantity: " + totalQty + "\n" +
                "Employee in Charge: " + (AuthService.getCurrentUser() != null ? AuthService.getCurrentUser().getName() : "Unknown");

        System.out.println(receipt);
        System.out.println();
        System.out.println("Model quantities updated successfully.");
        System.out.println(type + " recorded.");

        ReceiptHandler.appendReceipt(receipt); // appends to receipts_YYYY-MM-DD.txt
        sc.close();
    }

}

