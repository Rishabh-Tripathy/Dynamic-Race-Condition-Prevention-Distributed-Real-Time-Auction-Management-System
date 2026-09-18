package com.flashbid.dao;

import com.flashbid.config.DatabaseConnection;
import com.flashbid.model.Bid;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BidDAO {

    public void logBid(Bid bid) {
        String query = "INSERT INTO bid_history (item_id, bidder_name, bid_amount) VALUES (?, ?, ?)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bid.getItemId());
            stmt.setString(2, bid.getBidderName());
            stmt.setDouble(3, bid.getBidAmount());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[BidDAO] Failed to record bid history: " + e.getMessage());
        }
    }
}