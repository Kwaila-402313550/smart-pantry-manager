
package com.Kwaila.smartpantrymanger;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditItemActivity extends AppCompatActivity {

    private EditText etEditName;
    private EditText etEditQuantity;
    private Button btnEditExpiryDate;

    private String expiryDate = "";
    private int itemIndex = -1;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        etEditName = findViewById(R.id.etEditName);
        etEditQuantity = findViewById(R.id.etEditQuantity);
        btnEditExpiryDate = findViewById(R.id.btnEditExpiryDate);

        Button btnSaveChanges = findViewById(R.id.btnSaveChanges);
        Button btnDeleteItem = findViewById(R.id.btnDeleteItem);
        Button btnCancelEdit = findViewById(R.id.btnCancelEdit);

        preferences = getSharedPreferences(
                "SmartPantry", MODE_PRIVATE);

        itemIndex = getIntent().getIntExtra("item_index", -1);

        if (itemIndex < 0 || !loadItem()) {
            Toast.makeText(this,
                    "Item could not be found. Return to the pantry and try again.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        btnEditExpiryDate.setOnClickListener(v -> showDatePicker());
        btnSaveChanges.setOnClickListener(v -> saveChanges());
        btnDeleteItem.setOnClickListener(v -> confirmDelete());
        btnCancelEdit.setOnClickListener(v -> finish());
    }

    private JSONArray getItems() throws JSONException {
        String saved = preferences.getString("items", "[]");
        return new JSONArray(saved);
    }

    private boolean loadItem() {
        try {
            JSONArray items = getItems();

            if (itemIndex >= items.length()) {
                return false;
            }

            JSONObject item = items.getJSONObject(itemIndex);

            etEditName.setText(item.optString("name", ""));
            etEditQuantity.setText(
                    String.valueOf(item.optInt("quantity", 1)));

            expiryDate = item.optString("expiryDate", "");
            btnEditExpiryDate.setText(
                    expiryDate.isEmpty()
                            ? "Select expiry date"
                            : expiryDate);

            return true;

        } catch (JSONException e) {
            return false;
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        if (!expiryDate.isEmpty()) {
            try {
                SimpleDateFormat format =
                        new SimpleDateFormat(
                                "dd/MM/yyyy", Locale.getDefault());
                format.setLenient(false);

                Date date = format.parse(expiryDate);

                if (date != null) {
                    calendar.setTime(date);
                }
            } catch (Exception ignored) {
                // Use today's date if the saved date is invalid.
            }
        }

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (picker, year, month, day) -> {
                    expiryDate = String.format(
                            Locale.getDefault(),
                            "%02d/%02d/%04d",
                            day, month + 1, year);

                    btnEditExpiryDate.setText(expiryDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        dialog.show();
    }

    private void saveChanges() {
        String name = etEditName.getText().toString().trim();
        String quantityText =
                etEditQuantity.getText().toString().trim();

        if (name.isEmpty()) {
            etEditName.setError("Enter a food name");
            etEditName.requestFocus();
            return;
        }

        final int quantity;

        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            etEditQuantity.setError("Enter a valid whole quantity");
            etEditQuantity.requestFocus();
            return;
        }

        if (quantity <= 0) {
            etEditQuantity.setError(
                    "Quantity must be greater than zero");
            etEditQuantity.requestFocus();
            return;
        }

        if (expiryDate.isEmpty()) {
            Toast.makeText(this,
                    "Please select an expiry date",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONArray items = getItems();

            if (itemIndex < 0 || itemIndex >= items.length()) {
                Toast.makeText(this,
                        "Item no longer exists. Refresh your pantry.",
                        Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            JSONObject item = items.getJSONObject(itemIndex);
            item.put("name", name);
            item.put("quantity", quantity);
            item.put("expiryDate", expiryDate);

            boolean saved = preferences.edit()
                    .putString("items", items.toString())
                    .commit();

            if (!saved) {
                Toast.makeText(this,
                        "Could not save changes. Please try again.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            Toast.makeText(this,
                    "Changes saved successfully",
                    Toast.LENGTH_SHORT).show();

            finish();

        } catch (JSONException e) {
            Toast.makeText(this,
                    "Could not update item",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Pantry Item")
                .setMessage("Are you sure you want to delete "
                        + etEditName.getText().toString().trim() + "?")
                .setPositiveButton("Delete",
                        (dialog, which) -> deleteItem())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteItem() {
        try {
            JSONArray items = getItems();

            if (itemIndex < 0 || itemIndex >= items.length()) {
                Toast.makeText(this,
                        "Item no longer exists",
                        Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            JSONArray updatedItems = new JSONArray();

            for (int i = 0; i < items.length(); i++) {
                if (i != itemIndex) {
                    updatedItems.put(items.getJSONObject(i));
                }
            }

            boolean saved = preferences.edit()
                    .putString("items", updatedItems.toString())
                    .commit();

            if (!saved) {
                Toast.makeText(this,
                        "Could not delete item. Please try again.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            Toast.makeText(this,
                    "Item deleted successfully",
                    Toast.LENGTH_SHORT).show();

            finish();

        } catch (JSONException e) {
            Toast.makeText(this,
                    "Could not delete item",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
