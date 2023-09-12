package com.example.myjobportalapplication.EmployerPart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myjobportalapplication.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class CandidateDetailActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    ArrayList<String> applicantNamesList;
    ArrayList<String> applicantAgeList;
    ArrayList<String> applicantIdList;
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

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);

    }
    protected void onStart(){
        super.onStart();

        Bundle bundleName = getIntent().getExtras();
        Bundle bundleAge = getIntent().getExtras();
        Bundle bundleId = getIntent().getExtras();

        applicantNamesList = bundleName.getStringArrayList("applicantNamesList");
        applicantAgeList = bundleName.getStringArrayList("applicantAgeList");
        applicantIdList = bundleName.getStringArrayList("applicantIdList");

        MyAdapter madapter = new MyAdapter(applicantNamesList, applicantAgeList);
        mRecyclerView.setAdapter(madapter);
    }
    public class MyAdapter extends RecyclerView.Adapter<MyViewholder>{
        private ArrayList<String> itemList1;
        private ArrayList<String> itemList2;
        public MyAdapter(ArrayList<String> itemList1, ArrayList<String> itemList2) {
            this.itemList1 = itemList1;
            this.itemList2 = itemList2;
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

                }
            });
        }

        @Override
        public int getItemCount() {
            return itemList1.size();
        }
    }
    public class MyViewholder extends RecyclerView.ViewHolder{
        TextView textview;
        public MyViewholder(@NonNull View itemView) {
            super(itemView);
        }
        public void setString(String s, String x){
            TextView mString = textview.findViewById(R.id.nameAge);
            mString.setText(s + ", " + x + " years old.");
        }
    }

}