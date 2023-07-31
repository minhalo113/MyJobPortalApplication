package com.example.myjobportalapplication;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RecruiterActivity extends AppCompatActivity {

    private ImageButton avatarImage;
    private EditText nameRecruiter;
    private RecyclerView postedJobList;
    private FloatingActionButton addJob;
    uiDrawer UIDRAWER = new uiDrawer();
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFStore;
    private DatabaseReference mJobPostDatabase;
    private StorageReference mStorage;
    private String uId;

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    // Handle the returned Uri
                    if(uri != null){
                        try{
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);

                            int desiredWidth = avatarImage.getWidth();
                            int desiredHeight = avatarImage.getHeight();

                            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, desiredWidth, desiredHeight, true);

                            avatarImage.setImageBitmap(scaledBitmap);
                            uploadFile(bitmap);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            });
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruiter);

        mAuth = FirebaseAuth.getInstance();
        mFStore = FirebaseFirestore.getInstance();
        uId = mAuth.getCurrentUser().getUid();

        mJobPostDatabase = FirebaseDatabase.getInstance().getReference().child("Job Post").child(uId);
        mStorage = FirebaseStorage.getInstance().getReference().child("avatarImage" + uId);

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


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        UIDRAWER.myuiDrawer(this, mAuth);

        uiRecruiter();
    }

    protected void uiRecruiter(){
        avatarImage = findViewById(R.id.avatarImageButton);
        nameRecruiter = findViewById(R.id.nameRecruiter);
        postedJobList = findViewById(R.id.recyclerJobPost);
        addJob = findViewById(R.id.floatingActionButton);

        DocumentReference documentReference = mFStore.collection("Recruiter").document(uId);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null) {
                    nameRecruiter.setText(value.getString("name"));
                }else{
                    Log.d(TAG, "Document does not exist or user doesn't have access.");
                }
            }
        });

        //load avatar Images from database when user just log in
        final long ONE_MEGABYTE = 1024 * 1024;
        mStorage.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap =BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                int desiredWidth = avatarImage.getWidth();
                int desiredHeight = avatarImage.getHeight();

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, desiredWidth, desiredHeight, true);

                avatarImage.setImageBitmap(scaledBitmap);
            }
        });

        //handle when user press the avatar Image button
        avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
        nameRecruiter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    // Retrieve the entered name
                    String newName = nameRecruiter.getText().toString();
                    // Set the new name as the text value of nameRecruiter
                    nameRecruiter.setText(newName);
                    Map<String, Object> user = new HashMap<>();
                    user.put("name", newName);
                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "Name created for " + uId);
                        }
                    });
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
        if(UIDRAWER!=null && UIDRAWER.onOptionsItemSelected(item) == true){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onBackPressed(){
        UIDRAWER.onBackPressed();
    }
    private void openFileChooser(){
        mGetContent.launch("image/*");
    }
    private void uploadFile(Bitmap mImage){
        if(mImage != null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = mStorage.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Image failed to upload.");
                }
            });
        }else{
            Toast.makeText(this, "No Image selected", Toast.LENGTH_SHORT).show();
        }
    }
}