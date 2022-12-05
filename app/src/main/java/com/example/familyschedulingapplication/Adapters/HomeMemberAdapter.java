package com.example.familyschedulingapplication.Adapters;

import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
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
        View view = layoutInflater.inflate(R.layout.member_home_item, parent, false);
        return new HomeMemberAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeMemberAdapter.ViewHolder holder, int position) {
        holder.homeMemberName.setText(homeMemberList.get(position).getName());
        holder.homeMemberEmail.setText(homeMemberList.get(position).getEmail());
        // The image resource has to be set below.
//        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.homeMemberImage.getLayoutParams();
////        params.setMargins(4, 4, 0, 0);
//        int marginInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, holder.homeMemberImage.getContext().getResources().getDisplayMetrics());
//        params.setMargins(marginInDp, marginInDp, 0, 0);
//        holder.homeMemberImage.setLayoutParams(params);
//        holder.homeMemberImage.setImageURI(Uri.parse(homeMemberList.get(position).getProfileUrl()));
        holder.homeMemberImage.setImageResource(R.drawable.profile_foreground);
//        holder.homeMemberImage.setBackgroundResource(R.drawable.profile_background);
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
