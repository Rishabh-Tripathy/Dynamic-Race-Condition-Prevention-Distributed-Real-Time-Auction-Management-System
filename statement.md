# FlashBid: Real-Time Concurrent Auction System

## Problem Statement
In online auctions, peak bidding windows create severe race conditions where simultaneous bids at the identical millisecond result in lost bids, double-assignment, or database deadlock.

## Scope
FlashBid provides a concurrency-safe auction engine with live countdown daemons, role-based buyer/seller flows, transactional database locks, and automated disk settlement receipts.

## Target Users
Online marketplace bidders, auction administrators, and transaction auditors.

## High-Level Features
- Synchronized bidding engine preventing race conditions
- Background daemon threads for live auction countdowns and automatic closures
- JDBC transaction management with pessimistic row locking
- Automated invoice and audit log export using Java File Streams