
package com.Kwaila.smartpantrymanger;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExpiryCheckActivity extends AppCompatActivity {

    private LinearLayout expiryItemsContainer;
    private TextView tvExpirySummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expiry_check);

        expiryItemsContainer = findViewById(R.id.expiryItemsContainer);
        tvExpirySummary = findViewById(R.id.tvExpirySummary);

        Button btnBack = findViewById(R.id.btnExpiryBack);
        btnBack.setOnClickListener(v -> finish());

        checkExpiryDates();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (expiryItemsContainer != null && tvExpirySummary != null) {
            checkExpiryDates();
        }
    }

    private void checkExpiryDates() {
        expiryItemsContainer.removeAllViews();

        android.content.SharedPreferences preferences =
                getSharedPreferences("SmartPantry", MODE_PRIVATE);

        String savedItems = preferences.getString("items", "[]");

        try {
            JSONArray items = new JSONArray(savedItems);

            Calendar todayCalendar = Calendar.getInstance();
            clearTime(todayCalendar);
            Date today = todayCalendar.getTime();

            Calendar weekCalendar = (Calendar) todayCalendar.clone();
            weekCalendar.add(Calendar.DAY_OF_YEAR, 7);
            Date nextWeek = weekCalendar.getTime();

            int expiredCount = 0;
            int expiringSoonCount = 0;
            int noDateCount = 0;

            SimpleDateFormat dateFormat =
                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dateFormat.setLenient(false);

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);

                String name = item.optString("name", "Unnamed item");
                String quantity = item.optString("quantity", "0");
                String expiryDate = item.optString("expiryDate", "");

                TextView itemView = new TextView(this);
                itemView.setTextSize(16);
                itemView.setTextColor(0xFF222222);
                itemView.setPadding(16, 16, 16, 16);

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);

                params.setMargins(0, 0, 0, 12);
                itemView.setLayoutParams(params);
                itemView.setBackgroundColor(0xFFFFFFFF);

                String status;

                if (expiryDate.trim().isEmpty()) {
                    status = "Expiry date not set";
                    noDateCount++;
                } else {
                    ParsePosition position = new ParsePosition(0);
                    Date parsedDate = dateFormat.parse(expiryDate, position);

                    if (parsedDate == null
                            || position.getIndex() != expiryDate.length()) {
                        status = "Invalid expiry date";
                        noDateCount++;
                    } else {
                        Calendar expiryCalendar = Calendar.getInstance();
                        expiryCalendar.setTime(parsedDate);
                        clearTime(expiryCalendar);
                        parsedDate = expiryCalendar.getTime();

                        if (parsedDate.before(today)) {
                            status = "EXPIRED";
                            expiredCount++;
                        } else if (!parsedDate.after(nextWeek)) {
                            status = "EXPIRING SOON";
                            expiringSoonCount++;
                        } else {
                            status = "Not expiring soon";
                        }
                    }
                }

                itemView.setText(
                        "Item: " + name
                                + "\nQuantity: " + quantity
                                + "\nExpiry date: "
                                + (expiryDate.isEmpty() ? "Not set" : expiryDate)
                                + "\nStatus: " + status);

                if ("EXPIRED".equals(status)) {
                    itemView.setTextColor(0xFFB00020);
                } else if ("EXPIRING SOON".equals(status)) {
                    itemView.setTextColor(0xFFB26A00);
                }

                expiryItemsContainer.addView(itemView);
            }

            tvExpirySummary.setText(
                    "Pantry items: " + items.length()
                            + "\nExpired: " + expiredCount
                            + "\nExpiring within 7 days: " + expiringSoonCount
                            + "\nMissing or invalid dates: " + noDateCount);

            if (items.length() == 0) {
                TextView emptyMessage = new TextView(this);
                emptyMessage.setText("Your pantry is empty. Add items to check their expiry dates.");
                emptyMessage.setTextSize(16);
                emptyMessage.setGravity(Gravity.CENTER);
                emptyMessage.setPadding(16, 24, 16, 24);
                expiryItemsContainer.addView(emptyMessage);
            }

        } catch (Exception e) {
            tvExpirySummary.setText("Could not load pantry items.");
            TextView errorView = new TextView(this);
            errorView.setText("Please check your saved pantry data.");
            errorView.setPadding(16, 16, 16, 16);
            expiryItemsContainer.addView(errorView);
        }
    }

    private void clearTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}
