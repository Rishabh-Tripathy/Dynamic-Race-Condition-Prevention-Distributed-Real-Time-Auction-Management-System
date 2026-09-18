package com.flashbid.service;

import com.flashbid.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuctionTimerService implements Runnable {
    private volatile boolean running = true;

    @Override
    public void run() {
        while (running) {
            try {
                checkAndCloseExpiredAuctions();
                Thread.sleep(3000); // Check every 3 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void checkAndCloseExpiredAuctions() {
        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        String selectSql = "SELECT item_id, title, highest_bidder, current_bid FROM items WHERE status = 'OPEN' AND end_time <= ?";
        String updateSql = "UPDATE items SET status = 'CLOSED' WHERE item_id = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return;

        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setLong(1, currentTimeSeconds);
            ResultSet rs = selectStmt.executeQuery();

            while (rs.next()) {
                int itemId = rs.getInt("item_id");
                String title = rs.getString("title");
                String winner = rs.getString("highest_bidder");
                double finalPrice = rs.getDouble("current_bid");

                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, itemId);
                    updateStmt.executeUpdate();
                }

                System.out.println("\n[DAEMON EVENT] Auction ID #" + itemId + " (" + title + ") has EXPIRED!");
                System.out.println("[DAEMON EVENT] Winner: " + winner + " at Rs." + finalPrice);

                // Java File I/O: Generate settlement receipt
                BiddingEngine.generateReceipt(itemId, title, winner, finalPrice);
            }
        } catch (SQLException e) {
            System.err.println("[TimerService] Error checking expired auctions: " + e.getMessage());
        }
    }

    public void stop() {
        this.running = false;
    }
}