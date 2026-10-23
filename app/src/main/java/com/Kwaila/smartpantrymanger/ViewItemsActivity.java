
package com.Kwaila.smartpantrymanger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ViewItemsActivity extends AppCompatActivity {

    private ListView listPantryItems;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_items);

        listPantryItems = findViewById(R.id.listPantryItems);
        tvEmpty = findViewById(R.id.tvEmpty);

        Button btnBackHome = findViewById(R.id.btnBackHome);
        btnBackHome.setOnClickListener(v -> finish());

        listPantryItems.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(
                    ViewItemsActivity.this, EditItemActivity.class);

            intent.putExtra("item_index", position);
            startActivity(intent);
        });

        displayItems();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (listPantryItems != null) {
            displayItems();
        }
    }

    private void displayItems() {
        SharedPreferences preferences =
                getSharedPreferences("SmartPantry", MODE_PRIVATE);

        String savedItems = preferences.getString("items", "[]");
        ArrayList<String> displayList = new ArrayList<>();

        try {
            JSONArray items = new JSONArray(savedItems);

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            dateFormat.setLenient(false);

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);

                String name = item.optString("name", "Unknown item");
                int quantity = item.optInt("quantity", 0);
                String expiry = item.optString("expiryDate", "Not set");

                String status = getExpiryStatus(expiry, dateFormat);

                displayList.add(
                        name
                                + "\nQuantity: " + quantity
                                + "\nExpiry: " + expiry
                                + "\nStatus: " + status
                                + "\nTap to edit or delete"
                );
            }

        } catch (JSONException e) {
            displayList.clear();
            displayList.add("Could not read pantry items.");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                displayList);

        listPantryItems.setAdapter(adapter);

        boolean isEmpty = displayList.isEmpty();

        tvEmpty.setVisibility(
                isEmpty ? TextView.VISIBLE : TextView.GONE);

        listPantryItems.setVisibility(
                isEmpty ? ListView.GONE : ListView.VISIBLE);
    }

    private String getExpiryStatus(
            String expiry, SimpleDateFormat dateFormat) {

        try {
            Date expiryDate = dateFormat.parse(expiry);

            if (expiryDate == null) {
                return "Date unavailable";
            }

            Calendar today = Calendar.getInstance();
            clearTime(today);

            Calendar expiryCalendar = Calendar.getInstance();
            expiryCalendar.setTime(expiryDate);
            clearTime(expiryCalendar);

            long difference = expiryCalendar.getTimeInMillis()
                    - today.getTimeInMillis();

            long days = difference / (24L * 60 * 60 * 1000);

            if (days < 0) {
                return "EXPIRED";
            } else if (days == 0) {
                return "Expires today";
            } else if (days <= 3) {
                return "Expiring soon";
            } else {
                return "Fresh";
            }

        } catch (ParseException e) {
            return "Date unavailable";
        }
    }

    private void clearTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}
