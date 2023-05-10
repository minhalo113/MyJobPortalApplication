package com.example.myjobportalapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private EditText emailReg;
    private EditText passwordReg;

    private Button btnReg;
    private Button btnBack;

    private RadioGroup btnAccType;
    private RadioButton applicantAcc;
    private RadioButton recuiterAcc;

    boolean accType = false;
    //firebase auth
    private FirebaseAuth mFireAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mFireAuth = FirebaseAuth.getInstance();

        Registration();
        colorChange();
    }

    private void Registration(){
        emailReg = findViewById(R.id.emailRegistration);
        passwordReg = findViewById(R.id.passwordRegistration);

        btnReg = findViewById(R.id.registerButtonRegistration);
        btnBack = findViewById(R.id.backButtonRegistration);

        btnAccType = findViewById(R.id.accTypeRegistration);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = emailReg.getText().toString().trim();
                String password = passwordReg.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    emailReg.setError("Required Email");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    passwordReg.setError("Required Password");
                    return;
                }
                mFireAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Create Account Successful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnAccType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                applicantAcc = findViewById(R.id.applicantButtonRegistration);
                recuiterAcc = findViewById(R.id.recuiterButtonRegistration);

                if(i == R.id.applicantButtonRegistration){
                    accType = false;
                }else{
                    accType = true;
                }
                System.out.println(accType);
                colorChange();
            }
        });
    }
    protected void colorChange(){
        applicantAcc = findViewById(R.id.applicantButtonRegistration);
        recuiterAcc = findViewById(R.id.recuiterButtonRegistration);
        if(accType == false){
            recuiterAcc.setBackground(getDrawable(R.drawable.radio_button_recuiter_background2));
            applicantAcc.setBackground(getDrawable(R.drawable.radio_button_applicant_background2));
        }else{
            recuiterAcc.setBackground(getDrawable(R.drawable.radio_button_recuiter_background1));
            applicantAcc.setBackground(getDrawable(R.drawable.radio_button_applicant_background1));
        }
    }
}