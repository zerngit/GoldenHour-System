package com.goldenhour.service.stocksys;

import com.goldenhour.categories.Model;
import com.goldenhour.categories.Outlet;
import com.goldenhour.dataload.DataLoad;

import java.util.Map;
import java.util.Scanner;

public class StockSearch {

    /**
     * Interactive method: Prompts user, searches, and prints results.
     */
    public static void searchStock(Scanner sc) {
        System.out.println("\n=== Search Stock Information ===");
        System.out.print("Search Model Name: ");
        String keyword = sc.nextLine().trim();
        
        System.out.println("\u001B[32mSearching...\u001B[0m");

        if (keyword.isEmpty()) {
            System.out.println("Invalid input.");
            return;
        }

        // 1. Find the model in memory
        Model foundModel = DataLoad.allModels.stream()
                .filter(m -> m.getModelCode().equalsIgnoreCase(keyword))
                .findFirst()
                .orElse(null);

        if (foundModel == null) {
            System.out.println("Model not found: " + keyword);
            return;
        }

        // 2. Print Header Details
        System.out.println("\nModel: " + foundModel.getModelCode()); 
        // Note: If you have a .getModelName() field, print it here too
        System.out.println("Unit Price: RM" + foundModel.getPrice());
        System.out.println("Stock by Outlet:");

        // 3. Print Stock with Outlet Names
        Map<String, Integer> stocks = foundModel.getStockPerOutlet();
        int count = 0;

        for (Map.Entry<String, Integer> entry : stocks.entrySet()) {
            String outletCode = entry.getKey();
            int qty = entry.getValue();

            // Resolve Outlet Name (The Service layer joins the data here)
            String outletName = getOutletName(outletCode);

            // Format: "KLCC: 5   "
            System.out.printf("%s: %d   ", outletName, qty);

            // formatting: new line every 3 items
            count++;
            if (count % 3 == 0) System.out.println();
        }
        System.out.println("\n");
    }

    /**
     * Helper to find outlet name from code using DataLoad.
     * Keeps Model.java clean.
     */
    private static String getOutletName(String code) {
        return DataLoad.allOutlets.stream()
                .filter(o -> o.getOutletCode().equalsIgnoreCase(code))
                .map(Outlet::getOutletName)
                .findFirst()
                .orElse(code); // Fallback to code if name not found (e.g. C60)
    }
}