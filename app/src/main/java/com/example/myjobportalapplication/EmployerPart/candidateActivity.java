package com.example.myjobportalapplication.EmployerPart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.example.myjobportalapplication.JobDetailActivity;
import com.example.myjobportalapplication.JobListActivity;
import com.example.myjobportalapplication.R;
import com.example.myjobportalapplication.data_Model.Data;
import com.example.myjobportalapplication.uiDrawer.uiDrawer;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class candidateActivity extends AppCompatActivity {
    private RecyclerView mainRecyclerView;
    private DatabaseReference mCandidateList;
    private DatabaseReference mRecruiterList;
    private FirebaseAuth mAuth;
    private ArrayList<String> jobTitle;
    private ArrayList<String> numbApplicants;
    private String uId;
    uiDrawer UIDRAWER = new uiDrawer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();

        mRecruiterList = FirebaseDatabase.getInstance().getReference().child("Job Post").child(uId).child("Recruiter Job Post");
        mRecruiterList.keepSynced(true);

        mCandidateList = FirebaseDatabase.getInstance().getReference().child("Job Post").child(uId).child("Candidates List");
        mCandidateList.keepSynced(true);

        mainRecyclerView = findViewById(R.id.candidateList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mainRecyclerView.setHasFixedSize(true);
        mainRecyclerView.setLayoutManager(layoutManager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        UIDRAWER.myuiDrawer(this, mAuth, 0);

    }

    protected void onStart(){
        super.onStart();
        numbApplicants = new ArrayList<>();
        jobTitle = new ArrayList<>();

        mCandidateList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot candidateSnapshot : snapshot.getChildren()){
                    String candidateKey = candidateSnapshot.getKey();
                    long childrenCount = candidateSnapshot.getChildrenCount();
                    numbApplicants.add(String.valueOf(childrenCount));

                    DatabaseReference mJobTitle = FirebaseDatabase.getInstance().getReference().child("Job Post").child(uId).child("Recruiter Job Post").child(candidateKey);
                    mJobTitle.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                jobTitle.add(snapshot.getValue(Data.class).getTitle());
                            }
                            mAdapter myAdapter = new mAdapter(jobTitle, numbApplicants);
                            mainRecyclerView.setAdapter(myAdapter);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public class mAdapter extends RecyclerView.Adapter<candidatePostViewHolder>{
        private ArrayList<String> jobTitle;
        private ArrayList<String> numbApplicant;

        public mAdapter(ArrayList<String> jobTitle, ArrayList<String> numbApplicant) {
            this.jobTitle = jobTitle;
            this.numbApplicant = numbApplicant;
        }

        @NonNull
        @Override
        public candidatePostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.candidate_item, parent, false);
            return new candidateActivity.candidatePostViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull candidatePostViewHolder holder, int position) {
            String item1 = jobTitle.get(position);
            String item2 = numbApplicant.get(position);
            holder.setJobTitle(item1);
            holder.setApplicant(item2);
        }

        @Override
        public int getItemCount() {
            return jobTitle.size();
        }
    }
    public static class candidatePostViewHolder extends RecyclerView.ViewHolder{
        View myTextView;
        public candidatePostViewHolder(View itemView){
            super(itemView);
            myTextView = itemView;
        }
        public void setJobTitle(String title){
            TextView mTitle = myTextView.findViewById(R.id.candidateJobTitle);
            mTitle.setText(title);
        }
        public void setApplicant(String applicant){
            TextView mApplicant = myTextView.findViewById(R.id.candidatesApplied);
            mApplicant.setText(applicant + " applicant(s) applying for this job");
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