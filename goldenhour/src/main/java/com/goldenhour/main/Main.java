package com.goldenhour.main;

import com.goldenhour.ui.LoginUI;

import com.goldenhour.dataload.DataLoad;
import com.goldenhour.storage.CSVHandler;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to Golden Hour System!");
        System.out.println("Loading data...");
        DataLoad.allEmployees = CSVHandler.readEmployees();
        DataLoad.allModels = CSVHandler.readStock();
        DataLoad.allOutlets = CSVHandler.readOutlets(); 
        DataLoad.allAttendance = CSVHandler.readAttendance();
        ///DataLoad.allSales = CSVHandler.readSales();
        System.out.println("Data loaded successfully.\n");

        System.out.println();

        LoginUI.start();
    }
}
