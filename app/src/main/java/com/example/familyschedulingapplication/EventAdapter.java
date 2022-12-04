package com.example.familyschedulingapplication;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.ArrayList;

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
            dateView=itemView.findViewById(R.id.msgTextView);
            nameView.setOnClickListener(this);
            nameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
                        if(position==0) {

                        }
                        if (position==1)

                            if(position==2){
                                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                                alert.setTitle("Delete");
                                alert.setMessage("Are you sure you want to delete?");
                                alert.setPositiveButton("Yes",
                                        (dialog, which) -> {

                                        });
                                alert.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                                alert.show();
                            }

                    });
                    powerMenu.showAsDropDown(view);
                }
            });
        }

        @Override
        public void onClick(View view) {

        }
    }
}