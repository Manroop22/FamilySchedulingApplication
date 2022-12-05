package com.example.familyschedulingapplication.Adapters;

import static java.security.AccessController.getContext;

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
import com.example.familyschedulingapplication.MainActivity;
import com.example.familyschedulingapplication.Models.Bill;
import com.example.familyschedulingapplication.Models.Home;
import com.example.familyschedulingapplication.Models.HomeInvite;
import com.example.familyschedulingapplication.Models.Member;
import com.example.familyschedulingapplication.NoHomeActivity;
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
                Log.d(TAG, "onClick: acceptBtn clicked");
                Member.getMember(user.getUid(), task -> {
                    if (task.isSuccessful()) {
                        Member member = Member.getMemberByMemberId(task.getResult());
                        member.setHomeId(inviteList.get(getAbsoluteAdapterPosition()).getHomeId());
                        Home.getHomeById(inviteList.get(getAbsoluteAdapterPosition()).getHomeId(), task1 -> {
                            if (task1.isSuccessful()) {
                                DocumentSnapshot homeSnap = task1.getResult();
                                if (homeSnap.exists()) {
                                    Home home = Home.getHome(homeSnap);
                                    assert home != null;
                                    if (home.getAccessCode().equals(inviteList.get(getAbsoluteAdapterPosition()).getAccessCode())) {
                                        db.collection(Member.collection).document(member.getUserId()).set(member).addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                Log.d(TAG, "onClick: member added to home");
                                                db.collection(HomeInvite.collection).document(inviteList.get(getAbsoluteAdapterPosition()).getHomeInviteId()).delete().addOnCompleteListener(task3 -> {
                                                    if (task3.isSuccessful()) {
                                                        Log.d(TAG, "onClick: invite deleted");
                                                        inviteList.remove(getAbsoluteAdapterPosition());
                                                        notifyItemRemoved(getAbsoluteAdapterPosition());
                                                        Intent intent = new Intent(itemView.getContext(), MainActivity.class);
                                                        ((NoHomeActivity) itemView.getContext()).startActivity(intent);
                                                        ((NoHomeActivity) itemView.getContext()).finish();
                                                        Toast.makeText(itemView.getContext(), "Invite accepted", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Log.d(TAG, "onClick: invite not deleted");
                                                    }
                                                });
                                            } else {
                                                Log.d(TAG, "onClick: member not added to home");
                                            }
                                        });
                                    } else {
                                        Log.d("Member", "joinHome: incorrect access code");
                                    }
                                } else {
                                    Log.d("Member", "joinHome: home does not exist");
                                }
                            } else {
                                Log.d("Member", "joinHome: home does not exist");
                            }
                        });
                    }
                });
            });

            declineBtn.setOnClickListener(view -> {
                assert user != null;
                Member.getMember(user.getUid(), task -> {
                    if (task.isSuccessful()) {
                        Member member = Member.getMemberByMemberId(task.getResult());
                        Member.joinHome(inviteList.get(getAbsoluteAdapterPosition()).getHomeId(), member, inviteList.get(getAbsoluteAdapterPosition()).getAccessCode(), task1 -> {
                            HomeInvite.getHomeInviteByHomeMemberAndAccessCode(inviteList.get(getAbsoluteAdapterPosition()).getHomeId(), member.getReference(), inviteList.get(getAbsoluteAdapterPosition()).getAccessCode(), task2 -> {
                                if (task2.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task2.getResult();
                                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                        HomeInvite homeInvite = querySnapshot.getDocuments().get(0).toObject(HomeInvite.class);
                                        assert homeInvite != null;
                                        HomeInvite.deleteHomeInvite(homeInvite, task3 -> {
                                            if (task3.isSuccessful()) {
                                                inviteList.remove(getAbsoluteAdapterPosition());
                                                notifyItemRemoved(getAbsoluteAdapterPosition());
                                                Toast.makeText(itemView.getContext(), "Invite Declined", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(itemView.getContext(), MainActivity.class);
                                                ((NoHomeActivity) itemView.getContext()).startActivity(intent);
                                                ((NoHomeActivity) itemView.getContext()).finish();
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
