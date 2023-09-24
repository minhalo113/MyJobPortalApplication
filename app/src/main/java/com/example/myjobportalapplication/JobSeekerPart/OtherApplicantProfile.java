package com.example.myjobportalapplication.JobSeekerPart;

import static android.content.ContentValues.TAG;

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
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class OtherApplicantProfile extends AppCompatActivity {
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
    private TextView resumeLink;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFStore;
    private StorageReference mStorage;
    private StorageReference resumeFile;
    DocumentReference documentReference;
    private String uId;
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_applicant_profile);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        uId = getIntent().getStringExtra("userId");

        mFStore = FirebaseFirestore.getInstance();

        mStorage = FirebaseStorage.getInstance().getReference().child("avatarApplicantImage/" + uId);
        resumeFile = FirebaseStorage.getInstance().getReference().child("resumePDF/" + uId);

        documentReference = mFStore.collection("Job Applicant").document(uId);

        applicantProfile();
    }

    private void applicantProfile(){
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
        resumeLink = findViewById(R.id.linkButton);

        personName.setEnabled(false);
        Age.setEnabled(false);
        editTextPersonSkills.setEnabled(false);
        editTextPersonEmail.setEnabled(false);
        editTextPersonContact.setEnabled(false);
        editTextBriefIntro.setEnabled(false);
        editTextEducation.setEnabled(false);
        editTextExperience.setEnabled(false);
        editLanguage.setEnabled(false);

        personName.setTextColor(getResources().getColor(android.R.color.black));
        Age.setTextColor(getResources().getColor(android.R.color.black));
        editTextPersonSkills.setTextColor(getResources().getColor(android.R.color.black));
        editTextPersonEmail.setTextColor(getResources().getColor(android.R.color.black));
        editTextPersonContact.setTextColor(getResources().getColor(android.R.color.black));
        editTextBriefIntro.setTextColor(getResources().getColor(android.R.color.black));
        editTextEducation.setTextColor(getResources().getColor(android.R.color.black));
        editTextExperience.setTextColor(getResources().getColor(android.R.color.black));
        editLanguage.setTextColor(getResources().getColor(android.R.color.black));

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null){
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
                }
            }
        });
    };
}