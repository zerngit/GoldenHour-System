package com.goldenhour.ui;

import com.goldenhour.service.attendance.AttendanceService;
import java.util.Scanner;

public class AttendanceUI {
    private static Scanner sc = new Scanner(System.in);

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
                    if (com.goldenhour.service.loginregister.AuthService.getCurrentUser() != null) {
                        AttendanceService.viewAttendance(
                            com.goldenhour.service.loginregister.AuthService.getCurrentUser().getId());
                    }
                }
                case "4" -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }
}
