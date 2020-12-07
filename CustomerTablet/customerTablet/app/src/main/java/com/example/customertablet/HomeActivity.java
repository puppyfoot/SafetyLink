package com.example.customertablet;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.df.DataFrame;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;


public class HomeActivity extends AppCompatActivity {

    // TCP/IP Server
    ServerSocket serverSocket;
    int serverPort = 5558;
    Sender sender;
    HashMap<String, ObjectOutputStream> maps = new HashMap<>();

    // 현재 시간 표시 설정
    SimpleDateFormat format = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
    Date time = new Date();
    String timeNow = format.format(time);

    NotificationManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();
        // serverStart
        try {
            startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // FCM사용 (앱이 중단되어 있을 때 기본적으로 title,body값으로 푸시!!)
        FirebaseMessaging.getInstance().subscribeToTopic("car"). //구독, 이걸로 원하는 기능 설정하기(파이널 때, db 활용)
                addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String msg = "FCM Complete...";
                if (!task.isSuccessful()) {
                    msg = "FCM Fail";
                }
                Log.d("[TAG]", msg);
            }
        });


        // 여기서 부터는 앱 실행상태에서 상태바 설정!!
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this); // 브로드캐스트를 받을 준비
        lbm.registerReceiver(receiver, new IntentFilter("notification")); // notification이라는 이름의 정보를 받겠다


    }


    public void startServer() throws Exception {
        serverSocket = new ServerSocket(serverPort);
        Log.d("[Server]", "Start Server...");


        Runnable r = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Socket socket = null;
                        Log.d("[Server]", "Server Ready..");
                        socket = serverSocket.accept();
                        Log.d("[Server]", "Connected:" + socket.getInetAddress() + " " + timeNow); // 연결된 IP표시
                        new Receiver(socket).start();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        new Thread(r).start();
    }

    class Receiver extends Thread {
        Socket socket;
        ObjectInputStream oi;


        public Receiver(Socket socket) throws IOException {
            this.socket = socket;
            ObjectOutputStream oo;
            oi = new ObjectInputStream(this.socket.getInputStream());
            oo = new ObjectOutputStream(this.socket.getOutputStream());

            maps.put(socket.getInetAddress().toString(), oo);
            Log.d("[Server]", "[Server]" + socket.getInetAddress() + "연결되었습니다.");
        }


        @Override
        public void run() {
            while (oi != null) {
                try {
                    Log.d("[Server]","--------1---------");
                    DataFrame input = (DataFrame) oi.readObject();
                    Log.d("[Server]", "[DataFrame 수신] " + input.getSender() + ": " + input.getContents());

//                  sendDataFrame(input);


                } catch (Exception e) {
                    maps.remove(socket.getInetAddress().toString());
                    Log.d("[Server]", socket.getInetAddress() + " Exit..." + timeNow);
                    e.printStackTrace();
                    Log.d("[Server]", socket.getInetAddress() + " :Receiver 객체 수신 실패 ");

                    break;
                }
            } // end while

            try {
                if (oi != null) {
                    Log.d("[Server]", "ObjectInputStream Closed ..");
                    oi.close();
                }
                if (socket != null) {
                    Log.d("[Server]", "Socket Closed ..");
                    socket.close();
                }
            } catch (Exception e) {
                Log.d("[Server]", "객체 수신 실패 후 InputStream, socket 닫기 실패");
            }

        }
    }// End Receiver

    public void sendDataFrame(DataFrame df) {
        try {
            sender = new Sender();
            Log.d("[Server]", "setDataFrame 실행");
            sender.setDataFrame(df);
            Log.d("[Server]", "객체 송신 Thread 호출");
            sender.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class Sender extends Thread {
        DataFrame dataFrame;
        Socket socket;

        public Sender() {

        }

        public void setDataFrame(DataFrame df) {
            this.dataFrame = df;
            Log.d("[Server]", "setDataFrame 완료");
        }

        @Override
        public void run() {
            try {
                Log.d("[Server]", "Sender Thread 실행");
                // dataFrame.setIp("192.168.35.149");
                // dataFrame.setSender("[TabletServer]");
                // Log.d("[Server]", "테스트 목적 Client로 목적지 재설정");

                maps.get("/" + dataFrame.getIp()).writeObject(dataFrame);
                Log.d("[Server]", "Sender 객체 전송.. " + dataFrame.getIp() + "주소로 " + dataFrame.getContents());
                Log.d("[Server]", "Sender 객체 전송 성공");

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }



    // FCM 수신
    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String carid = intent.getStringExtra("carid");
                String contents = intent.getStringExtra("contents");



                // 상단알람 사용
                manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder = null;
                if (Build.VERSION.SDK_INT >= 26) {
                    if (manager.getNotificationChannel("ch1") == null) {
                        manager.createNotificationChannel(
                                new NotificationChannel("ch1", "chname", NotificationManager.IMPORTANCE_HIGH));
                    }
                    builder = new NotificationCompat.Builder(context, "ch1");
                } else {
                    builder = new NotificationCompat.Builder(context);
                }

                Intent intent1 = new Intent(context, HomeActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        context, 101, intent1, PendingIntent.FLAG_UPDATE_CURRENT
                );
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                builder.setAutoCancel(true);
                builder.setContentIntent(pendingIntent);

                builder.setContentTitle(carid+" "+contents);

                builder.setSmallIcon(R.mipmap.saftylink1_logo_round);
                Notification noti = builder.build();
                manager.notify(1, noti); // 상단 알림을 없애려면 이곳 주석 처리
            }
        }
    };


}