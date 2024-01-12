package com.example.ecommerce.emailfeedback;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;

import java.util.List;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ReceiptViewHolder> {
    private List<ReceiptItem> receiptItemList;

    public ReceiptAdapter(List<ReceiptItem> receiptItemList) {
        this.receiptItemList = receiptItemList;
    }

    @NonNull
    @Override
    public ReceiptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receipt_view, parent, false);
        return new ReceiptViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptViewHolder holder, int position) {
        ReceiptItem receiptItem = receiptItemList.get(position);

        // Встановлюємо дані відповідно до ReceiptItem
        holder.receiptNameTextView.setText(receiptItem.getReceiptName());
        holder.receiptCountTextView.setText(String.valueOf(receiptItem.getReceiptCount()));
        holder.receiptPriceTextView.setText(String.valueOf(receiptItem.getReceiptPrice()));
    }

    @Override
    public int getItemCount() {
        return receiptItemList.size();
    }

    static class ReceiptViewHolder extends RecyclerView.ViewHolder {
        TextView receiptNameTextView;
        TextView receiptCountTextView;
        TextView receiptPriceTextView;

        ReceiptViewHolder(@NonNull View itemView) {
            super(itemView);

            receiptNameTextView = itemView.findViewById(R.id.receiptName);
            receiptCountTextView = itemView.findViewById(R.id.receiptCount);
            receiptPriceTextView = itemView.findViewById(R.id.receiptPrice);
        }
    }
}

