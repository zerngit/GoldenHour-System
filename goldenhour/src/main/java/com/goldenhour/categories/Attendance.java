package com.goldenhour.categories;

/**
 * Attendance Class
 * Represents an employee's daily attendance record including clock in/out times
 * and hours worked at a specific outlet location.
 * Provides methods for data persistence (CSV serialization/deserialization).
 */
public class Attendance {
    // Employee identification
    private String employeeId;      // Unique employee identifier
    private String employeeName;    // Employee's name
    
    // Time tracking information
    private String date;            // Date of attendance (format: YYYY-MM-DD)
    private String clockInTime;     // Time employee started shift (HH:MM:SS)
    private String clockOutTime;    // Time employee ended shift (HH:MM:SS, nullable)
    private double hoursWorked;     // Total hours worked during the shift
    
    // Location information
    private String outletCode;      // Outlet location identifier

    /**
     * Default constructor
     * Creates an empty Attendance object with null/default values
     */
    public Attendance() {}

    /**
     * Constructor for creating a new attendance record (clock in)
     * Used when an employee first clocks in at the start of their shift
     * 
     * @param employeeId    Unique identifier for the employee
     * @param employeeName  Name of the employee
     * @param date          Date of attendance
     * @param clockInTime   Time when employee clocks in
     * @param outletCode    Location/outlet code where work is performed
     */
    public Attendance(String employeeId, String employeeName, String date, String clockInTime, String outletCode) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.date = date;
        this.clockInTime = clockInTime;
        this.clockOutTime = null;      // Not yet clocked out
        this.hoursWorked = 0.0;        // Hours will be calculated upon clock out
        this.outletCode = outletCode;
    }

    /**
     * Full constructor (for loading existing records from CSV)
     * Contains all attendance information including completed shift details
     * 
     * @param employeeId    Unique identifier for the employee
     * @param employeeName  Name of the employee
     * @param date          Date of attendance
     * @param clockInTime   Time when employee clocked in
     * @param clockOutTime  Time when employee clocked out
     * @param hoursWorked   Total hours worked during the shift
     * @param outletCode    Location/outlet code
     */
    public Attendance(String employeeId, String employeeName, String date, String clockInTime, String clockOutTime, double hoursWorked, String outletCode) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.date = date;
        this.clockInTime = clockInTime;
        this.clockOutTime = clockOutTime;
        this.hoursWorked = hoursWorked;
        this.outletCode = outletCode;
    }


    // ===== GETTER METHODS =====
    /** Retrieve the employee's unique ID */
    public String getEmployeeId() { return employeeId; }
    
    /** Retrieve the employee's name */
    public String getEmployeeName() { return employeeName; }
    
    /** Retrieve the date of this attendance record */
    public String getDate() { return date; }
    
    /** Retrieve the clock-in time (start of shift) */
    public String getClockInTime() { return clockInTime; }
    
    /** Retrieve the clock-out time (end of shift), may be null if still clocked in */
    public String getClockOutTime() { return clockOutTime; }
    
    /** Retrieve the total hours worked during this shift */
    public double getHoursWorked() { return hoursWorked; }
    
    /** Retrieve the outlet/location code where work was performed */
    public String getOutletCode() { return outletCode; }

    // ===== SETTER METHODS =====
    /** Update the clock-out time when employee ends their shift */
    public void setClockOutTime(String clockOutTime) { this.clockOutTime = clockOutTime; }
    
    /** Update the total hours worked (usually calculated after clock out) */
    public void setHoursWorked(double hoursWorked) { this.hoursWorked = hoursWorked; }
    
    /** Update the attendance date */
    public void setDate(String date) { this.date = date; }
    
    /** Update the clock-in time */
    public void setClockInTime(String clockInTime) { this.clockInTime = clockInTime; }
    
    /** Update the employee ID */
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    
    /** Update the employee name */
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    
    /** Update the outlet/location code */
    public void setOutletCode(String outletCode) { this.outletCode = outletCode; }
    
    /**
     * Convert Attendance object to CSV format string
     * Used for saving attendance records to CSV files
     * Format: employeeId,employeeName,date,clockInTime,clockOutTime,hoursWorked,outletCode
     * 
     * @return CSV formatted string representation of attendance record
     */
    public String toCSV() {
        // Null-safe conversion: use empty string if clockOutTime or outletCode is null
        return String.join(",", employeeId, employeeName, date, 
                          clockInTime, (clockOutTime != null ? clockOutTime : ""), 
                          String.valueOf(hoursWorked), (outletCode != null ? outletCode : ""));
    }

    /**
     * Parse a CSV line string into an Attendance object
     * Used for loading attendance records from CSV files
     * Handles empty values gracefully (null for optional fields)
     * 
     * @param line CSV formatted string to parse
     * @return Attendance object with data extracted from CSV line
     */
    public static Attendance fromCSV(String line) {
        // Split the CSV line by comma delimiter
        String[] data = line.split(",");
        
        // Trim whitespace from each field
        for (int i = 0; i < data.length; i++) {
            data[i] = data[i].trim();
        }
        
        // Handle null clockOutTime: if field is empty, set to null
        String clockOut = data[4].isEmpty() ? null : data[4];
        
        // Handle optional outletCode: check if field exists and is not empty
        String outlet = (data.length > 6 && !data[6].isEmpty()) ? data[6] : null;
        
        // Create and return new Attendance object using the full constructor
        return new Attendance(data[0], data[1], data[2], data[3], clockOut, 
                            Double.parseDouble(data[5]), outlet);
    }

    /**
     * Generate a formatted string representation of the Attendance record
     * Used for display purposes (logs, UI, console output)
     * 
     * @return Human-readable string with all attendance details
     */
    @Override
    public String toString() {
        return String.format("%s | %s | %s | Outlet: %s | Clock In: %s | Clock Out: %s | Hours: %.1f",
                employeeId, employeeName, date, (outletCode != null ? outletCode : "---"),
                clockInTime, (clockOutTime != null ? clockOutTime : "---"), hoursWorked);
    }
}

