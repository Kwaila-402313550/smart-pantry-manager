
package com.Kwaila.smartpantrymanger;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnAddItem;
    private Button btnViewItems;
    private Button btnExpiry;
    private Button btnSuggestRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect the buttons from activity_main.xml
        btnAddItem = findViewById(R.id.btnAddItem);
        btnViewItems = findViewById(R.id.btnViewItems);
        btnExpiry = findViewById(R.id.btnExpiry);
        btnSuggestRecipes = findViewById(R.id.btnSuggestRecipes);

        // Open Add Pantry Item screen
        btnAddItem.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AddItemActivity.class
            );
            startActivity(intent);
        });

        // Open View Pantry Items screen
        btnViewItems.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    ViewItemsActivity.class
            );
            startActivity(intent);
        });

        // Open Check Expiry Dates screen
        btnExpiry.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    ExpiryCheckActivity.class
            );
            startActivity(intent);
        });

        // Open Recipe Suggestions screen
        btnSuggestRecipes.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    RecipeSuggestionsActivity.class
            );
            startActivity(intent);
        });
    }
}
