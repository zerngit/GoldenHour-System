# Golden Hour Store Management System

A simple, console-based (as of now) inventory and sales system for store operations. It manages employees, stock, and outlets, storing all primary data in CSV files. 

---

## 🚀 Features

* **Authentication:** Secure employee login and registration.
* **Attendance:** Clock-in and clock-out tracking for employees.
* **Stock Management:** Track inventory with stock counts and stock movement.
* **Sales:** Process new sales and record them to CSV and text-based receipts.
* **Search Information:** 
* **Edit Information:**
* **Storage System:** All data is read from and saved to local `.csv` files.
* **Data Load State:** Right after system start, the data is loaded into list 

---

## 🏗️ How It Works: Architecture

The application is split into several key packages, each with a distinct responsibility.

### Key Folder Overview

* **`main`**: The application's entry point (`Main.java`) that launches the UI.
* **`categories`**: Contain classes that represent for each object. (e.g., `Employee`, `Stock`).
* **`service`**: The feature logic. All operations (logging in, adding stock, making a sale) are handled by these classes.
* **`ui`**: The user-facing console interface. These classes handle user input and display.
* **`util`**: Helper classes, such as `TimeUtil`.
* **`data`**: (Directory) Contains all persistent data as `.csv` files.

### 📂Project Structure

```bash
GoldenHour-System/
└── goldenhour/                  ← Maven project root
    ├── pom.xml                  ← Maven configuration
    ├── src/main/java/com/goldenhour/
    │   ├── main/                ← Application entry point
    │   │   └── Main.java
    │   ├── categories/          ← Data models (POJOs)
    │   │   └── Employee.java
    │   │   └── Model.java
    │   │   └── Outlet.java
    │   │   └── Sales.java
    │   │   └── Attendance.java
    │   │── dataload/             ← Data Loading once start
    |   |   └── DataLoad.java
    │   ├── service/             ← Features
    |   |   └── attendance
    |   |       └── AttendanceService.java
    |   |   └── loginregister
    |   |       └── AuthService.java
    |   |       └── RegistrationService.java
    |   |   └── salessys
    |   |       └── SalesService.java
    |   |       └── SalesSearch.java
    |   |   └── stocksys
    |   |       └── StockCountService.java
    |   |       └── StockMovementService.java
    |   |       └── StockSearch.java
    |   ├── storage/                          ← API between java and CSV
    |   |       └── CSVHandler.java
    |   |       └── ReceiptHandler.java       
    │   ├── ui/                  ← Console UI pages
    │   │   └── AttendanceUI.java
    │   │   └── LoginUI.java
    │   │   └── StockUI.java
    │   │   └── SalesUI.java
    │   │   └── SearchUI.java
    │   └── util/                ← Utility helpers
    │       └── TimeUtil.java
    └── data/                    ← CSV data storage
        └── receipts/
        └── employee.csv
        └── model.csv
        └── outlet.csv
        └── sales.csv
        └── attendance.csv

```

---

## 🔄 Key Workflows

**As of now**, here’s how the components interact during common operations.

### 1. Data Load State

1.  When the program starts, `Main.java` is executed.
2.  **Data Loading:** The system calls `CSVHandler.java` to read all CSV files (`stock.csv`, `employees.csv`,`outlets.csv`)
3. **Object Conversion:** The handlers convert the raw CSV data into POJO objects (`Employee`,`Model`,`Outlet`)
4. **Central Runtime Storage:** These lists of objects are stored into the static lists within `DataLoad.java`.
5.  `LoginUI` is run, which displays the main menu (e.g., "Login")

### 2. User Login/Logout + Registration

1.  **`LoginUI`**: Prompts the user for an ID and password.
2.  **`AuthService`**: The UI calls `authService.login(id, password)`.
3. **Data Retrieval:** Instead of reading a file, `AuthService` accesses the preloaded list : `DataLoad.allEmployees`
4.  **`AuthService`**: Compares the user's input to the list of `Employee` objects to find a match.
6.  **`LoginUI`**: Receives a success/failure response and either proceeds to the main menu or shows an error.

### 3. Attendance Log


### 4. Stock Management System

1.  **`StockUI`**: The user selects the service from a stock system
2.  **`Morning/Night Stock Count`**: System proceed to `StockCountService.performStockCount(...)` 
3. **`Stock In/Stock Out`** : The 
3.  **`storage` (CSVHandler)**: The service updates `data/model.csv` (when stock moves from A to B)
4.  **`storage` (ReceiptHandler)**: The service also appends a human-readable receipt to a `.txt` file using `ReceiptHandler`

### 4. Sales System

### 5. Search Information

### 6. Edit Information

### 7. Storage System


---

## ⛓️ Architecture & Components
### Concept: Classes & Objects (OOP)

### 1. Class Structure

- Data Classes (POJOs) - `com.goldenhour.categories`

  - Blueprints for data (`Employee`, `Model`, `Outlet`)
  - Rule: Must contain `fromCSV(String line)` and `toCSV()` methods for converting data

- Service Classes - `com.goldenhour.service`
  - Handle logic (`AuthService`, `StockCountService`).
  - Rule: Do not read files directly. Retrieve data from `DataRepository` and save data via `CSVHandler`

- Storage/Utility - `com.goldenhour.storage`
  - `CSVHandler`: The central engine for file I/O
  - `DataRepository:` Static lists holding all data in memory


--- 

## 📊 Data Flow
### Concept : IO handling

We use an "Eager Loading" pattern. Data is loaded once at startup, and services read from memory.

---

### Reading Data (Startup)

- When: Only when Main.java starts
- Flow: `Main` calls `CSVHandler` -> `CSVHandler` read files & uses `POJO.fromCSV()` -> Objects stored in `DataLoad`  
- Result: All data is ready in memory (RAM) for instat access

### Processing Data (Runtime)

- When: A user logs in or counts stock
- Flow: `Service` requests data -> `DataLoad` returns the ArrayList -> `Service` processes logic
- Note: No file I/O happens here.

### Writing Data (Updating/Insert Data)

- When: A user registers, updates stock, or make sale
- Flow: `Service` updates the Object -> `Service` calls `CSVHandler.write...` -> `CSVHandler` uses `object.toCSV()` -> File is overwritten

### Note : The CSV handling logic operates on a **line-by-line** basis. The `CSV Handler` iterates through each file, and the `fromCSV()` method is designed to parse a single row of text into a corresponding Java object.

---

## 👷‍♂️ Developer Guide: Adding New Features

### For Sales System (2 marks) /Attendance System (1/2 marks)

When adding new features (eg. Sales), you must follow this pattern to maintain data consistency. Do not write your own file readers

**Step 1: Create POJO** Work on `example: Sales.java` in categories. Implemented the standard conversion methods:

**Step 2: Update the Handler** Go to `CSVHandler.java` (or create SalesHandler)

```
public String toCSV() { ... }
public static Sales fromCSV(String line) { ... }
```

**Step 3: Update DataLoad** Add a list in `DataLoad.java`

```
example: public static List<Sales> allSales;
```

**Step 4: Load at startup** Add one line in Main.java

```
example: DataLoad.allSales = CSVHandler.readSales();
```
<br>

### For Search Information (1 mark)
Because all data is loaded into `DataLoad.java` at startup, "Searching" simply means iterating through the static lists (`allModels` or `allSales`) and finding matches.

**Step 1: Service** Create method in service (eg. `findStockbyModel.java` in `StockSearch.java`)

**Step 2: Logic** Loop through `DataLoad.allSales`

**Step 3: Comparison** Check if matches the user input

**Step 4: Display result**

<br>

### For Edit Information (1 mark)

**Step 1: Find** The service searches the `DataLoad` list to find the specific object (using ID or Code).

**Step 2: Modify** The service uses object's (POJO) **Setter methods** (eg, `setPrice()`, `setQuantity()`) to update data in memory

**Step 3: Overwrite** The service passes the entire updated list to the `CSVHandler`, which completely overwrites the old CSV with the new data. 

<br>

### Note: Marks allocated for each features does not reflect the actual workload. 

---

## 🤝 How to Contribute

**please follow these steps:**
1.  **Fork** the repository.
2.  **Clone your forked repo** to local machine

`Note: Step 1 & 2 are one-time setup, only need to be done once`

3.  Create a new branch (`git checkout -b feature/your name`).
4.  Make your changes.
5.  Stage your changes (`git add .`)
6.  Commit your changes (`git commit -m 'Add some feature'`).
7.  Push to the branch (`git push origin feature/your name`).
8.  Create a new pull Request.