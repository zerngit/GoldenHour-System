package com.goldenhour.ui;


import com.goldenhour.service.loginregister.AuthService;
import com.goldenhour.service.loginregister.RegistrationService;


import java.util.Scanner;

public class LoginUI {
    private static Scanner sc = new Scanner(System.in);

    public static void start() {
        while (true) {
            System.out.println("\n=== Employee Login ===");
            System.out.print("Enter User ID: ");
            String id = sc.nextLine();
            System.out.print("Enter Password: ");
            String password = sc.nextLine();

            if (AuthService.login(id, password)) {
                // If the logged-in user is a Manager show manager menu; otherwise show employee menu
                if (AuthService.getCurrentUser() != null &&
                        AuthService.getCurrentUser().getRole().equalsIgnoreCase("Manager")) {
                    ManagerMenu();
                } else {
                    EmployeeMenu();
                }
            }
        }
    }
    
    private static void ManagerMenu() {
        while (true) {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. Register New Employee");
            System.out.println("2. Attendance");
            System.out.println("3. Stock Management");
            System.out.println("4. Sales Management");
            System.out.println("5. Logout");
            System.out.print("Choose an option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter Employee Name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter Employee ID: ");
                    String id = sc.nextLine();
                    System.out.print("Set Password: ");
                    String pw = sc.nextLine();
                    System.out.print("Set Role: ");
                    String role = sc.nextLine();
                    RegistrationService.registerEmployee(id, name, role, pw);
                    break;

                case "2":
                    AttendanceUI.openAttendanceMenu();
                    return;

                case "3":
                    StockUI.openStockMenu();
                    return;
                
                case "4":
                    SalesUI.openSalesMenu();
                    break;

                case "5":
                    AuthService.logout();
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    
    private static void EmployeeMenu() {
        while (true) {
            System.out.println("\n=== Main Menu ===");
            // Employee menu: no registration option for non-managers
            System.out.println("1. Attendance");
            System.out.println("2. Stock Management");
            System.out.println("3. Sales Management");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    AttendanceUI.openAttendanceMenu();
                    break;
                
                case "2":
                    StockUI.openStockMenu();
                    break;

                case "3":
                    SalesUI.openSalesMenu();
                    break;

                case "4":
                    AuthService.logout();
                    return;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}