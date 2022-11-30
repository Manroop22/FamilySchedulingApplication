package com.example.familyschedulingapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.TaskStackBuilder;
import androidx.fragment.app.FragmentManager;

import android.content.Context;

public class myadapter extends AppCompatActivity {
    private Context c;
    int tabs;

    public myadapter(Context c1, FragmentManager fm, int t){
        super(fm);
        c=c1;
        this.tabs=t;
    }
    public TaskStackBuilder.SupportParentable geti(int pos){
        switch (pos){
            case 0:
                activity_home ac=new activity_home();
                return ac;
            case 1:
                activities a=new activities();
                return a;
            case 2:
                lists l=new lists();
                return l;
            default:
                return null;
        }
    }
    public int getcount(){
        return tabs;
    }
}
