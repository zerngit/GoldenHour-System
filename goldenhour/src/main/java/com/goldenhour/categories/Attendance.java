package com.goldenhour.categories;

public class Attendance {
    private String employeeId;
    private String employeeName;
    private String date;
    private String clockInTime;
    private String clockOutTime;
    private double hoursWorked;

    // Constructor for creating new attendance record (clock in - 4 parameters)
    public Attendance(String employeeId, String employeeName, String date, String clockInTime) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.date = date;
        this.clockInTime = clockInTime;
        this.clockOutTime = null;
        this.hoursWorked = 0.0;
    }

    // Full constructor (6 parameters - for loading from CSV)
    public Attendance(String employeeId, String employeeName, String date, String clockInTime, String clockOutTime, double hoursWorked) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.date = date;
        this.clockInTime = clockInTime;
        this.clockOutTime = clockOutTime;
        this.hoursWorked = hoursWorked;
    }

    public String getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public String getDate() { return date; }
    public String getClockInTime() { return clockInTime; }
    public String getClockOutTime() { return clockOutTime; }
    public double getHoursWorked() { return hoursWorked; }

    public void setClockOutTime(String clockOutTime) { this.clockOutTime = clockOutTime; }
    public void setHoursWorked(double hoursWorked) { this.hoursWorked = hoursWorked; }

    public String toCSV() {
        return String.join(",", employeeId, employeeName, date, 
                          clockInTime, (clockOutTime != null ? clockOutTime : ""), 
                          String.valueOf(hoursWorked));
    }

    public static Attendance fromCSV(String line) {
        
        String[] data = line.split(",");
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i].trim();
        }
        String clockOut = data[4].isEmpty() ? null : data[4];
        return new Attendance(data[0], data[1], data[2], data[3], clockOut, 
                            Double.parseDouble(data[5]));
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | Clock In: %s | Clock Out: %s | Hours: %.1f",
                employeeId, employeeName, date, clockInTime, 
                (clockOutTime != null ? clockOutTime : "---"), hoursWorked);
    }
}

