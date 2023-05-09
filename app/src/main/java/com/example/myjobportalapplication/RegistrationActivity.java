package com.example.myjobportalapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class RegistrationActivity extends AppCompatActivity {

    private EditText usernameReg;
    private EditText passwordReg;

    private Button btnReg;
    private Button btnBack;

    private RadioGroup accType;
    private RadioButton applicantAcc;
    private RadioButton recuiterAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Registration();
    }

    private void Registration(){
        usernameReg = findViewById(R.id.usernameRegistration);
        passwordReg = findViewById(R.id.passwordRegistration);

        btnReg = findViewById(R.id.registerButtonRegistration);
        btnBack = findViewById(R.id.backButtonRegistration);

        accType = findViewById(R.id.accTypeRegistration);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        accType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                applicantAcc = findViewById(R.id.applicantButtonRegistration);
                recuiterAcc = findViewById(R.id.recuiterButtonRegistration);
            }
        });

    }
}