package com.Kwaila.smartpantrymanger.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.Kwaila.smartpantrymanger.R;
import com.Kwaila.smartpantrymanger.model.PantryItem;
import java.util.ArrayList;
import java.util.List;

/** Binds PantryItem objects from the database to rows in the RecyclerView. */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    // Lets the Activity react to taps without the adapter knowing about the Activity
    public interface Listener {
        void onItemClick(PantryItem item);
        void onItemLongClick(PantryItem item);
    }

    private List<PantryItem> items = new ArrayList<>();
    private final Listener listener;

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }

    // Called by the Activity when fresh data arrives from the database
    public void setItems(List<PantryItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.name.setText(item.name);

        // Show 2 instead of 2.0 for whole numbers
        String qty = (item.quantity == Math.floor(item.quantity))
                ? String.valueOf((long) item.quantity)
                : String.valueOf(item.quantity);
        holder.quantity.setText(qty + " " + item.unit);

        if (item.expiryDate == null || item.expiryDate.isEmpty()) {
            holder.expiry.setText("No expiry date");
        } else {
            holder.expiry.setText("Expires: " + item.expiryDate);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(item);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Holds references to the views in one row so they aren't looked up repeatedly
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, quantity, expiry;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textName);
            quantity = itemView.findViewById(R.id.textQuantity);
            expiry = itemView.findViewById(R.id.textExpiry);
        }
    }
}