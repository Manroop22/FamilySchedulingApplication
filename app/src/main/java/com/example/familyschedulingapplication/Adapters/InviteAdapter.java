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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familyschedulingapplication.BillDetails;
import com.example.familyschedulingapplication.Models.Bill;
import com.example.familyschedulingapplication.Models.HomeInvite;
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

public class InviteAdapter extends RecyclerView.Adapter<InviteAdapter.ViewHolder> {
    private static final String TAG = "InviteAdapter";
    private final ArrayList<HomeInvite> inviteList;
    int count = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public InviteAdapter(ArrayList<HomeInvite> inviteList) {
        this.inviteList = inviteList;
    }

    @NonNull
    @Override
    public InviteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder: " + count++);
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.invite_item, parent, false);
        return new InviteAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InviteAdapter.ViewHolder holder, int position) {
        holder.inviteTextView.setText(inviteList.get(position).getHomeInviteId()); // This needs to be changed
    }

    @Override
    public int getItemCount() {
        return inviteList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView inviteTextView;
        ImageButton acceptBtn;
        ImageButton declineBtn;

        //        Bill bill;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            inviteTextView = itemView.findViewById(R.id.inviteTextView);
            acceptBtn = itemView.findViewById(R.id.inviteAcceptBtn);
            declineBtn = itemView.findViewById(R.id.inviteDeclineBtn);
            acceptBtn.setOnClickListener(view -> {
                // code for accepting the invite
            });

            declineBtn.setOnClickListener(view -> {

            });

        }

        @Override
        public void onClick(View view) {

        }
    }
}
