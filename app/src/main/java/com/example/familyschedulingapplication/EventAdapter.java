package com.example.familyschedulingapplication;

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

import com.example.familyschedulingapplication.Model.Event;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{
    public static final String TAG = "EventAdapter";
    public final ArrayList<Event> eventList;
    int count=0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public EventAdapter(ArrayList<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i (TAG,  "onCreateViewHolder: "+ count++);
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext ());
        View view = layoutInflater.inflate (R.layout.event_item, parent, false);
        return new ViewHolder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
        holder.nameView.setText(eventList.get(position).getName());
        holder.dateView.setText(eventList.get(position).getEventDate().toString());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView nameView;
        TextView dateView;
        ImageButton moreView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView=itemView.findViewById(R.id.nameView);
            dateView=itemView.findViewById(R.id.dateView);
            moreView=itemView.findViewById(R.id.moreView);
//            nameView.setOnClickListener(this);
            moreView.setOnClickListener(view -> {
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
                    Bundle eventBundle = new Bundle();
                    Log.d(TAG, "onBindViewHolder: " + eventList.get(getAdapterPosition()).getReference());
                    eventBundle.putString("name",eventList.get(getAdapterPosition()).getName());
                    eventBundle.putString("date",eventList.get(getAdapterPosition()).getEventDate().toString());
                    eventBundle.putString("eventId", eventList.get(getAdapterPosition()).getReference().getId());
                    // print reference
                    Class<?> destination = null;
                    switch (position){
                        case 0:
                            eventBundle.putString("mode", "view");
                            destination = EventDetailsActivity.class;
                            break;
                        case 1:
                            eventBundle.putString("mode", "edit");
                            destination = EventDetailsActivity.class;
                            break;
                        case 2:
                            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                            builder.setTitle("Delete Event");
                            builder.setMessage("Are you sure you want to delete this event?");
                            builder.setPositiveButton("Yes", (dialog, which) -> {
                                // get event from db based on reference
                                String eventId = eventList.get(getAdapterPosition()).getReference().getId();
                                db.collection("events").document(eventId).delete().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(itemView.getContext(), "Event deleted successfully", Toast.LENGTH_SHORT).show();
                                        eventList.remove(getAdapterPosition());
                                        notifyItemRemoved(getAdapterPosition());
                                    } else {
                                        Toast.makeText(itemView.getContext(), "Error deleting event", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            });
                            builder.setNegativeButton("No", (dialog, which) -> {
                            });
                            builder.show();
                            break;
                    }
                    if (destination != null) {
                        Intent intent = new Intent(itemView.getContext(), destination);
                        intent.putExtras(eventBundle);
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
