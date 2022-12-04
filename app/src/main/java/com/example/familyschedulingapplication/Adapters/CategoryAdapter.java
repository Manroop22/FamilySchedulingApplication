package com.example.familyschedulingapplication.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.familyschedulingapplication.ModalBottomSheets.CategoryBottomSheet;
import com.example.familyschedulingapplication.Models.Category;
import com.example.familyschedulingapplication.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<Category> implements SpinnerAdapter {
    public static ArrayList<Category> categories = new ArrayList<>();
    public ArrayList<Category> cats;
    public static final String TAG = "CategoryAdapter";
    int count=0;
    public CategoryAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Category> objects) {
        super(context, resource, objects);
        cats = objects;
        categories = objects;
//        categories = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.i (TAG,  "getView: "+ count++);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_array_item, parent, false);
        }
        return myView(position, convertView, parent);
//        return view;
    }

    public View myView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_array_item, parent, false);
        }
//        Category category = getItem(position);
        Category cat = cats.get(position);
        TextView nameView = convertView.findViewById(R.id.categoryName);
        nameView.setText(cat.getName());
        nameView.setBackgroundColor(cat.getColor());
//        ImageButton options = convertView.findViewById(R.id.categoryOptions);
//        View finalConvertView = convertView;
//        options.setOnClickListener(v -> {
//            // PowerMenu
//            ArrayList<PowerMenuItem> list=new ArrayList<>();
//            list.add(new PowerMenuItem("View",false));
//            list.add(new PowerMenuItem("Edit",false));
//            list.add(new PowerMenuItem("Delete",false));
//            PowerMenu powerMenu = new PowerMenu.Builder(finalConvertView.getContext())
//                    .addItemList(list) // list has "Novel", "Poetry", "Art"
//                    .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
//                    .setMenuRadius(10f) // sets the corner radius.
//                    .setMenuShadow(10f) // sets the shadow.
//                    .setTextColor(ContextCompat.getColor(finalConvertView.getContext(), R.color.black))
//                    .setTextGravity(Gravity.START)
//                    .setTextSize(16)
//                    .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
//                    .setSelectedTextColor(Color.WHITE)
//                    .setMenuColor(Color.WHITE)
//                    .setSelectedMenuColor(ContextCompat.getColor(finalConvertView.getContext(), R.color.purple_500)).build();
//            powerMenu.setOnMenuItemClickListener((pos, item) -> {
//                String mode = "view";
//                switch (pos) {
//                    case 0:
//                        mode = "view";
//                        break;
//                    case 1:
//                        mode = "edit";
//                        break;
//                    case 2:
//                        mode = null;
//                        AlertDialog.Builder builder = new AlertDialog.Builder(finalConvertView.getContext());
//                        FirebaseFirestore db = FirebaseFirestore.getInstance();
//                        builder.setTitle("Delete Activity");
//                        builder.setMessage("Are you sure you want to delete this activity?");
//                        builder.setPositiveButton("Yes", (dialog, which) -> db.collection("categories").get().addOnCompleteListener(task -> {
//                            if (task.isSuccessful()) {
//                                for (DocumentSnapshot document : task.getResult()) {
//                                    if (document.getId().equals(cat.getCategoryId())) {
//                                        db.collection("activities").document(document.getId()).delete().addOnCompleteListener(task1 -> {
//                                            if (task1.isSuccessful()) {
//                                                Log.i(TAG, "onComplete: deleted category");
//                                                Toast.makeText(finalConvertView.getContext(), "Deleted category", Toast.LENGTH_SHORT).show();
//                                            } else {
//                                                Log.i(TAG, "onComplete: failed to delete category");
//                                                Toast.makeText(finalConvertView.getContext(), "Failed to delete category", Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                        break;
//                                    }
//                                }
//                            }
//                        }));
//                        break;
//                }
//                if (mode != null) {
//                    CategoryBottomSheet.newInstance(mode, cat).show(((AppCompatActivity)finalConvertView.getContext()).getSupportFragmentManager(), CategoryBottomSheet.TAG);
//                }
//            });
//            powerMenu.showAsDropDown(v);
//        });
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return myView(position, convertView, parent);
    }

    @Override
    public Category getItem(int position) {
        return cats.get(position);
    }

    @Override
    public int getPosition(@Nullable Category item) {
//        return super.getPosition(item);
        return cats.indexOf(item);
    }
}
