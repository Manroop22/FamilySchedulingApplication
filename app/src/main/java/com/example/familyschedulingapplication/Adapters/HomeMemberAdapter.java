package com.example.familyschedulingapplication.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familyschedulingapplication.Models.HomeInvite;
import com.example.familyschedulingapplication.Models.Member;
import com.example.familyschedulingapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HomeMemberAdapter extends RecyclerView.Adapter<HomeMemberAdapter.ViewHolder>{
    private static final String TAG = "HomeMemberAdapter";
    private final ArrayList<Member> homeMemberList;
    int count = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public HomeMemberAdapter(ArrayList<Member> homeMemberList) {
        this.homeMemberList = homeMemberList;
    }
    @NonNull
    @Override
    public HomeMemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder: " + count++);
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.invite_item, parent, false);
        return new HomeMemberAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeMemberAdapter.ViewHolder holder, int position) {
        holder.homeMemberName.setText(homeMemberList.get(position).getName());
        holder.homeMemberEmail.setText(homeMemberList.get(position).getEmail());
        // The image resource has to be set below.
       // holder.homeMemberImage.setImageResource(homeMemberList.get(position).);
    }

    @Override
    public int getItemCount() {
        return homeMemberList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView homeMemberImage;
        TextView homeMemberName;
        TextView homeMemberEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            homeMemberImage = itemView.findViewById(R.id.homeMemberImageView);
            homeMemberName = itemView.findViewById(R.id.homeMemberNameView);
            homeMemberEmail = itemView.findViewById(R.id.homeMemberEmailView);
             // any on clickListener needed to be added over here.
        }

        @Override
        public void onClick(View view) {

        }
    }
}
