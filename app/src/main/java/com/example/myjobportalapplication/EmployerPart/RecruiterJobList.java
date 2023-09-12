package com.example.myjobportalapplication.EmployerPart;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myjobportalapplication.JobDetailActivity;
import com.example.myjobportalapplication.R;
import com.example.myjobportalapplication.data_Model.Data;
import com.example.myjobportalapplication.uiDrawer.uiDrawer;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RecruiterJobList extends AppCompatActivity {
    private RecyclerView postedJobList;
    private FloatingActionButton addJob;
    uiDrawer UIDRAWER = new uiDrawer();
    private FirebaseAuth mAuth;
    private DatabaseReference mJobPostDatabase;
    private String uId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruiter_job_list);

        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();

        mJobPostDatabase = FirebaseDatabase.getInstance().getReference().child("Job Post").child(uId).child("Recruiter Job Post");

        postedJobList = findViewById(R.id.recyclerJobPost);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        postedJobList.setHasFixedSize(true);
        postedJobList.setLayoutManager(layoutManager);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        UIDRAWER.myuiDrawer(this, mAuth, 0);
        uiRecruiter();
    }

    protected void uiRecruiter(){
        postedJobList = findViewById(R.id.recyclerJobPost);
        addJob = findViewById(R.id.floatingActionButton);

        addJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddJobActivity.class));
            }
        });

    }
    protected void onStart() {
        super.onStart();

        Query query = mJobPostDatabase;

        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(query, Data.class)
                        .build();
        FirebaseRecyclerAdapter<Data, myViewholder> mAdapter = new FirebaseRecyclerAdapter<Data, myViewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull myViewholder holder, int position, @NonNull Data model) {

                holder.setJobTitle(model.getTitle());
                holder.setJobDate(model.getDate());
                holder.setJobDescription(model.getDescription());
                holder.setJobSkills(model.getSkills());
                holder.setSalary(model.getSalary());
                holder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), JobDetailActivity.class);

                        intent.putExtra("title", model.getTitle());
                        intent.putExtra("date", model.getDate());
                        intent.putExtra("description", model.getDescription());
                        intent.putExtra("skills", model.getSkills());
                        intent.putExtra("salary", model.getSalary());
                        intent.putExtra("job id", model.getId());
                        intent.putExtra("user id", model.getRecruiterID());
                        intent.putExtra("able to delete?", 1);

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            };

            @NonNull
            @Override
            public myViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.job_item, parent, false);
                return new myViewholder(view);
            }
        };
        mAdapter.startListening();
        postedJobList.setAdapter(mAdapter);
    }
    public static class myViewholder extends RecyclerView.ViewHolder{
        View myView;
        public myViewholder(View itemView){
            super(itemView);
            myView = itemView;
        }

        public void setJobTitle(String title){
            TextView mTitle = myView.findViewById(R.id.JobTitle);
            mTitle.setText(title);
        }

        public void setJobDate(String date){
            TextView mDate = myView.findViewById(R.id.DatePosted);
            mDate.setText(date);
        }

        public void setJobDescription(String description){
            TextView mDescription = myView.findViewById(R.id.JobDescriptionDetail);
            mDescription.setText(description);
        }

        public void setJobSkills(String skills){
            TextView mSkills = myView.findViewById(R.id.SkillsDetail);
            mSkills.setText(skills);
        }

        public void setSalary(String salary){
            TextView mSalary = myView.findViewById(R.id.SalaryDetail);
            mSalary.setText(salary);
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if(UIDRAWER!=null && UIDRAWER.onOptionsItemSelected(item) == true){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onBackPressed(){
        UIDRAWER.onBackPressed();
    }
}