
package com.Kwaila.smartpantrymanger;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Kwaila.smartpantrymanger.db.AppDatabase;
import com.Kwaila.smartpantrymanger.model.RecipeIngredient;
import com.Kwaila.smartpantrymanger.model.RecipeWithIngredients;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class RecipeSuggestionsActivity extends AppCompatActivity {

    private LinearLayout recipeContainer;

    private final Set<String> pantryIngredients = new HashSet<>();

    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_suggestions);

        recipeContainer = findViewById(R.id.recipeContainer);

        Button btnBack = findViewById(R.id.btnRecipeBack);
        btnBack.setOnClickListener(v -> finish());

        database = AppDatabase.getInstance(getApplicationContext());

        loadPantryAndRecipes();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (database != null) {
            loadPantryAndRecipes();
        }
    }

    // Read pantry items from the same storage used by AddItemActivity.
    private void loadPantryIngredients() {
        pantryIngredients.clear();

        try {
            String savedItems = getSharedPreferences(
                    "SmartPantry",
                    MODE_PRIVATE
            ).getString("items", "[]");

            JSONArray items = new JSONArray(savedItems);

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.optJSONObject(i);

                if (item == null) {
                    continue;
                }

                String name = item.optString("name", "").trim();
                double quantity = item.optDouble("quantity", 0);

                if (!name.isEmpty() && quantity > 0) {
                    pantryIngredients.add(normalizeIngredient(name));
                }
            }

        } catch (Exception e) {
            pantryIngredients.clear();
        }
    }

    private String normalizeIngredient(String name) {
        String ingredient = name.toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("\\s+", " ");

        switch (ingredient) {
            case "eggs":
                return "egg";
            case "tomatoes":
                return "tomato";
            case "potatoes":
                return "potato";
            case "onions":
                return "onion";
            case "bananas":
                return "banana";
            case "apples":
                return "apple";
            default:
                return ingredient;
        }
    }

    private boolean hasIngredient(String name) {
        return pantryIngredients.contains(normalizeIngredient(name));
    }

    private void loadPantryAndRecipes() {
        AppDatabase.dbExecutor.execute(() -> {
            // Existing pantry items remain in SharedPreferences.
            loadPantryIngredients();

            // Recipes and their ingredient lists come from Room.
            List<RecipeWithIngredients> recipes =
                    database.recipeDao().getAllWithIngredients();

            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                showRecipeSuggestions(recipes);
            });
        });
    }

    private void showRecipeSuggestions(
            List<RecipeWithIngredients> recipes) {

        recipeContainer.removeAllViews();

        if (pantryIngredients.isEmpty()) {
            addMessage(
                    "Your pantry is empty. Add food items with a quantity "
                            + "greater than zero to see recipe suggestions."
            );
            return;
        }

        addMessage("Recipes matched to your available pantry ingredients:");

        if (recipes == null || recipes.isEmpty()) {
            addMessage(
                    "No recipes are saved in the database yet."
            );
            return;
        }

        int matchCount = 0;

        for (RecipeWithIngredients entry : recipes) {

            if (entry == null || entry.recipe == null
                    || entry.ingredients == null
                    || entry.ingredients.isEmpty()) {
                continue;
            }

            boolean allAvailable = true;

            for (RecipeIngredient ingredient : entry.ingredients) {
                if (ingredient.ingredientName == null
                        || !hasIngredient(ingredient.ingredientName)) {
                    allAvailable = false;
                    break;
                }
            }

            if (allAvailable) {
                addRecipeCard(entry);
                matchCount++;
            }
        }

        if (matchCount == 0) {
            addMessage(
                    "No saved recipes match all your pantry ingredients. "
                            + "Try adding more ingredients."
            );
        }
    }

    private void addRecipeCard(RecipeWithIngredients entry) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(24, 24, 24, 24);
        card.setBackgroundColor(Color.rgb(232, 245, 233));

        TextView title = new TextView(this);
        title.setText(entry.recipe.name);
        title.setTextSize(20);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.rgb(46, 125, 50));

        TextView ingredients = new TextView(this);
        ingredients.setTextSize(16);
        ingredients.setTextColor(Color.rgb(34, 34, 34));
        ingredients.setPadding(0, 12, 0, 8);

        StringBuilder ingredientText =
                new StringBuilder("Ingredients:\n");

        for (RecipeIngredient ingredient : entry.ingredients) {
            ingredientText.append("- ")
                    .append(ingredient.ingredientName)
                    .append(": ")
                    .append(ingredient.quantity);

            if (ingredient.unit != null
                    && !ingredient.unit.trim().isEmpty()) {
                ingredientText.append(" ").append(ingredient.unit);
            }

            ingredientText.append("\n");
        }

        ingredients.setText(ingredientText.toString().trim());

        TextView instructions = new TextView(this);
        instructions.setTextSize(16);
        instructions.setTextColor(Color.rgb(34, 34, 34));
        instructions.setPadding(0, 8, 0, 0);
        instructions.setText(
                "Instructions:\n" + entry.recipe.steps
        );

        card.addView(title);
        card.addView(ingredients);
        card.addView(instructions);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 12, 0, 8);
        recipeContainer.addView(card, params);
    }

    private void addMessage(String message) {
        TextView text = new TextView(this);
        text.setText(message);
        text.setTextSize(16);
        text.setTextColor(Color.rgb(51, 51, 51));
        text.setGravity(Gravity.START);
        text.setPadding(8, 12, 8, 12);

        recipeContainer.addView(text);
    }
}
