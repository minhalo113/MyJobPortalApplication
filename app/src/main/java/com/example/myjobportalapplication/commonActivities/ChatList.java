package com.example.myjobportalapplication.commonActivities;

import static com.google.common.primitives.UnsignedInts.max;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myjobportalapplication.R;
import com.example.myjobportalapplication.uiDrawer.uiDrawer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ChatList extends AppCompatActivity {
    private StorageReference mStorage;
    private StorageReference appStorage;
    private FloatingActionButton addChat;
    private FirebaseAuth mAuth;
    private RecyclerView mRecyclerView;
    private TextView name;
    private TextView email;
    private TextView phoneNumb;
    private ImageView avatarImage;

    private PreferenceManager preferenceManager;
    private String uId;
    uiDrawer UIDRAWER = new uiDrawer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        WindowInsetsControllerCompat windowInsetsCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsCompat.hide(WindowInsetsCompat.Type.statusBars());
        windowInsetsCompat.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();

        mRecyclerView = findViewById(R.id.userRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);

        int accType = getIntent().getIntExtra("accType", 2);
        UIDRAWER.myuiDrawer(this, mAuth, accType);

        loadUserDetail();
    }

    private void loadUserDetail(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();


//        mAdapter myAdapter = new mAdapter();
//        mRecyclerView.setAdapter(myAdapter);
    }
    public class mAdapter extends RecyclerView.Adapter<chatViewHolder>{
        private ArrayList<String> chatName;
        private ArrayList<String> chatPhone;
        private ArrayList<String> chatEmail;
        private ArrayList<Bitmap> chatImage;

        public mAdapter(ArrayList<String> chatName, ArrayList<String> chatPhone, ArrayList<Bitmap> chatImage, ArrayList<String> chatEmail) {
            this.chatName = chatName;
            this.chatPhone = chatPhone;
            this.chatImage = chatImage;
            this.chatEmail = chatEmail;
        }

        @NonNull
        @Override
        public chatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_user_chat, parent, false);
            return new ChatList.chatViewHolder(itemView);
        }
        @Override
        public void onBindViewHolder(@NonNull chatViewHolder holder, int position) {
            String item1 = chatName.get(position);
            String item2 = chatPhone.get(position);
            String item3 = chatEmail.get(position);
            Bitmap bitmap = chatImage.get(position);

            holder.setInfo(bitmap, item2, item1, item3);
        }
        @Override
        public int getItemCount() {
            return max(chatName.size(), chatPhone.size(), chatImage.size(), chatEmail.size());
        }
    }
    public static class chatViewHolder extends RecyclerView.ViewHolder{
        View myTextView;

        public chatViewHolder(@NonNull View itemView) {
            super(itemView);
            this.myTextView = itemView;
        }

        public void setInfo(Bitmap bitmap, String phone, String name, String email){
            TextView mName = myTextView.findViewById(R.id.TextName);
            TextView mPhoneEmail = myTextView.findViewById(R.id.textEmailTele);
            ImageView mImage = myTextView.findViewById(R.id.imageProfile);

            mName.setText(name);
            mPhoneEmail.setText(phone + "\n" + email);
            mImage.setImageBitmap(bitmap);
        }
    }
}