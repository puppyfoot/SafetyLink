package com.example.customertablet;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FMS";

    public MyFirebaseMessagingService() {

    }

    @Override
    public void onNewToken(String token){
        super.onNewToken(token);
        Log.e(TAG, "onNewToken 호출됨 : "+ token);
    }

    //    생성
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        // title은 고정 아래는 추가가능
        String carid = remoteMessage.getData().get("carid");
        String contents = remoteMessage.getData().get("contents");

        Log.d("[TAG]",title+" "+carid+" "+contents);
        Intent intent = new Intent("notification");
        intent.putExtra("carid",carid);
        intent.putExtra("contents",contents);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendToActivity(Context context, String from, String contents){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("from", from);
        intent.putExtra("contents", contents);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|
                Intent.FLAG_ACTIVITY_SINGLE_TOP|
                Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(intent);
    }
}