package com.example.myjobportalapplication.commonActivities;

import static com.google.common.primitives.UnsignedInts.max;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myjobportalapplication.R;
import com.example.myjobportalapplication.uiDrawer.uiDrawer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ChatList extends AppCompatActivity {
    private StorageReference mStorage;
    private StorageReference appStorage;
    private FloatingActionButton addChat;
    private FirebaseAuth mAuth;
    private RecyclerView mRecyclerView;
    private TextView name;
    private TextView email;
    private TextView phoneNumb;
    private ImageView avatarImage;

    private PreferenceManager preferenceManager;
    private String uId;
    uiDrawer UIDRAWER = new uiDrawer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();

        mRecyclerView = findViewById(R.id.userRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);

        int accType = getIntent().getIntExtra("accType", 2);
        UIDRAWER.myuiDrawer(this, mAuth, accType);

        addChat = findViewById(R.id.fabNewChat);
        loadUserDetail();
    }

    private void loadUserDetail(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        addChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), chatAddList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

//        mAdapter myAdapter = new mAdapter();
//        mRecyclerView.setAdapter(myAdapter);
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