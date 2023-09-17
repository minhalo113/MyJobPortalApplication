package com.example.myjobportalapplication.EmployerPart;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.myjobportalapplication.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class OtherRecruiterProfile extends AppCompatActivity {
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

    private String uID;
    private FirebaseFirestore mFStore;
    private StorageReference mStorage;
    private StorageReference compStorage;
    DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_people_profile);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        uID = getIntent().getStringExtra("userID");

        mFStore = FirebaseFirestore.getInstance();

        mStorage = FirebaseStorage.getInstance().getReference().child("avatarImage/" + uID);
        compStorage = FirebaseStorage.getInstance().getReference().child("compImage/" + uID);

        documentReference = mFStore.collection("Recruiter").document(uID);

        recruiterProfile();
    }

    private void recruiterProfile() {
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

        nameRecruiter.setEnabled(false);
        nameCompany.setEnabled(false);
        recruiterPosition.setEnabled(false);
        companyDescription.setEnabled(false);
        emailRecruiter.setEnabled(false);
        companyIndustry.setEnabled(false);
        phoneRecruiter.setEnabled(false);
        companyLocation.setEnabled(false);
        nameRecruiter.setTextColor(getResources().getColor(android.R.color.black));
        nameCompany.setTextColor(getResources().getColor(android.R.color.black));
        recruiterPosition.setTextColor(getResources().getColor(android.R.color.black));
        companyDescription.setTextColor(getResources().getColor(android.R.color.black));
        emailRecruiter.setTextColor(getResources().getColor(android.R.color.black));
        companyIndustry.setTextColor(getResources().getColor(android.R.color.black));
        phoneRecruiter.setTextColor(getResources().getColor(android.R.color.black));
        companyLocation.setTextColor(getResources().getColor(android.R.color.black));

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    nameRecruiter.setText(value.getString("name"));
                    nameCompany.setText(value.getString("Company Name"));
                    recruiterPosition.setText(value.getString("Recruiter Position"));
                    companyDescription.setText(value.getString("Company Description"));
                    emailRecruiter.setText(value.getString("Recruiter's email"));
                    companyIndustry.setText(value.getString("Company Industry"));
                    phoneRecruiter.setText(value.getString("Phone Recruiter"));
                    companyLocation.setText(value.getString("Company Location"));
                } else {
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
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}