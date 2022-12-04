package com.example.familyschedulingapplication.Adapters;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familyschedulingapplication.Models.Activity;
import com.example.familyschedulingapplication.R;
import com.example.familyschedulingapplication.ActivityDetails;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.ArrayList;
import java.util.Objects;

public class ActivitiesAdapter extends RecyclerView.Adapter<ActivitiesAdapter.ViewHolder>{
    public static final String TAG = "ActivitiesAdapter";
    public final ArrayList<Activity> activityList;
    int count=0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ActivitiesAdapter(ArrayList<Activity> activityList) {
        this.activityList = activityList;
    }

    @NonNull
    @Override
    public ActivitiesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i (TAG,  "onCreateViewHolder: "+ count++);
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext ());
        View view = layoutInflater.inflate (R.layout.activities_item, parent, false);
        return new ActivitiesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivitiesAdapter.ViewHolder holder, int position) {
//        holder.nameView.setText(eventList.get(position).getName());
//        holder.dateView.setText(eventList.get(position).getEventDate().toString());
        holder.listName.setText(activityList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView listName;
        ImageButton listOptions;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            listName=itemView.findViewById(R.id.listName);
            listOptions=itemView.findViewById(R.id.listOptions);
//            nameView.setOnClickListener(this);
            listOptions.setOnClickListener(view -> {
                ArrayList<PowerMenuItem> list=new ArrayList<>();
                list.add(new PowerMenuItem("View",false));
                list.add(new PowerMenuItem("Edit",false));
                list.add(new PowerMenuItem("Delete",false));
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
                    Bundle activityBundle = new Bundle();
                    activityBundle.putString("name", activityList.get(getAdapterPosition()).getName());
                    activityBundle.putString("activityId", activityList.get(getAdapterPosition()).getActivityId());
                    activityBundle.putString("createdBy", activityList.get(getAdapterPosition()).getCreatedBy().toString());
                    // print reference
                    Class<?> destination = null;
                    switch (position){
                        case 0:
                            activityBundle.putString("mode", "view");
                            destination = ActivityDetails.class;
                            break;
                        case 1:
                            activityBundle.putString("mode", "edit");
                            destination = ActivityDetails.class;
                            break;
                        case 2:
                            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                            builder.setTitle("Delete Activity");
                            builder.setMessage("Are you sure you want to delete this activity?");
                            builder.setPositiveButton("Yes", (dialog, which) -> db.collection("activities").get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        if (Objects.equals(document.getString("name"), activityList.get(getAdapterPosition()).getName()) && Objects.equals(document.getDocumentReference("createdBy"), activityList.get(getAdapterPosition()).getCreatedBy())) {
                                            db.collection("activities").document(document.getId()).delete().addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    Toast.makeText(itemView.getContext(), "Activity deleted successfully", Toast.LENGTH_SHORT).show();
                                                    activityList.remove(getAdapterPosition());
                                                    notifyItemRemoved(getAdapterPosition());
                                                } else {
                                                    Toast.makeText(itemView.getContext(), "Activity deletion failed", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                }
                            }));
                            builder.setNegativeButton("No", (dialog, which) -> {
                            });
                            builder.show();
                            break;
                    }
                    if (destination != null) {
                        Intent intent = new Intent(itemView.getContext(), destination);
                        intent.putExtras(activityBundle);
                        itemView.getContext().startActivity(intent);
                    }
                });
                powerMenu.showAsDropDown(view);
            });
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "onClick: " + getAdapterPosition());
        }
    }
}
