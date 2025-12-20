package com.goldenhour.service.loginregister;

import com.goldenhour.categories.Employee;
import com.goldenhour.storage.CSVHandler;
import com.goldenhour.dataload.DataLoad;

import java.util.*;

public class RegistrationService {

    public static void registerEmployee(String id, String name, String role, String password) {

        List<Employee> employees = DataLoad.allEmployees;

        for (Employee e : employees) {
            if (e.getId().equals(id)) {
                System.out.println("Error: Employee ID already exists!");
                return;
            }
        }

        employees.add(new Employee(id, name, role, password));
        CSVHandler.writeEmployees(employees);
        System.out.println("\nEmployee \u001B[32msuccessfully registered!\u001B[0m");
    }
}
