# FlashBid

---

# ⚡ Real-Time Concurrent Auction Engine

[![Status](https://img.shields.io/badge/Status-Complete-brightgreen?style=flat-square)](https://github.com/Rishabh-Tripathy)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17%2B-blue?style=flat-square)](https://www.oracle.com/java/)
[![Database](https://img.shields.io/badge/Database-MySQL%208.4-informational?style=flat-square)](https://www.mysql.com/)

> Dynamic Race-Condition Prevention, Autonomous Daemon Timers, and Thread-Safe Transactional Persistence.

---

### ⚡ FlashBid: High-Throughput Auction Management

A production-grade Java console platform engineered to solve race conditions, auction deadline collisions, and data synchronization hazards using optimistic SQL controls, ReentrantLocks, background daemon threads, and ACID-compliant transactional persistence.

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-0A66C2?style=flat-square&logo=linkedin)](https://www.linkedin.com/)
[![GitHub](https://img.shields.io/badge/GITHUB-Follow-181717?style=flat-square&logo=github)](https://github.com/Rishabh-Tripathy)

---

### 📌 Quick Navigation

* [Problem Statement](#-problem-statement)
* [Key Architectural Features](#-key-architectural-features)
* [Syllabus & Rubric Mapping](#-syllabus--rubric-mapping)
* [Repository Structure](#-repository-structure)
* [Prerequisites & Setup](#-prerequisites--setup)
* [Compilation & Execution](#-compilation--execution)
* [Functional Verification & Testing](#-functional-verification--testing)
* [License](#-license)

---

## 🌟 Problem Statement

High-frequency bidding systems encounter critical race conditions when concurrent users submit bids at identical timestamps. Standard relational lookups produce:
* **Lost Updates:** Lower bids overwriting higher concurrent bids.
* **Double-Spend Inconsistencies:** Multiple bidders registered as winning buyers.
* **Deadlock Conditions:** Blocking cascades stalling execution threads.

**FlashBid** eliminates these synchronization hazards through explicit thread memory fences (`ReentrantLock`), atomic database updates (`WHERE current_bid < ?`), and a non-blocking background daemon thread (`AuctionTimerService`).

---

## 🚀 Key Architectural Features

* **Race Condition & Deadlock Mitigation:** Uses thread-safe atomic memory synchronization and explicit `ReentrantLock` controls combined with atomic MySQL conditional updates (`WHERE current_bid < ? AND status = 'OPEN'`) to eliminate double-spend and race conditions during simultaneous bids.
* **Autonomous Daemon Monitoring:** Employs a background daemon thread (`AuctionTimerService`) running a discrete scheduled task loop to actively monitor auction expiry epochs, reconcile winners, trigger database status transitions, and automatically invoke disk I/O routines.
* **Transactional Persistence (JDBC):** Manages connection life cycles, transaction boundaries, rollbacks, and parameter binding using JDBC `PreparedStatement` interfaces against a local MySQL relational store.
* **Domain Exception Handling:** Custom, robust exceptions (`AuctionClosedException`, `BidTooLowException`) propagate business constraint violations through caller execution layers without interrupting the host thread pool.
* **Stream-Based I/O Invoicing:** Automatically serializes final settlement contracts directly to the `receipts/` directory upon auction termination using Java File I/O streams (`FileWriter`, `BufferedWriter`).

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
```

---

## 🛠️ Prerequisites & Setup

### System Prerequisites

* **Java Development Kit:** JDK 17+ or JDK 21+ installed and configured on your system environment path.
* **Database Engine:** MySQL Server (active via XAMPP Control Panel or standalone MySQL Server).
* **JDBC Driver:** `mysql-connector-j-8.4.0.jar` placed in the `lib/` directory.

### Database Initialization

1. Start **Apache** and **MySQL** inside your XAMPP Control Panel.
2. Open your web browser and navigate to `http://localhost/phpmyadmin`.
3. Select the **SQL** tab from the top menu bar.
4. Copy the entire contents of `database/schema.sql`, paste it into the query editor, and click **Go**.
5. Confirm that the `flashbid_db` database is created with both `items` and `bid_history` tables populated.

---

## ⚙️ Compilation & Execution

<details open>
<summary><b>💻 Option A: Running from Terminal (PowerShell / Command Prompt)</b></summary>
<br>

Ensure your terminal working directory is located at the project root:

```powershell
# 1. Compile all Java packages including external libraries
javac -cp "lib/mysql-connector-j-8.4.0.jar;src" (Get-ChildItem -Recurse -Filter *.java src | Select-Object -ExpandProperty FullName)

# 2. Launch the application
java -cp "lib/mysql-connector-j-8.4.0.jar;src" com.flashbid.Main
```

</details>

<details>
<summary><b>🖥️ Option B: Running via Visual Studio Code</b></summary>
<br>

1. Open the project root folder directly in VS Code.
2. Expand **JAVA PROJECTS** in the left sidebar and ensure `mysql-connector-j-8.4.0.jar` is listed under **Referenced Libraries**.
3. Navigate to `src/com/flashbid/Main.java`.
4. Click the **Run ▶** icon located in the upper-right corner of the editor.

</details>

---

## 🧪 Functional Verification & Testing

From the interactive terminal interface, select any of the following options:

* **`1` List All Active Auctions**  
  Queries the relational database using `ItemDAO` and prints all items whose status is `OPEN`, along with real-time remaining countdown times.

* **`2` Place a Bid (Single User)**  
  Submits a single bid against an item. Demonstrates domain checks:
  * Throws `BidTooLowException` if the amount is less than or equal to the current bid, or fails the reserve price threshold.
  * Throws `AuctionClosedException` if submitted after the timer has expired.

* **`3` Run Concurrency Stress Test (Simulate Race Condition)**  
  Fires multiple concurrent worker threads attempting to submit bids simultaneously on a single item. Demonstrates thread synchronization using `ReentrantLock`, ensuring only valid, sequential increments are accepted while outdated bids are safely rejected.

* **Background Daemon & Receipt Generation:**  
  When an auction countdown expires, the daemon thread automatically transitions the status to `CLOSED`, calculates the winning bidder, and writes an invoice receipt directly into the `receipts/` directory using Java File I/O streams.

```text
======================================================
       DAEMON EVENT: AUTOMATED RECEIPT GENERATION
======================================================
[DAEMON EVENT] Auction ID #1 (Vintage Campus Watch) has EXPIRED!
[DAEMON EVENT] Winner: User_Beta at Rs.2200.0
[I/O] Settlement invoice written to: receipts/settlement_item_1.txt
```

---

## 📄 License

This project is licensed under the MIT License:

```text
MIT License

Copyright (c) 2026 Rishabh Tripathy

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
