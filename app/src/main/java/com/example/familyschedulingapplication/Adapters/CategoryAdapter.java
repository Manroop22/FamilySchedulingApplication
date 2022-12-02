package com.example.familyschedulingapplication.Adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.familyschedulingapplication.ModalBottomSheets.CategoryBottomSheet;
import com.example.familyschedulingapplication.Models.Category;
import com.example.familyschedulingapplication.R;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<Category> implements SpinnerAdapter {
    private final ArrayList<Category> categories;
    public static final String TAG = "CategoryAdapter";
    int count=0;
    public CategoryAdapter(Context context, ArrayList<Category> categories) {
        super(context, 0, categories);
        this.categories = categories;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Category getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int pos, View convertView, android.view.ViewGroup parent) {
        return getCustomView(pos, convertView, parent);
    }

    public View getCustomView(int pos, View convertView, android.view.ViewGroup parent) {
        Category category = getItem(pos);
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.category_array_item, null);
        }
        TextView categoryName = convertView.findViewById(R.id.categoryName);
        ImageButton categoryOptions = convertView.findViewById(R.id.categoryOptions);
        categoryName.setText(category.getName());

        CategoryBottomSheet categoryBottomSheet = new CategoryBottomSheet();
//        categoryOptions.setOnClickListener(v -> categoryBottomSheet.show(getSupportFragmentManager(), CategoryBottomSheet.TAG));
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public View getDropDownView(int pos, View convertView, @NonNull android.view.ViewGroup parent) {
        return getCustomView(pos, convertView, parent);
    }
}
