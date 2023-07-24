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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myjobportalapplication.data_Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class RecruiterActivity extends AppCompatActivity {

    private ImageButton avatarImage;
    private EditText nameRecruiter;
    private RecyclerView postedJobList;
    private FloatingActionButton addJob;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    private FirebaseAuth mAuth;
    private DatabaseReference mJobPostDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruiter);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        assert mUser != null;
        String uId = mUser.getUid();

        mJobPostDatabase = FirebaseDatabase.getInstance().getReference().child("Job Post").child(uId);

        postedJobList = findViewById(R.id.recyclerJobPost);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        postedJobList.setHasFixedSize(true);
        postedJobList.setLayoutManager(layoutManager);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        uiDrawer();
        uiRecruiter();
    }

    protected void uiRecruiter(){
        avatarImage = findViewById(R.id.avatarImageButton);
        nameRecruiter = findViewById(R.id.nameRecruiter);
        postedJobList = findViewById(R.id.recyclerJobPost);
        addJob = findViewById(R.id.floatingActionButton);

//        String jobPost[] = new String[]{"1st item","2nd item","3rd item"};
//        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(RecruiterActivity.this, android.R.layout.simple_list_item_1, jobPost);

        nameRecruiter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    // Retrieve the entered name
                    String newName = nameRecruiter.getText().toString();
                    // Set the new name as the text value of nameRecruiter
                    nameRecruiter.setText(newName);
                    // Clear the focus to dismiss the keyboard
                    nameRecruiter.clearFocus();
                    System.out.println(nameRecruiter.getText());
                    return true;
                }
                return false;
            }
        });

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
        if(drawerToggle.onOptionsItemSelected(item)){
            navigationView.bringToFront();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    protected void uiDrawer(){
        drawerLayout = findViewById(R.id.drawer_menu);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.homeNavigateBar:{
                        Toast.makeText(RecruiterActivity.this, "Home Selected", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), JobListActivity.class));
                        break;
                    }
                    case R.id.profileNavigateBar:{
                        Toast.makeText(RecruiterActivity.this, "You already at Profile", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.chatNavigateBar:{
                        Toast.makeText(RecruiterActivity.this, "Chat Selected", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                return false;
            }
        });
    }
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }
}