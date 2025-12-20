package com.goldenhour.service.loginregister;

import com.goldenhour.categories.Employee;
import com.goldenhour.dataload.DataLoad;

import java.util.List;

public class AuthService {
    private static Employee currentUser;

    public static boolean login(String id, String password) {
        List<Employee> employees = DataLoad.allEmployees;
        for (Employee emp : employees) {
            if (emp.getId().equals(id) && emp.getPassword().equals(password)) {
                currentUser = emp;
                System.out.println("\nLogin \u001B[32mSuccessful!\u001B[0m");
                System.out.println("Welcome, " + emp.getName() + " (" + emp.getId().substring(0, 3) + ")");
                return true;
            }
        }
        System.out.println("\nLogin \u001B[31mFailed\u001B[31m: " + "\u001B[31m" + "Invalid User ID or Password" + "\u001B[0m");
        return false;
    }

    public static void logout() {
        System.out.println("\n" + currentUser.getName() + " has logged out.");
        currentUser = null;
    }

    public static Employee getCurrentUser() {
        return currentUser;
    }
}
