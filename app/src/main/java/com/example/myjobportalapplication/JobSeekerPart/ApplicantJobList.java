package com.example.myjobportalapplication.JobSeekerPart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myjobportalapplication.commonActivities.JobDetailActivity;
import com.example.myjobportalapplication.R;
import com.example.myjobportalapplication.data_Model.Data;
import com.example.myjobportalapplication.uiDrawer.uiDrawer;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ApplicantJobList extends AppCompatActivity {
    private RecyclerView postedJobList;
    uiDrawer UIDRAWER = new uiDrawer();
    private FirebaseAuth mAuth;
    private DatabaseReference mJobPostDatabase;
    private String uId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_job_list);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();
        mJobPostDatabase = FirebaseDatabase.getInstance().getReference().child("Job Post").child(uId).child("Applicant Job Post");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        UIDRAWER.myuiDrawer(this, mAuth, 1);

        postedJobList = findViewById(R.id.recyclerApplicantJobPost);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        postedJobList.setHasFixedSize(true);
        postedJobList.setLayoutManager(layoutManager);
    }
    protected void onStart() {
        super.onStart();

        Query query = mJobPostDatabase;

        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(query, Data.class)
                        .build();
        FirebaseRecyclerAdapter<Data, ApplicantJobList.myViewholder> mAdapter = new FirebaseRecyclerAdapter<Data, ApplicantJobList.myViewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ApplicantJobList.myViewholder holder, int position, @NonNull Data model) {

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
                        intent.putExtra("able to delete?", 2);

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            };

            @NonNull
            @Override
            public ApplicantJobList.myViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.job_item, parent, false);
                return new ApplicantJobList.myViewholder(view);
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
        if(UIDRAWER != null && UIDRAWER.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onBackPressed(){
        UIDRAWER.onBackPressed();
    }
}