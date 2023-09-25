package com.example.myjobportalapplication.data_Model;

import java.util.HashMap;

public class Constants {
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static HashMap<String, String> remoteMsgHeaders = null;
    public static HashMap<String, String> getRemoteMsgHeaders(){
        if(remoteMsgHeaders == null){
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "key=AAAAdx9roQs:APA91bEhgR_ZVKJrnJiEocXAhKOfD2NI8fO2vjc6_99APJqk0H8LVAAG4Legbz_ZxNVbRKAUl2Kd0OmC90Xsn3me6ZDo5hmViP2E_Q_VJP2Tqs9qrkbf6qGDf_otA29_dS84xnGxzWX1"
            );
            remoteMsgHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    }
}
