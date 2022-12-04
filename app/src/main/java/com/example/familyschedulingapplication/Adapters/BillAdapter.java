package com.example.familyschedulingapplication.Adapters;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familyschedulingapplication.BillDetails;
import com.example.familyschedulingapplication.Models.Bill;
import com.example.familyschedulingapplication.Models.Member;
import com.example.familyschedulingapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.ArrayList;
import java.util.Date;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.ViewHolder> {
    private static final String TAG = "BillAdapter";
    private final ArrayList<Bill> billList;
    int count=0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public BillAdapter(ArrayList<Bill> billList) {
        this.billList = billList;
    }

    @NonNull
    @Override
    public BillAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i (TAG,  "onCreateViewHolder: "+ count++);
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.bill_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillAdapter.ViewHolder holder, int position) {
        holder.nameView.setText(billList.get(position).getName());
        holder.dateView.setText(billList.get(position).getDueDate().toString());
//        holder.amountView.setText("$ "+billList.get(position).getAmount());
        // format the amount to 2 decimal places
        holder.amountView.setText(String.format("$ %.2f", billList.get(position).getAmount()));
        // if bill is overdue, change background color to red
        if (billList.get(position).getDueDate().before(new Date()) && !billList.get(position).getPaid()) {
            holder.amountView.setBackgroundColor(Color.parseColor("#FFCDD2"));
        }
    }

    @Override
    public int getItemCount() {
        return billList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameView;
        TextView dateView;
        TextView amountView;
        ImageButton billOptions;
        ConstraintLayout billItem;
//        Bill bill;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.nameView);
            dateView = itemView.findViewById(R.id.msgText);
            amountView = itemView.findViewById(R.id.billAmountView);
            billOptions = itemView.findViewById(R.id.billOptions);
            billItem = itemView.findViewById(R.id.billItemLayout);
            billOptions.setOnClickListener(view -> {
                ArrayList<PowerMenuItem> list = new ArrayList<>();
                list.add(new PowerMenuItem("Pay Now", false));
                list.add(new PowerMenuItem("View Bill", false));
                list.add(new PowerMenuItem("Edit Bill", false));
                list.add(new PowerMenuItem("Delete Bill", false));
                PowerMenu powerMenu = new PowerMenu.Builder(itemView.getContext())
                        .addItemList(list) // list has "Novel", "Poetry", "Art"
                        .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
                        .setMenuRadius(10f) // sets the corner radius.
                        .setMenuShadow(10f) // sets the shadow.
                        .setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.black))
                        .setTextGravity(Gravity.START)
                        .setTextSize(16)
                        .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                        .setSelectedTextColor(Color.WHITE)
                        .setMenuColor(Color.WHITE)
                        .setSelectedMenuColor(ContextCompat.getColor(itemView.getContext(), R.color.purple_500)).build();
                powerMenu.setOnMenuItemClickListener((position, item) -> {
                    powerMenu.dismiss();
                    Bundle billBundle = new Bundle();
                    billBundle.putString("billId", billList.get(getAdapterPosition()).getBillId());
                    Class<?> destination = BillDetails.class;
                    switch (position) {
                        case 0:
                            Log.d(TAG, "Pay Now");
                            String link = billList.get(getAdapterPosition()).getLink();
                            // browser intent
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                            itemView.getContext().startActivity(browserIntent);
                            // alert dialog to confirm payment and update bill status
                            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                            builder.setTitle("Confirm Payment");
                            builder.setMessage("Have you paid this bill?");
                            builder.setPositiveButton("Yes", (dialog, which) -> {
                                // update bill status to paid
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                assert user != null;
                                DocumentReference memRef = db.collection(Member.collection).document(user.getUid());
                                billList.get(getAdapterPosition()).setPaid(true);
                                billList.get(getAdapterPosition()).setPaidBy(memRef);
                                billList.get(getAdapterPosition()).setPaidAt(new Date());
                                billList.get(getAdapterPosition()).setUpdatedAt(new Date());
                                Bill.updateBill(billList.get(getAdapterPosition()), task -> {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Bill status updated to paid");
                                        // notify adapter of change
                                        notifyItemChanged(getAdapterPosition());
                                        // remove bill from list
                                        billList.remove(getAdapterPosition());
                                        // notify adapter of change
                                        notifyItemRemoved(getAdapterPosition());
                                    } else {
                                        Log.d(TAG, "Error updating bill status");
                                    }
                                });
                            });
                            builder.setNegativeButton("No", (dialog, which) -> {
                                // do nothing
                            });
                            builder.show();
                            break;
                        case 1:
                            Log.d(TAG, "View Bill");
                            billBundle.putString("mode", "view");
                            Intent intent2 = new Intent(itemView.getContext(), destination);
                            intent2.putExtras(billBundle);
                            itemView.getContext().startActivity(intent2);
                            break;
                        case 2:
                            Log.d(TAG, "Edit Bill");
                            billBundle.putString("mode", "edit");
                            Intent intent = new Intent(itemView.getContext(), destination);
                            intent.putExtras(billBundle);
                            itemView.getContext().startActivity(intent);
                            break;
                        case 3:
                            Log.d(TAG, "Delete Bill");
                            // alert dialog to confirm deletion
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(itemView.getContext());
                            builder2.setTitle("Confirm Deletion");
                            builder2.setMessage("Are you sure you want to delete this bill?");
                            builder2.setPositiveButton("Yes", (dialog, which) -> {
                                // delete bill from database
                                Bill.deleteBill(billList.get(getAdapterPosition()), task -> {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Bill deleted");
                                        // remove bill from list
                                        billList.remove(getAdapterPosition());
                                        // notify adapter of change
                                        notifyItemRemoved(getAdapterPosition());
                                    } else {
                                        Log.d(TAG, "Error deleting bill");
                                    }
                                });
                            });
                            builder2.setNegativeButton("No", (dialog, which) -> {
                                // do nothing
                            });
                            builder2.show();
                            break;
                    }
                });
                powerMenu.showAsDropDown(view);
            });
        }

        @Override
        public void onClick(View view) {

        }
    }
}
