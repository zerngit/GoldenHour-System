package com.goldenhour.categories;

//import java.util.ArrayList;
import java.util.HashMap;
//import java.util.List;
import java.util.Map;

public class Model {
    private String modelCode;
    private double price;
    private Map<String, Integer> stockPerOutlet; // key: outlet code, value: quantity

    public Model(String modelCode, double price) {
        this.modelCode = modelCode;
        this.price = price;
        this.stockPerOutlet = new HashMap<>();
    }

    public void setStock(String outletCode, int quantity) {
        stockPerOutlet.put(outletCode, quantity);

    }

    public int getStock(String outletCode) {
        return stockPerOutlet.getOrDefault(outletCode, 0);
    }

    public String getModelCode() {
        return modelCode;
    }

    public double getPrice() {
        return price;
    }


    public Map<String, Integer> getStockPerOutlet() {
        return stockPerOutlet;
    }

    // For debugging
    @Override
    public String toString() {
        return "Model{" +
                "modelCode='" + modelCode + '\'' +
                ", price=" + price +
                ", stockPerOutlet=" + stockPerOutlet +
                '}';
    }

    public static Model fromCSV(String line, String[] outletCodes) {
        String[] data = line.split(",");
        String modelCode = data[0];
        double price = Double.parseDouble(data[1]);
        Model model = new Model(modelCode, price);

        // Starting from index 2, each value corresponds to stock quantity for each outlet
        // The outletCodes array provides the mapping of index to outlet code
        // Loop through the stock quantities and assign them to the correct outlet
        for (int i = 2; i < data.length; i++) {

            int outletIndex = i-2;
            
            if (outletIndex < outletCodes.length) {
                String outletCode = outletCodes[outletIndex];
                int quantity = Integer.parseInt(data[i]);
                model.setStock( outletCode, quantity);
            }
        }

        return model;
    }

    public String toCSV(String[] outletCodes) {

        // Since the stockPerOutlet keys are dynamic, we need to serialize them in a way that matches the CSV structure
        // String.join cannot be used directly here as we need to ensure the order of outlets matches the CSV header
        // We are given two options here :
        // 1. Stringbuilder , then append each key-value pair in order
        // 2. Loop stockPerOutlet.get(outlet) + "," for each outlet in known order
      
        // We use StringBuilder here instead of String.join to handle dynamic keys
        // Because String is immutable, looping adding comma method will create String object everytime it runs O(n^2) time complexity
        // While StringBuilder is mutable, it appends in place O(n) time complexity

        StringBuilder sb = new StringBuilder();
        sb.append(modelCode).append(",").append(price);
        for (String code : outletCodes) {
            sb.append(",").append(getStock(code));
        }
        return sb.toString();
    }
}