package com.goldenhour.service.stocksys;

import com.goldenhour.categories.Model;
import com.goldenhour.dataload.DataLoad;
import com.goldenhour.storage.CSVHandler;

import java.util.Scanner;

public class EditStock {

    public static void editStock(Scanner sc) {
        System.out.println("\n=== Edit Stock Information ===");
        
        // 1. Identify Context (Which store are we fixing?)
        System.out.print("Enter Outlet Code (e.g., C60): ");
        String outletCode = sc.nextLine().trim().toUpperCase();
        
        // Validate Outlet
        boolean outletExists = DataLoad.allOutlets.stream()
                .anyMatch(o -> o.getOutletCode().equalsIgnoreCase(outletCode));
        
        if (!outletExists) {
            System.out.println("Error: Outlet code not found.");
            return;
        }

        // 2. Identify Model
        System.out.print("Enter Model Name: "); // Matches sample output
        String keyword = sc.nextLine().trim();

        Model foundModel = DataLoad.allModels.stream()
                .filter(m -> m.getModelCode().equalsIgnoreCase(keyword))
                .findFirst()
                .orElse(null);

        if (foundModel == null) {
            System.out.println("Error: Model not found.");
            return;
        }

        // 3. Show Current State
        int currentStock = foundModel.getStock(outletCode);
        System.out.println("\nCurrent Stock at " + outletCode + ": " + currentStock); // Matches sample output

        // 4. Get New Value
        int newStock = -1;
        while (true) {
            System.out.print("Enter New Stock Value: ");
            try {
                newStock = Integer.parseInt(sc.nextLine().trim());
                if (newStock < 0) {
                    System.out.println("Stock cannot be negative.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");
            }
        }

        // 5. Update Memory
        foundModel.setStock(outletCode, newStock);

        // 6. Save to Disk (Rewrites model.csv)
        CSVHandler.writeStock(DataLoad.allModels);
        
        System.out.println("\nStock information updated \u001B[32msuccessfully\u001B[0m.");
    }
}