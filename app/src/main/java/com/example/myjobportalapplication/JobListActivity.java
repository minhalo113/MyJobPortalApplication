package com.example.myjobportalapplication;

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
import android.widget.SearchView;
import android.widget.TextView;

import com.example.myjobportalapplication.data_Model.Data;
import com.example.myjobportalapplication.uiDrawer.uiDrawer;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Locale;

public class JobListActivity extends AppCompatActivity {
    private RecyclerView mainRecyclerView;
    private DatabaseReference mainAllJobPost;
    private FirebaseAuth mAuth;

    private String uId;
    private SearchView mSearchView;
    uiDrawer UIDRAWER = new uiDrawer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_list);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        mSearchView = findViewById(R.id.searchView);
        mSearchView.clearFocus();

        mainAllJobPost = FirebaseDatabase.getInstance().getReference().child("Public database");
        mainAllJobPost.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();

        mainRecyclerView = findViewById(R.id.mainRecyclerJobPost);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mainRecyclerView.setHasFixedSize(true);
        mainRecyclerView.setLayoutManager(layoutManager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int accType = getIntent().getIntExtra("accType", 2);
        UIDRAWER.myuiDrawer(this, mAuth, accType);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterSearch(s);
                return false;
            }
        });
    }

    protected void onStart(){
        super.onStart();
        Query query = mainAllJobPost;

        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(query, Data.class)
                        .build();
        FirebaseRecyclerAdapter<Data, JobListActivity.AllJobPostViewHolder> mAdapter = new FirebaseRecyclerAdapter<Data, AllJobPostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull JobListActivity.AllJobPostViewHolder holder, int position, @NonNull Data model) {

                holder.setJobTitle(model.getTitle());
                holder.setJobDate(model.getDate());
                holder.setJobDescription(model.getDescription());
                holder.setSkills(model.getSkills());
                holder.setSalary(model.getSalary());
                String userID = model.getRecruiterID();
                holder.myview.setOnClickListener(new View.OnClickListener() {
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
                        if(userID.equals(uId)){
                            intent.putExtra("able to delete?", 1);
                        }else{
                            intent.putExtra("able to delete?", 0);
                        }

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            };
            @NonNull
            @Override
            public JobListActivity.AllJobPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.job_item, parent, false);
                return new JobListActivity.AllJobPostViewHolder(view);
            }
        };
        mAdapter.startListening();
        mainRecyclerView.setAdapter(mAdapter);
    }
    public static class AllJobPostViewHolder extends RecyclerView.ViewHolder{
        View myview;
        public AllJobPostViewHolder(View itemView){
            super(itemView);
            myview=itemView;
        }
        public void setJobTitle(String title){
            TextView mTitle = myview.findViewById(R.id.JobTitle);
            mTitle.setText(title);
        }
        public void setJobDate(String date){
            TextView mDate = myview.findViewById(R.id.DatePosted);
            mDate.setText(date);
        }
        public void setSkills(String skills){
            TextView mSkill = myview.findViewById(R.id.SkillsDetail);
            mSkill.setText(skills);
        }
        public void setJobDescription(String description){
            TextView mDescription = myview.findViewById(R.id.JobDescriptionDetail);
            mDescription.setText(description);
        }
        public void setSalary(String salary){
            TextView mSalary = myview.findViewById(R.id.SalaryDetail);
            mSalary.setText(salary);
        }
    }

    private void filterSearch(String newText){
        Query query = mainAllJobPost;

        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(query, Data.class)
                        .build();

        FirebaseRecyclerAdapter<Data, JobListActivity.AllJobPostViewHolder> mAdapter = new FirebaseRecyclerAdapter<Data, AllJobPostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull JobListActivity.AllJobPostViewHolder holder, int position, @NonNull Data model) {
                if(model.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                    holder.setJobTitle(model.getTitle());
                    holder.setJobDate(model.getDate());
                    holder.setJobDescription(model.getDescription());
                    holder.setSkills(model.getSkills());
                    holder.setSalary(model.getSalary());
                    String userID = model.getRecruiterID();
                    holder.myview.setOnClickListener(new View.OnClickListener() {
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
                            if (userID.equals(uId)) {
                                intent.putExtra("able to delete?", 1);
                            } else {
                                intent.putExtra("able to delete?", 0);
                            }

                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    });
                }else{
                    holder.myview.setVisibility(View.GONE);
                    holder.myview.getLayoutParams().height = 0;
                    holder.myview.setPadding(0, 0, 0, 0);
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) holder.myview.getLayoutParams();
                    marginLayoutParams.setMargins(0, 0, 0, 0);
                }
            };
            @NonNull
            @Override
            public JobListActivity.AllJobPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.job_item, parent, false);
                return new JobListActivity.AllJobPostViewHolder(view);
            }
        };
        mAdapter.startListening();

        mainRecyclerView.setAdapter(mAdapter);
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