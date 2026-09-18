package com.flashbid;

import com.flashbid.dao.ItemDAO;
import com.flashbid.exception.AuctionClosedException;
import com.flashbid.exception.BidTooLowException;
import com.flashbid.model.Item;
import com.flashbid.service.AuctionTimerService;
import com.flashbid.service.BiddingEngine;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Start background auction countdown daemon thread
        AuctionTimerService timerService = new AuctionTimerService();
        Thread daemonThread = new Thread(timerService);
        daemonThread.setDaemon(true);
        daemonThread.start();

        ItemDAO itemDAO = new ItemDAO();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("\n=======================================================");
        System.out.println("   FLASHBID: REAL-TIME CONCURRENT AUCTION ENGINE       ");
        System.out.println("=======================================================");

        while (running) {
            System.out.println("\nSelect an action:");
            System.out.println("1. List All Active Auctions");
            System.out.println("2. Place a Bid (Single User)");
            System.out.println("3. Run Concurrency Stress Test (Simulate Race Condition)");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    List<Item> items = itemDAO.getAllOpenItems();
                    if (items.isEmpty()) {
                        System.out.println("[!] No open auctions found. (Check database connection/data)");
                    } else {
                        System.out.println("\n--- LIVE AUCTIONS ---");
                        for (Item it : items) {
                            long remaining = it.getEndTime() - (System.currentTimeMillis() / 1000);
                            System.out.printf("ID: %d | %s | Current: Rs.%.2f | Highest Bidder: %s | Time Left: %ds\n",
                                    it.getItemId(), it.getTitle(), it.getCurrentBid(), it.getHighestBidder(), Math.max(0, remaining));
                        }
                    }
                    break;

                case "2":
                    try {
                        System.out.print("Enter Item ID: ");
                        int itemId = Integer.parseInt(scanner.nextLine());
                        System.out.print("Enter Your Name: ");
                        String bidder = scanner.nextLine();
                        System.out.print("Enter Bid Amount: Rs.");
                        double amount = Double.parseDouble(scanner.nextLine());

                        boolean ok = BiddingEngine.placeBid(itemId, bidder, amount);
                        if (ok) {
                            System.out.println(">> SUCCESS: Bid registered successfully!");
                        }
                    } catch (BidTooLowException | AuctionClosedException e) {
                        System.out.println(">> [REJECTED] " + e.getMessage());
                    } catch (NumberFormatException e) {
                        System.out.println(">> [INVALID INPUT] Please enter numeric values for ID and amount.");
                    } catch (Exception e) {
                        System.out.println(">> [SYSTEM ERROR] " + e.getMessage());
                    }
                    break;

                case "3":
                    System.out.println("\n--- SIMULATING 3 SIMULTANEOUS BIDDER THREADS ---");
                    System.out.print("Enter target Item ID: ");
                    int testItemId = Integer.parseInt(scanner.nextLine());

                    // Launch three competing threads targeting the exact same item
                    Thread t1 = new Thread(() -> makeTestBid(testItemId, "Alice_Thread", 2100.0));
                    Thread t2 = new Thread(() -> makeTestBid(testItemId, "Bob_Thread", 2100.0));
                    Thread t3 = new Thread(() -> makeTestBid(testItemId, "Charlie_Thread", 2500.0));

                    t1.start();
                    t2.start();
                    t3.start();

                    try {
                        t1.join();
                        t2.join();
                        t3.join();
                    } catch (InterruptedException ignored) {}

                    System.out.println(">> Concurrency simulation finished. Check database state.");
                    break;

                case "4":
                    running = false;
                    timerService.stop();
                    System.out.println("Exiting FlashBid Engine. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid selection. Choose 1-4.");
            }
        }
        scanner.close();
    }

    private static void makeTestBid(int itemId, String user, double amount) {
        try {
            boolean res = BiddingEngine.placeBid(itemId, user, amount);
            if (res) System.out.println(">> [" + user + "] WON bid at Rs." + amount);
        } catch (BidTooLowException | AuctionClosedException e) {
            System.out.println(">> [" + user + "] BLOCKED: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(">> [" + user + "] ERROR: " + e.getMessage());
        }
    }
}