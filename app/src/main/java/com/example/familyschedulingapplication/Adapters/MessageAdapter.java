package com.example.familyschedulingapplication.Adapters;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
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

import com.example.familyschedulingapplication.ActivityDetails;
import com.example.familyschedulingapplication.Models.Message;
import com.example.familyschedulingapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.ArrayList;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    private static final String TAG = "MessageAdapter";
    private ArrayList<Message> messageList;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MessageAdapter(ArrayList<Message> messageList) {
        this.messageList = messageList;
    }
    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.msg_item, parent, false);
        MessageAdapter.ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        holder.nameView.setText(messageList.get(position).getTitle());
        holder.msgTextView.setText(messageList.get(position).getMessage());
        assert user != null;
        DocumentReference memberRef = db.collection("members").document(user.getUid());
        if (messageList.get(position).getCreatedBy().equals(memberRef)) {
            holder.msgByDate.setText(String.format("You on %s", messageList.get(position).getCreatedAt().toString()));
        } else {
            // split user.getEmail() by @ and get the first part
            String email = messageList.get(position).getCreatedBy().getId();
            String[] parts = email.split("@");
            String part1 = parts[0];
            holder.msgByDate.setText(String.format("%s on %s", part1, messageList.get(position).getCreatedAt().toString()));
        }
        if (db.collection("members").document(user.getUid()) == messageList.get(position).getCreatedBy()) {
            holder.msgOptions.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameView;
        TextView msgTextView;
        TextView msgByDate;
        ImageButton msgOptions;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.nameView);
            msgTextView = itemView.findViewById(R.id.msgText);
            msgByDate = itemView.findViewById(R.id.msgByDate);
            msgOptions = itemView.findViewById(R.id.msgOptions);
            msgOptions.setOnClickListener(v -> {
                ArrayList<PowerMenuItem> list=new ArrayList<>();
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
                    String messageId = messageList.get(getAdapterPosition()).getMessageId();
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setTitle("Delete Activity");
                    builder.setMessage("Are you sure you want to delete this activity?");
                    builder.setPositiveButton("Yes", (dialog, which) -> db.collection("messages").document(messageId).delete().addOnSuccessListener(aVoid -> {
                        Toast.makeText(itemView.getContext(), "Message deleted", Toast.LENGTH_SHORT).show();
                        messageList.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                    }).addOnFailureListener(e -> Toast.makeText(itemView.getContext(), "Error deleting message", Toast.LENGTH_SHORT).show()));
                    builder.setNegativeButton("No", (dialog, which) -> {
                    });
                    builder.show();
                });
                powerMenu.showAsDropDown(v);
            });

        }
        @Override
        public void onClick(View view) {

        }
    }
}
