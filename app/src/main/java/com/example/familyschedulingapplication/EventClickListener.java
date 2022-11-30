package com.example.familyschedulingapplication;

import android.view.View;

public interface EventClickListener {
    void onEditClick(View view, int position);

    void onCheckClick(View view, int position);

    void onTaskCreated(EventAdapter.ViewHolder holder, int position);
}
