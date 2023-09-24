package com.example.myjobportalapplication.Adapter;

import static com.google.common.primitives.UnsignedInts.max;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myjobportalapplication.R;
import com.example.myjobportalapplication.listenerInterface.UserListener;

import java.util.ArrayList;


public class mAdapter extends RecyclerView.Adapter<mAdapter.chatViewHolder>{
        private ArrayList<String> chatName;
        private ArrayList<String> chatPhone;
        private ArrayList<String> chatEmail;
        private ArrayList<Bitmap> chatImage;

        private ArrayList<String> userIds;
        private UserListener userListener;
        public mAdapter(ArrayList<String> chatName, ArrayList<String> chatPhone, ArrayList<Bitmap> chatImage, ArrayList<String> chatEmail, ArrayList<String> userIds,  UserListener userListener) {
            this.chatName = chatName;
            this.chatPhone = chatPhone;
            this.chatImage = chatImage;
            this.chatEmail = chatEmail;
            this.userListener = userListener;
            this.userIds = userIds;
        }

        @NonNull
        @Override
        public chatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_user_chat, parent, false);
            return new mAdapter.chatViewHolder(itemView);
        }
        @Override
        public void onBindViewHolder(@NonNull chatViewHolder holder, int position) {
            String item1 = null;
            String item2 = null;
            String item3 = null;
            Bitmap bitmap = null;
            if(position < chatName.size()) {
                item1 = chatName.get(position);
            }
            if(position < chatPhone.size()) {
                item2 = chatPhone.get(position);
            }
            if(position < chatEmail.size()) {
                item3 = chatEmail.get(position);
            }
            if(position < chatImage.size()) {
                bitmap = chatImage.get(position);
            }

            holder.setInfo(bitmap, item2, item1, item3);
            holder.myTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userListener.onUserClicked(userIds.get(position));
                }
            });
        }
        @Override
        public int getItemCount() {
            return max(chatName.size(), chatPhone.size(), chatImage.size(), chatEmail.size());
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

                int desiredWidth = 200;
                int desiredHeight = 200;

                if(name != null){
                mName.setText(name);}

                if(phone != null){
                mPhoneEmail.setText(phone + " / " + email);}else {
                    mPhoneEmail.setText(email);
                }

                if(bitmap != null){
                    System.out.println(bitmap +" " +  desiredWidth + " " +  desiredHeight);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, desiredWidth, desiredHeight, true);
                mImage.setImageBitmap(scaledBitmap);}
            }
        }
    }

