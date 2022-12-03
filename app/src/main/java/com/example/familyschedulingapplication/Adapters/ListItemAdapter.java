package com.example.familyschedulingapplication.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familyschedulingapplication.Models.ListItem;
import com.example.familyschedulingapplication.R;

import java.util.ArrayList;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ViewHolder> {
    public static final String TAG = "ListsAdapter";
    public ArrayList<ListItem> listItems;
    int count=0;
    public ListItemAdapter(ArrayList<ListItem> myItems) {
        this.listItems = myItems;
    }

    @NonNull
    @Override
    public ListItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: "+ count++);
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext ());
        View view = layoutInflater.inflate (R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemAdapter.ViewHolder holder, int position) {
//        holder.setValues(listItems.get(position));
        holder.listItemName.setText(listItems.get(position).getName());
        holder.isCompleted.setChecked(listItems.get(position).getCompleted());
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public ArrayList<ListItem> getListItems() {
        return listItems;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        EditText listItemName;
        CheckBox isCompleted;
        ImageButton editItem;
        ImageButton deleteItem;
        String mode = "view";
        ListItem item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            listItemName=itemView.findViewById(R.id.listItemName);
            isCompleted=itemView.findViewById(R.id.isCompleted);
            editItem=itemView.findViewById(R.id.editListitem);
            deleteItem=itemView.findViewById(R.id.deleteListItem);
            if (getAdapterPosition() == 0 || getAdapterPosition() == -1) {
                item = new ListItem(listItemName.getText().toString(), isCompleted.isChecked());
            } else {
                item = listItems.get(getAdapterPosition());
            }
//            setValues(item);
            editItem.setOnClickListener(view -> {
                if (mode.equals("view")) {
                    listItemName.setEnabled(true);
                    isCompleted.setEnabled(true);
                    mode = "edit";
                } else {
                    listItemName.setEnabled(false);
                    isCompleted.setEnabled(false);
                    mode = "view";
                }
            });
            deleteItem.setOnClickListener(view -> {
                listItems.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
            });
            isCompleted.setOnClickListener(view -> item.setCompleted(isCompleted.isChecked()));
        }
        @Override
        public void onClick(View view) {
        }

        public void switchMode(String mmode) {
            if (mmode.equals("view")) {
                listItemName.setEnabled(true);
                isCompleted.setEnabled(true);
                mode = "edit";
            } else {
                mode = "view";
                listItemName.setEnabled(false);
                isCompleted.setEnabled(false);
            }
        }

        public void setValues(ListItem item) {
            listItemName.setText(item.getName());
            isCompleted.setChecked(item.getCompleted());
        }
    }
}
