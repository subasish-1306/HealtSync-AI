package com.healthsync.inventory.entity;

/**
 * Types of inventory transaction events.
 */
public enum InventoryTransactionType {
    RECEIVE,
    DISPENSE,
    TRANSFER_OUT,
    TRANSFER_IN,
    EXPIRED,
    ADJUSTMENT
}
