package com.goldenhour.dataload;

import com.goldenhour.categories.*;
import com.goldenhour.storage.CSVHandler;
import java.util.List;

public class DataLoad {
    public static List<Model> allModels = CSVHandler.readStock();
    public static List<Employee> allEmployees = CSVHandler.readEmployees();
    public static List<Outlet> allOutlets = CSVHandler.readOutlets();
    public static List<Sales> allSales = CSVHandler.readSales();
    public static List<Attendance> allAttendance = CSVHandler.readAttendance();
}
