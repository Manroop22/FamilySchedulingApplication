package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;

public class activity_desc extends AppCompatActivity {
        EditText e1,e2,e3,e4;
        Button b1,b2;
        Spinner s1;
        CheckBox c1,c2,c3;
        ImageButton i1,i2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desc);
        e1=(EditText)findViewById(R.id.editTextTextPersonName10);
        e2=(EditText)findViewById(R.id.editTextTextMultiLine10);
        e3=(EditText)findViewById(R.id.editTextTextPersonName40);
        e4=(EditText)findViewById(R.id.editTextTextMultiLine30);
        b1=(Button) findViewById(R.id.button);
        b2=(Button) findViewById(R.id.button20);
        s1=(Spinner) findViewById(R.id.spinner2);
        c1=(CheckBox)findViewById(R.id.checkBox10);
        c2=(CheckBox)findViewById(R.id.checkBox20);
        c3=(CheckBox)findViewById(R.id.checkBox30);
        i1=(ImageButton)findViewById(R.id.imageButton20);
        ArrayList<String> spin=new ArrayList<String>();
        spin.add("URGENT");
        spin.add("FAMILY");
        spin.add("CASUAL");
        ArrayAdapter<String> a1=new ArrayAdapter<String>(activity_desc.this,android.R.layout.simple_spinner_item,spin);
        a1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(a1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),createActivity.class);
                startActivity(i);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(),activity_home.class);
                startActivity(i);
            }
        });

        i1.setOnClickListener(ModalBottomSheet.show(getSupportFragmentManager(), ModalBottomSheet.TAG));

        ArrayList<PowerMenuItem> list=new ArrayList<PowerMenuItem>();
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
        i2.setOnClickListener((position, item) -> {
                    AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                    alert.setTitle("Delete");
                    alert.setMessage("Are you sure you want to delete?");
                    alert.setPositiveButton("Yes",
                            (dialog, which) -> {

                            });
                    alert.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                    alert.show();
                });
    }

}