package com.example.myjobportalapplication.listenerInterface;

import com.google.firebase.firestore.auth.User;

public interface UserListener {
    void onUserClicked(String id);
}
