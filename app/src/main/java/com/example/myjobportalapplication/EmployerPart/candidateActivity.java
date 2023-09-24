package com.example.myjobportalapplication.EmployerPart;

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

import com.example.myjobportalapplication.R;
import com.example.myjobportalapplication.data_Model.Data;
import com.example.myjobportalapplication.data_Model.applicantData;
import com.example.myjobportalapplication.uiDrawer.uiDrawer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class candidateActivity extends AppCompatActivity {
    private RecyclerView mainRecyclerView;
    private DatabaseReference mCandidateList;
    private DatabaseReference mRecruiterList;
    private FirebaseAuth mAuth;
    private ArrayList<String> jobTitle;
    private ArrayList<String> numbApplicants;
    private ArrayList<String> jobID = new ArrayList<>();
    int candidatesWithData = 0;
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
                for(DataSnapshot jobSnapshot : snapshot.getChildren()){
                    String jobKey = jobSnapshot.getKey();
                    long childrenCount = jobSnapshot.getChildrenCount();
                    numbApplicants.add(String.valueOf(childrenCount));

                    DatabaseReference mJobTitle = FirebaseDatabase.getInstance().getReference().child("Job Post").child(uId).child("Recruiter Job Post").child(jobKey);
                    mJobTitle.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                jobID.add(snapshot.getValue(Data.class).getId());
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

            holder.myTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> candidateInfo = new ArrayList<>();
                    ArrayList<String> candidateAge = new ArrayList<>();
                    ArrayList<String> candidateName = new ArrayList<>();
                    candidatesWithData = 0;

                    DatabaseReference mCandidateKey = mCandidateList.child(jobID.get(holder.getAbsoluteAdapterPosition()));
                    mCandidateKey.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int totalCandidates = (int) snapshot.getChildrenCount();

                            for(DataSnapshot candidateSnapshot : snapshot.getChildren()){
                                String candidateID = candidateSnapshot.getKey();
                                candidateInfo.add(candidateID);
                                mCandidateKey.child(candidateID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        System.out.println(snapshot.exists());
                                        if (snapshot.exists()) {
                                            applicantData data = snapshot.getValue(applicantData.class);
                                            candidateAge.add(data.getApplicantAge());
                                            candidateName.add(data.getApplicantName());
                                            candidatesWithData ++;
                                            System.out.println(candidateAge);

                                            if (candidatesWithData == totalCandidates){
                                                System.out.println(candidateAge);
                                                Intent intent = new Intent(getApplicationContext(), CandidateDetailActivity.class);
                                                intent.putStringArrayListExtra("candidateAge", candidateAge);
                                                intent.putStringArrayListExtra("candidateName", candidateName);
                                                intent.putStringArrayListExtra("candidateKey", candidateInfo);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                            }
                                        }

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
            });
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