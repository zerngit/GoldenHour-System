package com.goldenhour.storage;

import com.goldenhour.categories.Sales;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReceiptHandler {

    /**
     * Appends receipt text to a daily file.
     * Returns the filename used.
     */
    public static String appendReceipt(String receiptText) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        File dir = new File("data/receipts");
        if (!dir.exists()) dir.mkdirs();
        
        // Changed prefix to 'sales_' to match your sample output
        String shortFilename = "receipts_" + date + ".txt";
        String fullPath = "data/receipts/" + shortFilename;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fullPath, true))) {
            bw.write(receiptText);
            bw.newLine();
            bw.write("--------------------------------------------------"); // Separator between receipts
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error writing receipt file.");
        }
        return shortFilename;
    }

    public static String appendSalesReceipt(String receiptText, String filePrefix) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        File dir = new File("data/receipts");
        if (!dir.exists()) dir.mkdirs();

        // Dynamically creates "sales_2024-10-13.txt" or "stock_2024-10-13.txt"
        String shortFilename = filePrefix + "_" + date + ".txt";
        String fullPath = "data/receipts/" + shortFilename;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fullPath, true))) {
            bw.write(receiptText);
            bw.newLine();
            bw.write("--------------------------------------------------"); 
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error writing receipt file: " + e.getMessage());
        }
        return shortFilename;
    }
    
}