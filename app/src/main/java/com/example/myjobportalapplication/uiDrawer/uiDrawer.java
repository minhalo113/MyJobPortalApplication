package com.example.myjobportalapplication.uiDrawer;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.myjobportalapplication.EmployerPart.RecruiterProfile;
import com.example.myjobportalapplication.EmployerPart.candidateActivity;
import com.example.myjobportalapplication.commonActivities.ChatList;
import com.example.myjobportalapplication.commonActivities.JobListActivity;
import com.example.myjobportalapplication.JobSeekerPart.ApplicantJobList;
import com.example.myjobportalapplication.JobSeekerPart.ApplicantProfile;
import com.example.myjobportalapplication.LoginRegistration.LoginActivity;
import com.example.myjobportalapplication.R;
import com.example.myjobportalapplication.EmployerPart.RecruiterJobList;
import com.example.myjobportalapplication.Settings.Settings;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Field;

public class uiDrawer extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    int mAccType;
    public void myuiDrawer(Activity activity, FirebaseAuth mAuth, int accType) {
        drawerLayout = activity.findViewById(R.id.drawer_menu);
        navigationView = activity.findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        mAccType = accType;
        if(mAccType == 1){
            MenuItem item = navigationView.getMenu().findItem(R.id.candidatesNavigateBar);
            item.setVisible(false);
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //recruiter section
                if(mAccType == 0) {
                    switch (item.getItemId()) {
                        case R.id.jobpostNavigateBar: {
                            Toast.makeText(activity, "Job Listings Selected", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity.getApplicationContext(), JobListActivity.class);
                            intent.putExtra("accType", 0);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activity.startActivity(intent);
                            break;
                        }
                        case R.id.profileNavigateBar: {
                            Toast.makeText(activity, "Profile Selected", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity.getApplicationContext(), RecruiterProfile.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activity.startActivity(intent);
                            break;
                        }
                        case R.id.chatNavigateBar: {
                            Toast.makeText(activity, "Chat List Selected", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity.getApplicationContext(), ChatList.class);
                            intent.putExtra("accType", 0);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activity.startActivity(intent);
                            break;
                        }
                        case R.id.settingsNavigateBar: {
                            Toast.makeText(activity, "Settings Selected", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity.getApplicationContext(), Settings.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activity.startActivity(intent);
                            break;
                        }
                        case R.id.jobManageNavigateBar: {
                            Toast.makeText(activity, "My Job Management Selected", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity.getApplicationContext(), RecruiterJobList.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activity.startActivity(intent);
                            break;
                        }
                        case R.id.logoutNavigateBar: {
                            Toast.makeText(activity, "Logged Out", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity.getApplicationContext(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                            FirebaseFirestore database = FirebaseFirestore.getInstance();
                            DocumentReference documentReference = database.collection("Job Applicant").document(mAuth.getCurrentUser().getUid());
                            documentReference.update("FCM TOKEN", FieldValue.delete());
                            documentReference = database.collection("Recruiter").document(mAuth.getCurrentUser().getUid());
                            documentReference.update("FCM TOKEN", FieldValue.delete());
                            mAuth.signOut();
                            break;
                        }
                        case R.id.changeAccountNavigateBar: {
                            Toast.makeText(activity, "Logged Out", Toast.LENGTH_SHORT).show();
                            mAccType = 1;
                            Intent intent = new Intent(activity.getApplicationContext(), ApplicantProfile.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                            break;
                        }
                        case R.id.candidatesNavigateBar:{
                            Toast.makeText(activity, "Candidates Management", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity.getApplicationContext(), candidateActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activity.startActivity(intent);
                            break;
                        }
                    }
                }else if(mAccType == 1){
                    switch (item.getItemId()) {
                        case R.id.jobpostNavigateBar: {
                            Toast.makeText(activity, "Job Listings Selected", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity.getApplicationContext(), JobListActivity.class);
                            intent.putExtra("accType", 1);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activity.startActivity(intent);
                            break;
                        }
                        case R.id.profileNavigateBar: {
                            Toast.makeText(activity, "Profile Selected", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity.getApplicationContext(), ApplicantProfile.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activity.startActivity(intent);
                            break;
                        }
                        case R.id.chatNavigateBar: {
                            Toast.makeText(activity, "Chat List Selected", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity.getApplicationContext(), ChatList.class);
                            intent.putExtra("accType", 1);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activity.startActivity(intent);
                            break;
                        }
                        case R.id.settingsNavigateBar: {
                            Toast.makeText(activity, "Settings Selected", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity.getApplicationContext(), Settings.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activity.startActivity(intent);
                            break;
                        }
                        case R.id.jobManageNavigateBar: {
                            Toast.makeText(activity, "My Job Management Selected", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity.getApplicationContext(), ApplicantJobList.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activity.startActivity(intent);
                            break;
                        }
                        case R.id.logoutNavigateBar: {
                            Toast.makeText(activity, "Logged Out", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity.getApplicationContext(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                            FirebaseFirestore database = FirebaseFirestore.getInstance();
                            DocumentReference documentReference = database.collection("Job Applicant").document(mAuth.getCurrentUser().getUid());
                            documentReference.update("FCM TOKEN", FieldValue.delete());
                            documentReference = database.collection("Recruiter").document(mAuth.getCurrentUser().getUid());
                            documentReference.update("FCM TOKEN", FieldValue.delete());
                            mAuth.signOut();
                            break;
                        }
                        case R.id.changeAccountNavigateBar: {
                            Toast.makeText(activity, "Logged Out", Toast.LENGTH_SHORT).show();
                            mAccType = 1;
                            Intent intent = new Intent(activity.getApplicationContext(), RecruiterProfile.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                            break;
                        }
                    }
                }else{
                    Toast.makeText(activity, "Some Error occured. Please Log Out and try again!" + " " + mAccType, Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)){
            navigationView.bringToFront();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
