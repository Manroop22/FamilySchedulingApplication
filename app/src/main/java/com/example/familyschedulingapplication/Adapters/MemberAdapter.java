package com.example.familyschedulingapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.example.familyschedulingapplication.Models.Member;
import com.example.familyschedulingapplication.R;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class MemberAdapter extends ArrayAdapter<Member> implements SpinnerAdapter {
    private final ArrayList<Member> members;
    public ArrayList<DocumentReference> selectedMembers;
    public static final String TAG = "MemberAdapter";
    int count=0;
    public MemberAdapter(Context context, ArrayList<Member> members) {
        super(context, 0, members);
        this.members = members;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.member_item, parent, false);
        }
        CheckBox memberName = convertView.findViewById(R.id.memberName);
        TextView userId = convertView.findViewById(R.id.memberUserId);
        memberName.setText(members.get(position).getName());
        userId.setText(members.get(position).getUserId());
        memberName.setOnClickListener(v -> {
            if (memberName.isChecked()) {
                // set item selected as true, add to list of selected items
                selectedMembers.add(members.get(position).getReference());
            } else {
                // set item selected as false, remove from list of selected items if it exists
                selectedMembers.remove(members.get(position));
            }
        });
        return convertView;
    }
}
