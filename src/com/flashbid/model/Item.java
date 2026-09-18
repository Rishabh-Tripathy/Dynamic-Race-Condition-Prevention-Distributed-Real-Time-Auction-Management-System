package com.flashbid.model;

public abstract class Item {
    protected int itemId;
    protected String title;
    protected String itemType;
    protected double reservePrice;
    protected double currentBid;
    protected String highestBidder;
    protected String status;
    protected long endTime;

    public Item(int itemId, String title, String itemType, double reservePrice, double currentBid, String highestBidder, String status, long endTime) {
        this.itemId = itemId;
        this.title = title;
        this.itemType = itemType;
        this.reservePrice = reservePrice;
        this.currentBid = currentBid;
        this.highestBidder = highestBidder;
        this.status = status;
        this.endTime = endTime;
    }

    // Abstract method: polymorphic validation rules
    public abstract boolean isBidValid(double proposedBid);

    public int getItemId() { return itemId; }
    public String getTitle() { return title; }
    public String getItemType() { return itemType; }
    public double getReservePrice() { return reservePrice; }
    public double getCurrentBid() { return currentBid; }
    public String getHighestBidder() { return highestBidder; }
    public String getStatus() { return status; }
    public long getEndTime() { return endTime; }

    public void setCurrentBid(double currentBid) { this.currentBid = currentBid; }
    public void setHighestBidder(String highestBidder) { this.highestBidder = highestBidder; }
    public void setStatus(String status) { this.status = status; }
}