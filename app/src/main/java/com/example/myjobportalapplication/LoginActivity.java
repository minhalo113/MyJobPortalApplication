package com.example.myjobportalapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;

    private Button loginBut;
    private Button regisBut;

    private RadioGroup accountType;
    private RadioButton recuiter;
    private RadioButton applicant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginFunction();
    }

    protected void loginFunction(){
        username = findViewById(R.id.usernameLogin);
        password = findViewById(R.id.passwordLogin);

        loginBut = findViewById(R.id.loginButtonLogin);
        regisBut = findViewById(R.id.registrationButtonLogin);

        accountType = findViewById(R.id.accTypeLogin);

        loginBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        regisBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        accountType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                recuiter = findViewById(R.id.recuiterButtonLogin);
                applicant = findViewById(R.id.applicantButtonLogin);
            }
        });

    }
}