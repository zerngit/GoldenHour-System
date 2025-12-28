package com.goldenhour.categories;

import java.time.LocalDate;
import java.util.Map;

public class DailySalesSummary {
    private LocalDate date;
    private String outletScope; // "All" or Specific Outlet Name
    private double grandTotal;
    private int transactionCount;
    private Map<String, Double> paymentMethodBreakdown; // e.g., "Cash" -> 500.00

    public DailySalesSummary(LocalDate date, String outletScope, double grandTotal, int transactionCount, Map<String, Double> breakdown) {
        this.date = date;
        this.outletScope = outletScope;
        this.grandTotal = grandTotal;
        this.transactionCount = transactionCount;
        this.paymentMethodBreakdown = breakdown;
    }

    // --- Getters for your teammate ---
    public LocalDate getDate() { return date; }
    public String getOutletScope() { return outletScope; }
    public double getGrandTotal() { return grandTotal; }
    public int getTransactionCount() { return transactionCount; }
    public Map<String, Double> getPaymentMethodBreakdown() { return paymentMethodBreakdown; }

    // Helper to print a quick console summary (for debugging)
    @Override
    public String toString() {
        return String.format("Report [%s] | Scope: %s | Txns: %d | Total: RM %.2f", 
            date, outletScope, transactionCount, grandTotal);
    }
}
