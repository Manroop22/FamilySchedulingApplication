package com.example.familyschedulingapplication.Adapters;

import android.content.Context;
import android.util.Log;
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
    public int layout;
    int count=0;
    public MemberAdapter(Context context, int lay, ArrayList<Member> members) {
        super(context, lay, members);
        this.layout = lay;
        this.members = members;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView: " + position);
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }
        CheckBox memberName = convertView.findViewById(R.id.memberName);
        Member mem = members.get(position);
        TextView userId = convertView.findViewById(R.id.memberUserId);
//        memberName.setText(mem.getName());
        userId.setText(mem.getName());
        memberName.setOnClickListener(v -> {
            if (memberName.isChecked()) {
                // set item selected as true, add to list of selected items
                selectedMembers.add(members.get(position).getReference());
            } else {
                // set item selected as false, remove from list of selected items if it exists
                selectedMembers.remove(members.get(position).getReference());
            }
        });
        if (selectedMembers != null) {
            for (DocumentReference member : selectedMembers) {
                if (member.getId().equals(members.get(position).getUserId())) {
                    memberName.setChecked(true);
                    count++;
                } else {
                    memberName.setChecked(false);
                }
            }
        }
        return convertView;
    }

    public boolean inSelect(DocumentReference ref) {
        boolean in = false;
        for (Member member : members) {
            if (member.getReference().equals(ref)) {
                in = true;
                break;
            }
        }
        return in;
    }
}
