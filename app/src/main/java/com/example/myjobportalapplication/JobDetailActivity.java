package com.example.myjobportalapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myjobportalapplication.EmployerPart.RecruiterProfile;
import com.example.myjobportalapplication.data_Model.Data;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Date;

public class JobDetailActivity extends AppCompatActivity {
    private EditText mTitle;
    private EditText mDate;
    private EditText mDescription;
    private EditText mSkills;
    private EditText mSalary;
    private Button delete;
    private Button contact;
    private Button apply;
    private Button cancel;
    private Button edit;
    private DatabaseReference mDatabaseReference;
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitle = findViewById(R.id.job_details_title);
        mDate = findViewById(R.id.job_details_date);
        mDescription = findViewById(R.id.job_details_description);
        mSalary = findViewById(R.id.job_details_salary);
        mSkills = findViewById(R.id.job_details_skills);
        delete = findViewById(R.id.deleteButton);
        contact = findViewById(R.id.contactButton);
        apply = findViewById(R.id.applyButton);
        cancel = findViewById(R.id.cancelButton);
        edit = findViewById(R.id.editButton);

        cancel.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);
        edit.setVisibility(View.GONE);
        //Recieve data from all job activity
        Intent intent = getIntent();

        String title = intent.getStringExtra("title");
        String date = intent.getStringExtra("date");
        String description = intent.getStringExtra("description");
        String skills = intent.getStringExtra("skills");
        String salary = intent.getStringExtra("salary");
        String jobId = intent.getStringExtra("job id");
        String recruiterID = intent.getStringExtra("user id");
        int value = intent.getIntExtra("able to delete?", 0);

        mTitle.setText(title);
        mDate.setText("Date: " + date);
        mDescription.setText("Description: " +description);
        mSalary.setText("Salary: " + salary);
        mSkills.setText("Skills: " + skills);

        mTitle.setEnabled(false);
        mTitle.setTextColor(Color.BLACK);
        mDate.setEnabled(false);
        mDate.setTextColor(Color.BLACK);
        mDescription.setEnabled(false);
        mDescription.setTextColor(Color.BLACK);
        mSalary.setEnabled(false);
        mSalary.setTextColor(Color.BLACK);
        mSkills.setEnabled(false);
        mSkills.setTextColor(Color.BLACK);

        if(value == 1){
            mTitle.setEnabled(true);
            mDate.setEnabled(true);
            mDescription.setEnabled(true);
            mSalary.setEnabled(true);
            mSkills.setEnabled(true);

            delete.setVisibility(View.VISIBLE);
            edit.setVisibility(View.VISIBLE);
            contact.setVisibility(View.GONE);
            apply.setVisibility(View.GONE);
        }if(value == 2){
            apply.setVisibility(View.GONE);
            cancel.setVisibility(View.VISIBLE);
        }

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Job Post").child(recruiterID).child("Recruiter Job Post").child(jobId);
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDatabaseReference.removeValue().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Cannot delete in account database", Toast.LENGTH_SHORT).show();
                    }
                });
                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Public database").child(jobId);
                mDatabaseReference.removeValue().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Cannot delete in public database", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), OtherPeopleProfile.class);
                intent.putExtra("userID", recruiterID);
                startActivity(intent);
            }
        });
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Data data = new Data(title, skills, description, salary, jobId, date, recruiterID);

                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Job Post").child(userID).child("Applicant Job Post").child(jobId);
                mDatabaseReference.setValue(data);

                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Job Post").child(recruiterID).child("Candidates List").child(jobId);
                mDatabaseReference.setValue(data);

                Toast.makeText(getApplicationContext(), "Successful! You can check for more detail at My Job Management.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Job Post").child(userID).child("Applicant Job Post").child(jobId);
                mDatabaseReference.removeValue().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Cannot delete in Applicant database", Toast.LENGTH_SHORT).show();
                    }
                });
                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Job Post").child(recruiterID).child("Candidates List").child(jobId);
                mDatabaseReference.removeValue().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Cannot delete in Candidates database", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mDatabaseReference = FirebaseDatabase.getInstance().getReference().child;
            }
        });
    }
}