package com.example.myjobportalapplication.commonActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myjobportalapplication.EmployerPart.OtherRecruiterProfile;
import com.example.myjobportalapplication.R;
import com.example.myjobportalapplication.data_Model.Data;
import com.example.myjobportalapplication.data_Model.applicantData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Map;

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
    private DocumentReference mdocumentReference;
    private DatabaseReference mDatabaseReference;

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

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
        mDescription.setText(description);
        mSalary.setText(salary);
        mSkills.setText( skills);

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

                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Job Post");
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot accountSnapshot : snapshot.getChildren()){
                            DataSnapshot CandidateSnapshot = accountSnapshot.child("Candidates List");
                            if(CandidateSnapshot.hasChild(jobId)){
                                CandidateSnapshot.child(jobId).getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                });
                            }
                            DataSnapshot JobApplicantSnapshot = accountSnapshot.child("Applicant Job Post");
                            if(JobApplicantSnapshot.hasChild(jobId)){
                                JobApplicantSnapshot.child(jobId).getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                finish();
            }
        });
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), OtherRecruiterProfile.class);
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

                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Job Post").child(recruiterID).child("Candidates List").child(jobId).child(userID);

                mdocumentReference = FirebaseFirestore.getInstance().collection("Job Applicant").document(userID);
                mdocumentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (value != null) {
                                String appName = value.getString("name");
                                String appAge = value.getString("Age");
                                applicantData mData = new applicantData(appName, appAge);
                                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Job Post").child(recruiterID).child("Candidates List").child(jobId).child(userID);
                                mDatabaseReference.setValue(mData);
                            }
                        }
                });

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
                if(TextUtils.isEmpty(title)){
                    mTitle.setError("Required Field...");
                    return;
                }
                if(TextUtils.isEmpty(skills)){
                    mSkills.setError("Required Field...");
                    return;
                }
                if(TextUtils.isEmpty(description)){
                    mDescription.setError("Required Field...");
                    return;
                }
                if(TextUtils.isEmpty(salary)){
                    mSalary.setError("Required Field...");
                    return;
                }

                String title = mTitle.getText().toString().trim();
                String skills = mSkills.getText().toString().trim();
                String description = mDescription.getText().toString().trim();
                String salary = mSalary.getText().toString().trim();

                Data data = new Data(title, skills, description, salary, jobId, date, userID);
                Map<String, Object> newValue = data.toMap();

                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Job Post").child(recruiterID).child("Recruiter Job Post").child(jobId);
                mDatabaseReference.setValue(data);

                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Public database").child(jobId);
                mDatabaseReference.setValue(data);

                mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Job Post");
                mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot accountSnapshot : snapshot.getChildren()){
                            DataSnapshot JobApplicantSnapshot = accountSnapshot.child("Applicant Job Post");
                            if(JobApplicantSnapshot.hasChild(jobId)){
                                JobApplicantSnapshot.child(jobId).getRef().setValue(data).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Change Unuccessful", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}