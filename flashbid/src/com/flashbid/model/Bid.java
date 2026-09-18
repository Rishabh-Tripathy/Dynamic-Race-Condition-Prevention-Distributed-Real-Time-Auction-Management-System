package com.flashbid.model;

public class Bid {
    private int itemId;
    private String bidderName;
    private double bidAmount;

    public Bid(int itemId, String bidderName, double bidAmount) {
        this.itemId = itemId;
        this.bidderName = bidderName;
        this.bidAmount = bidAmount;
    }

    public int getItemId() { return itemId; }
    public String getBidderName() { return bidderName; }
    public double getBidAmount() { return bidAmount; }
}