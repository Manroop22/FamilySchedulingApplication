package com.example.familyschedulingapplication.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familyschedulingapplication.Models.Bill;
import com.example.familyschedulingapplication.R;

import java.util.ArrayList;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {
    private static final String TAG = "BillAdapter";
    private ArrayList<Bill> billList;

    public BillAdapter(ArrayList billList) {
        this.billList = billList;
    }

    @NonNull
    @Override
    public BillAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.bill_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BillAdapter.ViewHolder holder, int position) {
        holder.nameView.setText(billList.get(position).getName());
        holder.dateView.setText(billList.get(position).getDate().toString());
        holder.amountView.setText("$ "+billList.get(position).getAmount());
    }

    @Override
    public int getItemCount() {
        return billList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameView;
        TextView dateView;
        TextView amountView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.nameView);
            dateView = itemView.findViewById(R.id.msgText);
            amountView=itemView.findViewById(R.id.billAmountView);
            nameView.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {

        }
    }
}
