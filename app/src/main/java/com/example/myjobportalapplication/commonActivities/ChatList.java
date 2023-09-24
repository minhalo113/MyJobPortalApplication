package com.example.myjobportalapplication.commonActivities;

import static com.google.common.primitives.UnsignedInts.max;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myjobportalapplication.Adapter.RecentConversationsAdapter;
import com.example.myjobportalapplication.R;
import com.example.myjobportalapplication.data_Model.chatMessage;
import com.example.myjobportalapplication.databinding.ActivityChatListBinding;
import com.example.myjobportalapplication.listenerInterface.ConversionListener;
import com.example.myjobportalapplication.uiDrawer.uiDrawer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ChatList extends AppCompatActivity implements ConversionListener {
    private StorageReference mStorage;
    private StorageReference appStorage;
    private FirebaseAuth mAuth;
    private ActivityChatListBinding binding;
    private ArrayList<chatMessage> conversations;
    private RecentConversationsAdapter conversationsAdapter;
    private FirebaseFirestore mFirestore;
    private String uId;
    uiDrawer UIDRAWER = new uiDrawer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();

        int accType = getIntent().getIntExtra("accType", 2);
        UIDRAWER.myuiDrawer(this, mAuth, accType);

        init();
        loadUserDetail();
        listenConversations();
    }

    private void init(){
        conversations = new ArrayList<>();
        conversationsAdapter = new RecentConversationsAdapter(conversations, this);
        binding.userRecyclerView.setAdapter(conversationsAdapter);
        mFirestore = FirebaseFirestore.getInstance();
    }
    private void loadUserDetail(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        binding.fabNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), chatAddList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void listenConversations(){
        mFirestore.collection("conversations")
                .whereEqualTo("senderID", uId)
                .addSnapshotListener(eventListener);
        mFirestore.collection("conversations")
                .whereEqualTo("receiverID", uId)
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = ((value, error) -> {
        if(error != null){
            return;
        }
        if(value != null){
            for(DocumentChange documentChange : value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED){
                    String senderId = documentChange.getDocument().getString("senderID");
                    String receiverId = documentChange.getDocument().getString("receiverID");
                    chatMessage chatMessage = new chatMessage();
                    chatMessage.senderId = senderId;
                    chatMessage.recieverId = receiverId;
                    if(mAuth.getCurrentUser().getUid().equals(senderId)){
                        chatMessage.conversionName = documentChange.getDocument().getString("receiverName");
                        chatMessage.conversionId = documentChange.getDocument().getString("receiverID");
                    }else{
                        chatMessage.conversionName = documentChange.getDocument().getString("senderName");
                        chatMessage.conversionId = documentChange.getDocument().getString("senderID");
                    }
                    chatMessage.message = documentChange.getDocument().getString("lastMessage");
                    chatMessage.dateObject = documentChange.getDocument().getDate("time");
                    conversations.add(chatMessage);
                }else if (documentChange.getType() == DocumentChange.Type.ADDED){
                    for(int i = 0; i < conversations.size(); ++i){
                        String senderId = documentChange.getDocument().getString("senderID");
                        String receiverId = documentChange.getDocument().getString("receiverID");
                        if(conversations.get(i).senderId.equals(senderId) && conversations.get(i).recieverId.equals(receiverId)){
                            conversations.get(i).message = documentChange.getDocument().getString("lastMessage");
                            conversations.get(i).dateObject = documentChange.getDocument().getDate("time");
                            break;
                        }
                    }
                }
            }
            Collections.sort(conversations,(obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
            conversationsAdapter.notifyDataSetChanged();
            binding.userRecyclerView.smoothScrollToPosition(0);
            binding.userRecyclerView.setVisibility(View.VISIBLE);
        }
    });
    public boolean onOptionsItemSelected(MenuItem item) {
        if(UIDRAWER != null && UIDRAWER.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onBackPressed(){
        UIDRAWER.onBackPressed();
    }

    @Override
    public void onConversionClicked(String id, String name) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra("userId", id);
        intent.putExtra("name", name);
        startActivity(intent);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
    }
}