package iss.workshop.livestreamapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ConnectToAPI extends Service {

    private Thread backgroundThread;

    public ConnectToAPI() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();

        //depending on action that started the service, need to
        switch(action){
            case "getProducts":
                System.out.println("console print placeholder");
                break;
            default:
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}