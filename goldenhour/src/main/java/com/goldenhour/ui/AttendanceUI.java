package com.goldenhour.ui;

import com.goldenhour.service.attendance.AttendanceService;
import java.util.Scanner;

/**
 * AttendanceUI
 * Console-based user interface for attendance functionality.
 *
 * Responsibilities:
 * - Present attendance menu options to the user
 * - Forward user actions to `AttendanceService` for business logic
 * - Ensure only authenticated users can view their records
 */
public class AttendanceUI {
    // Scanner used for reading user input from the console
    private static Scanner sc = new Scanner(System.in);

    /**
     * Opens the attendance management menu and handles simple navigation.
     * Menu options:
     * 1 - Clock In: Delegate to `AttendanceService.clockIn()` to create a record
     * 2 - Clock Out: Delegate to `AttendanceService.clockOut()` to finish a shift
     * 3 - View My Attendance: Show records for the currently authenticated user
     * 4 - Back to Main Menu: Exit this menu loop
     */
    public static void openAttendanceMenu() {
        while (true) {
            System.out.println("\n=== Attendance Management ===");
            System.out.println("1. Clock In");
            System.out.println("2. Clock Out");
            System.out.println("3. View My Attendance");
            System.out.println("4. Back to Main Menu");
            System.out.print("Select: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> AttendanceService.clockIn();
                case "2" -> AttendanceService.clockOut();
                case "3" -> {
                    // Only attempt to display attendance if a user is logged in
                    if (com.goldenhour.service.loginregister.AuthService.getCurrentUser() != null) {
                        AttendanceService.viewAttendance(
                            com.goldenhour.service.loginregister.AuthService.getCurrentUser().getId());
                    } else {
                        // Helpful feedback when no user session exists
                        System.out.println("No user logged in.");
                    }
                }
                case "4" -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }
}