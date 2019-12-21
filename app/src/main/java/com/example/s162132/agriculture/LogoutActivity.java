package com.example.s162132.agriculture;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class LogoutActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        findViewById(R.id.buttonBack).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pref = getSharedPreferences("ID", MODE_PRIVATE);
                        SharedPreferences.Editor editor;
                        editor = pref.edit();
                        editor.clear();
                        editor.commit();
                        finish();
                    }
                }
        );
    }
}
