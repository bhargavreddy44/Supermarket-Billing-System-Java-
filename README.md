# Supermarket Billing System

A comprehensive Java Swing application for supermarket billing operations with login authentication, product management, billing system, sales reporting, and automated backup functionality.

## 🏗️ Architecture

This project follows the MVC (Model-View-Controller) pattern with additional utility classes:

- **Model**: `Product.java`, `BilledItem.java`, `Sale.java`
- **View**: `LoginView.java`, `DashboardView.java`, `ProductView.java`, `BillingView.java`, `SalesReportView.java`, `SettingsView.java`
- **Controller**: `LoginController.java`, `ProductController.java`, `BillingController.java`, `SalesController.java`, `BackupController.java`
- **Utilities**: `FileUtils.java`, `SimpleLogger.java`
- **Main**: `Main.java`

## 📁 Project Structure

```
src/
 ├── model/
 │   ├── Product.java
 │   ├── BilledItem.java
 │   └── Sale.java
 ├── view/
 │   ├── LoginView.java
 │   ├── DashboardView.java
 │   ├── ProductView.java
 │   ├── BillingView.java
 │   ├── SalesReportView.java
 │   └── SettingsView.java
 ├── controller/
 │   ├── LoginController.java
 │   ├── ProductController.java
 │   ├── BillingController.java
 │   ├── SalesController.java
 │   └── BackupController.java
 ├── util/
 │   ├── FileUtils.java
 │   └── SimpleLogger.java
 └── Main.java

data/
 ├── products.csv
 └── sales.csv

bills/
 └── (generated bill files)

reports/
 └── (exported sales reports)

backup/
 └── (automated backups)

logs/
 └── app.log
```

## 🚀 How to Run

### Requirements
- **Java Development Kit (JDK) 17 or higher**
- **Operating System**: Windows, macOS, or Linux

### Method 1: Command Line

#### Compile and Run:
```bash
# Navigate to project directory
cd /path/to/billingsystem02

# Compile all Java files
javac -d bin $(find src -name "*.java")

# Run the application
java -cp bin Main
```

#### Using the provided script:
```bash
# On Unix/Linux/macOS
chmod +x run.sh
./run.sh

# On Windows
run.bat
```

### Method 2: IDE (Recommended)

1. **Open Project**: Import the project into your IDE (IntelliJ IDEA, Eclipse, VS Code)
2. **Set JDK**: Ensure JDK 17+ is configured
3. **Run**: Right-click on `Main.java` and select "Run Main.main()"

### Method 3: Using the run scripts

The project includes convenient run scripts:

- **`run.sh`** (Unix/Linux/macOS): Compiles and runs the application
- **`run.bat`** (Windows): Compiles and runs the application

## 🔐 Default Credentials

- **Username**: `admin`
- **Password**: `1234`

> **Security Note**: These are demo credentials. For production use, implement proper user authentication with encrypted passwords.

## ✨ Features

### Login System
- Clean and modern UI using Java Swing
- Username and password authentication
- Success/Error messages using JOptionPane
- Default credentials displayed as a hint
- Login validation
- Dashboard window opens on successful login
- Logout functionality with confirmation
- Enter key support for password field

### Product Management
- Full CRUD operations (Create, Read, Update, Delete)
- Product data stored in CSV format
- CSV file auto-creates if not present
- Product table with all fields displayed
- Input validation for all fields
- Add/Update/Delete with confirmation dialogs
- Refresh button to reload data
- Navigate back to dashboard
- Selection-based editing (click row to edit)

### Billing System
- Point-of-sale (POS) interface
- Real-time stock validation
- Dual-table interface (products + bill)
- Automatic total calculations
- Discount percentage support
- Professional bill generation
- Stock updates after sales
- Sales data recording
- Bill files saved with timestamps
- **Enhanced**: Atomic file operations for reliability
- **Enhanced**: Both text and CSV bill formats
- **Enhanced**: Success dialog with "Open folder" option

### Sales Reporting
- Daily sales tracking and analytics
- Date range filtering (yyyy-MM-dd format)
- Real-time sales statistics
- Export functionality to CSV reports
- All-time and range-based totals
- Professional report generation
- Sales data visualization
- Historical sales analysis

### Backup & Settings
- **Automated Backup System**: Scheduled backups of data and bills
- **Manual Backup**: Run backup on demand
- **Backup Cleanup**: Remove old backups automatically
- **Settings Interface**: Configure backup intervals and preferences
- **Atomic File Operations**: Safe file writing to prevent corruption
- **Comprehensive Logging**: All operations logged to `logs/app.log`

## 🎯 Technologies

- **Java 17+**
- **Java Swing** (javax.swing.*)
- **Java AWT** (java.awt.*)
- **Java File I/O** for CSV operations
- **Java NIO** for atomic file operations
- **Java Concurrency** for scheduled backups

## 📝 Data Storage

### Products (`data/products.csv`)
```csv
id,name,category,price,quantity
P001,Milk,Dairy,40.00,50
P002,Bread,Bakery,25.00,30
```

### Sales (`data/sales.csv`)
```csv
date,total,discount,netTotal
2025-10-27 10:30:00,105.00,5.00,99.75
2025-10-27 11:15:00,85.50,0.00,85.50
```

### Bills (`bills/` directory)
- **Text Bills**: `Bill_YYYY-MM-DD_HH-mm-ss.txt` - Human-readable format
- **CSV Bills**: `Bill_YYYY-MM-DD_HH-mm-ss.csv` - Structured data format

### Reports (`reports/` directory)
- Exported sales reports: `report_YYYY-MM-DD_to_YYYY-MM-DD.csv`
- Filtered sales data with headers

### Backups (`backup/` directory)
- **Backup Folders**: `backup_YYYY-MM-DD_HH-mm-ss/`
- **Contents**: Complete copies of `data/` and `bills/` directories
- **Manifest**: `backup_manifest.txt` with file listing and sizes

### Logs (`logs/` directory)
- **Application Log**: `app.log` - All system events and errors
- **Format**: `[YYYY-MM-DD HH:mm:ss] LEVEL: message`

## 🔧 Configuration

### Backup Settings
Access via **Dashboard → Settings**:

- **Auto Backup**: Enable/disable scheduled backups
- **Backup Interval**: Set frequency (1-168 hours)
- **Backup Cleanup**: Configure how many backups to keep
- **Manual Backup**: Run backup immediately

### Default Backup Behavior
- **Auto Backup**: Disabled by default
- **Interval**: 24 hours (when enabled)
- **Retention**: Keep last 5 backups (configurable)

## 📝 Notes

- The login window closes automatically upon successful login
- Product CSV file is created automatically if it doesn't exist
- All messages are displayed using JOptionPane
- The application uses the system's native look and feel
- Navigation: Dashboard → Product Management/Billing/Sales Reports/Settings → Dashboard
- Logout returns to login screen
- Stock is automatically updated after each sale
- Bills are saved with timestamps for record keeping
- Sales reports support date filtering and CSV export
- All modules share consistent data through CSV files
- **Enhanced**: Atomic file operations prevent data corruption
- **Enhanced**: Comprehensive logging for troubleshooting
- **Enhanced**: Graceful shutdown with cleanup

## 🛠️ Troubleshooting

### Common Issues

#### File Permission Errors
- **Problem**: Cannot write to data/bills/reports directories
- **Solution**: Ensure the application has write permissions to the project directory

#### CSV Format Issues
- **Problem**: Corrupted or invalid CSV files
- **Solution**: Check `logs/app.log` for specific errors, restore from backup if needed

#### Java Version Issues
- **Problem**: "UnsupportedClassVersionError"
- **Solution**: Ensure JDK 17+ is installed and configured

#### Memory Issues
- **Problem**: OutOfMemoryError with large datasets
- **Solution**: Increase JVM heap size: `java -Xmx512m -cp bin Main`

### Log Analysis
Check `logs/app.log` for detailed error information:
```bash
tail -f logs/app.log  # Monitor live logs
grep ERROR logs/app.log  # Find errors
```

### Backup Recovery
If data corruption occurs:
1. Navigate to `backup/` directory
2. Find the most recent backup folder
3. Copy `data/` and `bills/` contents back to project root
4. Restart the application

## 🔮 Future Enhancements

### Database Integration
For production use, consider migrating from CSV to a proper database:
- **SQLite**: Lightweight, file-based database
- **PostgreSQL**: Full-featured relational database
- **MySQL**: Popular open-source database

### Additional Features
- **Multi-user Support**: Role-based access control
- **Inventory Alerts**: Low stock notifications
- **Advanced Reporting**: Charts and analytics
- **Barcode Scanner**: Integration with hardware
- **Cloud Backup**: Remote backup storage
- **Mobile App**: Companion mobile application

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Supermarket Billing System**  
A comprehensive Java Swing application for retail management.

## 📞 Support

For issues, questions, or contributions:
1. Check the troubleshooting section above
2. Review `logs/app.log` for error details
3. Create an issue with detailed error information

---

**Version**: 2.0  
**Last Updated**: October 2025  
**Java Version**: 17+