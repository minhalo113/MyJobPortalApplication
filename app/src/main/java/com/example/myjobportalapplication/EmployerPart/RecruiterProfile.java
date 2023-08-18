package com.example.myjobportalapplication.EmployerPart;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myjobportalapplication.R;
import com.example.myjobportalapplication.uiDrawer.uiDrawer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class RecruiterProfile extends AppCompatActivity {

    private ImageButton avatarImage;
    private ImageButton companyImage;
    private EditText nameRecruiter;
    private EditText nameCompany;

    private EditText recruiterPosition;
    private EditText companyDescription;

    private EditText emailRecruiter;
    private EditText companyIndustry;

    private EditText phoneRecruiter;
    private EditText companyLocation;
    uiDrawer UIDRAWER = new uiDrawer();
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFStore;
    private StorageReference mStorage;
    private StorageReference compStorage;
    DocumentReference documentReference;
    private String uId;
    private int imageTap = 0;
    ActivityResultLauncher<String> mGetAvatarImage = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    // Handle the returned Uri
                    if(uri != null){
                        try{
                            if(imageTap == 1) {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                                int desiredWidth = avatarImage.getWidth();
                                int desiredHeight = avatarImage.getHeight();

                                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, desiredWidth, desiredHeight, true);

                                avatarImage.setImageBitmap(scaledBitmap);
                                uploadFile(bitmap, mStorage);
                            }
                            if(imageTap == 2){
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                                int desiredWidth = companyImage.getWidth();
                                int desiredHeight = companyImage.getHeight();

                                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, desiredWidth, desiredHeight, true);

                                companyImage.setImageBitmap(scaledBitmap);
                                uploadFile(bitmap, compStorage);
                            }
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruiter_profile);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mFStore = FirebaseFirestore.getInstance();
        uId = mAuth.getCurrentUser().getUid();

        mStorage = FirebaseStorage.getInstance().getReference().child("avatarImage/" + uId);
        compStorage = FirebaseStorage.getInstance().getReference().child("compImage/" + uId);

        documentReference = mFStore.collection("Recruiter").document(uId);

        UIDRAWER.myuiDrawer(this, mAuth, 0);

        recruiterProfile();
    }
    private void recruiterProfile(){
        avatarImage = findViewById(R.id.avatarImage);
        companyImage = findViewById(R.id.companyImage);

        nameRecruiter = findViewById(R.id.editTextPersonName);
        nameCompany = findViewById(R.id.editTextCompanyName);

        recruiterPosition = findViewById(R.id.editTextPersonPosition);
        companyDescription = findViewById(R.id.editTextCompanyDescription);

        emailRecruiter = findViewById(R.id.editTextPersonEmail);
        companyIndustry = findViewById(R.id.companyIndustryEditText);

        phoneRecruiter = findViewById(R.id.editTextPersonContact);
        companyLocation = findViewById(R.id.editTextCompanyLocation);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null) {
                    nameRecruiter.setText(value.getString("name"));
                    nameCompany.setText(value.getString("Company Name"));
                    recruiterPosition.setText(value.getString("Recruiter Position"));
                    companyDescription.setText(value.getString("Company Description"));
                    emailRecruiter.setText(value.getString("Recruiter's email"));
                    companyIndustry.setText(value.getString("Company Industry"));
                    phoneRecruiter.setText(value.getString("Phone Recruiter"));
                    companyLocation.setText(value.getString("Company Location"));
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
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                int desiredWidth = avatarImage.getWidth();
                int desiredHeight = avatarImage.getHeight();

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, desiredWidth, desiredHeight, true);

                avatarImage.setImageBitmap(scaledBitmap);
            }
        });
        compStorage.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                int desiredWidth = companyImage.getWidth();
                int desiredHeight = companyImage.getHeight();

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, desiredWidth, desiredHeight, true);

                companyImage.setImageBitmap(scaledBitmap);
            }
        });
        profileChangeHandle();
    }
    protected void profileChangeHandle(){
        //handle when user press the avatar Image button
        avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageTap = 1;
                openFileChooser();
            }
        });
        companyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageTap = 2;
                openFileChooser();}
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
                    return true;
                }
                return false;
            }
        });
        nameCompany.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    // Retrieve the entered name
                    String newName = nameCompany.getText().toString();
                    // Set the new name as the text value of nameRecruiter
                    nameCompany.setText(newName);
                    Map<String, Object> user = new HashMap<>();
                    user.put("Company Name", newName);
                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "Company Name created for " + uId);
                        }
                    });
                    return true;
                }
                return false;
            }
        });
        recruiterPosition.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    // Retrieve the entered name
                    String newName = recruiterPosition.getText().toString();
                    // Set the new name as the text value of nameRecruiter
                    recruiterPosition.setText(newName);
                    Map<String, Object> user = new HashMap<>();
                    user.put("Recruiter Position", newName);
                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "Recruiter Position created for " + uId);
                        }
                    });
                    return true;
                }
                return false;
            }
        });
        companyDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    // Retrieve the entered name
                    String newName = companyDescription.getText().toString();
                    // Set the new name as the text value of nameRecruiter
                    companyDescription.setText(newName);
                    Map<String, Object> user = new HashMap<>();
                    user.put("Company Description", newName);
                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "Company Description created for " + uId);
                        }
                    });
                    return true;
                }
                return false;
            }
        });
        emailRecruiter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    // Retrieve the entered name
                    String newName = emailRecruiter.getText().toString();
                    // Set the new name as the text value of nameRecruiter
                    emailRecruiter.setText(newName);
                    Map<String, Object> user = new HashMap<>();
                    user.put("Recruiter's email", newName);
                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "Recruiter's email created for " + uId);
                        }
                    });
                    return true;
                }
                return false;
            }
        });
        companyIndustry.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    // Retrieve the entered name
                    String newName = companyIndustry.getText().toString();
                    // Set the new name as the text value of nameRecruiter
                    companyIndustry.setText(newName);
                    Map<String, Object> user = new HashMap<>();
                    user.put("Company Industry", newName);
                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "Company Industry created for " + uId);
                        }
                    });
                    return true;
                }
                return false;
            }
        });
        phoneRecruiter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    // Retrieve the entered name
                    String newName = phoneRecruiter.getText().toString();
                    // Set the new name as the text value of nameRecruiter
                    phoneRecruiter.setText(newName);
                    Map<String, Object> user = new HashMap<>();
                    user.put("Phone Recruiter", newName);
                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "Phone Recruiter created for " + uId);
                        }
                    });
                    return true;
                }
                return false;
            }
        });
        companyLocation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    // Retrieve the entered name
                    String newName = companyLocation.getText().toString();
                    // Set the new name as the text value of nameRecruiter
                    companyLocation.setText(newName);
                    Map<String, Object> user = new HashMap<>();
                    user.put("Company Location", newName);
                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "Company Location created for " + uId);
                        }
                    });
                    return true;
                }
                return false;
            }
        });
    }
    private void openFileChooser(){
        mGetAvatarImage.launch("image/*");
    }
    private void uploadFile(Bitmap mImage, StorageReference choosenStorage){
        if(mImage != null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = choosenStorage.putBytes(data);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if(UIDRAWER!=null && UIDRAWER.onOptionsItemSelected(item) == true){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onBackPressed(){
        UIDRAWER.onBackPressed();
    }
}