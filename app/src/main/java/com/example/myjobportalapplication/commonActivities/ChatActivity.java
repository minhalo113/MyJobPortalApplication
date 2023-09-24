package com.example.myjobportalapplication.commonActivities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;

import com.example.myjobportalapplication.R;
import com.example.myjobportalapplication.data_Model.chatMessage;
import com.example.myjobportalapplication.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private String userID;
    private DocumentReference documentReference;
    private FirebaseFirestore databaseChat;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private ArrayList<chatMessage> chatMessages;
    private chatAdapter chatAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        userID = getIntent().getStringExtra("userId");
        System.out.println(userID);
        documentReference = FirebaseFirestore.getInstance().collection("Job Applicant").document(userID);
        mStorage = FirebaseStorage.getInstance().getReference().child("avatarImage/" + userID);

        setListeners();
        loadUserDetails();
        init();
        listenMessages();
    }

    private void init(){
        Bitmap bitmap = null;
        if(binding.imageInfo.getDrawable() instanceof BitmapDrawable){
            bitmap = ((BitmapDrawable)binding.imageInfo.getDrawable()).getBitmap();
        }else{
            bitmap = Bitmap.createBitmap(binding.imageInfo.getDrawable().getIntrinsicWidth(), binding.imageInfo.getDrawable().getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            binding.imageInfo.getDrawable().setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            binding.imageInfo.getDrawable().draw(canvas);
        }
        chatMessages = new ArrayList<>();
        chatAdapter = new chatAdapter(chatMessages, bitmap, mAuth.getCurrentUser().getUid());
        binding.chatRecyclerView.setAdapter(chatAdapter);
        databaseChat = FirebaseFirestore.getInstance();
    }
    private void sendMessage(){
        HashMap<String, Object> message = new HashMap<>();
        message.put("senderID", mAuth.getCurrentUser().getUid());
        message.put("recieverID", userID);
        message.put("message", binding.inputMessage.getText().toString());
        message.put("time", new Date());
        databaseChat.collection("Recruiter -> Applicant (Chat)").add(message);
        binding.inputMessage.setText(null);
    }

    private void listenMessages(){
        databaseChat.collection("Recruiter -> Applicant (Chat)")
                .whereEqualTo("senderID", mAuth.getCurrentUser().getUid())
                .whereEqualTo("recieverID", userID)
                .addSnapshotListener(eventListener);
        databaseChat.collection("Recruiter -> Applicant (Chat)")
                .whereEqualTo("senderID", userID)
                .whereEqualTo("recieverID", mAuth.getCurrentUser().getUid())
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = ((value, error) -> {
        if(error != null){
            return;
        }
        if(value != null){
            int count = chatMessages.size();
            for(DocumentChange documentChange : value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED){
                    chatMessage chatMessage = new chatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString("senderID");
                    chatMessage.recieverId = documentChange.getDocument().getString("recieverID");
                    chatMessage.message = documentChange.getDocument().getString("message");
                    chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate("time"));
                    chatMessage.dateObject = documentChange.getDocument().getDate("time");
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if(count == 0){
                chatAdapter.notifyDataSetChanged();
            }else{
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
    });
    private void loadUserDetails(){
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            binding.textName.setText(name);
                        }
                    }
                });
        mStorage.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                int desiredWidth = binding.imageInfo.getWidth();
                int desiredHeight = binding.imageInfo.getHeight();

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, desiredWidth, desiredHeight, true);

                binding.imageInfo.setImageBitmap(scaledBitmap);
            }
        });
    }

    private void setListeners(){
        binding.layoutSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }
    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}