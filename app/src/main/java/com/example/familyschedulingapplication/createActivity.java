package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

public class createActivity extends AppCompatActivity {
    CheckBox chkIos, chkAndroid, chkWindows;
    Button btnDisplay, btn_add;
    String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        addListenerOnChkIos();
        addListenerOnButton();
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        LinearLayout ll = (LinearLayout)findViewById(R.id.linearlayout);

        CheckBox cb = new CheckBox(this);
        int lHeight = LinearLayout.LayoutParams.MATCH_PARENT;
        int lWidth = LinearLayout.LayoutParams.WRAP_CONTENT;

        ll.addView(cb, new LinearLayout.LayoutParams(lHeight, lWidth));
        setContentView(ll);

    }

    public void addListenerOnButton() {

        chkIos = (CheckBox) findViewById(R.id.chkIos);
        chkAndroid = (CheckBox) findViewById(R.id.chkAndroid);
        chkWindows = (CheckBox) findViewById(R.id.chkWindows);
        btnDisplay = (Button) findViewById(R.id.btnDisplay);

        btnDisplay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                StringBuffer result = new StringBuffer();
                result.append("IPhone check : ")
                        .append(chkIos.isChecked());
                result.append("\nAndroid check : ").append(
                        chkAndroid.isChecked());
                result.append("\nWindows Mobile check :").append(
                        chkWindows.isChecked());

                Toast.makeText(createActivity.this, result.toString(),
                        Toast.LENGTH_LONG).show();

            }
        });

    }

    private void addListenerOnChkIos() {
        chkIos = (CheckBox) findViewById(R.id.chkIos);
        chkIos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    Toast.makeText(createActivity.this,
                            "Bro, try Android :)", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}