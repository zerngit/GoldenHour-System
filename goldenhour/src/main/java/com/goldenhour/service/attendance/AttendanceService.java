package com.goldenhour.service.attendance;

import com.goldenhour.categories.Attendance;
import com.goldenhour.categories.Employee;
import com.goldenhour.categories.Outlet;
import com.goldenhour.dataload.DataLoad;
import com.goldenhour.service.loginregister.AuthService;
import com.goldenhour.storage.CSVHandler;
import com.goldenhour.storage.DatabaseHandler;
import com.goldenhour.utils.TimeUtils;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * AttendanceService Class
 * Handles all employee attendance-related operations including clock in/out, time calculation, and attendance record retrieval.
 * Integrates with database and CSV storage systems.
 */
public class AttendanceService {
    // Scanner for console input during outlet selection
    private static Scanner sc = new Scanner(System.in);

    /**
     * Record employee clock-in for the current day
     * Validates that employee hasn't already clocked in today
     * Requires outlet selection and stores attendance record in database and CSV
     */
    public static void clockIn() {
        // Retrieve currently logged-in employee from authentication service
        Employee emp = AuthService.getCurrentUser();
        if (emp == null) {
            System.out.println("No user logged in.");
            return;
        }

        // Get current date and time for attendance record
        String today = TimeUtils.getDate();
        String clockInTime = TimeUtils.getTime();
        // Load all existing attendance records from data
        List<Attendance> records = DataLoad.allAttendance;

        // Validate: Check if employee already clocked in today
        for (Attendance a : records) {
            // Search for existing clock-in record with same employee ID and date
            if (a.getEmployeeId().equals(emp.getId()) && a.getDate().equals(today) 
                    && a.getClockInTime() != null) {
                System.out.println("Error: You have already clocked in today at " + a.getClockInTime());
                return;
            }
        }

        // Prompt employee to select their work outlet/location
        String outletCode = selectOutlet();
        if (outletCode == null) {
            System.out.println("Outlet selection cancelled.");
            return;
        }

        // Retrieve the outlet name for display purposes
        String outletName = getOutletName(outletCode);

        // Create new attendance record with clock-in information
        Attendance newRecord = new Attendance(emp.getId(), emp.getName(), today, clockInTime, outletCode);
        // Add to in-memory records list
        records.add(newRecord);
        // Persist to database
        DatabaseHandler.saveAttendance(newRecord);
        // Export updated records to CSV file
        CSVHandler.writeAttendance(records);

        // Display formatted clock-in success confirmation
        System.out.println("\n=== Attendance Clock In ===");
        System.out.println("Employee ID: " + emp.getId());
        System.out.println("Name: " + emp.getName());
        System.out.println("Outlet: " + outletCode + " (" + outletName + ")");
        System.out.println();
        System.out.println("Clock In \u001B[32mSuccessful!\u001B[0m"); // Green color for success
        System.out.println("Date: " + today);
        System.out.println("Time: " + clockInTime);
    }

    /**
     * Record employee clock-out for the current day
     * Validates that employee has already clocked in
     * Calculates hours worked and updates attendance record
     */
    public static void clockOut() {
        // Retrieve currently logged-in employee
        Employee emp = AuthService.getCurrentUser();
        if (emp == null) {
            System.out.println("No user logged in.");
            return;
        }

        // Get current date and time for clock-out record
        String today = TimeUtils.getDate();
        String clockOutTime = TimeUtils.getTime();
        // Load all existing attendance records
        List<Attendance> records = DataLoad.allAttendance;

        // Search for today's clock-in record for this employee
        Attendance todayRecord = null;
        for (Attendance a : records) {
            // Find today's attendance record for the employee
            if (a.getEmployeeId().equals(emp.getId()) && a.getDate().equals(today)) {
                todayRecord = a;
                break;
            }
        }

        // Validate: Ensure employee has clocked in today
        if (todayRecord == null) {
            System.out.println("Error: No clock-in record found for today. Please clock in first.");
            return;
        }

        // Validate: Ensure employee hasn't already clocked out
        if (todayRecord.getClockOutTime() != null) {
            System.out.println("Error: You have already clocked out today at " + todayRecord.getClockOutTime());
            return;
        }

        // Calculate total hours worked between clock-in and clock-out
        double hoursWorked = calculateHours(todayRecord.getClockInTime(), clockOutTime);
        
        // Update the attendance record with clock-out time and calculated hours
        todayRecord.setClockOutTime(clockOutTime);
        todayRecord.setHoursWorked(hoursWorked);

        // Persist updated record to database
        DatabaseHandler.updateAttendance(todayRecord);
        
        // Export updated records to CSV file
        CSVHandler.writeAttendance(records);

        // Retrieve outlet code from clock-in record (fallback to default if null)
        String outletCode = todayRecord.getOutletCode() != null ? todayRecord.getOutletCode() : "C60";
        // Get outlet name for display
        String outletName = getOutletName(outletCode);

        // Display formatted clock-out success confirmation
        System.out.println("\n=== Attendance Clock Out ===");
        System.out.println("Employee ID: " + emp.getId());
        System.out.println("Name: " + emp.getName());
        System.out.println("Outlet: " + outletCode + " (" + outletName + ")");
        System.out.println();
        System.out.println("Clock Out \u001B[32mSuccessful!\u001B[0m"); // Green color for success
        System.out.println("Date: " + today);
        System.out.println("Time: " + clockOutTime);
        System.out.printf("Total Hours Worked: %.1f hours\n", hoursWorked);
    }

    /**
     * Calculate the hours worked between clock-in and clock-out times
     * Handles time parsing and duration calculation
     * 
     * @param clockIn   Clock-in time string (HH:MM a format)
     * @param clockOut  Clock-out time string (HH:MM a format)
     * @return Hours worked as a decimal number, or 0.0 if calculation fails
     */
    private static double calculateHours(String clockIn, String clockOut) {
        try {
            // Parse both times using fixed date to calculate duration
            // (date doesn't matter for duration calculation)
            LocalDateTime inTime = LocalDateTime.parse("2025-01-01 " + clockIn, 
                                   DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"));
            LocalDateTime outTime = LocalDateTime.parse("2025-01-01 " + clockOut, 
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"));
            
            // Calculate minutes between times and convert to hours
            long minutes = java.time.temporal.ChronoUnit.MINUTES.between(inTime, outTime);
            return minutes / 60.0;
        } catch (Exception e) {
            // Return 0 hours if parsing or calculation fails
            return 0.0;
        }
    }

    /**
     * Display list of outlets and prompt user to select one
     * Allows selection by outlet number or code
     * 
     * @return Selected outlet code, or null if selection is cancelled
     */
    private static String selectOutlet() {
        // Load all available outlets
        List<Outlet> outlets = DataLoad.allOutlets;
        
        // Display menu of available outlets
        System.out.println("\nSelect Outlet:");
        for (int i = 0; i < outlets.size(); i++) {
            System.out.println((i + 1) + ". " + outlets.get(i).getOutletCode() + 
                             " - " + outlets.get(i).getOutletName());
        }
        System.out.print("Enter outlet code/name: ");
        
        // Get user input
        String input = sc.nextLine().trim();
        
        // Attempt to parse input as menu number first
        try {
            int choice = Integer.parseInt(input);
            // Validate menu choice is within range
            if (choice > 0 && choice <= outlets.size()) {
                return outlets.get(choice - 1).getOutletCode();
            }
        } catch (NumberFormatException e) {
            // Not a number, selection invalid
        }
        return null;  // Invalid selection
    }

    /**
     * Look up outlet name by outlet code
     * 
     * @param outletCode The outlet code to search for
     * @return Outlet name if found, "Unknown" otherwise
     */
    private static String getOutletName(String outletCode) {
        // Search through all outlets for matching code
        for (Outlet o : DataLoad.allOutlets) {
            if (o.getOutletCode().equals(outletCode)) {
                return o.getOutletName();
            }
        }
        return "Unknown";  // Return default if not found
    }

    /**
     * Display all attendance records for a specific employee
     * Shows all clock-in/out records and hours worked
     * 
     * @param employeeId The employee ID to retrieve records for
     */
    public static void viewAttendance(String employeeId) {
        // Load all attendance records
        List<Attendance> records = DataLoad.allAttendance;
        boolean found = false;

        // Display header with employee ID
        System.out.println("\n=== Attendance Records for " + employeeId + " ===");
        // Search and display all records matching the employee ID
        for (Attendance a : records) {
            if (a.getEmployeeId().equals(employeeId)) {
                System.out.println(a);
                found = true;
            }
        }

        // Display message if no records found
        if (!found) {
            System.out.println("No attendance records found.");
        }
    }
}
