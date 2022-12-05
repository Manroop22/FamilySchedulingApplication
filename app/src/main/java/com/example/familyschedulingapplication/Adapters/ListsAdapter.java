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

import com.example.familyschedulingapplication.ListDetails;
import com.example.familyschedulingapplication.Models.List;
import com.example.familyschedulingapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.ArrayList;

public class ListsAdapter extends RecyclerView.Adapter<ListsAdapter.ViewHolder>{
    public static final String TAG = "ListsAdapter";
    public final ArrayList<List> myLists;
    int count=0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ListsAdapter(ArrayList<List> myLists) {
        this.myLists = myLists;
    }

    @NonNull
    @Override
    public ListsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i (TAG,  "onCreateViewHolder: "+ count++);
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext ());
        View view = layoutInflater.inflate (R.layout.list_array_item, parent, false);
        return new ListsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListsAdapter.ViewHolder holder, int position) {
        holder.listName.setText(myLists.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return myLists.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView listName;
        ImageButton listOptions;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            listName=itemView.findViewById(R.id.categoryName);
            listOptions=itemView.findViewById(R.id.categoryOptions);
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
                    Bundle listBundle = new Bundle();
                    listBundle.putString("name", myLists.get(getAdapterPosition()).getName());
                    listBundle.putString("listId", myLists.get(getAdapterPosition()).getTaskId());
                    // print reference
                    Class<?> destination = null;
                    switch (position){
                        case 0:
                            listBundle.putString("mode", "view");
                            destination = ListDetails.class;
                            break;
                        case 1:
                            listBundle.putString("mode", "edit");
                            destination = ListDetails.class;
                            break;
                        case 2:
                            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                            builder.setTitle("Delete List");
                            builder.setMessage("Are you sure you want to delete this list?");
                            builder.setPositiveButton("Yes", (dialog, which) -> {
                                List mlist = myLists.get(getAdapterPosition());
                                List.deleteList(mlist, task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(itemView.getContext(), "List deleted successfully", Toast.LENGTH_SHORT).show();
                                        myLists.remove(getAdapterPosition());
                                        notifyItemRemoved(getAdapterPosition());
                                    } else {
                                        Toast.makeText(itemView.getContext(), "List deletion failed", Toast.LENGTH_SHORT).show();
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
                        intent.putExtras(listBundle);
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
