package com.Kwaila.smartpantrymanger.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/** A recipe; its ingredients live in the RecipeIngredient table. */
@Entity(tableName = "recipes")
public class Recipe {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String steps;

    public Recipe(String name, String steps) {
        this.name = name;
        this.steps = steps;
    }
}