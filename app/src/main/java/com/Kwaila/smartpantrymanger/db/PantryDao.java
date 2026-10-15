package com.Kwaila.smartpantrymanger.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.Kwaila.smartpantrymanger.model.PantryItem;
import java.util.List;

@Dao
public interface PantryDao {
    @Insert
    long insert(PantryItem item);            // Create

    @Query("SELECT * FROM pantry_items ORDER BY name ASC")
    List<PantryItem> getAll();               // Read

    @Query("SELECT * FROM pantry_items WHERE id = :id")
    PantryItem getById(int id);

    @Update
    void update(PantryItem item);            // Update

    @Delete
    void delete(PantryItem item);            // Delete
}