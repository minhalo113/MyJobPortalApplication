package com.example.myjobportalapplication.EmployerPart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myjobportalapplication.R;
import com.example.myjobportalapplication.data_Model.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;


public class AddJobActivity extends AppCompatActivity {
    private EditText jobTitle;
    private EditText Skills;
    private EditText jobDescription;
    private EditText Salary;
    private Button postJob;

    private FirebaseAuth mAuth;
    private DatabaseReference mJobPost;
    private DatabaseReference mPublicDatabase;

    private String uId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_job_activity);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        uId = mUser.getUid();

        mJobPost = FirebaseDatabase.getInstance().getReference().child("Job Post").child(uId);
        mPublicDatabase = FirebaseDatabase.getInstance().getReference().child("Public database");

        insertJob();
    }

    protected void insertJob(){
        jobTitle = findViewById(R.id.jobTitle);
        Skills = findViewById(R.id.skills);
        jobDescription = findViewById(R.id.jobDescription);
        Salary = findViewById(R.id.Salary);
        postJob = findViewById(R.id.buttonJobPost);

        postJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = jobTitle.getText().toString().trim();
                String skills = Skills.getText().toString().trim();
                String description = jobDescription.getText().toString().trim();
                String salary = Salary.getText().toString().trim();

                if(TextUtils.isEmpty(title)){
                    jobTitle.setError("Required Field...");
                    return;
                }
                if(TextUtils.isEmpty(skills)){
                    Skills.setError("Required Field...");
                    return;
                }
                if(TextUtils.isEmpty(description)){
                    jobDescription.setError("Required Field...");
                    return;
                }
                if(TextUtils.isEmpty(salary)){
                    Salary.setError("Required Field...");
                    return;
                }
                String id = mJobPost.push().getKey();

                String date = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(title, skills, description, salary, id, date, uId);

                mJobPost.child(id).setValue(data);
                mPublicDatabase.child(id).setValue(data);

                Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
                finish();
            }

        });

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}