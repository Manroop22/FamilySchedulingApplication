package com.example.familyschedulingapplication;

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

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyViewHolder>{
    private final ArrayList<Event> eventArrayList;
    private EventClickListener toDoClickListener;

    // constructor to initialize eventArrayList to values from eventList from the EventMainScreen
    public EventAdapter(ArrayList<Event> eventArrayList) {
        this.eventArrayList = eventArrayList;
    }

    @NonNull
    @Override

    public EventAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // uses the event.xml as the template.
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.MyViewHolder holder, int position) {
        String eventName = eventArrayList.get(position).getName();
        Date eventDate = eventArrayList.get(position).getDate();

        String pattern = "EEE, MMM dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.US);

        holder.checkBox.setText(toDoText);
        if (toDoDate != null)
            holder.dateView.setText(String.join("", "Due date: ", simpleDateFormat.format(toDoDate)));
        holder.checkBox.setChecked(toDoList.get(position).isDone());
        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> toDoClickListener.onCheckClick(compoundButton, holder.getAdapterPosition()));

        holder.onCreated();
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    // inner class responsible for managing to-do items
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // can access views (checkbox and dots image) contained in to-do item in here
        private final CheckBox checkBox;
        private final TextView dateView;

        public MyViewHolder(final View view) {
            super(view);
            dateView = view.findViewById(R.id.dateView);
            checkBox = view.findViewById(R.id.checkBox);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (toDoClickListener != null)
                toDoClickListener.onEditClick(view, getAdapterPosition());
        }

        public void onCreated() {
            toDoClickListener.onTaskCreated(this, getAdapterPosition());
        }
    }
}
