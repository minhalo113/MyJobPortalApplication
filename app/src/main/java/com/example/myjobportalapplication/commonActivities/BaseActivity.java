package com.example.myjobportalapplication.commonActivities;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BaseActivity extends AppCompatActivity {
    private DocumentReference documentReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        documentReference = database.collection("Job Applicant").document(mAuth.getCurrentUser().getUid());
    }
    @Override
    protected void onPause() {
        super.onPause();
        documentReference.update("availability", 0);
    }

    protected void onResume(){
        super.onResume();
        documentReference.update("availability", 1);
    }
}
