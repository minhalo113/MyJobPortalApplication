package com.example.myjobportalapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myjobportalapplication.data_Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import kotlinx.coroutines.Job;

public class JobListActivity extends AppCompatActivity {
    private RecyclerView mainRecyclerView;
    private DatabaseReference mainAllJobPost;

    uiDrawer UIDRAWER = new uiDrawer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_list);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        mainAllJobPost = FirebaseDatabase.getInstance().getReference().child("Public database");
        mainAllJobPost.keepSynced(true);

        mainRecyclerView = findViewById(R.id.mainRecyclerJobPost);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mainRecyclerView.setHasFixedSize(true);
        mainRecyclerView.setLayoutManager(layoutManager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        UIDRAWER.myuiDrawer(this);
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

                holder.myview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), JobDetailActivity.class);

                        intent.putExtra("title", model.getTitle());
                        intent.putExtra("date", model.getDate());
                        intent.putExtra("description", model.getDescription());
                        intent.putExtra("skills", model.getSkills());
                        intent.putExtra("salary", model.getSalary());

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