package com.Kwaila.smartpantrymanger.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/** A single ingredient the user currently has at home. */
@Entity(tableName = "pantry_items")
public class PantryItem {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public double quantity;
    public String unit;        // e.g. "g", "ml", "pcs"
    public String expiryDate;  // nullable, stored as "yyyy-MM-dd"

    public PantryItem(String name, double quantity, String unit, String expiryDate) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }
}
