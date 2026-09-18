package com.flashbid.model;

public class ReserveItem extends Item {

    public ReserveItem(int itemId, String title, double reservePrice, double currentBid, String highestBidder, String status, long endTime) {
        super(itemId, title, "RESERVE", reservePrice, currentBid, highestBidder, status, endTime);
    }

    @Override
    public boolean isBidValid(double proposedBid) {
        // ReserveItem requires bid to beat current bid AND satisfy hidden reserve threshold
        return (proposedBid > this.currentBid) && (proposedBid >= this.reservePrice);
    }
}