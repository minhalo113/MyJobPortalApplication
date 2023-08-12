package com.example.myjobportalapplication.LoginRegistration;

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

import com.example.myjobportalapplication.EmployerPart.RecruiterJobList;
import com.example.myjobportalapplication.EmployerPart.RecruiterProfile;
import com.example.myjobportalapplication.JobSeekerPart.ApplicantActivity;
import com.example.myjobportalapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

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
    private FirebaseFirestore mFirestore;
    private ProgressDialog mDialog;
    private String myUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        mFireAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
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
                                finish();
                            }else{
                                myUid = mFireAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = mFirestore.collection("Recruiter").document(myUid);
                                Map<String, Object> user = new HashMap<>();
                                user.put("email", email);
                                user.put("password", password);
                                documentReference.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getApplicationContext(), "Data saved to database", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                startActivity(new Intent(getApplicationContext(), RecruiterProfile.class));
                                finish();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "Unable to Create Account", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }
                    }
                });
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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