package com.flashbid.service;

import com.flashbid.config.DatabaseConnection;
import com.flashbid.dao.BidDAO;
import com.flashbid.exception.AuctionClosedException;
import com.flashbid.exception.BidTooLowException;
import com.flashbid.model.Bid;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

public class BiddingEngine {
    private static final ReentrantLock lock = new ReentrantLock();
    private static final BidDAO bidDAO = new BidDAO();

    public static boolean placeBid(int itemId, String bidderName, double bidAmount)
            throws BidTooLowException, AuctionClosedException, SQLException {

        lock.lock(); // Prevent concurrent access in-memory
        Connection conn = DatabaseConnection.getConnection();

        try {
            conn.setAutoCommit(false); // Begin ACID Transaction

            // Pessimistic Locking: Lock the row during verification
            String selectSql = "SELECT reserve_price, current_bid, status, end_time FROM items WHERE item_id = ? FOR UPDATE";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, itemId);
                ResultSet rs = selectStmt.executeQuery();

                if (!rs.next()) {
                    conn.rollback();
                    System.out.println("[!] Item not found.");
                    return false;
                }

                String status = rs.getString("status");
                double currentBid = rs.getDouble("current_bid");
                double reservePrice = rs.getDouble("reserve_price");
                long endTime = rs.getLong("end_time");
                long now = System.currentTimeMillis() / 1000;

                if (!"OPEN".equalsIgnoreCase(status) || now > endTime) {
                    conn.rollback();
                    throw new AuctionClosedException("Auction for item #" + itemId + " is CLOSED.");
                }

                if (bidAmount <= currentBid || bidAmount < reservePrice) {
                    conn.rollback();
                    throw new BidTooLowException("Bid Rs." + bidAmount + " is too low! Must exceed current bid (Rs." + currentBid + ") and reserve threshold.");
                }

                // Update row
                String updateSql = "UPDATE items SET current_bid = ?, highest_bidder = ? WHERE item_id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setDouble(1, bidAmount);
                    updateStmt.setString(2, bidderName);
                    updateStmt.setInt(3, itemId);
                    updateStmt.executeUpdate();
                }

                // Log bid entry
                bidDAO.logBid(new Bid(itemId, bidderName, bidAmount));

                conn.commit(); // Transaction success
                return true;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } finally {
            conn.setAutoCommit(true);
            lock.unlock(); // Release lock
        }
    }

    // Java File Streams (Unit 4 syllabus requirement)
    public static void generateReceipt(int itemId, String title, String winner, double amount) {
        File dir = new File("receipts");
        if (!dir.exists()) dir.mkdir();

        String filename = "receipts/settlement_item_" + itemId + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("==================================================\n");
            writer.write("            FLASHBID SETTLEMENT INVOICE           \n");
            writer.write("==================================================\n");
            writer.write("Item Reference : #" + itemId + "\n");
            writer.write("Item Title     : " + title + "\n");
            writer.write("Winner         : " + winner + "\n");
            writer.write("Settled Amount : Rs." + amount + "\n");
            writer.write("Status         : CLOSED & PERSISTED\n");
            writer.write("==================================================\n");
            System.out.println("[I/O] Settlement invoice written to: " + filename);
        } catch (IOException e) {
            System.err.println("[I/O Error] Could not write settlement: " + e.getMessage());
        }
    }
}