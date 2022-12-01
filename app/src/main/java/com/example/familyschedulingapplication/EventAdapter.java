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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{
    private static final String TAG = "EventAdapter";
    private final ArrayList<Event> eventList;
    int count=0;

    public EventAdapter(ArrayList<Event> eventList) {
        this.eventList = eventList;
        Log.d(TAG, "EventAdapter: " + eventList.size());
        // print all event names in eventList
        for (Event event : eventList) {
            Log.d(TAG, "EventAdapter: " + event.getName());
        }
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i (TAG,  "onCreateViewHolder: "+ count++);
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext ()) ;
        View view = layoutInflater.inflate (R.layout.event, parent, false) ;
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
                        .setTextGravity(Gravity.CENTER)
                        .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                        .setSelectedTextColor(Color.WHITE)
                        .setMenuColor(Color.WHITE)
                        .setSelectedMenuColor(ContextCompat.getColor(itemView.getContext(), R.color.purple_500)).build();
                powerMenu.setOnMenuItemClickListener((position, item) -> {
                    powerMenu.dismiss();
                    Bundle eventBundle = new Bundle();
                    Class<?> destination = null;
                    switch (position){
                        case 0:
                            eventBundle.putString("name",eventList.get(getAdapterPosition()).getName());
                            eventBundle.putString("date",eventList.get(getAdapterPosition()).getEventDate().toString());
                            eventBundle.putString("eventId", eventList.get(getAdapterPosition()).getReference().getId());
                            eventBundle.putString("mode", "view");
                            destination = EventDescription.class;
                            break;
                        case 1:
                            eventBundle.putString("name",eventList.get(getAdapterPosition()).getName());
                            eventBundle.putString("date",eventList.get(getAdapterPosition()).getEventDate().toString());
                            eventBundle.putString("eventId", eventList.get(getAdapterPosition()).getReference().getId());
                            eventBundle.putString("mode", "edit");
                            destination = EventDescription.class;
                            break;
                        case 2:
                            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                            builder.setTitle("Delete Event");
                            builder.setMessage("Are you sure you want to delete this event?");
                            builder.setPositiveButton("Yes", (dialog, which) -> {
                                eventList.get(getAdapterPosition()).deleteEvent(task -> {
                                    if(task.isSuccessful()){
                                        eventList.remove(getAdapterPosition());
                                        notifyDataSetChanged();
                                    }
                                });
                            });
                            builder.setNegativeButton("No", (dialog, which) -> {
                            });
                            builder.show();
                            break;
                    }
                    Intent intent = new Intent(itemView.getContext(), destination);
                    intent.putExtras(eventBundle);
                    itemView.getContext().startActivity(intent);
                });
                powerMenu.showAsDropDown(view);
            });
        }

        @Override
        public void onClick(View view) {

        }
    }
}
