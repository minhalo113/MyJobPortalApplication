package com.example.myjobportalapplication;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class uiDrawer extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    public void myuiDrawer(Activity activity) {
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
                        if (activity instanceof RecruiterActivity || activity instanceof ChatActivity) {
                            Toast.makeText(activity, "Job Post Selected", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity.getApplicationContext(), JobListActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activity.startActivity(intent);
                            break;
                        }else{
                            Toast.makeText(activity, "You already at Job Post", Toast.LENGTH_SHORT).show();
                        }
                    }
                    case R.id.profileNavigateBar: {
                        if (activity instanceof JobListActivity || activity instanceof ChatActivity) {
                            Toast.makeText(activity, "Profile Selected", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(activity.getApplicationContext(), RecruiterActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activity.startActivity(intent);
                            break;
                        }else{
                            Toast.makeText(activity, "You already at Profile", Toast.LENGTH_SHORT).show();
                        }
                    }
                    case R.id.chatNavigateBar: {
                        Toast.makeText(activity, "Chat Selected", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.logoutNavigateBar:{
                        Toast.makeText(activity, "Logged Out", Toast.LENGTH_SHORT).show();
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
