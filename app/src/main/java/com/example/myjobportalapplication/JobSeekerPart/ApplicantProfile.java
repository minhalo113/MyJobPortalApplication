package com.example.myjobportalapplication.JobSeekerPart;

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

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myjobportalapplication.R;
import com.example.myjobportalapplication.uiDrawer.uiDrawer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

public class ApplicantProfile extends AppCompatActivity {
    private ImageButton avatarImage;
    private EditText personName;
    private EditText Age;
    private EditText editTextPersonSkills;
    private EditText editTextPersonEmail;
    private EditText editTextPersonContact;
    private EditText editTextBriefIntro;
    private EditText editTextEducation;
    private EditText editTextExperience;
    private EditText editLanguage;
    private Button resumeUpload;
    private TextView resumeLink;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFStore;
    private StorageReference mStorage;
    private StorageReference resumeFile;
    DocumentReference documentReference;
    private String uId;
    uiDrawer UIDRAWER = new uiDrawer();
    int profileChange = 1;
    ActivityResultLauncher<String> mGetAvatarImage = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    // Handle the returned Uri
                    if(uri != null){
                        try{
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                                int desiredWidth = avatarImage.getWidth();
                                int desiredHeight = avatarImage.getHeight();

                                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, desiredWidth, desiredHeight, true);

                                avatarImage.setImageBitmap(scaledBitmap);
                                uploadFile(bitmap, mStorage);
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mFStore = FirebaseFirestore.getInstance();
        uId = mAuth.getCurrentUser().getUid();

        mStorage = FirebaseStorage.getInstance().getReference().child("avatarApplicantImage/" + uId);
        resumeFile = FirebaseStorage.getInstance().getReference().child("resumePDF/" + uId);

        documentReference = mFStore.collection("Job Applicant").document(uId);

        UIDRAWER.myuiDrawer(this, mAuth, 1);

        applicantProfile();
    }
    protected void applicantProfile(){
        avatarImage = findViewById(R.id.avatarImage);
        personName = findViewById(R.id.editTextPersonName);
        Age = findViewById(R.id.editTextPersonAge);
        editTextPersonSkills = findViewById(R.id.editTextPersonSkills);
        editTextPersonEmail = findViewById(R.id.editTextPersonEmail);
        editTextPersonContact = findViewById(R.id.editTextPersonContact);
        editTextBriefIntro = findViewById(R.id.editTextBriefIntro);
        editTextEducation = findViewById(R.id.editTextEducation);
        editTextExperience = findViewById(R.id.editTextExperience);
        editLanguage = findViewById(R.id.LanguageEditText);
        resumeUpload = findViewById(R.id.resumeUpload);
        resumeLink = findViewById(R.id.linkButton);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null) {
                    personName.setText(value.getString("name"));
                    Age.setText(value.getString("Age"));
                    editTextPersonSkills.setText(value.getString("PersonSkills"));
                    editTextPersonEmail.setText(value.getString("email"));
                    editTextPersonContact.setText(value.getString("Contact"));
                    editTextBriefIntro.setText(value.getString("Brief Intro"));
                    editTextEducation.setText(value.getString("Education"));
                    editTextExperience.setText(value.getString("Experience"));
                    editLanguage.setText(value.getString("Language"));
                }else{
                    Log.d(TAG, "Document does not exist or user doesn't have access.");
                }
            }
        });
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
        resumeFile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String downloadUrl = uri.toString();
                resumeLink.setText(downloadUrl);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Cannot load resume link", Toast.LENGTH_SHORT).show();
            }
        });
        resumeLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        String resumeLinkText = resumeLink.getText().toString();
                        if (Patterns.WEB_URL.matcher(resumeLinkText).matches()) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(resumeLinkText));
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid URL", Toast.LENGTH_SHORT).show();
                };
            }
        });
        if(profileChange == 1){
            profileChangeHandle();
        }else{
            personName.setEnabled(false);
            personName.setTextColor(getResources().getColor(android.R.color.primary_text_light));
            Age.setEnabled(false);
            Age.setTextColor(getResources().getColor(android.R.color.primary_text_light));
            editTextPersonSkills.setEnabled(false);
            editTextPersonSkills.setTextColor(getResources().getColor(android.R.color.primary_text_light));
            editTextPersonEmail.setEnabled(false);
            editTextPersonEmail.setTextColor(getResources().getColor(android.R.color.primary_text_light));
            editTextPersonContact.setEnabled(false);
            editTextPersonContact.setTextColor(getResources().getColor(android.R.color.primary_text_light));
            editTextBriefIntro.setEnabled(false);
            editTextBriefIntro.setTextColor(getResources().getColor(android.R.color.primary_text_light));
            editTextEducation.setEnabled(false);
            editTextEducation.setTextColor(getResources().getColor(android.R.color.primary_text_light));
            editTextExperience.setEnabled(false);
            editTextExperience.setTextColor(getResources().getColor(android.R.color.primary_text_light));
            editLanguage.setEnabled(false);
            editLanguage.setTextColor(getResources().getColor(android.R.color.primary_text_light));
        }
    }
    protected void profileChangeHandle(){
        avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
        personName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    // Retrieve the entered name
                    String newName = personName.getText().toString();
                    // Set the new name as the text value of nameRecruiter
                    personName.setText(newName);
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
        Age.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    // Retrieve the entered name
                    String newName = Age.getText().toString();
                    // Set the new name as the text value of nameRecruiter
                    Age.setText(newName);
                    Map<String, Object> user = new HashMap<>();
                    user.put("Age", newName);
                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "Age created for " + uId);
                        }
                    });
                    return true;
                }
                return false;
            }
        });
        editTextPersonSkills.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    // Retrieve the entered name
                    String newName = editTextPersonSkills.getText().toString();
                    // Set the new name as the text value of nameRecruiter
                    editTextPersonSkills.setText(newName);
                    Map<String, Object> user = new HashMap<>();
                    user.put("PersonSkills", newName);
                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "Skills created for " + uId);
                        }
                    });
                    return true;
                }
                return false;
            }
        });
        editTextPersonContact.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    // Retrieve the entered name
                    String newName = editTextPersonContact.getText().toString();
                    // Set the new name as the text value of nameRecruiter
                    editTextPersonContact.setText(newName);
                    Map<String, Object> user = new HashMap<>();
                    user.put("Contact", newName);
                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "Contact created for " + uId);
                        }
                    });
                    return true;
                }
                return false;
            }
        });
        editTextPersonEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    // Retrieve the entered name
                    String newName = editTextPersonEmail.getText().toString();
                    // Set the new name as the text value of nameRecruiter
                    editTextPersonEmail.setText(newName);
                    Map<String, Object> user = new HashMap<>();
                    user.put("Email", newName);
                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "Email created for " + uId);
                        }
                    });
                    return true;
                }
                return false;
            }
        });

        editTextBriefIntro.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    // Retrieve the entered name
                    String newName = editTextBriefIntro.getText().toString();
                    // Set the new name as the text value of nameRecruiter
                    editTextBriefIntro.setText(newName);
                    Map<String, Object> user = new HashMap<>();
                    user.put("Brief Intro", newName);
                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "Brief Intro created for " + uId);
                        }
                    });
                    return true;
                }
                return false;
            }
        });
        editTextEducation.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    // Retrieve the entered name
                    String newName = editTextEducation.getText().toString();
                    // Set the new name as the text value of nameRecruiter
                    editTextEducation.setText(newName);
                    Map<String, Object> user = new HashMap<>();
                    user.put("Education", newName);
                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "Education created for " + uId);
                        }
                    });
                    return true;
                }
                return false;
            }
        });
        editTextExperience.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    // Retrieve the entered name
                    String newName = editTextExperience.getText().toString();
                    // Set the new name as the text value of nameRecruiter
                    editTextExperience.setText(newName);
                    Map<String, Object> user = new HashMap<>();
                    user.put("Experience", newName);
                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "Experience created for " + uId);
                        }
                    });
                    return true;
                }
                return false;
            }
        });
        editLanguage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    // Retrieve the entered name
                    String newName = editLanguage.getText().toString();
                    // Set the new name as the text value of nameRecruiter
                    editLanguage.setText(newName);
                    Map<String, Object> user = new HashMap<>();
                    user.put("Language", newName);
                    documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "Language created for " + uId);
                        }
                    });
                    return true;
                }
                return false;
            }
        });
        resumeUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFiles();
            }
        });
    }

    private void openFileChooser(){
        mGetAvatarImage.launch("image/*");
    }
    private void uploadResume(Uri pdf){
        resumeFile.putFile(pdf).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "Upload Completed. Please wait 2 seconds to refresh.", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), ApplicantProfile.class));
                        finish();
                    }
                }, 2000);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void selectFiles(){
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF Files..."), 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data!= null && data.getData()!= null){
            uploadResume(data.getData());
        }
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