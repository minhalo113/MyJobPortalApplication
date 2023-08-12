package com.example.myjobportalapplication.Settings;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myjobportalapplication.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class changePassword extends AppCompatActivity {
    private EditText currentPass;
    private EditText newPass;
    private Button confirmButt;
    private TextView myTextView;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    String uId;
    String oldPassword;
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mFirestore = FirebaseFirestore.getInstance();
        uId = mUser.getUid();

        DocumentReference mdocumentReference = mFirestore.collection("Recruiter").document(uId);
        mdocumentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!= null){
                    oldPassword = value.getString("password");
                }else{
                    Log.d(TAG, "Document does not exist or user doesn't have access.");
                }
            }
        });

        currentPass = findViewById(R.id.editTextPassword2);
        newPass = findViewById(R.id.editTextPassword3);
        confirmButt = findViewById(R.id.button);
        myTextView = findViewById(R.id.errorText);

        myTextView.setVisibility(View.GONE);

        confirmButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                if(oldPassword.equals(currentPass.getText().toString())){
                    myTextView.setVisibility(View.GONE);
                    String mNewPass = newPass.getText().toString();

                    mUser.updatePassword(mNewPass).addOnCompleteListener(task -> {
                        if(!task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Unable to update to FirebaseAuth", Toast.LENGTH_LONG).show();
                            return;
                        }
                    });

                    Map<String, Object> pass = new HashMap<>();
                    pass.put("password", mNewPass);

                    mdocumentReference.update(pass).addOnCompleteListener(task -> {
                        if(!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Unable to update to FirebaseFirestore", Toast.LENGTH_LONG).show();
                            return;
                        }
                    });

                    currentPass.setText("");
                    newPass.setText("");

                    Toast.makeText(getApplicationContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                }
                else{
                    myTextView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

}