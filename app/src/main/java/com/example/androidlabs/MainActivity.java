package com.example.androidlabs;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_relative);

        Button text = findViewById(R.id.Button1);
        text.setOnClickListener(e -> Toast.makeText(this, getResources().getString(R.string.message) , Toast.LENGTH_LONG).show());

        String switchMessage = getResources().getString(R.string.switchMsg);
        String on = getResources().getString(R.string.on);
        String off = getResources().getString(R.string.off);

        CheckBox box = findViewById(R.id.checkbox);
        box.setOnCheckedChangeListener((CompoundButton cb, boolean b) ->{
            Snackbar snack = Snackbar.make(cb, switchMessage + " " + (b ? on : off) , Snackbar.LENGTH_LONG);
            snack.setAction( getResources().getString(R.string.undo), click -> cb.setChecked(!b));
            snack.show();
        });

        Switch s =  findViewById(R.id.Switch);
        s.setOnCheckedChangeListener((CompoundButton cb, boolean b) ->{
            Snackbar snack = Snackbar.make(cb, switchMessage + " " + (b ? on : off) , Snackbar.LENGTH_LONG);
            snack.setAction( getResources().getString(R.string.undo), click -> cb.setChecked(!b));
            snack.show();
        });
    }
}