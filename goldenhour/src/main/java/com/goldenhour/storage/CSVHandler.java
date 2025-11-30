package com.goldenhour.storage;

import java.io.*;
import java.util.*;

import com.goldenhour.categories.Attendance;
import com.goldenhour.categories.Employee;
import com.goldenhour.categories.Model;
import com.goldenhour.categories.Outlet;

public class CSVHandler {
    // private static final String EMPLOYEE_FILE = "data/employee.csv"
    // private static final String STOCK_FILE = "data/model.csv"
    // private static final String OUTLET_FILE = "data/outlet.csv"
    // private static final String ATTENDANCE_FILE = "data/attendance.csv"
    private static final String EMPLOYEE_FILE = "C:\\FOP WIX1002\\GoldenHour-System\\data\\employee.csv";
    private static final String STOCK_FILE = "C:\\FOP WIX1002\\GoldenHour-System\\data\\model.csv";
    private static final String OUTLET_FILE = "C:\\FOP WIX1002\\GoldenHour-System\\data\\outlet.csv";
    private static final String ATTENDANCE_FILE = "C:\\FOP WIX1002\\GoldenHour-System\\data\\attendance.csv";

    public static List<Employee> readEmployees() {
        List<Employee> employees = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(EMPLOYEE_FILE))) {
            //br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                employees.add(Employee.fromCSV(line));
            }
        } catch (IOException e) {
            System.out.println("Error reading employee file.");
        }
        return employees;
    }

    public static void writeEmployees(List<Employee> employees) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(EMPLOYEE_FILE))) {
            for (Employee emp : employees) {
                bw.write(emp.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing employee file.");
        }
    }

    public static List<Attendance> readAttendance() {
        List<Attendance> attendance = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ATTENDANCE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    attendance.add(Attendance.fromCSV(line));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading attendance file.");
        }
        return attendance;
    }

    public static void writeAttendance(List<Attendance> attendance) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ATTENDANCE_FILE))) {
            for (Attendance att : attendance) {
                bw.write(att.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing attendance file.");
        }
    }

    public static List<Model> readStock() {
        List<Model> stockList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(STOCK_FILE))) {
            String header = br.readLine(); // first line: Model,Price,C60,C61,...
            String[] outlets = header.split(",");
            // skip first 2 columns (Model, Price)
            String[] outletCodes = java.util.Arrays.copyOfRange(outlets, 2, outlets.length);

            String line;
            while ((line = br.readLine()) != null) {
                stockList.add(Model.fromCSV(line,outletCodes));
                //System.out.println("Loaded stock item from CSV: " + line);
            }
        } catch (IOException e) {
            System.out.println("Error reading stock file.");
        }
        return stockList;
    }

    public static void writeStock(List<Model> stockList) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/model.csv"))) {

            for (Model s : stockList) {
                bw.write(s.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing stock file.");
        }
    }

    public static List<Outlet> readOutlets() {
        List<Outlet> outlets = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(OUTLET_FILE))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                outlets.add(Outlet.fromCSV(line));
            }
        } catch (IOException e) {
            System.out.println("Error reading outlet file.");
        }
        return outlets;

    }
}

