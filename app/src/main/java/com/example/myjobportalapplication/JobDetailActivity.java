package com.example.myjobportalapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myjobportalapplication.EmployerPart.RecruiterProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class JobDetailActivity extends AppCompatActivity {
    private TextView mTitle;
    private TextView mDate;
    private TextView mDescription;
    private TextView mSkills;
    private TextView mSalary;
    private Button delete;
    private Button contact;
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

        delete.setVisibility(View.GONE);
        //Recieve data from all job activity
        Intent intent = getIntent();

        String title = intent.getStringExtra("title");
        String date = intent.getStringExtra("date");
        String description = intent.getStringExtra("description");
        String skills = intent.getStringExtra("skills");
        String salary = intent.getStringExtra("salary");
        String jobId = intent.getStringExtra("job id");
        String userID = intent.getStringExtra("user id");
        int value = intent.getIntExtra("able to delete?", 0);
        if(value == 1){
            delete.setVisibility(View.VISIBLE);
            contact.setVisibility(View.GONE);
        }

        mTitle.setText(title);
        mDate.setText("Date: " + date);
        mDescription.setText("Description: " +description);
        mSalary.setText("Salary: " + salary);
        mSkills.setText("Skills: " + skills);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Job Post").child(userID).child(jobId);
                mDatabaseReference.removeValue().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Cannot delete in account database", Toast.LENGTH_SHORT).show();
                    }
                });
                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Public database").child(jobId);            mDatabaseReference.removeValue().addOnFailureListener(new OnFailureListener() {
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
                Intent intent = new Intent(getApplicationContext(), RecruiterProfile.class);
                intent.putExtra("able to make change?", 0);
                startActivity(intent);
                finish();
            }
        });
    }
}