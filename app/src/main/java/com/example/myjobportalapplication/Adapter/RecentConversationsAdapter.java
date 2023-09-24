package com.example.myjobportalapplication.Adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myjobportalapplication.data_Model.chatMessage;
import com.example.myjobportalapplication.databinding.ItemContainerRecentChatBinding;
import com.example.myjobportalapplication.listenerInterface.ConversionListener;

import java.util.ArrayList;

public class RecentConversationsAdapter extends RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder>{

    private final ArrayList<chatMessage> chatMessages;
    private final ConversionListener conversionListener;

    public RecentConversationsAdapter(ArrayList<chatMessage> chatMessages, ConversionListener conversionListener) {
        this.chatMessages = chatMessages;
        this.conversionListener = conversionListener;
    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
                ItemContainerRecentChatBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder{
        ItemContainerRecentChatBinding binding;

        ConversionViewHolder(ItemContainerRecentChatBinding itemContainerRecentChatBinding){
            super(itemContainerRecentChatBinding.getRoot());
            binding = itemContainerRecentChatBinding;
        }
        void setData(chatMessage chatMessage){
            binding.TextName.setText(chatMessage.conversionName);
            binding.textRecentMessage.setText(chatMessage.message);
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String id = chatMessage.conversionId;
                    String name = chatMessage.conversionName;
                    conversionListener.onConversionClicked(id, name);
                }
            });
        }
    }
}
