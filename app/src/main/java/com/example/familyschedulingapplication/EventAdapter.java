package com.example.familyschedulingapplication;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{
    private static final String TAG = "EventAdapter";
    private ArrayList<Event> eventList;
    int count=0;

    public EventAdapter(ArrayList eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i (TAG,  "onCreateViewHolder: "+ count++);
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext ()) ;
        View view = layoutInflater.inflate (R.layout.event, parent, false) ;
        ViewHolder viewHolder = new ViewHolder (view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
        holder.nameView.setText(eventList.get(position).getName());
        holder.dateView.setText(eventList.get(position).getDate().toString());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView nameView;
        TextView dateView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView=itemView.findViewById(R.id.nameView);
            dateView=itemView.findViewById(R.id.dateView);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
