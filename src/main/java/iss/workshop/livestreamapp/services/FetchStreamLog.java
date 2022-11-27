package iss.workshop.livestreamapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import iss.workshop.livestreamapp.models.Stream;
import iss.workshop.livestreamapp.models.StreamLog;
import iss.workshop.livestreamapp.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FetchStreamLog extends Service {

    private Thread backgroundThread;
    private boolean isCollectingData = true;
    private StreamLog streamLog;
    private Stream stream;
    private User seller;

    public FetchStreamLog() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        streamLog = new StreamLog();
        int duration = intent.getIntExtra("duration", 5);
        stream = (Stream) intent.getSerializableExtra("stream");
        seller = (User) intent.getSerializableExtra("seller");

        if (action.equals("send_messages")){
            backgroundThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (Thread.interrupted())
                        return;

                    while (isCollectingData) {
                        //code to run in the background

                        streamLog.setNumLikes(45);


                        System.out.println("added new log");
                        //fetch current stream details as intent
                        try {
                            Thread.sleep(1000 * duration);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            backgroundThread.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (backgroundThread != null) backgroundThread.interrupt();
        isCollectingData = false;
        RetroFitService rfServ = new RetroFitService("save-logs");
        StreamLogApi streamlogAPI = rfServ.getRetrofit().create(StreamLogApi.class);

        streamlogAPI.addNewLogList(streamLog, seller.getId(), stream.getId()).enqueue(new Callback<StreamLog>() {
            @Override
            public void onResponse(Call<StreamLog> call, Response<StreamLog> response) {
                Toast.makeText(FetchStreamLog.this, "Logs have been saved!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<StreamLog> call, Throwable t) {
                Toast.makeText(FetchStreamLog.this, "Saving has been unsuccessful!", Toast.LENGTH_SHORT).show();
            }
        });

        super.onDestroy();
    }

}