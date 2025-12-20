package com.goldenhour.service.stocksys;

import com.goldenhour.categories.Model;
import com.goldenhour.categories.Outlet;
import com.goldenhour.utils.TimeUtils;
import com.goldenhour.dataload.DataLoad;

import java.util.*;


public class StockCountService {

    public static void performStockCount(String period) {
        Scanner sc = new Scanner(System.in);
        List<Model> stockList = DataLoad.allModels;
        List<Outlet> outlets = DataLoad.allOutlets;

        int totalChecked = 0;
        int tallyCorrect = 0;
        int mismatches = 0;
 
        System.out.println("\n=== " + period + " Stock Count ===");
        System.out.println("Date: " + TimeUtils.getDate());
        System.out.println("Time: " + TimeUtils.getTime());

        for (Model s : stockList) {
            for (Outlet o : outlets) {
                System.out.println("Outlet: " + o.getOutletCode() + " - " + o.getOutletName());
                System.out.print("Model: " + s.getModelCode() + " – Counted: ");
                int counted = Integer.parseInt(sc.nextLine());
                System.out.println("Store Record: " + s.getStock(o.getOutletCode()));

                if (counted == s.getStock(o.getOutletCode())) {
                    System.out.println("\u001B[32mStock tally correct\u001B[0m.");
                    tallyCorrect++;
                } else {
                    System.out.println("\u001B[31m! Mismatch detected (" + Math.abs(counted - s.getStock(o.getOutletCode())) + " unit difference)\u001B[0m");
                    mismatches++;
                }
            }

            totalChecked++;
        }

        System.out.println("\nTotal Models Checked: " + totalChecked);
        System.out.println("Tally \u001B[32mCorrect\u001B[0m: " + tallyCorrect);
        System.out.println("\u001B[31mMismatches\u001B[0m: " + mismatches);

        if (mismatches > 0) {
            System.out.println("\u001B[31mWarning: Please verify stock\u001B[0m.");
        }
        System.out.println(period + " stock count \u001B[32mcompleted\u001B[0m.");
        //sc.close();
    }
}