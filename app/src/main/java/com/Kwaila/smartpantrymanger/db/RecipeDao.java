
package com.Kwaila.smartpantrymanger.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.Kwaila.smartpantrymanger.model.Recipe;
import com.Kwaila.smartpantrymanger.model.RecipeIngredient;
import com.Kwaila.smartpantrymanger.model.RecipeWithIngredients;

import java.util.List;

@Dao
public interface RecipeDao {

    @Insert
    long insertRecipe(Recipe recipe);

    @Insert
    void insertIngredients(List<RecipeIngredient> ingredients);

    @Query("SELECT COUNT(*) FROM recipes")
    int getRecipeCount();

    @Transaction
    @Query("SELECT * FROM recipes ORDER BY name ASC")
    List<RecipeWithIngredients> getAllWithIngredients();

    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :id")
    RecipeWithIngredients getById(int id);
}
