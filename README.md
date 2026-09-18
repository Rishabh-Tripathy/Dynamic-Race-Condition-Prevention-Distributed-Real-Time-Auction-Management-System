# FlashBid: Distributed Real-Time Auction Management System with Dynamic Race-Condition Prevention

FlashBid is a high-throughput, concurrent console-based auction management platform developed in Java. The engine demonstrates robust thread synchronization, optimistic database concurrency control, ACID-compliant persistence via JDBC, custom domain-level exception propagation, and automated file-stream invoice generation.

---

## 🚀 Key Architectural Features

- **Race Condition & Deadlock Mitigation:** Uses thread-safe atomic memory synchronization and explicit `ReentrantLock` controls combined with atomic MySQL conditional updates (`WHERE current_bid < ? AND status = 'OPEN'`) to eliminate double-spend and race conditions during simultaneous bids.
- **Autonomous Daemon Monitoring:** Employs a background daemon thread (`AuctionTimerService`) running a discrete scheduled task loop to actively monitor auction expiry epochs, reconcile winners, trigger database status transitions, and automatically invoke disk I/O routines.
- **Transactional Persistence (JDBC):** Manages connection life cycles, transaction boundaries, rollbacks, and parameter binding using JDBC `PreparedStatement` interfaces against a local MySQL relational store.
- **Domain Exception Handling:** Custom, robust exceptions (`AuctionClosedException`, `BidTooLowException`) propagate business constraint violations through caller execution layers without interrupting the host thread pool.
- **Stream-Based I/O Invoicing:** Automatically serializes final settlement contracts directly to the `receipts/` directory upon auction termination using Java File I/O streams (`FileWriter`, `BufferedWriter`).

---

## 🏛️ Syllabus & Rubric Mapping

| Technical Domain | Implementation Details |
| :--- | :--- |
| **Object-Oriented Programming (OOP)** | Inheritance hierarchy (`Item` base class extended by specialized `ReserveItem`), polymorphism, encapsulation, interface abstractions. |
| **Multithreading & Concurrency** | Dedicated daemon timer thread, worker bidding simulations, lock arbitration via `java.util.concurrent.locks.ReentrantLock`. |
| **Relational Database & JDBC** | Normalized tables (`items`, `bid_history`), ACID transactional persistence, connection management via `DatabaseConnection` singleton. |
| **Exception Handling** | Custom checked exceptions (`BidTooLowException`, `AuctionClosedException`) with graceful caller recovery. |
| **File I/O & Streams** | Automated settlement invoice streaming and disk persistence via `receipts/settlement_item_<id>.txt`. |

---

## 📁 Repository Structure

```text
flashbid/
│
├── database/
│   └── schema.sql                  # Database DDL, table constraints, and initial seed data
│
├── lib/
│   └── mysql-connector-j-8.4.0.jar # MySQL JDBC Type 4 binary driver
│
├── receipts/                       # Output directory for daemon-generated settlement invoices
│
├── src/
│   └── com/
│       └── flashbid/
│           ├── config/
│           │   └── DatabaseConnection.java  # JDBC driver loader & connection provider
│           ├── dao/
│           │   ├── BidDAO.java              # Data Access Object for bid logging
│           │   └── ItemDAO.java             # Data Access Object for item state queries/updates
│           ├── exception/
│           │   ├── AuctionClosedException.java # Thrown when bidding on terminated items
│           │   └── BidTooLowException.java     # Thrown when bid does not exceed thresholds
│           ├── model/
│           │   ├── Bid.java                 # Immutable representation of a submitted bid
│           │   ├── Item.java                # Base auction model (encapsulation, validation)
│           │   └── ReserveItem.java         # Subclass enforcing reserve price domain rules
│           ├── service/
│           │   ├── AuctionTimerService.java # Background daemon monitoring auction deadlines
│           │   └── BiddingEngine.java       # Thread-safe bidding coordinator & invoice writer
│           └── Main.java                    # Terminal CLI controller and concurrency stress harness
│
├── README.md                       # Complete project technical documentation
└── statement.md                    # Problem statement, scope, and objectives

🛠️ Prerequisites & Setup
Java Development Kit: JDK 17+ or JDK 21+ installed and configured on your system environment path.

Database Engine: MySQL Server (active via XAMPP Control Panel or standalone MySQL Server).

JDBC Driver: mysql-connector-j-8.4.0.jar placed in the lib/ directory.

Database Initialization
Start Apache and MySQL inside your XAMPP Control Panel.

Open your web browser and navigate to http://localhost/phpmyadmin.

Select the SQL tab from the top menu bar.

Copy the entire contents of database/schema.sql, paste it into the query editor, and click Go.

Confirm that the flashbid_db database is created with both items and bid_history tables populated.

⚙️ Compilation & Execution
Option A: Running from the Terminal (PowerShell / Command Prompt)
Ensure your terminal working directory is located at the project root:

PowerShell
# 1. Compile all Java packages including external libraries
javac -cp "lib/mysql-connector-j-8.4.0.jar;src" (Get-ChildItem -Recurse -Filter *.java src | Select-Object -ExpandProperty FullName)

# 2. Launch the application
java -cp "lib/mysql-connector-j-8.4.0.jar;src" com.flashbid.Main
Option B: Running via Visual Studio Code
Open the project root folder directly in VS Code.

Expand JAVA PROJECTS in the left sidebar and ensure mysql-connector-j-8.4.0.jar is listed under Referenced Libraries.

Navigate to src/com/flashbid/Main.java.

Click the Run ▶ icon located in the upper-right corner of the editor.

🧪 Functional Verification & Testing
From the interactive terminal interface, select any of the following options:

Option 1: List All Active Auctions
Queries the relational database using ItemDAO and prints all items whose status is OPEN, along with real-time remaining countdown times.

Option 2: Place a Bid (Single User)
Submits a single bid against an item. Demonstrates domain checks:

Throws BidTooLowException if the amount is less than or equal to the current bid, or fails the reserve price threshold.

Throws AuctionClosedException if submitted after the timer has expired.

Option 3: Run Concurrency Stress Test (Simulate Race Condition)
Fires multiple concurrent worker threads attempting to submit bids simultaneously on a single item. Demonstrates thread synchronization using ReentrantLock, ensuring only valid, sequential increments are accepted while outdated bids are safely rejected.

Background Daemon & Receipt Generation:
When an auction countdown expires, the daemon thread automatically transitions the status to CLOSED, calculates the winning bidder, and writes an invoice receipt directly into the receipts/ directory using Java File I/O streams.