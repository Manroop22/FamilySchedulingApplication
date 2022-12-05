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
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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
        // if inviteList is null, create a new one
        if (inviteList == null) {
            this.inviteList = new ArrayList<>();
        } else {
            this.inviteList = inviteList;
        }
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
        // set inviteTextView to invitedByEmail sent you an invite to join their home
        holder.inviteTextView.setText(String.format("%s sent you an invite to join their home", inviteList.get(position).getInvitedByEmail()));
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
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            acceptBtn = itemView.findViewById(R.id.inviteAcceptBtn);
            declineBtn = itemView.findViewById(R.id.inviteDeclineBtn);
            acceptBtn.setOnClickListener(view -> {
                // code for accepting the invite
                Member.getMember(user.getUid(), task -> {
                    if (task.isSuccessful()) {
                        Member member = Member.getMemberByMemberId(task.getResult());
                        Member.joinHome(inviteList.get(getAdapterPosition()).getHomeId(), member, inviteList.get(getAdapterPosition()).getAccessCode(), task1 -> {
                            HomeInvite.getHomeInviteByHomeMemberAndAccessCode(inviteList.get(getAdapterPosition()).getHomeId(), member.getReference(), inviteList.get(getAdapterPosition()).getAccessCode(), task2 -> {
                                if (task2.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task2.getResult();
                                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                        HomeInvite homeInvite = querySnapshot.getDocuments().get(0).toObject(HomeInvite.class);
                                        assert homeInvite != null;
                                        HomeInvite.deleteHomeInvite(homeInvite, task3 -> {
                                            if (task3.isSuccessful()) {
                                                inviteList.remove(getAdapterPosition());
                                                notifyItemRemoved(getAdapterPosition());
                                                Toast.makeText(itemView.getContext(), "Invite Accepted", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(itemView.getContext(), "Error accepting invite", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });
                        });
                    }
                });
            });

            declineBtn.setOnClickListener(view -> {
                assert user != null;
                Member.getMember(user.getUid(), task -> {
                    if (task.isSuccessful()) {
                        Member member = Member.getMemberByMemberId(task.getResult());
                        Member.joinHome(inviteList.get(getAdapterPosition()).getHomeId(), member, inviteList.get(getAdapterPosition()).getAccessCode(), task1 -> {
                            HomeInvite.getHomeInviteByHomeMemberAndAccessCode(inviteList.get(getAdapterPosition()).getHomeId(), member.getReference(), inviteList.get(getAdapterPosition()).getAccessCode(), task2 -> {
                                if (task2.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task2.getResult();
                                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                        HomeInvite homeInvite = querySnapshot.getDocuments().get(0).toObject(HomeInvite.class);
                                        assert homeInvite != null;
                                        HomeInvite.deleteHomeInvite(homeInvite, task3 -> {
                                            if (task3.isSuccessful()) {
                                                inviteList.remove(getAdapterPosition());
                                                notifyItemRemoved(getAdapterPosition());
                                                Toast.makeText(itemView.getContext(), "Invite Declined", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(itemView.getContext(), "Error declining invite", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });
                        });
                    }
                });
            });

        }

        @Override
        public void onClick(View view) {

        }
    }
}
