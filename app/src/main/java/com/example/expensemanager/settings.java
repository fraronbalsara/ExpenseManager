package com.example.expensemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        String[] settingsArray = {"Edit Profile","Reset Password","Delete Account","Generate A Report","Report A Bug"};

        ListView list_settings = (ListView)findViewById(R.id.list_settings);

        ArrayAdapter ad = new ArrayAdapter(settings.this, android.R.layout.simple_list_item_1, settingsArray);
        list_settings.setAdapter(ad);

        list_settings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    Intent i = new Intent(settings.this,edit_profile.class);
                    startActivity(i);
                }
                else if(position == 1){
                    Intent i = new Intent(settings.this,reset_password.class);
                    startActivity(i);
                }
                else if(position == 2){
                    Intent i = new Intent(settings.this,delete_account.class);
                    startActivity(i);
                }
                else if(position == 3){
                    Intent i = new Intent(settings.this,report.class);
                    startActivity(i);
                }
                else if(position == 4){
                    Intent i = new Intent(settings.this,report_a_bug.class);
                    startActivity(i);
                }
            }
        });

    }
}
