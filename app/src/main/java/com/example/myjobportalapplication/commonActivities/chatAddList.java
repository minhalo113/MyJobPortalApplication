package com.example.myjobportalapplication.commonActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myjobportalapplication.Adapter.mAdapter;
import com.example.myjobportalapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class chatAddList extends BaseActivity {
    ProgressBar mProgressBar;
    private TextView errorMess;
    private RecyclerView mRecyclerView;
    private StorageReference mImage;

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_add_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        mRecyclerView = findViewById(R.id.chatAddRecyclerView);
        getUsers();
    }
    private void getUsers(){
        loading(true);
        ArrayList <String> names = new ArrayList<>();
        ArrayList <String> phones = new ArrayList<>();
        ArrayList <String> emails = new ArrayList<>();
//        ArrayList <String> tokens = new ArrayList<>();
        ArrayList<String> userIds = new ArrayList<>();
        ArrayList<Bitmap> images = new ArrayList<>();

        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection("Job Applicant").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        if(uId.equals(document.getId())){
                            continue;
                        }
                        String name = (String) document.get("name");
                        String phone = (String) document.get("Contact");
                        String email = (String) document.get("email");
                        String userId = (String) document.getId();
//                        String token = (String) document.get("FCM TOKEN");
//
//                        tokens.add(token);
                        userIds.add(userId);
                        names.add(name);
                        phones.add(phone);
                        emails.add(email);
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Cannot Load Users from Database", Toast.LENGTH_SHORT).show();
                }
                StorageReference mStorage = FirebaseStorage.getInstance().getReference();
                for(String userId : userIds){
                    mImage = mStorage.child("avatarApplicantImage/" + userId);
                    final long ONE_MEGABYTE = 1024 * 1024;
                    mImage.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            images.add(bitmap);

                            if(images.size() == userIds.size()) {
                                loading(false);
                                mAdapter newAdapter = new mAdapter(names, phones, images, emails, userIds, id -> {
                                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                    intent.putExtra("userId", id);
                                    startActivity(intent);
                                    finish();
                                });
                                mRecyclerView.setAdapter(newAdapter);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            images.add(null);
                        }
                    });
                }
                }
            });
        }
//        database.collection("Recruiter").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()){
//                    for(QueryDocumentSnapshot document : task.getResult()){
//                        if(uId.equals(document.getId())){
//                            continue;
//                        }
//                        String name = (String) document.get("name");
//                        String phone = (String) document.get("Phone Recruiter");
//                        String email = (String) document.get("Recruiter's email");
//                        String token = (String) document.get("FCM TOKEN");
//
//                        tokens.add(token);
//                        names.add(name);
//                        phones.add(phone);
//                        emails.add(email);
//                    }
//                }else{
//                    Toast.makeText(getApplicationContext(), "Cannot Load Users from Database", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


    private void showErrorMessage(){
        errorMess = findViewById(R.id.textErrorMessageAdd);
        errorMess.setText(String.format("%s", "No user available"));
        errorMess.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading){
        mProgressBar = findViewById(R.id.progressBarChatAdd);
        if(isLoading){
            mProgressBar.setVisibility(View.VISIBLE);
        }else{
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}