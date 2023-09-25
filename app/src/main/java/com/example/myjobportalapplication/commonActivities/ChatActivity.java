package com.example.myjobportalapplication.commonActivities;

import androidx.annotation.NonNull;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.myjobportalapplication.Adapter.chatAdapter;
import com.example.myjobportalapplication.data_Model.Constants;
import com.example.myjobportalapplication.data_Model.chatMessage;
import com.example.myjobportalapplication.databinding.ActivityChatBinding;
import com.example.myjobportalapplication.network.ApiClient;
import com.example.myjobportalapplication.network.ApiService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends BaseActivity {
    private ActivityChatBinding binding;
    private String userID;
    private String receiverName;
    private String FCM_TOKEN_receiver, FCM_TOKEN_sender;
    private DocumentReference documentReference;
    private DocumentReference recruiterInfo;
    private FirebaseFirestore databaseChat;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private ArrayList<chatMessage> chatMessages;
    private String conversionId = null;
    private com.example.myjobportalapplication.Adapter.chatAdapter chatAdapter;
    private Boolean isReceiverAvailable= false;
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
        documentReference = FirebaseFirestore.getInstance().collection("Job Applicant").document(userID);
        recruiterInfo = FirebaseFirestore.getInstance().collection("Recruiter").document(mAuth.getCurrentUser().getUid());
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

        final String[] name = {null};
        recruiterInfo.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        name[0] = document.getString("name");
                        FCM_TOKEN_sender = document.getString("FCM TOKEN");
                        if(conversionId != null){
                            updateConversion(binding.inputMessage.getText().toString());
                        }else{
                            HashMap<String, Object> conversion = new HashMap<>();
                            conversion.put("senderID", mAuth.getCurrentUser().getUid());
                            conversion.put("senderName", name[0]);
                            conversion.put("receiverName", receiverName);
                            conversion.put("receiverID", userID);
                            conversion.put("lastMessage", binding.inputMessage.getText().toString());
                            conversion.put("time", new Date());
                            addConversion(conversion);
                        }
                        if(!isReceiverAvailable){
                            try{
                            JSONArray tokens = new JSONArray();
                            tokens.put(FCM_TOKEN_receiver);
                            JSONObject data = new JSONObject();
                            data.put("userID", mAuth.getCurrentUser().getUid());
                            data.put("name", name[0]);
                            data.put("FCM TOKEN", FCM_TOKEN_sender);
                            data.put("message", binding.inputMessage.getText().toString());

                            JSONObject body = new JSONObject();
                            body.put(Constants.REMOTE_MSG_DATA, data);
                            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

                            sendNotification(body.toString());
                            }catch(Exception exception){
                                showToast(exception.getMessage());
                            }
                        }
                    }
                    binding.inputMessage.setText(null);
                }
            }
        });
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void sendNotification(String messageBody){
        ApiClient.getClient().create(ApiService.class).sendMessage(
                Constants.getRemoteMsgHeaders(),
                messageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if(response.isSuccessful()){
                    try {
                        if(response.body()!= null){
                            JSONObject responseJson = new JSONObject(response.body());
                            JSONArray results = responseJson.getJSONArray("results");
                            if(responseJson.getInt("failure") == 1){
                                JSONObject error = (JSONObject) results.get(0);
                                showToast(error.getString("error"));
                                return;
                            }
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    showToast("Notification sent successful");
                }else{
                    showToast("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                showToast(t.getMessage());
            }
        });
    }
    private void listenAvailabilityOfReceiver(){
        databaseChat.collection("Job Applicants").document(
                userID
        ).addSnapshotListener(ChatActivity.this, (value, error) -> {
            if(error != null){
                return;
            }
            if(value != null){
                if(value.getLong("availability") != null){
                    int availability = Objects.requireNonNull(
                            value.getLong("availability")
                    ).intValue();
                    isReceiverAvailable = availability == 1;
                }
                String receiverFCM = value.getString("FCM TOKEN");
            }
            if(isReceiverAvailable){
                binding.textAvailabilitty.setVisibility(View.VISIBLE);
            }else{
                binding.textAvailabilitty.setVisibility(View.GONE);
            }
        });
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
        if(conversionId == null){
            checkForConversion();
        }
    });
    private void loadUserDetails(){
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            receiverName = documentSnapshot.getString("name");
                            FCM_TOKEN_receiver = documentSnapshot.getString("FCM TOKEN");
                            binding.textName.setText(receiverName);
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

    private void checkForConversion(){
        if(chatMessages.size() != 0){
            checkForConversionRemotely(mAuth.getCurrentUser().getUid(), userID);
            checkForConversionRemotely(userID, mAuth.getCurrentUser().getUid());
        }
    }
    private void checkForConversionRemotely(String senderId, String receiverId){
        databaseChat.collection("conversations")
                .whereEqualTo("senderID", senderId)
                .whereEqualTo("receiverID", receiverId)
                .get()
                .addOnCompleteListener(conversionOnCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListener = task -> {
        if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversionId = documentSnapshot.getId();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        listenAvailabilityOfReceiver();
    }

    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }
    private void addConversion(HashMap<String, Object> conversion){
        databaseChat.collection("conversations").add(conversion).addOnSuccessListener(documentReference1 -> conversionId = documentReference1.getId());
    }

    private void updateConversion(String message){
        DocumentReference documentReference1 = databaseChat.collection("conversations").document(conversionId);
        documentReference1.update(
                "lastMessage", message,
                "time", new Date()
        );
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), ChatList.class);
        intent.putExtra("accType", 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        return super.onOptionsItemSelected(item);
    }
}