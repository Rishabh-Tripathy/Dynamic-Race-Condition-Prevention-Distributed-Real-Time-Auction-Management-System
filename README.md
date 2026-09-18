⚡ FlashBidHigh-Throughput Real-Time Auction Engine with Dynamic Concurrency ControlKey Features • Technical Architecture • Database Schema • Directory Layout • Quick Start • Verification📌 Problem Statement & ContextHigh-frequency bidding platforms face severe race conditions when competing users attempt to bid on the same item at identical timestamps. Standard relational lookups often produce:Lost Updates: A lower bid overwriting a concurrent higher bid.Double-Spend Inconsistencies: Multiple buyers registered as winning bidders.Deadlock Conditions: Blocking lock cascades that stall backend application threads.FlashBid eliminates these vulnerabilities entirely through a layered arbitration model combining explicit thread memory fences (ReentrantLock), atomic conditional relational updates (WHERE current_bid < ?), and a non-blocking background scheduler (AuctionTimerService).🚀 Key Features🔒 Race Condition Elimination: Leverages explicit Java ReentrantLock synchronizers paired with atomic database conditional queries to ensure strictly serializable bid evaluation.⏱️ Autonomous Background Daemon: A decoupled background thread (AuctionTimerService) monitors auction expirations in real time, auto-closes listings, settles winners, and triggers asynchronous disc persistence.🧾 Automated Stream Invoicing: Automatically formats and serializes binding settlement agreements to /receipts via Java File I/O streams (FileWriter, BufferedWriter).🛡️ Custom Exception Layer: Implements fine-grained business exceptions (BidTooLowException, AuctionClosedException) preventing system instability upon invalid input.🧱 Polymorphic Domain Modeling: Employs object-oriented principles with an abstract base Item model extended by ReserveItem, enforcing strict minimum reserve rules dynamically.🏛️ Technical ArchitectureConcurrency & Bid Arbitration PipelinePlaintext       [ Concurrent Client Bids ]
                   │
                   ▼
      ┌─────────────────────────┐
      │  BiddingEngine Service  │
      │  (ReentrantLock Fence)  │
      └────────────┬────────────┘
                   │  Acquire Thread Lock
                   ▼
      ┌─────────────────────────┐
      │ Domain Rule Validation  │ ──► [Fails] ──► Throws BidTooLowException
      │ (Reserve & Increments)  │
      └────────────┬────────────┘
                   │  Passes
                   ▼
      ┌─────────────────────────┐
      │   Atomic MySQL Commit   │ ──► [Outdated] ──► Rollback & Return False
      │ (WHERE current_bid < ?) │
      └────────────┬────────────┘
                   │  Successful Update
                   ▼
      ┌─────────────────────────┐
      │ Log to `bid_history` &  │
      │      Release Lock       │
      └─────────────────────────┘
📊 Syllabus & Technical Competency MatrixDomainImplementation in FlashBidObject-Oriented ProgrammingInheritance hierarchy (Item → ReserveItem), polymorphism, encapsulation, constructor overloadingMultithreading & ConcurrencyNon-blocking daemon threads, worker simulation pools, thread-safe arbitration using ReentrantLockJDBC & TransactionsSingleton connection pattern, PreparedStatement parameter binding, transactional state updatesException HandlingCustom checked exceptions (AuctionClosedException, BidTooLowException) with structured recoveryJava File I/O StreamsDisk serialization of final settlement receipts into structured .txt files under receipts/🗄️ Database SchemaThe database model is normalized into two relational tables enforcing referential integrity:SQLitems (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    item_type VARCHAR(20) NOT NULL,
    reserve_price DOUBLE NOT NULL,
    current_bid DOUBLE NOT NULL,
    highest_bidder VARCHAR(100) DEFAULT 'None',
    status ENUM('OPEN', 'CLOSED') DEFAULT 'OPEN',
    end_time BIGINT NOT NULL
);

bid_history (
    bid_id INT PRIMARY KEY AUTO_INCREMENT,
    item_id INT NOT NULL,
    bidder_name VARCHAR(100) NOT NULL,
    bid_amount DOUBLE NOT NULL,
    bid_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES items(item_id) ON DELETE CASCADE
);
📁 Directory LayoutPlaintextflashbid/
│
├── database/
│   └── schema.sql                  # Database creation and seed scripts
├── lib/
│   └── mysql-connector-j-8.4.0.jar # MySQL JDBC Type 4 Connector
├── receipts/                       # Generated settlement output text files
│   ├── settlement_item_1.txt
│   └── settlement_item_3.txt
├── src/
│   └── com/
│       └── flashbid/
│           ├── config/
│           │   └── DatabaseConnection.java   # Thread-safe connection singleton
│           ├── dao/
│           │   ├── BidDAO.java               # Bid persistence layer
│           │   └── ItemDAO.java              # Item retrieval & atomic update layer
│           ├── exception/
│           │   ├── AuctionClosedException.java
│           │   └── BidTooLowException.java
│           ├── model/
│           │   ├── Bid.java
│           │   ├── Item.java
│           │   └── ReserveItem.java
│           ├── service/
│           │   ├── AuctionTimerService.java  # Daemon thread monitoring expirations
│           │   └── BiddingEngine.java        # Thread-safe lock coordinator
│           └── Main.java                     # CLI controller and stress-test harness
│
├── README.md
└── statement.md
🛠️ Quick Start Guide[!IMPORTANT]Ensure Apache and MySQL are both running in your XAMPP Control Panel before executing the application runtime.1. Prerequisites CheckDependencyRequired VersionVerification CommandStatusJava Development KitJDK 17+ / 21+java -versionRequiredMySQL EngineMySQL 8.x / MariaDBAvailable via XAMPPPort 3306Connector/J Drivermysql-connector-j-8.4.0.jarCheck inside /libRequired2. Database InitializationCode snippetgraph LR
    A[Start XAMPP] --> B[Open phpMyAdmin]
    B --> C[Import database/schema.sql]
    C --> D[(flashbid_db Ready)]
Launch XAMPP Control Panel and start Apache and MySQL.Open your browser and navigate to:Plaintexthttp://localhost/phpmyadmin
Click the SQL tab on the top menu bar.Copy and paste the entire script from database/schema.sql, then click Go.Confirm that flashbid_db appears in the left panel containing items and bid_history tables.3. Build & ExecutionMake sure your terminal is positioned at the project root:PowerShell# Step 1: Compile all source packages with library dependencies
javac -cp "lib/mysql-connector-j-8.4.0.jar;src" (Get-ChildItem -Recurse -Filter *.java src | Select-Object -ExpandProperty FullName)

# Step 2: Launch the compiled FlashBid engine
java -cp "lib/mysql-connector-j-8.4.0.jar;src" com.flashbid.Main
Open the project root folder directly in VS Code (File → Open Folder...).Expand the JAVA PROJECTS panel at the bottom of the Explorer.Verify mysql-connector-j-8.4.0.jar is listed under Referenced Libraries (click + to add if missing).Navigate to src/com/flashbid/Main.java.Press F5 or click the Run ▶ icon in the top right corner.🧪 Functional Verification & Stress Harness[!NOTE]The engine provides an interactive command-line interface to demonstrate concurrency management and automated background routines in real time.Action KeyEngine RoutineArchitectural Behavior Demonstrated1List All Active AuctionsExecutes parameterized query via ItemDAO, calculating remaining countdown epochs dynamically.2Place Single BidVerifies reserve thresholds; triggers BidTooLowException or AuctionClosedException upon constraint failure.3Run Concurrency Stress TestSpawns multiple concurrent worker threads (ExecutorService) targeting the same item to prove ReentrantLock mutual exclusion.4Exit SystemGracefully terminates worker threads, daemon monitors, and releases active pool connections.Plaintext  ======================================================
         DAEMON EVENT: AUTOMATED RECEIPT GENERATION
  ======================================================
  [DAEMON EVENT] Auction ID #1 (Vintage Campus Watch) has EXPIRED!
  [DAEMON EVENT] Winner: User_Beta at Rs.2200.0
  [I/O] Settlement invoice written to: receipts/settlement_item_1.txt
👨‍💻 AuthorRishabh TripathyGitHub: @Rishabh-TripathyProject: Distributed Real-Time Auction Management System (FlashBid)