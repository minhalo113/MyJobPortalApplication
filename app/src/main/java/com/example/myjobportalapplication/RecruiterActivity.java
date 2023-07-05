package com.example.myjobportalapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RecruiterActivity extends AppCompatActivity {

    private ImageButton avatarImage;
    private EditText nameRecruiter;
    private ListView postedJobList;
    private FloatingActionButton addJob;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruiter);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        uiRecruiter();
    }

    protected void uiRecruiter(){
        avatarImage = findViewById(R.id.avatarImageButton);
        nameRecruiter = findViewById(R.id.nameRecruiter);
        postedJobList = findViewById(R.id.jobPostedList);
        addJob = findViewById(R.id.floatingActionButton);

//        String jobPost[] = new String[]{"1st item","2nd item","3rd item"};
//        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(RecruiterActivity.this, android.R.layout.simple_list_item_1, jobPost);

        nameRecruiter.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                    // Retrieve the entered name
                    String newName = nameRecruiter.getText().toString();
                    // Set the new name as the text value of nameRecruiter
                    nameRecruiter.setText(newName);
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

}