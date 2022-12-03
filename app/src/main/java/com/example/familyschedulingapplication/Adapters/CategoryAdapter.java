package com.example.familyschedulingapplication.Adapters;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.familyschedulingapplication.ModalBottomSheets.CategoryBottomSheet;
import com.example.familyschedulingapplication.Models.Category;
import com.example.familyschedulingapplication.R;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<Category> implements SpinnerAdapter {
    public static ArrayList<Category> categories = new ArrayList<>();
    public static final String TAG = "CategoryAdapter";
    int count=0;
    public CategoryAdapter(Context context) {
        super(context, 0, categories);
    }

    @Override
    public Category getItem(int position) {
        return categories.get(position);
    }

    @Override
    public View getView(int pos, View convertView, android.view.ViewGroup parent) {
        return getCustomView(pos, convertView, parent);
    }

    public int getPosition(DocumentReference cat) {
        // get position where cat.getReference() == item.getReference()
        int res = 0;
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getReference().equals(cat)) {
                res = i;
                break;
            }
        }
        return res;
    }

    public View getCustomView(int pos, View convertView, android.view.ViewGroup parent) {
        Category category = getItem(pos);
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.category_array_item, null);
        }
        TextView categoryName = convertView.findViewById(R.id.listName);
        ImageButton categoryOptions = convertView.findViewById(R.id.listOptions);
        categoryName.setText(category.getName());

        CategoryBottomSheet categoryBottomSheet = new CategoryBottomSheet();
        categoryOptions.setOnClickListener(v -> categoryBottomSheet.show(((FragmentActivity)getContext()).getSupportFragmentManager(), CategoryBottomSheet.TAG));
        return convertView;
    }

    public View getDropDownView(int pos, View convertView, @NonNull android.view.ViewGroup parent) {
        return getCustomView(pos, convertView, parent);
    }
}
