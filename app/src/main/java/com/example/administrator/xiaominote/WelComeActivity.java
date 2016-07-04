package com.example.administrator.xiaominote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.administrator.xiaominote.activity.NoteList;

public class WelComeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wel_come);
        startActivity(new Intent(this, NoteList.class));
    }
}
