package com.example.myjobportalapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;

    private Button loginBut;
    private Button regisBut;

    private RadioGroup accountType;
    private RadioButton recuiter;
    private RadioButton applicant;
    private boolean accType = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginFunction();
        colorChange();
    }

    protected void loginFunction(){
        email = findViewById(R.id.emailLogin);
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
                startActivity(new Intent(getApplicationContext(),RegistrationActivity.class));
            }
        });
        accountType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                recuiter = findViewById(R.id.recuiterButtonLogin);
                applicant = findViewById(R.id.applicantButtonLogin);
                if(i == R.id.applicantButtonLogin){
                    accType = false;
                }else{
                    accType = true;
                }
                colorChange();
            }
        });

    }
    protected void colorChange(){
        applicant = findViewById(R.id.applicantButtonLogin);
        recuiter = findViewById(R.id.recuiterButtonLogin);
        if(accType == false){
            recuiter.setBackground(getDrawable(R.drawable.radio_button_recuiter_background2));
            applicant.setBackground(getDrawable(R.drawable.radio_button_applicant_background2));
        }else{
            recuiter.setBackground(getDrawable(R.drawable.radio_button_recuiter_background1));
            applicant.setBackground(getDrawable(R.drawable.radio_button_applicant_background1));
        }
    }
}