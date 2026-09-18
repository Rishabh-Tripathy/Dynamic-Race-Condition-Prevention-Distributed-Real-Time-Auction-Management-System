package com.flashbid.dao;

import com.flashbid.config.DatabaseConnection;
import com.flashbid.model.Item;
import com.flashbid.model.ReserveItem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    public List<Item> getAllOpenItems() {
        List<Item> items = new ArrayList<>();
        String query = "SELECT * FROM items WHERE status = 'OPEN'";
        Connection conn = DatabaseConnection.getConnection();

        if (conn == null) return items;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                ReserveItem item = new ReserveItem(
                    rs.getInt("item_id"),
                    rs.getString("title"),
                    rs.getDouble("reserve_price"),
                    rs.getDouble("current_bid"),
                    rs.getString("highest_bidder"),
                    rs.getString("status"),
                    rs.getLong("end_time")
                );
                items.add(item);
            }
        } catch (SQLException e) {
            System.err.println("[ItemDAO] Fetch failed: " + e.getMessage());
        }
        return items;
    }
}