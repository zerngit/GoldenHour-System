package com.goldenhour.main;

import com.goldenhour.categories.*;
import com.goldenhour.storage.CSVHandler;
import com.goldenhour.storage.DatabaseHandler;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncSQLtoCSV {

    public static void main(String[] args) {
        System.out.println("Starting Migration from SQLite to CSV...");

        // 1. Export Employees
        exportEmployees();

        // 2. Export Outlets
        exportOutlets();

        // 3. Export Models & Stock (The Tricky Part: Reconstruction)
        exportModelsAndStock();

        // 4. Export Sales
        exportSales();

        // 5. Export Attendance
        exportAttendance();

        System.out.println("🎉 REVERSE MIGRATION COMPLETE! CSV files are now synced with Database.");
    }

    private static void exportEmployees() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees";

        try (Connection conn = DatabaseHandler.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Reconstruct Employee Object
                Employee e = new Employee(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("role"),
                    rs.getString("password")
                );
                list.add(e);
            }
            // WRITE TO CSV
            CSVHandler.writeEmployees(list); 
            System.out.println("✅ Exported " + list.size() + " Employees to CSV.");

        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static void exportOutlets() {
        List<Outlet> list = new ArrayList<>();
        String sql = "SELECT * FROM outlets";

        try (Connection conn = DatabaseHandler.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Outlet o = new Outlet(
                    rs.getString("code"),
                    rs.getString("name")
                );
                list.add(o);
            }
            CSVHandler.writeOutlets(list);
            System.out.println("✅ Exported " + list.size() + " Outlets to CSV.");

        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static void exportModelsAndStock() {
        // Map to hold models by code so we can attach stock to them
        Map<String, Model> modelMap = new HashMap<>();
        
        // JOIN models and stock tables to get everything in one go
        String sql = "SELECT m.code, m.price, s.outlet_code, s.quantity " +
                     "FROM models m " +
                     "LEFT JOIN stock s ON m.code = s.model_code";

        try (Connection conn = DatabaseHandler.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String modelCode = rs.getString("code");
                double price = rs.getDouble("price");
                String outletCode = rs.getString("outlet_code");
                int qty = rs.getInt("quantity");

                // Get existing model or create new one if we haven't seen it yet
                Model m = modelMap.computeIfAbsent(modelCode, k -> {
                    Model newModel = new Model();
                    newModel.setModelCode(k);
                    newModel.setPrice(price);
                    // Initialize the stock map if your constructor doesn't do it
                    newModel.setStockPerOutlet(new HashMap<>()); 
                    return newModel;
                });

                // Add stock entry if outlet exists (LEFT JOIN might return nulls for outlet if no stock)
                if (outletCode != null) {
                    m.getStockPerOutlet().put(outletCode, qty);
                }
            }

            // Convert Map values to List
            List<Model> list = new ArrayList<>(modelMap.values());
            CSVHandler.writeStock(list);
            System.out.println("✅ Exported " + list.size() + " Models (with stock) to CSV.");

        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static void exportSales() {
        List<Sales> list = new ArrayList<>();
        String sql = "SELECT * FROM sales";

        try (Connection conn = DatabaseHandler.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Sales s = new Sales();
                s.setDate(rs.getString("date"));
                s.setTime(rs.getString("time"));
                s.setCustomerName(rs.getString("customer_name"));
                s.setModel(rs.getString("model_code"));
                s.setQuantity(rs.getInt("quantity"));
                s.setSubtotal(rs.getDouble("subtotal"));
                s.setTransactionMethod(rs.getString("method"));
                s.setEmployee(rs.getString("employee_name"));
                
                // IMPORTANT: Don't forget the new columns!
                s.setOutletCode(rs.getString("outlet_code"));
                s.setEmployeeId(rs.getString("employee_id"));
                
                list.add(s);
            }
            CSVHandler.writeSales(list);
            System.out.println("✅ Exported " + list.size() + " Sales records to CSV.");

        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static void exportAttendance() {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance";

        try (Connection conn = DatabaseHandler.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Attendance a = new Attendance();
                a.setEmployeeId(rs.getString("emp_id"));
                a.setEmployeeName(rs.getString("emp_name"));
                a.setDate(rs.getString("date"));
                a.setClockInTime(rs.getString("clock_in"));
                a.setClockOutTime(rs.getString("clock_out"));
                a.setHoursWorked(rs.getDouble("hours_worked"));
                
                list.add(a);
            }
            CSVHandler.writeAttendance(list);
            System.out.println("✅ Exported " + list.size() + " Attendance records to CSV.");

        } catch (SQLException e) { e.printStackTrace(); }
    }
}