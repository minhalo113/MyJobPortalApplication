package com.example.myjobportalapplication.EmployerPart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myjobportalapplication.JobSeekerPart.OtherApplicantProfile;
import com.example.myjobportalapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.ArrayList;

public class CandidateDetailActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    ArrayList<String> applicantNamesList;
    ArrayList<String> applicantAgeList;
    ArrayList<String> applicantIdList;
    CollectionReference applicantRef;
    //private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_detail);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = findViewById(R.id.activityCandidateDetail);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        applicantRef = FirebaseFirestore.getInstance().collection("Job Applicant");

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);

    }
    protected void onStart(){
        super.onStart();

        applicantNamesList = getIntent().getStringArrayListExtra("candidateName");
        applicantAgeList = getIntent().getStringArrayListExtra("candidateAge");
        applicantIdList = getIntent().getStringArrayListExtra("candidateKey");

        MyAdapter madapter = new MyAdapter(applicantNamesList, applicantAgeList, applicantIdList);
        mRecyclerView.setAdapter(madapter);
    }
    public class MyAdapter extends RecyclerView.Adapter<MyViewholder>{
        private ArrayList<String> itemList1 = new ArrayList<>();
        private ArrayList<String> itemList2 = new ArrayList<>();
        private ArrayList<String> itemList3 = new ArrayList<>();
        public MyAdapter(ArrayList<String> itemList1, ArrayList<String> itemList2, ArrayList<String> itemList3) {
            this.itemList1 = itemList1;
            this.itemList2 = itemList2;
            this.itemList3 = itemList3;
        }

        @NonNull
        @Override
        public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_candidate_detail_item, parent, false);
            return new MyViewholder(itemView);
        }
        @Override
        public void onBindViewHolder(@NonNull MyViewholder holder, int position) {
            String item1 = itemList1.get(position);
            String item2 = itemList2.get(position);
            holder.setString(item1, item2);

            holder.textview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), OtherApplicantProfile.class);
                    intent.putExtra("userId", itemList3.get(holder.getAbsoluteAdapterPosition()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return itemList1.size();
        }
    }
    public class MyViewholder extends RecyclerView.ViewHolder{
        View textview;
        public MyViewholder(@NonNull View itemView) {
            super(itemView);
            textview = itemView;
        }
        public void setString(String s, String x){
            TextView mString = textview.findViewById(R.id.name);
            TextView mStringAge = textview.findViewById(R.id.Age);
            mString.setText("Name: " + s);
            mStringAge.setText("Age: " + x);
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}