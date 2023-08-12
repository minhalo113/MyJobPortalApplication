package com.example.myjobportalapplication.uiDrawer;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.myjobportalapplication.ChatActivity;
import com.example.myjobportalapplication.EmployerPart.RecruiterProfile;
import com.example.myjobportalapplication.JobListActivity;
import com.example.myjobportalapplication.LoginRegistration.LoginActivity;
import com.example.myjobportalapplication.R;
import com.example.myjobportalapplication.EmployerPart.RecruiterJobList;
import com.example.myjobportalapplication.Settings.Settings;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class uiDrawer extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    public void myuiDrawer(Activity activity, FirebaseAuth mAuth) {
        drawerLayout = activity.findViewById(R.id.drawer_menu);
        navigationView = activity.findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.jobpostNavigateBar: {
                            Toast.makeText(activity, "Job Listings Selected", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity.getApplicationContext(), JobListActivity.class);
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
                        Toast.makeText(activity, "Chat Selected", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case  R.id.settingsNavigateBar:{
                        Toast.makeText(activity, "Settings Selected", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(activity.getApplicationContext(), Settings.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                        break;
                    }
                    case R.id.jobManageNavigateBar:{
                        Toast.makeText(activity, "My Job Management Selected", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(activity.getApplicationContext(), RecruiterJobList.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                        break;
                    }
                    case R.id.logoutNavigateBar:{
                        Toast.makeText(activity, "Logged Out", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(activity.getApplicationContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                        mAuth.signOut();
                        break;
                    }
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
