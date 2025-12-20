package com.goldenhour.ui;

import com.goldenhour.service.stocksys.StockCountService;
import com.goldenhour.service.stocksys.StockMovementService;
import com.goldenhour.service.stocksys.StockSearch;
import com.goldenhour.service.stocksys.EditStock;

import java.util.Scanner;

public class StockUI {
    private static Scanner sc = new Scanner(System.in);

    public static void openStockMenu() {
        while (true) {
            System.out.println("\n=== Stock Management ===");
            System.out.println("1. Morning Stock Count");
            System.out.println("2. Night Stock Count");
            System.out.println("3. Stock In");
            System.out.println("4. Stock Out");
            System.out.println("5. Search Stock");
            System.out.println("6: Edit Stock");
            System.out.println("6. Back to Main Menu");
            System.out.print("Select: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> StockCountService.performStockCount("Morning");
                case "2" -> StockCountService.performStockCount("Night");
                case "3" -> StockMovementService.stockInOut("Stock In");
                case "4" -> StockMovementService.stockInOut("Stock Out");
                case "5" -> StockSearch.searchStock(sc);
                case "6" -> EditStock.editStock(sc);
                case "7" -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }
}
