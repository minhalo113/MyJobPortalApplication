package com.example.myjobportalapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;

    private Button loginBut;
    private Button regisBut;

    private RadioGroup accountType;
    private RadioButton recruiter;
    private RadioButton applicant;
    private boolean accType = false;

    //firebase auth
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        mAuth = FirebaseAuth.getInstance();

        mDialog = new ProgressDialog(this);

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
                String mEmail = email.getText().toString();
                String mPassword = password.getText().toString();

                if(TextUtils.isEmpty(mEmail)){
                    email.setError("Required email");
                    return;
                }
                if(TextUtils.isEmpty(mPassword)){
                    password.setError("Required password");
                    return;
                }

                mDialog.setMessage("Processing...");
                mDialog.show();
                mAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Login Successful", Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            if(accType == false){
                                startActivity(new Intent(getApplicationContext(), ApplicantActivity.class));
                            }else{
                                startActivity(new Intent(getApplicationContext(), RecruiterActivity.class));
                            }
                            mDialog.dismiss();
                        }else{
                            Toast.makeText(getApplicationContext(),"Login Failed...", Toast.LENGTH_SHORT).show();

                            mDialog.dismiss();
                        }
                    }
                });

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
                recruiter = findViewById(R.id.recruiterButtonLogin);
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
        recruiter = findViewById(R.id.recruiterButtonLogin);
        if(accType == false){
            recruiter.setBackground(getDrawable(R.drawable.radio_button_recruiter_background2));
            applicant.setBackground(getDrawable(R.drawable.radio_button_applicant_background2));
        }else{
            recruiter.setBackground(getDrawable(R.drawable.radio_button_recruiter_background1));
            applicant.setBackground(getDrawable(R.drawable.radio_button_applicant_background1));
        }
    }
}