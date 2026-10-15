package com.Kwaila.smartpantrymanger.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

/** Lets Room load a recipe together with its ingredient lines. */
public class RecipeWithIngredients {
    @Embedded
    public Recipe recipe;

    @Relation(parentColumn = "id", entityColumn = "recipeId")
    public List<RecipeIngredient> ingredients;
}