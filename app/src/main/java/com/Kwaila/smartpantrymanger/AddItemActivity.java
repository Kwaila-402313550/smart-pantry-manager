
package com.Kwaila.smartpantrymanger;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

public class AddItemActivity extends AppCompatActivity {

    private EditText etFoodName;
    private EditText etQuantity;
    private Button btnSelectDate;

    private String expiryDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        etFoodName = findViewById(R.id.etFoodName);
        etQuantity = findViewById(R.id.etQuantity);
        btnSelectDate = findViewById(R.id.btnSelectDate);

        Button btnSaveItem = findViewById(R.id.btnSaveItem);
        Button btnBack = findViewById(R.id.btnBack);

        btnSelectDate.setOnClickListener(v -> showDatePicker());

        btnSaveItem.setOnClickListener(v -> saveItem());

        btnBack.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    expiryDate = String.format(
                            Locale.getDefault(),
                            "%02d/%02d/%04d",
                            dayOfMonth,
                            month + 1,
                            year
                    );

                    btnSelectDate.setText(expiryDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void saveItem() {
        String foodName = etFoodName.getText()
                .toString().trim();

        String quantity = etQuantity.getText()
                .toString().trim();

        if (foodName.isEmpty()) {
            etFoodName.setError("Enter a food name");
            etFoodName.requestFocus();
            return;
        }

        if (quantity.isEmpty()
                || Integer.parseInt(quantity) <= 0) {
            etQuantity.setError("Enter a quantity greater than zero");
            etQuantity.requestFocus();
            return;
        }

        if (expiryDate.isEmpty()) {
            Toast.makeText(this,
                    "Please select an expiry date",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Read previously saved items.
            android.content.SharedPreferences preferences =
                    getSharedPreferences("SmartPantry", MODE_PRIVATE);

            String savedItems = preferences.getString("items", "[]");
            JSONArray items = new JSONArray(savedItems);

            // Create the new pantry item.
            JSONObject item = new JSONObject();
            item.put("name", foodName);
            item.put("quantity", Integer.parseInt(quantity));
            item.put("expiryDate", expiryDate);

            items.put(item);

            // Save the updated list on the phone.
            preferences.edit()
                    .putString("items", items.toString())
                    .apply();

            Toast.makeText(this,
                    foodName + " saved successfully!",
                    Toast.LENGTH_LONG).show();

            finish();

        } catch (JSONException e) {
            Toast.makeText(this,
                    "Could not save the item",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
