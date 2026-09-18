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