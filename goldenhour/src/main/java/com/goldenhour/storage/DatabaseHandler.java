package com.goldenhour.storage;

import com.goldenhour.categories.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHandler {

    private static final String URL = "jdbc:sqlite:data/goldenhour.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Database Connection Error: " + e.getMessage());
        }
        return conn;
    }

    // ==========================================
    //          DATABASE SETUP (One-Time)
    // ==========================================
    
    public static void initializeDatabase() {
        String sqlEmployees = "CREATE TABLE IF NOT EXISTS employees ("
                + "id TEXT PRIMARY KEY, "
                + "name TEXT, "
                + "role TEXT, "
                + "password TEXT)";

        String sqlOutlets = "CREATE TABLE IF NOT EXISTS outlets ("
                + "code TEXT PRIMARY KEY, "
                + "name TEXT)";

        String sqlModels = "CREATE TABLE IF NOT EXISTS models ("
                + "code TEXT PRIMARY KEY, "
                + "price REAL)";

        // FIXED: Added 'stock' singular table name consistency
        String sqlStock = "CREATE TABLE IF NOT EXISTS stock ("
                + "model_code TEXT, "
                + "outlet_code TEXT, "
                + "quantity INTEGER, "
                + "PRIMARY KEY (model_code, outlet_code), "
                + "FOREIGN KEY(model_code) REFERENCES models(code), "
                + "FOREIGN KEY(outlet_code) REFERENCES outlets(code))";

        String sqlSales = "CREATE TABLE IF NOT EXISTS sales ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "date TEXT, "
                + "time TEXT, "
                + "customer_name TEXT, "
                + "model_code TEXT, "
                + "quantity INTEGER, "
                + "subtotal REAL, "
                + "method TEXT, "
                + "employee_name TEXT)";

        // FIXED: Added UNIQUE constraint so we can Update attendance easily
        String sqlAttendance = "CREATE TABLE IF NOT EXISTS attendance ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "emp_id TEXT, "
                + "emp_name TEXT, "
                + "date TEXT, "
                + "clock_in TEXT, "
                + "clock_out TEXT, "
                + "hours_worked REAL, "
                + "UNIQUE(emp_id, date))"; 

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(sqlEmployees);
            stmt.execute(sqlOutlets);
            stmt.execute(sqlModels);
            stmt.execute(sqlStock);
            stmt.execute(sqlSales);
            stmt.execute(sqlAttendance);
            
            // System.out.println("Database tables initialized successfully.");

        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }

    // ==========================================
    //              READ METHODS
    // ==========================================

    public static List<Employee> fetchAllEmployees() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Employee(rs.getString("id"), rs.getString("name"), rs.getString("role"), rs.getString("password")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static List<Outlet> fetchAllOutlets() {
        List<Outlet> list = new ArrayList<>();
        String sql = "SELECT * FROM outlets";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Outlet(rs.getString("code"), rs.getString("name")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static List<Model> fetchAllModelsWithStock() {
        List<Model> list = new ArrayList<>();
        Map<String, Model> tempMap = new HashMap<>();

        // 1. Get Models
        String sqlModel = "SELECT * FROM models";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlModel)) {
            while (rs.next()) {
                Model m = new Model(rs.getString("code"), rs.getDouble("price"));
                list.add(m);
                tempMap.put(m.getModelCode(), m);
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // 2. Get Stock
        String sqlStock = "SELECT * FROM stock";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlStock)) {
            while (rs.next()) {
                Model m = tempMap.get(rs.getString("model_code"));
                if (m != null) {
                    m.setStock(rs.getString("outlet_code"), rs.getInt("quantity"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static List<Sales> fetchAllSales() {
        List<Sales> list = new ArrayList<>();
        String sql = "SELECT * FROM sales";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Sales s = new Sales();
                // Depending on your Sales constructor, set these:
                s.setDate(rs.getString("date"));
                s.setTime(rs.getString("time"));
                s.setCustomerName(rs.getString("customer_name"));
                s.setModel(rs.getString("model_code"));
                s.setQuantity(rs.getInt("quantity"));
                s.setSubtotal(rs.getDouble("subtotal"));
                s.setTransactionMethod(rs.getString("method"));
                s.setEmployee(rs.getString("employee_name"));

                // --- NEW FIELDS ---
                // If your DB table doesn't have these columns yet, this will crash!
                // Make sure to add them or handle SQLException.
                try {
                    s.setOutletCode(rs.getString("outlet_code"));
                    s.setEmployeeId(rs.getString("employee_id"));
                } catch (SQLException e) {
                    // Column might not exist in old DB version, set defaults
                    s.setOutletCode("Unknown");
                    s.setEmployeeId("Unknown");
                }

                list.add(s);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static List<Attendance> fetchAllAttendance() {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Attendance a = new Attendance();
                a.setEmployeeId(rs.getString("emp_id"));
                a.setEmployeeName(rs.getString("emp_name"));
                a.setDate(rs.getString("date"));
                a.setClockInTime(rs.getString("clock_in"));
                a.setClockOutTime(rs.getString("clock_out"));
                a.setHoursWorked(rs.getDouble("hours_worked"));
                list.add(a);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ==========================================
    //           WRITE / UPDATE METHODS
    // ==========================================

    public static void saveSale(Sales s) {
        String sql = "INSERT INTO sales(date, time, customer_name, model_code, quantity, subtotal, method, employee_name, outlet_code, employee_id) VALUES(?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, s.getDate());
            pstmt.setString(2, s.getTime());
            pstmt.setString(3, s.getCustomerName());
            pstmt.setString(4, s.getModel());
            pstmt.setInt(5, s.getQuantity());
            pstmt.setDouble(6, s.getSubtotal());
            pstmt.setString(7, s.getTransactionMethod());
            pstmt.setString(8, s.getEmployee());

            // --- NEW VALUES ---
            pstmt.setString(9, s.getOutletCode());
            pstmt.setString(10, s.getEmployeeId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving sale: " + e.getMessage());
        }
    }

    // Uses INSERT OR REPLACE to handle both New Models and Price Updates.
    public static void saveModel(Model m) {
        String sqlModel = "INSERT OR REPLACE INTO models(code, price) VALUES(?, ?)";
        // FIXED: Table name 'stock', not 'stocks'
        String sqlStock = "INSERT OR REPLACE INTO stock(model_code, outlet_code, quantity) VALUES(?, ?, ?)";

        try (Connection conn = connect()) {
            conn.setAutoCommit(false); 

            try (PreparedStatement pstmt = conn.prepareStatement(sqlModel)) {
                pstmt.setString(1, m.getModelCode());
                pstmt.setDouble(2, m.getPrice());
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sqlStock)) {
                for (Map.Entry<String, Integer> entry : m.getStockPerOutlet().entrySet()) {
                    pstmt.setString(1, m.getModelCode());
                    pstmt.setString(2, entry.getKey());
                    pstmt.setInt(3, entry.getValue());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            conn.commit(); 
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    // Uses INSERT OR REPLACE to handle Editing passwords/roles.
    public static void saveEmployee(Employee e) {
        String sql = "INSERT OR REPLACE INTO employees(id, name, role, password) VALUES(?,?,?,?)";
        
        try (Connection conn = connect(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, e.getId());
            pstmt.setString(2, e.getName());
            pstmt.setString(3, e.getRole());
            pstmt.setString(4, e.getPassword());
            
            pstmt.executeUpdate();
            
        } catch (SQLException ex) {
            System.out.println("Error saving employee: " + ex.getMessage());
        }
    }
    
    // With the UNIQUE(emp_id, date) constraint, this handles both Clock In (Insert) and Clock Out (Update).
    public static void saveAttendance(Attendance a) {
        String sql = "INSERT OR REPLACE INTO attendance(emp_id, emp_name, date, clock_in, clock_out, hours_worked) VALUES(?,?,?,?,?,?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, a.getEmployeeId());
            pstmt.setString(2, a.getEmployeeName());
            pstmt.setString(3, a.getDate());
            pstmt.setString(4, a.getClockInTime());
            pstmt.setString(5, a.getClockOutTime()); 
            pstmt.setDouble(6, a.getHoursWorked());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving attendance: " + e.getMessage());
        }
    }

    public static void saveOutlet(Outlet o) {
        String sql = "INSERT OR REPLACE INTO outlets(code, name) VALUES(?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, o.getOutletCode()); // Assuming Outlet class has getOutletId() or getCode()
            pstmt.setString(2, o.getOutletName());
            pstmt.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    // Keeps updateStock for specific quick updates if needed by CLI
    public static void updateStock(String modelCode, String outletCode, int newQuantity) {
        // FIXED: Table name 'stock'
        String sql = "UPDATE stock SET quantity = ? WHERE model_code = ? AND outlet_code = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newQuantity);
            pstmt.setString(2, modelCode);
            pstmt.setString(3, outletCode);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating stock: " + e.getMessage());
        }
    }
    
    // Keeps updateAttendance for Clock Out if preferred
    public static void updateAttendance(Attendance a) {
        String sql = "UPDATE attendance SET clock_out=?, hours_worked=? WHERE emp_id=? AND date=?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, a.getClockOutTime());
            pstmt.setDouble(2, a.getHoursWorked());
            pstmt.setString(3, a.getEmployeeId());
            pstmt.setString(4, a.getDate());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating attendance: " + e.getMessage());
        }
    }

    // Called by EditSales (CLI)
    // We pass the "Original" values to identify which row to update
    public static void updateSale(Sales s, String origDate, String origCustomer, String origModel) {
        String sql = "UPDATE sales SET customer_name=?, model_code=?, quantity=?, subtotal=?, method=? " +
                     "WHERE date=? AND customer_name=? AND model_code=?";
        
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // 1. Set New Values
            pstmt.setString(1, s.getCustomerName());
            pstmt.setString(2, s.getModel());
            pstmt.setInt(3, s.getQuantity());
            pstmt.setDouble(4, s.getSubtotal());
            pstmt.setString(5, s.getTransactionMethod());
            
            // 2. Set Criteria (Original Values) to find the specific row
            pstmt.setString(6, origDate);
            pstmt.setString(7, origCustomer);
            pstmt.setString(8, origModel);
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                // System.out.println("Database record updated."); 
            } else {
                System.out.println("Warning: Could not find original sales record in Database to update.");
            }
            
        } catch (SQLException e) {
            System.out.println("Error updating sale: " + e.getMessage());
        }
    }

    // ==========================================
    //              DELETE METHODS
    // ==========================================

    public static void deleteEmployee(String id) {
        String sql = "DELETE FROM employees WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    public static void deleteModel(String modelCode) {
        String sql = "DELETE FROM models WHERE code = ?";
        // FIXED: Table name 'stock', not 'stocks'
        String sqlStock = "DELETE FROM stock WHERE model_code = ?";
        
        try (Connection conn = connect()) {
            try (PreparedStatement p1 = conn.prepareStatement(sqlStock)) {
                p1.setString(1, modelCode);
                p1.executeUpdate();
            }
            try (PreparedStatement p2 = conn.prepareStatement(sql)) {
                p2.setString(1, modelCode);
                p2.executeUpdate();
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }
}