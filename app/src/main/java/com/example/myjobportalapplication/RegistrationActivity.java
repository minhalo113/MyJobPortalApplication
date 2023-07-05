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

public class RegistrationActivity extends AppCompatActivity {

    private EditText emailReg;
    private EditText passwordReg;

    private Button btnReg;
    private Button btnBack;

    private RadioGroup btnAccType;
    private RadioButton applicantAcc;
    private RadioButton recruiterAcc;
    //accType = false -> Applicant
    //accType = true -> Recruiter
    boolean accType = false;

    //firebase auth
    private FirebaseAuth mFireAuth;

    private ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        mFireAuth = FirebaseAuth.getInstance();

        mDialog = new ProgressDialog(this);

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
                mDialog.setMessage("Processing...");
                mDialog.show();
                mFireAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Create Account Successful", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                            //startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            if(accType == false){
                                startActivity(new Intent(getApplicationContext(), ApplicantActivity.class));
                            }else{
                                startActivity(new Intent(getApplicationContext(), RecruiterActivity.class));
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "Unable to Create Account", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }
                    }
                });
                if(accType == false){
                    startActivity(new Intent(getApplicationContext(), ApplicantActivity.class));
                }else{
                    startActivity(new Intent(getApplicationContext(), RecruiterActivity.class));
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
        btnAccType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                applicantAcc = findViewById(R.id.applicantButtonRegistration);
                recruiterAcc = findViewById(R.id.recruiterButtonRegistration);

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
        recruiterAcc = findViewById(R.id.recruiterButtonRegistration);
        if(accType == false){
            recruiterAcc.setBackground(getDrawable(R.drawable.radio_button_recruiter_background2));
            applicantAcc.setBackground(getDrawable(R.drawable.radio_button_applicant_background2));
        }else{
            recruiterAcc.setBackground(getDrawable(R.drawable.radio_button_recruiter_background1));
            applicantAcc.setBackground(getDrawable(R.drawable.radio_button_applicant_background1));
        }
    }
}