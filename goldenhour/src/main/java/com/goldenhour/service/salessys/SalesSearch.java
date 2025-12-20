package com.goldenhour.service.salessys;

import com.goldenhour.categories.Sales;
import com.goldenhour.dataload.DataLoad;
import java.util.List;
import java.util.stream.Collectors;

public class SalesSearch {

    /**
     * Smart search: checks Customer Name, Model, or Date for the given keyword.
     */
    public List<Sales> searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return List.of();

        String lowerKey = keyword.trim().toLowerCase();

        return DataLoad.allSales.stream()
                .filter(s -> 
                       // Check Customer Name (Partial match)
                       s.getCustomerName().toLowerCase().contains(lowerKey) ||
                       
                       // Check Model Code/Name (Exact or Partial)
                       s.getModel().toLowerCase().contains(lowerKey) ||
                       
                       // Check Date (Exact match)
                       s.getDate().equals(keyword.trim())
                )
                .collect(Collectors.toList());
    }

    public void printSalesList(List<Sales> list) {
        System.out.println("\u001B[32mSearching...\u001B[0m");

        System.out.println();
        
        if (list == null || list.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        for (Sales s : list) {
            System.out.println("Sales Record Found:");
            System.out.println("Date: " + s.getDate() + " Time: " + s.getTime());
            System.out.println("Customer: " + s.getCustomerName());
            System.out.println("Item(s): " + s.getModel() + " Quantity: " + s.getQuantity());
            System.out.println("Total: RM" + s.getSubtotal());
            System.out.println("Transaction Method: " + s.getTransactionMethod());
            System.out.println("Employee: " + s.getEmployee());
            
            String status = "Transaction verified.";
            if ("PENDING".equalsIgnoreCase(s.getTransactionMethod())) {
                status = "Pending Payment";
            }
            System.out.println("Status: " + status);
            System.out.println("----------------------------------------");
        }
    }
}