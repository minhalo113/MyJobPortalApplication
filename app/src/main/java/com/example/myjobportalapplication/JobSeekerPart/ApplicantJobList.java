package com.example.myjobportalapplication.JobSeekerPart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.myjobportalapplication.R;
import com.example.myjobportalapplication.uiDrawer.uiDrawer;
import com.google.firebase.auth.FirebaseAuth;

public class ApplicantJobList extends AppCompatActivity {
    uiDrawer UIDRAWER = new uiDrawer();
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_job_list);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        UIDRAWER.myuiDrawer(this, mAuth, 1);
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